package org.chromulan.system.control.device.setting;

public class ValueEnumeration<Value> extends AbstractValue<Value>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1550272675179584635L;
	private Value[] values;
	private int value;
	private int defValue;

	
	
	
	
	public ValueEnumeration(IDeviceSetting device, String name, Value[] values, int defValue, boolean isChangeable) {
		super(device,name,isChangeable);
		this.values = values;
		this.defValue = defValue;
	}

	@Override
	public Value getValue() {
		return values[value];
	}

	@Override
	public void setValue(Value value) {
		for (int i = 0; i < values.length; i++) {
			if (values[i].equals(value)) {
				this.value = i;
				return;
			}
		}	
	}
	
	public int getOrderSelection()
	{
		return value;
	}
	
	public Value[] getValues()
	{
		return values;
	}
	

	@Override
	public Value getDefaulValue() {
		
		return values[defValue];
	}

	
	@Override
	public String valueToString() {
		
		return values[value].toString();
	}


}
