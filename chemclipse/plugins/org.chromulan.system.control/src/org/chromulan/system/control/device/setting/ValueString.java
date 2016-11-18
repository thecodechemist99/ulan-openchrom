package org.chromulan.system.control.device.setting;

public class ValueString extends AbstractValue<String> {

	/**
	 *
	 */
	private static final long serialVersionUID = 4402933805287269255L;

	public ValueString(IDeviceSetting deviceSetting, String identificator, String name, String defValue) {
		super(deviceSetting, identificator, name, defValue);
	}
}
