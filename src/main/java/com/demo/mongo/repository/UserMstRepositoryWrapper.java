package com.demo.mongo.repository;

import com.demo.mongo.model.entity.mongo.UserMst;

public interface UserMstRepositoryWrapper {

	
	 UserMst findByUserName(String userName);
}
