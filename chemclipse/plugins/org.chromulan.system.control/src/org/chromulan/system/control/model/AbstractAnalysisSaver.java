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

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.chromulan.system.control.model.data.IDetectorData;
import org.eclipse.chemclipse.converter.core.ISupplier;

public abstract class AbstractAnalysisSaver implements IAnalysisSaver {

	private IAnalysis analysis;
	private List<IDetectorData> detectorsData;
	private File file;
	private ISupplier supplier;

	public AbstractAnalysisSaver(IAnalysis analysis) {
		detectorsData = new LinkedList<IDetectorData>();
		this.analysis = analysis;
	}

	@Override
	public void addDetectorData(IDetectorData detectorData) {

		detectorsData.add(detectorData);
	}

	@Override
	public IAnalysis getAnalysis() {

		return analysis;
	}

	@Override
	public List<IDetectorData> getDetectorsData() {

		return detectorsData;
	}

	@Override
	public File getFile() {

		return file;
	}

	@Override
	public ISupplier getSupplier() {

		return supplier;
	}

	@Override
	public void removeAllDetectorData() {

		detectorsData.clear();
	}

	@Override
	public void removeDetectorData(IDetectorData detectorData) {

		detectorsData.remove(detectorData);
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
