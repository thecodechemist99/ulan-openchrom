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

public class ValueBoolean extends AbstractValue<Boolean> {

	/**
	 *
	 */
	private static final long serialVersionUID = -8579377221326685L;

	public ValueBoolean(IDeviceSetting deviceSetting, String identificator, String name, Boolean defValue) {
		super(deviceSetting, identificator, name, defValue);
	}
}
