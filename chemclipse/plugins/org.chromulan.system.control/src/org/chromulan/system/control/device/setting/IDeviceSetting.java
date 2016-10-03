package org.chromulan.system.control.device.setting;

import java.io.Externalizable;
import java.util.HashMap;

public interface IDeviceSetting extends  Externalizable{
	
	String getName();
	
	void setName(String name);
	
	String getDeviceType();
	
	void setDeviceType(String type);
	
	String getDeviceID();
	
	void setDeviceID(String id);
	
	String getPluginID();
	
	void setPlugnID(String id);
		
	HashMap<String,IValue<?>>getValues();
	
	void updateValueListeners();
	
	void addUpdateValueListener(IValueChangeListener listener);
	
	
}
