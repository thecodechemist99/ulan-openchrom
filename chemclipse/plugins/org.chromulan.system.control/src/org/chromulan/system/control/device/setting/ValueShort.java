package org.chromulan.system.control.device.setting;

public class ValueShort extends ValueNumer<Short> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5785653019532647850L;

	public ValueShort(IDeviceSetting device, String name, Short defValue, boolean isChangeable) {
		super(device, name, defValue, isChangeable, Short.MIN_VALUE, Short.MAX_VALUE);
	}


}
