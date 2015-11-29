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
package org.chromulan.system.control.model.data;

import org.chromulan.system.control.model.IControlDevice;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;

public abstract class AbstractChromatogramData extends AbstractDeviceData implements IChromatogramData {

	private IChromatogram chromatogram;

	public AbstractChromatogramData(IControlDevice device, int scanDelay, int scanInterval) {

		super(device);
		chromatogram = createChromatogram(scanDelay, scanInterval);
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
			scan.setScanNumber(number + 1);
			chromatogram.addScan(scan);
		}
	}

	abstract protected IChromatogram createChromatogram(int scanDelay, int scanInterval);

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
	public int getNumberOfScans() {

		synchronized(chromatogram) {
			return chromatogram.getNumberOfScans();
		}
	}

	@Override
	public int getScanDelay() {

		synchronized(chromatogram) {
			return chromatogram.getScanDelay();
		}
	}

	@Override
	public int getScanInterval() {

		synchronized(chromatogram) {
			return chromatogram.getScanInterval();
		}
	}

	@Override
	public void newRecord(int scanDelay, int scanInterval) {

		synchronized(chromatogram) {
			this.chromatogram = createChromatogram(scanDelay, scanInterval);
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
