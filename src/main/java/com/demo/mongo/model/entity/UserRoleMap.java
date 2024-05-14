package com.demo.mongo.model.entity;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
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
@Document(collection = "UserRoleMap")
public class UserRoleMap implements Persistable<String>{

	
	@Id
	private String id;
	
	private String userId;
	private List<String> roleId;
	
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
