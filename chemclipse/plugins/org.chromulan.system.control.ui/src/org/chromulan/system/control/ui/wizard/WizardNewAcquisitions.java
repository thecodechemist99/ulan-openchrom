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

import org.eclipse.chemclipse.converter.core.ISupplier;
import org.eclipse.jface.wizard.Wizard;

public class WizardNewAcquisitions extends Wizard {

	private WizarPageNewAcquisitionsMain base;

	public WizardNewAcquisitions(File defFile, ISupplier defSupplierCSD, ISupplier defSupplierMSD, ISupplier defSupplierWSD) {
		super();
		base = new WizarPageNewAcquisitionsMain("Select Default Parameters", defFile, defSupplierCSD, defSupplierWSD, defSupplierMSD);
	}

	@Override
	public void addPages() {

		addPage(base);
	}

	public File getFile() {

		return base.getFile();
	}

	public ISupplier getSupplierCSD() {

		return base.getSupplierCSD();
	}

	public ISupplier getSupplierMSD() {

		return base.getSupplierMSD();
	}

	public ISupplier getSupplierWSD() {

		return base.getSupplierWSD();
	}

	@Override
	public boolean performFinish() {

		return base.isPageComplete();
	}
}
