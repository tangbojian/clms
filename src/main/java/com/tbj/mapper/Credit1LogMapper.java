package com.tbj.mapper;

import com.tbj.bean.Credit1Log;


public interface Credit1LogMapper {

	public int insert(Credit1Log model);
	
	public int countByKey(String key);
	
}
