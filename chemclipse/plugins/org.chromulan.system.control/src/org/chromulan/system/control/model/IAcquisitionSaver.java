/*******************************************************************************
 * Copyright (c) 2015, 2018 Jan Holy.
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

import java.io.File;
import java.util.List;

import org.eclipse.chemclipse.converter.core.ISupplier;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IAcquisitionSaver {

	IAcquisition getAcquisition();

	File getFile();

	List<IProcessingInfo> getChromatogramExportConverterProcessInfo();

	ISupplier getSupplier();

	List<IProcessingInfo> save(IProgressMonitor progressMonitor, List<SaveChromatogram> chromatograms);

	void setAcquisition(IAcquisition acquisition);

	void setFile(File file);

	void setSupplier(ISupplier supplier);
}
