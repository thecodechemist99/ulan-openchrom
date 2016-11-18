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

import java.util.List;

import javax.inject.Inject;

import org.chromulan.system.control.device.IControlDevice;
import org.chromulan.system.control.events.IControlDevicesEvents;
import org.chromulan.system.control.manager.acquisitions.IAcquisitionManager;
import org.chromulan.system.control.manager.devices.DataSupplier;
import org.chromulan.system.control.manager.events.IDataSupplierEvents;
import org.chromulan.system.control.model.Acquisitions;
import org.chromulan.system.control.model.IAcquisition;
import org.chromulan.system.control.model.IAcquisitions;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;

@Creatable
public class AcquisitionProcess {

	@Inject
	private IAcquisitionManager acquisitionManager;
	private IAcquisitions acquisitions = new Acquisitions();;
	@Inject
	private IEventBroker eventBroker;
	private IAcquisition prepareAcquisition;
	private IAcquisition setAcquisition;

	public AcquisitionProcess() {
	}

	public void addAcquisitions(List<IAcquisition> newAcquisitions) {

		if(acquisitions.getActualAcquisition() == null) {
			for(IAcquisition acquisition : newAcquisitions) {
				acquisitions.addAcquisition(acquisition);
			}
			if(acquisitions.getActualAcquisition() != null) {
				setAcquisition(acquisitions.getActualAcquisition());
			}
		} else {
			for(IAcquisition acquisition : newAcquisitions) {
				acquisitions.addAcquisition(acquisition);
			}
		}
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

		if(setAcquisition != null) {
			boolean b = acquisitionManager.end(setAcquisition);
			if(b) {
				setAcquisition = null;
			}
			return b;
		} else {
			prepareAcquisition = null;
		}
		return true;
	}

	public List<IAcquisition> getAcquisitions() {

		return acquisitions.getAcquisitions();
	}

	public IAcquisition getActualAcquisition() {

		return acquisitions.getActualAcquisition();
	}

	public boolean isSetAcquisition() {

		if(setAcquisition != null) {
			return true;
		} else {
			return false;
		}
	}

	public boolean removeAcquisition(IAcquisition acquisition) {

		int number = acquisitions.getIndex(acquisition);
		boolean remove = false;
		if(setAcquisition == acquisition || prepareAcquisition == acquisition) {
			if(endAcquisition()) {
				IAcquisition newAcquisition = acquisitions.setNextAcquisitionActual();
				acquisitions.removeAcquisition(number);
				setAcquisition(newAcquisition);
				remove = true;
			}
		} else {
			acquisitions.removeAcquisition(number);
			remove = true;
		}
		return remove;
	}

	@Inject
	@Optional
	private void setAcquisition(@UIEventTopic(value = IDataSupplierEvents.TOPIC_DATA_UPDATE_DEVICES) DataSupplier supplier) {

		if(prepareAcquisition != null) {
			setAcquisition(prepareAcquisition);
		}
	}

	private int setAcquisition(IAcquisition acquisition) {

		if(setAcquisition == null && acquisition != null) {
			boolean b = controlUsingDevices(acquisition);
			if(b == true) {
				acquisitionManager.set(acquisition);
				setAcquisition = acquisition;
				prepareAcquisition = null;
				return 0;
			} else {
				prepareAcquisition = acquisition;
				eventBroker.post(IControlDevicesEvents.TOPIC_CONTROL_DEVICES_CONTROL, acquisition.getDevicesProfile().getControlDevices());
				return 2;
			}
		}
		return 1;
	}

	public IAcquisition setNextAcquisition() {

		IAcquisition acquisition = null;
		boolean b = endAcquisition();
		if(b && (acquisition = acquisitions.setNextAcquisitionActual()) != null) {
			setAcquisition(acquisition);
		}
		return acquisition;
	}

	public boolean startAcquisition() {

		if(setAcquisition != null) {
			return acquisitionManager.start(setAcquisition);
		}
		return false;
	}

	public boolean stopAcquisition(boolean changeDuration) {

		if(setAcquisition != null) {
			boolean b = acquisitionManager.stop(setAcquisition, changeDuration);
			if(b) {
				setAcquisition = null;
			}
			return b;
		}
		return false;
	}
}
