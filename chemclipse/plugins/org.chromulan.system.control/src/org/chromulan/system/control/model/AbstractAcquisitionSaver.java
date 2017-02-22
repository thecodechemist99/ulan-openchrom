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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.chemclipse.converter.core.ISupplier;
import org.eclipse.chemclipse.converter.processing.chromatogram.IChromatogramExportConverterProcessingInfo;

public abstract class AbstractAcquisitionSaver implements IAcquisitionSaver {

	protected static String adjustNameFile(String name) {

		if(name == null) {
			return "CHROMATOGRAM";
		}
		String newName = name.trim();
		if(newName.isEmpty()) {
			return "CHROMATOGRAM";
		}
		return newName.replaceAll("[\\*/\\\\!\\|:?<>]", "_").replaceAll("(%22)", "_");
	}

	private IAcquisition acquisition;
	private File file;
	final private List<IChromatogramExportConverterProcessingInfo> chromatogramExportConverterProcessingInfos = new LinkedList<IChromatogramExportConverterProcessingInfo>();
	private HashMap<String, Integer> names = new HashMap<>();
	private ISupplier supplier;

	public AbstractAcquisitionSaver(IAcquisition acquisition) {
		this.acquisition = acquisition;
	}

	@Override
	public IAcquisition getAcquisition() {

		return acquisition;
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
	public ISupplier getSupplier() {

		return supplier;
	}

	protected void namesRemove() {

		names.clear();
	}

	@Override
	public void setAcquisition(IAcquisition acquisition) {

		this.acquisition = acquisition;
	}

	@Override
	public void setFile(File file) {

		this.file = file;
	}

	protected File setFile(String namefile, String fileExtension) {

		String allName = null;
		String name = null;
		fileExtension = fileExtension.toLowerCase();
		namefile = adjustNameFile(namefile);
		if(namefile.length() > fileExtension.length()) {
			String nameSuffix = namefile.substring(namefile.length() - fileExtension.length(), namefile.length()).toLowerCase();
			if(!nameSuffix.equals(fileExtension)) {
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
		if(names.containsKey(allName)) {
			int i = names.get(allName);
			newName = name + "(" + i++ + ")" + fileExtension;
			names.put(allName, i);
		} else {
			newName = allName;
			names.put(allName, 1);
		}
		return new File(file + File.separator + adjustNameFile(acquisition.getName()) + File.separator + newName);
	}

	@Override
	public void setSupplier(ISupplier suplier) {

		this.supplier = suplier;
	}
}
