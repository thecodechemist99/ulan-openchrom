/*******************************************************************************
 * Copyright (c) 2015 Jan Hol�.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Hol� - initial API and implementation
 *******************************************************************************/
package org.chromulan.system.control.model;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.chromulan.system.control.model.data.IAnalysisData;
import org.chromulan.system.control.model.data.IChromatogramCSDData;
import org.chromulan.system.control.model.data.IChromatogramData;
import org.eclipse.chemclipse.converter.core.ISupplier;
import org.eclipse.chemclipse.converter.processing.chromatogram.IChromatogramExportConverterProcessingInfo;
import org.eclipse.chemclipse.csd.converter.chromatogram.ChromatogramConverterCSD;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.core.runtime.IProgressMonitor;

public class AnalysisCSDSaver extends AbstractAnalysisSaver implements IAnalysisCSDSaver {

	private List<IChromatogramExportConverterProcessingInfo> chromatogramExportConverterProcessingInfos;

	public AnalysisCSDSaver(IAnalysis analysis) {
		super(analysis);
		chromatogramExportConverterProcessingInfos = new LinkedList<IChromatogramExportConverterProcessingInfo>();
	}

	@Override
	public List<IChromatogramExportConverterProcessingInfo> getChromatogramExportConverterProcessInfo() {

		return chromatogramExportConverterProcessingInfos;
	}

	@Override
	public List<IChromatogramExportConverterProcessingInfo> save(IProgressMonitor progressMonitor) {

		chromatogramExportConverterProcessingInfos.clear();
		ISupplier supplier = getSupplier();
		File file = getFile();
		IAnalysis analysis = getAnalysis();
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
		for(IAnalysisData iChromatogramDescription : getAnalysisDataAll()) {
			if(iChromatogramDescription.getDescription() != null) {
				stringBuilder.append(iChromatogramDescription.getDescription());
				stringBuilder.append("\r\n");
			}
		}
		for(IChromatogramData chromatogramData : getChromatograms()) {
			if(chromatogramData instanceof IChromatogramCSDData) {
				IChromatogramCSDData chromatogramCSDData = (IChromatogramCSDData)chromatogramData;
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
		}
		return chromatogramExportConverterProcessingInfos;
	}
}
