package com.tbj.service;

import com.tbj.bean.Credit1Log;


public interface Credit1LogService {

	public int insert(Credit1Log model);
	
	public int countByKey(String key);
	
}
