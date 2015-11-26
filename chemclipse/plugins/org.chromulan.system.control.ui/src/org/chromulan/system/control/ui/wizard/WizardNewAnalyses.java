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

import org.eclipse.chemclipse.converter.core.ISupplier;
import org.eclipse.jface.wizard.Wizard;

public class WizardNewAnalyses extends Wizard {

	private WizarPageNewAnalysesMain base;

	public WizardNewAnalyses() {

		super();
		base = new WizarPageNewAnalysesMain("Select Default Directory");
	}

	@Override
	public void addPages() {

		addPage(base);
	}

	public File getFile() {

		return base.getFile();
	}

	public ISupplier getSupplier() {

		return base.getSupplier();
	}

	@Override
	public boolean performFinish() {

		return base.isPageComplete();
	}
}
