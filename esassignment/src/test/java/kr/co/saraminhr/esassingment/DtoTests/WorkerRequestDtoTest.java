package kr.co.saraminhr.esassingment.DtoTests;

import kr.co.saraminhr.esassingment.Dtos.WorkerRequestDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WorkerRequestDtoTest {
    @Test
    @DisplayName("WorkerRequestDto toString 테스트")
    public void toStringTest(){
        //given
        String uuid = "test";
        String sqlString = "select * from test";


        //when
        WorkerRequestDto request = new WorkerRequestDto(uuid,sqlString);

        //then
        Assertions.assertEquals("{\"service_id\":\"" + uuid
                        +"\",\"sql_string\":\"" + sqlString
                        +"\"}"
                ,request.toString());
    }

    @Test
    @DisplayName("pk 존재하는 지 검사 테스트 : *")
    public void existPkSuccess1(){
        //given
        String uuid = "test";
        String sqlString = "select * from test";
        String pkName = "id";
        WorkerRequestDto workerRequestDto = new WorkerRequestDto(uuid,sqlString);

        //when
        boolean result = workerRequestDto.isExistPk(pkName);

        //then
        assertTrue(result);
    }

    @Test
    @DisplayName("pk 존재하는 지 검사 테스트 : pk exist")
    public void existPkSuccess2(){
        //given
        String uuid = "test";
        String sqlString = "select id,passwd, content, created_dt from test";
        String pkName = "id";
        WorkerRequestDto workerRequestDto = new WorkerRequestDto(uuid,sqlString);

        //when
        boolean result = workerRequestDto.isExistPk(pkName);

        //then
        assertTrue(result);
    }

    @Test
    @DisplayName("pk 존재하는 지 검사 테스트 : * and column")
    public void existPkSuccess3(){
        //given
        String uuid = "test";
        String sqlString = "select id, * from test";
        String pkName = "id";
        WorkerRequestDto workerRequestDto = new WorkerRequestDto(uuid,sqlString);

        //when
        boolean result = workerRequestDto.isExistPk(pkName);

        //then
        assertTrue(result);
    }

    @Test
    @DisplayName("pk 존재하는 지 검사 테스트 : 존재하지 않은 경우")
    public void existPkFail(){
        //given
        String uuid = "test";
        String sqlString = "select passwd, content from test";
        String pkName = "id";
        WorkerRequestDto workerRequestDto = new WorkerRequestDto(uuid,sqlString);

        //when
        boolean result = workerRequestDto.isExistPk(pkName);

        //then
        assertFalse(result);
    }
}
