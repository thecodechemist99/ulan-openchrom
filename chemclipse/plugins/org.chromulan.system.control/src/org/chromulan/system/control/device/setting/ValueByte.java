package org.chromulan.system.control.device.setting;

public class ValueByte extends AbstractValue<Byte> {

	/**
	 *
	 */
	private static final long serialVersionUID = 2967895273065840669L;

	public ValueByte(IDeviceSetting device, String identificator, String name, Byte defValue) {
		super(device, identificator, name, defValue);
	}
}
