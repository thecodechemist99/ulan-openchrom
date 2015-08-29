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
package org.chromulan.system.control.ui.parts;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.chromulan.system.control.events.IAnalysisEvents;
import org.chromulan.system.control.model.IAnalysis;
import org.chromulan.system.control.model.IControlDevice;
import org.chromulan.system.control.model.IControlDevices;
import org.chromulan.system.control.ui.events.IAnalysisUIEvents;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MBasicFactory;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;


public class AnalysisPart {
	

	@Inject
	protected Composite parent;
	
	@Inject
	protected IEclipseContext context;
	
	@Inject
	protected IEventBroker eventBroker;
	
	@Inject
	protected EModelService modelService;
	
	@Inject
	protected EPartService partService;
	
	@Inject
	protected MApplication aplication;
	
	
	@Inject
	protected IExtensionRegistry registry;
	
	@Inject 
	protected MPart part;
	
	@Inject
	protected MPerspective perspective;
	
	protected IAnalysis analysis;
	
	private Label lableNameAnalysis;
	private boolean isCloseAnalysis;
	
	
	private Button buttonStart;
	private Button buttonStop;
	private Button buttonEnd;
	private Button buttonSave;
	
	
	
	protected List<IControlDevice> deviceList; 
	protected List<IControlDevices> devicePlugins;
	
	
	
	@PostConstruct
	public void createPartControl() {
		
		loadExtension();
		analysis = (IAnalysis)part.getObject();

		isCloseAnalysis = false;

		
		GridLayout gridLayout = new GridLayout(2,false);
		
		parent.setLayout(gridLayout);
		
		lableNameAnalysis = new Label(parent, SWT.LEFT);
		
		if(analysis != null)
		{
			lableNameAnalysis.setText(analysis.getName());
		}
		
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, false); 
		gridData.horizontalSpan = 2;
		lableNameAnalysis.setLayoutData(gridData);
		
		

	    gridData = new GridData(GridData.FILL, GridData.FILL, true, true); 
	    gridData.horizontalSpan = 2;


		

		
		buttonStart = new Button(parent, SWT.PUSH);
		buttonStart.setText("Start Analysis");
		
