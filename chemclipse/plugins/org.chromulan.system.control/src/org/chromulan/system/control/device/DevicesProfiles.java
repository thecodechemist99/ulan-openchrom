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
package org.chromulan.system.control.device;

import java.util.LinkedList;
import java.util.List;

public class DevicesProfiles implements IDevicesProfiles {

	final private List<IDevicesProfile> profiles = new LinkedList<>();

	public DevicesProfiles() {
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
	public void remove(IDevicesProfile devicesProfile) {

		profiles.remove(devicesProfile);
	}
}
