package com.demo.mongo.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import com.demo.mongo.model.entity.mongo.UserMst;

@Repository
public interface UserMstRepository extends MongoRepository<UserMst, String>
                                          , QuerydslPredicateExecutor<UserMst> {

	
	@Query("{userId: ?0 }")
	public List<UserMst> findByUserId(String userId);

	
}
