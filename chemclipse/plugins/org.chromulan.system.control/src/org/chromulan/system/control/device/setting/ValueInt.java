package org.chromulan.system.control.device.setting;

public class ValueInt extends ValueNumer<Integer>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1242498352700767033L;

	public ValueInt(IDeviceSetting device, String name, Integer defValue, boolean isChangeable) {
		super(device, name, defValue, isChangeable, Integer.MIN_VALUE, Integer.MAX_VALUE);
	}

}
