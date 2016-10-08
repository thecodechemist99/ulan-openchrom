package org.chromulan.system.control.device.setting;

public interface IValueNumber<Num extends Number> extends IValue<Num> {

	Num getMax();

	void setMax(Num max);

	Num getMin();

	void setMin(Num min);

}
