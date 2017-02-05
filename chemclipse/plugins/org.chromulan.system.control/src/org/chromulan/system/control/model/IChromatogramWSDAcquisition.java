/*******************************************************************************
 * Copyright (c) 2016, 2017 Jan Holy.
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.implementation.ChromatogramCSD;
import org.eclipse.chemclipse.csd.model.implementation.ScanCSD;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanSignalWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;

public interface IChromatogramWSDAcquisition extends IChromatogramAcquisition {

	static HashMap<Double, IChromatogramCSD> chromatogramWSDtoCSD(IChromatogramWSD chromatogramWSD) {

		HashMap<Double, IChromatogramCSD> chromatogramCSDs = new HashMap<>();
		int scanDelay = chromatogramWSD.getScanDelay();
		int scanInterval = chromatogramWSD.getScanInterval();
		Iterator<IScan> iteratorScans = chromatogramWSD.getScans().stream().iterator();
		while(iteratorScans.hasNext()) {
			IScanWSD scan = (IScanWSD)iteratorScans.next();
			for(IScanSignalWSD signal : scan.getScanSignals()) {
				double waveLength = signal.getWavelength();
				float abundance = signal.getAbundance();
				int retentionTime = scan.getRetentionTime();
				if(!chromatogramCSDs.containsKey(waveLength)) {
					IChromatogramCSD newChromatogramCSD = new ChromatogramCSD();
					newChromatogramCSD.setScanDelay(scanDelay);
					newChromatogramCSD.setScanInterval(scanInterval);
					chromatogramCSDs.put(waveLength, newChromatogramCSD);
				}
				IChromatogramCSD chromatogram = chromatogramCSDs.get(waveLength);
				chromatogram.addScan(new ScanCSD(retentionTime, abundance));
			}
		}
		return chromatogramCSDs;
	}

	IChromatogramWSD getChromatogramWSD();

	Map<Double, Boolean> getSelectedWaveLengths();

	void resetWaveLengths();
}
