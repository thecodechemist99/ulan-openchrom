/*******************************************************************************
 * Copyright (c) 2016, 2017 Jan Holy.
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

	private final ExecutorService executor = Executors.newSingleThreadExecutor();
	private IChromatogramWSDAcquisition chromatogram;
	volatile private boolean reset;
	volatile private Boolean saveData;
	private int timeInterval;
	private float wavelenghtInterval;
	private int wavelenghtRangeFrom;

	public DataReceive(String name, int timeInterval, float wavelenghtInterval, int wavelenghtRangeFrom, int wavelenghtRangeTo) {
		this.chromatogram = new ChromatogramWSDAcquisition(timeInterval, 0);
		this.chromatogram.setName(name);
		this.wavelenghtInterval = wavelenghtInterval;
		this.wavelenghtRangeFrom = wavelenghtRangeFrom;
		this.timeInterval = timeInterval;
		this.saveData = true;
		this.reset = false;
	}

	private void addScan(IScanWSD actualScan) {

		chromatogram.addScanAutoSet(actualScan);
	}

	public void addScan(final String message) {

		if(!saveData) {
			return;
		}
		final boolean reset = this.reset;
		executor.execute(new Runnable() {

			@Override
			public void run() {

				parseMessagge(message, reset);
			}
		});
		this.reset = false;
	}

	private void addScanSignal(float abundance, IScanWSD scanWSD, int actualScanOrder) {

		IScanSignalWSD scanSignalWSD = new AbstractScanSignalWSD() {
		};
		scanSignalWSD.setAbundance(abundance);
		scanSignalWSD.setWavelength(wavelenghtRangeFrom + wavelenghtInterval * actualScanOrder);
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

	private void parseMessagge(String messagge, boolean reset) {

		if(reset) {
			chromatogram.newAcquisition(timeInterval, 0);
		}
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

	public void reset(boolean saveData, int timeInterval, float wavelenghtInterval, int wavelenghtRangeFrom, int wavelenghtRangeTo) {

		this.saveData = saveData;
		this.wavelenghtInterval = wavelenghtInterval;
		this.wavelenghtRangeFrom = wavelenghtRangeFrom;
		this.timeInterval = timeInterval;
		this.reset = true;
	}

	public void reset(int timeInterval, float wavelenghtInterval, int wavelenghtRangeFrom, int wavelenghtRangeTo) {

		this.wavelenghtInterval = wavelenghtInterval;
		this.wavelenghtRangeFrom = wavelenghtRangeFrom;
		this.timeInterval = timeInterval;
		this.reset = true;
	}

	public void setSaveData(boolean saveData) {

		this.saveData = saveData;
	}
}
