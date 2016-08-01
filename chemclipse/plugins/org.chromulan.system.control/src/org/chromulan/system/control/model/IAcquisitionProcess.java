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

public interface IAcquisitionProcess {

	IAcquisitionSaver getAcquisitionSaver();

	IDevicesProfile getDevicesProfile();

	boolean isCompleted();

	boolean isRunning();

	void setAcquisitionSaver(IAcquisitionSaver saver);

	void setDevicesProfile(IDevicesProfile devicesProfile);

	void start();

	void stop();
}
