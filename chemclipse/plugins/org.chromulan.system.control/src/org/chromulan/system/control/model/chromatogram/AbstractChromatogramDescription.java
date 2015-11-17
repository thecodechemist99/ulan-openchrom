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

public abstract class AbstractChromatogramDescription implements IChromatogramDescription {

	private String description;
	private String name;

	@Override
	public String getDescription() {

		return description;
	}

	@Override
	public String getName() {

		return name;
	}

	@Override
	public void setDescription(String description) {

		this.description = description;
	}

	@Override
	public void setName(String name) {

		this.name = name;
	}
}
