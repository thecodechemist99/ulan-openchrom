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
package org.chromulan.system.control.ui.acquisitions;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.chromulan.system.control.device.IControlDevice;
import org.chromulan.system.control.events.IAcquisitionEvents;
import org.chromulan.system.control.events.IControlDevicesEvents;
import org.chromulan.system.control.manager.acquisitions.IAcquisitionChangeListener;
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
	private IAcquisition nextAcquisition;
	private IAcquisition prepareAcquisition;
	private IAcquisition setAcquisition;
	private Object synSetNextAcqusition = new Object();

	public AcquisitionProcess() {
	}

	public void addAcquisitions(List<IAcquisition> newAcquisitions) {

		synchronized(synSetNextAcqusition) {
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
	}

	private boolean controlUsingDevices(IAcquisition acquisition) {

		for(IControlDevice device : acquisition.getDevicesProfile().getControlDevices()) {
			if(!device.isConnected() || !device.isPrepared()) {
				return false;
			}
		}
		return true;
	}

	public List<IAcquisition> getAcquisitions() {

		return acquisitions.getAcquisitions();
	}

	public IAcquisition getActualAcquisition() {

		return acquisitions.getActualAcquisition();
	}

	@PostConstruct
	private void postConstruct() {

		acquisitionManager.addChangeListener(new IAcquisitionChangeListener() {

			@Override
			public void endAcquisition(IAcquisition acquisition) {

				synchronized(synSetNextAcqusition) {
					if(acquisition == setAcquisition) {
						setAcquisition = null;
						nextAcquisition = acquisitions.setNextAcquisitionActual();
					}
				}
			}
		});
	}

	public boolean removeAcquisition(IAcquisition acquisition) {

		synchronized(synSetNextAcqusition) {
			int number = acquisitions.getIndex(acquisition);
			if(prepareAcquisition == acquisition) {
				prepareAcquisition = null;
				IAcquisition newAcquisition = acquisitions.setNextAcquisitionActual();
				acquisitions.removeAcquisition(number);
				if(newAcquisition != null) {
					setAcquisition(newAcquisition);
				}
				return true;
			}
			if(setAcquisition == acquisition) {
				boolean b = acquisitionManager.end(acquisition);
				if(b) {
					acquisitions.removeAcquisition(number);
					return true;
				} else {
					return false;
				}
			}
			acquisitions.removeAcquisition(number);
			return true;
		}
	}

	@Inject
	@Optional
	private void setAcquisition(@UIEventTopic(value = IDataSupplierEvents.TOPIC_DATA_UPDATE_DEVICES) DataSupplier supplier) {

		synchronized(synSetNextAcqusition) {
			if(prepareAcquisition != null) {
				setAcquisition(prepareAcquisition);
			}
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

	public boolean setNextAcquisition() {

		synchronized(synSetNextAcqusition) {
			if(setAcquisition != null) {
				return acquisitionManager.end(setAcquisition);
			} else {
				prepareAcquisition = null;
				IAcquisition nextAcqusution = acquisitions.setNextAcquisitionActual();
				if(nextAcqusution != null) {
					setAcquisition(nextAcqusution);
					return true;
				} else {
					return false;
				}
			}
		}
	}

	@Inject
	@Optional
	private void setNextAcqusitionAsActual(@UIEventTopic(value = IAcquisitionEvents.TOPIC_ACQUISITION_CHROMULAN_END) IAcquisition acquisition) {

		synchronized(synSetNextAcqusition) {
			if(nextAcquisition == acquisitions.getActualAcquisition()) {
				setAcquisition(nextAcquisition);
			}
			nextAcquisition = null;
		}
	}

	public boolean startAcquisition() {

		synchronized(synSetNextAcqusition) {
			if(setAcquisition != null) {
				return acquisitionManager.start(setAcquisition);
			}
			return false;
		}
	}

	public boolean stopAcquisition(boolean changeDuration) {

		synchronized(synSetNextAcqusition) {
			if(setAcquisition != null) {
				boolean b = acquisitionManager.stop(setAcquisition, changeDuration);
				return b;
			}
			return false;
		}
	}
}
