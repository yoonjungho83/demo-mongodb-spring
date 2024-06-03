package com.demo.mongo.model.entity.mongo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;


@Data
@AllArgsConstructor
//@NoArgsConstructor
@Builder
@ToString
@Document(collection = "OrderInfo")
public class OrderInfo {
	
	private String id;
	
	private String orderNum;
	
	private Integer totPrice;
	private String iscomplete;
	
	
	private String isReservation;//y 예약 , n 즉시
	
	@Indexed
	private LocalDateTime orderDate;
	@Indexed
	private LocalDateTime reservationDate;
	
	private List<SalesProd> prodList;
	@Indexed
	private UserMst orderUser;
	
	@CreatedDate
	private LocalDateTime createDate;

	
	
	
	public OrderInfo() {
		prodList = new ArrayList<>();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
