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
package org.chromulan.system.control.hitachi.l3000.ui.support;

import org.chromulan.system.control.hitachi.l3000.model.ControlDevice;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class ValidatorWavelenghtRangeFrom extends ValidatorControlDeviceVariable {

	public ValidatorWavelenghtRangeFrom(ControlDevice controlDevice) {
		super(controlDevice);
	}

	@Override
	public IStatus validate(Object value) {

		int controlValue = (Integer)value;
		int rangeTo = getControlDevice().getWavelenghtRangeTo();
		float interva = getControlDevice().getWavelenghtInterval();
		if(controlValue < rangeTo && ((controlValue % 5) == 0) && controlValue >= 200 && controlValue <= 520) {
			if((rangeTo - controlValue) >= interva) {
				return Status.OK_STATUS;
			} else {
				return Status.CANCEL_STATUS;
			}
		}
		return Status.CANCEL_STATUS;
	}
}
