package org.chromulan.system.control.manager.devices;

import java.io.IOException;
import java.io.ObjectInput;

import org.chromulan.system.control.device.IControlDevice;

public interface ICreateControlDevice {

	String ID = "org.chromulan.system.control.manager.devicesupplier";

	IControlDevice createDevice(String className, ObjectInput in) throws ClassNotFoundException, IOException;
}
