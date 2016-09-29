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

import java.util.List;

import org.chromulan.system.control.model.IAcquisition;

public interface IDevicesProfile extends IControlDevices {

	void addAcquisition(IAcquisition acquisition);

	boolean containsAcqusition();

	boolean containsAcqusition(IAcquisition acquisition);

	List<IAcquisition> getAcquisitio();

	String getName();

	void removeAcqusition(IAcquisition acquisition);

	void setName(String name);
}
