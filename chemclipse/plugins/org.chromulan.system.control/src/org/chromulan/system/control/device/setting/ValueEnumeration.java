package org.chromulan.system.control.device.setting;

public class ValueEnumeration extends AbstractValue<String>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1550272675179584635L;
	private String[] values;
	private int value;
	private int defValue;

	
	
	
	
	public ValueEnumeration(IDeviceSetting device, String name, String[] values, int defValue, boolean isChangeable) {
		super(device,name,isChangeable);
		this.values = values;
		this.defValue = defValue;
	}

	@Override
	public String getValue() {
		return values[value];
	}

	@Override
	public void setValue(String value) {
		if(isChangeable()){
			for (int i = 0; i < values.length; i++) {
				if (values[i].equals(value)) {
					this.value = i;
					return;
				}
			}
		}	
	}

	@Override
	public String getDefaulValue() {
		
		return values[defValue];
	}

	
	@Override
	public String valueToString() {
		
		return values[value];
	}


}
