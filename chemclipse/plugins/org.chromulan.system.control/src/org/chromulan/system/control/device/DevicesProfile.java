/*******************************************************************************
 * Copyright (c) 2015, 2017 Jan Holy.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.chromulan.system.control.device;

import java.util.LinkedList;
import java.util.List;

import org.chromulan.system.control.device.setting.IDeviceSetting;
import org.chromulan.system.control.model.IAcquisition;

public class DevicesProfile extends ControlDevices implements IDevicesProfile {

	final private List<IAcquisition> acquisitions = new LinkedList<>();
	final private List<IDeviceSetting> deviceSettings = new LinkedList<>();
	private String name;

	public DevicesProfile() {
		super();
	}

	@Override
	public void addAcquisition(IAcquisition acquisition) {

		if(!containsAcqusition(acquisition)) {
			acquisitions.add(acquisition);
		}
	}

	@Override
	public void addDeviceSetting(IDeviceSetting deviceSetting) {

		IDeviceSetting setting = getDeviceSetting(deviceSetting.getPluginID(), deviceSetting.getDeviceID());
		if(setting != null) {
			deviceSettings.remove(setting);
		}
		deviceSettings.add(deviceSetting);
	}

	@Override
	public boolean containsAcqusition() {

		return !acquisitions.isEmpty();
	}

	@Override
	public boolean containsAcqusition(IAcquisition acquisition) {

		return acquisitions.contains(acquisition);
	}

	@Override
	public boolean containsDeviceSetting(String pluginID, String deviceID) {

		if(getDeviceSetting(pluginID, deviceID) != null) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public List<IAcquisition> getAcquisitions() {

		return acquisitions;
	}

	@Override
	public List<IDeviceSetting> getDeviceSetting() {

		return deviceSettings;
	}

	@Override
	public IDeviceSetting getDeviceSetting(String pluginID, String deviceID) {

		for(IDeviceSetting iDeviceSetting : deviceSettings) {
			if(iDeviceSetting.getPluginID().equals(pluginID) && iDeviceSetting.getDeviceID().equals(deviceID)) {
				return iDeviceSetting;
			}
		}
		return null;
	}

	@Override
	public String getName() {

		return name;
	}

	@Override
	public void removeAcqusition(IAcquisition acquisition) {

		acquisitions.remove(acquisition);
	}

	@Override
	public void removeDeviceSetting(String pluginID, String deviceID) {

		IDeviceSetting setting = getDeviceSetting(pluginID, deviceID);
		if(setting != null) {
			deviceSettings.remove(setting);
		}
	}

	@Override
	public void setName(String name) {

		this.name = name;
	}
}
