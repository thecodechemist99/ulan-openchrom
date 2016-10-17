package org.chromulan.system.control.device.setting;

import java.io.Serializable;

public class ValueEnumeration<Value extends Serializable> extends AbstractValue<Value> {

	/**
	 *
	 */
	private static final long serialVersionUID = -1550272675179584635L;
	private Value[] values;

	public ValueEnumeration(IDeviceSetting device, String name, Value[] values, int defValue) {
		super(device, name, values[defValue]);
	}

	public int getOrderValue() {

		Value v = getValue();
		for(int i = 0; i < values.length; i++) {
			if(values[i].equals(v)) {
				return i;
			}
		}
		return -1;
	}

	public Value[] getValues() {

		return values;
	}

	@Override
	public void setDefValue(Value defValue) {

		for(int i = 0; i < values.length; i++) {
			if(values[i].equals(defValue)) {
				super.setDefValue(defValue);
				return;
			}
		}
	}

	@Override
	public void setValue(Value value) {

		for(int i = 0; i < values.length; i++) {
			if(values[i].equals(value)) {
				super.setValue(value);
				return;
			}
		}
	}
}
