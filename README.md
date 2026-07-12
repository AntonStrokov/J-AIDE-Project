# J-Aide

J-Aide is a local AI-powered developer assistant consisting of a Spring Boot backend and an IntelliJ IDEA plugin.

The plugin sends selected source code, runtime errors, and editor context to the local backend. The backend uses Ollama and Qwen2.5-Coder to generate structured responses that are displayed inside the J-Aide Tool Window.

J-Aide follows an explicit user-control model: AI suggestions never modify project files automatically. Code changes are applied only after a deliberate user action and additional safety checks.

## MVP Scope

The `v0.1.0-mvp` release includes:

- `Explain Selected Code` with `FAST`, `SMART`, and `DEEP` explanation modes;
- `Improve Selected Code` with structured preview, Copy Code, Diff View, explicit Apply, safety checks, and Undo;
- `Explain Runtime Error` for selected editor text, console output, and clipboard input;
- `Generate Tests` with structured preview and generated test code copying;
- `Check AI Setup` through the Tools menu and the J-Aide Tool Window;
- local Spring Boot backend integration with Ollama;
- quick backend/provider/model diagnostics through `GET /backend-info`;
- full AI readiness diagnostics with trial generation through `GET /ai/health`.

### Scope Freeze

The feature scope of `v0.1.0-mvp` is frozen.

New functionality must not be added to the MVP unless it fixes a confirmed release-blocking defect.

The following improvements are not required for the MVP release:

- guided remediation and automatic Ollama problem fixing;
- onboarding wizard;
- automatic test file creation;
- cloud AI providers and managed model runtime;
- streaming responses;
- RAG and project-wide context;
- Mentor View;
- telemetry, request history, and production security.

These items remain in the Post-MVP or Future Research backlog.

## Tech Stack

- Java 21
- Spring Boot
- Spring Web
- Maven
- LangChain4j
- Ollama
- Qwen2.5-Coder 7B

## Running the Application

### Requirements

- Java 21
- Ollama
- The `qwen2.5-coder:7b` model
- Windows PowerShell for the commands below

Verify the installed Java version:

```powershell
java -version
```

Verify that Ollama is available:

```powershell
ollama --version
```

Check the installed models:

```powershell
ollama list
```

The expected model is:

```text
qwen2.5-coder:7b
```

If the model is missing, download it:

```powershell
ollama pull qwen2.5-coder:7b
```

### Start Ollama

Run one Ollama server instance only.

Use either the Ollama desktop application or start the server manually:

```powershell
ollama serve
```

Do not start `ollama serve` if another Ollama process is already listening on port `11434`.

### Build the Backend

From the repository root:

```powershell
cd .\j-aide

.\mvnw.cmd clean verify
```

The command builds all backend modules, runs the tests, and creates the executable JAR:

```text
j-aide-app\target\j-aide-app-0.1.0.jar
```

### Run the Backend

```powershell
cd .\j-aide

java -jar .\j-aide-app\target\j-aide-app-0.1.0.jar
```

By default, the backend starts at:

```text
http://localhost:8080
```

Verify that it is running:

```powershell
Invoke-RestMethod http://localhost:8080/backend-info |
    ConvertTo-Json -Depth 10
```

The response should contain:

```text
backendVersion: 0.1.0
status: UP
```

