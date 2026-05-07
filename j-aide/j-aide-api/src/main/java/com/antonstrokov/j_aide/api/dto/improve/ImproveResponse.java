package com.antonstrokov.j_aide.api.dto.improve;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ImproveResponse {

	private ImproveResult improvement;
	private String rawJson;
	private boolean success;

	private ImproveMetadata metadata;
}