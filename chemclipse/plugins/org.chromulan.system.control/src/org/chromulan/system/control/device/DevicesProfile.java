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

import java.util.LinkedList;
import java.util.List;

import org.chromulan.system.control.model.IAcquisition;

public class DevicesProfile extends ControlDevices implements IDevicesProfile {

	private List<IAcquisition> acquisitions;
	private String name;

	public DevicesProfile() {
		super();
		acquisitions = new LinkedList<>();
	}

	@Override
	public void addAcquisition(IAcquisition acquisition) {

		if (!containsAcqusition(acquisition)) {
			acquisitions.add(acquisition);
		}
	}

	@Override
	public boolean containsAcqusition() {

		return !acquisitions.isEmpty();
	}

	@Override
	public boolean containsAcqusition(IAcquisition acquisition) {

		return acquisitions.contains(acquisition);
	}

	@Override
	public List<IAcquisition> getAcquisitio() {

		return acquisitions;
	}

	@Override
	public String getName() {

		return name;
	}

	@Override
	public void removeAcqusition(IAcquisition acquisition) {

		acquisitions.remove(acquisition);
	}

	@Override
	public void setName(String name) {

		this.name = name;
	}
}
