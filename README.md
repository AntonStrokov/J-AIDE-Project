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

The backend configures the Ollama chat model with a low temperature (`0.1`) to reduce response variance and make repeated developer-assistant requests more stable.

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

| Capability                        | Status      | Notes                                                    |
|-----------------------------------|-------------|----------------------------------------------------------|
| Explain selected code             | Done        | MVP implementation is working                            |
| Explain Runtime Error             | Done MVP    | Backend endpoint and IntelliJ plugin action explain stack traces and runtime error logs |
| Error explanation clipboard input | Done MVP    | Allows explaining copied stack traces through `Tools -> J-Aide: Explain Runtime Error` |
| Error explanation console popup   | Done MVP    | Allows explaining selected build output and runtime logs from console popup menu |
| Runtime error input validation    | Done MVP    | Rejects regular source code and accepts stack traces, compiler errors, and build logs |
| Runtime error text limit          | Done MVP    | Uses dedicated `J_AIDE_ERROR_MAX_LENGTH` limit instead of source code length limit |
| Read selected code from editor    | Done        | Uses active IntelliJ editor selection                    |
| Send editor context to backend    | Done        | Includes file, project, module, IDE, and plugin metadata |
| Detect language by file extension | Done        | Falls back to plain text when language is unknown        |
| JSON request serialization        | Done        | Implemented with Jackson                                 |
| JSON response parsing             | Done        | Implemented with Jackson                                 |
| Display result in Tool Window     | Done        | Shows structured Explain and Improve previews            |
| Back to Code button               | Done        | Available in Explain and Improve previews to hide Tool Window and return to editor |
| Friendly error notifications      | Done        | Backend and plugin errors are handled separately         |
| Backend capability handshake      | Done        | Available through `/backend-info`                        |
| Improve Code                      | Done        | Backend AI flow, IntelliJ action, preview, validation    |
| Improve response validation       | Done MVP    | Rejects no-op, blank, markdown-fenced improvements and missing change descriptions |
| Diff View                         | Done        | Custom J-Aide diff dialog with IntelliJ side-by-side Diff Viewer |
| Apply Last Improvement            | Done        | Available from Tool Window and Diff dialog with safety checks and Undo support |
| Diff Viewer flicker               | Known Issue | Tool Window hiding is required before opening Diff Viewer |
| Refactor Code                     | Planned     | Future feature from project roadmap                      |
| RAG project context               | Planned     | Future backend feature using vector search               |
| Chat with project code            | Planned     | Future plugin feature                                    |

## Implemented and Planned Features

### Improve Code

Improve Code is implemented as an MVP feature.

The current implementation sends selected code to the backend, asks the AI model to suggest an improved version, and displays the result in the J-Aide Tool Window.

Current behavior:

- reads selected code from the active editor;
- sends selected code and editor context to the backend;
- calls the backend endpoint `POST /ai/improve`;
- asks the AI model to suggest an improved version of the selected code;
- returns a structured improvement result;
- displays the improvement in the J-Aide Tool Window;
- shows Original Code and Improved Code in scrollable editor-like blocks;
- allows the user to open the IntelliJ side-by-side Diff Viewer manually;
- allows the user to apply the latest improvement explicitly.

Current status:

- Improve Code backend AI flow is implemented.
- `POST /ai/improve` is available.
- IntelliJ plugin action `J-Aide: Improve Selected Code` is registered.
- Suggested improved code is displayed in the J-Aide Tool Window.
- Tool Window preview is structured and uses dedicated UI panels.
- Long-code backend errors are displayed as user-friendly plugin errors.
- Applying changes is implemented as an MVP through `JaideApplyImprovementService`.
- Apply is available from the J-Aide Tool Window and from the J-Aide Diff Dialog.
- Apply uses safety checks before modifying the document.
- Undo is supported through IntelliJ `WriteCommandAction`.
- Plugin-side improvement validation prevents saving unsafe or meaningless improvements.
- No-op improvements are rejected when improved code equals original code after normalization.
- Blank improved code is rejected.
- Markdown-fenced improved code is rejected.
- Improvements without change descriptions are rejected.
- Improve prompt rules were strengthened to require clean source code, real change descriptions, Russian user-facing response fields, and to reduce invented change descriptions.

Important safety rules:

- J-Aide does not change user files automatically.
- The user must explicitly click Apply before any file is modified.
- Before applying, the plugin checks that the original selected code still matches the current document text.
- If the document was changed after the AI response, Apply is stopped with a warning.

Possible improvement types:

- simplify code;
- improve readability;
- suggest better naming;
- reduce duplication;
- suggest safer implementation;
- point out possible code smells.

Future polish:

