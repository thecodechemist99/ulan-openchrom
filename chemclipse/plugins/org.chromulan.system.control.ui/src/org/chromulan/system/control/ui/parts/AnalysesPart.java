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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.chromulan.system.control.events.IAnalysisEvents;
import org.chromulan.system.control.events.IControlDevicesEvents;
import org.chromulan.system.control.events.IULanConnectionEvents;
import org.chromulan.system.control.model.AnalysesCSD;
import org.chromulan.system.control.model.AnalysisCSD;
import org.chromulan.system.control.model.AnalysisCSDSaver;
import org.chromulan.system.control.model.IAnalysesCSD;
import org.chromulan.system.control.model.IAnalysis;
import org.chromulan.system.control.model.IAnalysisCSD;
import org.chromulan.system.control.model.IAnalysisCSDSaver;
import org.chromulan.system.control.model.IAnalysisSaver;
import org.chromulan.system.control.model.IControlDevice;
import org.chromulan.system.control.model.IControlDevices;
import org.chromulan.system.control.model.IDevicesProfile;
import org.chromulan.system.control.model.ULanConnection;
import org.chromulan.system.control.model.data.IChromatogramCSDData;
import org.chromulan.system.control.ui.analysis.support.AnalysesTable;
import org.chromulan.system.control.ui.analysis.support.AnalysisCDSSavePreferencePage;
import org.chromulan.system.control.ui.analysis.support.AnalysisSettingsPreferencePage;
import org.chromulan.system.control.ui.analysis.support.ChromatogramFilesDialog;
import org.chromulan.system.control.ui.analysis.support.LabelAnalysisDuration;
import org.chromulan.system.control.ui.devices.support.ProfilePreferencePage;
import org.chromulan.system.control.ui.wizard.WizardNewAnalyses;
import org.chromulan.system.control.ui.wizard.WizardNewAnalysis;
import org.eclipse.chemclipse.converter.core.ISupplier;
import org.eclipse.chemclipse.converter.processing.chromatogram.IChromatogramExportConverterProcessingInfo;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;
import org.eclipse.chemclipse.ux.extension.csd.ui.support.ChromatogramEditorSupport;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
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

import net.sourceforge.ulan.base.CompletionHandler;
import net.sourceforge.ulan.base.IULanCommunication;
import net.sourceforge.ulan.base.IULanCommunication.IFilt;
import net.sourceforge.ulan.base.ULanCommunicationInterface;
import net.sourceforge.ulan.base.ULanHandle;
import net.sourceforge.ulan.base.ULanMsg;

@SuppressWarnings("restriction")
public class AnalysesPart {

	private class ActualyationTimeRecording implements Runnable {

		@Override
		public void run() {

			IAnalysis analysis = AnalysesPart.this.analysis;
			if(analysis == null || analysis.hasBeenRecorded()) {
				return;
			}
			if(analysis.getStartDate() != null) {
				analysisInterval.setTime(System.currentTimeMillis() - analysis.getStartDate().getTime());
			}
			if(analysis.getAutoStop()) {
				long time = analysis.getStartDate().getTime() + analysis.getDuration() - System.currentTimeMillis();
				if(time < 0) {
					stopRecording();
					progressBarTimeRemain.setSelection(progressBarTimeRemain.getMaximum());
					return;
				} else {
					progressBarTimeRemain.setSelection((int)(analysis.getDuration() - time));
				}
			}
			display.timerExec(500, this);
		}
	}

	private IAnalysesCSD analyses;
	private AnalysesTable analysesTable;
	private IAnalysisCSD analysis;
	private LabelAnalysisDuration analysisInterval;
	@Inject
	private MApplication application;
	private Button buttonActualAnalysis;
	private Button buttonAddAnalysis;
	private Button buttonEnd;
	private Button buttonSave;
	private Button buttonStart;
	private Button buttonStartMeasurement;
	private Button buttonStop;
	private PropertyChangeListener dataAnalysisChange;
	@Inject
	private Display display;
	@Inject
	private IEventBroker eventBroker;
	private File file;
	private IFilt filtStartRecording;
	@Inject
	private EHandlerService handlerService;
	private boolean isSetAnalysis;
	private Label lableNameAnalysis;
	@Inject
	private EModelService modelService;
	@Inject
	private Composite parent;
	@Inject
	private EPartService partService;
	@Inject
	private MPerspective perspective;
	private ProgressBar progressBarTimeRemain;
	private ISupplier supplier;
	private Table tableActualAnalysis;
	private ActualyationTimeRecording timeRecording;

