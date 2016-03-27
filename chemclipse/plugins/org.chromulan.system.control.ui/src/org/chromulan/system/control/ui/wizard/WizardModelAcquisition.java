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
package org.chromulan.system.control.ui.wizard;

import java.io.File;

import org.chromulan.system.control.model.IDevicesProfile;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;

public class WizardModelAcquisition {

	public IObservableValue autoStop;
	public IObservableValue description;
	public IObservableValue devicesProfile;
	public IObservableValue duration;
	public IObservableValue folder;
	public IObservableValue name;
	public IObservableValue numberofAcquisitions;

	public WizardModelAcquisition() {
		autoStop = new WritableValue(true, Boolean.class);
		folder = new WritableValue(null, File.class);
		duration = new WritableValue(600000L, Long.class);
		name = new WritableValue("", String.class);
		numberofAcquisitions = new WritableValue(1, Integer.class);
		devicesProfile = new WritableValue(null, IDevicesProfile.class);
		description = new WritableValue("", String.class);
	}
}
