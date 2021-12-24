# Elasticsearch 와 Mysql 간의 데이터 싱크

## 요구사항 기능
1. 검색엔진의 api 제공
  - 인덱스 생성
  - 인덱스 조회
  - 인덱스의 document 검색
  - 인덱스의 document 수정 / 삭제 / 삽입

2. 검색엔진 인덱스 생성시 db table을 선택하는 것이 아닌 사용자가 쿼리문을 지정
3. Database와의 데이터 싱크


## 구현 내용
1. TDD로 개발 진행할 것
1. 검색엔진 api
  - 인덱스 생성 / 조회 / 수정 / 삭제 / 검색
  - document 생성 / 수정 /삭제 / 조회 / 검색
2. 검색엔진 인덱스 생성시 쿼리문으로 인덱스 생성
3. Database와 데이터 싱크
  - logstash 등 다양한 대안이 존재
  - api를 만든 만큼 kafka와 debizium의 mysqlconnect driver를 이용하여 변경감지를 통한 싱크로 구현
