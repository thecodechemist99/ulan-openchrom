/*******************************************************************************
 * Copyright (c) 2015 Jan Holy.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Jan Holy - initial API and implementation
*******************************************************************************/
package org.chromulan.system.control.ui.commands;

import javax.inject.Inject;

import org.chromulan.system.control.model.AbstractAnalysis;
import org.chromulan.system.control.model.IAnalysis;
import org.chromulan.system.control.ui.wizard.WizardNewAnalysis;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MBasicFactory;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Composite;

public class NewAnalysis {
	
	

	@Inject
	protected  EModelService modelService;
	@Inject
	protected  EPartService partService;
	@Inject
	protected  MApplication application;
	/*
	 * @Inject
	 * protected static IEclipseContext context;
	 */
	

	@Execute
	public void execute(Composite parent) {

		WizardNewAnalysis newAnalysisWizard = new WizardNewAnalysis();
		WizardDialog wizardDialog = new WizardDialog(parent.getShell(), newAnalysisWizard);
		if(wizardDialog.open() == Window.OK) {
			String analysisName = newAnalysisWizard.getAnalysisName();
			IAnalysis analysis = new AbstractAnalysis() {
			};
			analysis.setName(analysisName);
	
			
			MPerspective perspectiveChromulan = (MPerspective)modelService.find("org.chromulan.system.control.ui.perspective.chromulan", application);
			if(perspectiveChromulan != null) {
				
				partService.switchPerspective(perspectiveChromulan);
				
				
				MPart part = MBasicFactory.INSTANCE.createPart();
				part.setLabel(analysis.getName());
				part.setCloseable(true);
				part.setElementId("AvailableDevicesPart");
				part.setContributionURI("bundleclass://org.chromulan.system.control.ui/org.chromulan.system.control.ui.parts.AvailableDevicesPart");
				part.setObject(analysis);
				MPartStack stack = (MPartStack)modelService.find("org.chromulan.system.control.ui.partstack.0", application);
				stack.getChildren().add(part);
				partService.activate(part);
	
			}
		}
		
	}
	
}

