package org.chromulan.system.control.device.setting;

public class ValueFloat extends ValueDecimalNumber<Float> {

	/**
	 *
	 */
	private static final long serialVersionUID = 4466274019988822233L;

	public ValueFloat(IDeviceSetting device, String name, Float defValue, int numeberDecimalPlace) {
		super(device, name, defValue, numeberDecimalPlace);
	}

	@Override
	public String valueToString() {

		float value = getValue();
		int getNumeber = getNumeberDecimalPlace();
		if(getNumeber >= 0) {
			return String.format("%." + getNumeber + "f", value);
		}
		return super.valueToString();
	}
}
