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
package org.chromulan.system.control.devices.connection;

import java.io.IOException;

import javax.inject.Singleton;

import org.eclipse.e4.core.di.annotations.Creatable;

import net.sourceforge.ulan.base.ULanCommunicationInterface;
import net.sourceforge.ulan.base.exceptions.HandleHasNotBeenInitializedException;

@Creatable
@Singleton
public class ULanConnection {

	public ULanConnection() {
	}

	public void close() throws HandleHasNotBeenInitializedException, IOException {

		ULanCommunicationInterface.close();
	}

	public boolean isOpen() {

		return ULanCommunicationInterface.isOpen();
	}

	public void open() throws HandleHasNotBeenInitializedException, IOException {

		ULanCommunicationInterface.open();
	}
}
