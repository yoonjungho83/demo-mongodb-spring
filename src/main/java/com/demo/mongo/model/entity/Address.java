package com.demo.mongo.model.entity;

import java.time.OffsetDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.Id;
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
@Document
public class Address implements Persistable<String>{

	@Id
	private String id;
	
	@Indexed
	private String zipcode;
	private String addr;
	private String addrDetail;
	private String desc;
	
	@CreatedDate
    private OffsetDateTime createDate;
    @LastModifiedDate
    private OffsetDateTime updateDate;
    
	
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