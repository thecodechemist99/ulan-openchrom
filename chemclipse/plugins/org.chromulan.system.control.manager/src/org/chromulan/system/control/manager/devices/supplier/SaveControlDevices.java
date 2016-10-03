package org.chromulan.system.control.manager.devices.supplier;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

import org.chromulan.system.control.device.IControlDevice;
import org.chromulan.system.control.device.IControlDevices;
import org.chromulan.system.control.device.IDevicesProfile;
import org.chromulan.system.control.device.IDevicesProfiles;
import org.chromulan.system.control.device.setting.IDeviceSetting;

public class SaveControlDevices {
	
	public void saveControlDevices(ObjectOutputStream out, IControlDevices controlDevices) throws IOException
	{
		List<IControlDevice> devices = controlDevices.getControlDevices();
		out.writeInt(devices.size());
		for (IControlDevice device : devices) {
			saveControlDevice(out, device);
		}
	}
	
	public void saveProfile(ObjectOutputStream out, IDevicesProfile profile) throws IOException
	{
		out.writeObject(profile.getName());
		saveControlDevices(out, profile);
	}
	
	public  void saveProfiles(ObjectOutputStream out, IDevicesProfiles profiles) throws IOException
	{
		List<IDevicesProfile>devicesProfiles = profiles.getAll();
		out.writeInt(devicesProfiles.size());
		for (IDevicesProfile profile : devicesProfiles) {
			saveProfile(out, profile);
		}
	}
	
	public  void saveControlDevice(ObjectOutputStream out, IControlDevice controlDevice) throws IOException{
		out.writeObject(controlDevice.getPluginID());
		out.writeObject(controlDevice.getClass().getName());
		out.writeObject(controlDevice);
	}
	
	public void saveDeviceSettings(ObjectOutputStream out,List<IDeviceSetting> settings) throws IOException{
		out.writeObject(settings);	
	}
}
