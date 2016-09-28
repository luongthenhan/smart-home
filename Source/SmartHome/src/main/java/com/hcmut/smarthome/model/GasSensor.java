package com.hcmut.smarthome.model;

import com.hcmut.smarthome.entity.DeviceEntity;


public class GasSensor extends Device {
	private static final long serialVersionUID = 1L;
	private boolean isDanger;
	
	public GasSensor(Device device) {
		super(device);
		this.isDanger = false;
	}
	
	public GasSensor(DeviceEntity device) {
		super(device);
		this.isDanger = false;
	}

	public boolean isDanger() {
		return isDanger;
	}

	public void setDanger(boolean isDanger) {
		this.isDanger = isDanger;
	}
	
}
