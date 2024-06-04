package com.demo.mongo.model.aggregation;

import lombok.Data;

@Data
public class MongoProps {
	private String type;
	private String key;
	private Object value;
	public MongoProps(String type) {this.type=type;}
}
