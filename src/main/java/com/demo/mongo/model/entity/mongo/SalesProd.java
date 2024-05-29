package com.demo.mongo.model.entity.mongo;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@Builder
@ToString
public class SalesProd {

	private ObjectId id;
	
	@Indexed
	private String prodNm;
	private String prodObjId;
	private Integer orgPrice;
	private Integer salePrice;
	private Integer discountPrice;
	private Integer saleTotPrice;//
	
	private Double discountRate;
	private Integer cnt;
	
	
	public SalesProd() {
		this.id = ObjectId.get();
	}
	
}
