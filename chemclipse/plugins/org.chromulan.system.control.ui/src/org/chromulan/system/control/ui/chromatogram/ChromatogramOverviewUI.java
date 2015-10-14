/*******************************************************************************
 * Copyright (c) 2015 Jan Holy.
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

import org.chromulan.system.control.model.chromatogram.IChromatogramRecording;
import org.chromulan.system.control.model.chromatogram.IChromatogramRecordingWSD;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.swt.ui.components.AbstractLineSeriesUI;
import org.eclipse.chemclipse.swt.ui.converter.SeriesConverter;
import org.eclipse.chemclipse.swt.ui.series.ISeries;
import org.eclipse.chemclipse.swt.ui.support.AxisTitlesIntensityScale;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.swt.ui.support.Sign;
import org.eclipse.swt.widgets.Composite;
import org.swtchart.IAxis;
import org.swtchart.ILineSeries;
import org.swtchart.ILineSeries.PlotSymbolType;
import org.swtchart.ISeries.SeriesType;
import org.swtchart.Range;

public class ChromatogramOverviewUI extends AbstractLineSeriesUI {

	private IChromatogramRecording chromatogramRecording;

	public ChromatogramOverviewUI(Composite parent, int style) {

		super(parent, style, new AxisTitlesIntensityScale());
	}

	public void setChromatogram(IChromatogramRecording chromatogramRecording) {

		this.chromatogramRecording = chromatogramRecording;
	}

	public IChromatogramRecording getChromatogram() {

		return chromatogramRecording;
	}

	@Override
	public void setViewSeries() {

		if(this.chromatogramRecording != null && this.chromatogramRecording.getNumberOfScans() != 0) {
			ISeries series;
			ILineSeries lineSeries;
			deleteAllCurrentSeries();
			synchronized(chromatogramRecording.getChromatogram()) {
				series = SeriesConverter.convertChromatogram(chromatogramRecording.getChromatogram(), Sign.POSITIVE, false);
			}
			// addSeries(series);
			lineSeries = (ILineSeries)getSeriesSet().createSeries(SeriesType.LINE, series.getId());
			lineSeries.setXSeries(series.getXSeries());
			lineSeries.setYSeries(series.getYSeries());
			lineSeries.enableArea(true);
			lineSeries.setSymbolType(PlotSymbolType.NONE);
			lineSeries.setLineColor(Colors.RED);
		}
	}

	public void displayInteval(double interval) {

		double end = 0;
		double start = 0;
		for(ISeries series : getMultipleSeries().getMultipleSeries()) {
			if(end > series.getXMax()) {
				end = series.getXMax();
			}
		}
		for(IAxis axis : getAxisSet().getXAxes()) {
			axis.adjustRange();
		}
		start = end - interval;
		if((start < 0)) {
			start = 0;
		}
		for(IAxis axis : getAxisSet().getXAxes()) {
			axis.setRange(new Range(start, end));
		}
		redraw();
	}

	public void displayAllChromatogram() {

		adjustRange();
		redraw();
	}
}
