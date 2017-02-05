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
package org.chromulan.system.control.ui.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.chromulan.system.control.events.IAcquisitionEvents;
import org.chromulan.system.control.manager.devices.DataSupplier;
import org.chromulan.system.control.model.IAcquisition;
import org.chromulan.system.control.ui.acquisition.support.AcquisitionsTable;
import org.chromulan.system.control.ui.acquisition.support.LabelAcquisitionDuration;
import org.chromulan.system.control.ui.acquisitions.AcquisitionProcess;
import org.chromulan.system.control.ui.acquisitions.AcquisitionsAdministator;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

@SuppressWarnings("restriction")
public class AcquisitionsPart {

	private class ActualyationTimeRecording implements Runnable {

		@Override
		public void run() {

			IAcquisition acquisition = setAcquisition;
			if(acquisition == null) {
				return;
			}
			if(acquisition.isCompleted()) {
				progressBarTimeRemain.setSelection(progressBarTimeRemain.getMaximum());
			}
			if(acquisition.getStartDate() != null) {
				acquisitionInterval.setTime(System.currentTimeMillis() - acquisition.getStartDate().getTime());
			}
			if(acquisition.getAutoStop()) {
				long time = acquisition.getStartDate().getTime() + acquisition.getDuration() - System.currentTimeMillis();
				if(time < 0) {
					progressBarTimeRemain.setSelection(progressBarTimeRemain.getMaximum());
					return;
				} else {
					progressBarTimeRemain.setSelection((int)(acquisition.getDuration() - time));
				}
			}
			display.timerExec(500, this);
		}
	}

	public final static String ID_COMMAND_SEARCH = "org.chromulan.system.control.ui.command.acquisitions.search";
	public final static String ID_PARAMETER_SEARCH_NAME = "name";
	private LabelAcquisitionDuration acquisitionInterval;
	@Inject
	private AcquisitionProcess acquisitionProcess;
	private AcquisitionsAdministator acquisitionsAdministator;
	private AcquisitionsTable acquisitionsTable;
	@Inject
	private MApplication application;
	private Button buttonActualAcquisition;
	private Button buttonAddAcquisition;
	private Button buttonEnd;
	private Button buttonStart;
	private Button buttonStartMeasurement;
	private Button buttonStop;
	private PropertyChangeListener dataAcquisitionChange;
	@Inject
	private DataSupplier dataSupplier;
	@Inject
	private Display display;
	@Inject
	private EHandlerService handlerService;
	private Label lableNameAcquisition;
	@Inject
	private EModelService modelService;
	@Inject
	private Composite parent;
	@Inject
	private EPartService partService;
	private ProgressBar progressBarTimeRemain;
	private IAcquisition setAcquisition;
	private Table tableActualAcquisition;
	private ActualyationTimeRecording timeRecording;