The backend uses Ollama with `qwen2.5-coder:7b` and a low temperature of `0.1` to reduce response variance.

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
  },
  "defaults": {
    "defaultMode": "SMART",
    "defaultLanguage": "JAVA"
  },
  "llmInfo": {
    "llmProvider": "OLLAMA",
    "llmModel": "qwen2.5-coder:7b"
  },
  "health": {
    "backendStatus": "READY",
    "providerStatus": "READY",
    "modelStatus": "READY",
    "providerVersion": "0.30.10",
    "responseTimeMs": 104,
    "message": "AI provider and configured model are available."
  }
}
```
Current `/backend-info` health behavior:

- Performs a quick health check without sending a trial generation request to the model.
- `backendStatus` reports whether the J-Aide backend is ready.
- `providerStatus` reports whether the configured AI provider is reachable.
- `modelStatus` reports whether the configured model is present in the provider.
- `providerVersion` reports the detected Ollama version when available.
- If Ollama is unavailable, `providerStatus` becomes `FAILED` and `modelStatus` becomes `UNKNOWN`.
- If Ollama is reachable but the configured model is missing, `providerStatus` remains `READY` and `modelStatus` becomes `FAILED`.
- Guided remediation and automatic setup recovery are not included in the MVP.

### GET /ai/health

Runs a full AI setup health check, including a trial generation request to the configured model.

Example curl request:

```bash
curl http://localhost:8080/ai/health
```

Example response:

```json
{
  "backendStatus": "READY",
  "providerStatus": "READY",
  "modelStatus": "READY",
  "providerVersion": "0.30.10",
  "responseTimeMs": 3187,
  "message": "AI provider and configured model are ready."
}
```

Current `/ai/health` behavior:

- Performs the provider reachability and configured model checks.
- Stops before trial generation if the provider or model prerequisite check fails.
- Sends a lightweight trial generation request when the prerequisites are ready.
- Reports `READY` only after the trial generation succeeds.
- May take longer than `/backend-info` because Ollama may need to load the model.
- Reports the full check duration in `responseTimeMs`.
- Does not automatically start Ollama, download a missing model, or change local environment settings.

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

### POST /ai/improve

Improves selected source code and returns a structured AI response. The endpoint does not modify user files.

Example request:

```json
{
  "code": "public int sum(int a, int b) {\n    return a + b;\n}",
  "mode": "SMART",
  "language": "java",
  "fileName": "Calculator.java",
  "lineStart": 1,
  "lineEnd": 3,
  "projectName": "j-aide-test",
  "moduleName": "app",
  "pluginVersion": "0.1.0",
  "ideVersion": "2025.1"
}
```

Example response:

```json
{
  "success": true,
  "improvement": {
    "summary": "Improved readability and clarified the method intent.",
    "improvedCode": "public int sum(int firstNumber, int secondNumber) {\n    return firstNumber + secondNumber;\n}",
    "changes": [
      "Renamed method parameters to make their purpose clearer."
    ],
    "riskHint": "The method behavior is unchanged.",
    "confidence": "high"
  },
  "metadata": {
    "traceId": "example-trace-id",
    "backendVersion": "0.1.0",
    "responseTimeMs": 1234,
    "retried": false
  }
}
```

The IntelliJ plugin displays the original and improved code together with the change descriptions, risk hint, and confidence. The user can copy the suggestion, open the Diff View, or apply it explicitly. Before applying, the plugin verifies that the original selected text still matches the current document. Applied changes can be reverted through IntelliJ Undo.

### POST /ai/tests

Generates JUnit 5 / Mockito-style test code for selected source code and returns a structured AI response.

This endpoint supports the implemented `Generate Tests` IntelliJ plugin action. The plugin displays the generated test code in a structured preview and allows the user to copy it. The MVP does not create test files automatically and does not modify user code.

Example request:

```json
{
  "code": "public int sum(int a, int b) {\n    return a + b;\n}",
  "mode": "SMART",
  "language": "java",
  "fileName": "Calculator.java",
  "lineStart": 1,
  "lineEnd": 3,
  "projectName": "j-aide-test",
  "moduleName": "app",
  "pluginVersion": "0.1.0",
  "ideVersion": "2025.1"
}
```
Example response:

```json
{
  "testResult": {
    "summary": "Тесты для метода sum класса Calculator",
    "testCode": "import static org.junit.jupiter.api.Assertions.assertEquals;\n\nimport org.junit.jupiter.api.Test;\n\nclass CalculatorTest {\n\n    @Test\n    void testSumWithPositiveNumbers() {\n        Calculator calculator = new Calculator();\n        assertEquals(5, calculator.sum(2, 3));\n    }\n}",
    "testFramework": "JUnit 5",
    "coveredScenarios": [
      "сумма двух положительных чисел"
    ],
    "riskHint": "Явных рисков не обнаружено",
    "confidence": "high"
  },
  "rawJson": null,
  "success": true,
  "metadata": {
    "traceId": "example-trace-id",
    "backendVersion": "0.1.0",
    "responseTimeMs": 11109,
    "retried": false
  }
}
```
Current status:

- Backend endpoint `POST /ai/tests` is implemented.
- IntelliJ plugin action `Generate Tests` is implemented.
- The plugin displays a structured test generation preview.
- Generated test code can be copied from the preview.
- The endpoint returns `summary`, `testCode`, `testFramework`, `coveredScenarios`, `riskHint`, and `confidence`.
- `testCode` is expected to contain a full test class with imports, class declaration, and test methods when enough source context is available.
- Empty `riskHint` values are normalized to a safe default message.
- Automatic test file creation and direct project modification are not included in the MVP.
- The backend and plugin flow were manually regression-tested.

### POST /ai/explain-error

Explains a runtime error, exception stack trace, or application log and returns a structured AI response.

This endpoint is intended for analyzing errors such as Java exceptions, Spring Boot startup failures, Maven errors, Docker errors, database connection errors, and similar runtime diagnostics.

Example request:

```json
{
  "errorText": "java.lang.NullPointerException: Cannot invoke \"String.length()\" because \"name\" is null\n    at com.example.UserService.getNameLength(UserService.java:12)\n    at com.example.UserController.getUser(UserController.java:25)",
  "mode": "SMART",
  "language": "java",
  "fileName": "UserService.java",
  "lineStart": 12,
  "lineEnd": 12,
  "projectName": "j-aide-test",
  "moduleName": "app",
  "pluginVersion": "0.1.0",
  "ideVersion": "2025.1"
}
```

Example response:

```json
{
  "success": true,
  "errorExplanation": {
    "summary": "Short explanation of what happened",
    "likelyCause": "Most probable root cause",
    "whereToLook": "Class, method, configuration, dependency, port, database, Docker, or Spring bean to inspect",
    "suggestedFixes": [
      "Suggested fix step 1",
      "Suggested fix step 2"
    ],
    "riskHint": "Possible risk or side effect to consider",
    "confidence": "high"
  },
  "metadata": {
    "traceId": "generated-trace-id",
    "backendVersion": "0.1.0",
    "responseTimeMs": 1234,
    "retried": false
  }
}
```

Current status:

- Backend endpoint `POST /ai/explain-error` is implemented.
- Runtime error explanation prompt is implemented.
- The endpoint was tested manually through Postman.
- IntelliJ plugin action `J-Aide: Explain Runtime Error` is implemented.
- The plugin can explain selected error text from the editor.
- The plugin can also explain error text copied to the clipboard through `Tools -> J-Aide: Explain Runtime Error`.
- The plugin action is also available from the console popup menu for build output and runtime logs.
- Runtime error input is validated before sending the request: regular source code is rejected with a warning, while stack traces, compiler errors, and build logs are accepted.
- Runtime error explanations are displayed in the J-Aide Tool Window using a structured preview panel aligned with the existing Explain/Improve UI style.
- The endpoint returns structured error explanation with summary, likely cause, where to look, suggested fixes, risk hint, confidence, and metadata.
- Full error text is not logged; backend logs request length, context metadata, success status, confidence, and response time.
- Runtime error explanation uses a dedicated error text length limit.
- Default maximum error text length is `15000` characters through `J_AIDE_ERROR_MAX_LENGTH`.
- Plugin-side runtime error input validation supports JVM, Java/Kotlin compiler errors, JavaScript/Node.js, SQL, XML/config, and common system log markers.

## Explain Modes

J-Aide currently supports three explanation modes for source code explanation:

- `FAST` — short and quick explanation.
- `SMART` — balanced explanation, used by default.
- `DEEP` — more detailed explanation.

Current mode behavior:

- `POST /ai/explain` uses `FAST`, `SMART`, and `DEEP` to select different prompt templates.
- `POST /ai/improve` currently uses one dedicated improve prompt template.
- `POST /ai/explain-error` currently uses one dedicated runtime error explanation prompt template.
- The `SMART` explain prompt is strengthened to reduce inaccurate claims and better highlight nullable calls, weak names, and `System.out.println` usage.

In the IntelliJ plugin, the selected Explain mode is controlled from the J-Aide Tool Window. The default mode is `SMART`.

The Explain mode selector is shown only for `J-Aide: Explain Selected Code` previews. It is hidden for Improve Code and Explain Runtime Error previews because those flows use dedicated prompt templates.

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

- Backend MVP version `0.1.0` is implemented and successfully builds as an executable Spring Boot JAR.
- IntelliJ Plugin MVP version `0.1.0` is implemented and connected to the backend.
- Explain Selected Code supports `FAST`, `SMART`, and `DEEP` modes.
- Improve Selected Code supports structured preview, code copying, Diff View, explicit Apply, and Undo.
- Explain Runtime Error supports editor selection, console selection, and clipboard fallback.
- Generate Tests supports structured preview and generated test code copying.
- Check AI Setup reports backend, provider, and model health through the IntelliJ Tool Window and the `Tools` menu.
- Backend and plugin MVP flows were manually regression-tested.
- The plugin was installed and verified in a regular IntelliJ IDEA instance outside the Gradle Sandbox.
- The MVP scope is frozen. New functionality is deferred unless a release-blocking defect is discovered.
- RAG context, Mentor View, cloud AI providers, automatic remediation, and advanced project-wide analysis remain post-MVP work.

## IntelliJ Plugin MVP

The project includes an IntelliJ IDEA plugin MVP.

Current plugin capabilities:

- Reads selected code from the editor.
- Sends selected code and editor context to the backend.
- Detects language from file extension.
- Displays structured AI explanation in the J-Aide Tool Window.
- Allows selecting Explain mode (`FAST`, `SMART`, `DEEP`) from the J-Aide Tool Window for source code explanations.
- Rejects runtime error text in `J-Aide: Explain Selected Code` and suggests using `J-Aide: Explain Runtime Error`.
- Explains selected runtime errors, stack traces, and application logs through `J-Aide: Explain Runtime Error`.
- Supports clipboard fallback for runtime error explanation through `Tools -> J-Aide: Explain Runtime Error`.
- Supports runtime error explanation directly from the console popup menu.
- Validates runtime error input to avoid sending regular source code to the error explanation flow.
- Displays runtime error explanations in a structured Tool Window preview aligned with the existing Explain/Improve UI style.
- Plugin UI labels, colors, and shared preview layout constants are centralized in dedicated configuration classes.
- Sends selected code to the backend for code improvement.
- Displays suggested improved code in the J-Aide Tool Window.
- Allows copying improved code through a dedicated `Copy Code` action without applying changes to the file.
- Provides `J-Aide: Generate Tests` action from the editor context menu.
- Sends selected code and editor context to the backend `POST /ai/tests` endpoint.
- Displays generated test code in the J-Aide Tool Window through a dedicated Generate Tests Preview.
- Generate Tests Preview follows the same Tool Window UI style as Explain, Improve, and Runtime Error previews.
- Generate Tests Preview shows structured sections: status, summary, test framework, generated test code, covered scenarios, risk hint, and confidence.
- Generate Tests Preview supports copying generated test code through the shared `Copy Code` button.
- Generate Tests MVP does not create test files automatically and does not modify user code.
- Provides `J-Aide: Check AI Setup` from the IntelliJ `Tools` menu.
- Provides a permanent `Check AI Setup` button in the J-Aide Tool Window.
- Opens the J-Aide Tool Window automatically when the check is started from the `Tools` menu.
- Shows a loading state directly in the Tool Window while the AI setup check is running.
- Displays the full AI health result directly in the Tool Window.
- Shows backend, provider, and model statuses together with the Ollama version, response time, and diagnostic message.
- Uses colored health status indicators: `READY` is green, `DEGRADED` uses a warning color, `FAILED` is red, and `UNKNOWN` is gray.
- Displays backend connection errors directly in the Tool Window with a `Retry` action.
- Uses the backend `GET /ai/health` endpoint, including a lightweight trial generation request when the provider and configured model are available.
- Reports unavailable Ollama and missing model states without requiring an IntelliJ or backend restart after the local AI setup is restored.
- Uses the Tool Window as the primary UX channel for AI setup checks instead of popup notifications.
- Uses Jackson for safe backend request serialization and response parsing.

### Build and Install the IntelliJ Plugin

From the repository root, open the plugin module:

```powershell
cd .\j-aide\j-aide-intellij-plugin
```

Build the installable plugin distribution:

```powershell
.\gradlew.bat buildPlugin
```

The generated plugin archive is located at:

```text
build\distributions\j-aide-intellij-plugin-0.1.0.zip
```

Do not extract the ZIP archive before installation.

Install the plugin in IntelliJ IDEA:

- Open `File -> Settings -> Plugins`.
- Open the plugin settings menu.
- Select `Install Plugin from Disk`.
- Choose `j-aide-intellij-plugin-0.1.0.zip`.
- Restart IntelliJ IDEA only if the IDE requests it.

Before using the plugin:

- Start Ollama.
- Start the J-Aide backend on `http://localhost:8080`.
- Open the J-Aide Tool Window.
- Run `Check AI Setup` and confirm that the backend, provider, and model statuses are `READY`.

