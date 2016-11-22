package org.chromulan.system.control.manager.devices.support;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.chromulan.system.control.device.ControlDevices;
import org.chromulan.system.control.device.DevicesProfile;
import org.chromulan.system.control.device.IControlDevice;
import org.chromulan.system.control.device.IControlDevices;
import org.chromulan.system.control.device.IDevicesProfile;
import org.chromulan.system.control.device.setting.IDeviceSetting;
import org.chromulan.system.control.manager.devices.ICreateControlDevice;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

public class LoadControlDevices {

	public IControlDevice loadControlDevice(ObjectInputStream in, IConfigurationElement[] elements) throws ClassNotFoundException, IOException, CoreException {

		String id = (String)in.readObject();
		String className = (String)in.readObject();
		for(IConfigurationElement element : elements) {
			if(element.getContributor().getName().equals(id)) {
				Object o = element.createExecutableExtension("Create_Device");
				if(o instanceof ICreateControlDevice) {
					ICreateControlDevice createControlDevice = (ICreateControlDevice)o;
					IControlDevice controlDevice = createControlDevice.createDevice(className, in);
					if(controlDevice != null) {
						return controlDevice;
					} else {
						throw new ClassNotFoundException();
					}
				}
			}
		}
		throw new ClassNotFoundException();
	}

	public IControlDevices loadControlDevices(ObjectInputStream in, IConfigurationElement[] elements) throws IOException, ClassNotFoundException, CoreException {

		IControlDevices devices = new ControlDevices();
		List<IControlDevice> controlDevices = loadDevices(in, elements);
		devices.getControlDevices().addAll(controlDevices);
		return devices;
	}

	public List<IControlDevice> loadDevices(ObjectInputStream in, IConfigurationElement[] elements) throws IOException, ClassNotFoundException, CoreException {

		int size = in.readInt();
		List<IControlDevice> devices = new ArrayList<>(size);
		for(int i = 0; i < size; i++) {
			IControlDevice device = loadControlDevice(in, elements);
			devices.add(device);
		}
		return devices;
	}

	public IDevicesProfile loadProfile(ObjectInputStream in, IConfigurationElement[] elements) throws ClassNotFoundException, IOException, CoreException {

		String name = (String)in.readObject();
		IDevicesProfile profile = new DevicesProfile();
		profile.setName(name);
		List<IControlDevice> controlDevices = loadDevices(in, elements);
		profile.getControlDevices().addAll(controlDevices);
		return profile;
	}

	public List<IDevicesProfile> loadProfiles(ObjectInputStream in, IConfigurationElement[] elements) throws IOException, ClassNotFoundException, CoreException {

		int size = in.readInt();
		List<IDevicesProfile> profiles = new LinkedList<>();
		for(int i = 0; i < size; i++) {
			IDevicesProfile profile = loadProfile(in, elements);
			profiles.add(profile);
		}
		return profiles;
	}

	@SuppressWarnings("unchecked")
	public List<IDeviceSetting> loadSettings(ObjectInputStream in, IConfigurationElement[] elements) throws IOException, ClassNotFoundException, CoreException {

		return (List<IDeviceSetting>)in.readObject();
	}
}
