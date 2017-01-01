/*******************************************************************************
 * Copyright (c) 2016, 2017 Jan Holy.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.chromulan.system.control.device.setting;

public abstract class AbstractValueNumber<Num extends Number> extends AbstractValue<Num> implements IValueNumber<Num> {

	/**
	 *
	 */
	private static final long serialVersionUID = -955888226927580978L;
	private String unit;

	public AbstractValueNumber(IDeviceSetting deviceSetting, String identificator, String name, Num defValue, String unit) {
		super(deviceSetting, identificator, name, defValue);
		this.unit = unit;
	}

	@Override
	public String getUnit() {

		return unit;
	}

	@Override
	public IValueNumber<Num> setUnit(String unit) {

		this.unit = unit;
		return this;
	}

	@Override
	public String valueToString() {

		if(unit != null && !unit.isEmpty()) {
			return super.valueToString();
		} else {
			return super.valueToString() + " " + unit;
		}
	}
}
