/*******************************************************************************
 * Copyright (c) 2016, 2017 Jan Holy.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.chromulan.system.control.manager.acquisitions;

import java.util.List;

import org.chromulan.system.control.model.IAcquisition;
import org.chromulan.system.control.model.SaveChromatogram;

public interface IAcquisitionChangeListener {

	default void endAcquisition(IAcquisition acquisition) {

	};

	default List<SaveChromatogram> getChromatograms(IAcquisition acquisition) {

		return null;
	};

	default void proccessDataAcquisition(IAcquisition acquisition) {

	};

	default void setAcquisition(IAcquisition acquisition) {

	};

	default void startAcquisition(IAcquisition acquisition) {

	};

	default void stopAcquisition(IAcquisition acquisition) {

	};
}
