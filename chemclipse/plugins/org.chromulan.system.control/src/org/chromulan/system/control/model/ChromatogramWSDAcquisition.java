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
package org.chromulan.system.control.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.swt.ui.series.IMultipleSeries;
import org.eclipse.chemclipse.swt.ui.support.Sign;
import org.eclipse.chemclipse.wsd.model.core.AbstractChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanSignalWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.ChromatogramSelectionWSD;
import org.eclipse.chemclipse.wsd.swt.ui.converter.SeriesConverterWSD;

public class ChromatogramWSDAcquisition extends AbstractChromatogramAcquisition implements IChromatogramWSDAcquisition {

	private Map<Double, Boolean> selectedWavelengths;
	private boolean setWavelenght = true;

	public ChromatogramWSDAcquisition(int interval, int delay) {
		super(interval, delay);
		selectedWavelengths = new ConcurrentHashMap<>();
	}

	@Override
	public void addScan(IScan scan) {

		super.addScan(scan);
		if(setWavelenght) {
			resetWaveLengths();
			setWavelenght = false;
		}
	}

	@Override
	public void addScanAutoSet(IScan scan) {

		super.addScanAutoSet(scan);
		if(setWavelenght) {
			resetWaveLengths();
			setWavelenght = false;
		}
	}

	@Override
	protected IChromatogram createChromatogram() {

		setWavelenght = true;
		IChromatogramWSD chromatogramWSD = new AbstractChromatogramWSD() {

			@Override
			public double getPeakIntegratedArea() {

				return 0;
			}
		};
		return chromatogramWSD;
	}

	@Override
	public IChromatogramWSD getChromatogramWSD() {

		IChromatogram chromatogram = getChromatogram();
		if(chromatogram instanceof IChromatogramWSD) {
			return (IChromatogramWSD)chromatogram;
		}
		return null;
	}

	@Override
	public Map<Double, Boolean> getSelectedWaveLengths() {

		return selectedWavelengths;
	}

	@Override
	public IMultipleSeries getSeries() {

		IMultipleSeries multipleSeries = null;
		synchronized(this) {
			IChromatogramWSD chromatogramWSD = getChromatogramWSD();
			List<Double> wavelengths = new ArrayList<Double>();
			synchronized(selectedWavelengths) {
				Iterator<Entry<Double, Boolean>> iterator = selectedWavelengths.entrySet().iterator();
				while(iterator.hasNext()) {
					Map.Entry<Double, Boolean> entry = iterator.next();
					if(entry.getValue()) {
						wavelengths.add(entry.getKey());
					}
				}
			}
			try {
				ChromatogramSelectionWSD chromatogramSelection = new ChromatogramSelectionWSD(chromatogramWSD, false);
				multipleSeries = SeriesConverterWSD.convertChromatogram(chromatogramSelection, wavelengths, false, Sign.POSITIVE);
			} catch(ChromatogramIsNullException e) {
			}
		}
		return multipleSeries;
	}

	@Override
	public void resetWaveLengths() {

		synchronized(this) {
			selectedWavelengths.clear();
			IChromatogramWSD wsdChromatogram = getChromatogramWSD();
			IScanWSD scan = (IScanWSD)wsdChromatogram.getScans().stream().findFirst().get();
			if(scan != null) {
				for(IScanSignalWSD signal : scan.getScanSignals()) {
					selectedWavelengths.put(signal.getWavelength(), true);
				}
			}
		}
	}
}
