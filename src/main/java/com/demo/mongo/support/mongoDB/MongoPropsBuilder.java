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
	
	//props 강제 추가
	public MongoPropsBuilder setMongoProps(MongoProps mp) {
		try {
			list.add(mp);
			curIdx = list.size()-1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this;
	}
	//props 강제 삭제
	public MongoPropsBuilder delMongoProps(MongoProps mp) {
		try {
			if(list.contains(mp)) {
				int idx = list.indexOf(mp);
				list.remove(idx);
				curIdx = list.size()-1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this;
	}
	public MongoPropsBuilder delMongoProps(int idx) {
		try {
			if(idx < list.size() && idx >= 0) {
				list.remove(idx);
				curIdx = list.size()-1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this;
	}
	
	
	public MongoPropsBuilder newInsAppend(String type) {
		MongoProps s = new MongoProps(type);
		list.add(s );
		curIdx = list.size()-1;
		return this ;
	}
	
	
		
}
