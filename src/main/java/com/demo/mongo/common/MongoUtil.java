package com.demo.mongo.common;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.demo.mongo.support.mongoDB.DocumentBuilder;
import com.demo.mongo.support.mongoDB.MongoPropsBuilder;

@Component
public class MongoUtil {

	
	public List<Object> iteratorToList(Iterable t ) {
		List<Object> list = new ArrayList<>();
		
		Iterator it = t.iterator();
		while (it.hasNext()) {
			list.add(it.next());
		}
		return list;
	}
	
	public Criteria getCriteria(String expr , String key , Object val ) {
		Criteria c = new Criteria(key);
		     if(expr.equals("eq"    )) {c.is(val);              }              
		else if(expr.equals("ne"    )) {c.ne(val);              }              
		else if(expr.equals("exists")) {c.exists((boolean)val); } 
		else if(expr.equals("gt"    )) {c.gt(val);              }              
		else if(expr.equals("gte"   )) {c.gte(val);             }             
		else if(expr.equals("lt"    )) {c.lt(val);              }              
		else if(expr.equals("lte"   )) {c.lte(val);             }             
		else if(expr.equals("in"    )) {
//			ArrayList<String> list = new ArrayList<>(Stream.of(((String)val).split(",")).collect(Collectors.toList()));
//			c.in(list); 
		}
		
		return c;
	}
	
	public Criteria getCriteria(String key , Object val ) {
		Criteria c = new Criteria(key);
		if(val instanceof Boolean) {
			c.exists((boolean)val);
		}else {
			c.is(val);
		}
		
		return c;
	}
	
	//특정 조건 query 생성
	public Query getQuery(String key , Object val) {
		Query query = new Query(getCriteria(key,val));
		return query;
	}
	
	public Query getQuery(Object c) {
		Query query = new Query();
		query.addCriteria((CriteriaDefinition)c);
		return query;
	}
	
	
	public DocumentBuilder let(){
		return new DocumentBuilder();
	}
	public DocumentBuilder on(){
		return new DocumentBuilder();
	}
	
	public MongoPropsBuilder newMongoProp(String type){
		return new MongoPropsBuilder(type);
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
