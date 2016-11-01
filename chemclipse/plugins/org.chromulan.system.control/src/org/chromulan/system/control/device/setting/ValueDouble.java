package org.chromulan.system.control.device.setting;

public class ValueDouble extends ValueDecimalNumber<Double> {

	/**
	 *
	 */
	private static final long serialVersionUID = 4309413325867057120L;

	public ValueDouble(IDeviceSetting device, String identificator, String name, Double defValue, String unit, int numeberDecimalPlace) {
		super(device, identificator, name, defValue, unit, numeberDecimalPlace);
	}

	@Override
	public String valueToString() {

		double value = getValue();
		int getNumeber = getNumeberDecimalPlace();
		if(getNumeber >= 0) {
			return String.format("%." + getNumeber + "d", value);
		}
		return super.valueToString();
	}
}
