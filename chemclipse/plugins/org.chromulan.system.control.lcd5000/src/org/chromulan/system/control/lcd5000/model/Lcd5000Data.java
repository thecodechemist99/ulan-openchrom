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
package org.chromulan.system.control.lcd5000.model;

import org.chromulan.system.control.model.IControlDevice;
import org.chromulan.system.control.model.data.IDetectorData;
import org.eclipse.chemclipse.model.core.IChromatogram;

public class Lcd5000Data implements IDetectorData {

	private IControlDevice controlDevice;
	private String description;
	private IChromatogram chromatogram;

	public Lcd5000Data(IControlDevice controlDevice) {
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
