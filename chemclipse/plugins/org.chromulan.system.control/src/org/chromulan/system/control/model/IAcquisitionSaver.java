/*******************************************************************************
 * Copyright (c) 2015, 2016 Jan Holy.
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

import org.chromulan.system.control.model.data.IDetectorData;
import org.eclipse.chemclipse.converter.core.ISupplier;
import org.eclipse.chemclipse.converter.processing.chromatogram.IChromatogramExportConverterProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IAcquisitionSaver {

	void addDetectorData(IDetectorData detectorData);

	IAcquisition getAcquisition();

	List<IDetectorData> getDetectorsData();

	File getFile();

	List<IChromatogramExportConverterProcessingInfo> getChromatogramExportConverterProcessInfo();

	ISupplier getSupplier();

	void removeAllDetectorData();

	void removeDetectorData(IDetectorData detectorData);

	List<IChromatogramExportConverterProcessingInfo> save(IProgressMonitor progressMonitor);

	void setFile(File file);

	void setSuplier(ISupplier suplier);
}
