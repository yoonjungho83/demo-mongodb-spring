package com.demo.mongo.model.aggregation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {

	
	private String orderId;
	private String prodNm;
	private String orderDate;
	private String isComplete;
}
