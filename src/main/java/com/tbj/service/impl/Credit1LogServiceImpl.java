package com.tbj.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tbj.bean.Credit1Log;
import com.tbj.mapper.Credit1LogMapper;
import com.tbj.service.Credit1LogService;

@Service("credit1LogService")
public class Credit1LogServiceImpl implements Credit1LogService {
	
	@Autowired
	private Credit1LogMapper credit1LogMapper;

	public int insert(Credit1Log model) {
		return credit1LogMapper.insert(model);
	}

	public int countByKey(String key) {
		return credit1LogMapper.countByKey(key);
	}

}
