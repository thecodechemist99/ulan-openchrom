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
import java.util.Iterator;
import java.util.Map.Entry;

import org.eclipse.chemclipse.model.core.IChromatogram;

public class SaveChromatogram {

	final private HashMap<String, HashMap<String, String>> devicesProperties = new HashMap<>();
	private IChromatogram chromatogram;
	private String name;

	public SaveChromatogram(IChromatogram chromatogram, String name, String nameDevice) {
		this.chromatogram = chromatogram;
		this.name = name;
	}

	public void addDevicePropertie(String nameDevice, String name, String value) {

		HashMap<String, String> devicesPropertie = devicesProperties.get(nameDevice);
		if(devicesPropertie != null) {
			devicesPropertie.put(name, value);
		} else {
			devicesPropertie = new HashMap<>();
			devicesPropertie.put(name, value);
			devicesProperties.put(nameDevice, devicesPropertie);
		}
	}

	public HashMap<String, String> getDeviceProperties(String nameDevice) {

		return devicesProperties.get(nameDevice);
	}

	public IChromatogram getChromatogram() {

		return chromatogram;
	}

	public String getName() {

		return name;
	}

	public String[] getNamesDevices() {

		if(!devicesProperties.isEmpty()) {
			String[] names = new String[devicesProperties.size()];
			Iterator<Entry<String, HashMap<String, String>>> iterator = devicesProperties.entrySet().iterator();
			for(int i = 0; iterator.hasNext(); i++) {
				Entry<String, HashMap<String, String>> entry = iterator.next();
				names[i] = entry.getKey();
			}
			return names;
		} else {
			return null;
		}
	}

	public void setChromatogram(IChromatogram chromatogram) {

		this.chromatogram = chromatogram;
	}

	public void setName(String name) {

		this.name = name;
	}
}
