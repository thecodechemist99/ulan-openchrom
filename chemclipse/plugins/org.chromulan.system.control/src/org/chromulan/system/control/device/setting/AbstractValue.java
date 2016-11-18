package org.chromulan.system.control.device.setting;

import java.io.Serializable;

public abstract class AbstractValue<ValueType extends Serializable> implements IValue<ValueType> {

	/**
	 *
	 */
	private static final long serialVersionUID = 3608658762358725659L;
	private ValueType defValue;
	private IDeviceSetting device;
	private String id;
	private boolean isChangeable;
	private boolean isPrintable;
	private String name;
	private ValueType value;

	public AbstractValue(IDeviceSetting deviceSetting, String identificator, String name, ValueType defValue) {
		this.device = deviceSetting;
		this.name = name;
		this.isChangeable = true;
		this.isPrintable = true;
		this.defValue = defValue;
		this.value = defValue;
		this.id = identificator;
	}

	@Override
	public boolean equals(Object obj) {

		if(obj instanceof IValue<?>) {
			IValue<?> compareValue = (IValue<?>)obj;
			if(compareValue.getIdentificator().equals(getIdentificator()) && compareValue.getValue().equals(getValue())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public ValueType getDefaulValue() {

		return defValue;
	}

	@Override
	public IDeviceSetting getDevice() {

		return device;
	}

	@Override
	public String getIdentificator() {

		return id;
	}

	@Override
	public String getName() {

		return name;
	}

	@Override
	public ValueType getValue() {

		return value;
	}

	@Override
	public boolean isChangeable() {

		return isChangeable;
	}

	@Override
	public boolean isPrintable() {

		return isPrintable;
	}

	@Override
	public IValue<ValueType> setDefValue(ValueType defValue) {

		this.defValue = defValue;
		return this;
	}

	@Override
	public IValue<ValueType> setDevice(IDeviceSetting deviceSetting) {

		this.device = deviceSetting;
		return this;
	}

	@Override
	public IValue<ValueType> setChangeable(boolean b) {

		this.isChangeable = b;
		return this;
	}

	@Override
	public IValue<ValueType> setIdentificator(String id) {

		this.id = id;
		return this;
	}

	@Override
	public IValue<ValueType> setPrintable(boolean isPrintable) {

		this.isPrintable = isPrintable;
		return this;
	}

	@Override
	public IValue<ValueType> setValue(ValueType value) {

		this.value = value;
		return this;
	}
}
