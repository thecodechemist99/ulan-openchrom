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

import org.chromulan.system.control.model.data.IAnalysisData;
import org.chromulan.system.control.model.data.IChromatogramData;
import org.eclipse.chemclipse.converter.core.ISupplier;

public abstract class AbstractAnalysisSaver implements IAnalysisSaver {

	private IAnalysis analysis;
	private List<IAnalysisData> analysisDataList;
	private File file;
	private List<IChromatogramData> chromatograms;
	private ISupplier supplier;

	public AbstractAnalysisSaver(IAnalysis analysis) {
		chromatograms = new LinkedList<IChromatogramData>();
		analysisDataList = new LinkedList<IAnalysisData>();
		this.analysis = analysis;
	}

	@Override
	public void addDescription(IAnalysisData description) {

		analysisDataList.add(description);
	}

	@Override
	public void addChromatogam(IChromatogramData chromatogram) {

		chromatograms.add(chromatogram);
	}

	@Override
	public IAnalysis getAnalysis() {

		return analysis;
	}

	@Override
	public List<IAnalysisData> getAnalysisDataAll() {

		return analysisDataList;
	}

	@Override
	public File getFile() {

		return file;
	}

	@Override
	public List<IChromatogramData> getChromatograms() {

		return chromatograms;
	}

	@Override
	public ISupplier getSupplier() {

		return supplier;
	}

	@Override
	public void removeAllAnalysisData() {

		analysisDataList.clear();
	}

	@Override
	public void removeAllChromatograms() {

		chromatograms.clear();
	}

	@Override
	public void removeAnalysisData(IAnalysisData description) {

		analysisDataList.remove(description);
	}

	@Override
	public void removeChromatogam(IChromatogramData chromatogram) {

		chromatograms.remove(chromatogram);
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
