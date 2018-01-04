/*******************************************************************************
 * Copyright (c) 2016, 2018 Jan Holy.
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

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.chromulan.system.control.model.ChromatogramWSDAcquisition;
import org.chromulan.system.control.model.IChromatogramWSDAcquisition;
import org.eclipse.chemclipse.wsd.model.core.AbstractScanSignalWSD;
import org.eclipse.chemclipse.wsd.model.core.AbstractScanWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanSignalWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;

public class DataReceive {

	private boolean dataRecieve;
	private final List<IDataRecieveListener> dataRecieveListeners = new ArrayList<>();
	private final List<IDataStopRecieveListener> dataStopRecieveListeners = new ArrayList<>();
	private final ExecutorService executor = Executors.newSingleThreadExecutor();
	private IChromatogramWSDAcquisition chromatogram;
	private float lastScanTime = 0;
	private volatile boolean reset;
	private volatile Boolean saveData;
	volatile private boolean testConnection;
	private int timeInterval;
	private Timer timer = new Timer();
	private float wavelenghtInterval;
	private int wavelenghtRangeFrom;
	private int wavelenghtRangeTo;

	public DataReceive(String name, int timeInterval, float wavelenghtInterval, int wavelenghtRangeFrom, int wavelenghtRangeTo) {
		this.chromatogram = new ChromatogramWSDAcquisition(timeInterval, 0);
		this.chromatogram.setName(name);
		this.wavelenghtInterval = wavelenghtInterval;
		this.wavelenghtRangeFrom = wavelenghtRangeFrom;
		this.wavelenghtRangeTo = wavelenghtRangeTo;
		this.timeInterval = timeInterval;
		this.saveData = true;
		this.reset = false;
	}

	void addDataRecieveListener(IDataRecieveListener dataRecieveListener) {

		synchronized(dataRecieveListeners) {
			dataRecieveListeners.add(dataRecieveListener);
		}
	}

	public void addScan(final String message) {

		if(!dataRecieve) {
			scheduleTimer();
		}
		testConnection = true;
		dataRecieve = true;
		synchronized(dataRecieveListeners) {
			for(IDataRecieveListener dataRecieveListener : dataRecieveListeners) {
				dataRecieveListener.dataRecieve();
			}
		}
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

	void addStopDataRecieveListener(IDataStopRecieveListener dataStopRecieveListener) {

		synchronized(dataStopRecieveListeners) {
			dataStopRecieveListeners.add(dataStopRecieveListener);
		}
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

	public boolean isDataRecieve() {

		return dataRecieve;
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
			if(data.length == (2 + (wavelenghtRangeTo - wavelenghtRangeFrom) / wavelenghtInterval + 1)) {
				float scanTime = Float.valueOf(data[1]);
				if(scanTime < lastScanTime) {
					chromatogram.newAcquisition(timeInterval, 0);
				}
				lastScanTime = scanTime;
				for(int i = 2; i < data.length; i++) {
					float value = Float.valueOf(data[i]);
					addScanSignal(value, actualScan, i - 2);
				}
				actualScan.setRetentionTime((int)(lastScanTime * 1000));
				chromatogram.addScanAutoSet(actualScan);
			}
		} catch(NumberFormatException e) {
		}
	}

	void removeDataRecieveListener(IDataRecieveListener dataRecieveListener) {

		synchronized(dataRecieveListener) {
			dataRecieveListeners.remove(dataRecieveListener);
		}
	}

	void removeStopDataRecieveListener(IDataStopRecieveListener dataStopRecieveListener) {

		synchronized(dataStopRecieveListeners) {
			dataStopRecieveListeners.remove(dataStopRecieveListener);
		}
	}

	public void reset(int timeInterval, float wavelenghtInterval, int wavelenghtRangeFrom, int wavelenghtRangeTo) {

		this.wavelenghtInterval = wavelenghtInterval;
		this.wavelenghtRangeFrom = wavelenghtRangeFrom;
		this.wavelenghtRangeTo = wavelenghtRangeTo;
		this.timeInterval = timeInterval;
		this.reset = true;
		this.lastScanTime = 0;
	}

	private void scheduleTimer() {

		timer.cancel();
		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {

				if(testConnection) {
					testConnection = false;
					scheduleTimer();
				} else {
					dataRecieve = false;
					synchronized(dataStopRecieveListeners) {
						for(IDataStopRecieveListener dataStopRecieveListener : dataStopRecieveListeners) {
							dataStopRecieveListener.dataStopRecieve();
						}
					}
				}
			}
		}, 10000);
	}
}