The plugin was manually installed and verified in a regular IntelliJ IDEA instance outside the Gradle Sandbox.

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
4. The plugin rejects selected runtime error text, stack traces, or logs and suggests using `J-Aide: Explain Runtime Error`.
5. The plugin detects the programming language from the file extension.
6. The plugin builds a backend request JSON using Jackson.
7. The plugin sends the request to the J-Aide backend.
8. The backend returns a structured JSON response.
9. The plugin parses the response using Jackson.
10. The explanation is displayed in the J-Aide Tool Window.
11. If an error occurs, the plugin shows a friendly notification.

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

| Capability                       | Status          | Notes                                                                            |
| -------------------------------- | --------------- | -------------------------------------------------------------------------------- |
| Explain Selected Code            | Done MVP        | Structured explanation is displayed in the J-Aide Tool Window                    |
| Explain modes                    | Done MVP        | Supports `FAST`, `SMART`, and `DEEP`                                             |
| Explain input validation         | Done MVP        | Rejects runtime error text and redirects the user to the correct action          |
| Improve Selected Code            | Done MVP        | Backend flow, IntelliJ action, structured preview, and validation                |
| Copy improved code               | Done MVP        | Copies the suggestion without modifying the source file                          |
| Diff View                        | Done MVP        | Custom J-Aide dialog with the IntelliJ side-by-side Diff Viewer                  |
| Apply Improvement                | Done MVP        | Available from the Tool Window and Diff dialog with safety checks                |
| Undo applied improvement         | Done MVP        | Applied editor changes can be reverted through IntelliJ Undo                     |
| Improve response validation      | Done MVP        | Rejects blank, no-op, markdown-fenced, and incomplete responses                  |
| Explain Runtime Error            | Done MVP        | Supports stack traces, compiler failures, build output, and runtime logs         |
| Runtime error editor input       | Done MVP        | Reads selected error text from the active editor                                 |
| Runtime error clipboard fallback | Done MVP        | Available through `Tools -> J-Aide: Explain Runtime Error`                       |
| Runtime error console popup      | Done MVP        | Reads selected build output and runtime logs from the console                    |
| Runtime error input validation   | Done MVP        | Rejects regular source code and accepts supported diagnostic formats             |
| Generate Tests                   | Done MVP        | Sends selected source context to `POST /ai/tests`                                |
| Generate Tests preview           | Done MVP        | Displays summary, framework, code, scenarios, risk, and confidence               |
| Copy generated test code         | Done MVP        | Copies the generated test class from the Tool Window preview                     |
| Automatic test file creation     | Post-MVP        | The MVP does not create or modify project test files                             |
| Check AI Setup from Tools        | Done MVP+       | Opens the J-Aide Tool Window and runs the full setup check in-panel              |
| Check AI Setup from Tool Window  | Done MVP+       | Shows loading, result, error, and Retry states inside the J-Aide panel           |
| Quick backend health             | Done MVP        | `/backend-info` checks backend, provider reachability, and model presence        |
| Full AI health check             | Done MVP        | `/ai/health` includes a lightweight trial generation request                     |
| AI setup recovery check          | Done MVP        | Restored Ollama or model availability is detected without restarting the backend |
| Colored health indicators        | Done MVP+       | READY is green, DEGRADED uses a warning color, FAILED is red, and UNKNOWN is gray |
| Guided AI setup remediation      | Post-MVP        | Automatic startup, model download, and environment repair are not included       |
| Structured backend responses     | Done MVP        | Explain, Improve, Runtime Error, and Generate Tests return structured data       |
| Request metadata                 | Done MVP        | Includes file, project, module, IDE, plugin, trace, and timing information       |
| Plugin notifications             | Done MVP        | Information, warning, and error notifications expire automatically               |
| Installable plugin ZIP           | Done MVP        | Built with `buildPlugin` and verified outside the Gradle Sandbox                 |
| Diff Viewer flicker              | Known Issue     | The Tool Window is hidden before opening the Diff Viewer                         |
| Explain mode persistence         | Post-MVP        | The selected mode should persist between IDE sessions                            |
| Tool Window startup state        | Post-MVP        | The last visible J-Aide view should be restored after IDE restart                |
| RAG project context              | Future Research | Project-wide retrieval and context indexing                                      |
| Mentor View                      | Future Research | Architectural and educational project analysis                                   |
| Cloud AI providers               | Future Research | Alternative remote providers and managed runtime options                         |

