package com.antonstrokov.j_aide.core.service;

import org.springframework.stereotype.Service;

@Service
public class AiService {

	public String explain(String code) {
		return "AI will explain this code: " + code;
	}
}