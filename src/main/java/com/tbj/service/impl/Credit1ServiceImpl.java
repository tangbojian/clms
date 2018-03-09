package com.tbj.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tbj.bean.Credit1;
import com.tbj.mapper.Credit1Mapper;
import com.tbj.service.Credit1Service;

@Service("credit1Service")
public class Credit1ServiceImpl implements Credit1Service {
	
	@Autowired
	private Credit1Mapper credit1Mapper;

	public int update(Credit1 model) {
		return credit1Mapper.update(model);
	}

}
