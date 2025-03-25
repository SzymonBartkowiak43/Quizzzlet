
# Quizzis - English learning app🎓

**Quizzis** is an app that helps you learn English in a fun and interactive way. It supports the learning of vocabulary, grammar and other language skills through flashcards, quizzes and watching videos. Learning has never been easier!


> **Author:**  Szymon Bartkowiak  <br>
> **Linkedin:** https://www.linkedin.com/in/szymon-bartkowiak-7516532b4/  <br>
> **Email:** szymon.b4310@gmail.com

## Video
https://github.com/user-attachments/assets/0a384748-82de-427c-9431-8f46bc5afcbd



## Screenshots:
![Ekran główny aplikacji](https://github.com/user-attachments/assets/4e3e2646-5686-41d1-b9b1-ae1c9bdcf783)
_Home screen of the Quizzis app_

![Widok quizu](https://github.com/user-attachments/assets/f394d677-376d-435a-8b7a-65e80f156516)
_Preview of recommended films_

![Panel użytkownika](https://github.com/user-attachments/assets/f3b2ee2e-a9c8-40eb-8f7e-2fbe5f37a718)
_User flashcard sets_

![Ekran ustawień](https://github.com/user-attachments/assets/aa4fb8d6-a023-443e-8e61-4137eef94a46)
_Kit editing_

## The application allows you to:
- learn with flashcards
- create and solve quizzes
- keep track of your learning
- watch, rate and comment videos
- interact with other users (add to friends, share sets) (planned)
- send messages (planned)

## Technologies used:
- **Spring Boot Framework** - the foundation of the application, which enables a REST API and backend.
- **Spring Boot Security** - provides user authentication and authorisation.
- **Maven** - for dependency management and project building.
- **Docker** - facilitates running applications in containers.
- **H2 Database** - in-memory database for storing temporary data during testing.
- **SQL** - for creating database queries.
- **Liquibase** - for managing database migrations.
- **HTML, CSS, JavaScript** - to create the user interface.
- **Thymeleaf** - template engine for dynamically generating HTML pages on the backend.

## TO DO:
- [ ] Write 50 tests (currently: 27/50)
- [ ] Give more admin rights
- [ ] Add the ability to add friends and share sets
- [ ] Add possibility to send messages to other users

## How to run the app:
1. Clone the repository:

```bash
git clone https://github.com/SzymonBartkowiak43/Quizzzlet.git
```

2. Go to the project folder:

```bash
cd Quizzzlet
```

3. Install Maven dependencies:

```bash
mvn install
```

4. Start the test data application by selecting the "test" profile:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=test
```
and you can create a new account or login in:
```
admin@wp.com      password:admin
```

5. Alternatively, to run the application without test data:

```bash
docker-compose --profile dev up
```

6. The application is also available at http://localhost:8080/. You can now log in and try it out.
   
