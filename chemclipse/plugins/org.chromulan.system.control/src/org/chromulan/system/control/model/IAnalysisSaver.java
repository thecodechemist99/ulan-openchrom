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

import java.io.File;
import java.util.List;

import org.chromulan.system.control.model.chromatogram.IChromatogramDescription;
import org.eclipse.chemclipse.converter.core.ISupplier;
import org.eclipse.chemclipse.converter.processing.IExportConverterProcessingInfo;
import org.eclipse.chemclipse.converter.processing.chromatogram.IChromatogramExportConverterProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IAnalysisSaver {

	void addDescription(IChromatogramDescription description);

	List<IChromatogramDescription> getDescriptions();

	File getFile();

	List<IChromatogramExportConverterProcessingInfo> getChromatogramExportConverterProcessInfo();

	String getName();

	List<IExportConverterProcessingInfo> getProcessInfo();

	ISupplier getSupplier();

	void removeAllDescriptions();

	void removeDescription(IChromatogramDescription description);

	List<IExportConverterProcessingInfo> save(IProgressMonitor progressMonitor);

	void setFile(File file);

	void setName(String name);

	void setSuplier(ISupplier suplier);
}
