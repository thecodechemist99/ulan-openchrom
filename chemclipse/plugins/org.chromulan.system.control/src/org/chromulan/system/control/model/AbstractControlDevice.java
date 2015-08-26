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

import org.eclipse.chemclipse.model.core.IChromatogram;

public abstract class AbstractControlDevice implements IControlDevice {

	protected String name;
	protected IAnalysis analysis;
	protected String contributionURI;
	
	public AbstractControlDevice() {
		name = "";
		
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


}
