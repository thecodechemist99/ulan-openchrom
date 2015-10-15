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
import org.eclipse.chemclipse.model.core.AbstractChromatogram;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.swt.ui.components.AbstractLineSeriesUI;
import org.eclipse.chemclipse.swt.ui.converter.SeriesConverter;
import org.eclipse.chemclipse.swt.ui.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.swt.ui.series.ISeries;
import org.eclipse.chemclipse.swt.ui.support.AxisTitlesIntensityScale;
import org.eclipse.chemclipse.swt.ui.support.ChartUtil;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.swt.ui.support.Sign;
import org.eclipse.swt.graphics.Color;
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
			boolean showChromatogramArea = PreferenceSupplier.showChromatogramArea();
			synchronized(chromatogramRecording.getChromatogram()) {
				series = SeriesConverter.convertChromatogram(chromatogramRecording.getChromatogram(), Sign.POSITIVE, false);
			}
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

	public void displayInteval(double interval, boolean setMinAdjustIntensity, double minAdjustIntensity) {

		setViewSeries();
		setXAxis(interval);
		setYAxis(setMinAdjustIntensity, minAdjustIntensity);
		redraw();
	}

	public void displayAllChromatogram(boolean setMinAdjustIntensity, double minAdjustIntensity) {

		setViewSeries();
		getXAxisBottom().adjustRange();
		getXAxisTop().adjustRange();
		setYAxis(setMinAdjustIntensity, minAdjustIntensity);
		redraw();
	}

	public void reloadData() {

		setViewSeries();
		redraw();
	}

	public void setXAxis(double interval) {

		if(interval > 0) {
			double minX, maxX;
			maxX = getMultipleSeries().getXMax();
			if(maxX - interval > getMultipleSeries().getXMin()) {
				minX = maxX - interval;
			} else {
				minX = getMultipleSeries().getXMin();
			}
			for(IAxis axis : getAxisSet().getXAxes()) {
				ChartUtil.setRange(axis, minX, maxX);
			}
		}
	}

	public void setYAxis(boolean setMinAdjustIntensity, double minAdjustIntensity) {

		if(!setMinAdjustIntensity) {
			setAutoAdjustIntensity(true);
			for(IAxis axis : getAxisSet().getYAxes()) {
				axis.adjustRange();
			}
		} else {
			double minY, maxY;
			setAutoAdjustIntensity(false);
			minY = getMultipleSeries().getYMin();
			maxY = getMultipleSeries().getYMax();
			if(maxY < minAdjustIntensity) {
				maxY = minAdjustIntensity;
				setMaxSignal(minAdjustIntensity);
				setYMaxIntensityAdjusted(minAdjustIntensity);
				for(IAxis axis : getAxisSet().getYAxes()) {
					ChartUtil.setRange(axis, minY, maxY);
				}
			} else {
				setAutoAdjustIntensity(true);
				for(IAxis axis : getAxisSet().getYAxes()) {
					axis.adjustRange();
				}
			}
		}
	}

	@Override
	public void redrawXAxisBottomScale() {

		double min, max;
		Range range;
		/*
		 * Set minutes scale.
		 */
		range = getXAxisTop().getRange();
		min = range.lower / AbstractChromatogram.MINUTE_CORRELATION_FACTOR;
		max = range.upper / AbstractChromatogram.MINUTE_CORRELATION_FACTOR;
		ChartUtil.setRange(getXAxisBottom(), min, max);
	}
}