## Known Limitations and Post-MVP Work

The following limitations are accepted for J-Aide `v0.1.0-mvp`.

### Known MVP Limitations

- Generate Tests displays and copies generated test code but does not create test files automatically.
- Generated test quality depends on the amount and completeness of the selected source context.
- AI setup checks report problems but do not automatically start Ollama, download models, or repair environment settings.
- The selected Explain mode is not yet persisted between IntelliJ IDEA sessions.
- The J-Aide Tool Window does not yet restore its last visible preview after an IDE restart.
- Opening the Diff Viewer may cause a short visual flicker while the Tool Window is hidden.
- Automated IntelliJ plugin tests are not enabled; plugin behavior is verified through manual regression testing.
- AI-generated improvements and tests must be reviewed by the user before use.

### Post-MVP Backlog

- Guided Ollama installation, model download, and environment remediation.
- Clearer guided AI health diagnostics and remediation instructions.
- Explain mode persistence between IDE sessions.
- Tool Window startup restoration and last-view persistence.
- Automatic test file creation with explicit user confirmation.
- Stronger generated test validation and formatting checks.
- Semantic validation of Improve Code responses.
- Additional Runtime Error input formats and console integrations.
- Cleaner Tool Window mode and visibility state management.
- Automated plugin testing strategy.

