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

import org.eclipse.chemclipse.converter.core.ISupplier;
import org.eclipse.chemclipse.model.core.IChromatogram;

public interface IChromatogramMaker {

	static String fileValidation(String file) {

		String nfile = file.trim();
		if(nfile.isEmpty()) {
			return "Chromatogram";
		}
		nfile.replace('<', '_');
		nfile.replace('>', '_');
		nfile.replace(':', '_');
		nfile.replace('/', '_');
		nfile.replace('\\', '_');
		nfile.replace('|', '_');
		nfile.replace('?', '_');
		nfile.replace('*', '_');
		return nfile;
	}

	List<IChromatogram> getChromatograms(String path, ISupplier supplier);
}
