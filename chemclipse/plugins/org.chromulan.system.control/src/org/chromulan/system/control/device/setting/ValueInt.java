package org.chromulan.system.control.device.setting;

public class ValueInt extends AbstractValueNumber<Integer> {

	/**
	 *
	 */
	private static final long serialVersionUID = -1242498352700767033L;

	public ValueInt(IDeviceSetting device, String identificator, String name, Integer defValue, String unit) {
		super(device, identificator, name, defValue, unit);
	}
}
