package org.chromulan.system.control.device.setting;

public class ValueByte extends ValueNumer<Byte> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2967895273065840669L;

	public ValueByte(IDeviceSetting device, String name, Byte defValue, boolean isChangeable) {
		super(device, name, defValue, isChangeable, Byte.MIN_VALUE, Byte.MAX_VALUE);
	}

}
