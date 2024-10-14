# Quizzis - Aplikacja do nauki języka angielskiego 🎓

**Quizzis** to aplikacja, która pomoże Ci w nauce języka angielskiego w przyjemny i interaktywny sposób. Dzięki quizom i testom aplikacja wspiera naukę słownictwa, gramatyki oraz innych umiejętności językowych. Nauka nigdy nie była prostsza!



https://github.com/user-attachments/assets/6b413bf3-0c25-43f5-8a18-e120004259f1



## Zrzuty ekranu:
![Ekran główny aplikacji](https://github.com/user-attachments/assets/4e3e2646-5686-41d1-b9b1-ae1c9bdcf783)
_Ekran główny aplikacji Quizzis_


![Widok quizu](https://github.com/user-attachments/assets/f394d677-376d-435a-8b7a-65e80f156516)
_Podgląd polecanych filmów_


![Panel użytkownika](https://github.com/user-attachments/assets/f3b2ee2e-a9c8-40eb-8f7e-2fbe5f37a718)
_Zestawy użytkownika_


![Ekran ustawień](https://github.com/user-attachments/assets/aa4fb8d6-a023-443e-8e61-4137eef94a46)
_Edycja zestawu_


## Funkcje aplikacji:
- Tworzenie i rozwiązywanie quizów
- Śledzenie postępów nauki
- Interaktywna nauka słownictwa i gramatyki
- Oglądanie polecanych filmów
- Integracja z przyjaciółmi (planowane)
- Wysyłanie wiadomości do innych użytkowników (planowane)

## Wykorzystane technologie:
- **Spring Boot Framework** - fundament aplikacji, który umożliwia tworzenie REST API oraz backendu.
- **Spring Boot Security** - zapewnia uwierzytelnianie i autoryzację użytkowników.
- **Maven** - do zarządzania zależnościami oraz budowy projektu.
- **Docker** - ułatwia uruchamianie aplikacji w kontenerach.
- **H2 Database** - baza danych w pamięci do przechowywania tymczasowych danych podczas testów.
- **SQL** - do tworzenia zapytań do bazy danych.
- **Liquibase** - do zarządzania migracjami bazy danych.
- **HTML, CSS, JavaScript** - tworzą interfejs użytkownika.
- **Thymeleaf** - szablon silnika do dynamicznego generowania stron HTML na backendzie.

## TODO:
- [ ] Napisać 50 testów (aktualnie: 27/50)
- [ ] Dodać możliwość dodawania znajomych
- [ ] Dać większe uprawnienia adminowi
- [ ] Dodać możliwość wysyłania wiadomości do innych użytkowników


## Jak uruchomić aplikację:
1. Sklonuj repozytorium:
   ```bash
   git clone https://github.com/SzymonBartkowiak43/Quizzzlet.git
   ```

2. Przejdź do katalogu z projektem:
   ```bash
   cd Quizzzlet
   ```

3. Zainstaluj zależności Maven:
   ```bash
   mvn install
   ```

4. Uruchom aplikację z danymi testowymi, wybierając profil 'test':
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=test
   ```

5. Alternatywnie, aby uruchomić aplikację bez danych testowych:
   ```bash
   docker-compose --profile dev up
   ```

6. Aplikacja będzie dostępna pod adresem [http://localhost:8080/](http://localhost:8080/). Możesz teraz zalogować się i przetestować jej działanie.
   Polecam użyć konta email: admin@wp.com hasło: admin będzie ono odrazu wypełnione danymi testowymi.