### Future Research

- RAG-based project context and project-wide indexing.
- Mentor View for architectural and educational analysis.
- Cloud AI providers and managed model runtimes.
- Streaming AI responses.
- Project history, telemetry, and advanced observability.
- Deeper IntelliJ project and build-system integration.

## IntelliJ Plugin Testing Notes

Pure unit tests for the IntelliJ plugin module are currently not enabled.

A first attempt to add JUnit 5 test infrastructure directly to `j-aide-intellij-plugin` was reverted because the IntelliJ Platform Gradle Plugin test task started the test JVM with `com.intellij.util.lang.PathClassLoader`, which caused console `gradlew build` to fail before JUnit execution.

Current rule:

- do not reintroduce plugin test infrastructure until `gradlew build` remains green;
- prefer manual sandbox checks for plugin behavior in the current MVP stage;
- future testing strategy should be investigated separately.

Possible future directions:

- extract pure Java validation logic into a plain Java module and test it with regular JUnit;
- configure a separate clean unit test source set/task that does not use IntelliJ Platform runtime;
- use proper IntelliJ Platform tests only for plugin behavior that depends on IDE APIs.

## Troubleshooting

### Maven Dependency Download Failure

If Maven reports that a dependency download was interrupted and the failure was cached locally, force Maven to download that dependency again.

