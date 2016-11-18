package org.chromulan.system.control.device.setting;

public class ValueShort extends AbstractValueNumber<Short> {

	/**
	 *
	 */
	private static final long serialVersionUID = 5785653019532647850L;

	public ValueShort(IDeviceSetting device, String identificator, String name, Short defValue, String unit) {
		super(device, identificator, name, defValue, unit);
	}
}
