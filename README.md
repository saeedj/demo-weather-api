# demo-weather-api
A project to fetch weather data from open weather api (http://openweathermap.org/current#name) for limited number of time based on the given API KEY

API KEY strategy: 
```
PLAN_FREE     : max of 1 request per 60min, To use this apiKey should start with anything except bellow ones
PLAN_BASIC_ONE: max of 5 requests per 60min, To use this apiKey should start with b1-
PLAN_BASIC_TWO: max of 10 requests per 90min, To use this apiKey should start with b2-
PLAN_PRO_ONE  : max of 100 requests per 60min, To use this apiKey should start with p1-
PLAN_PRO_TWO  : max of 200 requests per 90min; To use this apiKey should start with p2-


**Note: if no apiKey passed, the PLAN_BASIC_ONE will be used as default**
```
 
## Prerequisites

To run this app, you need: 

- JDK 11+
- Gradle configured

### Running the app locally

To run the app locally run the bootRun gradle task

```
./gradlew bootRun
```

### Running tests locally

To run the test locally run the test gradle task

```
./gradlew test
```

### API definition 

To see the api definition navigate to openAPI URL after running the app

```
http://localhost:8090/weather/
```



