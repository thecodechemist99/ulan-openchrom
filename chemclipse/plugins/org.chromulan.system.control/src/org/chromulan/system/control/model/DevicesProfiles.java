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
package org.chromulan.system.control.model;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.LinkedList;
import java.util.List;

public class DevicesProfiles implements IDevicesProfiles {

	private List<IDevicesProfile> profiles;

	public DevicesProfiles() {
		profiles = new LinkedList<>();
	}

	@Override
	public void add(IDevicesProfile devicesProfile) {

		profiles.add(devicesProfile);
	}

	@Override
	public boolean contains(IDevicesProfile devicesProfile) {

		return profiles.contains(devicesProfile);
	}

	@Override
	public List<IDevicesProfile> getAll() {

		return profiles;
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

		profiles = (List<IDevicesProfile>)in.readObject();
	}

	@Override
	public void remove(IDevicesProfile devicesProfile) {

		profiles.remove(devicesProfile);
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {

		out.writeObject(profiles);
	}
}
