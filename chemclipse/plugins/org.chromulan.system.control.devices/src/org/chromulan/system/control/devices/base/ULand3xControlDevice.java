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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import net.sourceforge.ulan.base.DeviceDescription;

public class ULand3xControlDevice extends DetectorULanControlDevice {

	public final static int DEFAULT_SCAN_INTERVAL = 100;

	public ULand3xControlDevice() {
		super();
	}

	public ULand3xControlDevice(DeviceDescription description, boolean isConnected) {
		super(description, isConnected, DEFAULT_SCAN_INTERVAL);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

		super.readExternal(in);
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {

		super.writeExternal(out);
	}
}
