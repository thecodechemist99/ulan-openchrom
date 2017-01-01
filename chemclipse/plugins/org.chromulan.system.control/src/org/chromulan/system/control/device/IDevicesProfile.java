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
package org.chromulan.system.control.device;

import java.util.List;

import org.chromulan.system.control.device.setting.IDeviceSetting;
import org.chromulan.system.control.model.IAcquisition;

public interface IDevicesProfile extends IControlDevices {

	void addAcquisition(IAcquisition acquisition);

	void addDeviceSetting(IDeviceSetting deviceSetting);

	boolean containsAcqusition();

	boolean containsAcqusition(IAcquisition acquisition);

	boolean containsDeviceSetting(String pluginID, String deviceID);

	List<IAcquisition> getAcquisitions();

	List<IDeviceSetting> getDeviceSetting();

	IDeviceSetting getDeviceSetting(String pluginID, String deviceID);

	String getName();

	void removeAcqusition(IAcquisition acquisition);

	void removeDeviceSetting(String pluginID, String deviceID);

	void setName(String name);
}
