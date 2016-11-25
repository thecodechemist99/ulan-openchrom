/*******************************************************************************
 * Copyright (c) 2016 PC.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * PC - initial API and implementation
 *******************************************************************************/
package org.chromulan.system.control.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.swt.ui.series.IMultipleSeries;
import org.eclipse.chemclipse.swt.ui.support.Sign;
import org.eclipse.chemclipse.wsd.model.core.AbstractChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanSignalWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.ChromatogramSelectionWSD;
import org.eclipse.chemclipse.wsd.model.core.support.IMarkedWavelengths;
import org.eclipse.chemclipse.wsd.model.core.support.MarkedWavelength;
import org.eclipse.chemclipse.wsd.model.core.support.MarkedWavelengths;
import org.eclipse.chemclipse.wsd.swt.ui.converter.SeriesConverterWSD;

public class ChromatogramWSDAcquisition extends AbstractChromatogramAcquisition implements IChromatogramWSDAcquisition {

	private IMarkedWavelengths selectedMarkedWavelengths;

	public ChromatogramWSDAcquisition(int interval, int delay) {
		super(interval, delay);
		selectedMarkedWavelengths = new MarkedWavelengths();
	}

	@Override
	protected IChromatogram createChromatogram() {

		IChromatogramWSD chromatogramWSD = new AbstractChromatogramWSD() {

			@Override
			public double getPeakIntegratedArea() {

				return 0;
			}
		};
		return chromatogramWSD;
	}

	@Override
	public IChromatogramWSD geChromatogramWSD() {

		IChromatogram chromatogram = getChromatogram();
		if(chromatogram instanceof IChromatogramWSD) {
			return (IChromatogramWSD)chromatogram;
		}
		return null;
	}

	@Override
	public IMarkedWavelengths getSelectedWaveLenaght() {

		return selectedMarkedWavelengths;
	}

	@Override
	public IMultipleSeries getSeries() {

		IMultipleSeries multipleSeries = null;
		synchronized(this) {
			IChromatogramWSD chromatogramWSD = geChromatogramWSD();
			List<Double> wavelengths = new ArrayList<Double>(this.getWaveLenaght().getWavelengths());
			try {
				ChromatogramSelectionWSD chromatogramSelection = new ChromatogramSelectionWSD(chromatogramWSD, false);
				multipleSeries = SeriesConverterWSD.convertChromatogram(chromatogramSelection, wavelengths, false, Sign.POSITIVE);
			} catch(ChromatogramIsNullException e) {
			}
		}
		return multipleSeries;
	}

	@Override
	public IMarkedWavelengths getWaveLenaght() {

		IMarkedWavelengths wavelengths = new MarkedWavelengths();
		synchronized(this) {
			IChromatogramWSD wsdChromatogram = geChromatogramWSD();
			IScanWSD scan = (IScanWSD)wsdChromatogram.getScans().stream().findFirst().get();
			for(IScanSignalWSD signal : scan.getScanSignals()) {
				wavelengths.add(new MarkedWavelength(signal.getWavelength()));
			}
			return wavelengths;
		}
	}
}
