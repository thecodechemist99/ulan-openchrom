/*******************************************************************************
 * Copyright (c) 2016, 2018, 2019 Jan Holy.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 * Alexander Kerner - Generics
 *******************************************************************************/
package org.chromulan.system.control.ui.acquisitions;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.chromulan.system.control.device.IDevicesProfile;
import org.chromulan.system.control.model.AcquisitionCSD;
import org.chromulan.system.control.model.AcquisitionCSDSaver;
import org.chromulan.system.control.model.AcquisitionMSD;
import org.chromulan.system.control.model.AcquisitionMSDSaver;
import org.chromulan.system.control.model.AcquisitionWSD;
import org.chromulan.system.control.model.AcquisitionWSDSaver;
import org.chromulan.system.control.model.IAcquisition;
import org.chromulan.system.control.model.IAcquisitionCSD;
import org.chromulan.system.control.model.IAcquisitionMSD;
import org.chromulan.system.control.model.IAcquisitionSaver;
import org.chromulan.system.control.model.IAcquisitionWSD;
import org.chromulan.system.control.preferences.PreferenceSupplier;
import org.chromulan.system.control.ui.acquisition.support.AcquisitionSavePreferencePage;
import org.chromulan.system.control.ui.acquisition.support.AcquisitionSettingsPreferencePage;
import org.chromulan.system.control.ui.acquisition.support.ChromatogramFilesDialog;
import org.chromulan.system.control.ui.devices.support.ProfilePreferencePage;
import org.chromulan.system.control.ui.wizard.WizardModelAcquisition;
import org.chromulan.system.control.ui.wizard.WizardNewAcquisition;
import org.chromulan.system.control.ui.wizard.WizardNewAcquisitions;
import org.eclipse.chemclipse.converter.chromatogram.IChromatogramConverterSupport;
import org.eclipse.chemclipse.converter.core.ISupplier;
import org.eclipse.chemclipse.csd.converter.chromatogram.ChromatogramConverterCSD;
import org.eclipse.chemclipse.msd.converter.chromatogram.ChromatogramConverterMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.ux.extension.ui.provider.ISupplierEditorSupport;
import org.eclipse.chemclipse.wsd.converter.chromatogram.ChromatogramConverterWSD;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.osgi.service.prefs.BackingStoreException;

public class AcquisitionsAdministator {

	public static final String PREFERENCE_FILE = "file";
	public static final String PREFERENCE_SUPPLIER_CSD = "supplierCDS";
	public static final String PREFERENCE_SUPPLIER_MSD = "supplierMSD";
	public static final String PREFERENCE_SUPPLIER_TYPE = "supplierTYPE";
	public static final String PREFERENCE_SUPPLIER_WSD = "supplierWSD";
	private String defClassAcquisition;
	private File defFile;
	private ISupplier defSupplierCSD;
	private ISupplier defSupplierMSD;
	private ISupplier defSupplierWSD;

	public AcquisitionsAdministator() {
		String defClass = IAcquisitionCSD.class.getName();
		String filePath = PreferenceSupplier.INSTANCE().getPreferences().get(PREFERENCE_FILE, null);
		String supplierCSD = PreferenceSupplier.INSTANCE().getPreferences().get(PREFERENCE_SUPPLIER_CSD, null);
		String supplierMSD = PreferenceSupplier.INSTANCE().getPreferences().get(PREFERENCE_SUPPLIER_MSD, null);
		String supplierWSD = PreferenceSupplier.INSTANCE().getPreferences().get(PREFERENCE_SUPPLIER_WSD, null);
		defClassAcquisition = PreferenceSupplier.INSTANCE().getPreferences().get(PREFERENCE_SUPPLIER_TYPE, defClass);
		setDefaultParameters(filePath, supplierCSD, supplierMSD, supplierWSD);
	}

