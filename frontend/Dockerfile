FROM node:20-alpine as build-stage

WORKDIR /app

COPY package*.json ./

RUN npm install

COPY . .

RUN npm run build

FROM nginx:alpine as production-stage

COPY --from=build-stage /app/dist /usr/share/nginx/html

# ADD THIS LINE: Copy your custom Nginx configuration
# Ensure the path './nginx.conf' is correct relative to where your Dockerfile is.
COPY nginx.conf ./frontend/nginx.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]