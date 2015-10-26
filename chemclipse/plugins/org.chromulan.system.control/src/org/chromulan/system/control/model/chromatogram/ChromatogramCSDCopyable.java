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

import java.io.File;
import java.util.Date;

import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.implementation.ChromatogramCSD;
import org.eclipse.chemclipse.model.core.IChromatogram;

public class ChromatogramCSDCopyable extends ChromatogramCSD implements IChromatogramCSDCopyable {

	public ChromatogramCSDCopyable() {

		super();
	}

	@Override
	public IChromatogram copyChromatogram() {

		return copyChromatogramCSD();
	}

	@Override
	public IChromatogramCSD copyChromatogramCSD() {

		IChromatogramCSD chrom = new ChromatogramCSD();
		chrom.getScans().addAll(getScans());
		chrom.setScanInterval(getScanInterval());
		chrom.setConverterId(getConverterId());
		chrom.setDate(new Date(getDate().getTime()));
		chrom.setScanDelay(getScanDelay());
		chrom.setFile(new File(getFile().getAbsolutePath()));
		// TODO: dodelat.... !!!!!!!!!!!!!
		return chrom;
	}
}
