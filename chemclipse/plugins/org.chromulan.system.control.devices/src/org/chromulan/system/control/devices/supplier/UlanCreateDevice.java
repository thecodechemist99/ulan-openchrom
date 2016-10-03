package org.chromulan.system.control.devices.supplier;

import java.io.IOException;
import java.io.ObjectInput;

import org.chromulan.system.control.device.IControlDevice;
import org.chromulan.system.control.devices.base.UlanControlDevice;
import org.chromulan.system.control.manager.devices.supplier.ICreateControlDevice;

public class UlanCreateDevice implements ICreateControlDevice{

	@Override
	public IControlDevice createDevice(String className, ObjectInput in) throws ClassNotFoundException, IOException {
		if(className.equals(UlanControlDevice.class.getName()))
		{
			return (IControlDevice) in.readObject();
		} else {
			return null;
		}			
	}
	
}
