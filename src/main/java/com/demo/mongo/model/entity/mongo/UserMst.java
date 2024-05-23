package com.demo.mongo.model.entity.mongo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.demo.mongo.annotation.CascadeSave;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
//@NoArgsConstructor
@Builder
@ToString
@Document(collection = "UserMst")
public class UserMst implements Persistable<String>{

	@Id
    private String  id;
	
	@Indexed//(unique = true)
	private String userId;
	private String userName;
	private String birth;
	private Integer age;
	private String phone;
	private String desc;
	
	
	/* @DBRef , @CascadeSave
	 *  custom casecade : userMst에 address 객체 입력시 objectId가 입력되며 해당 객체는 address 테이블에 저장됨.
	 * */
	@DBRef
    @Field("address")
    @CascadeSave 
    private Address address;
	
	
	private List<RoleDetail> roleList = new ArrayList<>();
	
	@CreatedDate // date 자동입렫됨
    private LocalDateTime createDate;
    @LastModifiedDate // date 자동입력됨
    private LocalDateTime updateDate;
    
    
    public UserMst() {
    	roleList = new ArrayList<>();
    }
	
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
