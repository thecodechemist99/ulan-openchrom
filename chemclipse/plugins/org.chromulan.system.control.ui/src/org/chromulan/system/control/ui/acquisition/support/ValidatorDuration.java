/*******************************************************************************
 * Copyright (c) 2015, 2017 Jan Holy.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.chromulan.system.control.ui.acquisition.support;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class ValidatorDuration implements IValidator {

	@Override
	public IStatus validate(Object value) {

		if(value != null) {
			long i = (Long)value;
			if(i >= 1) {
				return Status.OK_STATUS;
			} else {
				return ValidationStatus.error("INTERVAL: value have to be larger then zero");
			}
		} else {
			return ValidationStatus.error("INTERVAL: is not number");
		}
	}
}
