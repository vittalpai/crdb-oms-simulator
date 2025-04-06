
# CRDB OMS Simulator

A lightweight **Order Management System (OMS)** built with **Spring Boot** and **CockroachDB**, designed for simulating order lifecycles and load testing with **Apache JMeter**.

---

## 🔧 Prerequisites

Ensure the following are installed:

- Java 17+
- Maven
- CockroachDB (local or cloud)
- Apache JMeter (for testing)

---

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/vittalpai/crdb-oms-simulator.git
cd crdb-oms-simulator
```

### 2. Create Database Schema

Connect to CockroachDB using CLI:

```bash
cockroach sql --url "postgresql://<user>:<password>@<host>:26257/default_db?sslmode=require"
```

Then run:

```sql
CREATE DATABASE IF NOT EXISTS oms_db;
USE oms_db;
```

Apply schema:

```bash
cockroach sql --url "...?sslmode=require" --file=schema/create_oms_tables.sql
```

### 3. Configure the Application

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://<host>:26257/oms_db?sslmode=require
spring.datasource.username=<your-username>
spring.datasource.password=<your-password>
```

### 4. Build and Run the Service

```bash
mvn clean install
mvn spring-boot:run
```

App will be available at: `http://localhost:8080`

---

## 📡 API Endpoints

| Method | Endpoint                          | Description                                  |
|--------|-----------------------------------|----------------------------------------------|
| POST   | `/api/oms/create/{orderId}`       | Create new order                             |
| GET    | `/api/oms/{orderId}`              | Fetch order by ID                            |
| PUT    | `/api/oms/update-status/{orderId}`| Update order status                          |
| DELETE | `/api/oms/cancel/{orderId}`       | Cancel an order                              |
| GET    | `/api/oms/list/{status}`          | List orders by status (mocked)               |
| POST   | `/api/oms/process/{orderId}`      | Process an order (update items/amount)       |

---

## 🧪 Load Testing with JMeter

### Script: `jmeter/oms-simulator-script.jmx`

- Traffic split using throughput controller
- Sequential order IDs with JMeter `__counter()` function

### Run via CLI:

```bash
jmeter -n -t jmeter/oms-simulator-script.jmx -l results.jtl
```

---

## 📁 Project Structure

```
crdb-oms-simulator/
├── schema/                      # CockroachDB DDL
│   └── create_oms_tables.sql
├── jmeter/                      # JMeter test script
│   └── oms-simulator-script.jmx
├── src/main/java/com/oms/demo  # Spring Boot OMS code
└── README.md
```