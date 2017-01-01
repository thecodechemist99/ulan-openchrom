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
package org.chromulan.system.control.ui.acquisition.support;

import java.io.File;

import org.eclipse.core.databinding.conversion.Converter;

public class FileToString extends Converter {

	public FileToString() {
		super(File.class, String.class);
	}

	@Override
	public Object convert(Object fromObject) {

		if(fromObject instanceof File) {
			File file = (File)fromObject;
			return file.getAbsolutePath();
		}
		return null;
	}
}
