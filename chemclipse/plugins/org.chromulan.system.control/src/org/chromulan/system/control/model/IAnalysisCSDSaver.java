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
package org.chromulan.system.control.model;

import java.util.List;

import org.chromulan.system.control.model.chromatogram.IChromatogramRecordingCSD;

public interface IAnalysisCSDSaver extends IAnalysisSaver {

	void addChromatogam(IChromatogramRecordingCSD chromatogram);

	List<IChromatogramRecordingCSD> getChromatograms();

	void removeAllChromatograms();

	void removeChromatogam(IChromatogramRecordingCSD chromatogram);
}
