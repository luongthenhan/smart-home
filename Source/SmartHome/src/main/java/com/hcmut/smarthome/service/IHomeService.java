package com.hcmut.smarthome.service;

import java.util.List;

import com.hcmut.smarthome.model.Home;

public interface IHomeService {
	List<Home> getAllHomes(int userId);
}
