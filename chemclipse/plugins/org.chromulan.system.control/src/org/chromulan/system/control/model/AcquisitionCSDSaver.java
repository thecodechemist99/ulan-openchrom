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
package org.chromulan.system.control.model;

import java.io.File;
import java.util.List;

import org.chromulan.system.control.report.ProccessMiscellaneousDataChromatogram;
import org.eclipse.chemclipse.converter.core.ISupplier;
import org.eclipse.chemclipse.converter.processing.chromatogram.IChromatogramExportConverterProcessingInfo;
import org.eclipse.chemclipse.csd.converter.chromatogram.ChromatogramConverterCSD;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.core.runtime.IProgressMonitor;

public class AcquisitionCSDSaver extends AbstractAcquisitionSaver implements IAcquisitionCSDSaver {

	public AcquisitionCSDSaver(IAcquisitionCSD acquisitionCSD) {
		super(acquisitionCSD);
	}

	@Override
	public List<IChromatogramExportConverterProcessingInfo> save(IProgressMonitor progressMonitor, List<SaveChromatogram> chromatograms) {

		List<IChromatogramExportConverterProcessingInfo> chromatogramExportConverterProcessingInfos = getChromatogramExportConverterProcessInfo();
		File file = getFile();
		ISupplier supplier = getSupplier();
		IAcquisition acquisition = getAcquisition();
		if(acquisition == null || chromatograms == null || file == null || supplier == null) {
			throw new NullPointerException();
		}
		namesRemove();
		chromatogramExportConverterProcessingInfos.clear();
		for(SaveChromatogram saveChromatogram : chromatograms) {
			ProccessMiscellaneousDataChromatogram.setChromatogramParameters(saveChromatogram, acquisition);
			IChromatogram chromatogram = saveChromatogram.getChromatogram();
			if(chromatogram instanceof IChromatogramCSD) {
				IChromatogramCSD chromatogramCSD = (IChromatogramCSD)chromatogram;
				File nfile = setFile(saveChromatogram.getName(), supplier.getFileExtension());
				IChromatogramExportConverterProcessingInfo procesInfo = ChromatogramConverterCSD.convert(nfile, chromatogramCSD, supplier.getId(), progressMonitor);
				chromatogramExportConverterProcessingInfos.add(procesInfo);
			}
		}
		return chromatogramExportConverterProcessingInfos;
	}
}
