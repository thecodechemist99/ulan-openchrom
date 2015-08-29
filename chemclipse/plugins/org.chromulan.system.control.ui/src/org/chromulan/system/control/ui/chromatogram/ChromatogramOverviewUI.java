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

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.swt.ui.components.AbstractLineSeriesUI;
import org.eclipse.chemclipse.swt.ui.converter.SeriesConverter;
import org.eclipse.chemclipse.swt.ui.series.ISeries;
import org.eclipse.chemclipse.swt.ui.series.Series;
import org.eclipse.chemclipse.swt.ui.support.AxisTitlesIntensityScale;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.swt.ui.support.Sign;
import org.eclipse.chemclipse.wsd.model.core.selection.ChromatogramSelectionWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;
import org.eclipse.swt.widgets.Composite;
import org.swtchart.ILineSeries;
import org.swtchart.LineStyle;
import org.swtchart.ILineSeries.PlotSymbolType;
import org.swtchart.ISeries.SeriesType;


public class ChromatogramOverviewUI extends AbstractLineSeriesUI {

	protected IChromatogram chromatogram; 
	protected Thread autoOverwrite;
	
	
	protected int overWritePeriod;
	
	
	public ChromatogramOverviewUI(Composite parent, int style) {

		super(parent, style, new AxisTitlesIntensityScale());
		overWritePeriod = 1000;
	}
	
	
	
	public void setChromatogram(IChromatogram chromatogram) 
	{
		this.chromatogram= chromatogram;
	}
	
	public  IChromatogram getChromatogram() {

		return chromatogram;
	}
	

	
	@Override
	public void setViewSeries() {
		
		//if(storedChromatogramSelection instanceof IChromatogramSelectionWSD) {

			//IChromatogramSelectionWSD chromatogramSelection = (IChromatogramSelectionWSD)storedChromatogramSelection;
			ISeries series = null;
			ILineSeries lineSeries;

			/*synchronized(chromatogram) {
				series = SeriesConverter.convertChromatogram(chromatogram, Sign.POSITIVE, false);
			}*/
			
			
			
			//addSeries(series);
			lineSeries = (ILineSeries)getSeriesSet().createSeries(SeriesType.LINE, series.getId());
			lineSeries.setXSeries(series.getXSeries());
			lineSeries.setYSeries(series.getYSeries());
			lineSeries.enableArea(true);
			lineSeries.setSymbolType(PlotSymbolType.NONE);
			

			
		//}

	}
	
	
	
	public void setAutoOverwrite(boolean autoOverwrite)
	{
		if(this.autoOverwrite!= null)
		{
			if(autoOverwrite)
			{
				if(!this.autoOverwrite.isAlive())
				{
					this.autoOverwrite.start();
				}
				
			}
			else
			{
				if(this.autoOverwrite.isAlive())
				{
					this.autoOverwrite.interrupt();
				}
			}
			
		}
	}
	
	public void setOverwritePeriod(int milliseconds)
	{
		overWritePeriod = milliseconds;
	}
	
	
	public int getOverWritePeriod() {

		return overWritePeriod;
	}
	
	
	public void setVisitableInterval(int seconds,boolean autoOverwrite)
	{
		setAutoOverwrite(false);
		this.autoOverwrite = new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				
				
			}
		});
		this.autoOverwrite.start();
		
		
	}
	
	public void  viewAllchromatogram(boolean autoOverwrite)
	{
		setAutoOverwrite(false);
		
	}
}
