package org.chromulan.system.control.device.setting;

public class ValueFloat extends ValueDecimalNumber<Float>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4466274019988822233L;

	public ValueFloat(IDeviceSetting device, String name, Float defValue, boolean isChangeable) {
		super(device, name, defValue, isChangeable, Float.MIN_VALUE, Float.MAX_VALUE);
	}

}
