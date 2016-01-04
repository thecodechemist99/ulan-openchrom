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
package org.chromulan.system.control.model;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;

public abstract class AbstractChromatogramAcquisition implements IChromatogramAcquisition {

	private IChromatogram chromatogram;
	private String name;

	public AbstractChromatogramAcquisition() {
	}
	
	public AbstractChromatogramAcquisition(IChromatogram chromatogram, int interval, int delay) {
		this.chromatogram = chromatogram;
		chromatogram.setScanInterval(interval);
		chromatogram.setScanDelay(delay);
	}

	@Override
	public void addScan(IScan scan) {

		synchronized(this) {
			chromatogram.addScan(scan);
		}
	}

	@Override
	public void addScanAutoSet(IScan scan) {

		synchronized(this) {
			int number = chromatogram.getNumberOfScans();
			scan.setParentChromatogram(chromatogram);
			scan.setRetentionTime(chromatogram.getScanDelay() + chromatogram.getScanInterval() * (number));
			scan.setScanNumber(number + 1);
			chromatogram.addScan(scan);
		}
	}

	@Override
	public IChromatogram getChromatogram() {

		synchronized(this) {
			return chromatogram;
		}
	}

	@Override
	public double getMaxSignal() {

		synchronized(this) {
			return getChromatogram().getMaxSignal();
		}
	}

	@Override
	public String getName() {
		synchronized(this) {
			return name;
		}
	}

	@Override
	public int getNumberOfScans() {

		synchronized(this) {
			return chromatogram.getNumberOfScans();
		}
	}

	@Override
	public int getScanDelay() {

		synchronized(this) {
			return chromatogram.getScanDelay();
		}
	}

	@Override
	public int getScanInterval() {

		synchronized(this) {
			return chromatogram.getScanInterval();
		}
	}

	@Override
	public void setChromatogram(IChromatogram chromatogram, int interval, int delay ) {

		synchronized(this) {
			this.chromatogram = chromatogram;
			chromatogram.setScanInterval(interval);
			chromatogram.setScanDelay(delay);
		}
	}

	private void reset() {

		chromatogram.removeScans(1, chromatogram.getNumberOfScans());
	}

	@Override
	public void resetChromatogram() {

		synchronized(this) {
			reset();
		}
	}

	@Override
	public void setName(String name) {

		synchronized(this) {
			this.name = name;
		}
	}

	@Override
	public void setScanDelay(int milliseconds) {

		synchronized(this) {
			if(chromatogram.getScanDelay() != milliseconds) {
				chromatogram.setScanDelay(milliseconds);
				chromatogram.recalculateRetentionTimes();
			}
		}
	}

	@Override
	public void setScanInterval(int milliseconds, boolean reset) {

		synchronized(this) {
			if(milliseconds != chromatogram.getScanInterval()) {
				if(reset)
				{
					reset();
					chromatogram.setScanInterval(milliseconds);
				} else {
					chromatogram.setScanInterval(milliseconds);
					chromatogram.recalculateRetentionTimes();
				}
				
			}
		}
	}
}
