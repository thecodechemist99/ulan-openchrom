package org.chromulan.system.control.device.setting;

public abstract class ValueDecimalNumber<Num extends Number> extends AbstractValueNumber<Num> implements IValueDecimalNumber<Num> {

	/**
	 *
	 */
	private static final long serialVersionUID = 2560189805897499195L;
	private int decimalNumber;

	public ValueDecimalNumber(IDeviceSetting device, String identificator, String name, Num defValue, String unit, int numeberDecimalPlace) {
		super(device, identificator, name, defValue, unit);
		this.decimalNumber = numeberDecimalPlace;
	}

	@Override
	public int getNumeberDecimalPlace() {

		return decimalNumber;
	}

	@Override
	public void setNumeberDecimalPlace(int number) {

		this.decimalNumber = number;
	}
}
