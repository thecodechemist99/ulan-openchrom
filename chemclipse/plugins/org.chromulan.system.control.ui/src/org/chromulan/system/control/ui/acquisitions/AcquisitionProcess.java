/*******************************************************************************
 * Copyright (c) 2016 Jan Holy.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.chromulan.system.control.ui.acquisitions;

import javax.inject.Inject;

import org.chromulan.system.control.device.IControlDevice;
import org.chromulan.system.control.events.IControlDevicesEvents;
import org.chromulan.system.control.manager.acquisitions.IAcquisitionManager;
import org.chromulan.system.control.manager.devices.DataSupplier;
import org.chromulan.system.control.manager.events.IDataSupplierEvents;
import org.chromulan.system.control.model.IAcquisition;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;

public class AcquisitionProcess {

	@Inject
	private IAcquisitionManager acquisitionManager;
	@Inject
	private IEventBroker eventBroker;
	private IAcquisition prepareAcqisition;
	private IAcquisition setAcqusition;

	public AcquisitionProcess() {
	}

	private boolean controlUsingDevices(IAcquisition acquisition) {

		for(IControlDevice device : acquisition.getDevicesProfile().getControlDevices()) {
			if(!device.isConnected() || !device.isPrepared()) {
				return false;
			}
		}
		return true;
	}

	public boolean endAcquisition() {

		if(setAcqusition != null) {
			return acquisitionManager.end(setAcqusition);
		}
		return false;
	}

	public IAcquisition getAcqisition() {

		return prepareAcqisition;
	}

	public boolean isSetAcquisition() {

		if(setAcqusition != null) {
			return true;
		} else {
			return false;
		}
	}

	@Inject
	@Optional
	private void setAcquisition(@UIEventTopic(value = IDataSupplierEvents.TOPIC_DATA_UPDATE_DEVICES) DataSupplier supplier) {

		if(prepareAcqisition != null) {
			setAcquisition(prepareAcqisition);
		}
	}

	public int setAcquisition(IAcquisition acquisition) {

		if(setAcqusition == null && acquisition != null) {
			boolean b = controlUsingDevices(acquisition);
			if(b == true) {
				acquisitionManager.set(acquisition);
				setAcqusition = acquisition;
				prepareAcqisition = null;
				return 0;
			} else {
				prepareAcqisition = acquisition;
				eventBroker.post(IControlDevicesEvents.TOPIC_CONTROL_DEVICES_CONTROL, acquisition.getDevicesProfile().getControlDevices());
				return 2;
			}
		}
		return 1;
	}

	public boolean startAcquisition() {

		if(setAcqusition != null) {
			return acquisitionManager.start(setAcqusition);
		}
		return false;
	}

	public boolean stopAcquisition() {

		if(setAcqusition != null) {
			return acquisitionManager.stop(setAcqusition);
		}
		return false;
	}
}
