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
import java.util.List;

import org.chromulan.system.control.model.data.IAnalysisData;
import org.chromulan.system.control.model.data.IChromatogramData;
import org.eclipse.chemclipse.converter.core.ISupplier;
import org.eclipse.chemclipse.converter.processing.chromatogram.IChromatogramExportConverterProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IAnalysisSaver {

	void addDescription(IAnalysisData analysisData);

	void addChromatogam(IChromatogramData chromatogram);

	IAnalysis getAnalysis();

	List<IAnalysisData> getAnalysisDataAll();

	File getFile();

	List<IChromatogramExportConverterProcessingInfo> getChromatogramExportConverterProcessInfo();

	List<IChromatogramData> getChromatograms();

	ISupplier getSupplier();

	void removeAllAnalysisData();

	void removeAllChromatograms();

	void removeAnalysisData(IAnalysisData analysisData);

	void removeChromatogam(IChromatogramData chromatogram);

	List<IChromatogramExportConverterProcessingInfo> save(IProgressMonitor progressMonitor);

	void setFile(File file);

	void setSuplier(ISupplier suplier);
}