- improve semantic validation of AI suggestions;
- add diff-aware validation for `changes` to detect descriptions that are not present in `improvedCode`;
- detect suspicious improvements;
- add richer highlighting for changed lines;
- improve confidence and risk visual indicators.

### Diff View

Diff View is implemented as an MVP feature for Improve Code.

The current implementation shows the original selected code and the AI-improved code in two ways:

- a safe preview inside the J-Aide Tool Window;
- a built-in IntelliJ side-by-side Diff Viewer.

Current status:

- Original Code is displayed in the J-Aide Tool Window.
- Improved Code is displayed in the J-Aide Tool Window.
- Code blocks support vertical and horizontal scrolling.
- Changes are displayed as a structured list.
- IntelliJ Diff Viewer opens with `Original` and `Improved` contents.
- The Tool Window is hidden before opening the Diff Viewer to prevent UI overlap.
- No files are changed automatically.
- Apply is available as an explicit user action from the J-Aide Tool Window.
- Apply uses `JaideApplyImprovementService`.
- Apply checks that the original selected code still matches the current document before modifying it.
- Undo is supported through IntelliJ `WriteCommandAction`.

Known issue:

- Opening the Diff Viewer from the Tool Window may cause a visual flicker.
- The Tool Window hide step is required because otherwise the Tool Window overlaps the Diff Viewer.
- Do not remove Tool Window hiding as a quick fix.
- Future fix should introduce a coordinated Diff opening flow or a custom J-Aide controlled diff screen.

Future polish:

- add a clearer Apply control inside a custom Diff screen;
- improve visual highlighting for changed lines;
- improve the Diff opening UX without breaking Tool Window behavior.

### Explain Runtime Error

Explain Runtime Error is implemented as an MVP feature.

Current behavior:

- accepts selected error text from the active editor;
- accepts selected build output or runtime logs from the console popup menu;
- supports copied stack traces or logs through clipboard fallback;
- is available from the editor popup menu;
- is available from the console popup menu;
- is also available through `Tools -> J-Aide: Explain Runtime Error`;
- validates input before sending it to the backend;
- rejects regular source code with a warning and suggests using `J-Aide: Explain Selected Code`;
- calls the backend endpoint `POST /ai/explain-error`;
- displays structured runtime error explanation in the J-Aide Tool Window;
- automatically hides the J-Aide Tool Window when the user switches editor tabs after viewing a runtime error explanation;
- accepts longer stack traces and logs than regular source code explain requests;
- uses plugin-side multi-language error markers before sending the request to the backend;

Current status:

- Backend endpoint is implemented and tested through Postman.
- IntelliJ plugin action is implemented.
- Clipboard fallback is implemented.
- Runtime error preview uses the same visual style as existing Explain/Improve previews.
- `Show Diff` and `Apply` are hidden for runtime error explanations.
- `Back to Code` is available.
- Runtime error preview is automatically hidden on editor tab switch; regular Explain and Improve previews stay open.
- Console popup integration is implemented.
- Dedicated backend error text limit is implemented.
- Runtime error input validation supports multiple error families: JVM/compiler, JavaScript/Node.js, SQL, XML/config, and common system logs.
- Runtime error input validation is implemented;
- Error explanation prompt is strengthened to preserve exact file names, class names, methods, ports, dependency names, symbols, error codes, and likely typo hints from logs.

Smoke-test checklist:

- selected Java compiler error from editor -> `J-Aide: Explain Runtime Error`;
- selected stack trace or build output from console popup -> `J-Aide: Explain Runtime Error`;
- copied stack trace or log -> `Tools -> J-Aide: Explain Runtime Error`;
- regular Java source code -> should be rejected with a warning;
- long stack trace over regular source code limit -> should be accepted up to `J_AIDE_ERROR_MAX_LENGTH`;
- SQL error -> should be accepted;
- XML/config error -> should be accepted;
- JavaScript/Node.js error -> should be accepted;
- Docker port binding error -> should be accepted;
- Maven dependency resolution error -> should be accepted;
- Maven non-parseable POM error -> should be accepted;
- Node.js module resolution error -> should be accepted;
- regular Java source code -> should be rejected with a warning.

Known limitation:

- Terminal / Run Console popup integration is available for console-like editor popups covered by IntelliJ `ConsoleEditorPopupMenu`.
- If a specific terminal UI does not expose this popup group, the fallback flow is: copy stack trace, then run `Tools -> J-Aide: Explain Runtime Error`.

Future polish:

- verify more IntelliJ terminal and run console variants and add extra popup groups if needed;
- test more error types: Spring startup errors, Maven errors, Docker errors, datasource errors, port conflicts;
- continue expanding validation markers based on real failed smoke-test cases;

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


