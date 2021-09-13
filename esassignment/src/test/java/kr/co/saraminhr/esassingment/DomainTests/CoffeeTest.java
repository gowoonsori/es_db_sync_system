package kr.co.saraminhr.esassingment.DomainTests;

import kr.co.saraminhr.esassingment.Domains.Coffee;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class CoffeeTest {
    @Test
    @DisplayName("Coffee 모델 toString 테스트")
    public void toStringTest(){
        //given
        int id = 1;
        String name = "brazil";
        String description ="this is brazil coffee";
        LocalDateTime createdAt = null;
        LocalDateTime updatedAt = null;

        //when
        Coffee coffee = new Coffee(id,name,description,createdAt,updatedAt);

        //then
        Assertions.assertEquals("{\"id\":" +  id
                        +",\"name\":\"" + name
                        +"\",\"description\":\""+description
                        +"\",\"created_at\":"+ createdAt
                        +",\"updated_at\":"+ updatedAt
                        +"}"
                ,coffee.toString());
    }
}
