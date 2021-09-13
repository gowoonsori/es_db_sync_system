package kr.co.saraminhr.esassingment.ServiceTests;

import kr.co.saraminhr.esassingment.Daos.CoffeeDAO;
import kr.co.saraminhr.esassingment.Services.CoffeeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CoffeeServiceTest {
    @InjectMocks
    CoffeeService coffeeService;

    @Mock
    CoffeeDAO coffeeDAO;

    @Test
    @DisplayName("coffee 테이블에서 id로 데이터 조회")
    public void getData(){
        //given
        Map<String,Object> data = Map.of("id",1,"name","test","description","test-data");
        String id = "1";
        when(coffeeDAO.findById(id)).thenReturn(data);

        //when
        Map<String,Object> result = coffeeService.getData(id);

        //then
        assertAll(
                ()->assertEquals(data.get("id"),result.get("id")),
                ()->assertEquals(data.get("name"),result.get("name")),
                ()->assertEquals(data.get("description"),result.get("description")));
    }
}
