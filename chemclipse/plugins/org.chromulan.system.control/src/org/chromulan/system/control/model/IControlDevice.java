/*******************************************************************************
 * Copyright (c) 2015 Dr. Philip Wenig.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.chromulan.system.control.model;

import org.chromulan.system.control.model.chromatogram.ChromatogramRecording;

public interface IControlDevice {

	String getName();

	void setName(String name);

	boolean hasChromatogram();

	void setChromatogram(ChromatogramRecording chromatogram) throws UnsupportedOperationException;

	ChromatogramRecording getChromatogram() throws UnsupportedOperationException;

	void setAnalysis(IAnalysis analysis);

	IAnalysis getAnalysis();

	boolean isPrepare();

	String getContributionURI();
}