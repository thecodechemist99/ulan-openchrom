package org.chromulan.system.control.device.setting;

public abstract class AbstractValue<ValueType> implements IValue<ValueType> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3608658762358725659L;
	private IDeviceSetting device;
	private String name;
	private boolean isChangeable;
	private boolean isPrintable;

	public AbstractValue(IDeviceSetting deviceSetting, String name, boolean isChangeable) {
		this.device = deviceSetting;
		this.name = name;
		this.isChangeable = isChangeable;
		isPrintable = true;
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
	public boolean isChangeable() {

		return isChangeable;
	}

	@Override
	public void setChangeable(boolean b) {

		this.isChangeable = b;

	}

	@Override
	public boolean isPrintable() {
		return isPrintable;
	}

	@Override
	public void setPrintable(boolean isPrintable) {
		this.isPrintable = isPrintable;

	}

}
