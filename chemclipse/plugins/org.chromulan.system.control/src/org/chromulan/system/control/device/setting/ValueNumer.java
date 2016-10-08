package org.chromulan.system.control.device.setting;

public abstract class ValueNumer<Num extends Number> extends AbstractValue<Num> implements IValueNumber<Num> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2024634992212923691L;
	private Num value;
	private Num defValue;
	private Num min;
	private Num max;

	public ValueNumer(IDeviceSetting device, String name, Num defValue, boolean isChangeable, Num min, Num max) {
		super(device, name, isChangeable);
		this.defValue = defValue;
		this.min = min;
		this.max = max;
	}

	@Override
	public Num getValue() {

		return value;
	}

	@Override
	public void setValue(Num value) {
		this.value = value;
	}

	@Override
	public Num getDefaulValue() {

		return defValue;
	}

	@Override
	public Num getMax() {

		return max;
	}

	@Override
	public void setMax(Num max) {
		this.max = max;
	}

	@Override
	public Num getMin() {

		return min;
	}

	@Override
	public void setMin(Num min) {
		this.min = min;
	}
}
