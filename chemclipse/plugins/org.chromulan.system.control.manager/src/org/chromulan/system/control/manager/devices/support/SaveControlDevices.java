package org.chromulan.system.control.manager.devices.support;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

import org.chromulan.system.control.device.IControlDevice;
import org.chromulan.system.control.device.IDevicesProfile;
import org.chromulan.system.control.device.setting.IDeviceSetting;

public class SaveControlDevices {

	public void saveControlDevice(ObjectOutputStream out, IControlDevice controlDevice) throws IOException {

		out.writeObject(controlDevice.getPluginID());
		out.writeObject(controlDevice.getClass().getName());
		out.writeObject(controlDevice);
	}

	public void saveControlDevices(ObjectOutputStream out, List<IControlDevice> devices) throws IOException {

		out.writeInt(devices.size());
		for(IControlDevice device : devices) {
			saveControlDevice(out, device);
		}
	}

	public void saveDeviceSettings(ObjectOutputStream out, List<IDeviceSetting> settings) throws IOException {

		out.writeObject(settings);
	}

	public void saveProfile(ObjectOutputStream out, IDevicesProfile profile) throws IOException {

		out.writeObject(profile.getName());
		saveControlDevices(out, profile.getControlDevices());
	}

	public void saveProfiles(ObjectOutputStream out, List<IDevicesProfile> devicesProfiles) throws IOException {

		out.writeInt(devicesProfiles.size());
		for(IDevicesProfile profile : devicesProfiles) {
			saveProfile(out, profile);
		}
	}
}
