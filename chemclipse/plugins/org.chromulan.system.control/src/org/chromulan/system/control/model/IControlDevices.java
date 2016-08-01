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

import java.io.Externalizable;
import java.util.List;

public interface IControlDevices extends Externalizable {

	boolean add(IControlDevice device);

	boolean contains(String id);

	IControlDevice getControlDevice(String id);

	List<IControlDevice> getControlDevices();

	boolean remove(String id);

	void removeAllControlDevices();
}
