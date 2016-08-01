/*******************************************************************************
 * Copyright (c) 2016 Jan Holy.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.chromulan.system.control.data;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.chromulan.system.control.model.ControlDevices;
import org.chromulan.system.control.model.DevicesProfiles;
import org.chromulan.system.control.model.IControlDevices;
import org.chromulan.system.control.model.IDevicesProfiles;

public class DataStore implements IDataStore {

	private IControlDevices devices;
	private IDevicesProfiles profiles;

	public DataStore() {
		devices = new ControlDevices();
		profiles = new DevicesProfiles();
	}

	@Override
	public IControlDevices getControlDevices() {

		return devices;
	}

	@Override
	public IDevicesProfiles getDevicesProfiles() {

		return profiles;
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

		this.devices = (IControlDevices)in.readObject();
		this.profiles = (IDevicesProfiles)in.readObject();
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {

		out.writeObject(devices);
		out.writeObject(profiles);
	}
}
