/*******************************************************************************
 * Copyright (c) 2015, 2016 Jan Holy.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.chromulan.system.control.device;

import java.util.LinkedList;
import java.util.List;

public class ControlDevices implements IControlDevices {

	private List<IControlDevice> controlDevices;

	public ControlDevices() {
		controlDevices = new LinkedList<IControlDevice>();
	}

	@Override
	public List<IControlDevice> getControlDevices() {

		return controlDevices;
	}
}
