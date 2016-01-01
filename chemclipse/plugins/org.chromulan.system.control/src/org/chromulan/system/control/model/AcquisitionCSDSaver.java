/*******************************************************************************
 * Copyright (c) 2015, 2016 Jan Hol�.
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

import org.chromulan.system.control.model.data.IDetectorData;
import org.eclipse.chemclipse.converter.core.ISupplier;
import org.eclipse.chemclipse.converter.processing.chromatogram.IChromatogramExportConverterProcessingInfo;
import org.eclipse.chemclipse.csd.converter.chromatogram.ChromatogramConverterCSD;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.core.runtime.IProgressMonitor;

public class AcquisitionCSDSaver extends AbstractAcquisitionSaver implements IAcquisitionCSDSaver {

	private List<IChromatogramExportConverterProcessingInfo> chromatogramExportConverterProcessingInfos;

	public AcquisitionCSDSaver(IAcquisition acquisition) {
		super(acquisition);
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
		IAcquisition acquisition = getAcquisition();
		if(supplier == null || file == null || acquisition == null) {
			// TODO: exception or return null ??
			return null;
		}
		String path = file.getAbsolutePath() + File.separator + acquisition.getName();
		File nFile = new File(path);
		if(!nFile.exists()) {
			if(!nFile.mkdir()) {
				// TODO:excetpiton or return null ??
				return null;
			}
		}
		StringBuilder stringBuilder = new StringBuilder("");
		stringBuilder.append(acquisition.getDescription());
		stringBuilder.append("\r\n");
		for(IDetectorData detectorData : getDetectorsData()) {
			IChromatogram chromatogram = detectorData.getChromatogram();
			if(chromatogram instanceof IChromatogramCSD) {
				IChromatogramCSD chromatogramCSD = (IChromatogramCSD)chromatogram;
				String shortInfo;
				if(detectorData.getDescription() != null) {
					shortInfo = stringBuilder.toString() + detectorData.getDescription();
				} else {
					shortInfo = stringBuilder.toString();
				}
				chromatogramCSD.setShortInfo(shortInfo);
				chromatogramCSD.setFile(nFile);
				File fileSave = new File(nFile + File.separator + detectorData.getName());
				IChromatogramExportConverterProcessingInfo procesInfo = ChromatogramConverterCSD.convert(fileSave, chromatogramCSD, supplier.getId(), progressMonitor);
				chromatogramExportConverterProcessingInfos.add(procesInfo);
			}
		}
		return chromatogramExportConverterProcessingInfos;
	}
}
