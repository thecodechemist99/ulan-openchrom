/*******************************************************************************
 * Copyright (c) 2015, 2017 Jan Holy.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.chromulan.system.control.devices.base;

public enum DeviceType {
	AUTO_SAMLER("Auto Sampler"), COLLECTOR("Colector"), DETECTOR("Detector"), FRACTION_COLLECTOR("Fraction Collector"), PEAK_SELECTOR("Peak Selector"), PUMP("Pump"), THERMOSTAT("Thermostat"), UNKNOWEN("Unknown");

	private String type;

	private DeviceType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {

		return type;
	}
}
