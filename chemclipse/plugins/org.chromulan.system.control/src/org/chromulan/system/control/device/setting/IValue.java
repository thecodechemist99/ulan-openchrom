package org.chromulan.system.control.device.setting;

import java.io.Serializable;

public interface IValue<ValueType> extends Serializable {

	IDeviceSetting getDevice();

	String getName();

	ValueType getValue();

	void setValue(ValueType value);

	ValueType getDefaulValue();

	boolean isChangeable();

	void setChangeable(boolean b);

	boolean isPrintable();

	void setPrintable(boolean b);

	default String valueToString() {
		return getValue().toString();
	}
}
