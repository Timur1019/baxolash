# Деплой на Render (без docker-compose)

## Архитектура

- **Frontend** — Render Static Site (npm install, npm run build → dist/)
- **Backend** — Render Web Service (Docker, порт 8080)
- **Database** — Render PostgreSQL (managed)
- **Storage** — Cloudflare R2 (S3-совместимый)

## 1. Backend (Web Service, Docker)

- **Build**: Dockerfile в корне репозитория.
- **Start command**: по умолчанию `java -jar app.jar` (в образе).
- **Environment** (обязательно задать в Render):

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://<host>:5432/<db>
SPRING_DATASOURCE_USERNAME=<user>
SPRING_DATASOURCE_PASSWORD=<password>

S3_ACCESS_KEY=<r2-access-key>
S3_SECRET_KEY=<r2-secret-key>
S3_REGION=auto
S3_BUCKET=<bucket-name>
S3_ENDPOINT=https://<account-id>.r2.cloudflarestorage.com

APP_CORS_ALLOWED_ORIGINS=https://your-frontend.onrender.com
```

Опционально: `S3_PUBLIC_URL` — публичный URL бакета (если нужны прямые ссылки на файлы).

## 2. Frontend (Static Site)

- **Build command**: `npm install && npm run build`
- **Publish directory**: `dist`
- **Environment** (при сборке): `VITE_API_URL=https://your-backend.onrender.com` (URL вашего Web Service)

## 3. Database

- Создать PostgreSQL в Render, скопировать Internal/External URL в `SPRING_DATASOURCE_URL`.
- Liquibase применит миграции при старте backend.

## 4. Cloudflare R2

- Создать R2 bucket, создать API-токен (Access Key + Secret).
- Endpoint: `https://<ACCOUNT_ID>.r2.cloudflarestorage.com` (в настройках R2).
- Те же ключи задать в переменных S3_* backend.

## 5. Локальный запуск

- Скопировать `.env.example` в `.env` (или задать переменные в системе).
- Backend: `./gradlew bootRun` (нужны PostgreSQL и S3/R2).
- Frontend: `cd frontend && npm install && npm run dev` (VITE_API_URL=http://localhost:8080 по умолчанию).
