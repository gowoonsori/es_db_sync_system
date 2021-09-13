# Getting Start

## 1. application.yml 에 db 정보 추가
local, saramin db 접속을 위해 dataSource 정보 추가

## 2. elasticsearch 시작
9200포트로 elasticsearch 7.13버전 실행.

해당 Java HighClient 라이브러리가 7.13버전으로 구현되어있음.

## 3. 프로젝트 빌드 & 실행

```shell
#프로젝트 경로에서
./gradlew.bat build
java -jar build/libs/*.jar
```


<br>

# API 명세
## 컨텐츠 검색
### Request
#### Header
|option |value|
|:---:|:---:|
|uri|/index/search/contents|
|method|post|
|headers|'Content-Type:application/json;charset=utf-8'|
#### body
|데이터 항목 | 변수 이름 | 타입 | 필수 | 비고 |
|:---:|:---:|:---:|:---:|:---:|
|서비스 아이디|service_id|String|필수|Admin에서 등록한 서비스 ID값|
|검색 단어|search_texts|String|옵션|검색 할 단어 리스트 (String)|
|결과 조회 컬럼|result_columns|Array|옵션|결과 조회 칼럼 리스트(String)|
|하이라이트|highlight|Object|옵션|하이라이터로 지정 정보|
|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;하이라이트 칼럼리스트|columns|Array|옵션|하이라이트로 지정할 칼럼 정보|
|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;하이라이트 칼럼명|column|String|옵션|하이라이트 칼럼명|
|하이라이트 Prefix|prerfix_tag|String|	 옵션	|하이라이트 Prefix|
|하이라이트 Postfix|postfix_tag|String|	 옵션	|하이라이트 Postfix|
|페이지 사이즈|page_size|Int|옵션|Default:10|
|페이지 번호|page_no|Int|옵션|Default:0|

#### example
```json
{
  "service_id": "{{SERVICE_ID}}",
  "search_text": "채용",
  "reslut_columns": [
    "title",
    "content",
    "read_cnt",
    "reg_dt",
    "display_dt"
  ],
  "highlight": {
    "columns": [
      {
        "column": "title"
      },
      {
        "column": "content"
      }
    ],
    "prerfix_tag": "<b>",
    "postfix_tag": "</b>"
  },
  "page_size": 10,
  "page_no": 1
}
```

### Response
#### header
- 200
    - body

      | 데이터 항목 | 변수이름 | 타입 | 필수 | 비고 |
          |:---:|:---:|:---:|:---:|:---:|
      |전체 건수 | total |int |필수 |검색 결과 전체 건수|
      |페이지 |page|Object|옵션||
      |페이지 사이즈 | page_size| int|옵션||
      |결과|result |array|필수||
      |결과 조회 컬럼들 | - | - | 필수 | 요청 전문의 검색 결과 조회 컬럼에 지정된 컬럼 정보
      |하이라이트 결과|highlight | Object | 필수| |
      |하이라이트 결과 컬럼들 | - | array | 필수 | 요청 전문의 하이라이트 조회 컬럼에 지정된 컬럼 정보(하이라이트 결과 없을 시 비어있음)|
- 204 : 데이터 없는 경우
- 400 : bad request
- 404 : not found

