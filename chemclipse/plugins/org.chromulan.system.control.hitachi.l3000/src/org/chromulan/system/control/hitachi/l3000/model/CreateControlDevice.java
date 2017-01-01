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
package org.chromulan.system.control.hitachi.l3000.model;

import java.io.IOException;
import java.io.ObjectInput;

import org.chromulan.system.control.device.IControlDevice;
import org.chromulan.system.control.manager.devices.ICreateControlDevice;

public class CreateControlDevice implements ICreateControlDevice {

	@Override
	public IControlDevice createDevice(String className, ObjectInput in) throws ClassNotFoundException, IOException {

		if(className.equals(ControlDevice.class.getName())) {
			return (ControlDevice)in.readObject();
		} else {
			return null;
		}
	}
}
