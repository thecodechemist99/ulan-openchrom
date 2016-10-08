/*******************************************************************************
 * Copyright (c) 2015, 2016 Jan Holy.
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

public class AcquisitionsCSD extends AbstractAcquisitions implements IAcquisitionsCSD {

	@Override
	public IAcquisitionCSD getAcquisitionCSD(int index) {

		IAcquisition acquisition = getAcquisition(index);
		if (acquisition instanceof IAcquisitionCSD) {
			return (IAcquisitionCSD) acquisition;
		}
		return null;
	}

	@Override
	public IAcquisitionCSD getActualAcquisitionCSD() {

		IAcquisition acquisition = getActualAcquisition();
		if (acquisition instanceof IAcquisitionCSD) {
			return (IAcquisitionCSD) acquisition;
		}
		return null;
	}
}
