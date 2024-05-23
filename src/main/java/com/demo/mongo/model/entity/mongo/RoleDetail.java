package com.demo.mongo.model.entity.mongo;

import java.time.LocalDateTime;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@Builder
@ToString
public class RoleDetail {

	@Id
	private ObjectId roleId;
	@Indexed
	private String roleName;
	private String useYn;
	private String desc;
	private LocalDateTime useSDate;
	private LocalDateTime useEDate;
	
	private LocalDateTime createDate;
	private LocalDateTime updateDate;
	
	public RoleDetail() {
		this.roleId = ObjectId.get();
	}
	
}
