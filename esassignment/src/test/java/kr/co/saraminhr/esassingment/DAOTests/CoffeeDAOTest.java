package kr.co.saraminhr.esassingment.DAOTests;

import kr.co.saraminhr.esassingment.Configs.LocalDataSourceConfig;
import kr.co.saraminhr.esassingment.Configs.TransactionConfig;
import kr.co.saraminhr.esassingment.Daos.CoffeeDAO;
import kr.co.saraminhr.esassingment.Domains.Coffee;
import kr.co.saraminhr.esassingment.Exceptions.BadRequestException;
import kr.co.saraminhr.esassingment.Exceptions.DuplicateServiceIdException;
import kr.co.saraminhr.esassingment.Utils.ErrorMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(classes = {CoffeeDAO.class ,LocalDataSourceConfig.class}, properties = "classpath:application.yml")
public class CoffeeDAOTest {
    @Autowired
    CoffeeDAO coffeeDAO;

    @Test
    @DisplayName("커피 생성 테스트")
    public void createTest(){
        //given
        Coffee coffee= Coffee.builder()
                .name("test")
                .description("test coffee")
                .build();

        //when
        int result = coffeeDAO.insert(coffee);

        //then
        assertEquals(1,result);
    }
    @Test
    @DisplayName("커피 생성 실패 테스트 : 중복 service Id")
    public void createFailTest(){
        //given
        Coffee coffee= Coffee.builder()
                .name("test")
                .description("test coffee")
                .build();
        createCoffee(coffee);

        //when
        Exception exception = assertThrows(DuplicateServiceIdException.class, ()->{
            coffeeDAO.insert(coffee);
        });

        //then
        assertEquals(ErrorMessage.DUPLICATE_SERVICE_ID.getMessage(),
                exception.getMessage());
    }

    @Test
    @DisplayName("커피 생성 실패 테스트 : 잘못된 parameter")
    public void createFailTest2(){
        //given
        Coffee coffee= Coffee.builder()
                .name("test")
                .description("test coffeetest coffeetest coffeetest coffeetest coffeetest coffeetest coffeetest coffeetest coffeetest coffeetest coffeetest coffeetest coffeetest coffeetest coffeetest coffeetest coffeetest coffeetest coffeetest coffeetest coffeetest coffeetest coffeetest coffeetest coffeetest coffeetest coffeetest coffeetest coffeetest coffeetest coffeetest coffeetest coffee")
                .build();

        //when
        Exception exception = assertThrows(DuplicateServiceIdException.class, ()->{
            coffeeDAO.insert(coffee);
        });

        //then
        assertEquals(ErrorMessage.BAD_REQUEST.getMessage(),
                exception.getMessage());
    }

    @Test
    @DisplayName("find All test")
    public void findAllTest(){
        //given
        String name = "test";
        String description = "test coffee";
        createCoffee(Coffee.builder()
                .name(name)
                .description(description)
                .build());

        //when
        List<Coffee> coffeeList = coffeeDAO.findAll();

        //then
        assertEquals(name,coffeeList.get(coffeeList.size()-1).getName());
        assertEquals(description,coffeeList.get(coffeeList.size()-1).getDescription());
    }

    @Test
    @DisplayName("모든 커피들 페이징 조회 테스트")
    public void findAllPaging(){
        //given
        Pageable pageable = PageRequest.of(0,10);

        //when
        Page<Coffee> coffees = coffeeDAO.findAll(pageable);

        //then
        assertEquals(10,coffees.getSize());
        assertEquals(0,coffees.getNumber());
    }

    @Test
    @DisplayName("sql문으로 local table 접근 테스트")
    public void findBySQL(){
        //given
        String sql = "select * from coffee";

        //when
        List<Map<String,Object>> dataList = coffeeDAO.findBySQL(sql);

        //then
        for(Map<String,Object> data : dataList){
            assertEquals(String.class, data.get("name").getClass());
            assertEquals(String.class, data.get("description").getClass());
        }
    }

    @Test
    @DisplayName("sql문으로 local table 접근 테스트 : 실패 유효하지 않은 sql")
    public void findBySQLFail(){
        //given
        String sql = "select * from test-table";

        //when
        Exception exception = assertThrows(BadRequestException.class, ()->{
            coffeeDAO.findBySQL(sql);
        });

        //then
        assertEquals(ErrorMessage.INVALID_SQL.getMessage(),exception.getMessage());
    }

    @Test
    @DisplayName("find by id test")
    public void findById(){
        //given
        String id = "1";

        //when
        Map<String,Object> data = coffeeDAO.findById(id);

        //then
        assertEquals("Kenya AA",data.get("name"));
    }


    private void createCoffee(Coffee coffee){
        coffeeDAO.insert(coffee);
    }
}
