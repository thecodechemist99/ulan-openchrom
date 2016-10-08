package org.chromulan.system.control.device.setting;

public abstract class ValueDecimalNumber<Num extends Number> extends ValueNumer<Num>
		implements IValueDecimalNumber<Num> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2560189805897499195L;

	public ValueDecimalNumber(IDeviceSetting device, String name, Num defValue, boolean isChangeable, Num min,
			Num max) {
		super(device, name, defValue, isChangeable, min, max);
	}

	private int decimalNumber;

	@Override
	public void setNumeberDecimalPlace(int number) {
		this.decimalNumber = number;

	}

	@Override
	public int getNumeberDecimalPlace() {

		return decimalNumber;
	}

}
