package com.demo.mongo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.mongo.service.MongoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/mg")
@RequiredArgsConstructor
public class MongoController {

	private final MongoService mongoService;
	
	
	@PostMapping("/saveUser")
	public ResponseEntity<?> saveUser() {
		Object obj = mongoService.saveUser();
		return ResponseEntity.ok(obj);
	}
	@PostMapping("/saveUsers")
	public ResponseEntity<?> saveUsers() {
		Object obj = mongoService.saveUsers();
		return ResponseEntity.ok(obj);
	}
	@PostMapping("/saveRole")
	public ResponseEntity<?> saveRole() {
		Object obj = mongoService.saveRole();
		return ResponseEntity.ok(obj);
	}
	@PostMapping("/saveRoles")
	public ResponseEntity<?> saveRoles() {
		Object obj = mongoService.saveRoles();
		return ResponseEntity.ok(obj);
	}
	//user 수정
	@PutMapping("setUser")
	public ResponseEntity<?> setUser() {
		Object obj = mongoService.setUser();
		return ResponseEntity.ok(obj);
	}
	
	@GetMapping("/users")
	public ResponseEntity<?> getUsers() {
		Object obj = mongoService.getUsers();
		return ResponseEntity.ok(obj);
	}
	@GetMapping("/user/{userId}")
	public ResponseEntity<?> getUser() {
		Object obj = mongoService.getUser();
		return ResponseEntity.ok(obj);
	}
	
	
}
