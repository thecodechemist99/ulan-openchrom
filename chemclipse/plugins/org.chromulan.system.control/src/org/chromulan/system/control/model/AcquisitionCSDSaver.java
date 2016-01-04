/*******************************************************************************
 * Copyright (c) 2015, 2016 Jan Hol�.
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

import org.eclipse.chemclipse.converter.core.ISupplier;
import org.eclipse.chemclipse.converter.processing.chromatogram.IChromatogramExportConverterProcessingInfo;
import org.eclipse.chemclipse.csd.converter.chromatogram.ChromatogramConverterCSD;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.core.runtime.IProgressMonitor;

public class AcquisitionCSDSaver extends AbstractAcquisitionSaver implements IAcquisitionCSDSaver {

	private List<IChromatogramExportConverterProcessingInfo> chromatogramExportConverterProcessingInfos;

	public AcquisitionCSDSaver() {
		chromatogramExportConverterProcessingInfos = new LinkedList<IChromatogramExportConverterProcessingInfo>();
	}

	@Override
	public List<IChromatogramExportConverterProcessingInfo> getChromatogramExportConverterProcessInfo() {

		return chromatogramExportConverterProcessingInfos;
	}

	@Override
	public List<IChromatogramExportConverterProcessingInfo> save(IProgressMonitor progressMonitor) {

		this.chromatogramExportConverterProcessingInfos = new LinkedList<IChromatogramExportConverterProcessingInfo>();
		ISupplier supplier = getSupplier();
		IChromatogramMaker chromatogramMaker = getChromatogramMaker();
		if(chromatogramMaker == null) {
			throw new NullPointerException();
		}
		for(IChromatogram chromatogram : getChromatogramMaker().getChromatograms()) {
			if(chromatogram instanceof IChromatogramCSD) {
				IChromatogramCSD chromatogramCSD = (IChromatogramCSD)chromatogram;
				File file = chromatogramCSD.getFile();
				if(file !=null)
				{
					IChromatogramExportConverterProcessingInfo procesInfo = ChromatogramConverterCSD.convert(file, chromatogramCSD, supplier.getId(), progressMonitor);
					chromatogramExportConverterProcessingInfos.add(procesInfo);
				}
			}
		}
		return chromatogramExportConverterProcessingInfos;
	}
}
