# Менеджер задач (Java)

[![hexlet-check](https://github.com/kicha3007-boop/java-project-99/actions/workflows/hexlet-check.yml/badge.svg)](https://github.com/kicha3007-boop/java-project-99/actions)
[![Java CI](https://github.com/kicha3007-boop/java-project-99/actions/workflows/main.yml/badge.svg)](https://github.com/kicha3007-boop/java-project-99/actions/workflows/main.yml)

На практике узнаете про проектирование баз данных, связи между сущностями, PaaS, ORM, мониторинг ошибок, Swagger, фреймворк Spring.

Учебный проект Хекслета: https://ru.hexlet.io/programs/java
Как это должно работать: https://files.hexlet.app/a/xg6yxv

## Стек

- Java 21, Spring Boot 3.5: Web, Data JPA, Validation, Security (OAuth2 Resource Server, JWT)
- H2 (профиль `dev`, по умолчанию) и PostgreSQL (профиль `prod`)
- springdoc-openapi — документация API на `/swagger-ui.html`
- Sentry SDK — отправка ошибок в коллектор (Bugsink / Sentry)
- Фронтенд — npm-пакет `@hexlet/java-task-manager-frontend`, собирается в статику Spring
- JUnit 5, MockMvc, Instancio + Datafaker, JsonUnit, JaCoCo (порог 80% в `check`), Spotless

## Установка

Нужны JDK 21, Node.js 22 и make.

```bash
git clone https://github.com/kicha3007-boop/java-project-99.git
cd java-project-99
make setup        # npm ci, сборка фронтенда в src/main/resources/static, ./gradlew installDist
make start        # http://localhost:8080, профиль dev (H2, консоль на /h2-console)
make start-prod   # профиль prod (PostgreSQL из переменных окружения)
make lint
make test
```

Переменные окружения:

| Переменная | Назначение |
|---|---|
| `PORT` | порт, по умолчанию 8080 |
| `SPRING_PROFILES_ACTIVE` | `dev` (по умолчанию) или `prod` |
| `JDBC_DATABASE_URL`, `JDBC_DATABASE_USERNAME`, `JDBC_DATABASE_PASSWORD` | PostgreSQL в `prod` |
| `RSA_PUBLIC_KEY`, `RSA_PRIVATE_KEY` | ключи подписи JWT в PEM; без них пара создаётся при старте |
| `SENTRY_DSN` | адрес проекта в коллекторе ошибок |
| `SENTRY_AUTH_TOKEN` (+ `SENTRY_ORG`, `SENTRY_PROJECT`) | токен для выгрузки исходников в коллектор при сборке |

## Использование

Система управления задачами: пользователи, статусы, метки, задачи с исполнителем и фильтрацией.
Администратор при старте: `hexlet@example.com` / `qwerty`.

```bash
TOKEN=$(curl -s -H "Content-Type: application/json"   -d '{"username":"hexlet@example.com","password":"qwerty"}' localhost:8080/api/login)
curl -H "Authorization: Bearer $TOKEN" "localhost:8080/api/tasks?titleCont=create&status=draft"
```

Ресурсы API (`GET` список и запись, `POST`, `PUT` с частичным обновлением, `DELETE`):
`/api/users`, `/api/task_statuses`, `/api/labels`, `/api/tasks`. Фильтр задач: `titleCont`,
`assigneeId`, `status`, `labelId`. Открыты только `/`, статика фронтенда и `POST /api/login`.

---

<details>
<summary>Автоматические тесты Хекслета</summary>

Тесты запускаются на каждый коммит. За запуск отвечает файл `.github/workflows/hexlet-check.yml` — не удаляйте и не переименовывайте ни его, ни репозиторий.

</details>

## О Хекслете

[Хекслет](https://ru.hexlet.io/) — школа программирования: авторские программы обучения с практикой, поддержкой наставников и реальными проектами, которые остаются в резюме. Этот репозиторий — один из таких проектов.
