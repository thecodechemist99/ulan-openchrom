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

public interface IAnalyses {

	final String DEFAULT_NAME = "analyses";

	void addAnalysis(IAnalysis analysis);

	void addAnalysis(IAnalysis analysis, int index);

	IAnalysis getActualAnalysis();

	List<IAnalysis> getAnalyses();

	IAnalysis getAnalysis(int index);

	String getName();

	int getNumberAnalysis();

	int gettIndex(IAnalysis analysis);

	boolean hasNextAnalysisActual();

	void changeAnalysis(int index1, int index2, boolean changeActualAnalysis);

	boolean isActualAnalysis(IAnalysis analysis);

	void removeAnalysis(int intex);

	IAnalysis setActualAnalysis(IAnalysis analysis);

	void setName(String name);

	IAnalysis setNextAnalysisActual();
}
