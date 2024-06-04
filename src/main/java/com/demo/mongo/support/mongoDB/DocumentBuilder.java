package com.demo.mongo.support.mongoDB;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;

import lombok.Data;

@Data
public class DocumentBuilder {

	private Document document;
	private List<Document> documentList;
	
	public DocumentBuilder() {
		document = new Document();
		documentList = new ArrayList<>();
	}
	
	//mongoDB let 변수정의
	public DocumentBuilder andLet(String variable , String value) {
		
		this.document.append(variable, value);
		return this;
	}
	public DocumentBuilder andOn(String expr ,String val1 , String val2) {
		if(expr.equals("$eq")) {
			documentList.add(new Document(expr ,Arrays.asList(val1,val2)));
		}
		else if(expr.equals("$ne")) {
			documentList.add(new Document(expr ,Arrays.asList(val1,val2)));
		}
		return this;
	}
	
	
	public void test() {
		TypedAggregation<Object> aggregation = 
		Aggregation.newAggregation
		(Object.class
        ,Aggregation.group("state", "city").sum("population").as("pop")
		,Aggregation.sort(Sort.Direction.ASC, "pop", "state", "city")
		,Aggregation.group("state").last("city") .as("biggestCity")
			                       .last("pop")  .as("biggestPop")
			                       .first("city").as("smallestCity")
			                       .first("pop") .as("smallestPop")
	    ,Aggregation.project().and("state").previousOperation()
			                  .and("biggestCity").nested(Aggregation.bind("name", "biggestCity")
			                                                        .and("population", "biggestPop")
			                                            )
			                  .and("smallestCity").nested(Aggregation.bind("name", "smallestCity")
			        		                                         .and("population", "smallestPop")
			        		                             )
	    ,Aggregation.sort(Sort.Direction.ASC, "state")
		);
	}
}
