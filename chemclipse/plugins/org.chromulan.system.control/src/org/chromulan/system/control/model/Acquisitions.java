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
package org.chromulan.system.control.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Acquisitions implements IAcquisitions {

	final private List<IAcquisition> acquisitions = new ArrayList<>();
	private IAcquisition actualAcquisition;
	private String name;

	public Acquisitions() {
		name = DEFAULT_NAME;
	}

	@Override
	public void addAcquisition(IAcquisition acquisition) {

		acquisitions.add(acquisition);
		if(actualAcquisition == null) {
			actualAcquisition = acquisition;
		}
	}

	@Override
	public void addAcquisition(IAcquisition acquisition, int index) {

		acquisitions.add(index, acquisition);
		if(actualAcquisition == null) {
			actualAcquisition = acquisition;
		}
	}

	@Override
	public IAcquisition getAcquisition(int index) {

		return acquisitions.get(index);
	}

	@Override
	public List<IAcquisition> getAcquisitions() {

		return Collections.unmodifiableList(acquisitions);
	}

	@Override
	public IAcquisition getActualAcquisition() {

		return actualAcquisition;
	}

	@Override
	public int getIndex(IAcquisition acquisition) {

		return acquisitions.indexOf(acquisition);
	}

	@Override
	public String getName() {

		return name;
	}

	@Override
	public int getNumberAcquisition() {

		return acquisitions.size();
	}

	@Override
	public boolean hasNextAcquisitionActual() {

		return (actualAcquisition != null) && !acquisitions.isEmpty() && (acquisitions.indexOf(actualAcquisition) + 1 < acquisitions.size());
	}

	@Override
	public void changeAcquisition(int index1, int index2, boolean changeActualAcquisition) {

		IAcquisition acquisition1 = acquisitions.get(index1);
		IAcquisition acquisition2 = acquisitions.get(index2);
		acquisitions.set(index2, acquisition1);
		acquisitions.set(index1, acquisition2);
		if(changeActualAcquisition) {
			if(actualAcquisition == acquisition1) {
				actualAcquisition = acquisition2;
			}
			if(actualAcquisition == acquisition2) {
				actualAcquisition = acquisition1;
			}
		}
	}

	@Override
	public boolean isActualAcquisition(IAcquisition acquisition) {

		return acquisition == this.actualAcquisition;
	}

	@Override
	public void removeAcquisition(int intex) {

		IAcquisition acquisition = acquisitions.get(intex);
		if(acquisition == actualAcquisition) {
			setNextAcquisitionActual();
		}
		acquisitions.remove(intex);
	}

	@Override
	public void removeAllAcquisitions() {

		acquisitions.clear();
		actualAcquisition = null;
	}

	@Override
	public IAcquisition setActualAcquisition(IAcquisition acquisition) {

		if(acquisitions.contains(acquisition)) {
			actualAcquisition = acquisition;
			return actualAcquisition;
		} else {
			return null;
		}
	}

	@Override
	public void setName(String name) {

		this.name = name;
	}

	@Override
	public IAcquisition setNextAcquisitionActual() {

		if(hasNextAcquisitionActual()) {
			actualAcquisition = acquisitions.get(acquisitions.indexOf(actualAcquisition) + 1);
			return actualAcquisition;
		} else {
			actualAcquisition = null;
			return null;
		}
	}
}
