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
package org.chromulan.system.control.hitachi.l3000.model;

import org.chromulan.system.control.model.ChromatogramWSDAcquisition;
import org.chromulan.system.control.model.IChromatogramWSDAcquisition;

public class DeviceCommunication {

	private ControlDevice controlDevice;
	private IChromatogramWSDAcquisition chromatogramWSDAcquisition;
	private String name;

	public DeviceCommunication(ControlDevice controlDevice) {
		this.controlDevice = controlDevice;
		chromatogramWSDAcquisition = new ChromatogramWSDAcquisition(controlDevice.getTimeIntervalMill(), 0);
	}

	public void closeConnection() {

	}

	public ControlDevice getControlDevice() {

		return controlDevice;
	}

	public IChromatogramWSDAcquisition getChromatogram() {

		return chromatogramWSDAcquisition;
	}

	public String getName() {

		return name;
	}

	public boolean openConnection() {

		return false;
	}

	public void resetChromatogram() {

		chromatogramWSDAcquisition.newAcquisition(controlDevice.getTimeIntervalMill(), 0);
	}

	public void setDevices() {

		setOutput();
		setTimeInterval();
		setWaveLenghtInterval();
		setWaveRange();
	}

	public void setOutput() {

	}

	public void setParamenters() {

	}

	public void setTimeInterval() {

	}

	public void setWaveLenghtInterval() {

	}

	public void setWaveRange() {

	}

	public void start() {

		resetChromatogram();
	}

	public void stop() {

	}
}
