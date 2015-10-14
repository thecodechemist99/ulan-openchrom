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

	public IObservableValue name = new WritableValue("", String.class);
	public IObservableValue interval = new WritableValue(600000L, Long.class);
	public IObservableValue autoContinue = new WritableValue(false, Boolean.class);
	public IObservableValue autoStop = new WritableValue(false, Boolean.class);
	public IObservableValue folder = new WritableValue(null, File.class);
}
