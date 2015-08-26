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

import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.chromulan.system.control.events.IAnalysisEvents;
import org.chromulan.system.control.model.IAnalysis;
import org.chromulan.system.control.model.IControlDevice;
import org.chromulan.system.control.model.IControlDevices;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
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

public class AvailableDevicesPart {

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
	
	protected IAnalysis analysis;
	
	private Label lableNameAnalysis;
	private Table table;
	private boolean isCloseAnalysis;
	
	
	
	private Button buttonRefreshDevices;
	private Button buttonStart;
	private Button buttonStop;
	private Button buttonEnd;
	private Button buttonSave;
	private Button buttonAddDevice;
	
	protected List<IControlDevice> deviceList; 
	protected List<IControlDevices> devicePlugins;
	
	

	
	
	
	@PostConstruct
	public void createPartControl() {
		analysis = (IAnalysis)part.getObject();
		
		
		loadExtension();
		//refreshDevice();
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
		
		
		
		table = new Table(parent,SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
	    
	    gridData = new GridData(GridData.FILL, GridData.FILL, true, true); 
	    gridData.horizontalSpan = 2;
	    table.setLayoutData(gridData);
	    
	   
		TableColumn tableColumn = new TableColumn(table, SWT.NULL);
		tableColumn.setText("Name of devices");
		tableColumn.setWidth(150);
		table.setHeaderVisible(true);
		refreshDevice();
		
		
		buttonRefreshDevices = new Button(parent, SWT.PUSH);
		buttonRefreshDevices.setText("Refresh Devices");
		buttonRefreshDevices.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				refreshDevice();
			}
		
		});
		gridData = new GridData(GridData.FILL, GridData.FILL, true, false); 
		buttonRefreshDevices.setLayoutData(gridData);
		
		
		
		buttonAddDevice = new Button(parent, SWT.PUSH);
		buttonAddDevice.setText("Add device");
		buttonAddDevice.setEnabled(true);
		buttonAddDevice.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				  
			 }
		});
		gridData = new GridData(GridData.FILL, GridData.FILL, true, false); 
		buttonAddDevice.setLayoutData(gridData);
		
		
		buttonStart = new Button(parent, SWT.PUSH);
		buttonStart.setText("Start Analysis");
		buttonStart.setEnabled(true);
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
		buttonStop.setEnabled(false);
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
		buttonEnd.setEnabled(true);
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
		buttonSave.setEnabled(false);
		buttonSave.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				exportChromatogram();
			  }
		});
		gridData = new GridData(GridData.FILL, GridData.FILL, true, false); 
		buttonSave.setLayoutData(gridData);
		
		
		

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
	
	
	
	
	protected void refreshDevice()
	{	
		
		deviceList = new LinkedList<IControlDevice>();
		for(IControlDevices elem : devicePlugins) {
			deviceList.addAll(elem.getControlDevices());
		}
		
		table.removeAll();
		
		//Display display = new Display();
		for(int i = 0; i < deviceList.size(); i++) {
			TableItem item = new TableItem(table, SWT.NULL);
			
			IControlDevice device = deviceList.get(i);
			if(device.isPrepare())
			{
				item.setText(i, device.getName());
				//item.setBackground(i, display.getSystemColor(SWT.COLOR_WHITE));
			}
			else
			{
				item.setText(i, device.getName());
				//item.setBackground(i, display.getSystemColor(SWT.COLOR_GRAY));
			}
			
		}
	}
	
	protected boolean openNewDeviceSettingsPart(int numberDevice)
	{
	
		
		IControlDevice device = deviceList.get(numberDevice);
		
		if((analysis != null) && (device.isPrepare() && !analysis.isRecerding()))
		{
			
			
			
			device.setAnalysis(analysis);
			MPart part = MBasicFactory.INSTANCE.createPart();
			part.setLabel(device.getName());
			part.setElementId("Devices Setting");
			part.setCloseable(true);
			part.setObject(device);
			
			
			String contributionURI = device.getContributionURI();
			part.setContributionURI(contributionURI);
			
			MPartStack stack = (MPartStack) modelService.find("org.chromulan.system.control.ui.partstack.devicesSetting", aplication);
			stack.getChildren().add(part);
			partService.activate(part);
			
			
			return true;
		}
		else
		{
			return false;
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
				buttonAddDevice.setEnabled(false);
				buttonSave.setEnabled(false);
			}
			
			
			
			
			analysis.setRecording(true);
			
			eventBroker.send(IAnalysisEvents.TOPIC_ANALYSIS_CHROMULAN_START, analysis);
			
			
			
			
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
				buttonAddDevice.setEnabled(false);
				buttonSave.setEnabled(true);
			}
			
			
			
			analysis.setRecording(false);
			eventBroker.send(IAnalysisEvents.TOPIC_ANALYSIS_CHROMULAN_STOP, analysis);
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
					if(chromatogram == null)
					{
						chromatogram = iControlDevice.getChromatogram();
					} 
					else
					{
						chromatogram.addReferencedChromatogram(chromatogram);
					}
					
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
				buttonAddDevice.setEnabled(false);
				buttonSave.setEnabled(false);
			}
	
			eventBroker.send(IAnalysisEvents.TOPIC_ANALYSIS_CHROMULAN_END, analysis);
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
