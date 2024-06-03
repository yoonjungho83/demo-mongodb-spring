package com.demo.mongo.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import com.demo.mongo.model.entity.mongo.UserMst;


public interface UserMstRepository extends MongoRepository<UserMst, String>
                                          , QuerydslPredicateExecutor<UserMst> , UserMstRepositoryWrapper {

	
	@Query("{userId: ?0 }")
	public List<UserMst> findByUserId(String userId);

	
	
}
