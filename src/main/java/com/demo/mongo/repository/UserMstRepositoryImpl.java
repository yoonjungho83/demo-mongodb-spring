package com.demo.mongo.repository;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import com.demo.mongo.model.entity.mongo.QUserMst;
import com.demo.mongo.model.entity.mongo.UserMst;

@Repository
public class UserMstRepositoryImpl extends QuerydslRepositorySupport implements UserMstRepositoryWrapper{

	
	 private static final QUserMst userMst = QUserMst.userMst;
	
	public UserMstRepositoryImpl(MongoOperations operations) {
		super(operations);
		// TODO Auto-generated constructor stub
	}

	@Override
	public UserMst findByUserName(String userName) {
		// TODO Auto-generated method stub
		
		return from(userMst).where(userMst.userName.eq(userName)).fetchOne();
	}

}
