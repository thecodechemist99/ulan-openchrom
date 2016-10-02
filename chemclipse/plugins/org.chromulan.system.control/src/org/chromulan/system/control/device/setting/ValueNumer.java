package org.chromulan.system.control.device.setting;

public abstract class ValueNumer<Num extends Number> implements IValueNumber<Num> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2024634992212923691L;
	private IDeviceSetting device;
	private String name;
	private Num value;
	private Num defValue;
	private boolean isChangeable;
	private Num min;
	private Num max;
	
	public ValueNumer(IDeviceSetting device, String name, Num defValue, boolean isChangeable, Num min, Num max) {
		super();
		this.device = device;
		this.name = name;
		this.defValue = defValue;
		this.isChangeable = isChangeable;
		this.min = min;
		this.max = max;
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
	public Num getValue() {

		return value;
	}

	@Override
	public void setValue(Num value) {
		if (isChangeable) {
			this.value = value;
		}		
	}

	@Override
	public Num getDefaulValue() {
		
		return defValue;
	}

	@Override
	public boolean isChangeable() {
		
		return isChangeable;
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
	
	@Override
	public void setChangeable(boolean b) {
		this.isChangeable = b;		
	}
}