		buttonStart.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				  startAnalysis(true);
			  }
		}); 
		gridData = new GridData(GridData.FILL, GridData.FILL, true, false); 
		buttonStart.setLayoutData(gridData);
		
		
		
		
		buttonStop = new Button(parent, SWT.PUSH);
		buttonStop.setText("Stop Analysis");
		
		buttonStop.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				  stopAnalysis(true);
			  }
		});
		gridData = new GridData(GridData.FILL, GridData.FILL, true, false); 
		buttonStop.setLayoutData(gridData);
		
	
		
		buttonEnd = new Button(parent, SWT.PUSH);
		buttonEnd.setText("End Analysis");
		
		buttonEnd.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				  endAnalysis(true);
			  }
		});
		gridData = new GridData(GridData.FILL, GridData.FILL, true, false); 
		buttonEnd.setLayoutData(gridData);
		
		
		
		buttonSave = new Button(parent, SWT.PUSH);
		buttonSave.setText("Save");
		
		buttonSave.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				exportChromatogram();
			  }
		});
		gridData = new GridData(GridData.FILL, GridData.FILL, true, false); 
		buttonSave.setLayoutData(gridData);
		
		initializationButtons();
		

	}
	
	@PreDestroy
	public void preDestroy()
	{
		if(analysis != null)
		{
		if(analysis.isRecerding())
		{
			stopAnalysis(false);
			endAnalysis(false); 
		}
		else
		{
			if(!isCloseAnalysis)
			{
				endAnalysis(false);
			}
			
		}
		}
	}
	
	
	
	
	@Inject
	@Optional
	public void disableButtons(@UIEventTopic(value=IAnalysisEvents.TOPIC_ANALYSIS_CHROMULAN_START) IAnalysis analysis)
	{
		if(this.analysis != analysis)
		{
			buttonStart.setEnabled(false);
			buttonStop.setEnabled(false);
			buttonEnd.setEnabled(false);
			buttonSave.setEnabled(false);	
		}
	}
	
	@Inject
	@Optional
	public void enableButtons(@UIEventTopic(value=IAnalysisEvents.TOPIC_ANALYSIS_CHROMULAN_END) IAnalysis analysis)
	{
		if((this.analysis != analysis) && !isCloseAnalysis)
		{
			initializationButtons();	
		}
	}

	

	
	protected void initializationButtons()
	{
		if(!perspective.getContext().containsKey(IAnalysis.class))
		{
			buttonStart.setEnabled(true);
			buttonStop.setEnabled(false);
			buttonEnd.setEnabled(true);
			buttonSave.setEnabled(false);
		}
		else
		{
			buttonStart.setEnabled(false);
			buttonStop.setEnabled(false);
			buttonEnd.setEnabled(false);
			buttonSave.setEnabled(false);
		}
		
	}
	
	protected boolean startAnalysis(boolean setControl)
	{
		if((analysis != null)&&(!analysis.isRecerding())&& !isCloseAnalysis)
		{
			if(setControl)
			{
				buttonStart.setEnabled(false);
				buttonStop.setEnabled(true);
				buttonEnd.setEnabled(false);
				buttonSave.setEnabled(false);
			}
			
			
			
			
			analysis.setRecording(true);
			perspective.getContext().set(IAnalysis.class, analysis);
			
			eventBroker.post(IAnalysisEvents.TOPIC_ANALYSIS_CHROMULAN_START, analysis);
			
			
			
			
			return true;
		} else
		{
			return false;
		}
	}
	
	protected boolean stopAnalysis(boolean setControl)
	{
		if((analysis != null)&&(analysis.isRecerding())&& !isCloseAnalysis){
			
			if(setControl)
			{
				buttonStart.setEnabled(false);
				buttonStop.setEnabled(false);
				buttonEnd.setEnabled(true);
				buttonSave.setEnabled(true);
			}
			
			
			
			analysis.setRecording(false);
			eventBroker.post(IAnalysisEvents.TOPIC_ANALYSIS_CHROMULAN_STOP, analysis);
			return true;
		}
		else
		{
			return false;
		}
	}
	
	protected boolean exportChromatogram()
	{
		if((analysis != null) && (!analysis.isRecerding()) && !isCloseAnalysis)
		{	
			IChromatogram chromatogram = null;
			for(IControlDevice iControlDevice : deviceList) {
				if((iControlDevice.getAnalysis() == analysis) && (iControlDevice.hasChromatogram()))
				{
						chromatogram = iControlDevice.getChromatogram().getChromatogram();
				
					
				}
			}
			
			return true;
		}
		else
		{
			return false;
		}
	}
	
	protected boolean endAnalysis(boolean setControl)
	{
		
		if((analysis != null) &&(!analysis.isRecerding())&& !isCloseAnalysis)
		{
			
			if(setControl) 
			{
				buttonStart.setEnabled(false);
				buttonStop.setEnabled(false);
				buttonEnd.setEnabled(false);
				buttonSave.setEnabled(false);
			}
	
			perspective.getContext().remove(IAnalysis.class);
			eventBroker.post(IAnalysisEvents.TOPIC_ANALYSIS_CHROMULAN_END, analysis);
			isCloseAnalysis = true;
			
			return true;
		}
		else
		{
			return false;
		}
	}
	
	protected void loadExtension()
	{
		devicePlugins = new LinkedList<IControlDevices>();
		IConfigurationElement[] config = registry.getConfigurationElementsFor("org.chromulan.system.control.ui");
		
		for(IConfigurationElement elem : config) {
			
			try {
				IControlDevices controlDevices= (IControlDevices) elem.createExecutableExtension("Control device");
				devicePlugins.add(controlDevices);
				
			} catch(CoreException e) {
			}
			
			
		}
	}
	
	
	
	
}
