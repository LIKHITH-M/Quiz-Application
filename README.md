# 📋 Quiz Application — Microservices Architecture

## Overview
This is a **Quiz Application** built using **Java Spring Boot** with a **microservices architecture**. The application is split into 4 independent services that communicate with each other to create quizzes, serve questions, and calculate scores. It demonstrates how requests are routed and handled between microservices using **Eureka Service Discovery**, **Spring Cloud Gateway**, and **OpenFeign** for inter-service communication.

---

## 🏗️ Architecture & Request Flow

The diagram below illustrates the high-level architecture and how requests flow from a client through the API Gateway to the relevant microservices. The Quiz Service depends on the Question Service to fetch questions when creating a quiz or calculating scores.

![Microservices Architecture Diagram](https://github.com/LIKHITH-M/Quiz-Application/blob/main/quiz-workflow.png)

1. **Client** sends a request to the **API Gateway**.
2. **API Gateway** uses Eureka to discover the target service and routes the request.
3. **Quiz Service** handles quiz-related requests, and internally calls **Question Service** via OpenFeign to fetch/manage questions.
4. Each service has its **own PostgreSQL database** (`quizdb` and `questiondb`).

---

## 📦 Services Breakdown

### 1. 🛰️ Service Registry (Port `8761`)
* **Role:** Eureka Server — the central hub for service discovery.
* All other services register themselves here on startup.
* Configured with `register-with-eureka=false` and `fetch-registry=false` since it is the server itself.

### 2. ⚡ API Gateway (Port `8765`)
* **Role:** Single entry point for all client requests.
* Uses **Spring Cloud Gateway** with discovery locator enabled.
* Automatically routes requests to the correct service based on the service name in the URL (e.g., `/question-service/...` routes to Question Service).

### 3. ❓ Question Service (Port `8083`)
* **Role:** Manages the question bank.
* **Database:** `questiondb` (PostgreSQL).
* **Model:** `Question` entity with fields — `id`, `questionTitle`, `option1-4`, `rightAnswer`, `difficultyLevel`, `category`.
* **Endpoints:**

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| GET | `/question/allQuestions` | Get all questions |
| GET | `/question/category/{category}` | Get questions by category |
| POST | `/question/add` | Add a new question |
| GET | `/question/generate` | Generate random question IDs by category |
| POST | `/question/getQuestions` | Get question details by list of IDs (hides correct answer using `QuestionWrapper`) |
| POST | `/question/getScore` | Calculate score from submitted responses |

* Uses a **native SQL query** with `ORDER BY RANDOM() LIMIT :numQ` to pick random questions.

### 4. 📝 Quiz Service (Port `8090`)
* **Role:** Manages quiz creation, question retrieval, and scoring.
* **Database:** `quizdb` (PostgreSQL).
* **Model:** `Quiz` entity with `id`, `title`, and `questionIds` (list of integer IDs stored via `@ElementCollection`).
* **Endpoints:**

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| POST | `/quiz/create` | Create a quiz (category, number of questions, title) |
| POST | `/quiz/get/{id}` | Get quiz questions by quiz ID |
| POST | `/quiz/submit/{id}` | Submit answers and get score |

* Uses **OpenFeign** (`QuizInterface`) annotated with `@FeignClient(name = "QUESTION-SERVICE")` to call Question Service endpoints.

---

## 🔄 How a Request Flows (Example: Creating a Quiz)

The request flow diagram below visualizes the step-by-step process of creating a new quiz. This involves the Client interacting with the API Gateway, which routes the request to the Quiz Service. The Quiz Service then uses Feign to communicate with the Question Service to get a list of question IDs, and finally saves the quiz details.

![Quiz Creation Request Flow Diagram](path/to/your/generated/request_flow_diagram.png)

1. Client sends `POST /quiz-service/quiz/create` with `{ categoryName, numQuestions, title }` to API Gateway (`:8765`).
2. API Gateway discovers `quiz-service` via Eureka and forwards the request.
3. `QuizController.createQuiz()` → `QuizService.createQuiz()` is called.
4. Quiz Service calls **Question Service** via Feign: `quizInterface.getQuestionsForQuiz(category, numQ)`.
5. Question Service runs `SELECT q.id FROM question q WHERE q.category=:category ORDER BY RANDOM() LIMIT :numQ` and returns a list of random question IDs.
6. Quiz Service saves the quiz (title + question IDs) to `quizdb` and returns `"Success"`.

---

## 🛠️ Tech Stack

| Technology | Purpose |
| :--- | :--- |
| **Java 17+** | Programming language |
| **Spring Boot** | Application framework |
| **Spring Cloud Eureka**| Service discovery & registration |
| **Spring Cloud Gateway** | API Gateway / request routing |
| **Spring Cloud OpenFeign** | Declarative inter-service REST calls |
| **Spring Data JPA** | Database access (ORM) |
| **PostgreSQL** | Relational database (2 databases) |
| **Lombok** | Boilerplate code reduction (`@Data`) |
| **Maven** | Build & dependency management |
