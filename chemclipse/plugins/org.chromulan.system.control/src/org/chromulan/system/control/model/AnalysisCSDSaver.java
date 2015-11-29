/*******************************************************************************
 * Copyright (c) 2015 Jan Holý.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holý - initial API and implementation
 *******************************************************************************/
package org.chromulan.system.control.model;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.chromulan.system.control.model.data.IAnalysisData;
import org.chromulan.system.control.model.data.IChromatogramCSDData;
import org.eclipse.chemclipse.converter.core.ISupplier;
import org.eclipse.chemclipse.converter.processing.chromatogram.IChromatogramExportConverterProcessingInfo;
import org.eclipse.chemclipse.csd.converter.chromatogram.ChromatogramConverterCSD;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.core.runtime.IProgressMonitor;

public class AnalysisCSDSaver implements IAnalysisCSDSaver {

	private IAnalysis analysis;
	private List<IAnalysisData> analysisDataList;
	private File file;
	private List<IChromatogramExportConverterProcessingInfo> chromatogramExportConverterProcessingInfos;
	private List<IChromatogramCSDData> chromatograms;
	private ISupplier supplier;

	public AnalysisCSDSaver(IAnalysis analysis) {

		chromatograms = new LinkedList<IChromatogramCSDData>();
		analysisDataList = new LinkedList<IAnalysisData>();
		chromatogramExportConverterProcessingInfos = new LinkedList<IChromatogramExportConverterProcessingInfo>();
		this.analysis = analysis;
	}

	@Override
	public void addDescription(IAnalysisData description) {

		analysisDataList.add(description);
	}

	@Override
	public void addChromatogam(IChromatogramCSDData chromatogram) {

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
	public List<IChromatogramExportConverterProcessingInfo> getChromatogramExportConverterProcessInfo() {

		return chromatogramExportConverterProcessingInfos;
	}

	@Override
	public List<IChromatogramCSDData> getChromatograms() {

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
	public void removeChromatogam(IChromatogramCSDData chromatogram) {

		chromatograms.remove(chromatogram);
	}

	@Override
	public List<IChromatogramExportConverterProcessingInfo> save(IProgressMonitor progressMonitor) {

		chromatogramExportConverterProcessingInfos.clear();
		if(supplier == null || file == null || analysis == null) {
			// TODO: exception or return null ??
			return null;
		}
		String path = file.getAbsolutePath() + File.separator + analysis.getName();
		File nFile = new File(path);
		if(!nFile.exists()) {
			if(!nFile.mkdir()) {
				// TODO:excetpiton or return null ??
				return null;
			}
		}
		StringBuilder stringBuilder = new StringBuilder("");
		stringBuilder.append(analysis.getDescription());
		stringBuilder.append("\r\n");
		for(IAnalysisData iChromatogramDescription : analysisDataList) {
			if(iChromatogramDescription.getDescription() != null) {
				stringBuilder.append(iChromatogramDescription.getDescription());
				stringBuilder.append("\r\n");
			}
		}
		for(IChromatogramCSDData chromatogramCSDData : chromatograms) {
			IChromatogramCSD chromatogramCSD = chromatogramCSDData.getChromatogramCSD();
			if(chromatogramCSD != null) {
				String shortInfo;
				if(chromatogramCSDData.getDescription() != null) {
					shortInfo = stringBuilder.toString() + chromatogramCSDData.getDescription();
				} else {
					shortInfo = stringBuilder.toString();
				}
				chromatogramCSD.setShortInfo(shortInfo);
				chromatogramCSD.setFile(nFile);
				File fileSave = new File(nFile + File.separator + chromatogramCSDData.getName());
				IChromatogramExportConverterProcessingInfo procesInfo = ChromatogramConverterCSD.convert(fileSave, chromatogramCSD, supplier.getId(), progressMonitor);
				chromatogramExportConverterProcessingInfos.add(procesInfo);
			}
		}
		return chromatogramExportConverterProcessingInfos;
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
