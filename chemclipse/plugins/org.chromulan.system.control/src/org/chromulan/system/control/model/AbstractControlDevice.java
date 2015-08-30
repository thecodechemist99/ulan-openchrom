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

public abstract class AbstractControlDevice implements IControlDevice {

	private String name;
	private IAnalysis analysis;
	private String contributionURI;

	public AbstractControlDevice() {

		name = "";
		contributionURI = "";
	}

	@Override
	public String getName() {

		return name;
	}

	@Override
	public void setName(String name) {

		this.name = name;
	}

	@Override
	public IAnalysis getAnalysis() {

		return analysis;
	}

	@Override
	public void setAnalysis(IAnalysis analysis) {

		this.analysis = analysis;
	}

	@Override
	public String getContributionURI() {

		return contributionURI;
	}
}
