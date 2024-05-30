package com.demo.mongo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.mongo.model.entity.mongo.Address;
import com.demo.mongo.model.entity.mongo.UserMst;
import com.demo.mongo.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/mg")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;
	
	
	@GetMapping("/users")
	public ResponseEntity<?> getUsers() {
		Object obj = userService.getUsers();
		return ResponseEntity.ok(obj);
	}
	@GetMapping("/user/{userId}")
	public ResponseEntity<?> getUser(@PathVariable("userId") String userId) {
		Object obj = userService.getUser(userId);
		return ResponseEntity.ok(obj);
	}
	
	@GetMapping("/getUserPaging/{startP}/{dataCnt}/{minusDate}")
	public ResponseEntity<?> getUserPaging(@PathVariable("startP") Integer startP 
										 , @PathVariable("dataCnt") Integer dataCnt 
										 , @PathVariable("minusDate") Integer minusDate) 
	{
		Object obj = userService.getUserPaging(startP,dataCnt,minusDate);
		return ResponseEntity.ok(obj);
	}
	
	@PostMapping("/saveUser")
	public ResponseEntity<?> saveUser(@RequestBody UserMst user) {
		Object obj = userService.saveUser(user);
		return ResponseEntity.ok(obj);
	}
	
	@PostMapping("/saveUsers")
	public ResponseEntity<?> saveUsers() {
		Object obj = userService.saveUsers();
		return ResponseEntity.ok(obj);
	}
	
	//user 수정
	@PutMapping("/setUser")
	public ResponseEntity<?> setUser() {
		Object obj = userService.setUser();
		return ResponseEntity.ok(obj);
	}
	
	@GetMapping("/allDeleteUser")
	public String allDeleteUser() {
		System.out.println("allDeleteUser");
		
		
		userService.allDeleteUser();
		
		return "succ";
	}
	@GetMapping("/deleteUser/{userId}")
	public String deleteUser(@PathVariable("userId") String userId) {
		System.out.println("deleteUser");
		
		
		userService.deleteUser(userId);
		
		return "succ";
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
