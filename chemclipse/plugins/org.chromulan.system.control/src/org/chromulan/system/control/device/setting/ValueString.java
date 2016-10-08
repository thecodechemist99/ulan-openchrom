package org.chromulan.system.control.device.setting;

public class ValueString extends AbstractValue<String>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4402933805287269255L;
	String value;
	String defValue;
	int maxNumCharacters;
	int minNumCharacters;
	

	public ValueString(IDeviceSetting deviceSetting,String name, String defValue, boolean isChangeable) {
		super(deviceSetting,name,isChangeable);
		this.defValue = defValue;
		this.value = defValue;
		this.maxNumCharacters = -1;
		this.minNumCharacters = -1;
	}


	@Override
	public String getValue() {
		
		return value;
	}

	@Override
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String getDefaulValue() {
		
		return defValue;
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

}
