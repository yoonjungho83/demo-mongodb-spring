package com.demo.mongo.support.mongoDB;

import java.util.ArrayList;
import java.util.List;

import com.demo.mongo.model.aggregation.MongoProps;

import lombok.Data;

@Data
public class MongoPropsBuilder {

	private List<MongoProps> list;
	int curIdx ;
	
	public MongoPropsBuilder(String type) {
		list = new ArrayList<>();
		MongoProps s = new MongoProps(type);
		curIdx = 0;
		list.add(s);
	}
	
	public MongoPropsBuilder type(String type) {
		list.get(curIdx).setType(type);
		return this;
	}
	public MongoPropsBuilder key(String key) {
		list.get(curIdx).setKey(key);
		return this;
	}
	public MongoPropsBuilder val(Object value) {
		list.get(curIdx).setValue(value);
		return this;
	}
	
	
	public MongoPropsBuilder newInstance(String type) {
		MongoProps s = new MongoProps(type);
		list.add(s );
		curIdx = list.size()-1;
		return this ;
	}
	
	
		
}
