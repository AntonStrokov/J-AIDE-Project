# J-Aide

J-Aide is a backend service for an AI-powered programming assistant.

The project provides REST API endpoints that can explain source code, return structured AI responses, and expose backend capabilities for future IDE plugin integration.

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
- The project is being prepared for future IDE plugin integration.

## Next Step: IntelliJ Plugin MVP

The next planned step is to prepare a minimal IntelliJ IDEA plugin that can communicate with the J-Aide backend.

Planned MVP features:

- Send selected code from IntelliJ IDEA to `POST /ai/explain`.
- Display structured explanation results inside the IDE.
- Use `GET /backend-info` to check backend capabilities.
- Start with local backend communication via `http://localhost:8080`.
## Environment Configuration

The project includes an `.env.example` file with example environment variables.

Available variables:

- `OLLAMA_BASE_URL` — Ollama server URL.
- `OLLAMA_MODEL` — local model used for AI explanations.
- `J_AIDE_APP_NAME` — backend application name.
- `J_AIDE_APP_VERSION` — backend version.
- `J_AIDE_CODE_MAX_LENGTH` — maximum allowed code length for explain requests.

If these variables are not provided, default values from `application.yaml` are used.
