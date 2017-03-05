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
import org.chromulan.system.control.model.IChromatogramCSDAcquisition;
import org.eclipse.chemclipse.swt.ui.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.swt.ui.series.IMultipleSeries;
import org.eclipse.chemclipse.swt.ui.series.ISeries;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.swtchart.ILineSeries;
import org.swtchart.ILineSeries.PlotSymbolType;
import org.swtchart.ISeries.SeriesType;

public class ChromatogramCSDOverviewUI extends ChromatogramOverviewUI {

	public ChromatogramCSDOverviewUI(Composite parent, int style) {
		super(parent, style);
	}

	public IChromatogramCSDAcquisition getChromatogramCSD() {

		IChromatogramAcquisition chromatogramAcquisition = getChromatogram();
		if(chromatogramAcquisition instanceof IChromatogramCSDAcquisition) {
			return (IChromatogramCSDAcquisition)chromatogramAcquisition;
		}
		return null;
	}

	@Override
	public void setViewSeries() {

		ISeries series;
		ILineSeries lineSeries;
		boolean showChromatogramArea = PreferenceSupplier.showChromatogramArea();
		IChromatogramCSDAcquisition chromatogramCSD = getChromatogramCSD();
		if(chromatogramCSD != null) {
			IMultipleSeries multipleSeries = chromatogramCSD.getSeries();
			if(!multipleSeries.getMultipleSeries().isEmpty() && (multipleSeries.getXMin() != multipleSeries.getXMax())) {
				series = multipleSeries.getMultipleSeries().get(0);
				setMaxSignal(series.getYMax());
				addSeries(series);
				lineSeries = (ILineSeries)getSeriesSet().createSeries(SeriesType.LINE, series.getId());
				lineSeries.setXSeries(series.getXSeries());
				lineSeries.setYSeries(series.getYSeries());
				lineSeries.enableArea(showChromatogramArea);
				lineSeries.setSymbolType(PlotSymbolType.NONE);
				Color chromatogramColor = PreferenceSupplier.getChromatogramColor();
				if(chromatogramColor == null) {
					chromatogramColor = Colors.RED;
				}
				lineSeries.setLineColor(chromatogramColor);
			}
		}
	}
}
