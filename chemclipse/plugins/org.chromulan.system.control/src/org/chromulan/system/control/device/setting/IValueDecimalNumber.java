package org.chromulan.system.control.device.setting;

public interface IValueDecimalNumber<DecimalNumber extends Number> extends IValueNumber<DecimalNumber>{
	
	void setNumeberDecimalPlace(int number);
	
	int getNumeberDecimalPlace();
	
}
