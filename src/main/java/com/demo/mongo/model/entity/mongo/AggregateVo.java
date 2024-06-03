package com.demo.mongo.model.entity.mongo;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
//@NoArgsConstructor
@Builder
@ToString
public class AggregateVo {

	private String prodNm;
	private String orderNum;
	
	private Integer totPrice;
	private String iscomplete;
	
	
	private LocalDateTime reservationDate;//y 예약 , n 즉시
	
}
