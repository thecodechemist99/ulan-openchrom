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
package org.chromulan.system.control.ulad3x.model;

import org.chromulan.system.control.device.IControlDevice;
import org.chromulan.system.control.devices.base.IUlanControlDevice;
import org.chromulan.system.control.devices.base.data.IDetectorData;
import org.eclipse.chemclipse.model.core.IChromatogram;

public class ULad3xData implements IDetectorData {

	private IUlanControlDevice controlDevice;
	private String description;
	private IChromatogram chromatogram;

	public ULad3xData(IUlanControlDevice controlDevice) {
		super();
		this.controlDevice = controlDevice;
	}

	@Override
	public IControlDevice getControlDevice() {

		return controlDevice;
	}

	@Override
	public String getDescription() {

		return description;
	}

	@Override
	public IChromatogram getChromatogram() {

		return chromatogram;
	}

	@Override
	public String getName() {

		return controlDevice.getName();
	}

	public void setDescription(String description) {

		this.description = description;
	}

	public void setChromatogram(IChromatogram chromatogram) {

		this.chromatogram = chromatogram;
	}
}
