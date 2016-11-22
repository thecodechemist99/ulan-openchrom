package org.chromulan.system.control.devices.supplier;

import java.io.IOException;
import java.io.ObjectInput;

import org.chromulan.system.control.device.IControlDevice;
import org.chromulan.system.control.devices.base.Lcd5000ControlDevice;
import org.chromulan.system.control.devices.base.ULand3xControlDevice;
import org.chromulan.system.control.manager.devices.ICreateControlDevice;

public class UlanCreateDevice implements ICreateControlDevice {

	@Override
	public IControlDevice createDevice(String className, ObjectInput in) throws ClassNotFoundException, IOException {

		if(className.equals(ULand3xControlDevice.class.getName())) {
			return (IControlDevice)in.readObject();
		} else if(className.equals(Lcd5000ControlDevice.class.getName())) {
			return (IControlDevice)in.readObject();
		} else {
			return null;
		}
	}
}
