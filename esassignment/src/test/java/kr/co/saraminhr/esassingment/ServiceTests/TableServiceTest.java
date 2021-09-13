package kr.co.saraminhr.esassingment.ServiceTests;

import kr.co.saraminhr.esassingment.Services.TableService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class TableServiceTest {
    @Autowired
    TableService tableService;

    @Test
    @DisplayName("모든 테이블 조회")
    public void getAllTables(){
        //when
        List<String> tableList = tableService.getAllTables();

        //then
        assertTrue(tableList.contains("members"));
    }

    @Test
    @DisplayName("모든 테이블 페이징 조회")
    public void getTables(){
        //given
        Pageable pageable = PageRequest.of(0,10);

        //when
        Page<String> tableList = tableService.getTables(pageable);

        //then
        assertEquals(10, tableList.getSize());
        assertEquals(0, tableList.getNumber());
        assertTrue( tableList.getContent().contains("0420_payment"));
    }
}
