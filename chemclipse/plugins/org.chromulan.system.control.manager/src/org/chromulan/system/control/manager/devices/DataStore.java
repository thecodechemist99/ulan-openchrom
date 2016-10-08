/*******************************************************************************
 * Copyright (c) 2016 Jan Holy.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.chromulan.system.control.manager.devices;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.chromulan.system.control.device.ControlDevices;
import org.chromulan.system.control.device.DevicesProfiles;
import org.chromulan.system.control.device.IControlDevices;
import org.chromulan.system.control.device.IDevicesProfiles;
import org.chromulan.system.control.device.setting.IDeviceSetting;
import org.chromulan.system.control.manager.devices.supplier.LoadControlDevices;
import org.chromulan.system.control.manager.devices.supplier.SaveControlDevices;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

public class DataStore {

	private IControlDevices devices;
	private IDevicesProfiles profiles;
	private List<IDeviceSetting> deviceSettings;

	public DataStore() {
		devices = new ControlDevices();
		profiles = new DevicesProfiles();
		deviceSettings = new ArrayList<>();
	}

	public IControlDevices getControlDevices() {

		return devices;
	}

	public IDevicesProfiles getDevicesProfiles() {
		return profiles;
	}

	public List<IDeviceSetting> getDeviceSettings() {
		return deviceSettings;
	}

	public void readExternal(ObjectInputStream in, IConfigurationElement[] elements)
			throws IOException, ClassNotFoundException, CoreException {
		LoadControlDevices loadControlDevices = new LoadControlDevices();
		devices = loadControlDevices.loadControlDevices(in, elements);
		deviceSettings = loadControlDevices.loadSettings(in, elements);
		profiles = loadControlDevices.loadProfiles(in, elements);
	}

	public void writeExternal(ObjectOutputStream out) throws IOException {
		SaveControlDevices saveControlDevices = new SaveControlDevices();
		saveControlDevices.saveControlDevices(out, devices);
		saveControlDevices.saveDeviceSettings(out, deviceSettings);
		;
		saveControlDevices.saveProfiles(out, profiles);
	}
}