```powershell
cd .\j-aide

.\mvnw.cmd -U dependency:get "-Dartifact=groupId:artifactId:version"
```

Replace `groupId:artifactId:version` with the dependency coordinates shown in the Maven error.

After the download succeeds, reload all Maven projects in IntelliJ IDEA.

### Maven Cannot Delete the Backend JAR

If `mvnw clean verify` cannot delete the backend JAR because it is being used by another process, stop the running backend before rebuilding.

- Find the PowerShell window where the backend is running.
- Stop the backend with `Ctrl + C`.
- Keep Ollama running.
- Run the Maven build again.

Windows cannot delete or replace the executable JAR while its Java process is still running.

### Ollama Uses the Wrong Model Directory

Use only one Ollama server process on port `11434`.

If Ollama tries to load models from an unexpected user-profile directory, stop the Ollama desktop application and any existing `ollama serve` process before starting it again with the correct model path.

```powershell
$env:OLLAMA_MODELS = "C:\ollama-models"

ollama serve
```

In another PowerShell window, verify the available models:

```powershell
ollama list
```

The expected model is:

```text
qwen2.5-coder:7b
```

Do not run the Ollama desktop server and a separate manual `ollama serve` process at the same time. Both processes use port `11434`, and the desktop application may start Ollama with a different model directory.

