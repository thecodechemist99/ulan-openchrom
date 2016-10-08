package org.chromulan.system.control.device.setting;

public class DeviceSetting extends AbstractDeviceSetting {

	public DeviceSetting() {
		super();
	}

	public DeviceSetting(String pluginID, String name, String deviceType, String deviceID) {
		this();
		setPlugnID(pluginID);
		setName(name);
		setDeviceType(deviceType);
		setDeviceID(deviceID);
	}
}
