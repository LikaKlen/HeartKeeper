FROM python:3-alpine

WORKDIR /app

# Копируем зависимости сначала для лучшего кэширования
COPY requirements.txt .

# Устанавливаем зависимости
RUN apk add --no-cache --virtual .build-deps gcc musl-dev postgresql-dev \
    && pip install --upgrade pip \
    && pip install -r requirements.txt \
    && apk del .build-deps

# Копируем остальные файлы
COPY . .

# Устанавливаем переменные окружения
ENV FLASK_APP=app/main.py
ENV FLASK_ENV=production
ENV FLASK_RUN_HOST=0.0.0.0
ENV FLASK_RUN_PORT=5000

# Открываем порт
EXPOSE 5000

# Запускаем приложение
CMD ["flask", "run"]