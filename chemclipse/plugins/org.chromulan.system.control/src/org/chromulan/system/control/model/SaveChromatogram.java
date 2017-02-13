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
package org.chromulan.system.control.model;

import java.util.HashMap;

import org.eclipse.chemclipse.model.core.IChromatogram;

public class SaveChromatogram {

	final private HashMap<String, String> deviceProperties = new HashMap<>();
	private IChromatogram chromatogram;
	private String name;
	private String nameDevice;

	public SaveChromatogram(IChromatogram chromatogram, String name, String nameDevice) {
		this.chromatogram = chromatogram;
		this.name = name;
		this.nameDevice = nameDevice;
	}

	public boolean addDevicePropertie(String name, String value) {

		String ss = deviceProperties.put(name, value);
		if(ss == null) {
			return true;
		} else {
			return false;
		}
	}

	public HashMap<String, String> getDeviceProperties() {

		return deviceProperties;
	}

	public IChromatogram getChromatogram() {

		return chromatogram;
	}

	public String getName() {

		return name;
	}

	public String getNameDevice() {

		return nameDevice;
	}

	public void setChromatogram(IChromatogram chromatogram) {

		this.chromatogram = chromatogram;
	}

	public void setName(String name) {

		this.name = name;
	}

	public void setNameDevice(String nameDevice) {

		this.nameDevice = nameDevice;
	}
}
