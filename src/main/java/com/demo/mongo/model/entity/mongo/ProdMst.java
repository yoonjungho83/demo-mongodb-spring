package com.demo.mongo.model.entity.mongo;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Document
public class ProdMst implements Persistable<String>{

	@Id
	private String id;
	private String  prodNm;
	private Integer orgPrice;
	private Integer salePrice;
	private Integer stockCnt;
	
	@CreatedDate
	private LocalDateTime createDate;
	@LastModifiedDate // date 자동입력됨
    private LocalDateTime updateDate;

	@Override
	public String getId() {
      return this.id;
	}
	
	@Override
	public boolean isNew() {
      // createDate가 null이면 새로운 객체로 판별
      return createDate == null;
	}
}
