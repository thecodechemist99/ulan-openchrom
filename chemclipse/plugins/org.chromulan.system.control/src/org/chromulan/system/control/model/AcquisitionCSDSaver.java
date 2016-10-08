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
import java.util.HashMap;
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
	public List<IChromatogramExportConverterProcessingInfo> save(IProgressMonitor progressMonitor,
			List<IChromatogram> chromatograms) {

		this.chromatogramExportConverterProcessingInfos = new LinkedList<IChromatogramExportConverterProcessingInfo>();
		ISupplier supplier = getSupplier();
		if (chromatograms == null) {
			throw new NullPointerException();
		}
		HashMap<String, Integer> names = new HashMap<>();
		for (IChromatogram chromatogram : chromatograms) {
			if (chromatogram instanceof IChromatogramCSD) {
				IChromatogramCSD chromatogramCSD = (IChromatogramCSD) chromatogram;
				File file = chromatogramCSD.getFile();
				if (file != null) {
					File nfile = setFile(file, names, supplier.getFileExtension());
					IChromatogramExportConverterProcessingInfo procesInfo = ChromatogramConverterCSD.convert(nfile,
							chromatogramCSD, supplier.getId(), progressMonitor);
					chromatogramExportConverterProcessingInfos.add(procesInfo);
				}
			}
		}
		return chromatogramExportConverterProcessingInfos;
	}

	private File setFile(File file, HashMap<String, Integer> names, String fileExtension) {

		String namefile = file.getName();
		String allName = null;
		String name = null;
		fileExtension = fileExtension.toLowerCase();
		if (namefile.length() > fileExtension.length()) {
			String nameSuffix = namefile.substring(namefile.length() - fileExtension.length(), namefile.length())
					.toLowerCase();
			if (!nameSuffix.equals(fileExtension)) {
				allName = namefile + fileExtension;
				name = namefile;
			} else {
				allName = namefile;
				name = namefile.substring(0, namefile.length() - fileExtension.length());
			}
		} else {
			allName = namefile + fileExtension;
			name = namefile;
		}
		String newName;
		if (names.containsKey(allName)) {
			int i = names.get(allName);
			newName = name + "(" + i++ + ")" + fileExtension;
			names.put(allName, i);
		} else {
			newName = allName;
			names.put(allName, 1);
		}
		return new File(file.getParentFile().getAbsolutePath() + File.separator + newName);
	}
}
