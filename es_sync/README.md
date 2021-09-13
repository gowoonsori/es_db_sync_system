# Getting Start
## 1. kafka 실행 및 설정 / Connector 생성
zookeeper(2181) 와 kafka(9202) 실행 시키고 debezium을 kafka connect plugin에 등록시킨 후 아래와 같이 connector 생성

```shell
curl --location --request POST 'http://localhost:8083/connectors' \
--header 'Content-Type: application/json' \
--data-raw '{
"name" : "es-sync",
"config":{
"connector.class": "io.debezium.connector.mysql.MySqlConnector",
"database.hostname" : "localhost",
"database.port" : "3306",
"database.user" : "kafka",
"database.password" : "password",
"database.server.id" : "1",
"database.server.name" : "es-sync",
"database.history.kafka.bootstrap.servers" : "localhost:9092",
"database.history.kafka.topic" : "es-sync.data",
"include.shcema.changes": "true",
"databse.whitelist" : "es_assignment",
"database.serverTiemzone" : "Asia/Seoul"
}
}'
```

## 2. db 설정
application.yml 파일에 dataSource정보 등록

## 3. 빌드 및 실행
```shell
#프로젝트 경로에서
./gradlew.bat build
java -jar build/libs/*.jar
```