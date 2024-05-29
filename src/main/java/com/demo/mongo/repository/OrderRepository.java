package com.demo.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import com.demo.mongo.model.entity.mongo.OrderInfo;

public interface  OrderRepository extends MongoRepository<OrderInfo, String>
										, QuerydslPredicateExecutor<OrderInfo>{
	

	
	
}