	public AnalysesPart() {

		analysis = null;
		timeRecording = new ActualyationTimeRecording();
		IULanCommunication com = new ULanCommunicationInterface();
		filtStartRecording = com.addFilt(ULanHandle.CMD_LCDMRK, null, new CompletionHandler<ULanMsg, Void>() {

			@Override
			public void completed(ULanMsg arg0, Void arg1) {

				display.asyncExec(new Runnable() {

					@Override
					public void run() {

						startRecording();
					}
				});
			}

			@Override
			public void failed(Exception arg0, Void arg1) {

			}
		});
		dataAnalysisChange = new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {

				setTable();
			}
		};
	}

	private void addAnalysis(IAnalysisCSD analysis) {

		if(analyses.getActualAnalysis() == null) {
			analyses.addAnalysis(analysis);
			setAnalysis(analysis);
		} else {
			analyses.addAnalysis(analysis);
		}
		redrawTable();
	}

	private void analysisSettings() {

		int index = analysesTable.getViewer().getTable().getSelectionIndex();
		if(index != -1) {
			if(index < analyses.getNumberAnalysis()) {
				IAnalysisCSD analysis = (IAnalysisCSD)analyses.getAnalysis(index);
				if(!analysis.hasBeenRecorded()) {
					PreferenceManager manager = new PreferenceManager();
					AnalysisSettingsPreferencePage settings = new AnalysisSettingsPreferencePage(analysis);
					ProfilePreferencePage page = new ProfilePreferencePage(analysis.getDevicesProfile());
					AnalysisCDSSavePreferencePage save = new AnalysisCDSSavePreferencePage(analysis);
					PreferenceNode nodeBase = new PreferenceNode("Main", settings);
					PreferenceNode nodeProfile = new PreferenceNode("Devices", page);
					PreferenceNode nodeSave = new PreferenceNode("Save", save);
					manager.addToRoot(nodeBase);
					manager.addToRoot(nodeProfile);
					manager.addToRoot(nodeSave);
					PreferenceDialog dialog = new PreferenceDialog(Display.getCurrent().getActiveShell(), manager);
					dialog.open();
				} else {
					ChromatogramFilesDialog dialog = new ChromatogramFilesDialog(Display.getCurrent().getActiveShell(), analysis.getAnalysisSaver());
					if(dialog.open() == Window.OK) {
						List<IChromatogramExportConverterProcessingInfo> chromatogramFiles = dialog.getChromatogramExportConverterProcessingInfos();
						ChromatogramEditorSupport support = new ChromatogramEditorSupport();
						for(IChromatogramExportConverterProcessingInfo chromatogramFile : chromatogramFiles) {
							try {
								support.openEditor(chromatogramFile.getFile(), modelService, application, partService);
							} catch(TypeCastException e) {
								// TODO: logger.warn(e);
							}
						}
					}
					;
				}
			}
		}
	}

	private boolean controlAnalysis(IAnalysis analysis) {

		return controlUsingDevices(analysis.getDevicesProfile()) && controlConnection();
	}

	private boolean controlConnection() {

		return ULanCommunicationInterface.isOpen();
	}

	private boolean controlUsingDevices(IDevicesProfile profile) {

		for(IControlDevice device : profile.getControlDevices().getControlDevices()) {
			if(!device.isConnected()) {
				return false;
			}
			MPart part = partService.findPart(device.getID());
			if(part == null) {
				return false;
			}
		}
		return true;
	}

	private void createActualAnalysisControl(Composite composite) {

		GridLayout gridLayout = new GridLayout(4, false);
		composite.setLayout(gridLayout);
		lableNameAnalysis = new Label(composite, SWT.LEFT);
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, false);
		gridData.horizontalSpan = 3;
		lableNameAnalysis.setLayoutData(gridData);
		buttonEnd = new Button(composite, SWT.PUSH);
		buttonEnd.setText("Next Analysis");
		buttonEnd.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				endAnalysis();
				setAnalysis((IAnalysisCSD)analyses.setNextAnalysisActual());
			}
		});
		gridData = new GridData(GridData.END, GridData.FILL, false, false);
		buttonEnd.setLayoutData(gridData);
		buttonStart = new Button(composite, SWT.PUSH);
		buttonStart.setText("Start recording");
		buttonStart.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				startRecording();
			}
		});
		gridData = new GridData(GridData.FILL, GridData.FILL, false, false);
		buttonStart.setLayoutData(gridData);
		buttonStop = new Button(composite, SWT.PUSH);
		buttonStop.setText("Stop Recording");
		buttonStop.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				stopRecording();
			}
		});
		gridData = new GridData(GridData.FILL, GridData.FILL, false, false);
		buttonStop.setLayoutData(gridData);
		buttonSave = new Button(composite, SWT.PUSH);
		buttonSave.setText("Save");
		buttonSave.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				saveAnalysis();
			}
		});
		gridData = new GridData(GridData.BEGINNING, GridData.FILL, false, false);
		buttonSave.setLayoutData(gridData);
		analysisInterval = new LabelAnalysisDuration(composite, SWT.RIGHT);
		analysisInterval.setTime(0);
		gridData = new GridData(GridData.CENTER, GridData.CENTER, true, false);
		analysisInterval.getLabel().setLayoutData(gridData);
		tableActualAnalysis = new Table(composite, SWT.BORDER | SWT.MULTI);
		gridData = new GridData(GridData.FILL, GridData.FILL, true, false);
		gridData.horizontalSpan = 4;
		tableActualAnalysis.setLayoutData(gridData);
		TableColumn columnVariable = new TableColumn(tableActualAnalysis, SWT.None);
		columnVariable.setText("variable");
		columnVariable.setWidth(100);
		TableColumn columnValue = new TableColumn(tableActualAnalysis, SWT.None);
		columnValue.setText("value");
		columnValue.setWidth(200);
		progressBarTimeRemain = new ProgressBar(composite, SWT.HORIZONTAL);
		gridData = new GridData(GridData.FILL, GridData.FILL, true, false);
		gridData.horizontalSpan = 4;
		progressBarTimeRemain.setLayoutData(gridData);
	}

	@PostConstruct
	public void createAnalysesArea() {

		if(ULanCommunicationInterface.isOpen()) {
			try {
				filtStartRecording.activateFilt();
			} catch(IOException e1) {
				// TODO: exception logger.warn(e1);
			}
		}
		Composite composite = new Composite(parent, SWT.None);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		composite.setLayout(gridLayout);
		GridData gridData;
		final Composite tableComposite = new Composite(composite, SWT.None);
		analysesTable = new AnalysesTable(tableComposite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		tableComposite.setLayout(new FillLayout());
		gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		gridData.horizontalSpan = 2;
		tableComposite.setLayoutData(gridData);
		buttonAddAnalysis = new Button(composite, SWT.PUSH);
		buttonAddAnalysis.setText("Add");
		buttonAddAnalysis.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				createAnalysis();
			}
		});
		gridData = new GridData(GridData.FILL, GridData.FILL, true, false);
		buttonAddAnalysis.setLayoutData(gridData);
		buttonActualAnalysis = new Button(composite, SWT.PUSH);
		buttonActualAnalysis.setText("Select Actual");
		buttonActualAnalysis.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				analysesTable.selectActualAnalysis();
			}
		});
		gridData = new GridData(GridData.FILL, GridData.FILL, true, false);
		buttonActualAnalysis.setLayoutData(gridData);
		analysesTable.getViewer().addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {

				analysisSettings();
			}
		});
		analysesTable.getViewer().getTable().addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				if(e.keyCode == SWT.DEL) {
					removeSelecetion();
				}
			}
		});
		Group group = new Group(composite, SWT.SHADOW_ETCHED_IN);
		group.setText("Actual analysis");
		gridData = new GridData(GridData.FILL, GridData.FILL, true, false);
		gridData.horizontalSpan = 2;
		gridData.minimumWidth = 300;
		group.setLayoutData(gridData);
		createActualAnalysisControl(group);
		buttonStartMeasurement = new Button(composite, SWT.PUSH);
		gridData = new GridData(GridData.FILL, GridData.FILL, true, false);
		gridData.horizontalSpan = 2;
		buttonStartMeasurement.setText("New Measurement");
		buttonStartMeasurement.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				createMeasurement();
			}
		});
		buttonStartMeasurement.setLayoutData(gridData);
		initializationHandler();
		initializationButtons();
	}

	private void createAnalysis() {

		WizardNewAnalysis newAnalysisWizard = new WizardNewAnalysis(getDevicesProfile());
		newAnalysisWizard.getModel().folder.setValue(file);
		WizardDialog wizardDialog = new WizardDialog(display.getActiveShell(), newAnalysisWizard);
		if(wizardDialog.open() == Window.OK) {
			int numberAnalysis = (Integer)newAnalysisWizard.getModel().numberAnalyses.getValue();
			for(int i = 1; i <= numberAnalysis; i++) {
				IAnalysisCSD analysis = new AnalysisCSD();
				String name = getNameAnalysis((String)newAnalysisWizard.getModel().name.getValue(), i, numberAnalysis);
				analysis.setName(name);
				analysis.setAutoContinue((Boolean)newAnalysisWizard.getModel().autoContinue.getValue());
				analysis.setAutoStop((Boolean)newAnalysisWizard.getModel().autoStop.getValue());
				analysis.setDuration((Long)newAnalysisWizard.getModel().duration.getValue());
				analysis.setDevicesProfile((IDevicesProfile)newAnalysisWizard.getModel().devicesProfile.getValue());
				analysis.setDescription((String)newAnalysisWizard.getModel().description.getValue());
				IAnalysisSaver saver = new AnalysisCSDSaver(analysis);
				saver.setFile(file);
				saver.setSuplier(supplier);
				analysis.setAnalysisSaver(saver);
				addAnalysis(analysis);
			}
		}
	}

	private void createMeasurement() {

		if(this.analysis != null && this.analysis.isBeingRecorded()) {
			// TODO: alert
			return;
		}
		if(this.analysis != null) {
			// TODO: alert
			analysis.removePropertyChangeListener(dataAnalysisChange);
			removeData();
		}
		if(this.analyses != null) {
			this.analyses.removeAllAnalysis();
		}
		WizardNewAnalyses newAnalysisWizard = new WizardNewAnalyses();
		WizardDialog wizardDialog = new WizardDialog(display.getActiveShell(), newAnalysisWizard);
		if(wizardDialog.open() == Window.OK) {
			this.analyses = new AnalysesCSD();
			analysesTable.setAnalyses(analyses);
			buttonActualAnalysis.setEnabled(true);
			buttonAddAnalysis.setEnabled(true);
			this.file = newAnalysisWizard.getFile();
			this.supplier = newAnalysisWizard.getSupplier();
		}
	}

	synchronized private void endAnalysis() {

		if((analysis != null) && (isSetAnalysis) && (!analysis.isBeingRecorded())) {
			analysis.removePropertyChangeListener(dataAnalysisChange);
			buttonStart.setEnabled(false);
			buttonStop.setEnabled(false);
			buttonEnd.setEnabled(false);
			buttonSave.setEnabled(false);
			eventBroker.send(IAnalysisEvents.TOPIC_ANALYSIS_CHROMULAN_END, analysis);
			isSetAnalysis = false;
			analysis = null;
			perspective.getContext().remove(IAnalysis.class);
		}
		removeData();
	}

	private List<IDevicesProfile> getDevicesProfile() {

		MPart part = partService.findPart(DevicesProfilesPart.ID);
		if(part.getContext() == null) {
			partService.activate(part, false);
		}
		if(part.getContext().containsKey(DevicesProfilesPart.DEVICES_PROFILES_DATA)) {
			return (List<IDevicesProfile>)part.getContext().get(DevicesProfilesPart.DEVICES_PROFILES_DATA);
		} else {
			return null;
		}
	}

	private String getNameAnalysis(String name, int numberOfAnalysis, int maxNumber) {

		if(name != null && !name.isEmpty() && maxNumber > 0) {
			if(maxNumber == 1) {
				return name;
			} else {
				int maxNumDigits = (int)(Math.log10(maxNumber) + 1);
				int actulNumDigits = (int)(Math.log10(numberOfAnalysis) + 1);
				StringBuilder builder = new StringBuilder(name);
				for(int i = 0; i < maxNumDigits - actulNumDigits; i++) {
					builder.append('0');
				}
				builder.append(numberOfAnalysis);
				return builder.toString();
			}
		} else {
			return name;
		}
	}

	private void initializationButtons() {

		buttonStart.setEnabled(false);
		buttonStop.setEnabled(false);
		buttonEnd.setEnabled(false);
		buttonSave.setEnabled(false);
		progressBarTimeRemain.setEnabled(false);
		buttonAddAnalysis.setEnabled(false);
		buttonActualAnalysis.setEnabled(false);
	}

	private void initializationHandler() {

		handlerService.activateHandler(AnalysesSearchToolItem.ID_COMMAND_SEARCH, new Object() {

			@Execute
			public void execute(@Named(AnalysesSearchToolItem.ID_PARAMETER_SEARCH_NAME) final String nameSearch) {

				if(nameSearch == null || nameSearch.isEmpty()) {
					analysesTable.removeFilterName();
				} else {
					analysesTable.addFilterName(nameSearch);
				}
			}
		});
	}

	@Inject
	@Optional
	public void openCommunicationEvent(@UIEventTopic(value = IULanConnectionEvents.TOPIC_COMMUCATION_ULAN_OPEN) ULanConnection connection) {

		try {
			filtStartRecording.activateFilt();
		} catch(IOException e) {
			// TODO: exception
		}
	}

	@PreDestroy
	public void preDestroy() {

		filtStartRecording.deactivateFilt();
		display.timerExec(-1, timeRecording);
	}

	private void redrawTable() {

		analysesTable.getViewer().refresh();
	}

	private void removeAnalysis(IAnalysis analysis) {

		int number = analyses.gettIndex(analysis);
		if(analyses.isActualAnalysis(analysis)) {
			if(!analysis.isBeingRecorded()) {
				if(isSetAnalysis) {
					endAnalysis();
				}
				analyses.setNextAnalysisActual();
				analyses.removeAnalysis(number);
				setAnalysis((IAnalysisCSD)analyses.getActualAnalysis());
			} else {
				// TODO: alert
			}
		} else {
			analyses.removeAnalysis(number);
		}
		redrawTable();
	}

	private void removeData() {

		analysisInterval.setTime(0);
		tableActualAnalysis.removeAll();
		lableNameAnalysis.setText("");
		progressBarTimeRemain.setSelection(0);
	}

	private void removeSelecetion() {

		IStructuredSelection selection = (IStructuredSelection)analysesTable.getViewer().getSelection();
		List<IAnalysis> selections = selection.toList();
		for(IAnalysis analysis : selections) {
			removeAnalysis(analysis);
		}
	}

	private void saveAnalysis() {

		IAnalysisCSDSaver saver = analysis.getAnalysisCSDSaver();
		saver.removeAllChromatograms();
		saver.removeAllAnalysisData();
		for(IControlDevice device : analysis.getDevicesProfile().getControlDevices().getControlDevices()) {
			MPart part = partService.findPart(device.getID());
			if(part != null && part.getContext() != null && part.getContext().containsKey(IChromatogramCSDData.class)) {
				IChromatogramCSDData chromatogramRecording = part.getContext().get(IChromatogramCSDData.class);
				saver.addChromatogam(chromatogramRecording);
			}
		}
		saver.save(new NullProgressMonitor());
	}

	synchronized private void setAnalysis(IAnalysisCSD analysis) {

		if(analysis != null && !isSetAnalysis) {
			if(this.analysis == null) {
				this.analysis = analysis;
				analysis.addPropertyChangeListener(dataAnalysisChange);
				setTable();
				eventBroker.post(IControlDevicesEvents.TOPIC_CONTROL_DEVICES_ULAN_REQIRED, analysis.getDevicesProfile().getControlDevices());
			}
			if(this.analysis == analysis) {
				if(controlAnalysis(analysis)) {
					eventBroker.send(IAnalysisEvents.TOPIC_ANALYSIS_CHROMULAN_SET, analysis);
					isSetAnalysis = true;
					perspective.getContext().set(IAnalysis.class, analysis);
					buttonStart.setEnabled(true);
					buttonStop.setEnabled(false);
					buttonEnd.setEnabled(true);
					buttonSave.setEnabled(false);
				} else {
					buttonStart.setEnabled(false);
					buttonStop.setEnabled(false);
					buttonEnd.setEnabled(false);
					buttonSave.setEnabled(false);
				}
			}
		}
		redrawTable();
	}

	@Inject
	@Optional
	public void setAnalysis(@UIEventTopic(value = IControlDevicesEvents.TOPIC_CONTROL_DEVICES_ULAN_AVAILABLE) IControlDevices devices) {

		if(this.analysis != null && !this.isSetAnalysis && controlUsingDevices(analysis.getDevicesProfile())) {
			setAnalysis(analysis);
		}
	}

	private void setTable() {

		if(this.analysis != null) {
			tableActualAnalysis.removeAll();
			lableNameAnalysis.setText(analysis.getName());
			TableItem tableItem = new TableItem(tableActualAnalysis, SWT.NONE);
			tableItem.setText(0, "AutoStop");
			if(analysis.getAutoStop()) {
				tableItem.setText(1, "Yes");
				tableItem = new TableItem(tableActualAnalysis, SWT.NONE);
				progressBarTimeRemain.setEnabled(true);
				progressBarTimeRemain.setMaximum((int)analysis.getDuration());
				tableItem.setText("Duration (min)");
				tableItem.setText(1, Long.toString(analysis.getDuration() / (long)IChromatogramOverview.MINUTE_CORRELATION_FACTOR));
			} else {
				progressBarTimeRemain.setSelection(0);
				progressBarTimeRemain.setEnabled(false);
				tableItem.setText(1, "No");
			}
			tableItem = new TableItem(tableActualAnalysis, SWT.NONE);
			tableItem.setText(0, "AutoContinue");
			if(analysis.getAutoContinue()) {
				tableItem.setText(1, "Yes");
			} else {
				tableItem.setText(1, "No");
			}
		}
	}

	synchronized public void startRecording() {

		if((analysis != null) && isSetAnalysis && !analysis.isBeingRecorded() && !analysis.hasBeenRecorded()) {
			analysis.startRecording();
			eventBroker.send(IAnalysisEvents.TOPIC_ANALYSIS_CHROMULAN_START_RECORDING, analysis);
			display.asyncExec(timeRecording);
			buttonStart.setEnabled(false);
			buttonStop.setEnabled(true);
			buttonEnd.setEnabled(false);
			buttonSave.setEnabled(false);
		}
	}

	synchronized public void stopRecording() {

		if((analysis != null) && isSetAnalysis && analysis.isBeingRecorded()) {
			analysis.stopRecording();
			eventBroker.send(IAnalysisEvents.TOPIC_ANALYSIS_CHROMULAN_STOP_RECORDING, analysis);
			display.timerExec(-1, timeRecording);
			if(analysis.getAutoContinue()) {
				saveAnalysis();
				endAnalysis();
				setAnalysis((IAnalysisCSD)analyses.setNextAnalysisActual());
			} else {
				buttonStart.setEnabled(false);
				buttonStop.setEnabled(false);
				buttonEnd.setEnabled(true);
				buttonSave.setEnabled(true);
			}
		}
	}
}
