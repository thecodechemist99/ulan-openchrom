package org.chromulan.system.control.device.setting;

public class ValueLong extends AbstractValue<Long> {

	/**
	 *
	 */
	private static final long serialVersionUID = 3491606819668506456L;

	public ValueLong(IDeviceSetting device, String name, Long defValue, boolean isChangeable) {
		super(device, name, defValue);
	}
}
