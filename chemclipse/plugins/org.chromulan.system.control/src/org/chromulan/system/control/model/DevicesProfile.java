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
package org.chromulan.system.control.model;

public class DevicesProfile implements IDevicesProfile {

	private IControlDevices controlDevices;
	private String name;

	public DevicesProfile() {
		controlDevices = new ControlDevices();
	}

	@Override
	public IControlDevices getControlDevices() {

		return controlDevices;
	}

	@Override
	public String getName() {

		return name;
	}

	@Override
	public void setControlDevice(IControlDevices devices) {

		this.controlDevices = devices;
	}

	@Override
	public void setName(String name) {

		this.name = name;
	}
}
