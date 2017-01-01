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

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.swt.ui.series.IMultipleSeries;

public interface IChromatogramAcquisition {

	void addScan(IScan scan);

	void addScanAutoSet(IScan scan);

	IChromatogram getChromatogram();

	double getMaxSignal();

	String getName();

	int getNumberOfScans();

	int getScanDelay();

	int getScanInterval();

	IMultipleSeries getSeries();

	void newAcquisition(int scanInterval, int scanDeley);

	void setName(String name);

	void setScanDelay(int milliseconds);

	void setScanInterval(int milliseconds, boolean reset);
}
