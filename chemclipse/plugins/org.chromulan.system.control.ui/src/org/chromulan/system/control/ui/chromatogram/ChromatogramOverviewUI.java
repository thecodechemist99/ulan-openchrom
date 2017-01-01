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
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.swt.ui.components.AbstractLineSeriesUI;
import org.eclipse.chemclipse.swt.ui.support.AxisTitlesIntensityScale;
import org.eclipse.chemclipse.swt.ui.support.ChartUtil;
import org.eclipse.swt.widgets.Composite;
import org.swtchart.IAxis;
import org.swtchart.Range;

public abstract class ChromatogramOverviewUI extends AbstractLineSeriesUI {

	private boolean autoMinYAdjustIntensity;
	private IChromatogramAcquisition chromatogramAcquisition;
	private double interval;
	private double minYAdjustIntensity;

	public ChromatogramOverviewUI(Composite parent, int style) {
		super(parent, style, new AxisTitlesIntensityScale());
		autoMinYAdjustIntensity = false;
		minYAdjustIntensity = 0.01;
		interval = 30000;
	}

	@Override
	public void adjustRange() {

		adjustXRange();
		adjustYRange();
	}

	@Override
	public void adjustYRange() {

		if(autoMinYAdjustIntensity) {
			setAutoAdjustIntensity(true);
			for(IAxis axis : getAxisSet().getYAxes()) {
				axis.adjustRange();
			}
		} else {
			double minY, maxY;
			minY = getMultipleSeries().getYMin();
			maxY = getMultipleSeries().getYMax();
			if(maxY < minYAdjustIntensity) {
				setAutoAdjustIntensity(false);
				maxY = minYAdjustIntensity;
				setMaxSignal(minYAdjustIntensity);
				setYMaxIntensityAdjusted(minYAdjustIntensity);
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

	public void displayAllChromatogram() {

		if(this.chromatogramAcquisition != null && this.chromatogramAcquisition.getNumberOfScans() != 0) {
			adjustRange();
			redraw();
		}
	}

	public void displayInteval() {

		if(this.chromatogramAcquisition != null && this.chromatogramAcquisition.getNumberOfScans() != 0) {
			setXAxisInterval();
			adjustYRange();
			redraw();
		}
	}

	public IChromatogramAcquisition getChromatogram() {

		return chromatogramAcquisition;
	}

	public double getInterval() {

		return interval;
	}

	public double getMinYAdjustIntensity() {

		return minYAdjustIntensity;
	}

	public boolean isAutoMinYAdjustIntensity() {

		return autoMinYAdjustIntensity;
	}

	@Override
	public void redrawXAxisBottomScale() {

		double min, max;
		Range range;
		/*
		 * Set minutes scale.
		 */
		range = getXAxisTop().getRange();
		min = range.lower / IChromatogramOverview.MINUTE_CORRELATION_FACTOR;
		max = range.upper / IChromatogramOverview.MINUTE_CORRELATION_FACTOR;
		ChartUtil.setRange(getXAxisBottom(), min, max);
	}

	public void reloadData() {

		if(this.chromatogramAcquisition != null && this.chromatogramAcquisition.getNumberOfScans() != 0) {
			deleteAllCurrentSeries();
			setViewSeries();
		}
	}

	public void setAutoMinYAdjustIntensity(boolean autoMinYAdjustIntensity) {

		this.autoMinYAdjustIntensity = autoMinYAdjustIntensity;
	}

	public void setChromatogram(IChromatogramAcquisition chromatogramAcquisition) {

		this.chromatogramAcquisition = chromatogramAcquisition;
	}

	public void setInterval(double interval) {

		if(interval > 0) {
			this.interval = interval;
		}
	}

	public void setMinYAdjustIntensity(double minYAdjustIntensity) {

		if(minYAdjustIntensity > 0) {
			this.minYAdjustIntensity = minYAdjustIntensity;
		}
	}

	public void setXAxisInterval() {

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
}
