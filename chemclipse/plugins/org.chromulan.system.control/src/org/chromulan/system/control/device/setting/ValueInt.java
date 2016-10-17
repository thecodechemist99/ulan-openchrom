package org.chromulan.system.control.device.setting;

public class ValueInt extends AbstractValue<Integer> {

	/**
	 *
	 */
	private static final long serialVersionUID = -1242498352700767033L;

	public ValueInt(IDeviceSetting device, String name, Integer defValue) {
		super(device, name, defValue);
	}
}
