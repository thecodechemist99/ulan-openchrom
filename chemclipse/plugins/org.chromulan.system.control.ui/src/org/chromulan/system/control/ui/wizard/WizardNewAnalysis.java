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

import java.util.List;

import org.chromulan.system.control.model.IDevicesProfile;
import org.eclipse.jface.wizard.Wizard;

public class WizardNewAnalysis extends Wizard {

	private WizardModelAnalysis modelAnalysis;
	private WizardNewAnalysisProfile page1;
	private WizardNewAnalysisMain page2;
	private WizardNewAnalysisMultiple page3;

	public WizardNewAnalysis(List<IDevicesProfile> devicesProfil) {
		super();
		setNeedsProgressMonitor(true);
		setWindowTitle("Analysis wizard");
		modelAnalysis = new WizardModelAnalysis();
		page1 = new WizardNewAnalysisProfile(devicesProfil);
		page2 = new WizardNewAnalysisMain();
		page3 = new WizardNewAnalysisMultiple();
	}

	@Override
	public void addPages() {

		addPage(page1);
		addPage(page2);
		addPage(page3);
	}

	public WizardModelAnalysis getModel() {

		return modelAnalysis;
	}

	@Override
	public boolean performFinish() {

		return page2.isPageComplete() && page1.isPageComplete();
	}
}
