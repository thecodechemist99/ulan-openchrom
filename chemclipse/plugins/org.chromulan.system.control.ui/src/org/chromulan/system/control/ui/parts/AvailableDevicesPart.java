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
import javax.inject.Inject;

import org.chromulan.system.control.events.IAnalysisEvents;
import org.chromulan.system.control.model.IAnalysis;
import org.chromulan.system.control.model.IControlDevice;
import org.chromulan.system.control.model.IControlDevices;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.e4.core.di.annotations.Optional;
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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class AvailableDevicesPart {

	@Inject
	private Composite parent;
	@Inject
	private EModelService modelService;
	@Inject
	private EPartService partService;
	@Inject
	private MApplication application;
	@Inject
	private IExtensionRegistry registry;
	private Table table;
	private Button buttonRefreshDevices;
	private Button buttonAddDevice;
	private List<IControlDevice> deviceList;
	private List<IControlDevices> devicePlugins;

	@PostConstruct
	public void createPartControl() {

		loadExtension();
		// refreshDevice();
		GridLayout gridLayout = new GridLayout(2, false);
		parent.setLayout(gridLayout);
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, false);
		gridData.horizontalSpan = 2;
		table = new Table(parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
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
		MPerspective perspectiveChromulan = (MPerspective)modelService.find("org.chromulan.system.control.ui.perspective.chromulan", application);
		if(perspectiveChromulan.getContext().containsKey(IAnalysis.class)) {
			buttonAddDevice.setEnabled(false);
		} else {
			buttonAddDevice.setEnabled(true);
		}
	}

	@Inject
	@Optional
	public void disableButtons(@UIEventTopic(value = IAnalysisEvents.TOPIC_ANALYSIS_CHROMULAN_START) IAnalysis analysis) {

		buttonAddDevice.setEnabled(false);
	}

	@Inject
	@Optional
	public void enableButtons(@UIEventTopic(value = IAnalysisEvents.TOPIC_ANALYSIS_CHROMULAN_END) IAnalysis analysis) {

		buttonAddDevice.setEnabled(true);
	}

	protected void refreshDevice() {

		deviceList = new LinkedList<IControlDevice>();
		for(IControlDevices elem : devicePlugins) {
			deviceList.addAll(elem.getControlDevices());
		}
		table.removeAll();
		// Display display = new Display();
		for(int i = 0; i < deviceList.size(); i++) {
			TableItem item = new TableItem(table, SWT.NULL);
			IControlDevice device = deviceList.get(i);
			if(device.isPrepare()) {
				item.setText(i, device.getName());
				// item.setBackground(i, display.getSystemColor(SWT.COLOR_WHITE));
			} else {
				item.setText(i, device.getName());
				// item.setBackground(i, display.getSystemColor(SWT.COLOR_GRAY));
			}
		}
	}

	protected boolean openNewDeviceSettingsPart(int numberDevice) {

		IControlDevice device = deviceList.get(numberDevice);
		if(device.isPrepare()) {
			MPart part = MBasicFactory.INSTANCE.createPart();
			part.setLabel(device.getName());
			part.setElementId("Devices Setting");
			part.setCloseable(true);
			part.setObject(device);
			String contributionURI = device.getContributionURI();
			part.setContributionURI(contributionURI);
			MPartStack stack = (MPartStack)modelService.find("org.chromulan.system.control.ui.partstack.devicesSetting", application);
			stack.getChildren().add(part);
			partService.activate(part);
			return true;
		} else {
			return false;
		}
	}

	protected void loadExtension() {

		devicePlugins = new LinkedList<IControlDevices>();
		IConfigurationElement[] config = registry.getConfigurationElementsFor("org.chromulan.system.control.ui");
		for(IConfigurationElement elem : config) {
			try {
				IControlDevices controlDevices = (IControlDevices)elem.createExecutableExtension("Control device");
				devicePlugins.add(controlDevices);
			} catch(CoreException e) {
			}
		}
	}
}
