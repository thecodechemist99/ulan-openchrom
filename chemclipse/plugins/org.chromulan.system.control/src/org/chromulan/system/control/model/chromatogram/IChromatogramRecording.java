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

public interface IChromatogramRecording {

	final String DEFAULT_NAME = "chromatogram";

	void addScan(IScan scan);

	void addScanAutoSet(IScan scan);

	IChromatogram copyChromatogram();

	IChromatogram getChromatogram();

	double getMaxSignal();

	String getName();

	int getNumberOfScans();

	int getScanInterval();

	void resetRecording();

	void setChromatogram(IChromatogram chromatogram);

	void setName(String name);

	void setScanDelay(int milliseconds);

	void setScanInterval(int milliseconds);
}
