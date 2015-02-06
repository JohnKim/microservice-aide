package io.stalk.cloud.auth.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestAPIController {

	@RequestMapping("/ping")
	public String ping() {
		return "pong";
	}
}
