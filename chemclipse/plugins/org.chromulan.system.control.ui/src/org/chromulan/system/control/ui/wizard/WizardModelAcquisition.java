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

	public IObservableValue<String> acquisitionType = new WritableValue<>(null, String.class);
	public IObservableValue<Float> amount = new WritableValue<>(null, Float.class);
	public IObservableValue<String> analysis = new WritableValue<>(null, String.class);
	public IObservableValue<Boolean> autoStop = new WritableValue<>(true, Boolean.class);
	public IObservableValue<String> column = new WritableValue<>(null, String.class);
	public IObservableValue<String> description = new WritableValue<>(null, String.class);
	public IObservableValue<String> detection = new WritableValue<>(null, String.class);
	public IObservableValue<IDevicesProfile> devicesProfile = new WritableValue<>(null, IDevicesProfile.class);
	public IObservableValue<Long> duration = new WritableValue<>(600000L, Long.class);
	public IObservableValue<Float> flowRate = new WritableValue<>(null, Float.class);
	public IObservableValue<String> flowRateUnit = new WritableValue<>(IAcquisition.FLOW_RATE_UNIT_ML_MIN, String.class);
	public IObservableValue<File> folder = new WritableValue<>(null, File.class);
	public IObservableValue<Float> injectionVolume = new WritableValue<>(null, Float.class);
	public IObservableValue<Float> ISTDAmount = new WritableValue<>(null, Float.class);
	public IObservableValue<String> mobilPhase = new WritableValue<>(null, String.class);
	public IObservableValue<String> name = new WritableValue<>("", String.class);
	public IObservableValue<Integer> numberofAcquisitions = new WritableValue<>(1, Integer.class);
	public IObservableValue<Float> temperature = new WritableValue<>(null, Float.class);
	public IObservableValue<String> temperatureUnit = new WritableValue<>(IAcquisition.TEMPERATURE_UNIT_C, String.class);
}
