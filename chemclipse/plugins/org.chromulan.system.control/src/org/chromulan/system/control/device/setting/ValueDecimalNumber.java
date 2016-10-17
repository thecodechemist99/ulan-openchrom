package org.chromulan.system.control.device.setting;

public abstract class ValueDecimalNumber<Num extends Number> extends AbstractValue<Num> implements IValueDecimalNumber<Num> {

	/**
	 *
	 */
	private static final long serialVersionUID = 2560189805897499195L;
	private int decimalNumber;

	public ValueDecimalNumber(IDeviceSetting device, String name, Num defValue, int numeberDecimalPlace) {
		super(device, name, defValue);
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
