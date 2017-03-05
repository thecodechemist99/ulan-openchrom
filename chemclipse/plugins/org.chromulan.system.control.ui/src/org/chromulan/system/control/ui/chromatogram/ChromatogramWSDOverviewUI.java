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
package org.chromulan.system.control.ui.chromatogram;

import org.chromulan.system.control.model.IChromatogramAcquisition;
import org.chromulan.system.control.model.IChromatogramWSDAcquisition;
import org.eclipse.chemclipse.swt.ui.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.swt.ui.series.IMultipleSeries;
import org.eclipse.chemclipse.swt.ui.series.ISeries;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.swt.ui.support.IColorScheme;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.swtchart.ILineSeries;
import org.swtchart.ILineSeries.PlotSymbolType;
import org.swtchart.ISeries.SeriesType;

public class ChromatogramWSDOverviewUI extends ChromatogramOverviewUI {

	public ChromatogramWSDOverviewUI(Composite parent, int style) {
		super(parent, style);
	}

	public IChromatogramWSDAcquisition getChromatogramWSD() {

		IChromatogramAcquisition chromatogramAcquisition = getChromatogram();
		if(chromatogramAcquisition instanceof IChromatogramWSDAcquisition) {
			return (IChromatogramWSDAcquisition)chromatogramAcquisition;
		}
		return null;
	}

	private void setAdditionalIonSeries(ISeries series, Color color) {

		addSeries(series);
		ILineSeries lineSeries = (ILineSeries)getSeriesSet().createSeries(SeriesType.LINE, series.getId());
		lineSeries.setXSeries(series.getXSeries());
		lineSeries.setYSeries(series.getYSeries());
		lineSeries.enableArea(true);
		lineSeries.setSymbolType(PlotSymbolType.NONE);
		lineSeries.setLineColor(color);
	}

	@Override
	public void setViewSeries() {

		IChromatogramWSDAcquisition chromatogramWSD = getChromatogramWSD();
		if(chromatogramWSD != null) {
			IMultipleSeries multipleSeries = chromatogramWSD.getSeries();
			if(!multipleSeries.getMultipleSeries().isEmpty() && (multipleSeries.getXMin() != multipleSeries.getXMax())) {
				int size = multipleSeries.getMultipleSeries().size();
				setMaxSignal(multipleSeries.getYMax());
				ISeries series;
				String colorSchemeOverlay = PreferenceSupplier.getColorSchemeOverlay();
				IColorScheme colorScheme = Colors.getColorScheme(colorSchemeOverlay);
				for(int i = 0; i < size; i++) {
					series = multipleSeries.getMultipleSeries().get(i);
					if(series.getXSeries().length != 0) {
						setAdditionalIonSeries(series, colorScheme.getColor());
						colorScheme.incrementColor();
					}
				}
			}
		}
	}
}
