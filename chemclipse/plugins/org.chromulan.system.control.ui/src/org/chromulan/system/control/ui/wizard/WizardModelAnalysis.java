/*******************************************************************************
 * Copyright (c) 2015 Jan Holy.
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

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;

public class WizardModelAnalysis {

	public IObservableValue autoContinue;
	public IObservableValue autoStop;
	public IObservableValue folder;
	public IObservableValue interval;
	public IObservableValue name;
	public IObservableValue numberAnalyses;

	public WizardModelAnalysis() {

		autoContinue = new WritableValue(false, Boolean.class);
		autoStop = new WritableValue(false, Boolean.class);
		folder = new WritableValue(null, File.class);
		interval = new WritableValue(600000L, Long.class);
		name = new WritableValue("", String.class);
		numberAnalyses = new WritableValue(1, Integer.class);
	}
}
