SQL Scheduler
-

RDBMS 접속정보와 SQL쿼리, 스케줄(cron 형식)을 등록하면 해당 스케줄 대로 쿼리가 실행되고 성공여부와 결과를 로그로 기록합니다.  
로그는 MongoDB에, 웹 애플리케이션 관리 데이터는 Embedded DB(SQLite3)에 기록합니다.

#### Environment
* JDK 8
* SpringBoot 2.0
* Gradle 4.1
* Quartz
* Vue.js 2.1
* MongoDB 3.6

#### Installation

1. application.properties 파일 내 아래 내용을 설정  
spring.mail.host=`SMTP 서버 IP`  
spring.mail.port=`SMTP 서버 Port`  
spring.mail.username=`SMTP 서버 접속 ID`  
spring.mail.password=`SMTP 서버 접속 Password`  
system.super.admin.mail=`수퍼 관리자 메일`  
system.super.admin.deleteTargetDataLogCron=`데이터 삭제 쿼리를 수행 시 삭제 데이터를 일시 백업 후 삭제하는데 어느 시각에 삭제할 것인지(cron 형식)`  
system.super.admin.deleteTargetDataBeforeDay=`삭제 데이터 최대 백업 일수`  
system.super.admin.dataBackupMaxRows=`삭제 데이터 최대 보관 Row수(설정된 Row수를 초과하면 백업을 하지 않고 삭제함)`  
spring.data.mongodb.database=task=`로그를 적재할 MongoDB Instance명`
2. `gradle build` 명령어로 빌드하여 war 파일 생성
3. `java -jar scheduler-0.0.1.war` 명령어로 Deploy
4. 웹브라우저에서 http://127.0.0.1:9001 로 접속