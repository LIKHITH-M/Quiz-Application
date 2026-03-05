# 📋 Quiz Application — Microservices Architecture

## Overview
This is a **Quiz Application** built using **Java Spring Boot** with a **microservices architecture**. The application is split into 4 independent services that communicate with each other to create quizzes, serve questions, and calculate scores. It demonstrates how requests are routed and handled between microservices using **Eureka Service Discovery**, **Spring Cloud Gateway**, and **OpenFeign** for inter-service communication.

---

## 🏗️ Architecture & Request Flow

The diagram below illustrates the high-level architecture and the specific request flow for creating a quiz. The **Quiz Service** depends on the **Question Service** to fetch questions via **OpenFeign**.

![Quiz Application Architecture and Request Flow](https://github.com/LIKHITH-M/Quiz-Application/blob/main/quiz-workflow.png)

1. **Client** sends a request to the **API Gateway**.
2. **API Gateway** uses **Eureka** to discover the target service and routes the request.
3. **Quiz Service** handles quiz-related requests and internally calls the **Question Service** to fetch/manage questions.
4. Each service maintains its **own PostgreSQL database** (`quizdb` and `questiondb`) to ensure data isolation.

---

## 📦 Services Breakdown

### 1. 🛰️ Service Registry (Port `8761`)
* **Role**: **Eureka Server** — the central hub for service discovery.
* **Registration**: All other services register themselves here on startup.
* **Configuration**: Set with `register-with-eureka=false` and `fetch-registry=false` as it is the server itself.

### 2. ⚡ API Gateway (Port `8765`)
* **Role**: Single entry point for all client requests.
* **Routing**: Uses **Spring Cloud Gateway** to automatically route requests based on the service name in the URL (e.g., `/question-service/...`).

### 3. ❓ Question Service (Port `8083`)
* **Role**: Manages the question bank.
* **Database**: `questiondb` (PostgreSQL).
* **Endpoints**:
    * `GET /question/allQuestions` — Get all questions.
    * `GET /question/category/{category}` — Get questions by category.
    * `POST /question/add` — Add a new question.
    * `POST /question/getScore` — Calculate score from submitted responses.
* **Logic**: Uses a native SQL query with `ORDER BY RANDOM() LIMIT :numQ` to pick random questions.

### 4. 📝 Quiz Service (Port `8090`)
* **Role**: Manages quiz creation, question retrieval, and scoring.
* **Database**: `quizdb` (PostgreSQL).
* **Endpoints**:
    * `POST /quiz/create` — Create a quiz (category, number of questions, title).
    * `GET /quiz/get/{id}` — Get quiz questions by quiz ID.
    * `POST /quiz/submit/{id}` — Submit answers and get score.
* **Communication**: Annotated with `@FeignClient(name = "QUESTION-SERVICE")` to call Question Service endpoints.

---

## 🛠️ Tech Stack

| Technology | Purpose |
| :--- | :--- |
| **Java 17+** | Programming language |
| **Spring Boot** | Application framework |
| **Spring Cloud Eureka** | Service discovery & registration |
| **Spring Cloud Gateway** | API Gateway / request routing |
| **Spring Cloud OpenFeign** | Declarative inter-service REST calls |
| **Spring Data JPA** | Database access (ORM) |
| **PostgreSQL** | Relational database (2 databases) |
| **Lombok** | Boilerplate code reduction (`@Data`) |
| **Maven** | Build & dependency management |

---
