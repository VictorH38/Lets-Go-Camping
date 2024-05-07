# Let's Go Camping!

## Introduction
"Let's Go Camping!" is a web application that helps users discover national parks to visit based on a range of criteria. Developed using React, SpringBoot, Java, and the NPS API, the platform offers a variety of features to improve the experience of planning park trips, keeping track of favorites, and getting recommendations with friends.

## Features
- **Search & Discover:** Search for national parks based on various attributes, and obtain detailed information about each.
- **User Accounts:** Create a user account to maintain a personalized list of favorite parks.
- **Favorites & Comparison:** Update and review a favorite park list, and compare it with friends using the friend feature.
- **Park Suggestions:** Get park suggestions between friends based on shared favorite parks.
- **Accessibility:** Accessible to people with disabilities through features like audio support, alternative labels, larger/bold text, and keyboard navigation.
- **Cross-Platform Compatibility:** Works seamlessly on Chrome browsers and mobile devices with dynamic styles.
- **Data Security:** Secure data storage with hashed passwords, SQL injection protection, and policies covering login, authentication, and search limits.
- **Architecture:** Features a React client-side and a SpringBoot server-side architecture.

## Technology Stack
- **Frontend:** React, HTML, CSS, JavaScript
- **Backend:** SpringBoot, Java
- **Database:** MySQL
- **API Integration:** National Park Service (NPS) API

## Getting Started

### Prerequisites
- Java 14 or later
- Node.js and npm
- MySQL
- Maven
- Docker (optional for containerization)

### Installation
1. **Clone the Repository:**  
    ```bash
    git clone https://github.com/VictorH38/Lets-Go-Camping.git
    ```
2. **Configure MySQL Database:**  
    Set up the MySQL database using the schema and data scripts provided in `/src/main/resources/db`.

3. **Update Database Configuration:**  
    Update the database configuration in `src/main/resources/application.properties` with your MySQL credentials.

4. **Install Dependencies:**  
    - **Frontend:**  
        Navigate to `/site` and install the dependencies.  
        ```bash
        cd site
        npm install
        ```
    - **Backend:**  
        Install the Java dependencies using Maven.  
        ```bash
        mvn install
        ```

### Running the Application

- **Frontend:**  
    Start the frontend by navigating to `/site` and running:  
    ```bash
    npm start
    ```
    This will launch the frontend on `http://localhost:3000`.

- **Backend:**  
    Compile and run the SpringBoot application from the root directory:  
    ```bash
    mvn compile
    mvn spring-boot:run
    ```
    The backend will be accessible at `http://localhost:8080`.

- **Containerized (Optional):**  
    Run the application in a Docker container:  
    ```bash
    docker-compose up --build
    ```
    The web application will be available on `http://localhost:8080`.

### Testing
- **Java:**  
    Run the unit tests with Maven:  
    ```bash
    mvn test
    ```

- **JavaScript:**  
    Navigate to `/site` and run Jest tests:  
    ```bash
    npm run test
    ```
    To run coverage tests:  
    ```bash
    npm run test -- --coverage --watchAll=false
    ```

## Useful Links & Resources
- **React:**  
    - [React Official Documentation](https://reactjs.org/docs/getting-started.html)
    - [React Hooks Guide](https://reactjs.org/docs/hooks-intro.html)

- **SpringBoot:**  
    - [SpringBoot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/index.html)

- **NPS API:**  
    - [NPS API Guide](https://www.nps.gov/subjects/digital/nps-data-api.htm)

- **Accessibility Guidelines:**  
    - [W3C Web Accessibility Initiative](https://www.w3.org/WAI/)

## Authors
- Victor Hoang
- Sang Kim
- Matthew Grant
- Ryan Yeung
- Damien Felch
