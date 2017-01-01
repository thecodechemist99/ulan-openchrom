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
package org.chromulan.system.control.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.implementation.ChromatogramCSD;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.swt.ui.converter.SeriesConverter;
import org.eclipse.chemclipse.swt.ui.series.IMultipleSeries;
import org.eclipse.chemclipse.swt.ui.support.Sign;

public class ChromatogramCSDAcquisition extends AbstractChromatogramAcquisition implements IChromatogramCSDAcquisition {

	public ChromatogramCSDAcquisition(int interval, int delay) {
		super(interval, delay);
	}

	@Override
	protected IChromatogram createChromatogram() {

		return new ChromatogramCSD();
	}

	@Override
	public IChromatogramCSD getChromatogramCSD() {

		IChromatogram chromatogram = getChromatogram();
		if(chromatogram instanceof IChromatogramCSD) {
			return (IChromatogramCSD)chromatogram;
		}
		return null;
	}

	@Override
	public IMultipleSeries getSeries() {

		IChromatogramCSD chromatogram = getChromatogramCSD();
		List<IChromatogramOverview> list = new ArrayList<IChromatogramOverview>(1);
		list.add(chromatogram);
		IMultipleSeries series = null;
		synchronized(this) {
			series = SeriesConverter.convertChromatogramOverviews(list, Sign.POSITIVE, null, false);
		}
		return series;
	}
}
