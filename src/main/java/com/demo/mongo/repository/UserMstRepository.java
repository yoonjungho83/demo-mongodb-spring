package com.demo.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import com.demo.mongo.model.entity.UserMst;

@Repository
public interface UserMstRepository extends MongoRepository<UserMst, String>
                                          , QuerydslPredicateExecutor<UserMst> {

	
}
