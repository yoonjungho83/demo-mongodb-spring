package com.demo.mongo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.mongo.service.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

	private final OrderService orderService;
	
	@GetMapping("/sample/createProd")
	public void createProdMst() {
		orderService.createProdMst();
	}
	@GetMapping("/sample/createReservation/{createCnt}")
	public String createReservation(@PathVariable("createCnt") int createCnt) {
		return orderService.createReservation(createCnt);
	}
	
	@GetMapping("/sample/setTotPrice")
	public String setTotPrice() {
		return orderService.setTotPrice();
	}
	
	@GetMapping("/sample/group")
	public String group() {
		return orderService.getGrouppingOrder();
	}
	
	@GetMapping("/querySampleFinal")
	public String querySampleFinal() {
		orderService.querySampleFinal();
		return "succ";
	}
	
	
	
	
}
