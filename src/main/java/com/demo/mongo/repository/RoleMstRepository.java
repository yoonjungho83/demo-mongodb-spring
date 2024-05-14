package com.demo.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import com.demo.mongo.model.entity.RoleMst;

@Repository
public interface RoleMstRepository extends MongoRepository<RoleMst, String>
                                          , QuerydslPredicateExecutor<RoleMst> {

	
}
