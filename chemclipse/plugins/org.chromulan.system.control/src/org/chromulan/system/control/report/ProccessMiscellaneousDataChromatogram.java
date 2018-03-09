/*******************************************************************************
 * Copyright (c) 2017 Jan Holy.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.chromulan.system.control.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.chromulan.system.control.model.IAcquisition;
import org.chromulan.system.control.model.SaveChromatogram;
import org.eclipse.chemclipse.model.core.IChromatogram;

public class ProccessMiscellaneousDataChromatogram {

	static private final String ANALYSIS = "Analysis";
	static private final String ANOUNT = "Amount";
	static private final String COLUMN = "Column";
	static private final String DETECTION = "Detection";
	static private final String DEVICE = "device";
	static private final String DURATION = "Duration";
	static private final String FLOW_RATE = "FlowRate";
	static private final String INJ_Volume = "Inj. Volume";
	static private final String ISTD_ANOUNT = "ISTD Amount";
	static private final String MOBIL_PHASE = "Mobil phase";
	static private final String TEMPERATURE = "Temperature";

	static private void addDevice(IChromatogram chromatogram, String[] namesDevices) {

		for(int i = 0; i < namesDevices.length; i++) {
			chromatogram.putHeaderData(DEVICE + i, namesDevices[i]);
		}
	}

	static private void addDeviceSettings(IChromatogram chromatogram, String namedevice, HashMap<String, String> settings) {

		Iterator<Entry<String, String>> iterator = settings.entrySet().iterator();
		while(iterator.hasNext()) {
			Entry<String, String> entry = iterator.next();
			chromatogram.putHeaderData(namedevice + ":" + entry.getKey(), entry.getValue());
		}
	}

	private static void addMiscellaneousAcqusitionDataToStringBuilder(StringBuilder builder, IChromatogram chromatogram, String key, String name) {

		Object value = chromatogram.getHeaderData(key);
		if(value != null) {
			builder.append(name);
			builder.append(":");
			builder.append(value);
			builder.append(System.lineSeparator());
		}
	}

	static private void floatPutToMiscellaneous(IChromatogram chromatogram, String name, Float value, String unit) {

		if(value == null) {
			return;
		}
		chromatogram.putHeaderData(name, Float.toString(value) + unit);
	}

	public static String getMiscellaneousData(IChromatogram chromatogram) {

		StringBuilder builder = new StringBuilder();
		addMiscellaneousAcqusitionDataToStringBuilder(builder, chromatogram, DURATION, DURATION);
		addMiscellaneousAcqusitionDataToStringBuilder(builder, chromatogram, ANALYSIS, ANALYSIS);
		addMiscellaneousAcqusitionDataToStringBuilder(builder, chromatogram, ANOUNT, ANOUNT);
		addMiscellaneousAcqusitionDataToStringBuilder(builder, chromatogram, ISTD_ANOUNT, ISTD_ANOUNT);
		addMiscellaneousAcqusitionDataToStringBuilder(builder, chromatogram, INJ_Volume, INJ_Volume);
		addMiscellaneousAcqusitionDataToStringBuilder(builder, chromatogram, COLUMN, COLUMN);
		addMiscellaneousAcqusitionDataToStringBuilder(builder, chromatogram, MOBIL_PHASE, MOBIL_PHASE);
		addMiscellaneousAcqusitionDataToStringBuilder(builder, chromatogram, FLOW_RATE, FLOW_RATE);
		addMiscellaneousAcqusitionDataToStringBuilder(builder, chromatogram, DETECTION, DETECTION);
		addMiscellaneousAcqusitionDataToStringBuilder(builder, chromatogram, TEMPERATURE, TEMPERATURE);
		// TODO:List<String> devicesNames= getNameofDeviceFormMiscellaneous(miscellaneous);
		return builder.toString();
	}

	static private void longPutToMiscellaneous(IChromatogram chromatogram, String name, Long value, String unit) {

		if(value == null) {
			return;
		}
		chromatogram.putHeaderData(name, Long.toString(value) + unit);
	}

	public static void setChromatogramParameters(SaveChromatogram saveChromatogram, IAcquisition acquisition) {

		IChromatogram chromatogram = saveChromatogram.getChromatogram();
		String[] devicesNames = saveChromatogram.getNamesDevices();
		chromatogram.setShortInfo(acquisition.getDescription());
		longPutToMiscellaneous(chromatogram, DURATION, acquisition.getDuration() / (1000 * 60), " min");
		stringPutToMiscellaneous(chromatogram, ANALYSIS, acquisition.getAnalysis(), "");
		floatPutToMiscellaneous(chromatogram, ANOUNT, acquisition.getAmount(), "");
		floatPutToMiscellaneous(chromatogram, ISTD_ANOUNT, acquisition.getISTDAmount(), "");
		floatPutToMiscellaneous(chromatogram, INJ_Volume, acquisition.getInjectionVolume(), "");
		stringPutToMiscellaneous(chromatogram, COLUMN, acquisition.getColumn(), "");
		stringPutToMiscellaneous(chromatogram, MOBIL_PHASE, acquisition.getMobilPhase(), "");
		floatPutToMiscellaneous(chromatogram, FLOW_RATE, acquisition.getFlowRate(), acquisition.getFlowRateUnit());
		stringPutToMiscellaneous(chromatogram, DETECTION, acquisition.getDetection(), "");
		floatPutToMiscellaneous(chromatogram, TEMPERATURE, acquisition.getTemperature(), acquisition.getTemperatureUnit());
		addDevice(chromatogram, devicesNames);
		for(int i = 0; i < devicesNames.length; i++) {
			HashMap<String, String> settings = saveChromatogram.getDeviceProperties(devicesNames[i]);
			if(settings != null && devicesNames[i] != null) {
				addDeviceSettings(chromatogram, devicesNames[i], settings);
			}
		}
	}

	public static void setMiscellaneousData(List<SaveChromatogram> saveChromatograms, IAcquisition acquisition) {

		for(SaveChromatogram saveChromatogram : saveChromatograms) {
			setChromatogramParameters(saveChromatogram, acquisition);
		}
	}

	public static void setMiscellaneousData(SaveChromatogram saveChomapotogram, IAcquisition acquisition) {

		List<SaveChromatogram> saveChromatograms = new ArrayList<>();
		saveChromatograms.add(saveChomapotogram);
	}

	static private void stringPutToMiscellaneous(IChromatogram chromatogram, String name, String value, String unit) {

		if(value == null) {
			return;
		}
		chromatogram.putHeaderData(name, value + unit);
	}
}
