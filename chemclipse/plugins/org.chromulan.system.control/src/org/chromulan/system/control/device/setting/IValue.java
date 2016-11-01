package org.chromulan.system.control.device.setting;

import java.io.Serializable;

public interface IValue<ValueType extends Serializable> extends Serializable {

	ValueType getDefaulValue();

	IDeviceSetting getDevice();

	String getIdentificator();

	String getName();

	ValueType getValue();

	boolean isChangeable();

	boolean isPrintable();

	IValue<ValueType> setDefValue(ValueType defValue);

	IValue<ValueType> setDevice(IDeviceSetting deviceSetting);

	IValue<ValueType> setChangeable(boolean b);

	IValue<ValueType> setIdentificator(String id);

	IValue<ValueType> setPrintable(boolean b);

	IValue<ValueType> setValue(ValueType value);

	default String valueToString() {

		return getValue().toString();
	}
}
