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

import java.util.List;

public interface IDevicesProfiles {

	void add(IDevicesProfile devicesProfile);

	boolean contains(IDevicesProfile devicesProfile);

	List<IDevicesProfile> getAll();

	void remove(IDevicesProfile devicesProfile);
}
