package org.chromulan.system.control.device.setting;

public interface IValueDecimalNumber<DecimalNumber extends Number> extends IValue<DecimalNumber> {

	int getNumeberDecimalPlace();

	void setNumeberDecimalPlace(int number);
}
