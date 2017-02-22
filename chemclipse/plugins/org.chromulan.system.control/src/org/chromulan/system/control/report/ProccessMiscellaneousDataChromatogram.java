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
import java.util.Map;
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

	static private void addDevice(Map<String, String> miscellaneous, String[] namesDevices) {

		for(int i = 0; i < namesDevices.length; i++) {
			miscellaneous.put(DEVICE + i, namesDevices[i]);
		}
	}

	static private void addDeviceSettings(Map<String, String> miscellaneous, String namedevice, HashMap<String, String> settings) {

		Iterator<Entry<String, String>> iterator = settings.entrySet().iterator();
		while(iterator.hasNext()) {
			Entry<String, String> entry = iterator.next();
			miscellaneous.put(namedevice + ":" + entry.getKey(), entry.getValue());
		}
	}

	private static void addMiscellaneousAcqusitionDataToStringBuilder(StringBuilder builder, Map<String, String> miscellaneous, String key, String name) {

		String value = miscellaneous.get(key);
		if(value != null) {
			builder.append(name);
			builder.append(":");
			builder.append(value);
			builder.append(System.lineSeparator());
		}
	}

	static private void floatPutToMiscellaneous(Map<String, String> miscellaneous, String name, Float value, String unit) {

		if(value == null) {
			return;
		}
		miscellaneous.put(name, Float.toString(value) + unit);
	}

	public static String getMiscellaneousData(IChromatogram chromatogram) {

		StringBuilder builder = new StringBuilder();
		Map<String, String> miscellaneous = chromatogram.getMiscellaneous();
		addMiscellaneousAcqusitionDataToStringBuilder(builder, miscellaneous, DURATION, DURATION);
		addMiscellaneousAcqusitionDataToStringBuilder(builder, miscellaneous, ANALYSIS, ANALYSIS);
		addMiscellaneousAcqusitionDataToStringBuilder(builder, miscellaneous, ANOUNT, ANOUNT);
		addMiscellaneousAcqusitionDataToStringBuilder(builder, miscellaneous, ISTD_ANOUNT, ISTD_ANOUNT);
		addMiscellaneousAcqusitionDataToStringBuilder(builder, miscellaneous, INJ_Volume, INJ_Volume);
		addMiscellaneousAcqusitionDataToStringBuilder(builder, miscellaneous, COLUMN, COLUMN);
		addMiscellaneousAcqusitionDataToStringBuilder(builder, miscellaneous, MOBIL_PHASE, MOBIL_PHASE);
		addMiscellaneousAcqusitionDataToStringBuilder(builder, miscellaneous, FLOW_RATE, FLOW_RATE);
		addMiscellaneousAcqusitionDataToStringBuilder(builder, miscellaneous, DETECTION, DETECTION);
		addMiscellaneousAcqusitionDataToStringBuilder(builder, miscellaneous, TEMPERATURE, TEMPERATURE);
		// TODO:List<String> devicesNames= getNameofDeviceFormMiscellaneous(miscellaneous);
		return builder.toString();
	}

	static private void longPutToMiscellaneous(Map<String, String> miscellaneous, String name, Long value, String unit) {

		if(value == null) {
			return;
		}
		miscellaneous.put(name, Long.toString(value) + unit);
	}

	public static void setChromatogramParameters(SaveChromatogram saveChromatogram, IAcquisition acquisition) {

		IChromatogram chromatogram = saveChromatogram.getChromatogram();
		String[] devicesNames = saveChromatogram.getNamesDevices();
		chromatogram.setShortInfo(acquisition.getDescription());
		Map<String, String> miscellaneous = chromatogram.getMiscellaneous();
		longPutToMiscellaneous(miscellaneous, DURATION, acquisition.getDuration() / (1000 * 60), " min");
		stringPutToMiscellaneous(miscellaneous, ANALYSIS, acquisition.getAnalysis(), "");
		floatPutToMiscellaneous(miscellaneous, ANOUNT, acquisition.getAmount(), "");
		floatPutToMiscellaneous(miscellaneous, ISTD_ANOUNT, acquisition.getISTDAmount(), "");
		floatPutToMiscellaneous(miscellaneous, INJ_Volume, acquisition.getInjectionVolume(), "");
		stringPutToMiscellaneous(miscellaneous, COLUMN, acquisition.getColumn(), "");
		stringPutToMiscellaneous(miscellaneous, MOBIL_PHASE, acquisition.getMobilPhase(), "");
		floatPutToMiscellaneous(miscellaneous, FLOW_RATE, acquisition.getFlowRate(), acquisition.getFlowRateUnit());
		stringPutToMiscellaneous(miscellaneous, DETECTION, acquisition.getDetection(), "");
		floatPutToMiscellaneous(miscellaneous, TEMPERATURE, acquisition.getTemperature(), acquisition.getTemperatureUnit());
		addDevice(miscellaneous, devicesNames);
		for(int i = 0; i < devicesNames.length; i++) {
			HashMap<String, String> settings = saveChromatogram.getDeviceProperties(devicesNames[i]);
			if(settings != null && devicesNames[i] != null) {
				addDeviceSettings(miscellaneous, devicesNames[i], settings);
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

	static private void stringPutToMiscellaneous(Map<String, String> miscellaneous, String name, String value, String unit) {

		if(value == null) {
			return;
		}
		miscellaneous.put(name, value + unit);
	}
}
