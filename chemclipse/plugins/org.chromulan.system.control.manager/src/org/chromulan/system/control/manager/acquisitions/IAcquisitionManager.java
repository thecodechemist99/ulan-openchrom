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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
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
import org.chromulan.system.control.model.SaveChromatogram;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.services.events.IEventBroker;

@Creatable
@Singleton
public class IAcquisitionManager {

	private HashSet<IAcquisition> acquisitions = new HashSet<>();
	@Inject
	private IEventBroker eventBroker;
	private ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(100);;
	private List<IAcquisitionChangeListener> changeListeners = new ArrayList<>();
	private HashMap<IAcquisition, ScheduledFuture<?>> scheduleStopAcquisition = new HashMap<>();

	public IAcquisitionManager() {
	}

	public void addChangeListener(IAcquisitionChangeListener changeListener) {

		synchronized(changeListeners) {
			changeListeners.add(changeListener);
		}
	}

	public boolean end(IAcquisition acquisition) {

		synchronized(changeListeners) {
			if(acquisition != null && !acquisition.isRunning() && acquisitions.remove(acquisition)) {
				endAcqustion(acquisition);
				return true;
			} else {
				return false;
			}
		}
	}

	private void endAcqustion(IAcquisition acquisition) {

		for(IAcquisitionChangeListener listener : changeListeners) {
			listener.endAcquisition(acquisition);
		}
		eventBroker.post(IAcquisitionEvents.TOPIC_ACQUISITION_CHROMULAN_END, acquisition);
		acquisitions.remove(acquisition);
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

		List<SaveChromatogram> chromatogramsToSave = new ArrayList<>();
		IAcquisitionSaver saver = null;
		if(acquisition instanceof IAcquisitionCSD) {
			IAcquisitionCSD acquisitionCSD = (IAcquisitionCSD)acquisition;
			for(IAcquisitionChangeListener listener : changeListeners) {
				List<SaveChromatogram> chromatograms = listener.getChromatograms(acquisitionCSD);
				if(chromatograms != null) {
					for(SaveChromatogram saveChromatogram : chromatograms) {
						if(saveChromatogram.getChromatogram() instanceof IChromatogramCSD) {
							chromatogramsToSave.add(saveChromatogram);
						}
					}
				}
			}
			saver = acquisitionCSD.getAcquisitionCSDSaver();
		} else if(acquisition instanceof IAcquisitionWSD) {
			IAcquisitionWSD acquisitionWSD = (IAcquisitionWSD)acquisition;
			for(IAcquisitionChangeListener listener : changeListeners) {
				List<SaveChromatogram> chromatograms = listener.getChromatograms(acquisitionWSD);
				if(chromatograms != null) {
					for(SaveChromatogram saveChromatogram : chromatograms) {
						if(saveChromatogram.getChromatogram() instanceof IChromatogramWSD) {
							chromatogramsToSave.add(saveChromatogram);
						}
					}
				}
			}
			saver = acquisitionWSD.getAcquisitionWSDSaver();
		} else if(acquisition instanceof IAcquisitionMSD) {
			IAcquisitionMSD acquisitionMSD = (IAcquisitionMSD)acquisition;
			for(IAcquisitionChangeListener listener : changeListeners) {
				List<SaveChromatogram> chromatograms = listener.getChromatograms(acquisitionMSD);
				if(chromatograms != null) {
					for(SaveChromatogram saveChromatogram : chromatograms) {
						if(saveChromatogram.getChromatogram() instanceof IChromatogramMSD) {
							chromatogramsToSave.add(saveChromatogram);
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

		synchronized(changeListeners) {
			if(acquisitions.add(acquisition)) {
				return setAcquisition(acquisition);
			} else {
				return false;
			}
		}
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
			if(acquisition != null && !acquisition.isRunning() && !acquisition.isCompleted() && acquisitions.contains(acquisition)) {
				acquisition.start();
				for(IAcquisitionChangeListener listener : changeListeners) {
					listener.startAcquisition(acquisition);
				}
				if(acquisition.getAutoStop()) {
					final ScheduledFuture<?> future = executor.schedule(new Runnable() {

						@Override
						public void run() {

							synchronized(changeListeners) {
								ScheduledFuture<?> future = scheduleStopAcquisition.get(acquisition);
								if(!future.isCancelled()) {
									stopAndProcessAcquisition(acquisition, false);
								}
							}
						}
					}, acquisition.getDuration(), TimeUnit.MILLISECONDS);
					scheduleStopAcquisition.put(acquisition, future);
				}
				eventBroker.post(IAcquisitionEvents.TOPIC_ACQUISITION_CHROMULAN_START_RECORDING, acquisition);
				return true;
			} else {
				return false;
			}
		}
	}

	public boolean stop(IAcquisition acquisition, boolean changeDuaration) {

		synchronized(changeListeners) {
			ScheduledFuture<?> future = scheduleStopAcquisition.get(acquisition);
			if(future != null) {
				future.cancel(false);
			}
			return stopAndProcessAcquisition(acquisition, changeDuaration);
		}
	}

	private void stopAcquisition(IAcquisition acquisition, boolean changeDuaration) {

		acquisition.stop(changeDuaration);
		for(IAcquisitionChangeListener listener : changeListeners) {
			listener.stopAcquisition(acquisition);
		}
		eventBroker.post(IAcquisitionEvents.TOPIC_ACQUISITION_CHROMULAN_STOP_RECORDING, acquisition);
	}

	private boolean stopAndProcessAcquisition(IAcquisition acquisition, boolean changeDuaration) {

		synchronized(changeListeners) {
			if((acquisition != null) && acquisition.isRunning() && acquisitions.contains(acquisition)) {
				stopAcquisition(acquisition, changeDuaration);
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
