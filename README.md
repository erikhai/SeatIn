# SeatIn — Event Ticketing Systemx
### **ELEC5619_Practical01_Group_3**

## Team Members

| Name                  | SID       | Email                           | Role                         |
|-----------------------|-----------|---------------------------------|------------------------------|
| Mithil Patel          | 520461266 | mpat5683@uni.sydney.edu.au      | Developer                    |
| Erik Hai              | 520489709 | ehai2411@uni.sydney.edu.au      | Product Owner & Developer    |
| Aaron Emerson Manoj   | 520436633 | leme4933@uni.sydney.edu.au      | Scrum Master & Developer     |
| Mustafa El Tannir     | 520307768 | muel5605@uni.sydney.edu.au      | Developer                    |
| Ahmed Mirza   | 530476267 | amir7598@uni.sydney.edu.au      | Developer   |

---


---

## 1. Problem Summary
Event organisers need an easy way to create and host events, define a seating layout, and track bookings and revenue. Attendees need a simple way to browse events, see available seats (including accessible seats), as well as book and receive a ticket.

**SeatIn** provides:
- a **React** front‑end for customers and organisers,
- a **Spring Boot** back‑end,
- a **database** layer for users, events, seats and bookings,
- and **analytics** for organisers (revenue, seats filled, popular areas).

---

## 2. High‑Level Goals
**Organisers can:**
- create events and venues,
- customise seating (rows, columns, coloured / blocked areas),
- set prices per seat category,
- cancel events,
- view analytics on current and past events (revenue, % filled).

**Customers can:**
- sign up / log in,
- view all public events,
- open an event and see a live seat view,
- select one or more available seats and book,
- cancel their booking,
- receive an e‑ticket (QR) after booking.

**System supports:**
- role‑based UI (guest / customer / organiser / admin),
- secure API access (JWT),
- RESTful server–client communication (Axios / fetch).

---

## 3. Working Functionalities (as required in the rubric)
- Login as **customer** or **organiser**
- View all **current events**
- Organisers can **create** new events and configure venues
- Organisers can **cancel** events
- Event page shows **available vs booked** seats
- Customers can **book** 1+ seats
- Customers can **cancel** their own booking
- On booking, **ticket/QR is generated or sent**
- Organisers can view **analytics** for:
  - current events (booked vs not booked, revenue),
  - past events (revenue, % seats filled),
  - venues (popular seats / areas)

---

## 4. How to Run the Application (quick guide)

### 4.1 Back‑end (Spring Boot)

**.env configuration**

