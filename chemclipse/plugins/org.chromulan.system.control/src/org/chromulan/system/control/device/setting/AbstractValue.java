package org.chromulan.system.control.device.setting;

import java.io.Serializable;

public class AbstractValue<ValueType extends Serializable> implements IValue<ValueType> {

	/**
	 *
	 */
	private static final long serialVersionUID = 3608658762358725659L;
	private ValueType defValue;
	private IDeviceSetting device;
	private boolean isChangeable;
	private boolean isPrintable;
	private String name;
	private ValueType value;

	public AbstractValue(IDeviceSetting deviceSetting, String name, ValueType defValue) {
		this.device = deviceSetting;
		this.name = name;
		this.isChangeable = true;
		this.isPrintable = true;
		this.defValue = defValue;
		this.value = defValue;
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
	public void setDefValue(ValueType defValue) {

		this.defValue = defValue;
	}

	@Override
	public void setChangeable(boolean b) {

		this.isChangeable = b;
	}

	@Override
	public void setPrintable(boolean isPrintable) {

		this.isPrintable = isPrintable;
	}

	@Override
	public void setValue(ValueType value) {

		this.value = value;
	}
}
