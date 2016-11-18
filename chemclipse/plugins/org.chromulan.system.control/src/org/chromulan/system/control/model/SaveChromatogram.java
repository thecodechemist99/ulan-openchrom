/*******************************************************************************
 * Copyright (c) 2016 Jan Holy.
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

import org.eclipse.chemclipse.model.core.IChromatogram;

public class SaveChromatogram {

	private IChromatogram chromatogram;
	private String name;

	public SaveChromatogram() {
	}

	public SaveChromatogram(IChromatogram chromatogram, String name) {
		this.chromatogram = chromatogram;
		this.name = name;
	}

	public IChromatogram getChromatogram() {

		return chromatogram;
	}

	public String getName() {

		return name;
	}

	public void setChromatogram(IChromatogram chromatogram) {

		this.chromatogram = chromatogram;
	}

	public void setName(String name) {

		this.name = name;
	}
}
