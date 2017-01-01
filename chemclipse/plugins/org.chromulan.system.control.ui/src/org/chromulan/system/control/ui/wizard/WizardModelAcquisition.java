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
package org.chromulan.system.control.ui.wizard;

import java.io.File;

import org.chromulan.system.control.device.IDevicesProfile;
import org.chromulan.system.control.model.IAcquisition;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;

public class WizardModelAcquisition {

	public IObservableValue acquisitionType = new WritableValue(null, String.class);
	public IObservableValue amount = new WritableValue(null, Float.class);
	public IObservableValue analysis = new WritableValue(null, String.class);
	public IObservableValue autoStop = new WritableValue(true, Boolean.class);
	public IObservableValue column = new WritableValue(null, String.class);
	public IObservableValue description = new WritableValue(null, String.class);
	public IObservableValue detection = new WritableValue(null, String.class);
	public IObservableValue devicesProfile = new WritableValue(null, IDevicesProfile.class);
	public IObservableValue duration = new WritableValue(600000L, Long.class);
	public IObservableValue flowRate = new WritableValue(null, Float.class);
	public IObservableValue flowRateUnit = new WritableValue(IAcquisition.FLOW_RATE_UNIT_ML_MIN, String.class);
	public IObservableValue folder = new WritableValue(null, File.class);
	public IObservableValue injectionVolume = new WritableValue(null, Float.class);
	public IObservableValue ISTDAmount = new WritableValue(null, Float.class);
	public IObservableValue mobilPhase = new WritableValue(null, String.class);
	public IObservableValue name = new WritableValue("", String.class);
	public IObservableValue numberofAcquisitions = new WritableValue(1, Integer.class);
	public IObservableValue temperature = new WritableValue(null, Float.class);
	public IObservableValue temperatureUnit = new WritableValue(IAcquisition.TEMPERATURE_UNIT_C, String.class);
}
