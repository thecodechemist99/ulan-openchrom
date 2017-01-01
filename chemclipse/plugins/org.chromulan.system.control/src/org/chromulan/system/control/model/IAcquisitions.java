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

import java.util.List;

public interface IAcquisitions {

	final String DEFAULT_NAME = "Acquisitions";

	void addAcquisition(IAcquisition acquisition);

	void addAcquisition(IAcquisition acquisition, int index);

	IAcquisition getAcquisition(int index);

	List<IAcquisition> getAcquisitions();

	IAcquisition getActualAcquisition();

	int getIndex(IAcquisition acquisition);

	String getName();

	int getNumberAcquisition();

	boolean hasNextAcquisitionActual();

	void changeAcquisition(int index1, int index2, boolean changeActualAcquisition);

	boolean isActualAcquisition(IAcquisition acquisition);

	void removeAcquisition(int intex);

	void removeAllAcquisitions();

	IAcquisition setActualAcquisition(IAcquisition acquisition);

	void setName(String name);

	IAcquisition setNextAcquisitionActual();
}
