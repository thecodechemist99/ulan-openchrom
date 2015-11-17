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

import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.implementation.ChromatogramCSD;
import org.eclipse.chemclipse.model.core.IChromatogram;

public class ChromatogramRecordingCSD extends AbstractChromatogramRecording implements IChromatogramRecordingCSD {

	public ChromatogramRecordingCSD(int scanDelay, int scanInterval) {

		super(scanDelay, scanInterval);
	}

	@Override
	protected IChromatogram createChromatogram(int scanDelay, int scanInterval) {

		IChromatogramCSD chromatogram = new ChromatogramCSD();
		chromatogram.setScanDelay(scanDelay);
		chromatogram.setScanInterval(scanInterval);
		return chromatogram;
	}

	@Override
	public IChromatogramCSD getChromatogramCSD() {

		IChromatogram chromatogram = getChromatogram();
		if(chromatogram instanceof IChromatogramCSD) {
			return (IChromatogramCSD)chromatogram;
		}
		return null;
	}
}
