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

import java.io.Externalizable;

import org.chromulan.system.control.device.IControlDevices;
import org.chromulan.system.control.device.IDevicesProfiles;

public interface IDataStore extends Externalizable {

	IControlDevices getControlDevices();

	IDevicesProfiles getDevicesProfiles();
}
