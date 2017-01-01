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
package org.chromulan.system.control.devices.base;

import net.sourceforge.ulan.base.DeviceDescription;

public class Lcd5000ControlDevice extends DetectorULanControlDevice {

	public final static int DEFAULT_SCAN_INTERVAL = 40;

	public Lcd5000ControlDevice() {
		super();
	}

	public Lcd5000ControlDevice(DeviceDescription description, boolean isConnected) {
		super(description, isConnected, DEFAULT_SCAN_INTERVAL);
	}
}
