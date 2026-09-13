package com.antonstrokov.j_aide.api.dto.improve;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ImproveChangeFact {

	private String operation;
	private String symbolKind;
	private String before;
	private String after;
}
