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

import org.chromulan.system.control.model.chromatogram.IChromatogramDescription;
import org.chromulan.system.control.model.chromatogram.IChromatogramRecordingCSD;
import org.eclipse.chemclipse.converter.core.ISupplier;
import org.eclipse.chemclipse.converter.processing.IExportConverterProcessingInfo;
import org.eclipse.chemclipse.converter.processing.chromatogram.IChromatogramExportConverterProcessingInfo;
import org.eclipse.chemclipse.csd.converter.chromatogram.ChromatogramConverterCSD;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.core.runtime.IProgressMonitor;

public class AnalysisCSDSaver implements IAnalysisCSDSaver {

	private List<IExportConverterProcessingInfo> converterProcessingInfos;
	private List<IChromatogramDescription> descriptions;
	private File file;
	private List<IChromatogramExportConverterProcessingInfo> chromatogramExportConverterProcessingInfos;
	private List<IChromatogramRecordingCSD> chromatograms;
	private String name;
	private ISupplier supplier;

	public AnalysisCSDSaver() {

		chromatograms = new LinkedList<IChromatogramRecordingCSD>();
		descriptions = new LinkedList<IChromatogramDescription>();
		converterProcessingInfos = new LinkedList<IExportConverterProcessingInfo>();
		chromatogramExportConverterProcessingInfos = new LinkedList<IChromatogramExportConverterProcessingInfo>();
	}

	@Override
	public void addDescription(IChromatogramDescription description) {

		descriptions.add(description);
	}

	@Override
	public void addChromatogam(IChromatogramRecordingCSD chromatogram) {

		chromatograms.add(chromatogram);
	}

	@Override
	public List<IChromatogramDescription> getDescriptions() {

		return descriptions;
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
	public List<IChromatogramRecordingCSD> getChromatograms() {

		return chromatograms;
	}

	@Override
	public String getName() {

		return name;
	}

	@Override
	public List<IExportConverterProcessingInfo> getProcessInfo() {

		return converterProcessingInfos;
	}

	@Override
	public ISupplier getSupplier() {

		return supplier;
	}

	@Override
	public void removeAllDescriptions() {

		descriptions.clear();
	}

	@Override
	public void removeAllChromatograms() {

		chromatograms.clear();
	}

	@Override
	public void removeDescription(IChromatogramDescription description) {

		descriptions.remove(description);
	}

	@Override
	public void removeChromatogam(IChromatogramRecordingCSD chromatogram) {

		chromatograms.remove(chromatogram);
	}

	@Override
	public List<IExportConverterProcessingInfo> save(IProgressMonitor progressMonitor) {

		converterProcessingInfos.clear();
		chromatogramExportConverterProcessingInfos.clear();
		if(supplier == null || file == null || name == null) {
			// TODO: exception or return null ??
			return null;
		}
		String path = file.getAbsolutePath() + File.separator + name;
		File nFile = new File(path);
		if(!nFile.exists()) {
			if(!nFile.mkdir()) {
				// TODO:excetpiton or return null ??
				return null;
			}
		}
		StringBuilder stringBuilder = new StringBuilder("");
		for(IChromatogramDescription iChromatogramDescription : descriptions) {
			if(iChromatogramDescription.getDescription() != null) {
				stringBuilder.append(iChromatogramDescription.getDescription());
				stringBuilder.append("\r\n");
			}
		}
		for(IChromatogramRecordingCSD chromatogramRecordingCSD : chromatograms) {
			IChromatogramCSD chromatogramCSD = chromatogramRecordingCSD.getChromatogramCSD();
			if(chromatogramCSD != null) {
				String shortInfo;
				if(chromatogramRecordingCSD.getDescription() != null) {
					shortInfo = stringBuilder.toString() + chromatogramRecordingCSD.getDescription();
				} else {
					shortInfo = stringBuilder.toString();
				}
				chromatogramCSD.setShortInfo(shortInfo);
				chromatogramCSD.setFile(nFile);
				File fileSave = new File(nFile + File.separator + getName() + "_" + chromatogramRecordingCSD.getName());
				IChromatogramExportConverterProcessingInfo procesInfo = ChromatogramConverterCSD.convert(fileSave, chromatogramCSD, supplier.getId(), progressMonitor);
				converterProcessingInfos.add(procesInfo);
				chromatogramExportConverterProcessingInfos.add(procesInfo);
			}
		}
		return converterProcessingInfos;
	}

	@Override
	public void setFile(File file) {

		this.file = file;
	}

	@Override
	public void setName(String name) {

		this.name = name;
	}

	@Override
	public void setSuplier(ISupplier suplier) {

		this.supplier = suplier;
	}
}
