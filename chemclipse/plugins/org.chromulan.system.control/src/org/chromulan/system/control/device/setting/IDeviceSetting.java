package org.chromulan.system.control.device.setting;

import java.io.Externalizable;
import java.util.HashMap;

public interface IDeviceSetting extends  Externalizable{
	
	String getName();
	
	void setName(String name);
	
	String getPluginID();
		
	HashMap<String,IValue<?>>getValues();
	
	void updateValueListeners();
	
	void addUpdateValueListener(IValueChangeListener listener);
	
}
