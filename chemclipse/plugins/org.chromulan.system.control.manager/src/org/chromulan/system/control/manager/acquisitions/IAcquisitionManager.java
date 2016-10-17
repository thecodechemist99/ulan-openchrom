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
package org.chromulan.system.control.manager.acquisitions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.chromulan.system.control.events.IAcquisitionEvents;
import org.chromulan.system.control.model.IAcquisition;
import org.chromulan.system.control.model.IAcquisitionCSD;
import org.chromulan.system.control.model.IAcquisitionMSD;
import org.chromulan.system.control.model.IAcquisitionSaver;
import org.chromulan.system.control.model.IAcquisitionWSD;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.services.events.IEventBroker;

@Creatable
@Singleton
public class IAcquisitionManager {

	@Inject
	IEventBroker eventBroker;
	private ScheduledThreadPoolExecutor executor;
	private List<IAcquisitionChangeListener> changeListeners;

	public IAcquisitionManager() {
		changeListeners = new ArrayList<>();
		executor = new ScheduledThreadPoolExecutor(100);
	}

	public void addChangeListener(IAcquisitionChangeListener changeListener) {

		synchronized(changeListeners) {
			changeListeners.add(changeListener);
		}
	}

	public boolean end(IAcquisition acquisition) {

		if(acquisition != null && !acquisition.isRunning()) {
			endAcqustion(acquisition);
			return false;
		} else {
			return false;
		}
	}

	private void endAcqustion(IAcquisition acquisition) {

		for(IAcquisitionChangeListener listener : changeListeners) {
			listener.endAcquisition(acquisition);
		}
		eventBroker.post(IAcquisitionEvents.TOPIC_ACQUISITION_CHROMULAN_END, acquisition);
	}

	private void proccessDataAcqusition(IAcquisition acquisition) {

		for(IAcquisitionChangeListener listener : changeListeners) {
			listener.proccessDataAcquisition(acquisition);
		}
	}

	public void removeChangeListener(IAcquisitionChangeListener changeListener) {

		synchronized(changeListeners) {
			changeListeners.remove(changeListener);
		}
	}

	private void saveAcqusition(IAcquisition acquisition) {

		List<IChromatogram> chromatogramsToSave = new ArrayList<>();
		IAcquisitionSaver saver = null;
		if(acquisition instanceof IAcquisitionCSD) {
			IAcquisitionCSD acquisitionCSD = (IAcquisitionCSD)acquisition;
			for(IAcquisitionChangeListener listener : changeListeners) {
				List<IChromatogram> chromatograms = listener.getChromatograms(acquisitionCSD);
				if(chromatograms != null) {
					for(IChromatogram chromatogram : chromatograms) {
						if(chromatogram instanceof IChromatogramCSD) {
							IChromatogramCSD chromatogramCSD = (IChromatogramCSD)chromatogram;
							chromatogramsToSave.add(chromatogramCSD);
						}
					}
				}
			}
			saver = acquisitionCSD.getAcquisitionCSDSaver();
		} else if(acquisition instanceof IAcquisitionWSD) {
			IAcquisitionWSD acquisitionWSD = (IAcquisitionWSD)acquisition;
			for(IAcquisitionChangeListener listener : changeListeners) {
				List<IChromatogram> chromatograms = listener.getChromatograms(acquisitionWSD);
				if(chromatograms != null) {
					for(IChromatogram chromatogram : chromatograms) {
						if(chromatogram instanceof IChromatogramWSD) {
							IChromatogramWSD chromatogramWSD = (IChromatogramWSD)chromatogram;
							chromatogramsToSave.add(chromatogramWSD);
						}
					}
				}
			}
			saver = acquisitionWSD.getAcquisitionWSDSaver();
		} else if(acquisition instanceof IAcquisitionMSD) {
			IAcquisitionMSD acquisitionMSD = (IAcquisitionMSD)acquisition;
			for(IAcquisitionChangeListener listener : changeListeners) {
				List<IChromatogram> chromatograms = listener.getChromatograms(acquisitionMSD);
				if(chromatograms != null) {
					for(IChromatogram chromatogram : chromatograms) {
						if(chromatogram instanceof IChromatogramMSD) {
							IChromatogramMSD chromatogramMSD = (IChromatogramMSD)chromatogram;
							chromatogramsToSave.add(chromatogramMSD);
						}
					}
				}
			}
			saver = acquisitionMSD.getAcquisitionMSDSaver();
		}
		if(saver != null && !chromatogramsToSave.isEmpty()) {
			saver.save(new NullProgressMonitor(), chromatogramsToSave);
		}
	}

	public boolean set(IAcquisition acquisition) {

		return setAcquisition(acquisition);
	}

	private boolean setAcquisition(IAcquisition acquisition) {

		synchronized(changeListeners) {
			for(IAcquisitionChangeListener listener : changeListeners) {
				listener.setAcquisition(acquisition);
			}
			eventBroker.post(IAcquisitionEvents.TOPIC_ACQUISITION_CHROMULAN_SET, acquisition);
			return true;
		}
	}

	public boolean start(final IAcquisition acquisition) {

		synchronized(changeListeners) {
			if(acquisition != null && !acquisition.isRunning() && !acquisition.isCompleted()) {
				acquisition.start();
				for(IAcquisitionChangeListener listener : changeListeners) {
					listener.startAcquisition(acquisition);
				}
				if(acquisition.getAutoStop()) {
					executor.schedule(new Runnable() {

						@Override
						public void run() {

							stop(acquisition);
						}
					}, acquisition.getDuration(), TimeUnit.MILLISECONDS);
				}
				eventBroker.post(IAcquisitionEvents.TOPIC_ACQUISITION_CHROMULAN_START_RECORDING, acquisition);
				return true;
			} else {
				return false;
			}
		}
	}

	public boolean stop(IAcquisition acquisition) {

		return stopAndProcessAcquisition(acquisition);
	}

	private void stopAcquisition(IAcquisition acquisition) {

		acquisition.stop();
		for(IAcquisitionChangeListener listener : changeListeners) {
			listener.stopAcquisition(acquisition);
		}
		eventBroker.post(IAcquisitionEvents.TOPIC_ACQUISITION_CHROMULAN_STOP_RECORDING, acquisition);
	}

	private boolean stopAndProcessAcquisition(IAcquisition acquisition) {

		synchronized(changeListeners) {
			if((acquisition != null) && acquisition.isRunning()) {
				stopAcquisition(acquisition);
				proccessDataAcqusition(acquisition);
				saveAcqusition(acquisition);
				endAcqustion(acquisition);
				return true;
			} else {
				return false;
			}
		}
	}
}
