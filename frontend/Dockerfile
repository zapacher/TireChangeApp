FROM nginx:alpine

COPY frontend/index.html /usr/share/nginx/html/index.html
COPY frontend/styles.css /usr/share/nginx/html/styles.css
COPY frontend/script.js /usr/share/nginx/html/script.js

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]