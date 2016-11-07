/*******************************************************************************
 * Copyright (c) 2016 Jan Holy.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.chromulan.system.control.hitachi.l3000.model;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.chromulan.system.control.model.ChromatogramWSDAcquisition;
import org.chromulan.system.control.model.IChromatogramWSDAcquisition;
import org.eclipse.chemclipse.wsd.model.core.AbstractScanSignalWSD;
import org.eclipse.chemclipse.wsd.model.core.AbstractScanWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanSignalWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;

public class DataReceive {

	private ControlDevice controlDevice;
	private final ExecutorService executor = Executors.newSingleThreadExecutor();
	private IChromatogramWSDAcquisition chromatogram;
	volatile private boolean reset;
	private Boolean saveData;
	private int timeInterval;
	private float wavelenghtInterval;
	private int wavelenghtRangeFrom;

	public DataReceive(ControlDevice controlDevice) {
		this.chromatogram = new ChromatogramWSDAcquisition(controlDevice.getTimeIntervalMill(), 0);
		;
		this.controlDevice = controlDevice;
		this.wavelenghtInterval = controlDevice.getWavelenghtInterval();
		this.wavelenghtRangeFrom = controlDevice.getWavelenghtRangeFrom();
		this.timeInterval = controlDevice.getTimeIntervalMill();
		this.saveData = true;
		this.reset = false;
	}

	private void addScan(IScanWSD actualScan) {

		synchronized(saveData) {
			if(saveData && !reset) {
				chromatogram.addScanAutoSet(actualScan);
			}
		}
	}

	public void addScan(final String message) {

		executor.execute(new Runnable() {

			@Override
			public void run() {

				parseMessagge(message);
			}
		});
	}

	private void addScanSignal(float abundance, IScanWSD scanWSD, int actualScanOrder) {

		IScanSignalWSD scanSignalWSD = new AbstractScanSignalWSD() {
		};
		scanSignalWSD.setAbundance(abundance);
		scanSignalWSD.setWavelength(wavelenghtRangeFrom + (int)(wavelenghtInterval * actualScanOrder));
		actualScanOrder++;
		scanWSD.addScanSignal(scanSignalWSD);
	}

	@Override
	protected void finalize() throws Throwable {

		try {
			executor.shutdownNow();
		} finally {
			super.finalize();
		}
	}

	public IChromatogramWSDAcquisition getChromatogram() {

		return chromatogram;
	}

	public boolean isSaveData() {

		return saveData;
	}

	private void parseMessagge(String messagge) {

		String[] data = messagge.trim().split("\\s+");
		IScanWSD actualScan = new AbstractScanWSD() {

			/**
			 *
			 */
			private static final long serialVersionUID = 1088506877109062789L;
		};
		try {
			for(int i = 2; i < data.length; i++) {
				float value = Float.valueOf(data[i]);
				addScanSignal(value, actualScan, i - 2);
			}
			addScan(actualScan);
		} catch(NumberFormatException e) {
		}
	}

	public void reset(boolean saveData) {

		synchronized(this.saveData) {
			this.reset = true;
			this.saveData = saveData;
			this.wavelenghtInterval = controlDevice.getWavelenghtInterval();
			this.wavelenghtRangeFrom = controlDevice.getWavelenghtRangeFrom();
			this.timeInterval = controlDevice.getTimeIntervalMill();
			chromatogram.newAcquisition(timeInterval, 0);
		}
	}

	public void setSaveData(boolean saveData) {

		synchronized(this.saveData) {
			this.saveData = saveData;
		}
	}
}
