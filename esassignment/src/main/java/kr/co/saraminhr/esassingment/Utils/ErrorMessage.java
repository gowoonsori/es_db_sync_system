package kr.co.saraminhr.esassingment.Utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public enum ErrorMessage {
    //validation
    NOT_BLANK("V001", "공백은 입력이 불가능합니다."),
    DUPLICATE_SERVICE_ID("V002", "중복된 service id입니다."),
    NOT_EXIST_SERVICE_ID("C003","존재하지 않은 service id 입니다. "),
    OVER_PAGE("V004","유효하지 않은 페이징 요청입니다."),
    INVALID_SQL("V005", "유효하지 않은 SQL문 입니다."),
    INVALID_DATETIME("V006", "유효하지 않은 DATETIME 입니다."),
    NOT_EXIST_TABLE_NAME("V007", "존재하지 않는 테이블입니다."),
    NOT_EXIST_PK("V008", "PK가 존재하지 않습니다."),

    //common
    BAD_REQUEST("C001","잘못된 요청입니다."),
    NOT_FOUND("C002","찾을 수 없습니다."),
    SERVICE_UNAVAILABLE("C003","이용 불가능한 서비스입니다."),
    INTERNAL_SERVER("C004","요청 처리 중 에러가 발생하였습니다."),
    SELECT_ERROR("C005","조회중 에러가 발생하였습니다."),
    INSERT_ERROR("C006","삽입중 에러가 발생하였습니다."),
    DELETE_ERROR("C007","삭제중 에러가 발생하였습니다."),
    UPDATE_ERROR("C008","수정중 에러가 발생하였습니다."),

    //Elastic
    DUPLICATE_INDEX_NAME("E001","중복된 인덱스명입니다."),
    NOT_EXIST_INDEX("E002","인덱스가 존재하지 않습니다."),
    NOT_EXIST_DOCUMENT("E003","문서가 존재하지 않습니다."),
    INVALID_INDEX_NAME("E004","유효하지 않은 인덱스명입니다.")
    ;

    private final String code;
    private final String message;
}