	public void acquisitionSettings(IAcquisition acquisition, Display display, EModelService modelService, MApplication application, EPartService partService) {

		if(!acquisition.isCompleted()) {
			PreferenceManager manager = new PreferenceManager();
			AcquisitionSettingsPreferencePage settings = new AcquisitionSettingsPreferencePage(acquisition);
			ProfilePreferencePage page = new ProfilePreferencePage(acquisition.getDevicesProfile());
			AcquisitionSavePreferencePage save = new AcquisitionSavePreferencePage(acquisition);
			PreferenceNode nodeBase = new PreferenceNode("Main", settings);
			PreferenceNode nodeProfile = new PreferenceNode("Devices", page);
			PreferenceNode nodeSave = new PreferenceNode("Save", save);
			manager.addToRoot(nodeBase);
			manager.addToRoot(nodeProfile);
			manager.addToRoot(nodeSave);
			PreferenceDialog dialog = new PreferenceDialog(Display.getCurrent().getActiveShell(), manager);
			dialog.open();
		} else {
			ChromatogramFilesDialog dialog = new ChromatogramFilesDialog(display.getActiveShell(), acquisition.getAcquisitionSaver());
			if(dialog.open() == Window.OK) {
				List<IProcessingInfo> chromatogramFiles = dialog.getChromatogramExportConverterProcessingInfos();
				ISupplierEditorSupport support = null;
				if(acquisition instanceof IAcquisitionCSD) {
					support = new org.eclipse.chemclipse.ux.extension.csd.ui.support.ChromatogramEditorSupport();
				} else if(acquisition instanceof IAcquisitionCSD) {
					support = new org.eclipse.chemclipse.ux.extension.wsd.ui.support.ChromatogramEditorSupport();
				} else if(acquisition instanceof IAcquisitionCSD) {
					support = new org.eclipse.chemclipse.ux.extension.msd.ui.support.ChromatogramEditorSupport();
				}
				if(support != null) {
					for(IProcessingInfo<File> chromatogramFile : chromatogramFiles) {
						support.openEditor(chromatogramFile.getProcessingResult());
					}
				}
			}
		}
	}

	private IAcquisition createAcqusition(WizardModelAcquisition model) {

		if(model.acquisitionType.getValue().equals(IAcquisitionCSD.class.getName())) {
			IAcquisitionCSD acquisition = new AcquisitionCSD();
			saveDefChromatogramType(IAcquisitionCSD.class);
			IAcquisitionSaver saver = new AcquisitionCSDSaver(acquisition);
			saver.setSupplier(defSupplierCSD);
			saver.setFile(model.folder.getValue());
			acquisition.setAcquisitionSaver(saver);
			return acquisition;
		} else if(model.acquisitionType.getValue().equals(IAcquisitionMSD.class.getName())) {
			IAcquisitionMSD acquisition = new AcquisitionMSD();
			saveDefChromatogramType(IAcquisitionMSD.class);
			IAcquisitionSaver saver = new AcquisitionMSDSaver(acquisition);
			saver.setSupplier(defSupplierMSD);
			saver.setFile(model.folder.getValue());
			acquisition.setAcquisitionSaver(saver);
			return acquisition;
		} else if(model.acquisitionType.getValue().equals(IAcquisitionWSD.class.getName())) {
			IAcquisitionWSD acquisition = new AcquisitionWSD();
			saveDefChromatogramType(IAcquisitionWSD.class);
			IAcquisitionSaver saver = new AcquisitionWSDSaver(acquisition);
			saver.setSupplier(defSupplierWSD);
			saver.setFile(model.folder.getValue());
			acquisition.setAcquisitionSaver(saver);
			return acquisition;
		}
		return null;
	}

