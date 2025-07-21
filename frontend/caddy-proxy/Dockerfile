FROM node:20-alpine as build-stage

WORKDIR /app/frontend

COPY frontend/frontend/package*.json ./

RUN npm install --omit=dev

COPY frontend/frontend/ .

RUN npm run build

FROM caddy:2.7.6-alpine AS caddy_runtime

COPY frontend/caddy-proxy/Caddyfile /etc/caddy/Caddyfile

COPY --from=build-stage /app/frontend/dist /usr/share/caddy/html

EXPOSE 80
EXPOSE 443

CMD ["caddy", "run", "--config", "/etc/caddy/Caddyfile", "--adapter", "caddyfile"]