### AI Health Reports Provider Failed

If `Check AI Setup` reports that the provider is `FAILED` and the model is `UNKNOWN`, verify that Ollama is running and listening on:

```text
http://127.0.0.1:11434
```

After Ollama is restored, run `Check AI Setup` again. The J-Aide backend does not need to be restarted.

### AI Health Reports Model Failed

If the provider is `READY` but the model is `FAILED`, verify that the configured model is installed in the active Ollama model directory.

Run:

```powershell
ollama list
```

If `qwen2.5-coder:7b` is missing, download it:

```powershell
ollama pull qwen2.5-coder:7b
```

After the model download completes, run `Check AI Setup` again. The backend restart is not required.

## Environment Configuration

The project includes an `.env.example` file with example environment variables.

Available variables:

- `OLLAMA_BASE_URL` — Ollama server URL.
- `OLLAMA_MODEL` — local model used for AI explanations.
- `OLLAMA_TEMPERATURE` — Ollama generation temperature. Default: `0.1`.
- `OLLAMA_TIMEOUT_SECONDS` — Ollama request timeout in seconds. Default: `60`.
- `J_AIDE_APP_NAME` — backend application name.
- `J_AIDE_APP_VERSION` — backend version.
- `J_AIDE_CODE_MAX_LENGTH` — maximum allowed source code length for code explain and improve requests.
- `J_AIDE_ERROR_MAX_LENGTH` — maximum allowed error text length for runtime error explanation requests. Default: `15000`.

If these variables are not provided, default values from `application.yaml` are used.


