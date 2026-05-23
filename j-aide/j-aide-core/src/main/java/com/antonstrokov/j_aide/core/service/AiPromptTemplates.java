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

	public static final PromptTemplate IMPROVE_TEMPLATE = PromptTemplate.from(
			"Ты опытный {{language}}-разработчик.\n" +
					"Улучши данный код на русском языке.\n" +
					"Главная цель: сделать код более читаемым, понятным и безопасным без изменения бизнес-поведения.\n" +
					"Если в коде есть плохие имена методов, переменных или параметров, предложи более понятные имена.\n" +
					"Если код написан в одну строку, отформатируй его.\n" +
					"Если сообщения или строки неинформативны, предложи более понятный вариант.\n" +
					"Не добавляй комментарии без необходимости.\n" +
					"Не меняй публичный API без явной причины, но если имя метода явно плохое, можно предложить улучшенное имя.\n\n" +
					"Верни ответ строго в JSON формате БЕЗ markdown и БЕЗ ```.\n" +
					"Только чистый JSON.\n\n" +
					"ВАЖНО:\n" +
					"- Поле improvedCode должно содержать только чистый исходный код без markdown.\n" +
					"- Никогда не оборачивай improvedCode в ``` или ```java.\n" +
					"- Не добавляй пояснения, комментарии к ответу или текстовые описания внутрь improvedCode.\n" +
					"- Поле changes должно быть массивом строк.\n" +
					"- Если ты изменил код, changes должен содержать хотя бы одно описание реального изменения.\n" +
					"- Описывай в changes только те изменения, которые действительно есть между исходным кодом и improvedCode.\n" +
					"- Не выдумывай изменения, которых нет в improvedCode.\n" +
					"- Если код уже нормальный и реальных улучшений нет, верни исходный код без изменений.\n" +
					"- Если реальных улучшений нет, changes должен быть [\"No meaningful changes were necessary\"].\n" +
					"- Не добавляй пояснения вне JSON.\n\n" +
					"Формат:\n" +
					"{\n" +
					"  \"summary\": \"краткое описание улучшения\",\n" +
					"  \"improvedCode\": \"улучшенный код\",\n" +
					"  \"changes\": [\"изменение 1\", \"изменение 2\"],\n" +
					"  \"riskHint\": \"возможный риск после изменения или 'нет явных рисков'\",\n" +
					"  \"confidence\": \"high/medium/low\"\n" +
					"}\n\n" +
					"Имя проекта: {{projectName}}\n" +
					"Имя модуля: {{moduleName}}\n" +
					"Имя файла: {{fileName}}\n" +
					"Диапазон строк: {{lineStart}}-{{lineEnd}}\n\n" +
					"Код:\n{{code}}"
	);

	public static final PromptTemplate ERROR_EXPLAIN_TEMPLATE = PromptTemplate.from(
			"Ты опытный {{language}}-разработчик и Java/Spring mentor.\n" +
					"Проанализируй runtime error, stack trace или лог ошибки на русском языке.\n" +
					"Главная цель: объяснить причину ошибки простыми словами и дать практические шаги исправления.\n\n" +
					"Верни ответ строго в JSON формате БЕЗ markdown и БЕЗ ```.\n" +
					"Только чистый JSON.\n\n" +
					"ВАЖНО:\n" +
					"- Поле summary должно кратко описывать, что произошло.\n" +
					"- Поле likelyCause должно объяснять наиболее вероятную корневую причину ошибки.\n" +
					"- Поле whereToLook должно подсказать, где искать проблему: класс, метод, конфиг, dependency, порт, БД, Docker, Spring bean и т.п.\n" +
					"- Поле suggestedFixes должно быть массивом конкретных шагов исправления.\n" +
					"- Поле riskHint должно предупредить о возможных рисках или побочных эффектах исправления.\n" +
					"- Поле confidence должно быть одним из значений: high, medium, low.\n" +
					"- Если информации недостаточно, честно укажи это в confidence и suggestedFixes.\n" +
					"- Сохраняй точные имена файлов, классов, методов, портов, dependency/artifact id, symbols и error codes из текста ошибки.\n" +
					"- Не заменяй имена файлов, классов, методов или dependencies выдуманными значениями.\n" +
					"- Если видишь вероятную опечатку в имени dependency, метода, класса, package или symbol, явно укажи её в likelyCause или suggestedFixes.\n" +
					"- В suggestedFixes давай команды под Windows/PowerShell, если ошибка похожа на локальную проблему с портом, процессом или Docker.\n" +
					"- В riskHint пиши только практичный риск, связанный с предлагаемым исправлением; не добавляй общий риск, если он не помогает.\n" +
					"- Не добавляй пояснения вне JSON.\n\n" +
					"Формат:\n" +
					"{\n" +
					"  \"summary\": \"краткое описание ошибки\",\n" +
					"  \"likelyCause\": \"наиболее вероятная причина\",\n" +
					"  \"whereToLook\": \"где искать проблему\",\n" +
					"  \"suggestedFixes\": [\"шаг 1\", \"шаг 2\"],\n" +
					"  \"riskHint\": \"на что обратить внимание\",\n" +
					"  \"confidence\": \"high/medium/low\"\n" +
					"}\n\n" +
					"Имя проекта: {{projectName}}\n" +
					"Имя модуля: {{moduleName}}\n" +
					"Имя файла: {{fileName}}\n" +
					"Диапазон строк: {{lineStart}}-{{lineEnd}}\n\n" +
					"Текст ошибки:\n{{errorText}}"
	);

	public static PromptTemplate resolveImproveTemplate() {
		return IMPROVE_TEMPLATE;
	}

	public static PromptTemplate resolveErrorExplainTemplate() {
		return ERROR_EXPLAIN_TEMPLATE;
	}

	public static PromptTemplate resolveTemplate(String effectiveMode) {
		switch (effectiveMode) {
			case "FAST":
				return FAST_TEMPLATE;
			case "DEEP":
				return DEEP_TEMPLATE;
			default:
				return SMART_TEMPLATE;
		}
	}
}