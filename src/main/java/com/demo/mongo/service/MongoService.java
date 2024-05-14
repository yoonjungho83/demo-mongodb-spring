package com.demo.mongo.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.demo.mongo.model.entity.UserMst;
import com.demo.mongo.repository.RoleMstRepository;
import com.demo.mongo.repository.UserMstRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MongoService {

	private final UserMstRepository userMstRepository;
	private final RoleMstRepository roleMstRepository;
	
	
	public Object saveUser() {
		UserMst u = UserMst.builder().build();
		UserMst r = userMstRepository.save(u);
		return r;
	}
	public Object saveUsers() {
		return ResponseEntity.ok("");
	}
	public Object saveRole() {
		return ResponseEntity.ok("");
	}
	public Object saveRoles() {
		return ResponseEntity.ok("");
	}
	public Object setUser() {
		return ResponseEntity.ok("");
	}
	
	public Object getUsers() {
		return ResponseEntity.ok("");
	}
	public Object getUser() {
		return ResponseEntity.ok("");
	}
	
}
