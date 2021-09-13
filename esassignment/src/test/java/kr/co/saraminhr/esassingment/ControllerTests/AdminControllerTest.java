package kr.co.saraminhr.esassingment.ControllerTests;

import kr.co.saraminhr.esassingment.Commons.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class AdminControllerTest extends BaseTest {

    @Test
    @DisplayName("홈 view 테스트")
    public void homeViewTest() throws Exception {
        this.mockMvc.perform(
                        get("/"))
                .andExpect(view().name("home"));
    }

    @Test
    @DisplayName("사전 조회 view 테스트")
    public void documentsViewTest() throws Exception {
        this.mockMvc.perform(
                        get("/dictionary"))
                .andExpect(model().attributeExists("indexList"))
                .andExpect(view().name("dictionary"));
    }

    @Test
    @DisplayName("테이블 조회 view 테스트")
    public void tablesViewTest() throws Exception {
        this.mockMvc.perform(
                        get("/table"))
                .andExpect(model().attributeExists("tableList"))
                .andExpect(view().name("table"));
    }

    @Test
    @DisplayName("검색 view 테스트")
    public void searchViewTest() throws Exception {
        this.mockMvc.perform(
                        get("/search"))
                .andExpect(view().name("search"));
    }
}
