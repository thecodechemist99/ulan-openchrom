/*******************************************************************************
 * Copyright (c) 2015, 2016 Jan Holy.
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

public abstract class AbstractAcquisitionSaver implements IAcquisitionSaver {

	private File file;
	private ISupplier supplier;
	private List<IChromatogramExportConverterProcessingInfo> chromatogramExportConverterProcessingInfos;
	private HashMap<String,Integer> names;
	private IAcquisition acquisition;
	
	public AbstractAcquisitionSaver(IAcquisition acquisition) {
		this.chromatogramExportConverterProcessingInfos = new LinkedList<IChromatogramExportConverterProcessingInfo>();
		this.names = new HashMap<>();
		this.acquisition = acquisition;
	}

	@Override
	public List<IChromatogramExportConverterProcessingInfo> getChromatogramExportConverterProcessInfo() {

		return chromatogramExportConverterProcessingInfos;
	}

	@Override
	public File getFile() {

		return file;
	}

	@Override
	public ISupplier getSupplier() {

		return supplier;
	}

	@Override
	public void setFile(File file) {

		this.file = file;
	}

	@Override
	public void setSupplier(ISupplier suplier) {

		this.supplier = suplier;
	}
	
	
	protected HashMap<String,Integer> getNames(){
		return names;
	}
	
	
	protected File setFile(File file, String fileExtension) {

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
	
	
	@Override
	public IAcquisition getAcquisition() {
		
		return acquisition;
	}
	
	@Override
	public void setAcquisition(IAcquisition acquisition) {
		this.acquisition = acquisition;
		
	}
}
