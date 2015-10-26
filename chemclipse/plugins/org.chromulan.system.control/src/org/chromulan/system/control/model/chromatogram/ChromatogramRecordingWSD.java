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
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;

public class ChromatogramRecordingWSD extends AbstractChromatogramRecording implements IChromatogramRecordingWSD {

	public ChromatogramRecordingWSD() {

		super();
	}

	@Override
	public IChromatogram copyChromatogram() {

		return null;
	}

	@Override
	public IChromatogramWSD getChromatogramWSD() {

		IChromatogram chromatogram = getChromatogram();
		if(chromatogram instanceof IChromatogramWSD) {
			return (IChromatogramWSD)chromatogram;
		}
		return null;
	}
}
