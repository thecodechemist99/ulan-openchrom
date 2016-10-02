package org.chromulan.system.control.device.setting;

public class ValueEnumeration implements IValue<String>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1550272675179584635L;
	private String[] values;
	private int value;
	private int defValue;
	private String name;
	private IDeviceSetting device;
	private boolean isChangeable;
	
	
	
	
	public ValueEnumeration(IDeviceSetting device, String name, String[] values, int defValue, boolean isChangeable) {
		super();
		this.values = values;
		this.defValue = defValue;
		this.name = name;
		this.device = device;
		this.isChangeable = isChangeable;
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
		return values[value];
	}

	@Override
	public void setValue(String value) {
		if(isChangeable){
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
	public boolean isChangeable() {
		return isChangeable;
	}
	
	@Override
	public String valueToString() {
		
		return values[value];
	}


	@Override
	public void setChangeable(boolean b) {
		this.isChangeable = b;
		
	}

}
