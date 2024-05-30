package com.demo.mongo.service;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.demo.mongo.common.DataUtil;
import com.demo.mongo.model.entity.mongo.QOrderInfo;
import com.demo.mongo.model.entity.mongo.QProdMst;
import com.demo.mongo.model.entity.mongo.QUserMst;
import com.demo.mongo.repository.OrderRepository;
import com.demo.mongo.repository.ProdMstRepository;
import com.demo.mongo.repository.UserMstRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SampleService {

	
	private final OrderRepository   orderRepository;
	private final ProdMstRepository prodMstRepository;
	private final UserMstRepository userMstRepository;
	private final MongoTemplate mongoTemplate;
	private final DataUtil dataUtil;
	
	private QUserMst     qUserMst = QUserMst.userMst;
	private QProdMst     qProdMst = QProdMst.prodMst;
	private QOrderInfo qOrderInfo = QOrderInfo.orderInfo;
	
	
	public void getUser(String type) {
		
	}
	
	
	public void getOrder() {
		
	}
	
	
}
