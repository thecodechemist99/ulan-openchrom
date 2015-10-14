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

public interface IChromatogramRecording {

	final String DEFAULT_NAME = "chromatogram";

	void setName(String name);

	String getName();

	void addScan(IScan scan);

	void setScanInterval(int milliseconds);

	int getScanInterval();

	void setScanDelay(int milliseconds);

	void resetRecording();

	IChromatogram getChromatogram();

	void setChromatogram(IChromatogram chromatogram);
	
	int getNumberOfScans();
	
	void addScanAutoSet(IScan scan);
}
