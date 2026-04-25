package com.antonstrokov.j_aide.core.service;

import dev.langchain4j.model.input.PromptTemplate;

public final class AiPromptTemplates {

	public static final PromptTemplate FAST_TEMPLATE = PromptTemplate.from(
			"Ты опытный {{language}}-разработчик.\n" +
					"Объясни код очень кратко на русском языке.\n\n" +
					"Верни ответ строго в JSON формате БЕЗ markdown и БЕЗ ```.\n" +
					"Только чистый JSON.\n\n" +
					"- Если вход содержит синтаксис языка программирования, inputType должен быть code\n" +
					"- Если вход является обычным текстом без программного синтаксиса, inputType должен быть plain_text\n\n" +
					"Формат:\n" +
					"{\n" +
					"  \"summary\": \"очень краткое объяснение\",\n" +
					"  \"details\": \"1-2 коротких предложения\",\n" +
					"  \"complexity\": \"easy/medium/hard\",\n" +
					"  \"suggestion\": \"короткая рекомендация\",\n" +
					"  \"bestPractice\": \"какая хорошая практика здесь уместна\",\n" +
					"  \"riskHint\": \"есть ли здесь риск или на что стоит обратить внимание\",\n" +
					"  \"confidence\": \"high/medium/low\",\n" +
					"  \"codeSmell\": \"есть ли здесь запах кода или краткая оценка качества\",\n" +
					"  \"inputType\": \"одно значение: code или plain_text\"\n" +
					"}\n\n" +
					"Не добавляй никаких пояснений.\n\n" +
					"Имя проекта: {{projectName}}\n" +
					"Имя модуля: {{moduleName}}\n" +
					"Имя файла: {{fileName}}\n" +
					"Диапазон строк: {{lineStart}}-{{lineEnd}}\n\n" +
					"Код:\n{{code}}"
	);

	public static final PromptTemplate SMART_TEMPLATE = PromptTemplate.from(
			"Ты опытный {{language}}-разработчик.\n" +
					"Объясни код на русском языке.\n\n" +
					"Верни ответ строго в JSON формате БЕЗ markdown и БЕЗ ```.\n" +
					"Только чистый JSON.\n\n" +
					"- Если вход содержит синтаксис языка программирования, inputType должен быть code\n" +
					"- Если вход является обычным текстом без программного синтаксиса, inputType должен быть " +
					"plain_text\n\n" +
					"Формат:\n" +
					"{\n" +
					"  \"summary\": \"краткое объяснение\",\n" +
					"  \"details\": \"подробное объяснение\",\n" +
					"  \"complexity\": \"easy/medium/hard\", \n" +
					"  \"suggestion\": \"что можно улучшить или на что обратить внимание\", \n" +
					"  \"bestPractice\": \"какая хорошая практика здесь уместна\", \n" +
					"  \"riskHint\": \"есть ли здесь риск или на что стоит обратить внимание\", \n" +
					"  \"confidence\": \"high/medium/low\", \n" +
					"  \"codeSmell\": \"есть ли здесь запах кода или краткая оценка качества\", \n" +
					"  \"inputType\": \"одно значение: code или plain_text\"\n" +
					"}\n\n" +
					"Не добавляй никаких пояснений.\n\n" +
					"Имя проекта: {{projectName}}\n" +
					"Имя модуля: {{moduleName}}\n" +
					"Имя файла: {{fileName}}\n\n" +
					"Диапазон строк: {{lineStart}}-{{lineEnd}}\n\n" +
					"Код:\n{{code}}"
	);

	public static final PromptTemplate DEEP_TEMPLATE = PromptTemplate.from(
			"Ты опытный {{language}}-разработчик.\n" +
					"Подробно объясни код на русском языке.\n" +
					"Объясняй только данный код.\n" +
					"Не добавляй лишние примеры.\n\n" +
					"Верни ответ строго в JSON формате БЕЗ markdown и БЕЗ ```.\n" +
					"Только чистый JSON.\n\n" +
					"ВАЖНО:\n" +
					"- Все значения в JSON должны быть строками\n" +
					"- Поле details должно быть строкой, а не объектом и не массивом\n" +
					"- Если вход содержит синтаксис языка программирования, inputType должен быть code\n" +
					"- Если вход является обычным текстом без программного синтаксиса, inputType должен быть " +
					"plain_text\n" +
					"- В details обязательно опиши: 1) что делает код, 2) ключевые элементы синтаксиса, 3) что здесь " +
					"отсутствует или упрощено, 4) где такой код может использоваться\n\n" +
					"Формат:\n" +
					"{\n" +
					"  \"summary\": \"краткий вывод\",\n" +
					"  \"details\": \"подробное объяснение в несколько предложений, можно с нумерацией внутри " +
					"строки\",\n" +
					"  \"complexity\": \"easy/medium/hard\", \n" +
					"  \"suggestion\": \"что можно улучшить или на что обратить внимание\", \n" +
					"  \"bestPractice\": \"какая хорошая практика здесь уместна\", \n" +
					"  \"riskHint\": \"есть ли здесь риск или на что стоит обратить внимание\", \n" +
					"  \"confidence\": \"high/medium/low\", \n" +
					"  \"codeSmell\": \"есть ли здесь запах кода или краткая оценка качества\", \n" +
					"  \"inputType\": \"одно значение: code или plain_text\"\n" +
					"}\n\n" +
					"Не добавляй никаких пояснений вне JSON.\n\n" +
					"Имя проекта: {{projectName}}\n" +
					"Имя модуля: {{moduleName}}\n" +
					"Имя файла: {{fileName}}\n" +
					"Диапазон строк: {{lineStart}}-{{lineEnd}}\n\n" +
					"Код:\n{{code}}"
	);

	private AiPromptTemplates() {


	}
}