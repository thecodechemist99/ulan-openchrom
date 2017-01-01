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
import org.eclipse.core.databinding.validation.IValidator;

public abstract class ValidatorControlDeviceVariable implements IValidator {

	private ControlDevice controlDevice;

	public ValidatorControlDeviceVariable(ControlDevice controlDevice) {
		this.controlDevice = controlDevice;
	}

	public ControlDevice getControlDevice() {

		return controlDevice;
	}
}
