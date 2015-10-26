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

	private WizardModelAnalysis modelAnalysis;
	private WizardNewAnalysisPageOne page1;
	private WizardNewAnalysisPageTwo page2;

	public WizardNewAnalysis() {

		super();
		setNeedsProgressMonitor(true);
		setWindowTitle("Analysis wizard");
		modelAnalysis = new WizardModelAnalysis();
		page1 = new WizardNewAnalysisPageOne();
		page2 = new WizardNewAnalysisPageTwo();
	}

	@Override
	public void addPages() {

		addPage(page1);
		addPage(page2);
	}

	public WizardModelAnalysis getModel() {

		return modelAnalysis;
	}

	@Override
	public boolean performFinish() {

		return page1.isPageComplete();
	}
}
