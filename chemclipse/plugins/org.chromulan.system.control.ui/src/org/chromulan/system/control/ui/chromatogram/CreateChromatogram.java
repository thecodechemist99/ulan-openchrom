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
package org.chromulan.system.control.ui.chromatogram;

import javax.inject.Inject;

import org.chromulan.system.control.events.IAcquisitionUIEvents;
import org.chromulan.system.control.model.IChromatogramCSDAcquisition;
import org.chromulan.system.control.model.IChromatogramWSDAcquisition;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

public class CreateChromatogram {

	@Inject
	private MApplication application;
	@Inject
	private EModelService modelService;
	@Inject
	private EPartService partService;

	@Inject
	@Optional
	public void displayChromatogamCSD(@UIEventTopic(value = IAcquisitionUIEvents.TOPIC_ACQUISITION_CHROMULAN_UI_CHROMATOGRAM_DISPLAY) IChromatogramCSDAcquisition chromatogram) {

		if(chromatogram == null) {
			return;
		}
		MPartStack stack = (MPartStack)modelService.find("org.chromulan.system.control.ui.partstack.2", application);
		if(stack == null) {
			return;
		}
		MPart part = partService.createPart("org.chromulan.system.control.ui.ChromatogramCSDRecording");
		part.setLabel(chromatogram.getName());
		part.setObject(chromatogram);
		stack.getChildren().add(part);
		partService.activate(part);
	}

	@Inject
	@Optional
	public void displayChromatogamCSD(@UIEventTopic(value = IAcquisitionUIEvents.TOPIC_ACQUISITION_CHROMULAN_UI_CHROMATOGRAM_DISPLAY) IChromatogramWSDAcquisition chromatogram) {

		if(chromatogram == null) {
			return;
		}
		MPartStack stack = (MPartStack)modelService.find("org.chromulan.system.control.ui.partstack.2", application);
		if(stack == null) {
			return;
		}
		MPart part = partService.createPart("org.chromulan.system.control.ui.ChromatogramWSDRecording");
		part.setLabel(chromatogram.getName());
		part.setObject(chromatogram);
		stack.getChildren().add(part);
		partService.activate(part);
	}
}
