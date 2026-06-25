# Estágio 1: Compilação da aplicação Angular
FROM node:20-alpine AS build
WORKDIR /app

# Copia dependências do package.json e instala
COPY package*.json ./
RUN npm ci

# Copia código fonte e compila para produção
COPY . .
RUN npm run build -- --configuration production

# Estágio 2: Servidor Web Nginx para servir os arquivos estáticos
FROM nginx:alpine

# Copia os arquivos compilados do Angular (pasta dist/frontend/browser)
COPY --from=build /app/dist/frontend/browser /usr/share/nginx/html

# Copia a configuração customizada do Nginx
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expõe a porta HTTP padrão
EXPOSE 80

# Inicia o Nginx
CMD ["nginx", "-g", "daemon off;"]
