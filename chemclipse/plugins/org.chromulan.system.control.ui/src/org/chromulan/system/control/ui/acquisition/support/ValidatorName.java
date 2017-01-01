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

public class ValidatorName implements IValidator {

	@Override
	public IStatus validate(Object value) {

		if(value == null) {
			return ValidationStatus.error("NAME: name is empty");
		}
		if(value instanceof String) {
			String ss = (String)value;
			if(ss.isEmpty()) {
				return ValidationStatus.error("NAME: name is empty");
			} else if(!ss.trim().equals(ss)) {
				return ValidationStatus.error("NAME: name can not start or end whitespace");
			} else if(ss.contains("<")) {
				return ValidationStatus.error("NAME: name contains unsupported character <");
			} else if(ss.contains(">")) {
				return ValidationStatus.error("NAME: name contains unsupported character >");
			} else if(ss.contains(":")) {
				return ValidationStatus.error("NAME: name contains unsupported character :");
			} else if(ss.contains("/")) {
				return ValidationStatus.error("NAME: name contains unsupported character /");
			} else if(ss.contains("\\")) {
				return ValidationStatus.error("NAME: name contains unsupported character \\");
			} else if(ss.contains("|")) {
				return ValidationStatus.error("NAME: name contains unsupported character |");
			} else if(ss.contains("?")) {
				return ValidationStatus.error("NAME: name contains unsupported character ?");
			} else if(ss.contains("*")) {
				return ValidationStatus.error("NAME: name contains unsupported character *");
			} else {
				return Status.OK_STATUS;
			}
		} else {
			return ValidationStatus.error("NAME: invalid name");
		}
	}
}
