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

import java.util.Map;

public interface IAnalysisData {

	String getDescription();

	String getName();

	Object getVariable(String name);

	Map<String, Object> getVariables();

	void setDescription(String description);

	void setVariable(String name, Object value);
}
