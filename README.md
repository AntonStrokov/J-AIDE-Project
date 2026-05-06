# J-Aide

J-Aide is a backend service for an AI-powered programming assistant.

The project provides REST API endpoints that can explain source code, return structured AI responses, and expose backend
capabilities for future IDE plugin integration.

## Tech Stack

- Java 21
- Spring Boot
- Spring Web
- Maven
- LangChain4j
- Ollama
- Qwen2.5-Coder 7B

## Running the Application

Make sure Ollama is running locally and the required model is available.

Check available Ollama models:

```bash
ollama list
```

Current model:

```text
qwen2.5-coder:7b
```

Run the backend:

```bash
mvn spring-boot:run
```

By default, the application starts on:

```text
http://localhost:8080
```

## API Endpoints

### GET /backend-info

Returns backend metadata, default settings, available endpoints, and supported capabilities.
Example curl request:

```bash
curl http://localhost:8080/backend-info
```

Example response:

```json
{
  "metadata": {
    "backendName": "J-Aide",
    "backendVersion": "0.1.0",
    "status": "UP"
  }
}
```

### POST /ai/explain

Explains a source code snippet and returns a structured AI response with metadata and context.
Example curl request:

```bash
curl -X POST http://localhost:8080/ai/explain \
  -H "Content-Type: application/json" \
  -d "{\"code\":\"public class Test {}\",\"mode\":\"SMART\",\"language\":\"java\"}"
```

Example request:

```json
{
  "code": "public class Test {}",
  "mode": "SMART",
  "language": "java",
  "fileName": "Test.java",
  "lineStart": 1,
  "lineEnd": 1,
  "projectName": "TESTING",
  "moduleName": "app",
  "pluginVersion": "0.1.0",
  "ideVersion": "26.2"
}
```

Example response:

```json
{
  "success": true,
  "explanation": {
    "summary": "Short explanation",
    "details": "Detailed explanation",
    "complexity": "easy",
    "suggestion": "Improvement suggestion",
    "bestPractice": "Relevant best practice",
    "riskHint": "Possible risk",
    "confidence": "high",
    "codeSmell": "Code quality note",
    "inputType": "code"
  },
  "metadata": {
    "traceId": "generated-trace-id",
    "backendVersion": "0.1.0",
    "responseTimeMs": 1234,
    "retried": false
  }
}
```

## Explain Modes

J-Aide supports three explanation modes:

- `FAST` — short and quick explanation.
- `SMART` — balanced explanation, used by default.
- `DEEP` — more detailed explanation.

## Supported Languages

Currently supported languages:

- `JAVA`
- `KOTLIN`
- `SQL`
- `XML`
- `JAVASCRIPT`
- `PLAIN_TEXT`

If an unknown language is provided, the backend falls back to `PLAIN_TEXT`.

## Response Structure

The `/ai/explain` response is organized into several blocks:

- `explanation` — structured AI explanation.
- `metadata` — technical response metadata.
- `effectiveContext` — normalized backend values used for processing.
- `fileContext` — source file and project context.
- `requestContext` — original values received from the client.

## Observability

Each `/ai/explain` response contains technical metadata:

- `traceId` — request trace identifier used in logs.
- `responseTimeMs` — total backend response time in milliseconds.
- `retried` — indicates whether the backend had to retry parsing the AI response.

## Validation

The backend validates incoming `/ai/explain` requests.

Examples of validation rules:

- `code` must not be empty.
- `code` must not exceed the configured maximum length.
- `lineStart` and `lineEnd` must be greater than or equal to `1`.
- `lineStart` must be less than or equal to `lineEnd`.
- Optional text fields must not be blank when provided.

## Project Status

Current status:

- Backend core is implemented.
- Structured AI responses are supported.
- Backend capability handshake is available via `/backend-info`.
- IntelliJ Plugin MVP is implemented and connected to the backend.
- The project is ready for future plugin features such as Improve Code, Diff View, RAG context, and project chat.

## IntelliJ Plugin MVP

The project includes an IntelliJ IDEA plugin prototype.

Current plugin capabilities:

- Reads selected code from the editor.
- Sends selected code and editor context to the backend.
- Detects language from file extension.
- Displays structured AI explanation in the J-Aide Tool Window.
- Opens the Tool Window automatically after receiving a response.
- Shows friendly error notifications when the backend is unavailable.
- Uses Jackson for safe backend request serialization and response parsing.

Current request context sent by the plugin:

