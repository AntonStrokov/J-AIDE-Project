package com.antonstrokov.j_aide.core.dto.improve;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StructuredImproveChangeFact {

	private String operation;
	private String symbolKind;
	private String before;
	private String after;
}
