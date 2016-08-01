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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.chromulan.system.control.model.data.IDetectorData;
import org.eclipse.chemclipse.converter.core.ISupplier;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.model.core.IChromatogram;

public class ChromatogramCSDMaker implements IChromatogramMaker {

	private IAcquisition acquisition;
	private List<IDetectorData> detectorsData;

	public ChromatogramCSDMaker(IAcquisition acquisition) {
		super();
		this.acquisition = acquisition;
		this.detectorsData = new LinkedList<IDetectorData>();
	}

	public void addDetectorData(IDetectorData detectorData) {

		this.detectorsData.add(detectorData);
	}

	@Override
	public List<IChromatogram> getChromatograms(String path, ISupplier supplier) {

		List<IChromatogram> chromatograms = new ArrayList<>();
		if(path == null || acquisition == null) {
			// TODO: exception or return null ??
			return null;
		}
		String npath = path + File.separator + acquisition.getName();
		File nFile = new File(npath);
		if(!nFile.exists()) {
			if(!nFile.mkdir()) {
				// TODO:excetpiton or return null ??
				return null;
			}
		}
		StringBuilder stringBuilder = new StringBuilder("");
		stringBuilder.append(acquisition.getDescription());
		stringBuilder.append("\r\n");
		for(IDetectorData detectorData : detectorsData) {
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
				File fileSave = new File(nFile + File.separator + acquisition.getName() + "-" + IChromatogramMaker.fileValidation(detectorData.getName()) + supplier.getFileExtension());
				chromatogramCSD.setFile(fileSave);
				chromatograms.add(chromatogramCSD);
			}
		}
		return chromatograms;
	}
}
