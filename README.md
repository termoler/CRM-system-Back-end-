# CRM-system-Backend

**CRM-система** - это система, которая управляет информацией о продавцах и их
транзакциях. Система включает возможности для создания, чтения, обновления и
удаления данных о продавцах и транзакциях. Система также имеет функции аналитики
для обработки и анализа данных.

## Содержание
- [Зависимости](#зависимости)
- [Сборка и запуск](#Сборка-и-запуск)
- [Сущности](#сущности)
- [Аналитика](#аналитика)
- [Методы REST](#методы-REST)
- [Примеры запросов и ответов](#примеры-запросов-и-ответов)

## Зависимости
В проекте используются следующие зависимости:

- **ModelMapper (org.modelmapper:modelmapper:2.3.0):** Библиотека для упрощения преобразования объектов между различными слоями приложения.

- **Spring Boot Starter Validation (org.springframework.boot:spring-boot-starter-validation):** Поддержка валидации данных в Spring Boot приложениях.

- **Spring Boot Starter Data JPA (org.springframework.boot:spring-boot-starter-data-jpa):** Упрощает работу с базами данных с использованием JPA (Java Persistence API).

- **Lombok (org.projectlombok:lombok):** Упрощает написание кода, автоматически генерируя геттеры, сеттеры и другие методы. Используется только во время компиляции.

- **PostgreSQL (org.postgresql:postgresql):** Драйвер для подключения к базе данных PostgreSQL, используется во время выполнения.

- **Spring Boot Starter Test (org.springframework.boot:spring-boot-starter-test):** Библиотека для тестирования Spring Boot приложений, включая JUnit и другие инструменты.

- **JUnit Platform Launcher (org.junit.platform:junit-platform-launcher):** Позволяет запускать тесты, написанные с использованием JUnit.


## Сборка и запуск
### Требования
Перед тем как начать, убедитесь, что у вас установлены следующие технологии:
- Java JDK 23
- Gradle 6.0 или выше
- PostgreSQL 16.3

### Сборка проекта
Клонируйте репозиторий:
```bash
git clone https://github.com/username/repository.git
cd repository
```
### Конфигурация базы данных
При использовании PostgreSQL, необходимо настроить подключение в файле application.properties, это необходимо сделать, иначе проект не запустится:
```properties
spring.application.name=Crm
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.url=jdbc:postgresql://localhost:5432/your_database
spring.datasource.username=your_username
spring.datasource.password=your_password

spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.show_sql=true

spring.mvc.hiddenmethod.filter.enabled=true
```

### Запуск проекта
Соберите проект с помощью Gradle:
```bash
./gradlew build
```
Запустите проект с помощью следующей команды:
```bash
./gradlew bootRun
```

## Сущности:
1. **Продавец (Seller)**:
   - **ID (id):** уникальный идентификатор продавца (целое число, автоинкремент).
   - **Имя (name):** имя продавца (строка).
   - **Контактные данные (contactInfo):** контактная информация продавца (строка).
   - **Дата регистрации (registrationDate):** дата и время регистрации продавца в системе (тип LocalDateTime).
2. **Транзакция (Transaction)**:
   - **ID (id):** уникальный идентификатор транзакции (целое число, автоинкремент).
   - **Продавец (seller):** ссылка на продавца, к которому относится транзакция (внешний
   ключ на сущность "Продавец").
   - **Сумма (amount):** сумма транзакции (десятичное число).
   - **Тип оплаты (paymentType):** тип оплаты (CASH, CARD, TRANSFER) (строка).
   - **Дата транзакции (transactionDate):** дата и время совершения транзакции (тип
   LocalDateTime).

## Аналитика
### **1. Функция для получения самого продуктивного продавца**
#### Описание
Данная функция предназначена для получения самого продуктивного продавца
за указанный период времени (день, месяц, квартал или год). 
Продуктивность определяется как сумма всех транзакций, 
совершенных продавцом в заданный период. 
Функция возвращает список продавцов, которые имеют наибольшую(одинаковую) сумму транзакций.

#### Метод:
public List<Seller> getBestSellerForPeriod(String period, LocalDateTime startDate, LocalDateTime endDate)

##### Параметры:
- String period: Период времени для анализа. Может принимать следующие значения:
  - "day": анализ за один день.
  - "month": анализ за один месяц.
  - "year": анализ за один год.
  - "quarter": анализ за квартал.
  - "specifiedDates": анализ за указанный диапазон дат.
- LocalDateTime startDate: Дата и время начала периода анализа.
- LocalDateTime endDate: Дата и время окончания периода анализа (для периодов, отличных от дня, месяца и года).

##### Проверки:
- Проверка параметров period и startDate на наличие значения (не равны null).
- Если параметр endDate равен null, то он устанавливается равным startDate, увеличенному на единицу соответствующего периода.
- Выполняется проверка соответствия разницы между заданными датами и указанным периодом. Разница должна соответствовать заданному периоду с точностью до единицы данного периода.
- В случае, если результирующий список, полученный в результате выполнения запроса, оказывается пустым, выбрасывается исключение.

Все ошибки обрабатываются и возвращаются клиенту в формате JSON.
##### Внутренние операции:
Функция использует Hibernate для выполнения запросов к базе данных, в зависимости от указанного периода. Запросы выбирают продавцов и суммируют их транзакции, сортируя результаты по убыванию суммы транзакций.

1. **period = "year":**
```hql
select t.seller, sum(t.amount) as sumAmount from Transaction t 
where year(t.transactionDate) = :year 
group by t.seller
order by sumAmount desc
```

2. **period = "month":**
```hql
select t.seller, sum(t.amount) as sumAmount from Transaction t 
where month(t.transactionDate) = :month 
and year(t.transactionDate) = :year 
group by t.seller
order by sumAmount desc
```

3. **period = "day":**
```hql
select s from Seller s 
join s.transactionList t 
where t.transactionDate between :startDate and :endDate 
group by s.id 
having sum (t.amount) < :amount
```

4. **period = "quarter" or period = "specifiedDates":**
```hql
select t.seller, sum(t.amount) as sumAmount from Transaction t 
where t.transactionDate >= :startDate and t.transactionDate <= :endDate 
group by t.seller 
order by sumAmount desc
```
##### Возвращаемое значение:
- List<Seller>: Список продавцов, которые имеют наибольшую сумму транзакций за указанный период.

#### Заключение:
Функция getBestSellerForPeriod является важным инструментом для анализа продуктивности продавцов в CRM-системе.
Она позволяет быстро получать информацию о самых успешных продавцах за различные временные промежутки, что может быть полезно для принятия бизнес-решений и планирования.



### **2. Функция получения списка продавцов с суммой всех транзакций за выбранный период, меньше переданного параметра суммы**
#### Описание
Данная функция предназначена для получения списка продавцов, 
чьи суммарные транзакции за указанный период времени (между startDate и endDate) ниже заданного значения amount.
Это может быть полезно для анализа производительности продавцов и выявления тех,
кто не достиг определенных финансовых показателей.

#### Метод:
public List<Seller> getSellersBelowAmountForPeriod(double amount, LocalDateTime startDate, LocalDateTime endDate)

##### Параметры:
- double amount: Значение, ниже которого необходимо получить сумму транзакций продавцов.
- LocalDateTime startDate: Дата и время начала периода анализа.
- LocalDateTime endDate: Дата и время окончания периода анализа.

##### Проверки:
- Проверка параметров startDate и endDate на наличие значения (не равны null)
- В случае, если результирующий список, полученный в результате выполнения запроса, оказывается пустым, выбрасывается исключение.

Все ошибки обрабатываются и возвращаются клиенту в формате JSON.

##### Внутренние операции:
Функция использует Hibernate для выполнения запроса к базе данных. Запрос выбирает продавцов, у которых сумма транзакций за указанный период времени меньше заданного значения. Продавцы группируются по идентификатору, что позволяет избежать дублирования в результате.
```hql
select s from Seller s 
join s.transactionList t 
where t.transactionDate between :startDate and :endDate 
group by s.id 
having sum (t.amount) < :amount
```
##### Возвращаемое значение:
List<Seller>: Список продавцов, чьи суммарные транзакции за указанный период ниже заданного значения amount. Если таких продавцов нет, функция возвращает пустой список.   
#### Заключение:
Функция getSellersBelowAmountForPeriod является полезным инструментом для анализа производительности продавцов в CRM-системе. Она позволяет быстро идентифицировать продавцов, чьи транзакции не достигают заданного уровня, что может быть полезно для дальнейшего анализа и принятия мер по улучшению их результатов.

## Методы REST

1. **Request mapping: /api/sellers** 
   - **GET /getSellers:** Получить список всех продавцов.
   - **GET /getSeller/{id}** Получить информацию о конкретном продавце.
   - **GET /getTransactionBySellerId/{id}:** Получить транзакции выполеннные конкретным продавцом.
   - **GET /getBestSellerForPeriod/{period}:** Получить самого продуктивного продавца(самых продуктивных продавцов в случае если сумма всех транзакций у них одинаковая) за определенный период времени.
   - **GET /getSellersBelowAmountForPeriod/{amount}:** Получить список продавцов с суммой меньше указанной
   - **POST /createSeller:** Создать нового продавца.
   - **PATCH /updateSeller/{id}:** Обновить информацию о продавце.
   - **DELETE /deleteSeller/{id}:** Удалить продавца.

2. **Request mapping: /api/transactions**
   - **GET /getTransactions:** Получить список всех транзакций.
   - **GET /getTransaction/{id}:** Получить информацию о конкретной транзакции.
   - **POST /createTransaction:** Создать новую транзакцию.
   - **PATCH /updateTransaction/{id}:** Обновить транзакцию.
   - **DELETE /deleteTransaction/{id}:** Удалить транзакцию.

## Примеры запросов и ответов:
- **GET /getSellers:**
  - **Описание:** Получить список всех продавцов.
  - **Пример запроса:** http://localhost:8080/api/sellers/getSellers
  - **Пример ответа:**
  ```
  [
    {
        "id": 16,
        "name": "example1",
        "contact_info": "example1@example.ru"
    },
    {
        "id": 15,
        "name": "example2",
        "contact_info": "example2@example.ru"
    },
    {
        "id": 3,
        "name": "example3",
        "contact_info": "example3@example.ru"
    },
    {
        "id": 13,
        "name": "example4",
        "contact_info": "example4@example.ru"
    },
    {
        "id": 2,
        "name": "example5",
        "contact_info": "example5@example.ru"
    }
  ]
  ```
- **GET /getTransactionBySellerId/{id}:**
  - **Описание:** Получить транзакции, выполненные конкретным продавцом.
  - **Пример запроса:** http://localhost:8080/api/sellers/getTransactionBySellerId/3
  - **Пример ответа:**
  ```
  [
     {
       "amount": 2400,
       "paymentType": "CASH",
       "transactionDate": "2019-07-27T16:00:00",
       "seller": {
         "id": 3,
         "name": "example1",
         "contact_info": "example1@example.ru"
       }
     },
     {
       "amount": 2900,
       "paymentType": "TRANSFER",
       "transactionDate": "2019-08-01T10:45:00",
       "seller": {
         "id": 3,
         "name": "example2",
         "contact_info": "example2@example.ru"
       }
     }
  ]
  ```
- **POST /createSeller:**
  - **Описание:** Создать нового продавца.
  - **Пример запроса:** http://localhost:8080/api/sellers/createSeller <br/>
  ```
  {
    "name": "example1",
    "email": "example1@example.com"
  }
  ```
  - **Пример ответа:**
  ```
  OK
  ```  
- **POST /createTransaction:**
   - **Описание:** Создать нового продавца.
   - **Пример запроса:** http://localhost:8080/api/transactions/createTransaction <br/>
  ```
  {
    "amount": "100",
    "paymentType": "CASH",
    "seller": {
      "id": 1
    }
  }
  ```
   - **Пример ответа:**
  ```
  OK
  ``` 
- **GET /getSellersBelowAmountForPeriod/{amount}:**
  - **Описание:** Получить список продавцов с суммой меньше указанной
  - **Пример запроса:** http://localhost:8080/api/sellers/getSellersBelowAmountForPeriod/500?startDate=2022-10-19T07:44:03&endDate=2024-10-30T07:55:03
  - **Пример ответа:**
  ```
  [
    {
        "id": 1,
        "name": "example1",
        "contact_info": "example1@example.ru"
    }
  ]
  ```
