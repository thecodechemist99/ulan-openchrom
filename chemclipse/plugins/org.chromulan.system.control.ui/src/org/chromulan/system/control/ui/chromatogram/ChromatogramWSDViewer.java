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

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.chromulan.system.control.model.IChromatogramWSDAcquisition;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

@SuppressWarnings("restriction")
public class ChromatogramWSDViewer {

	private class RedrawChromatogram implements Runnable {

		public RedrawChromatogram() {
		}

		@Override
		public void run() {

			if(autoRedraw) {
				redraw();
			} else {
				chromatogramOverView.reloadData();
			}
			display.timerExec(2000, this);
		}
	}

	final static public int DISPLAY_ALL_CHROMATOGRAM = 1;
	final static public int DISPLAY_INTERVAL_CHROMATOGRAM = 2;
	private boolean autoRedraw;
	private int diplayChromatogram;
	@Inject
	private Display display;
	@Inject
	private EHandlerService handlerService;
	private IChromatogramWSDAcquisition chromatogramAcquisition;
	private ChromatogramWSDOverviewUI chromatogramOverView;
	@Inject
	private MPart part;
	private RedrawChromatogram redrawChromatogram;

	public ChromatogramWSDViewer() {
		diplayChromatogram = DISPLAY_INTERVAL_CHROMATOGRAM;
		autoRedraw = true;
		redrawChromatogram = new RedrawChromatogram();
	}

	private void addHandler() {

		Object handler = new Object() {

			@Execute
			public void execute() {

				diplayChromatogram = DISPLAY_ALL_CHROMATOGRAM;
				chromatogramOverView.displayAllChromatogram();
			}
		};
		handlerService.activateHandler("org.chromulan.system.control.ui.chromatogram.displayAllData", handler);
		Object handler2 = new Object() {

			@Execute
			public void execute() {

				diplayChromatogram = DISPLAY_INTERVAL_CHROMATOGRAM;
				chromatogramOverView.displayInteval();
			}
		};
		handlerService.activateHandler("org.chromulan.system.control.ui.chromatogram.displayLastData", handler2);
		Object handler3 = new Object() {

			@Execute
			public void execute() {

				autoRedraw = !autoRedraw;
			}
		};
		handlerService.activateHandler("org.chromulan.system.control.ui.command.chromatogram.displayRefreshData", handler3);
		Object handler4 = new Object() {

			@Execute
			public void execute() {

				PreferenceManager manager = new PreferenceManager();
				PreferencePage page1 = new ChromatogramPreferencePage(chromatogramOverView);
				PreferencePage page2 = new ChromatogramWSDPreferencePage(chromatogramOverView);
				PreferenceNode mainNode = new PreferenceNode("Main", page1);
				PreferenceNode wavelenght = new PreferenceNode("Select wave lenght", page2);
				manager.addToRoot(mainNode);
				manager.addToRoot(wavelenght);
				PreferenceDialog dialog = new PreferenceDialog(Display.getCurrent().getActiveShell(), manager);
				dialog.open();
				redraw();
			}
		};
		handlerService.activateHandler("org.chromulan.system.control.ui.command.chromatogram.displaySettings", handler4);
	}

	@PostConstruct
	public void createPartControl(Composite parent) {

		chromatogramAcquisition = (IChromatogramWSDAcquisition)part.getObject();
		redrawChromatogram(autoRedraw);
		chromatogramOverView = new ChromatogramWSDOverviewUI(parent, SWT.NONE);
		chromatogramOverView.setChromatogram(chromatogramAcquisition);
		addHandler();
	}

	@PreDestroy
	public void destroyPart() {

		display.timerExec(-1, redrawChromatogram);
	}

	private void redraw() {

		chromatogramOverView.reloadData();
		if(diplayChromatogram == DISPLAY_ALL_CHROMATOGRAM) {
			chromatogramOverView.displayAllChromatogram();
		} else if(diplayChromatogram == DISPLAY_INTERVAL_CHROMATOGRAM) {
			chromatogramOverView.displayInteval();
		}
	}

	private void redrawChromatogram(boolean b) {

		if(b) {
			display.timerExec(0, redrawChromatogram);
		} else {
			display.timerExec(-1, redrawChromatogram);
		}
	}
}
