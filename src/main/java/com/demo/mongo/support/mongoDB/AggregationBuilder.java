package com.demo.mongo.support.mongoDB;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators;
import org.springframework.data.mongodb.core.aggregation.DateOperators;
import org.springframework.data.mongodb.core.aggregation.DateOperators.Timezone;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.data.mongodb.core.query.Criteria;

import com.demo.mongo.model.aggregation.MongoProps;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AggregationBuilder {

	private MongoTemplate mongoTemplate;
	private List<AggregationOperation> operationList;

	public AggregationBuilder(MongoTemplate mongoTemplate ) {
		this.mongoTemplate = mongoTemplate;
		operationList = new ArrayList<>();
	}
	
	public AggregationBuilder join(boolean unwind , String from, DocumentBuilder letBuilder, DocumentBuilder onBuilder) {
		Document let     = letBuilder.getDocument();
		Document letByOn = onBuilder.getDocument();
		letByOn.forEach(let::append);
		List<Document> match = onBuilder.getDocumentList();

		lookup(from, let, match);
		unwind(from, unwind);//unwind :true => left Join  / false => inner Join
		
		return this;
	}
	
//	public AggregationBuilder addField() {
//		
////		Aggregation.addFields().addField("time_dist")
//		AddFieldsOperation af = 
//			Aggregation.addFields()
//			           .addField("")
//			           .withValue(ArithmeticOperators.Abs.absoluteValueOf(Subtract.valueOf("timeStampTemp").subtract("a.date"))).build();
//		operationList.add(af);
//		return this;
//	}
	

	

	public void lookup(String from, Document let, List<Document> match) {
		AggregationOperation lookupOperation = makeLookupOperation(from, let, match);
		this.operationList.add(lookupOperation);
	}

	
	//true : left join  / false : inner join
	public AggregationBuilder unwind(String field, boolean preserveNullAndEmptyArrays) {
		UnwindOperation unwindOperation = Aggregation.unwind(field, preserveNullAndEmptyArrays);
		this.operationList.add(unwindOperation);
		return this;
	}

	public AggregationOperation makeLookupOperation(String from, Document let, List<Document> match) {
		return context -> 
			new Document("$lookup",new Document("from"    , from)
									    .append("let"     , let)
									    .append("pipeline", Arrays.<Object>asList(new Document("$match", new Document("$expr", new Document("$and", match)) ))  )
									    .append("as"      , from));
	}

	public AggregationBuilder match(List<Criteria> criteriaList) {
		MatchOperation matchOperation = Aggregation
				.match(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
		operationList.add(matchOperation);
		return this;
	}
	
	public AggregationBuilder setMatch(MongoPropsBuilder props) {
		
		return setMatch(props.getList());
	}
	
	//expr : $and 만 지원
	public AggregationBuilder setMatch(List<MongoProps> conList) {
//		final List<AggregateParam>  conList1 = 
//				mongoUtil.newParam("match").col("").val("")
//			          .newInstance("match").col("").val("")
//			          .getList();
				
		List<Criteria> cList = new ArrayList<>();
		for(MongoProps mp : conList) {
			
			if(mp.getType().equals("")) continue;
			
			String key  = mp.getKey();
			String expr = mp.getType();
			Object obj  = mp.getValue();
			Criteria c  = new Criteria(key);
			
			if(obj instanceof OffsetDateTime) {
				obj = offsetDateTimeToDate((OffsetDateTime)obj) ;
			}
			else if(obj instanceof LocalDateTime) {
				obj = localDateTimeToDate((LocalDateTime)obj) ;
			}
			
			if(expr.equals("eq"    ))      {c.is(obj);              }              
			else if(expr.equals("ne"    )) {c.ne(obj);              }              
			else if(expr.equals("exists")) {c.exists((boolean)obj); } 
			else if(expr.equals("gt"    )) {c.gt(obj);              }              
			else if(expr.equals("gte"   )) {c.gte(obj);             }             
			else if(expr.equals("lt"    )) {c.lt(obj);              }              
			else if(expr.equals("lte"   )) {c.lte(obj);             }             
			else if(expr.equals("like"  )) {c.regex((String)obj);  }
			else if(expr.equals("in"    )) {
				String [] inArr = ((String)obj).split(",");
				c.in(Arrays.asList(inArr)); 
			}
		     cList.add(c);
		}
		
		MatchOperation m = Aggregation.match( new Criteria().andOperator(cList) );
		operationList.add(m);
		
		//Deprecated		
//		List<Document> list  = new ArrayList<>();
//		for(MongoProps mp :conList) {
//			String type = "$"+mp.getType().trim();
//			
//			Object obj = null;
//			if(mp.getValue() instanceof String) {
//				obj = mp.getValue();
//			}
//			else if(mp.getValue() instanceof OffsetDateTime) {
//				obj = offsetDateTimeToDate((OffsetDateTime)mp.getValue()) ;
//			}
//			else if(mp.getValue() instanceof LocalDateTime) {
//				obj = localDateTimeToDate((LocalDateTime)mp.getValue()) ;
//			}
//			else {
//				obj = mp.getValue();
//			}
//			
//			list.add( new Document(mp.getKey(), new Document(type, obj))  );
//		}
//		;
//		
//		AggregationOperation matchOperation = 
//				context -> {
//					return new Document("$match", new Document("$and", list));
//				};
//		operationList.add(matchOperation);
		return this;
	}

	public AggregationBuilder project(ProjectionOperation projectionOperation) {
		this.operationList.add(projectionOperation);
		return this;
	}

	/*
	 * //ex params
		final List<AggregateParam> tempList = 
		mongoUtil.newParam("project")     .key("prodName")      .val("$prodList.prodNm")
		      .newInstance("project")     .key("fCnt")          .val("$prodList.cnt")
		      .newInstance("project")     .key("salePrice")     .val("$prodList.salePrice")
		      .newInstance("dateToString").key("newReserveDate").val("%Y-%m-%d,$reservationDate")
		      .newInstance("multiply")    .key("saleTotPrice")  .val("$prodList.cnt,$prodList.discountPrice")
		      .getList();
		      
        new Document
        ("$project", new Document("newReserveDate", new Document("$dateToString", new Document("format", "%Y-%m-%d").append("date", "$reservationDate")))
        	          .append("prodName"    , "$prodList.prodNm")
        	          .append("fCnt"        , "$prodList.cnt")
        	          .append("salePrice"   , "$prodList.salePrice")
        	          .append("saleTotPrice", new Document("$multiply", Arrays.asList("$prodList.cnt", "$prodList.discountPrice")))
        );
	 * */
	public AggregationBuilder setProject(MongoPropsBuilder props) {
		
		return setProject(props.getList());
	}
	
	public AggregationBuilder setProject(final List<MongoProps> conList) {
		
		ProjectionOperation p = Aggregation.project();
		
		for(MongoProps mp : conList) {
			String val = mp.getValue() == null ? "" : (String)mp.getValue();
			String key = mp.getKey();
			if(mp.getType().equals("Y") || mp.getType().equals("")) 
			{
				String temp = !val.equals("")? val : key;
				p = p.andExpression(temp).as(key.replace("$", ""));
			}
			else if(mp.getType().equals("N") ) //컬럼 안보이게
			{
				key = key.equals("id") ? "_id" : key;
				p = p.andExclude(key);
			}
			else if(mp.getType().equals("dateToString")) 
			{
				String [] str = val.split(",");
				if(str == null || str.length != 2) continue;
				
				//0번째 : format  / 1번째: DB 컬럼 객체 
				//key : 신규 컬럼명
//				System.out.println("ZoneId.systemDefault() ================================ "+ZoneId.systemDefault().getId());
				p = p.and(DateOperators.zonedDateOf(str[1].trim(), Timezone.valueOf(ZoneId.systemDefault().getId())).toString(str[0].trim())).as(mp.getKey());
			}
			else if(mp.getType().equals("multiply")) 
			{//인수들의 곱
				String [] str = val.split(","); 
				if(str == null || str.length != 2) continue;
				p = p.and(ArithmeticOperators.Multiply.valueOf(str[0].trim()).multiplyBy(str[1].trim())).as(mp.getKey().trim());
			}
			else if(mp.getType().equals("subtract")) 
			{//첫번째 인수에서 2번째 인수를 뺌
				
				String [] str = val.split(","); 
				if(str == null || str.length != 2) continue;
				p = p.and(ArithmeticOperators.Subtract.valueOf(str[0].trim()).subtract(str[1].trim())).as(mp.getKey().trim());				
//				List<String> llist = Arrays.asList(((String)mp.getValue()).split(",")).stream().map(x->x.trim()).collect(Collectors.toList());
//				if(llist == null || llist.size() == 0) continue;
//				d = d == null? new Document(mp.getKey() , new Document("$subtract", llist)  )
//						     :     d.append(mp.getKey() , new Document("$subtract", llist)  );
			}
			
		}
		operationList.add(p);
		
//Deprecated
//		AggregationOperation projectOperation = 
//		context ->{
//			
//			Document d = null;
//			for(MongoProps mp : conList) {
//				if(mp.getType().equals("Y") || mp.getType().equals("")) 
//				{
//					d = d == null? new Document(mp.getKey(),mp.getValue())
//							         : d.append(mp.getKey(),mp.getValue());
//				}
//				else if(mp.getType().equals("N") ) //컬럼 안보이게
//				{
//					String key = mp.getKey().equals("id") ? "_id" : mp.getKey();
//					d = d == null? new Document(key , 0L)
//							         : d.append(key , 0L);
//				}
//				else if(mp.getType().equals("dateToString")) 
//				{
//					String [] str = ((String)mp.getValue()).split(",");
//					if(str == null || str.length != 2) continue;
//					d = d == null? new Document(mp.getKey() , new Document("$dateToString", new Document("format", str[0].trim()).append("date", str[1].trim())) )
//							     :     d.append(mp.getKey() , new Document("$dateToString", new Document("format", str[0].trim()).append("date", str[1].trim())) );
//				}
//				else if(mp.getType().equals("multiply")) 
//				{//인수들의 곱
//					List<String> llist = Arrays.asList(((String)mp.getValue()).split(",")).stream().map(x->x.trim()).collect(Collectors.toList());
//					if(llist == null || llist.size() == 0) continue;
//					d = d == null? new Document(mp.getKey() , new Document("$multiply", llist)  )
//							     :     d.append(mp.getKey() , new Document("$multiply", llist)  );
//				}
//				else if(mp.getType().equals("subtract")) 
//				{//첫번째 인수에서 2번째 인수를 뺌
//					List<String> llist = Arrays.asList(((String)mp.getValue()).split(",")).stream().map(x->x.trim()).collect(Collectors.toList());
//					if(llist == null || llist.size() == 0) continue;
//					d = d == null? new Document(mp.getKey() , new Document("$subtract", llist)  )
//							     :     d.append(mp.getKey() , new Document("$subtract", llist)  );
//				}
//			}
//			if(d == null) return null;
//			
//			return new Document("$project" , d);
//			
//		} ;
//		operationList.add(projectOperation);
		
		return this;
	}
	
	
	public AggregationBuilder group(GroupOperation groupOperation) {
		
		this.operationList.add(groupOperation);
		return this;
	}


	/**
	 * conList sample
	 * mongoUtil.newParam("id")    .col("")           .val("$newReserveDate,$prodName")
			      .newInstance("first") .col("reserveDate").val("$newReserveDate")
			      .newInstance("first") .col("pname")      .val("$prodName")
			      .newInstance("sum")   .col("fCnt")       .val("$fCnt")
			      .newInstance("max")   .col("fPrice")     .val("$salePrice")
			      .newInstance("sum")   .col("totPrice")   .val("$saleTotPrice")
			      .getList();
		
	   생성
	   new Document
	   ("$group", new Document("_id", Arrays.asList("$newReserveDate", "$prodName"))
	              .append("reserveDate", new Document("$first", "$newReserveDate"))
	              .append("pname", new Document("$first", "$prodName"))
	              .append("fCnt", new Document("$sum", "$fCnt"))
	              .append("fPrice", new Document("$max", "$salePrice"))
	              .append("totPrice", new Document("$sum", "$saleTotPrice"))
	   );
	 * 
	 */
	public AggregationBuilder setGroup(MongoPropsBuilder props) {
		
		return setGroup(props.getList());
	}
	public AggregationBuilder setGroup(final List<MongoProps> conList) {
		
		GroupOperation g = null;
		for(MongoProps mp :conList) 
		{
			String val = (String)mp.getValue();
			val = val.replace("$", "");
			String key = mp.getKey();
			
			if(mp.getType().equals("id")) {
				g = Aggregation.group( val.split(",") );// "$newReserveDate",
			}
			else if(mp.getType().equals("sum")) {
				g = g.sum(val).as(key);
			}
			else if(mp.getType().equals("max")) {
				g = g.max(val).as(key);
			}
			else if(mp.getType().equals("last")) {
				g = g.last(val).as(key);
			}
			else if(mp.getType().equals("first")) {
				g = g.first(val).as(key);
			}
			else if(mp.getType().equals("count")) {
				g = g.count().as(key);
			}
		}
		
		if(g != null) {
			operationList.add(g);
		}else {
			log.error("setGroup GroupOperation null error");
		}
		
		
		
		//deprecated
//		AggregationOperation groupOperation =
//		context -> {
//
//			Document d = null;
//			for(MongoProps mp : conList) 
//			{
//				if(mp.getType().equals("id")) 
//				{
//					List<String> idList = Arrays.asList(((String)mp.getValue()).split(",")).stream().map(x->x.trim()).collect(Collectors.toList());
//					if(idList == null || idList.size() == 0) continue;
//					d = d == null? new Document("_id" , idList)
//							         : d.append("_id" , idList);
//				}
//				else if(mp.getType().equals("sum")) 
//				{
//					d = d == null? new Document(mp.getKey() , new Document("$sum", mp.getValue()) )
//							     :     d.append(mp.getKey() , new Document("$sum", mp.getValue()) );
//				}
//				else 
//				{
//					d = d == null? new Document(mp.getKey() , new Document("$"+mp.getType() , mp.getValue()) )
//						         :     d.append(mp.getKey() , new Document("$"+mp.getType() , mp.getValue()) );
//				}
//			}
//			if(d == null) return null;
//			
//			return new Document("$group" , d);
//		};
//		
//		operationList.add(groupOperation);
			
		return this;

	}

	public AggregationBuilder sort(SortOperation sortOperation) {
		operationList.add(sortOperation);
		return this;
	}
	
	public AggregationBuilder setSort(MongoPropsBuilder props) {
		
		return setSort(props.getList());
	}
	public AggregationBuilder setSort(final List<MongoProps> conList) {
		SortOperation s = null;
		
		for(MongoProps mp: conList) {
			String key = mp.getKey();
			String val = (String)mp.getValue();
			//default DESC
			Direction direction = val.equals("asc") || val.equals("1")
					              ? Sort.Direction.ASC 
					              : Sort.Direction.DESC;
			if(s == null) {
				s = Aggregation.sort(direction, key);
			}else {
				s = s.and(direction, key);
			}
		}
		operationList.add(s);
		
		//deprecated
//		AggregationOperation sortOperation = 
//		context -> {
//			Document d = null;
//			for(MongoProps mp: conList) {
//				Long val = 1L;
//				if(mp.getValue() instanceof String) {
//					val = ((String)mp.getValue()).trim().equals("asc") ?  1L
//						 :((String)mp.getValue()).trim().equals("desc")? -1L
//						 :1L;//default asc
//				}
//				else if(mp.getValue() instanceof Integer) {
//					val =Long.valueOf((int)mp.getValue());
//				}
//				else if(mp.getValue() instanceof Long) {
//					val = ((Long)mp.getValue());
//				}
//				
//				
//				if(d == null) {
//					d = new Document(mp.getKey(), val); 
//				}else {
//					d.append(mp.getKey(), val);
//				}
//			}
//			
//			return new Document("$sort", d);
//		};
//		operationList.add(sortOperation);
		
		return this;
	}

	
	/** mongodb aggregation 실행 
	 * @param entityClass : 리턴받을 dto
	 * @param colectionName : 기준 table명
	 * @return 조인 및 집계 결과 리스트.
	 */
	public <T> List<T> aggregate(String collectionName, Class<T> entityClass) {

		Aggregation aggregation = Aggregation.newAggregation(operationList.toArray(new AggregationOperation[0]));

		log.info("AggregationBuilder > aggregate = {}", aggregation);
		return mongoTemplate.aggregate(aggregation, collectionName, entityClass).getMappedResults();
	}

	public <T> PageImpl<T> aggregate(Pageable pageable, String collectionName, Class<T> entityClass) {

		List<T>  result = aggregate(collectionName, entityClass);
		final int start = (int) pageable.getOffset();
		final int   end = Math.min((start + pageable.getPageSize()), result.size());
		
		return new PageImpl<>(result.subList(start, end), pageable, result.size());
	}
	
	
	/* mongoDB data 조회시 해당 함수로 변환하여 조회
	 * 아래는 변환된 예
	 * { "$match" : { "$and" : [{ "reservationDate" : { "$gte" : { "$date" : "2024-06-05T01:21:49.066Z"}}}]}}
	 *  */
	public Date localDateTimeToDate(LocalDateTime localdDateTime) {
		
		Instant instant = localdDateTime.atZone(ZoneId.systemDefault()).toInstant();    
		Date date = Date.from(instant);
		return date;
	}
	
	/* mongoDB data 조회시 해당 함수로 변환하여 조회 */
	public Date offsetDateTimeToDate(OffsetDateTime offsetDateTime) {
		
		return Date.from(offsetDateTime.toInstant());
	}
}
