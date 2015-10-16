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
package org.chromulan.system.control.model.chromatogram;

import java.util.List;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;

public class ChromatogramRecording implements IChromatogramRecording {

	private IChromatogram chromatogram;
	private int numberOfScan;
	String name;

	public ChromatogramRecording() {

		numberOfScan = 0;
		name = DEFAULT_NAME;
	}

	public void addScan(IScan scan) {

		synchronized(chromatogram) {
			chromatogram.addScan(scan);
		}
	}

	public void addScanAutoSet(IScan scan) {

		synchronized(chromatogram) {
			int number = chromatogram.getNumberOfScans();
			scan.setParentChromatogram(chromatogram);
			scan.setRetentionTime(chromatogram.getScanDelay() + chromatogram.getScanInterval() * (number));
			scan.setScanNumber(number);
			chromatogram.addScan(scan);
		}
	}

	public void setScanInterval(int milliseconds) {

		synchronized(chromatogram) {
			if(milliseconds != chromatogram.getScanInterval()) {
				reset();
				chromatogram.setScanInterval(milliseconds);
			}
		}
	}

	public int getScanInterval() {

		synchronized(chromatogram) {
			return chromatogram.getScanInterval();
		}
	}

	public void setScanDelay(int milliseconds) {

		synchronized(chromatogram) {
			if(chromatogram.getScanDelay() != milliseconds) {
				chromatogram.setScanDelay(milliseconds);
				chromatogram.recalculateRetentionTimes();
			}
		}
	}

	public void resetRecording() {

		synchronized(chromatogram) {
			reset();
		}
	}

	private void reset() {

		chromatogram.removeScans(1, chromatogram.getNumberOfScans());
	}

	public IChromatogram getChromatogram() {

		synchronized(chromatogram) {
			return chromatogram;
		}
	}

	public void setChromatogram(IChromatogram chromatogram) {

		synchronized(chromatogram) {
			this.chromatogram = chromatogram;
		}
	}

	@Override
	public void setName(String name) {

		this.name = name;
	}

	@Override
	public String getName() {

		return name;
	}

	@Override
	public int getNumberOfScans() {

		synchronized(chromatogram) {
			return chromatogram.getNumberOfScans();
		}
	}

	@Override
	public double getMaxSignal() {

		synchronized(chromatogram) {
			return getChromatogram().getMaxSignal();
		}
	}
}
