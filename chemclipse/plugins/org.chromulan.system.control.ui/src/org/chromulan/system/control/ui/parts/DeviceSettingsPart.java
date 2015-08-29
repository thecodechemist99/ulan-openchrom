/*******************************************************************************
 * Copyright (c) 2015 Jan Holy, Dr. Philip Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Jan Holy - initial API and implementation
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.chromulan.system.control.ui.parts;


import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.chromulan.system.control.model.IAnalysis;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.chromulan.system.control.model.IControlDevice;
import org.chromulan.system.control.model.chromatogram.ChromatogramRecording;
import org.chromulan.system.control.ui.events.IAnalysisUIEvents;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class DeviceSettingsPart {

	@Inject
	private Composite parent;
	
	@Inject 
	MPart part;
	
	@Inject
	private IEventBroker eventBroker;

	@PostConstruct
	public void createPartControl() {

		part.setLabel("Hokus Pokus");
		parent.setLayout(new FillLayout());
		Button button = new Button(parent, SWT.PUSH);
		button.setText("Add Chromatogtam");
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				eventBroker.post(IAnalysisUIEvents.TOPIC_ANALYSIS_CHROMULAN_UI_CHROMATOGRAM_DISPLAY, new IControlDevice() {

					@Override
					public String getName() {

						return null;
					}

					@Override
					public void setName(String name) {

					}

					@Override
					public boolean hasChromatogram() {

						return true;
					}

					@Override
					public ChromatogramRecording getChromatogram() throws UnsupportedOperationException {
					
						return null;
					}
					
					@Override
					public void setChromatogram(ChromatogramRecording chromatogram) throws UnsupportedOperationException {
					
					}

					@Override
					public void setAnalysis(IAnalysis analysis) {

					}

					@Override
					public IAnalysis getAnalysis() {

						return null;
					}

					@Override
					public boolean isPrepare() {

						return false;
					}

					@Override
					public String getContributionURI() {

						return null;
					}

					
				
				});				
			}	
		});
	}
}