#### example
```json
{
    "total": 38,
    "page_size": null,
    "page": null,
    "result": [
        {
            "data": {
                "updated_at": "2021-09-08T16:47:02.000Z",
                "name": "Kenya AA",
                "description": "향이 강하고 묵직한 바디감. 와인향과 과일같은 상큼한 신맛이 특징인 프리미엄 커피로 독특한 쌉살한 맛이 일품이다.",
                "created_at": "2021-09-08T16:47:02.000Z",
                "id": "1"
            },
            "highlight": {}
        },
        {
            "data": {
                "updated_at": null,
                "name": "Indonesia Gayo Mountain",
                "description": "초콜릿 같은 진한향과 쌉사름한 신맛뒤에 오는 단맛의 세가지 풍미를 조화롭게 맛볼 수 있어 남성들에 잘 어울리는 커피입니다.",
                "created_at": "2021-09-10T17:08:45.000Z",
                "id": "4"
            },
            "highlight": {}
        },
        {
            "data": {
                "updated_at": null,
                "name": "Colombia Supremo",
                "description": "콜롬비아 지역의 최고급 등급의 커피로 진한 초콜릿 향과 단맛이 특징이다.",
                "created_at": "2021-09-10T17:08:45.000Z",
                "id": "5"
            },
            "highlight": {}
        },
        {
            "data": {
                "updated_at": null,
                "name": "Guatemala Marcos",
                "description": "신맛보다는 단맛과 끈적한 오일의 느낌이 조화를 이루어 풍부한 바디를 갖게하는 커피이다.",
                "created_at": "2021-09-10T17:12:17.000Z",
                "id": "6"
            },
            "highlight": {}
        },
        {
            "data": {
                "updated_at": null,
                "name": "Colombia Medellin",
                "description": "풍부한 향미와 산도를 가지고 있으며 생두의 크기는 큰편이며 달콤한 향기와 신맛과 묵직한 바디감을 가지고 있다.",
                "created_at": "2021-09-10T17:08:45.000Z",
                "id": "7"
            },
            "highlight": {}
        },
        {
            "data": {
                "updated_at": null,
                "name": "Guatemala Atitlan",
                "description": "화산지대에서 재배되는 커피로 강한 신맛이 특징이고 아로마가 강하고 바디가 중후한 커피이다.",
                "created_at": "2021-09-10T17:12:17.000Z",
                "id": "8"
            },
            "highlight": {}
        },
        {
            "data": {
                "updated_at": null,
                "name": "Costa rica Tarrazu",
                "description": "!!상큼하면서도 톡 쏘는 신맛과 풍부한 바디감, 아로마 향을 느낄 수 있다.",
                "created_at": "2021-09-10T18:06:28.000Z",
                "id": "9"
            },
            "highlight": {}
        },
        {
            "data": {
                "updated_at": null,
                "name": "Costa rica Tres Rios",
                "description": "남성적인 향미를 가진 프랑스 보르도 지역의 와인맛과 비슷해 코스타리카 보르도라고도 불리며 상큼한 신맛이 강하고 균형잡힌 바디감과 풍부한 아로마향을 가진다.",
                "created_at": "2021-09-10T17:25:43.000Z",
                "id": "10"
            },
            "highlight": {}
        },
        {
            "data": {
                "updated_at": null,
                "name": "Ethiopia Harrar",
                "description": "에티오피아를 대표하는 전통적인 커피로 맑은 홍차와 같은 아로마를 가지고 있고 풍부한 바디와 중간정도의 산도, 초콜릿 향미를 느낄 수 있다.",
                "created_at": "2021-09-10T17:25:43.000Z",
                "id": "14"
            },
            "highlight": {}
        },
        {
            "data": {
                "updated_at": null,
                "name": "Ethiopia Sidamo",
                "description": "커피의 귀부인으로 불리는 커피로 카페인이 거의 없어 저녁에도 마시기 부담이 없다. 부드러운 신맛, 단맛, 꽃향기가 가득해 향미가 풍부한 커피이다.",
                "created_at": "2021-09-10T17:25:43.000Z",
                "id": "17"
            },
            "highlight": {}
        }
    ]
}
```

## 컨텐츠 bulk
### Request
#### Header
|option |value|
|:---:|:---:|
|uri|/index/bulk|
|method|post|
|headers|'Content-Type:application/json;charset=utf-8'|

#### body
|데이터 항목 | 변수 이름 | 타입 | 필수 | 비고 |
|:---:|:---:|:---:|:---:|:---:|
|서비스 아이디|service_id|String|필수|Admin에서 등록한 서비스 ID값|
|인덱스 이름|index_name|String|필수|생성할 인덱스 이름|

#### example
```json
{
  "service_id": "{{SERVICE_ID}}",
  "index_name": "test_index",
}
```

### Response
#### header
- 200 : OK
    - body : N/A
- 400 : bad request
- 404 : not found


## 컨텐츠 부분 색인
### Request
#### Header
|option |value|
|:---:|:---:|
|uri|/index/search/indexing|
|method|post/put/delete|
|headers|'Content-Type:application/json;charset=utf-8'|

#### body
|데이터 항목 | 변수 이름 | 타입 | 필수 | 비고 |
|:---:|:---:|:---:|:---:|:---:|
|서비스 아이디들|service_ids|String[]|필수|Admin에서 등록한 서비스 ID값|
|컨텐츠 id 값|contents_id_value|String|필수|인덱싱할 데이터 id(db의 pk<->es _id)|

#### example
```json
{
  "service_id": "{{SERVICE_ID}}",
  "index_name": "35",
}
```

### Response
#### header
- 200 : OK
    - body : N/A
- 400 : bad request
- 404 : not found

## 서비스 생성
### Request
#### Header
|option |value|
|:---:|:---:|
|uri|/service|
|method|post|
|headers|'Content-Type:application/json;charset=utf-8'|

#### body
|데이터 항목 | 변수 이름 | 타입 | 필수 | 비고 |
|:---:|:---:|:---:|:---:|:---:|
|서비스 아이디|service_id|String|필수|등록할 서비스 ID값|
|SQL 문|sql_string|String|필수|인덱싱할 데이터 조회를 위한 SQL문(반드시 pk를 포함해야하며 단일 테이블만 조회가능)|

#### example
```json
{
  "service_id": "{{SERVICE_ID}}",
  "sql_string" : "select * from members"
}
```

### Response
#### header
- 200 : OK
    - body : N/A
- 400 : bad request
- 404 : not found

## 서비스 삭제
### Request
#### Header
|option |value|
|:---:|:---:|
|uri|/service/{serviceId}|
|method|delete|
|headers|'Content-Type:application/json;charset=utf-8'|

#### queryString
|데이터 항목 |  타입 | 필수 | 비고 |
|:---:|:---:|:---:|:---:|:---:|
|서비스 아이디|String|필수|삭제할 서비스 ID값|


### Response
#### header
- 200 : OK
    - body : N/A
- 400 : bad request
- 404 : not found
