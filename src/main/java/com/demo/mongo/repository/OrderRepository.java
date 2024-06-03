package com.demo.mongo.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import com.demo.mongo.model.entity.mongo.AggregateVo;
import com.demo.mongo.model.entity.mongo.OrderInfo;

public interface  OrderRepository extends MongoRepository<OrderInfo, String>
										, QuerydslPredicateExecutor<OrderInfo>{
	

	
	@Aggregation(pipeline = "?0")
	public List<OrderInfo> aggregateExec(String aggregateStr);
	
	
	
	
	@Aggregation(pipeline = ""
			+ "  { $match: { $and: [{ 'iscomplete': 'N' },{'orderNum':'2024-05-27T17:28:01.623070900'}] } },"
//			+ "  {"
//			+ "    $unwind: {"
//			+ "      path: \"$prodList\","
//			+ "      preserveNullAndEmptyArrays: true"
//			+ "    }"
//			+ "  },"
//			+ "  {"
//			+ "    $match: {"
//			+ "      $and: [{ \"prodList.prodNm\": \"키위\" }]"
//			+ "    }"
//			+ "  },"
//			+ "  {"
//			+ "    $group: {"
//			+ "      _id: \"$prodList.prodNm\","
//			+ "      sumCnt: { $sum: 1 },"
//			+ "      fruitPrice: {"
//			+ "        $max: \"$prodList.discountPrice\""
//			+ "      },"
//			+ "      saleTotPrice: {"
//			+ "        $sum: \"$prodList.saleTotPrice\""
//			+ "      }"
//			+ "    }"
//			+ "  },"
			+ "  {"
			+ "    $project: {"
			+ "      _id: 0,"
			+ "      orderNum: 1,"
			+ "      prodNm: 'prodList.prodNm',"
			+ "      totPrice: 1,"
			+ "      reservationDate: 1"
			+ "    }"
//			+ "  },"
//			+ "  { $sort: { prodNm: 1 } }"
			+ "")
	public List<AggregateVo> aggregateExec2();
}
