FROM node:18-alpine as build
WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .
# Zmiana 1: Usunięto flagę --prod, która nie jest potrzebna w react-scripts
RUN npm run build

FROM nginx:alpine

# Zmiana 2: Kopiujemy folder całego folderu build do katalogu nginx
# Uwaga: Kopiujemy zawartość folderu build (kropka na końcu jest ważna jeśli kopiujemy całą zawartość)
COPY --from=build /app/build /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]