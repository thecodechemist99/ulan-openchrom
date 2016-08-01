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
package org.chromulan.system.control.model;

import java.io.File;

import org.eclipse.chemclipse.converter.core.ISupplier;

public abstract class AbstractAcquisitionSaver implements IAcquisitionSaver {

	private File file;
	private ISupplier supplier;

	@Override
	public File getFile() {

		return file;
	}

	@Override
	public ISupplier getSupplier() {

		return supplier;
	}

	@Override
	public void setFile(File file) {

		this.file = file;
	}

	@Override
	public void setSuplier(ISupplier suplier) {

		this.supplier = suplier;
	}
}
