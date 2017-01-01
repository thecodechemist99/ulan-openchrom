/*******************************************************************************
 * Copyright (c) 2015, 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.chromulan.system.control.device;

import java.io.Externalizable;

import org.chromulan.system.control.device.setting.IDeviceSetting;

public interface IControlDevice extends Externalizable {

	final long FLG_SUPPORT_CSD_CHROMATOGRAM = 0b1;
	final long FLG_SUPPORT_MSD_CHROMATOGRAM = 0b10;
	final long FLG_SUPPORT_WSD_CHROMATOGRAM = 0b100;

	String getCompany();

	String getDescription();

	String getDeviceID();

	IDeviceSetting getDeviceSetting();

	long getFlg();

	String getModel();

	String getName();

	String getPluginID();

	boolean isConnected();

	boolean isPrepared();

	void setDeviceSetting(IDeviceSetting deviceSetting);
}