Upon first time running you must create/update a file called **`.env`** in the **SeatIn/** (back‑end) project root so the application can load database credentials. Example:

```dotenv
DB_URL=jdbc:postgresql://localhost:5432/seatin
DB_USERNAME=YOUR_DB_USERNAME
DB_PASSWORD=YOUR_DB_PASSWORD
```

Without this `.env` the back‑end will start but **will not be able to connect to the database**, which is what the tutors need to test.

**After Set up procced:**

```bash
cd SeatIn
./gradlew bootRun
```

The back‑end will start on **http://localhost:8080**.


### 4.2 Front‑end (React)

```bash
cd frontend
npm install
npm start
```

The front‑end will start on **http://localhost:3000** and will call the back‑end on **http://localhost:8080**. If your back‑end runs on a different port, update the Axios base URL in the front‑end.

---

## 5. Libraries Used and Their Purpose

This project uses Spring Boot on the back-end and React on the front-end. Below is a grouped list showing **what each dependency is for** (security, DB, email, QR, testing, UI, etc.), so markers can trace it back to the rubric.

### 5.1 Back-end (Java / Gradle)

#### Core Spring / Web
```gradle
implementation 'org.springframework.boot:spring-boot-starter'
implementation 'org.springframework.boot:spring-boot-starter-web'
```
- **Purpose:** start the Spring Boot app and expose RESTful endpoints.

#### Data / Database
```gradle
implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
runtimeOnly 'org.postgresql:postgresql'
```
- **Purpose:** JPA for entities/repositories and PostgreSQL as the runtime DB.

#### Security / Authentication
```gradle
implementation 'org.springframework.boot:spring-boot-starter-security'
implementation 'org.springframework.security:spring-security-crypto:6.3.1'
implementation 'io.jsonwebtoken:jjwt-api:0.11.5'
runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.11.5'
runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.11.5'
```
- **Purpose:** secure endpoints (Spring Security), password hashing/crypto, and JWT creation/verification for login.

#### Environment / Config
```gradle
implementation 'me.paulschwarz:spring-dotenv:3.0.0'
```
- **Purpose:** load `.env` so tutors can set `DB_USERNAME` and `DB_PASSWORD` without hardcoding in `application.properties`.

#### Monitoring / Ops
```gradle
implementation 'org.springframework.boot:spring-boot-starter-actuator'
```
- **Purpose:** health/info endpoints for monitoring the API.

#### QR / Ticket Generation
```gradle
implementation 'com.google.zxing:core:3.5.2'
implementation 'com.google.zxing:javase:3.5.2'
```
- **Purpose:** generate QR codes for e-tickets.

#### Email / Notifications
```gradle
implementation 'com.sendgrid:sendgrid-java:4.10.1'
```
- **Purpose:** send booking confirmation / ticket emails.

#### Lombok / Boilerplate
```gradle
compileOnly 'org.projectlombok:lombok'
annotationProcessor 'org.projectlombok:lombok'
```
- **Purpose:** reduce boilerplate (getters/setters/builders) in entities and DTOs.

#### Testing
```gradle
testImplementation 'org.springframework.boot:spring-boot-starter-test'
testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
```
- **Purpose:** JUnit 5 + Spring Test for unit/integration tests and for generating JaCoCo coverage.

---

### 5.2 Front-end (npm)

#### Core React
```text
react@19.1.1
react-dom@19.1.1
react-scripts@5.0.1
```
- **Purpose:** main React app (CRA).

#### Routing / Navigation
```text
react-router-dom@7.8.2
```
- **Purpose:** client-side routing and protected routes (`/auth/...`).

#### HTTP / API Calls
```text
axios@1.11.0
```
- **Purpose:** call Spring Boot REST APIs, attach JWT.

#### UI / Styling
```text
tailwindcss@3.4.17
postcss@8.5.6
autoprefixer@10.4.21
bootstrap@5.3.7
```
- **Purpose:** fast layout & styling; Tailwind-like utilities + Bootstrap for some components.

#### Charts / Analytics Page
```text
@mui/x-charts@8.11.3
@emotion/react@11.14.0
@emotion/styled@11.14.1
```
- **Purpose:** render organiser analytics (revenue, seats, counts) with MUI charts.

#### UX / Notifications / Carousel
```text
react-toastify@11.0.5
react-slick@0.31.0
slick-carousel@1.8.1
date-fns@4.1.0
```
- **Purpose:** toast messages (success/error), carousels/sliders, and date utilities for events.

#### Testing (frontend)
```text
@testing-library/react@16.3.0
@testing-library/jest-dom@6.7.0
@testing-library/user-event@13.5.0
@testing-library/dom@10.4.1
web-vitals@2.1.4
```
- **Purpose:** unit/integration tests for React components and performance metrics.


---

## 6. Testing & Coverage 

The project includes unit / integration tests for the Spring Boot back‑end.

**Run tests and generate coverage:**

```bash
cd SeatIn
./gradlew clean test jacocoTestReport
```

Outputs:
- Test report (HTML): `build/reports/tests/test/index.html`
- Coverage report (HTML): `build/reports/jacoco/test/html/index.html`

---

## 7. Additional Front‑end Notes
- React app uses a **dynamic navbar** that reacts to login state.
- JWT returned from the back‑end is stored (e.g. `localStorage`) and attached to protected requests.
- UI uses custom components + Tailwind‑style classes.

---


## 8. Icon References (used in Analytics Page)

- **Seat ICON:** https://www.flaticon.com/free-icons/seat  
  Author: **HAJICON** – Flaticon
- **Calendar ICON:** https://www.flaticon.com/free-icons/calendar  
  Author: **Freepik** – Flaticon
- **User ICON:** https://www.flaticon.com/free-icons/user  
  Author: **Freepik** – Flaticon
- **Dollar ICON:** https://www.flaticon.com/free-icons/dollar  
  Author: **Gregor Cresnar** – Flaticon
