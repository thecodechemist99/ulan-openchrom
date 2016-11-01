package org.chromulan.system.control.device.setting;

public class ValueEnumeration implements IValue<IValue<?>> {

	/**
	 *
	 */
	private static final long serialVersionUID = -1550272675179584635L;
	private int defValue;
	private IDeviceSetting device;
	private String id;
	private boolean isChangeble;
	private boolean isPrintable;
	private String name;
	private int order;
	private IValue<?>[] values;

	public ValueEnumeration(IDeviceSetting device, String identificator, String name, IValue<?>[] values, int defValue) {
		this.device = device;
		this.name = name;
		this.defValue = defValue;
		this.order = defValue;
		this.values = values;
		this.id = identificator;
	}

	@Override
	public IValue<?> getDefaulValue() {

		return values[defValue];
	}

	@Override
	public IDeviceSetting getDevice() {

		return device;
	}

	@Override
	public String getIdentificator() {

		return id;
	}

	@Override
	public String getName() {

		return name;
	}

	@Override
	public IValue<?> getValue() {

		return values[order];
	}

	@Override
	public boolean isChangeable() {

		return isChangeble;
	}

	@Override
	public boolean isPrintable() {

		return isPrintable;
	}

	@Override
	public IValue<IValue<?>> setDefValue(IValue<?> defValue) {

		for(int i = 0; i < values.length; i++) {
			if(values[i].equals(defValue)) {
				this.defValue = i;
				break;
			}
		}
		return this;
	}

	@Override
	public IValue<IValue<?>> setDevice(IDeviceSetting deviceSetting) {

		this.device = deviceSetting;
		return this;
	}

	@Override
	public IValue<IValue<?>> setChangeable(boolean b) {

		this.isChangeble = b;
		return this;
	}

	@Override
	public IValue<IValue<?>> setIdentificator(String id) {

		this.id = id;
		return this;
	}

	@Override
	public IValue<IValue<?>> setPrintable(boolean b) {

		this.isPrintable = b;
		return this;
	}

	@Override
	public IValue<IValue<?>> setValue(IValue<?> value) {

		for(int i = 0; i < values.length; i++) {
			if(values[i].equals(defValue)) {
				this.order = i;
				break;
			}
		}
		return this;
	}

	public IValue<?> setValueByOrder(int order) {

		this.order = order;
		return this;
	}
}
