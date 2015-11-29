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
package org.chromulan.system.control.model.data;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractAnalysisData implements IAnalysisData {

	private String description;
	private Map<String, Object> variables;

	public AbstractAnalysisData() {

		variables = new HashMap<String, Object>();
	}

	@Override
	public String getDescription() {

		return description;
	}

	@Override
	public Object getVariable(String name) {

		return variables.get(name);
	}

	@Override
	public Map<String, Object> getVariables() {

		return variables;
	}

	@Override
	public void setDescription(String description) {

		this.description = description;
	}

	@Override
	public void setVariable(String name, Object value) {

		variables.put(name, value);
	}
}
