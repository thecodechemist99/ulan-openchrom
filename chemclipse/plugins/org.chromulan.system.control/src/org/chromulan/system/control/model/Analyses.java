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


import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;



public class Analyses implements IAnalyses {

	List<IAnalysis> analyses;
	String name;
	IAnalysis actualAnalysis;
	
	
	public Analyses() {
		analyses = new LinkedList<IAnalysis>();
		name = DEFAULT_NAME;
		actualAnalysis = null;
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
	public Iterator<IAnalysis> getAnalyses() {

		return Collections.unmodifiableList(analyses).iterator()   ;
	}

	@Override
	public void addAnalysis(IAnalysis analysis) {
		
		analyses.add(analysis);
		if(actualAnalysis == null)
		{
			actualAnalysis = analysis;
		}
		
		
		
	}

	@Override
	public void addAnalysis(IAnalysis analysis, int index) {
		
		analyses.add(index, analysis);
		if(actualAnalysis == null)
		{
			actualAnalysis = analysis;
		}
		
	}

	@Override
	public IAnalysis getAnalysis(int index) {
		return analyses.get(index);
	}

	@Override
	public IAnalysis getActualAnalysis() {

		return actualAnalysis;
	}

	@Override
	public IAnalysis setNextAnalysisActual() {
		if(hasNextAnalysisActual())
		{
			actualAnalysis = analyses.get(analyses.indexOf(actualAnalysis)+1);
			return actualAnalysis;
		}
		else 
		{
			actualAnalysis = null;
			return null;
		}
		
	}

	@Override
	public IAnalysis setActualAnalysis(IAnalysis analysis) {
		if(analyses.contains(analysis))
		{
			actualAnalysis = analysis;
			return actualAnalysis;
		} else 
		{
			return null;
		}
		
		
	}
	
	
	@Override
	public boolean hasNextAnalysisActual() {

		return (actualAnalysis != null) && !analyses.isEmpty() && (analyses.indexOf(actualAnalysis)+1 < analyses.size());
	}

	@Override
	public void changeAnalysis(int index1, int index2, boolean changeActualAnalysis) {
		IAnalysis analysis1 = analyses.get(index1);
		IAnalysis analysis2 = analyses.get(index2);
		analyses.set(index2, analysis1);
		analyses.set(index1, analysis2);
		
		
		if(changeActualAnalysis)
		{
			if(actualAnalysis == analysis1)
			{
				actualAnalysis = analysis2;
			}
			
			if(actualAnalysis == analysis2)
			{
				actualAnalysis = analysis1;
			}
		}
	
	}

	@Override
	public void removeAnalysis(int intex) {
		IAnalysis analysis = analyses.get(intex);
		
		
		if(analysis == actualAnalysis)
		{
			setNextAnalysisActual();
		}
		
		analyses.remove(intex);
	}

	@Override
	public int gettIndex(IAnalysis analysis) {

		return analyses.indexOf(analysis);
	}

	@Override
	public int getNumberAnalysis() {

		return analyses.size();
	}

	@Override
	public boolean isActualAnalysis(IAnalysis analysis) {

		return analysis == this.actualAnalysis;
	}

	
}
