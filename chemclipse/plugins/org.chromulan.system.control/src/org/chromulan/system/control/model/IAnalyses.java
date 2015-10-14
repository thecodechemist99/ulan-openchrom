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

import java.util.Iterator;

public interface IAnalyses {

	final String DEFAULT_NAME = "analyses";

	String getName();

	void setName(String name);

	Iterator<IAnalysis> getAnalyses();

	void addAnalysis(IAnalysis analysis);

	void addAnalysis(IAnalysis analysis, int index);

	IAnalysis getAnalysis(int index);

	IAnalysis getActualAnalysis();

	IAnalysis setNextAnalysisActual();

	IAnalysis setActualAnalysis(IAnalysis analysis);

	boolean isActualAnalysis(IAnalysis analysis);

	boolean hasNextAnalysisActual();

	void changeAnalysis(int index1, int index2, boolean changeActualAnalysis);

	void removeAnalysis(int intex);

	int gettIndex(IAnalysis analysis);

	int getNumberAnalysis();
}
