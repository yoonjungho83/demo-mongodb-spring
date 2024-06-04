package com.demo.mongo.service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.demo.mongo.common.MongoUtil;
import com.demo.mongo.model.aggregation.MongoProps;
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
import com.demo.mongo.support.mongoDB.MongoPropsBuilder;
import com.mongodb.client.result.UpdateResult;

import lombok.Data;
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
	private final MongoUtil mongoUtil;
	
	private QUserMst qUserMst = QUserMst.userMst;
	private QProdMst qProdMst = QProdMst.prodMst;
	private QOrderInfo qOrderInfo = QOrderInfo.orderInfo;
	
	
	//prod mst data 생성
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
	
	//주문  생성
	public String createReservation(int createCnt) {
		List<ProdMst> pList =  prodMstRepository.findAll();
		
		int loop = createCnt;
		

		for (int i = 0; i < loop; i++) {
			int plusDay = (int) (Math.random()*100);
			LocalDateTime reserDate = LocalDateTime.now().plusDays(plusDay);
			
			//order user 정보 조회
			int c = (int) (Math.random()*1000);
			UserMst user = userMstRepository.findOne(qUserMst.userId.eq("user"+c)).orElse(new UserMst());
//			log.info("setReservation > user.getUserId() = " + user.getUserId());
			
			OrderInfo oi = new OrderInfo();
			
			oi.setOrderNum(LocalDateTime.now().toString());
			oi.setReservationDate(reserDate);
			oi.setOrderDate(LocalDateTime.now());
			oi.setOrderUser(user);
			oi.setIscomplete("N");
			
			if(i%2==0) oi.setIsReservation("Y");//짝수일 예약 데이터 입력
			else       oi.setIsReservation("N");
			
			Long adminCnt = user.getRoleList().stream().filter(x->x.getRoleName().equals("ADMIN")).count();
			Long mngCnt   = user.getRoleList().stream().filter(x->x.getRoleName().equals("MANAGER")).count();
			Long userCnt  = user.getRoleList().stream().filter(x->x.getRoleName().equals("USER")).count();
			
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
				
				double discount = adminCnt > 0 ? 0.2 : mngCnt > 0 ? 0.1 : 0.0  ;//할인율
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
	
	//수정
	public String setTotPrice() {
		OrderInfo oi = orderRepository.findOne(qOrderInfo.totPrice.isNull()).get();
		
//		for(OrderInfo oi : oderList) {
			int totP = oi.getProdList().stream().mapToInt(x -> x.getSalePrice()).sum();
			oi.setTotPrice(totP);
			
			Query query = mongoUtil.getQuery("_id", oi.getId());
			Update update = new Update();
			update.set("totPrice", totP);
			UpdateResult u = mongoTemplate.updateFirst(query, update, OrderInfo.class);
			log.info("u result = "+u);
//		}
		
		return "setTotPrice succ";
	}
	
	
	
	//주문내역 조회 sample
	public String getGrouppingOrder() {
		
		mongoDBJoinSample();
		mongoDBGroupSample1();
		mongoDBGroupSample2();
		mongoDBAggregateSampleDto();
		
		return "group succ";
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
		Criteria ct1 = mongoUtil.getCriteria("exists","roleList", false); //where roleList is null
//		Criteria ct2 = mongoUtil.getCriteria("eq","userId", "testAdmin1");//  and userId = 'testAdmin'
		List<Object> res1 = 
		userLeftJoinRole.match(Arrays.asList(ct1))//,ct2
		                .join( true //true : left outer join   / false : inner join 
		                	 , "RoleMst"
		                	   //[지역변수] userRoleName : 기준 테이블(UserMst) join key alias / roleName1 : join column 
		                	 , mongoUtil.let().andLet("roleName1", "$roleName1")
//		                	                 .andLet("refId"    , "$refId")//조인 여러개 처리시 사용
		                	   //roleName : ref 테이블(RoleMst) join key 
		                	 , mongoUtil.on().andOn("$eq", "$roleName", "$$roleName1")
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
	public void mongoDBGroupSample1() {
		
		AggregationBuilder userGroup = new AggregationBuilder(mongoTemplate);
		Criteria where1 = mongoUtil.getCriteria("ne","roleList", "[]"); //where roleList is null
//		Criteria where2 = mongoUtil.getCriteria("eq","roleList.roleName", "USER"); //where roleList is null
		
		List<Object> resGrp = 
		userGroup.match(Arrays.asList(where1))//,where2
		         .unwind("$roleList", true)
		         .group(Aggregation.group("$roleList.roleName" , "$phone")
		            			   .count().as("count")
		            			   .sum("age").as("ageSum")
		         )
		         .match(Arrays.asList(mongoUtil.getCriteria("exists","_id.roleName", true)))
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
		log.info("mongoDBGroupSample1 userGroup size = {} /  result = {}" , resGrp.size() ,resGrp);
		
	}
	
	
	/** 2024-06-03일 기준
	 * 
	 * MONGO DB EX)
	 * [ 
		 { "$match" : { "$and" : [{ "reservationDate" : { "$gte" : { "$date" : "2024-06-05T01:39:35.723Z"}}}
		                        , { "reservationDate" : { "$lte" : { "$date" : "2024-06-08T01:39:35.724Z"}}}]}}
		 , { "$unwind" : { "path" : "$prodList", "preserveNullAndEmptyArrays" : true}}
		 , { "$group" : { "_id"          : "$prodList.prodNm"
		                , "sumCnt"       : { "$sum" : 1}
						, "fruitPrice"   : { "$max" : "$prodList.discountPrice"}
						, "saleTotPrice" : { "$sum" : "$prodList.saleTotPrice"}}}
		 , { "$project" : { "_id" : 0, "prodNm" : "$_id", "fruitPrice" : 1, "saleTotPrice" : 1, "sumCnt" : 1}}
		 , { "$sort" : { "prodNm" : 1}}
	   ]
	 * 
	 * 
	 * RDB EX)
	 * SELECT A.PROD_NAME           prodNm
	 *      , MAX(A.FRUIT_PRICE)    fruitPrice
	 *      , SUM(A.SALE_TOT_PRICE) saleTotPrice
	 * FROM   ORDER_INFO A
	 * LEFT OUTER 
	 * JOIN   PROD_LIST B   //해당 테이블은 ORDER_INFO의 배열로 존재함.
	 * ON     A.PROD_NAME = B.PROD_NAME 
	 * AND    A.RESERVATION_DATE >= '2024-06-03'
	 * AND    A.RESERVATION_DATE <= '2024-06-08'
	 * GROUP BY A.PROD_NAME
	 * ORDER BY A.PROD_NAME
	 */
	public void mongoDBGroupSample2() {
		Date sDate = mongoUtil.localDateTimeToDate(LocalDateTime.now());//.plusDays(2)
		Date eDate = mongoUtil.localDateTimeToDate(LocalDateTime.now().plusDays(5));
		log.info("localDateTimeToDate sDate  === "+sDate);
		log.info("localDateTimeToDate eDate  === "+eDate);
		
		AggregationBuilder orderGroup = new AggregationBuilder(mongoTemplate);
		Criteria where10 = mongoUtil.getCriteria("gte","reservationDate", sDate); 
		Criteria where11 = mongoUtil.getCriteria("lte","reservationDate", eDate); 
//		Criteria where11 = mongoUtil.getCriteria("eq","isReservation"   , "N"); 
//		Criteria where12 = mongoUtil.getCriteria("eq","iscomplete"      , "N"); 
//		Criteria where13 = mongoUtil.getCriteria("eq","prodList.prodNm"      , "키위"); 
		
		List<Object> orderInfoGrp = 
			   orderGroup.match(Arrays.asList(where10,where11))//,,where11,where12
				         .unwind("$prodList", true)
//				         .match(Arrays.asList(where13))
//				         .project(Aggregation.project()
//				        		 .andExclude("_id")
//				        		 .and("orderNum"               ).as("orderNum"     )
//				        		 .and("totPrice"               ).as("totPrice"     )
//			             		 .and("$prodList.prodNm"       ).as("prodName"     )
//			             		 .and("$prodList.orgPrice"     ).as("orgPrice"     )
//			             		 .and("$prodList.cnt"          ).as("orderCnt"     )
//			             		 .and("$prodList.discountPrice").as("discountPrice")
//			             		 .and("$prodList.discountRate" ).as("discountRate" )
//			             		 .and("$prodList.salePrice"    ).as("salePrice"    )
//			             		 .and("$prodList.saleTotPrice" ).as("saleTotPrice" )
//					     )
//				         .group(Aggregation.group("$prodName" )
//				            			   .count().as("sumCnt")
//				            			   .max("$discountPrice").as("fruitPrice")
//				            			   .sum("$saleTotPrice").as("saleTotPrice")
//				         )
				         .group(Aggregation.group("$prodList.prodNm" )
				        		 .count().as("sumCnt")
				        		 .max("$prodList.discountPrice").as("fruitPrice")
				        		 .sum("$prodList.saleTotPrice").as("saleTotPrice")
				        		 )
				         .project(Aggregation.project()
			        		 .andExclude("_id")
			        		 .and("_id"   ).as("prodNm"     )
			        		 .and("fruitPrice"   ).as("fruitPrice"     )
		             		 .and("saleTotPrice" ).as("saleTotPrice"     )
		             		 .and("sumCnt"       ).as("sumCnt"     )
				         )
				         .sort(Aggregation.sort(Sort.by("prodNm").ascending()))
				         .aggregate("OrderInfo", Object.class);
		
		log.info("mongoDBGroupSample2 orderInfoGrp size = {} /  result = {}" , orderInfoGrp.size() ,orderInfoGrp);
	}
	
	
	public void mongoDBAggregateSampleDto() {
		
		Aggregation agg = Aggregation.newAggregation(
				Aggregation.match(Criteria.where("orderNum").is("2024-05-27T17:28:01.623070900")),
				Aggregation.unwind("$prodList"),
//				Aggregation.addFields().addField("").withValue(projectToMatchModel).build(),
				 new AggregationOperation(){ 
			         @Override 
			         public Document toDocument(AggregationOperationContext aoc) {
			            return new Document("$addFields",new Document("testName","$prodList.prodNm"));
			         }
			      },
				Aggregation.group("$testName")
				           .sum("$prodList.salePrice").as("total")
				           .sum("$prodList.cnt").as("totCnt")
				           .count().as("cnt"),
				Aggregation.project().andExpression("_id").as("prodNm")
	                                 .andExpression("total").as("total")
	                                 .andExpression("totCnt").as("totCnt")
	                                 .andExpression("cnt").as("cnt")
	                                 ,
				Aggregation.sort(Sort.Direction.DESC, "total")
					
			);
		
		log.info("mongoDBAggregateSampleDto > agg ===================> "+agg);
		
		AggregationResults<TempDto> groupResults = mongoTemplate.aggregate(agg, OrderInfo.class, TempDto.class);
		List<TempDto> result1 = groupResults.getMappedResults();
		
		log.info("mongoDBAggregateSampleDto > result1 == "+result1);
	}
	@Data
	public class TempDto {

		private String prodNm;

		private long total;
		private long cnt;
		private long totCnt;

	}
	
	
	
	
	
	/* mongo db sample query
	 * [ 
		{ "$match" : { "$and" : [{ "reservationDate" : { "$gte" : ISODate('2024-06-03T05:19:51.247Z')}}
		                         , { "reservationDate" : { "$lte" : ISODate('2024-06-04T05:19:51.247Z')}}]}
		  }
		, { "$unwind" : { "path" : "$prodList", "preserveNullAndEmptyArrays" : true}}
		, { "$project" : { "prodName" : "$prodList.prodNm"
		                 , "fCnt" : "$prodList.cnt"
						 , "salePrice" : "$prodList.salePrice"
						 , "newReserveDate" : { "$dateToString" : { "format" : "%Y-%m-%d", "date" : "$reservationDate"}}
						 , "saleTotPrice" : { "$multiply" : ["$prodList.cnt", "$prodList.discountPrice"]}}}
					
		, { "$group" : { "_id" : ["$newReserveDate", "$prodName"]
		               , "reserveDate" : { "$first" : "$newReserveDate"}
					         , "pname" : { "$first" : "$prodName"}
					         , "fCnt" : { "$sum" : "$fCnt"}
					         , "fPrice" : { "$max" : "$salePrice"}
					         , "totPrice" : { "$sum" : "$saleTotPrice"}
					   }
		  }
		, { "$project" : { "_id" : 0, "reserveDate" : 1, "pname" : 1, "fCnt" : 1, "fPrice" : 1, "totPrice" : 1}}
		, { "$sort" : { "totPrice" : 1}}
	  ]
	  
	  MARIADB query
	  SELECT DATE_FORMAT(A.RESERVATION_DATE , '%Y-%m-%d') RESERVE_DATE
	       , A.PROD_NAME                                  P_NAME
	       , SUM(PROD_CNT)                                F_CNT
	       , MAX(DISCOUNT_PRICE)                          SALE_PRICE
	       , SUM(A.PROD_CNT * A.DISCOUNT_PRICE)           TOT_PRICE
	  FROM   ORDER_INFO A
	  WHERE  A.RESERVATION_DATE >= STR_TO_DATE('2024-06-03T05:19:51', '%Y-%m-%dT%H:%i:%S')
	  AND    A.RESERVATION_DATE <= STR_TO_DATE('2024-06-04T05:19:51', '%Y-%m-%dT%H:%i:%S')
	  GROUP  BY DATE_FORMAT(A.RESERVATION_DATE , '%Y-%m-%d') , A.PROD_NAME
	  ORDER BY TOT_PRICE ASC
	 * */
	public void querySampleFinal() {
		
		//match type= 조건 / key = 컬럼 / val = 값
		List<MongoProps>  matchParams = 
			mongoUtil.newMongoProp("gte").key("reservationDate").val(OffsetDateTime.now().minusDays(1))
		             .newInsAppend("lte").key("reservationDate").val(OffsetDateTime.now())
		             .getList();
		
		//project type= [표현여부 또는 계산할 예약어] Y:표현 N:표현안함 예약어:dateToString,multiply / key = 컬럼 / val = 값
		List<MongoProps> projectParams = 
			mongoUtil.newMongoProp("Y")          .key("prodName")      .val("$prodList.prodNm")
			         .newInsAppend("Y")           .key("fCnt")          .val("$prodList.cnt")
			         .newInsAppend("Y")           .key("salePrice")     .val("$prodList.discountPrice")
			         .newInsAppend("dateToString").key("newReserveDate").val("%Y-%m-%d,$reservationDate")
			         .newInsAppend("multiply")    .key("saleTotPrice")  .val("$prodList.cnt,$prodList.discountPrice")
			         .getList();
		
		//group type= id:그룹핑할 내역 또는 함수명 / key = 신규로 보여줄 컬럼 설정 / val = 기존 컬럼 지정
		List<MongoProps> groupParams = 
			mongoUtil.newMongoProp("id")   .key("")           .val("$newReserveDate,$prodName")
			         .newInsAppend("first") .key("reserveDate").val("$newReserveDate")
			         .newInsAppend("first") .key("pname")      .val("$prodName")
			         .newInsAppend("sum")   .key("fCnt")       .val("$fCnt")
			         .newInsAppend("max")   .key("fPrice")     .val("$salePrice")
			         .newInsAppend("sum")   .key("totPrice")   .val("$saleTotPrice")
			         .getList();
		
		List<MongoProps> projectParams2 = 
				mongoUtil.newMongoProp("N") .key("id")            
				         .newInsAppend("Y")  .key("reserveDate").val(1L)
				         .newInsAppend("Y")  .key("pname")      .val(1L)
				         .newInsAppend("Y")  .key("fCnt")       .val(1L)
				         .newInsAppend("Y")  .key("fPrice")     .val(1L)
				         .newInsAppend("Y")  .key("totPrice")   .val(1L)
				         .getList();
		
		//sort type= 사용안함 안넣어도됨 / key = 정렬컬럼 / val = asc or desc 또는 1 또는 -1 
		List<MongoProps> sortParams = 
				mongoUtil.newMongoProp("").key("totPrice").val("asc")        
				         .newInsAppend("").key("fPrice").val(-1)
				         .getList();
		
		
		AggregationBuilder newBuilder = new AggregationBuilder(mongoTemplate);
		List<Object> resList = 
		newBuilder.setMatch(matchParams)
		          .unwind("$prodList", true)
		          .setProject(projectParams)
		          .setGroup(groupParams)
		          .setProject(projectParams2)
		          .setSort(sortParams)
		          .aggregate("OrderInfo", Object.class);
		
		
		if(resList.size() < 100) {
			log.info("resList.size = {} / resList = {}",resList.size(), resList);	
		}else {
			log.info("resList.size = {} ",resList.size());
		}
		
		
		
		AggregationBuilder newBuilder2 = new AggregationBuilder(mongoTemplate);
		List<Object> resList2 = 
		newBuilder2.setMatch(mongoUtil.newMongoProp("gte").key("reservationDate").val(OffsetDateTime.now().minusDays(1))
	                                  .newInsAppend("lte").key("reservationDate").val(OffsetDateTime.now())
	                                  .getList()
				   )
		           .unwind("$prodList", true)
		           .setProject(mongoUtil.newMongoProp("Y")           .key("prodName")      .val("$prodList.prodNm")
					                    .newInsAppend("Y")           .key("fCnt")          .val("$prodList.cnt")
					                    .newInsAppend("Y")           .key("salePrice")     .val("$prodList.discountPrice")
					                    .newInsAppend("dateToString").key("newReserveDate").val("%Y-%m-%d,$reservationDate")
					                    .newInsAppend("multiply")    .key("saleTotPrice")  .val("$prodList.cnt,$prodList.discountPrice")
					                    .getList()
		           )
		           .setGroup(mongoUtil.newMongoProp("id")    .key("")           .val("$newReserveDate,$prodName")
					                  .newInsAppend("first") .key("reserveDate").val("$newReserveDate")
					                  .newInsAppend("first") .key("pname")      .val("$prodName")
					                  .newInsAppend("sum")   .key("fCnt")       .val("$fCnt")
					                  .newInsAppend("max")   .key("fPrice")     .val("$salePrice")
					                  .newInsAppend("sum")   .key("totPrice")   .val("$saleTotPrice")
					                  .getList()
				   )
		           .setProject(mongoUtil.newMongoProp("N")         .key("id")            
					                    .newInsAppend("Y")         .key("reserveDate").val(1L)
					                    .newInsAppend("Y")         .key("pname")      .val(1L)
					                    .newInsAppend("Y")         .key("fCnt")       .val(1L)
					                    .newInsAppend("Y")         .key("fPrice")     .val(1L)
					                    .newInsAppend("Y")         .key("totPrice")   .val(1L)
					                    .newInsAppend("subtract")  .key("minusTest")  .val("$fCnt,$fPrice")
					                    .getList()
				   )
		           .setSort(mongoUtil.newMongoProp("").key("totPrice").val("asc")        
					                 .newInsAppend("").key("fPrice")  .val(-1)
					                 .getList()
				   )
		           .aggregate("OrderInfo", Object.class);
		
		if(resList2.size() < 100) {
			log.info("resList2.size = {} / resList2 = {}",resList2.size(), resList2);	
		}else {
			log.info("resList2.size = {} ",resList2.size());
		}
		
	}
	
	
	
	
	
	
	
	
	
}
