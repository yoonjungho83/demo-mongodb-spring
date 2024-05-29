package com.demo.mongo.model.aggregation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAndRoleDto {

	private String userId;
	private String roleId;
	private String roleNm;
	
}
