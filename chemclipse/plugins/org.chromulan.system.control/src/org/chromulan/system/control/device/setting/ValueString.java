package org.chromulan.system.control.device.setting;

public class ValueString implements IValue<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4402933805287269255L;
	IDeviceSetting device;
	String value;
	String defValue;
	boolean isChangeable;
	int maxNumCharacters;
	int minNumCharacters;
	String name;
	

	public ValueString(IDeviceSetting deviceSetting,String name, String defValue, boolean isChangeable) {
		super();
		this.device = deviceSetting;
		this.name = name;
		this.defValue = defValue;
		this.value = defValue;
		this.isChangeable = isChangeable;
		this.maxNumCharacters = -1;
		this.minNumCharacters = -1;
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
	public String getValue() {
		
		return value;
	}

	@Override
	public void setValue(String value) {
		if (isChangeable) {
			this.value = value;
		}
	}

	@Override
	public String getDefaulValue() {
		
		return defValue;
	}

	@Override
	public boolean isChangeable() {
		
		return isChangeable;
	}
	
	public void setMaxNumCharacters(int maxNumCharacters) {
		this.maxNumCharacters = maxNumCharacters;
	}
	
	public void setMinNumCharacters(int minNumCharacters) {
		this.minNumCharacters = minNumCharacters;
	}
	
	public int getMaxNumCharacters() {
		return maxNumCharacters;
	}

	public int getMinNumCharacters() {
		return minNumCharacters;
	}

	@Override
	public void setChangeable(boolean b) {
		this.isChangeable = b;
	}

}
