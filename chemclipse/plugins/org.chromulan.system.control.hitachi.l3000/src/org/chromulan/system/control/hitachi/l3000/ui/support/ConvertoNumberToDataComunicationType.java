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
import org.eclipse.core.databinding.conversion.Converter;

public class ConvertoNumberToDataComunicationType extends Converter {

	public ConvertoNumberToDataComunicationType() {
		super(Integer.class, String.class);
	}

	@Override
	public Object convert(Object fromObject) {

		if(fromObject == null) {
			return null;
		}
		Integer number = (Integer)fromObject;
		if(number == ControlDevice.OUTPUT_ANALOG) {
			return ControlDevice.SETTING_DATA_OUTPUT_ANALOG;
		} else if(number == ControlDevice.OUTPUT_DIGITAL) {
			return ControlDevice.SETTING_DATA_OUTPUT_DATA_COMMUNICATION;
		}
		return null;
	}
}
