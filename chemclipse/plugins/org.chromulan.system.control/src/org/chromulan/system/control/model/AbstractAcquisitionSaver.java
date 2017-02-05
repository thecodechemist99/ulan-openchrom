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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.chemclipse.converter.core.ISupplier;
import org.eclipse.chemclipse.converter.processing.chromatogram.IChromatogramExportConverterProcessingInfo;
import org.eclipse.chemclipse.model.core.IChromatogram;

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
	private List<IChromatogramExportConverterProcessingInfo> chromatogramExportConverterProcessingInfos;
	private HashMap<String, Integer> names;
	private ISupplier supplier;

	public AbstractAcquisitionSaver(IAcquisition acquisition) {
		this.chromatogramExportConverterProcessingInfos = new LinkedList<IChromatogramExportConverterProcessingInfo>();
		this.names = new HashMap<>();
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

	protected IChromatogram setChromatogramParameters(SaveChromatogram saveChromatogram, IAcquisition acquisition) {

		IChromatogram chromatogram = saveChromatogram.getChromatogram();
		Iterator<Entry<String, String>> deviceProperties = saveChromatogram.getDeviceProperties().entrySet().iterator();
		String deviceName = saveChromatogram.getNameDevice();
		chromatogram.setShortInfo(acquisition.getDescription());
		Map<String, String> miscellaneous = chromatogram.getMiscellaneous();
		String duration = Long.toString(acquisition.getDuration() / (1000 * 60)) + " min";
		String analysis = acquisition.getAnalysis();
		String amount = Float.toString(acquisition.getAmount());
		String istdAmount = Float.toString(acquisition.getISTDAmount());
		String InjVolume = Float.toString(acquisition.getInjectionVolume());
		String column = acquisition.getColumn();
		String mobilPhase = acquisition.getMobilPhase();
		String flowrate = Float.toString(acquisition.getFlowRate()) + " " + acquisition.getFlowRateUnit();
		String detection = acquisition.getDetection();
		String temperature = Float.toString(acquisition.getTemperature()) + " " + acquisition.getTemperatureUnit();
		miscellaneous.put("Duration", duration);
		miscellaneous.put("Analysis", analysis);
		miscellaneous.put("Amount", amount);
		miscellaneous.put("ISTD Amount", istdAmount);
		miscellaneous.put("Inj. Volume", InjVolume);
		miscellaneous.put("Column", column);
		miscellaneous.put("Mobil Phase", mobilPhase);
		miscellaneous.put("Flow rate", flowrate);
		miscellaneous.put("Detection", detection);
		miscellaneous.put("Temperature", temperature);
		miscellaneous.put("Device", deviceName);
		while(deviceProperties.hasNext()) {
			Entry<String, String> entry = deviceProperties.next();
			String propertyName = entry.getKey();
			String propertyValue = entry.getValue();
			miscellaneous.put(propertyName, propertyValue);
		}
		return chromatogram;
	}

	@Override
	public void setSupplier(ISupplier suplier) {

		this.supplier = suplier;
	}
}
