FROM node:20 AS build
WORKDIR /app

# Копируем файлы проекта и устанавливаем зависимости
COPY package*.json ./
RUN npm install

# Копируем остальной код и собираем Vue.js приложение
COPY . .
RUN npm run build

# Используем Nginx для раздачи статических файлов
FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html

# Открываем порт для Nginx
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]