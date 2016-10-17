package org.chromulan.system.control.device.setting;

import java.io.Serializable;

public interface IValue<ValueType extends Serializable> extends Serializable {

	ValueType getDefaulValue();

	IDeviceSetting getDevice();

	String getName();

	ValueType getValue();

	boolean isChangeable();

	boolean isPrintable();

	void setDefValue(ValueType defValue);

	void setChangeable(boolean b);

	void setPrintable(boolean b);

	void setValue(ValueType value);

	default String valueToString() {

		return getValue().toString();
	}
}
