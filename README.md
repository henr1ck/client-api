# Client-API | Spring Boot-Security 🔒🍃
## 1. Breve introdução
Uma API de CRUD simples de clientes, com implementação de autenticação/autorização via JWT. Desenvolvida apenas para fins de aprendizado e portfólio.

## 2. Status do projeto
Em andamento... 🚧

## 3. Funcionalidades
- [x] CRUD de Clientes;
- [x] Autenticação utilizando token de portador;
- [X] Autorização baseada em roles;
- [X] Reinvidicação de tokens (Refresh-Token);
- [X] Exigência de role específica para requisições que manipulam o recurso;
- [X] Tratamento global de exceções e respostas de erros customizadas;

## 4. Como executar?
OBS: É necessário que o docker esteja instalado em sua máquina;

1. Clone o código do repositório para o seu computador;
2. Abra o terminal na pasta raiz do projeto;
3. Execute o seguinte comando:
```bash
$ docker compose up
```
Se preferir, crie seu próprio arquivo compose e copie o código:

```yml
version: '3.9'
services:
  db:
    image: 'mysql'
    container_name: 'client-db'
    environment:
      - 'MYSQL_ROOT_PASSWORD=RbzQg1NWrD'
    ports:
      - '3306:3306'
    deploy:
      resources:
        limits:
          cpus: '0.5'
          memory: 512M
        reservations:
          cpus: '0.2'
          memory: 128M
    volumes:
      - client_db_vol:/var/lib/mysql

  app:
    image: 'henr1ck/client-app'
    container_name: 'client-app'
    ports:
      - '8080:8080'
    depends_on:
      - db
    deploy:
      restart_policy:
        condition: on-failure
        max_attempts: 3
        delay: 5s
        window: 40s
      resources:
        limits:
          cpus: '0.5'
          memory: 512M
        reservations:
          cpus: '0.2'
          memory: 128M

volumes:
  client_db_vol:
```

## 5. Endpoints
Todas as rotas estão disponíveis no arquivo do insomnia, no diretório raiz do repositório.

Os métodos HTTP's que possuem a URI `**/admin/**` só poderão ser acessados por usuarios que possuem a role de admin. 

### `POST /signin`
Ao executar a aplicação, dois usuários são criados no banco de dados e possuem as seguintes credenciais:
```json
[
  {
    "username": "admin", 
    "password": "cheesebread", 
    "roles": ["ROLE_USER", "ROLE_ADMIN"]
  },
  {
    "username": "user", 
    "password": "cheesebread", 
    "roles": ["ROLE_USER"]
  }
]
```
Para solicitar os tokens de acesso e atualização, basta enviar uma requisição HTTP com os headers:

`Username:` `admin` ou `user`

`Password: cheesebread`

Após a requisição, dois cabeçalhos de resposta são retornados, um contendo o Access token, e outro o Refresh token. Então,
para as rotas seguintes, o `Authorization: Bearer <access-token>` deverá ser anexado.

### `POST /refresh-token`
Os tokens de acesso possuem duração de 1 hora, para fazer uma reinvidicação sem precisar fornecer as
credenciais novamente, basta enviar para esse endpoint um cabeçalho: `Authorization: Bearer <refresh-token>`.
Esse token de atualização, possui duração de 24 horas.

### `GET /api/client/{id}`
Esse endpoint retorna um cliente do banco de dados, de acordo com um ID especificado na URI:

Request: `GET /api/client/1`

Response body `200 - OK`:
```json
{
  "id": 1,
  "firstName": "Pedro Henrique",
  "lastName": "Vieira",
  "phoneNumber": "+55 86 981021180",
  "email": "pedrohenrick.dev@gmail.com",
  "birthDate": "2002-03-27T12:00:00"
}
```
Caso o ID não seja especificado, uma lista de clientes é retornada do banco. 

### `POST /api/admin/client/`
Esse endpoint recebe um cliente no corpo da requisição, salva e o retorna com um ID gerado. 

Request body:
```json
{
  "firstName": "Yedda",
  "lastName": "Santos",
  "birthDate": "2001-08-06T12:00:00",
  "phoneNumber": "+55 86 981021181",
  "email": "yeddasantos0806@gmail.com"
}
```

Response body `201 - CREATED`:
```json
{
  "id": 2,
  "firstName": "Yedda",
  "lastName": "Santos",
  "birthDate": "2001-08-06T12:00:00",
  "phoneNumber": "+55 86 981021181",
  "email": "yeddasantos0806@gmail.com"
}
```

### `PUT /api/admin/client/{id}`
Esse endpoint recebe o ID do cliente para ser atualizado com os valores do corpo da solicitação:

Request body `/api/admin/client/1`: 
```json
{
  "firstName": "Pedro Henrick",
  "lastName": "Vieira",
  "birthDate": "2001-08-06T12:34:56",
  "phoneNumber": "+55 86 98102-1180",
  "email": "pedrohenrick.dev@gmail.com"
}
```

Response: `204 - NO CONTENT`

### `DELETE /api/admin/client/{id}`
Exclui do banco de dados o cliente associado ao ID especificado na URI. 

Response: `204 - NO CONTENT`

## 6. Tecnologias/Bibliotecas
- Spring Boot 🚀
- Spring Security 🔒
- Spring DataJPA 🛢
- Bean Validation 🔎
- Java JWT 🔑
- Docker 🐳
- Lombok 🌶
