package org.chromulan.system.control.manager.devices.supplier;

import java.io.IOException;
import java.io.ObjectInput;

import org.chromulan.system.control.device.IControlDevice;

public interface ICreateControlDevice {

	IControlDevice createDevice(String className, ObjectInput in) throws ClassNotFoundException, IOException;
}