	public List<IAcquisition> createAcqusitions(List<IDevicesProfile> devicesProfil, Display display) {

		List<IAcquisition> acquisitions = new LinkedList<>();
		WizardNewAcquisition newAcquisitionWizard = new WizardNewAcquisition(devicesProfil);
		newAcquisitionWizard.getModel().acquisitionType.setValue(defClassAcquisition);
		newAcquisitionWizard.getModel().folder.setValue(defFile);
		WizardDialog wizardDialog = new WizardDialog(display.getActiveShell(), newAcquisitionWizard);
		if(wizardDialog.open() == Window.OK) {
			int numberAcquisition = newAcquisitionWizard.getModel().numberofAcquisitions.getValue();
			for(int i = 1; i <= numberAcquisition; i++) {
				WizardModelAcquisition model = newAcquisitionWizard.getModel();
				IAcquisition acquisition = createAcqusition(model);
				if(acquisition != null) {
					String name = getNameAcquisition(model.name.getValue(), i, numberAcquisition);
					acquisition.setName(name);
					acquisition.setAutoStop(model.autoStop.getValue());
					acquisition.setDuration(model.duration.getValue());
					acquisition.setDevicesProfile(model.devicesProfile.getValue());
					acquisition.setDescription(model.description.getValue());
					acquisition.setAmount(model.amount.getValue());
					acquisition.setAnalysis(model.analysis.getValue());
					acquisition.setColumn(model.column.getValue());
					acquisition.setDetection(model.detection.getValue());
					acquisition.setFlowRate(model.flowRate.getValue());
					acquisition.setFlowRateUnit(model.flowRateUnit.getValue());
					acquisition.setMobilPhase(model.mobilPhase.getValue());
					acquisition.setTemperature(model.temperature.getValue());
					acquisition.setTemperatureUnit(model.temperatureUnit.getValue());
					acquisition.setISTDAmount(model.ISTDAmount.getValue());
					acquisition.setInjectionVolume(model.injectionVolume.getValue());
					acquisitions.add(acquisition);
				}
			}
		}
		return acquisitions;
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

	public boolean isDefParametersSet() {

		if(defFile != null && defFile.exists() && defSupplierCSD != null && defSupplierMSD != null && defSupplierWSD != null) {
			return true;
		} else {
			return false;
		}
	}

	private void saveDefChromatogramType(Class<? extends IAcquisition> defClassAcquisition) {

		PreferenceSupplier.INSTANCE().getPreferences().put(PREFERENCE_SUPPLIER_TYPE, defClassAcquisition.getName());
	}

	private void setDefaultParameters(String path, String supplierCSD, String supplierMSD, String supplierWSD) {

		if(path != null) {
			this.defFile = new File(path);
			PreferenceSupplier.INSTANCE().getPreferences().put(PREFERENCE_FILE, path);
		}
		if(supplierCSD != null) {
			IChromatogramConverterSupport support = ChromatogramConverterCSD.getInstance().getChromatogramConverterSupport();
			List<ISupplier> suppliers = support.getExportSupplier();
			for(ISupplier supplier : suppliers) {
				if(supplier.getId().equals(supplierCSD)) {
					this.defSupplierCSD = supplier;
					PreferenceSupplier.INSTANCE().getPreferences().put(PREFERENCE_SUPPLIER_CSD, supplierCSD);
					try {
						PreferenceSupplier.INSTANCE().getPreferences().flush();
					} catch(BackingStoreException e) {
						// TODO logger.warn(e);
					}
					break;
				}
			}
		}
		if(supplierMSD != null) {
			IChromatogramConverterSupport support = ChromatogramConverterMSD.getInstance().getChromatogramConverterSupport();
			List<ISupplier> suppliers = support.getExportSupplier();
			for(ISupplier supplier : suppliers) {
				if(supplier.getId().equals(supplierMSD)) {
					this.defSupplierMSD = supplier;
					PreferenceSupplier.INSTANCE().getPreferences().put(PREFERENCE_SUPPLIER_MSD, supplierMSD);
					try {
						PreferenceSupplier.INSTANCE().getPreferences().flush();
					} catch(BackingStoreException e) {
						// TODO logger.warn(e);
					}
					break;
				}
			}
		}
		if(supplierWSD != null) {
			IChromatogramConverterSupport support = ChromatogramConverterWSD.getInstance().getChromatogramConverterSupport();
			List<ISupplier> suppliers = support.getExportSupplier();
			for(ISupplier supplier : suppliers) {
				if(supplier.getId().equals(supplierWSD)) {
					this.defSupplierWSD = supplier;
					PreferenceSupplier.INSTANCE().getPreferences().put(PREFERENCE_SUPPLIER_WSD, supplierWSD);
					try {
						PreferenceSupplier.INSTANCE().getPreferences().flush();
					} catch(BackingStoreException e) {
						// TODO logger.warn(e);
					}
					break;
				}
			}
		}
	}

	public boolean setDefaultParametersWizard(Display display) {

		WizardNewAcquisitions newAcquisitionWizard = new WizardNewAcquisitions(defFile, defSupplierCSD, defSupplierMSD, defSupplierWSD);
		WizardDialog wizardDialog = new WizardDialog(display.getActiveShell(), newAcquisitionWizard);
		if(wizardDialog.open() == Window.OK) {
			setDefaultParameters(newAcquisitionWizard.getFile().getAbsolutePath(), newAcquisitionWizard.getSupplierCSD().getId(), newAcquisitionWizard.getSupplierMSD().getId(), newAcquisitionWizard.getSupplierWSD().getId());
			return true;
		}
		return false;
	}
}
