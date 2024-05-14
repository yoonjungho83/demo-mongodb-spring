package com.demo.mongo.model.entity;

import java.time.OffsetDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.demo.mongo.annotation.CascadeSave;

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
public class UserMst implements Persistable<String>{

	@Id
    private String  id;
	
	@Indexed
	private String userId;
	private String userName;
	private String birth;
	private String age;
	private String phone;
	
	@DBRef
    @Field("address")
    @CascadeSave
    private Address address;
	
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
