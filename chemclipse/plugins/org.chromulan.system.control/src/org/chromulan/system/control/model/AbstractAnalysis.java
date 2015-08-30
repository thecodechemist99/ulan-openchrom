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
package org.chromulan.system.control.model;

import org.eclipse.chemclipse.model.core.IChromatogram;

public abstract class AbstractAnalysis implements IAnalysis {

	private IChromatogram chromatogram;
	private String name;
	private boolean isRecording;

	public AbstractAnalysis() {

		name = "";
		chromatogram = null;
		isRecording = false;
	}

	@Override
	public void setChromatogram(IChromatogram chromatogram) {

		this.chromatogram = chromatogram;
	}

	@Override
	public IChromatogram getChromatogram() {

		return chromatogram;
	}

	@Override
	public void setName(String name) {

		this.name = name;
	}

	@Override
	public String getName() {

		return name;
	}

	@Override
	public boolean isRecerding() {

		return isRecording;
	}

	@Override
	public void setRecording(boolean b) {

		isRecording = b;
	}
}