- `code`
- `mode`
- `language`
- `fileName`
- `lineStart`
- `lineEnd`
- `projectName`
- `moduleName`
- `pluginVersion`
- `ideVersion`

## IntelliJ Plugin Architecture

The IntelliJ plugin is organized by responsibility.

| Package        | Responsibility                                   |
|----------------|--------------------------------------------------|
| `config`       | Plugin configuration and constants               |
| `context`      | Extracting selected code and full editor context |
| `dto`          | Backend request and response DTOs                |
| `error`        | Plugin-specific exceptions and error handling    |
| `factory`      | Request creation and object construction         |
| `language`     | Programming language detection by file extension |
| `model`        | Internal plugin models                           |
| `notification` | User notifications inside IntelliJ IDEA          |
| `ui`           | J-Aide Tool Window and UI rendering              |

### Explain Code Flow

1. The user selects code in IntelliJ IDEA.
2. The user runs the Explain Code action.
3. The plugin extracts the selected code and editor context.
4. The plugin detects the programming language from the file extension.
5. The plugin builds a backend request JSON using Jackson.
6. The plugin sends the request to the J-Aide backend.
7. The backend returns a structured JSON response.
8. The plugin parses the response using Jackson.
9. The explanation is displayed in the J-Aide Tool Window.
10. If an error occurs, the plugin shows a friendly notification.

## Project Structure

```text
j-aide/
├── README.md
├── .env.example
├── .gitignore
└── j-aide/
    ├── pom.xml
    ├── j-aide-api/
    ├── j-aide-app/
    ├── j-aide-core/
    └── j-aide-intellij-plugin/
```

The repository contains the backend Maven multi-module project and the IntelliJ IDEA plugin module.

Module responsibilities:

| Module | Responsibility |
|---|---|
| `j-aide-api` | REST controllers, API DTOs, and global API error handling |
| `j-aide-app` | Spring Boot application entry point, backend configuration, and tracing filter |
| `j-aide-core` | AI service logic, prompt templates, backend properties, supported modes and languages |
| `j-aide-intellij-plugin` | IntelliJ IDEA plugin implementation |


## Capabilities Status

| Capability                        | Status  | Notes                                                    |
|-----------------------------------|---------|----------------------------------------------------------|
| Explain selected code             | Done    | MVP implementation is working                            |
| Read selected code from editor    | Done    | Uses active IntelliJ editor selection                    |
| Send editor context to backend    | Done    | Includes file, project, module, IDE, and plugin metadata |
| Detect language by file extension | Done    | Falls back to plain text when language is unknown        |
| JSON request serialization        | Done    | Implemented with Jackson                                 |
| JSON response parsing             | Done    | Implemented with Jackson                                 |
| Display result in Tool Window     | Done    | Shows structured explanation in J-Aide Tool Window       |
| Friendly error notifications      | Done    | Backend and plugin errors are handled separately         |
| Backend capability handshake      | Done    | Available through `/backend-info`                        |
| Improve Code                      | Planned | Future feature, not implemented yet                      |
| Refactor Code                     | Planned | Future feature from project roadmap                      |
| Diff View                         | Planned | Future IDE feature for before/after code comparison      |
| RAG project context               | Planned | Future backend feature using vector search               |
| Chat with project code            | Planned | Future plugin feature                                    |

## Future Features

### Improve Code

Improve Code is a planned feature for future versions of J-Aide.

The goal of this feature is to help developers improve selected code without manually rewriting it from scratch.

Planned behavior:

- read selected code from the active editor;
- send selected code and editor context to the backend;
- ask the AI model to suggest an improved version of the code;
- return explanation of what was changed and why;
- display the suggested improvement in the J-Aide Tool Window.

Possible improvement types:

- simplify code;
- improve readability;
- suggest better naming;
- reduce duplication;
- suggest safer implementation;
- point out possible code smells.

Current status:

- Improve Code is not implemented yet.
- No IDE action is registered for this feature yet.
- No backend endpoint is implemented for this feature yet.
- Diff View is planned separately and will be needed before applying changes directly to files.

This feature will be implemented after the Explain Code MVP is stable.

## Environment Configuration

The project includes an `.env.example` file with example environment variables.

Available variables:

- `OLLAMA_BASE_URL` — Ollama server URL.
- `OLLAMA_MODEL` — local model used for AI explanations.
- `J_AIDE_APP_NAME` — backend application name.
- `J_AIDE_APP_VERSION` — backend version.
- `J_AIDE_CODE_MAX_LENGTH` — maximum allowed code length for explain requests.

If these variables are not provided, default values from `application.yaml` are used.


