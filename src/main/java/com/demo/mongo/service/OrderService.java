package com.demo.mongo.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.demo.mongo.common.DataUtil;
import com.demo.mongo.model.entity.mongo.OrderInfo;
import com.demo.mongo.model.entity.mongo.ProdMst;
import com.demo.mongo.model.entity.mongo.QOrderInfo;
import com.demo.mongo.model.entity.mongo.QProdMst;
import com.demo.mongo.model.entity.mongo.QUserMst;
import com.demo.mongo.model.entity.mongo.SalesProd;
import com.demo.mongo.model.entity.mongo.UserMst;
import com.demo.mongo.repository.OrderRepository;
import com.demo.mongo.repository.ProdMstRepository;
import com.demo.mongo.repository.UserMstRepository;
import com.demo.mongo.support.mongoDB.AggregationBuilder;
import com.mongodb.client.result.UpdateResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

	private final OrderRepository   orderRepository;
	private final ProdMstRepository prodMstRepository;
	private final UserMstRepository userMstRepository;
	private final MongoTemplate mongoTemplate;
	private final DataUtil dataUtil;
	
	private QUserMst qUserMst = QUserMst.userMst;
	private QProdMst qProdMst = QProdMst.prodMst;
	private QOrderInfo qOrderInfo = QOrderInfo.orderInfo;
	
	
	//prod data 생성
	public void createProdMst() {
		prodMstRepository.save(ProdMst.builder().prodNm("사과").orgPrice(1500).salePrice(6000).stockCnt(100).build());
		prodMstRepository.save(ProdMst.builder().prodNm("오렌지").orgPrice(1000).salePrice(2000).stockCnt(100).build());
		prodMstRepository.save(ProdMst.builder().prodNm("수박").orgPrice(5000).salePrice(15000).stockCnt(100).build());
		prodMstRepository.save(ProdMst.builder().prodNm("참외").orgPrice(2000).salePrice(5000).stockCnt(100).build());
		prodMstRepository.save(ProdMst.builder().prodNm("배").orgPrice(2000).salePrice(6500).stockCnt(100).build());
		prodMstRepository.save(ProdMst.builder().prodNm("망고").orgPrice(3000).salePrice(7000).stockCnt(100).build());
		prodMstRepository.save(ProdMst.builder().prodNm("바나나").orgPrice(500).salePrice(1500).stockCnt(100).build());
		prodMstRepository.save(ProdMst.builder().prodNm("키위").orgPrice(400).salePrice(1000).stockCnt(100).build());
		prodMstRepository.save(ProdMst.builder().prodNm("딸기").orgPrice(5000).salePrice(12000).stockCnt(100).build());
		prodMstRepository.save(ProdMst.builder().prodNm("블루베리").orgPrice(7000).salePrice(14000).stockCnt(100).build());
		prodMstRepository.save(ProdMst.builder().prodNm("자몽").orgPrice(3000).salePrice(8000).stockCnt(100).build());
	}
	
	//주문 
	public String createReservation(LocalDateTime reserDate) {
		List<ProdMst> pList =  prodMstRepository.findAll();
		
		int loop = 1000;

		for (int i = 0; i < loop; i++) {
			int plusDay = (int) (Math.random()*10);
			reserDate = reserDate != null? reserDate : LocalDateTime.now().plusDays(plusDay);
			
			//order user 정보 조회
			int c = (int) (Math.random()*1000);
			UserMst user = userMstRepository.findOne(qUserMst.userId.eq("user"+c)).orElse(new UserMst());
//			System.out.println("setReservation > user.getUserId() = " + user.getUserId());
			
			OrderInfo oi = new OrderInfo();
			
			oi.setOrderNum(LocalDateTime.now().toString());
			oi.setReservationDate(reserDate);
			oi.setOrderDate(LocalDateTime.now());
			oi.setOrderUser(user);
			oi.setIscomplete("N");
			
			if(i%2==0) oi.setIsReservation("Y");//짝수일 예약 데이터 입력
			else       oi.setIsReservation("N");
			
			int pcnt = (int) (Math.ceil(Math.random()*10));
			pcnt = pcnt==0 ? 1 : pcnt;//1~10 주문상품 cnt
			for(int  j =0 ; j < pcnt; j++) 
			{
				int idx = (int) Math.round(Math.random()*10);//0~10
				ProdMst prod = pList.get(idx);
				SalesProd sp = new SalesProd();
				int cnt = (int) (Math.random()*10);
				int prodCnt = cnt== 0? 1: cnt;
				sp.setProdNm   (prod.getProdNm());
				sp.setCnt      (prodCnt);
				sp.setOrgPrice (prod.getOrgPrice());
				sp.setSalePrice(prod.getSalePrice());
				
				double temp = (Math.random()*100)/1000;
				double discount = Double.parseDouble(String.format("%.2f", temp))  ;
				int disPrice = (int) (prod.getSalePrice()*(1-discount));
				sp.setDiscountRate(discount*100);
				sp.setDiscountPrice(disPrice);
				sp.setSaleTotPrice(disPrice*prodCnt);
				
				sp.setProdObjId(prod.getId());
				
				oi.getProdList().add(sp);
			}
			
			
			int totP = oi.getProdList().stream().mapToInt(x -> x.getSaleTotPrice()).sum();
			oi.setTotPrice(totP);
			
			orderRepository.save(oi);
		}
		
		
		return "succ";
	}
	
	
	public String setTotPrice() {
		OrderInfo oi = orderRepository.findOne(qOrderInfo.totPrice.isNull()).get();
		
//		for(OrderInfo oi : oderList) {
			int totP = oi.getProdList().stream().mapToInt(x -> x.getSalePrice()).sum();
			oi.setTotPrice(totP);
			
			Query query = dataUtil.getQuery("_id", oi.getId());
			Update update = new Update();
			update.set("totPrice", totP);
			UpdateResult u = mongoTemplate.updateFirst(query, update, OrderInfo.class);
			System.out.println("u result = "+u);
//		}
		
		return "setTotPrice succ";
	}
	
	
	//주문내역 조회
	public void getOrder() {
		
	}
	
	//주문내역 통계 조회
	public String getGrouppingOrder() {
		
		
		mongoDBJoinSample();
		
		mongoDBGroupSample();
		
//		Query query = new Query();
//		query.fields().include("name").exclude("id");
//		List<UserMst> john = mongoTemplate.find(query, UserMst.class);
		
		
//		//필터
//		MatchOperation matchStage = Aggregation.match(new Criteria("foo").is("bar"));
//		//표현
//		ProjectionOperation projectStage = Aggregation.project("foo", "bar.baz");
//		 
//		GroupOperation groupByStateAndSumPop = group("state").sum("pop").as("statePop");
//		
//		//생성
//		Aggregation aggregation 
//		  = Aggregation.newAggregation(matchStage, projectStage);
//
//		//결과
//		AggregationResults<OutType> output 
//		  = mongoTemplate.aggregate(aggregation, "foobar", OutType.class);
		
		
		
	//		Criteria criteria = new Criteria().where("orderId").in("");
	//	    MatchOperation matchOperation = Aggregation.match(criteria);
	//	    
	//	  //group
	//        GroupOperation groupOperation = Aggregation.group("orderId");
	//        
	//      //projection
	//        ProjectionOperation projectionOperation = Aggregation.project("enames")
	//                                                             .and(previousOperation()).as("deptId");
	//        
	//      //aggrgation
	//        AggregationResults<OrderInfo> aggregate =
	//                this.mongoTemplate.aggregate(newAggregation(matchOperation, groupOperation, projectionOperation),
	//                							OrderInfo.class,
	//                                             UserMst.class);
	//        
	//        aggregate.getMappedResults();
		
		return "group succ";
		}
	
	
	
	
	
	
	//aggregation join 샘플
	public AggregationOperation aggregateLookup() {
		
		
		AggregationOperation lookupOperation = 
		(context) -> new Document
		("$lookup",
		 new Document("from", "RoleMst")
		 	.append("let", new Document("roleName1", "$roleName1"))
		    .append( "pipeline"
		    	   , List.of( new Document("$match",
		    			  		new Document("$expr",
		                           new Document( "$and"
		                        		       , List.of(new Document( "$eq"
		                            		                         , List.of("$roleName", "$$roleName1")
		                            		                         )
		                                                )
		                        		       )
		                        )
		                      )
		                   )
		    	   )
		    .append("as", "roleInfo")
		);
		
		return lookupOperation;
	}
	
	
	
	/** join sample 
	아래 내용은 mongoDB left join 
    db.UserMst.aggregate
    ([  
	   { "$match" : { "$and" : [{ "roleList" : { "$exists" : false}}]}}
	 , { "$lookup" : 
			{ "from" : "RoleMst"
			, "let" : { "roleName1" : "$roleName1"}
			, "pipeline" : [{ "$match" : 
								{ "$expr" : 
									{ "$and" : [{ "$eq" : ["$roleName", "$$roleName1"]}]
									}
								}
							}
						   ]
			, "as" : "RoleMst"
			}
		}
	 , { "$unwind" : { "path" : "$RoleMst", "preserveNullAndEmptyArrays" : true}}
	 , { "$sort" : { "userId" : -1, "roleName1" : 1}}
	 , { "$project" : 
			{ "user_id" : "$userId"
			, "user_talbe_role" : "$roleName1"
			, "refId" : 1
			, "roleMstId" : "$RoleMst._id"
			, "roleMstRoleName" : "$RoleMst.roleName"
			}
	   }
	])
	
	** RDB 번역
	SELECT A.userId    AS user_id              
		 , A.roleName1 AS user_talbe_role      
		 , A.refId     AS refId                 
		 , B._id       AS roleMstId       
		 , B.roleName  AS roleMstRoleName
	FROM   USER_MST A
	LEFT OUTER 
	JOIN   ROLE_MST RoleMst
	ON     1=1 
	AND    A.ROLE_LIST IS NULL
	AND    A.ROLENAME1 = B.ROLE_NAME
	ORDER BY A.userId DESC , A.roleName1 
  * */
	public void mongoDBJoinSample() {
		
		AggregationBuilder userLeftJoinRole = new AggregationBuilder(mongoTemplate);
		Criteria ct1 = dataUtil.getCriteria("exists","roleList", false); //where roleList is null
//		Criteria ct2 = dataUtil.getCriteria("eq","userId", "testAdmin1");//  and userId = 'testAdmin'
		List<Object> res1 = 
		userLeftJoinRole.match(Arrays.asList(ct1))//,ct2
		                .join( true //true : left outer join   / false : inner join 
		                	 , "RoleMst"
		                	   //[지역변수] userRoleName : 기준 테이블(UserMst) join key alias / roleName1 : join column 
		                	 , dataUtil.let().andLet("roleName1", "$roleName1")
//		                	                 .andLet("refId"    , "$refId")//조인 여러개 처리시 사용
		                	   //roleName : ref 테이블(RoleMst) join key 
		                	 , dataUtil.on().andOn("$eq", "$roleName", "$$roleName1")
//		                	                .andOn("$eq", "$_id"     , "$$refId" )
		                )
		                .project(Aggregation.project()
		                			.and("userId"          ).as("user_id")
		                			.and("roleName1"       ).as("user_talbe_role")
		                			.and("refId"           ).as("refId")
		                			.and("RoleMst._id"     ).as("roleMstId")
		                			.and("RoleMst.roleName").as("roleMstRoleName")
		                )
		                .sort(Aggregation.sort(Sort.by("user_id").ascending())
		                	             .and (Sort.by("user_talbe_role").ascending())
		                )
		                .aggregate("UserMst", Object.class)//실행 : 기준 테이블 / 리턴 Object 셋팅
		;
		 
		log.info("mongoDBJoinSample result size = {} /  result = {}" , res1.size() ,res1);
	}
	
	
	//그룹핑 sample
	public void mongoDBGroupSample() {
		
		/*
		 * //role 별 인원수 와 age 합계
			db.UserMst
			  .aggregate
			  (
				[
				  { "$match" : { "$and" : [{ "roleList" : { "$ne" : "[]"}}]}}
				, { "$unwind" : { "path" : "$roleList", "preserveNullAndEmptyArrays" : true}}
				, { "$group" : { "_id" : { "roleName" : "$roleList.roleName", "phone" : "$phone"} 
				               , "count" : { "$sum" : 1}, "ageSum" : { "$sum" : "$age"}}}
				, { "$match" : { "$and" : [{ "_id.roleName" : { "$exists" : true}}]}}
				, { "$project" : { "roleName" : "$_id.roleName", "age" : "$ageSum", "count" : 1}}
				, { "$sort" : { "count" : 1}}
				]
			  )
			  
			  RDB 
			  SELECT ROLE_NAME 
			       , PHONE 
			       , COUNT(*) AS COUNT
			       , SUM(AGE) AS AGE_SUM
			  FROM   USER_MST 
			  WHERE  ROLE_LIST IS NOT NULL
			  GROUP BY ROLE_NAME , PHONE
			  ORDER BY COUNT
		 * */
		AggregationBuilder userGroup = new AggregationBuilder(mongoTemplate);
		Criteria where1 = dataUtil.getCriteria("ne","roleList", "[]"); //where roleList is null
//		Criteria where2 = dataUtil.getCriteria("eq","roleList.roleName", "USER"); //where roleList is null
		
		List<Object> resGrp = 
		userGroup.match(Arrays.asList(where1))//,where2
		         .unwind("$roleList", true)
		         .group(Aggregation.group("$roleList.roleName" , "$phone")
		            			   .count().as("count")
		            			   .sum("age").as("ageSum")
		         )
		         .match(Arrays.asList(dataUtil.getCriteria("exists","_id.roleName", true)))
		         .project(Aggregation.project()
		        		 .andExclude("_id")
		        		 .and("_id.roleName").as("roleName1")
		        		 .and("_id.phone"   ).as("phone1")
	             		 .and("ageSum"      ).as("age")
	             		 .and("count"       ).as("count")
			     )
		         .sort(Aggregation.sort(Sort.by("count").ascending()))
		         .aggregate("UserMst", Object.class);
		;
		log.info("mongoDBGroupSample userGroup size = {} /  result = {}" , resGrp.size() ,resGrp);
		
		
		
		
		
		AggregationBuilder orderGroup = new AggregationBuilder(mongoTemplate);
		Criteria where10 = dataUtil.getCriteria("gte","reservationDate", LocalDate.of( 2024,Month.JUNE,5 )); 
		Criteria where11 = dataUtil.getCriteria("eq","isReservation"   , "N"); 
		Criteria where12 = dataUtil.getCriteria("eq","iscomplete"      , "N"); 
		
		List<Object> orderInfoGrp = 
			   orderGroup.match(Arrays.asList(where10,where11,where12))
				         .unwind("$prodList", true)
				         .project(Aggregation.project()
				        		 .andExclude("_id")
				        		 .and("orderNum"               ).as("orderNum"     )
				        		 .and("totPrice"               ).as("totPrice"     )
			             		 .and("$prodList.prodNm"       ).as("prodName"     )
			             		 .and("$prodList.orgPrice"     ).as("orgPrice"     )
			             		 .and("$prodList.cnt"          ).as("orderCnt"     )
			             		 .and("$prodList.discountPrice").as("discountPrice")
			             		 .and("$prodList.discountRate" ).as("discountRate" )
			             		 .and("$prodList.salePrice"    ).as("salePrice"    )
			             		 .and("$prodList.saleTotPrice" ).as("saleTotPrice" )
					     )
				         .group(Aggregation.group("$prodName" )
				            			   .count().as("sumCnt")
				            			   .max("$discountPrice").as("fruitPrice")
				            			   .sum("$saleTotPrice").as("saleTotPrice")
				         )
				         .sort(Aggregation.sort(Sort.by("_id.prodName").ascending()))
				         .aggregate("OrderInfo", Object.class);
		
		log.info("mongoDBGroupSample orderInfoGrp size = {} /  result = {}" , orderInfoGrp.size() ,orderInfoGrp);
	}
	
	
	
	/*
	 *
	 
	 
	 
	  Aggregation aggri = 
	  newAggregation(
	  	group("ctgry", "reg_date", "status")
          .sum("silence_cnt").as("silenceSum")
          .sum("attack_cnt").as("attackSum")
          .push("status").as("status")             /////push/////
          .push("reg_date").as("reg_date")         /////push/////
      ,match(Criteria.where("status").is(0)
                     .and("reg_date").gte(srchdate1).lte(srchdate2))
      ,group("ctgry")
             .sum("silenceSum").as("silenceSum")
             .sum("attackSum").as("attackSum")
      ,sort(Sort.Direction.ASC,"_id")
   );

               AggregationResults results = mongoTemplate.aggregate(aggri, "audio_info", Map.class);

               List<Map> list = results.getMappedResults();
	 */
	
	
}
