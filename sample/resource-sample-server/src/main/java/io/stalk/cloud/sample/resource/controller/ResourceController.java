package io.stalk.cloud.sample.resource.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ResourceController {
	
	@RequestMapping("/ping")
	public String ping() {
		return "pong";
	}
	
}
