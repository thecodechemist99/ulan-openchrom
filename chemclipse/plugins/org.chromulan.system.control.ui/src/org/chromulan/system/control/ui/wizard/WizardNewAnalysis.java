/*******************************************************************************
 * Copyright (c) 2015 Majitel.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Majitel - initial API and implementation
 *******************************************************************************/
package org.chromulan.system.control.ui.wizard;

import org.eclipse.jface.wizard.Wizard;

public class WizardNewAnalysis extends Wizard {

	private WizardPageOne page1;

	public WizardNewAnalysis() {

		super();
		setNeedsProgressMonitor(true);
		setWindowTitle("Analysis wizard");
	}

	@Override
	public void addPages() {

		page1 = new WizardPageOne();
		addPage(page1);
	}

	@Override
	public boolean performFinish() {

		return page1.isPageComplete();
	}

	public String getAnalysisName() {

		return page1.getAnalysisName();
	}
}
