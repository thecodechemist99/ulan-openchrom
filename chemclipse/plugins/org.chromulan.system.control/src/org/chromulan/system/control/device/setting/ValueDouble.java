package org.chromulan.system.control.device.setting;

public class ValueDouble extends ValueDecimalNumber<Double>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4309413325867057120L;

	public ValueDouble(IDeviceSetting device, String name, Double defValue, boolean isChangeable) {
		super(device, name, defValue, isChangeable, Double.MIN_VALUE, Double.MAX_VALUE);
	}

}
