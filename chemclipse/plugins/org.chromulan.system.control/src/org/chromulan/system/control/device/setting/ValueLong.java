package org.chromulan.system.control.device.setting;

public class ValueLong extends AbstractValueNumber<Long> {

	/**
	 *
	 */
	private static final long serialVersionUID = 3491606819668506456L;

	public ValueLong(IDeviceSetting device, String identificator, String name, Long defValue, String unit) {
		super(device, name, identificator, defValue, unit);
	}
}
