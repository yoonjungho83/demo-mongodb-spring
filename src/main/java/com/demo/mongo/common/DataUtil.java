package com.demo.mongo.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.demo.mongo.support.mongoDB.DocumentBuilder;

@Component
public class DataUtil {

	
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
}
