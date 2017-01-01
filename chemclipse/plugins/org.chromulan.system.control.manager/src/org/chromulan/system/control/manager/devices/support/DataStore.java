/*******************************************************************************
 * Copyright (c) 2016, 2017 Jan Holy.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.chromulan.system.control.manager.devices.support;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.chromulan.system.control.device.IControlDevice;
import org.chromulan.system.control.device.IDevicesProfile;
import org.chromulan.system.control.device.setting.IDeviceSetting;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

public class DataStore {

	private List<IControlDevice> devices;
	private List<IDeviceSetting> deviceSettings;
	private List<IDevicesProfile> profiles;

	public DataStore() {
		devices = new CopyOnWriteArrayList<>();
		profiles = new CopyOnWriteArrayList<>();
		deviceSettings = new CopyOnWriteArrayList<>();
	}

	public List<IControlDevice> getControlDevices() {

		return devices;
	}

	public List<IDeviceSetting> getDeviceSettings() {

		return deviceSettings;
	}

	public List<IDevicesProfile> getDevicesProfiles() {

		return profiles;
	}

	public void readExternal(ObjectInputStream in, IConfigurationElement[] elements) throws IOException, ClassNotFoundException, CoreException {

		LoadControlDevices loadControlDevices = new LoadControlDevices();
		List<IControlDevice> devices = loadControlDevices.loadDevices(in, elements);
		List<IDeviceSetting> deviceSettings = loadControlDevices.loadSettings(in, elements);
		List<IDevicesProfile> profiles = loadControlDevices.loadProfiles(in, elements);
		this.devices.addAll(devices);
		this.deviceSettings.addAll(deviceSettings);
		this.profiles.addAll(profiles);
	}

	public void writeExternal(ObjectOutputStream out) throws IOException {

		SaveControlDevices saveControlDevices = new SaveControlDevices();
		saveControlDevices.saveControlDevices(out, devices);
		saveControlDevices.saveDeviceSettings(out, deviceSettings);
		;
		saveControlDevices.saveProfiles(out, profiles);
	}
}
