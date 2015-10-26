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

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;

public abstract class AbstractChromatogramRecording implements IChromatogramRecording {

	private IChromatogram chromatogram;
	String name;

	public AbstractChromatogramRecording() {

		name = DEFAULT_NAME;
	}

	@Override
	public void addScan(IScan scan) {

		synchronized(chromatogram) {
			chromatogram.addScan(scan);
		}
	}

	@Override
	public void addScanAutoSet(IScan scan) {

		synchronized(chromatogram) {
			int number = chromatogram.getNumberOfScans();
			scan.setParentChromatogram(chromatogram);
			scan.setRetentionTime(chromatogram.getScanDelay() + chromatogram.getScanInterval() * (number));
			scan.setScanNumber(number);
			chromatogram.addScan(scan);
		}
	}

	@Override
	public IChromatogram getChromatogram() {

		synchronized(chromatogram) {
			return chromatogram;
		}
	}

	@Override
	public double getMaxSignal() {

		synchronized(chromatogram) {
			return getChromatogram().getMaxSignal();
		}
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
	public int getScanInterval() {

		synchronized(chromatogram) {
			return chromatogram.getScanInterval();
		}
	}

	private void reset() {

		chromatogram.removeScans(1, chromatogram.getNumberOfScans());
	}

	@Override
	public void resetRecording() {

		synchronized(chromatogram) {
			reset();
		}
	}

	@Override
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
	public void setScanDelay(int milliseconds) {

		synchronized(chromatogram) {
			if(chromatogram.getScanDelay() != milliseconds) {
				chromatogram.setScanDelay(milliseconds);
				chromatogram.recalculateRetentionTimes();
			}
		}
	}

	@Override
	public void setScanInterval(int milliseconds) {

		synchronized(chromatogram) {
			if(milliseconds != chromatogram.getScanInterval()) {
				reset();
				chromatogram.setScanInterval(milliseconds);
			}
		}
	}
}
