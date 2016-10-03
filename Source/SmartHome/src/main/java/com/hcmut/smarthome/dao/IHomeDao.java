package com.hcmut.smarthome.dao;

import java.util.List;

import com.hcmut.smarthome.entity.HomeEntity;

public interface IHomeDao extends ICommonDao<HomeEntity>{
	List<HomeEntity> getAllHomes(int userId);
}
