/*******************************************************************************
 * Copyright (c) 2015, 2016 Jan Holy.
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
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.chromulan.system.control.events.IAcquisitionEvents;
import org.chromulan.system.control.events.IControlDevicesEvents;
import org.chromulan.system.control.events.IULanConnectionEvents;
import org.chromulan.system.control.model.AcquisitionCSD;
import org.chromulan.system.control.model.AcquisitionCSDSaver;
import org.chromulan.system.control.model.AcquisitionsCSD;
import org.chromulan.system.control.model.ChromatogramCSDMaker;
import org.chromulan.system.control.model.IAcquisition;
import org.chromulan.system.control.model.IAcquisitionCSD;
import org.chromulan.system.control.model.IAcquisitionSaver;
import org.chromulan.system.control.model.IAcquisitionsCSD;
import org.chromulan.system.control.model.IControlDevice;
import org.chromulan.system.control.model.IControlDevices;
import org.chromulan.system.control.model.IDevicesProfile;
import org.chromulan.system.control.model.ULanConnection;
import org.chromulan.system.control.model.data.IDetectorData;
import org.chromulan.system.control.ui.acquisition.support.AcquisitionCDSSavePreferencePage;
import org.chromulan.system.control.ui.acquisition.support.AcquisitionSettingsPreferencePage;
import org.chromulan.system.control.ui.acquisition.support.AcquisitionsTable;
import org.chromulan.system.control.ui.acquisition.support.ChromatogramFilesDialog;
import org.chromulan.system.control.ui.acquisition.support.LabelAcquisitionDuration;
import org.chromulan.system.control.ui.devices.support.ProfilePreferencePage;
import org.chromulan.system.control.ui.wizard.WizardNewAcquisition;
import org.chromulan.system.control.ui.wizard.WizardNewAcquisitions;
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
public class AcquisitionsPart {

	private class ActualyationTimeRecording implements Runnable {

		@Override
		public void run() {

			IAcquisition acquisition = AcquisitionsPart.this.acquisition;
			if(acquisition == null || acquisition.isCompleted()) {
				return;
			}
			if(acquisition.getStartDate() != null) {
				acquisitionInterval.setTime(System.currentTimeMillis() - acquisition.getStartDate().getTime());
			}
			if(acquisition.getAutoStop()) {
				long time = acquisition.getStartDate().getTime() + acquisition.getDuration() - System.currentTimeMillis();
				if(time < 0) {
					stopRecording();
					progressBarTimeRemain.setSelection(progressBarTimeRemain.getMaximum());
					return;
				} else {
					progressBarTimeRemain.setSelection((int)(acquisition.getDuration() - time));
				}
			}
			display.timerExec(500, this);
		}
	}

	private IAcquisitionCSD acquisition;
	private LabelAcquisitionDuration acquisitionInterval;
	private IAcquisitionsCSD acquisitions;
	private AcquisitionsTable acquisitionsTable;
	@Inject
	private MApplication application;
	private Button buttonActualAcquisition;
	private Button buttonAddAcquisition;
	private Button buttonEnd;
	private Button buttonSave;
	private Button buttonStart;
	private Button buttonStartMeasurement;
	private Button buttonStop;
	private PropertyChangeListener dataAcquisitionChange;
	@Inject
	private Display display;
	@Inject
	private IEventBroker eventBroker;
	private File file;
	private IFilt<Void> filtStartRecording;
	@Inject
	private EHandlerService handlerService;
	private boolean isSetAcquisition;
	private Label lableNameAcquisition;
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
	private Table tableActualAcquisition;
	private ActualyationTimeRecording timeRecording;

	public AcquisitionsPart() {
		acquisition = null;
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
		dataAcquisitionChange = new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {

				setTable();
			}
		};
	}

	private void acquisitionSettings() {

		int index = acquisitionsTable.getViewer().getTable().getSelectionIndex();
		if(index != -1) {
			if(index < acquisitions.getNumberAcquisition()) {
				IAcquisitionCSD acquisition = (IAcquisitionCSD)acquisitions.getAcquisition(index);
				if(!acquisition.isCompleted()) {
					PreferenceManager manager = new PreferenceManager();
					AcquisitionSettingsPreferencePage settings = new AcquisitionSettingsPreferencePage(acquisition);
					ProfilePreferencePage page = new ProfilePreferencePage(acquisition.getDevicesProfile());
					AcquisitionCDSSavePreferencePage save = new AcquisitionCDSSavePreferencePage(acquisition);
					PreferenceNode nodeBase = new PreferenceNode("Main", settings);
					PreferenceNode nodeProfile = new PreferenceNode("Devices", page);
					PreferenceNode nodeSave = new PreferenceNode("Save", save);
					manager.addToRoot(nodeBase);
					manager.addToRoot(nodeProfile);
					manager.addToRoot(nodeSave);
					PreferenceDialog dialog = new PreferenceDialog(Display.getCurrent().getActiveShell(), manager);
					dialog.open();
				} else {
					ChromatogramFilesDialog dialog = new ChromatogramFilesDialog(Display.getCurrent().getActiveShell(), acquisition.getAcquisitionSaver());
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

	private void addAcquisition(IAcquisitionCSD acquisition) {

		if(acquisitions.getActualAcquisition() == null) {
			acquisitions.addAcquisition(acquisition);
			setAcquisition(acquisition);
		} else {
			acquisitions.addAcquisition(acquisition);
		}
		redrawTable();
	}

	private boolean controlAcquisition(IAcquisition acquisition) {

		return controlUsingDevices(acquisition.getDevicesProfile()) && controlConnection();
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

	private void createAcquisition() {

		WizardNewAcquisition newAcquisitionWizard = new WizardNewAcquisition(getDevicesProfile());
		newAcquisitionWizard.getModel().folder.setValue(file);
		WizardDialog wizardDialog = new WizardDialog(display.getActiveShell(), newAcquisitionWizard);
		if(wizardDialog.open() == Window.OK) {
			int numberAcquisition = (Integer)newAcquisitionWizard.getModel().numberofAcquisitions.getValue();
			for(int i = 1; i <= numberAcquisition; i++) {
				IAcquisitionCSD acquisition = new AcquisitionCSD();
				String name = getNameAcquisition((String)newAcquisitionWizard.getModel().name.getValue(), i, numberAcquisition);
				acquisition.setName(name);
				acquisition.setAutoContinue((Boolean)newAcquisitionWizard.getModel().autoContinue.getValue());
				acquisition.setAutoStop((Boolean)newAcquisitionWizard.getModel().autoStop.getValue());
				acquisition.setDuration((Long)newAcquisitionWizard.getModel().duration.getValue());
				acquisition.setDevicesProfile((IDevicesProfile)newAcquisitionWizard.getModel().devicesProfile.getValue());
				acquisition.setDescription((String)newAcquisitionWizard.getModel().description.getValue());
				IAcquisitionSaver saver = new AcquisitionCSDSaver();
				saver.setFile(file);
				saver.setSuplier(supplier);
				acquisition.setAcquisitionSaver(saver);
				addAcquisition(acquisition);
			}
		}
	}

	@PostConstruct
	public void createAcquisitionsArea() {

		if(ULanCommunicationInterface.isOpen()) {
			try {
				filtStartRecording.activateFilt();
			} catch(IOException e1) {
				// TODO: exception logger.warn(e1);
			}
		}
		this.acquisitions = new AcquisitionsCSD();
		Composite composite = new Composite(parent, SWT.None);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		composite.setLayout(gridLayout);
		GridData gridData;
		final Composite tableComposite = new Composite(composite, SWT.None);
		acquisitionsTable = new AcquisitionsTable(tableComposite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		acquisitionsTable.setAcquisitions(acquisitions);
		tableComposite.setLayout(new FillLayout());
		gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		gridData.horizontalSpan = 2;
		tableComposite.setLayoutData(gridData);
		buttonAddAcquisition = new Button(composite, SWT.PUSH);
		buttonAddAcquisition.setText("Add");
		buttonAddAcquisition.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				createAcquisition();
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

				acquisitionSettings();
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

				setDefaultParameters();
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

				endAcquisition();
				setAcquisition((IAcquisitionCSD)acquisitions.setNextAcquisitionActual());
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

				saveAcquisition();
			}
		});
		gridData = new GridData(GridData.BEGINNING, GridData.FILL, false, false);
		buttonSave.setLayoutData(gridData);
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

	synchronized private void endAcquisition() {

		if((acquisition != null) && (isSetAcquisition) && (!acquisition.isRunning())) {
			acquisition.removePropertyChangeListener(dataAcquisitionChange);
			buttonStart.setEnabled(false);
			buttonStop.setEnabled(false);
			buttonEnd.setEnabled(false);
			buttonSave.setEnabled(false);
			eventBroker.send(IAcquisitionEvents.TOPIC_ACQUISITION_CHROMULAN_END, acquisition);
			isSetAcquisition = false;
			acquisition = null;
			perspective.getContext().remove(IAcquisition.class);
		}
		removeData();
	}

	private List<IDevicesProfile> getDevicesProfile() {

		MPart part = partService.findPart(DevicesProfilesPart.ID);
		if(part.getContext() == null) {
			partService.activate(part, false);
		}
		if(part.getContext().containsKey(DevicesProfilesPart.DEVICES_PROFILES_DATA)) {
			return ((List<IDevicesProfile>)part.getContext().get(DevicesProfilesPart.DEVICES_PROFILES_DATA));
		} else {
			return null;
		}
	}

	private String getNameAcquisition(String name, int numberOfAcquisition, int maxNumber) {

		if(name != null && !name.isEmpty() && maxNumber > 0) {
			if(maxNumber == 1) {
				return name;
			} else {
				int maxNumDigits = (int)(Math.log10(maxNumber) + 1);
				int actulNumDigits = (int)(Math.log10(numberOfAcquisition) + 1);
				StringBuilder builder = new StringBuilder(name);
				for(int i = 0; i < maxNumDigits - actulNumDigits; i++) {
					builder.append('0');
				}
				builder.append(numberOfAcquisition);
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
		buttonAddAcquisition.setEnabled(false);
		buttonActualAcquisition.setEnabled(false);
	}

	private void initializationHandler() {

		handlerService.activateHandler(AcquisitionsSearchToolItem.ID_COMMAND_SEARCH, new Object() {

			@Execute
			public void execute(@Named(AcquisitionsSearchToolItem.ID_PARAMETER_SEARCH_NAME) final String nameSearch) {

				if(nameSearch == null || nameSearch.isEmpty()) {
					acquisitionsTable.removeFilterName();
				} else {
					acquisitionsTable.addFilterName(nameSearch);
				}
			}
		});
	}

	@Inject
	@Optional
	public void openCommunicationEvent(@UIEventTopic(value = IULanConnectionEvents.TOPIC_CONNECTION_ULAN_OPEN) ULanConnection connection) {

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

		acquisitionsTable.getViewer().refresh();
	}

	private void removeAcquisition(IAcquisition acquisition) {

		int number = acquisitions.getIndex(acquisition);
		if(acquisitions.isActualAcquisition(acquisition)) {
			if(!acquisition.isRunning()) {
				if(isSetAcquisition) {
					endAcquisition();
				}
				acquisitions.setNextAcquisitionActual();
				acquisitions.removeAcquisition(number);
				setAcquisition((IAcquisitionCSD)acquisitions.getActualAcquisition());
			} else {
				// TODO: alert
			}
		} else {
			acquisitions.removeAcquisition(number);
		}
		redrawTable();
	}

	private void removeData() {

		acquisitionInterval.setTime(0);
		tableActualAcquisition.removeAll();
		lableNameAcquisition.setText("");
		progressBarTimeRemain.setSelection(0);
	}

	private void removeSelecetion() {

		IStructuredSelection selection = (IStructuredSelection)acquisitionsTable.getViewer().getSelection();
		List<IAcquisition> selections = selection.toList();
		for(IAcquisition acquisition : selections) {
			removeAcquisition(acquisition);
		}
	}

	private void saveAcquisition() {

		IAcquisitionSaver saver = acquisition.getAcquisitionSaver();
		ChromatogramCSDMaker maker = new ChromatogramCSDMaker(acquisition, saver.getFile());
		for(IControlDevice device : acquisition.getDevicesProfile().getControlDevices().getControlDevices()) {
			MPart part = partService.findPart(device.getID());
			if(part != null && part.getContext() != null && part.getTransientData().containsKey(IDetectorData.DETECTORS_DATA)) {
				Object detectorsData = part.getTransientData().get(IDetectorData.DETECTORS_DATA);
				if(detectorsData instanceof List) {
					List<?> detectorDataList = (List<?>)detectorsData;
					for(Iterator<?> iterator = detectorDataList.iterator(); iterator.hasNext();) {
						Object detectorDataObject = iterator.next();
						if(detectorDataObject instanceof IDetectorData) {
							IDetectorData detectorData = (IDetectorData)detectorDataObject;
							maker.addDetectorData(detectorData);
						}
					}
				}
			}
		}
		saver.save(new NullProgressMonitor(), maker);
	}

	synchronized private void setAcquisition(IAcquisitionCSD acquisition) {

		if(acquisition != null && !isSetAcquisition && !acquisition.isCompleted()) {
			eventBroker.send(IControlDevicesEvents.TOPIC_CONTROL_DEVICES_ULAN_CONTROL, acquisition.getDevicesProfile().getControlDevices());
			this.acquisition = acquisition;
			acquisition.addPropertyChangeListener(dataAcquisitionChange);
			setTable();
			if(controlAcquisition(acquisition)) {
				eventBroker.send(IAcquisitionEvents.TOPIC_ACQUISITION_CHROMULAN_SET, acquisition);
				isSetAcquisition = true;
				perspective.getContext().set(IAcquisition.class, acquisition);
				buttonStart.setEnabled(true);
				buttonStop.setEnabled(false);
				buttonEnd.setEnabled(true);
				buttonSave.setEnabled(false);
			} else {
				buttonStart.setEnabled(false);
				buttonStop.setEnabled(false);
				buttonEnd.setEnabled(true);
				buttonSave.setEnabled(false);
			}
		}
		redrawTable();
	}

	@Inject
	@Optional
	public void setAcquisition(@UIEventTopic(value = IControlDevicesEvents.TOPIC_CONTROL_DEVICES_ULAN_AVAILABLE) IControlDevices devices) {

		if(this.acquisition != null && !this.isSetAcquisition && controlUsingDevices(acquisition.getDevicesProfile())) {
			setAcquisition(acquisition);
		}
	}

	private void setDefaultParameters() {

		WizardNewAcquisitions newAcquisitionWizard = new WizardNewAcquisitions();
		WizardDialog wizardDialog = new WizardDialog(display.getActiveShell(), newAcquisitionWizard);
		if(wizardDialog.open() == Window.OK) {
			buttonActualAcquisition.setEnabled(true);
			buttonAddAcquisition.setEnabled(true);
			this.file = newAcquisitionWizard.getFile();
			this.supplier = newAcquisitionWizard.getSupplier();
		}
	}

	private void setTable() {

		if(this.acquisition != null) {
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
			tableItem = new TableItem(tableActualAcquisition, SWT.NONE);
			tableItem.setText(0, "AutoContinue");
			if(acquisition.getAutoContinue()) {
				tableItem.setText(1, "Yes");
			} else {
				tableItem.setText(1, "No");
			}
		}
	}

	synchronized public void startRecording() {

		if((acquisition != null) && isSetAcquisition && !acquisition.isRunning() && !acquisition.isCompleted()) {
			acquisition.start();
			eventBroker.send(IAcquisitionEvents.TOPIC_ACQUISITION_CHROMULAN_START_RECORDING, acquisition);
			display.asyncExec(timeRecording);
			buttonStart.setEnabled(false);
			buttonStop.setEnabled(true);
			buttonEnd.setEnabled(false);
			buttonSave.setEnabled(false);
		}
	}

	synchronized public void stopRecording() {

		if((acquisition != null) && isSetAcquisition && acquisition.isRunning()) {
			acquisition.stop();
			eventBroker.send(IAcquisitionEvents.TOPIC_ACQUISITION_CHROMULAN_STOP_RECORDING, acquisition);
			display.timerExec(-1, timeRecording);
			if(acquisition.getAutoContinue()) {
				saveAcquisition();
				endAcquisition();
				setAcquisition((IAcquisitionCSD)acquisitions.setNextAcquisitionActual());
			} else {
				buttonStart.setEnabled(false);
				buttonStop.setEnabled(false);
				buttonEnd.setEnabled(true);
				buttonSave.setEnabled(true);
			}
		}
	}
}
