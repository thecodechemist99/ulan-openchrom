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
package org.chromulan.system.control.ui.wizard;

import java.util.List;

import org.chromulan.system.control.device.IControlDevice;
import org.chromulan.system.control.device.IDevicesProfile;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;

public class WizardNewDevicesProfile extends Wizard {

	private IDevicesProfile devicesProfile;
	private WizardPage page1;

	public WizardNewDevicesProfile(List<IControlDevice> devices) {
		super();
		page1 = new WizarsNewDevicesProfilePage(devices);
	}

	@Override
	public void addPages() {

		addPage(page1);
	}

	public IDevicesProfile getDevicesProfile() {

		return devicesProfile;
	}

	@Override
	public boolean performFinish() {

		return page1.isPageComplete();
	}

	public void setDevicesProfile(IDevicesProfile devicesProfile) {

		this.devicesProfile = devicesProfile;
	}
}
