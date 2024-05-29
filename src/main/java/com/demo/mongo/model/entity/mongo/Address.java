package com.demo.mongo.model.entity.mongo;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
//@Document(collection = "Address")
public class Address {//implements Persistable<String>

	@Id
	private String id;
	
	private String gubun;//집 / 직장 등
	
	@Indexed
	private String zipcode;
	private String city;
	private String addr;
	private String addrDetail;
	private String tel1;
	private String tel2;
	private String fax;
	
	private String desc;
	
	@CreatedDate
    private LocalDateTime createDate;
    @LastModifiedDate
    private LocalDateTime updateDate;
    
	
//	@Override
//    public String getId() {
//        return this.id;
//    }
//
//    @Override
//    public boolean isNew() {
//        // createDate가 null이면 새로운 객체로 판별
//        return createDate == null;
//    }
}