	public AcquisitionsPart() {
		timeRecording = new ActualyationTimeRecording();
		dataAcquisitionChange = new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {

				setTable((IAcquisition)evt.getSource());
			}
		};
		acquisitionsAdministator = new AcquisitionsAdministator();
	}

	private void addAcquisitions(List<IAcquisition> newAcquisitions) {

		acquisitionProcess.addAcquisitions(newAcquisitions);
		if(acquisitionProcess.getActualAcquisition() != null) {
			buttonEnd.setEnabled(true);
		}
		redrawTable();
	}

	@PostConstruct
	public void createAcquisitionsArea() {

		Composite composite = new Composite(parent, SWT.None);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		composite.setLayout(gridLayout);
		GridData gridData;
		final Composite tableComposite = new Composite(composite, SWT.None);
		acquisitionsTable = new AcquisitionsTable(tableComposite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		acquisitionsTable.setAcquisitions(acquisitionProcess);
		tableComposite.setLayout(new FillLayout());
		gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		gridData.horizontalSpan = 2;
		tableComposite.setLayoutData(gridData);
		buttonAddAcquisition = new Button(composite, SWT.PUSH);
		buttonAddAcquisition.setText("Add");
		buttonAddAcquisition.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				List<IAcquisition> newAcquisitions = acquisitionsAdministator.createAcqusitions(dataSupplier.getDevicesProfiles(), display);
				addAcquisitions(newAcquisitions);
			}
		});
		gridData = new GridData(GridData.FILL, GridData.FILL, true, false);
		buttonAddAcquisition.setLayoutData(gridData);
		buttonActualAcquisition = new Button(composite, SWT.PUSH);
		buttonActualAcquisition.setText("Select Actual");
		buttonActualAcquisition.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				acquisitionsTable.selectActualAcquisition();
			}
		});
		gridData = new GridData(GridData.FILL, GridData.FILL, true, false);
		buttonActualAcquisition.setLayoutData(gridData);
		acquisitionsTable.getViewer().addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {

				IStructuredSelection thisSelection = (IStructuredSelection)event.getSelection();
				if(thisSelection.getFirstElement() instanceof IAcquisition) {
					IAcquisition acquisition = (IAcquisition)thisSelection.getFirstElement();
					acquisitionsAdministator.acquisitionSettings(acquisition, display, modelService, application, partService);
				}
			}
		});
		acquisitionsTable.getViewer().getTable().addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				if(e.keyCode == SWT.DEL) {
					removeSelecetion();
				}
			}
		});
		Group group = new Group(composite, SWT.SHADOW_ETCHED_IN);
		group.setText("Actual acquisition");
		gridData = new GridData(GridData.FILL, GridData.FILL, true, false);
		gridData.horizontalSpan = 2;
		gridData.minimumWidth = 300;
		group.setLayoutData(gridData);
		createActualAcquisitionControl(group);
		buttonStartMeasurement = new Button(composite, SWT.PUSH);
		gridData = new GridData(GridData.FILL, GridData.FILL, true, false);
		gridData.horizontalSpan = 2;
		buttonStartMeasurement.setText("Set Default Parameters");
		buttonStartMeasurement.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(acquisitionsAdministator.setDefaultParametersWizard(display)) {
					setAcquisitionManagerButtons(true);
				}
			}
		});
		buttonStartMeasurement.setLayoutData(gridData);
		initializationHandler();
		initializationButtons();
	}

	private void createActualAcquisitionControl(Composite composite) {

		GridLayout gridLayout = new GridLayout(4, false);
		composite.setLayout(gridLayout);
		lableNameAcquisition = new Label(composite, SWT.LEFT);
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, false);
		gridData.horizontalSpan = 3;
		lableNameAcquisition.setLayoutData(gridData);
		buttonEnd = new Button(composite, SWT.PUSH);
		buttonEnd.setText("Next Acquisition");
		buttonEnd.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				acquisitionProcess.setNextAcquisition();
				redrawTable();
			}
		});
		gridData = new GridData(GridData.END, GridData.FILL, false, false);
		buttonEnd.setLayoutData(gridData);
		buttonStart = new Button(composite, SWT.PUSH);
		buttonStart.setText("Start recording");
		buttonStart.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				acquisitionProcess.startAcquisition();
			}
		});
		gridData = new GridData(GridData.FILL, GridData.FILL, false, false);
		buttonStart.setLayoutData(gridData);
		buttonStop = new Button(composite, SWT.PUSH);
		buttonStop.setText("Stop Recording");
		buttonStop.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				acquisitionProcess.stopAcquisition(true);
			}
		});
		gridData = new GridData(GridData.FILL, GridData.FILL, false, false);
		buttonStop.setLayoutData(gridData);
		gridData.horizontalSpan = 2;
		acquisitionInterval = new LabelAcquisitionDuration(composite, SWT.RIGHT);
		acquisitionInterval.setTime(0);
		gridData = new GridData(GridData.CENTER, GridData.CENTER, true, false);
		acquisitionInterval.getLabel().setLayoutData(gridData);
		tableActualAcquisition = new Table(composite, SWT.BORDER | SWT.MULTI);
		gridData = new GridData(GridData.FILL, GridData.FILL, true, false);
		gridData.horizontalSpan = 4;
		tableActualAcquisition.setLayoutData(gridData);
		TableColumn columnVariable = new TableColumn(tableActualAcquisition, SWT.None);
		columnVariable.setText("variable");
		columnVariable.setWidth(100);
		TableColumn columnValue = new TableColumn(tableActualAcquisition, SWT.None);
		columnValue.setText("value");
		columnValue.setWidth(200);
		progressBarTimeRemain = new ProgressBar(composite, SWT.HORIZONTAL);
		gridData = new GridData(GridData.FILL, GridData.FILL, true, false);
		gridData.horizontalSpan = 4;
		progressBarTimeRemain.setLayoutData(gridData);
	}

	@Inject
	@Optional
	public void endAcquisition(@UIEventTopic(value = IAcquisitionEvents.TOPIC_ACQUISITION_CHROMULAN_END) IAcquisition acquisition) {

		if(acquisition != null && acquisition == setAcquisition) {
			acquisition.removePropertyChangeListener(dataAcquisitionChange);
			buttonStart.setEnabled(false);
			buttonStop.setEnabled(false);
			acquisitionInterval.setTime(0);
			tableActualAcquisition.removeAll();
			lableNameAcquisition.setText("");
			progressBarTimeRemain.setSelection(0);
			redrawTable();
			if(acquisitionProcess.getActualAcquisition() != null) {
				buttonEnd.setEnabled(false);
			} else {
				buttonEnd.setEnabled(true);
			}
		}
	}

	private void initializationButtons() {

		buttonStart.setEnabled(false);
		buttonStop.setEnabled(false);
		buttonEnd.setEnabled(false);
		progressBarTimeRemain.setEnabled(false);
		setAcquisitionManagerButtons(acquisitionsAdministator.isDefParametersSet());
	}

	private void initializationHandler() {

		handlerService.activateHandler(AcquisitionsPart.ID_COMMAND_SEARCH, new Object() {

			@Execute
			public void execute(@Named(AcquisitionsPart.ID_PARAMETER_SEARCH_NAME) final String nameSearch) {

				if(nameSearch == null || nameSearch.isEmpty()) {
					acquisitionsTable.removeFilterName();
				} else {
					acquisitionsTable.addFilterName(nameSearch);
				}
			}
		});
	}

	@PreDestroy
	public void preDestroy() {

		display.timerExec(-1, timeRecording);
	}

	private void redrawTable() {

		acquisitionsTable.getViewer().refresh();
	}

	private boolean removeAcquisition(IAcquisition acquisition) {

		boolean b = acquisitionProcess.removeAcquisition(acquisition);
		if(acquisitionProcess.getActualAcquisition() != null) {
			buttonEnd.setEnabled(true);
		} else {
			buttonEnd.setEnabled(false);
		}
		return b;
	}

	@SuppressWarnings("unchecked")
	private void removeSelecetion() {

		IStructuredSelection selection = (IStructuredSelection)acquisitionsTable.getViewer().getSelection();
		List<IAcquisition> selections = selection.toList();
		for(IAcquisition acquisition : selections) {
			if(!removeAcquisition(acquisition)) {
				return;
			}
		}
		redrawTable();
	}

	@Inject
	@Optional
	public void setAcquisition(@UIEventTopic(value = IAcquisitionEvents.TOPIC_ACQUISITION_CHROMULAN_SET) IAcquisition acquisition) {

		if(acquisition != null && this.acquisitionProcess.getActualAcquisition() == acquisition) {
			buttonStart.setEnabled(true);
			buttonStop.setEnabled(false);
			buttonEnd.setEnabled(true);
			acquisition.addPropertyChangeListener(dataAcquisitionChange);
			setTable(acquisition);
			setAcquisition = acquisition;
			redrawTable();
		}
	}

	private void setAcquisitionManagerButtons(boolean allow) {

		buttonActualAcquisition.setEnabled(allow);
		buttonAddAcquisition.setEnabled(allow);
	}

	private void setTable(IAcquisition acquisition) {

		if(acquisition != null) {
			tableActualAcquisition.removeAll();
			lableNameAcquisition.setText(acquisition.getName());
			TableItem tableItem = new TableItem(tableActualAcquisition, SWT.NONE);
			tableItem.setText(0, "AutoStop");
			if(acquisition.getAutoStop()) {
				tableItem.setText(1, "Yes");
				tableItem = new TableItem(tableActualAcquisition, SWT.NONE);
				progressBarTimeRemain.setEnabled(true);
				progressBarTimeRemain.setMaximum((int)acquisition.getDuration());
				tableItem.setText("Duration (min)");
				tableItem.setText(1, Long.toString(acquisition.getDuration() / (long)IChromatogramOverview.MINUTE_CORRELATION_FACTOR));
			} else {
				progressBarTimeRemain.setSelection(0);
				progressBarTimeRemain.setEnabled(false);
				tableItem.setText(1, "No");
			}
		}
	}

	@Inject
	@Optional
	public void startRecording(@UIEventTopic(value = IAcquisitionEvents.TOPIC_ACQUISITION_CHROMULAN_START_RECORDING) IAcquisition acquisition) {

		if(acquisition != null && setAcquisition == acquisition) {
			display.asyncExec(timeRecording);
			buttonStart.setEnabled(false);
			buttonStop.setEnabled(true);
			buttonEnd.setEnabled(false);
		}
	}

	@Inject
	@Optional
	public void stopRecording(@UIEventTopic(value = IAcquisitionEvents.TOPIC_ACQUISITION_CHROMULAN_STOP_RECORDING) IAcquisition acquisition) {

		if(acquisition != null && setAcquisition == acquisition) {
			display.timerExec(-1, timeRecording);
		}
	}
}
