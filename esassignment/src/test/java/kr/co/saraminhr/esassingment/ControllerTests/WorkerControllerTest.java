package kr.co.saraminhr.esassingment.ControllerTests;

import kr.co.saraminhr.esassingment.Commons.BaseTest;
import kr.co.saraminhr.esassingment.Daos.IndexDAO;
import kr.co.saraminhr.esassingment.Domains.Worker;
import kr.co.saraminhr.esassingment.Dtos.WorkerRequestDto;
import kr.co.saraminhr.esassingment.Services.WorkerService;
import kr.co.saraminhr.esassingment.Utils.ErrorMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
public class WorkerControllerTest extends BaseTest {
    @Autowired
    WorkerService workerService;
    @Autowired
    IndexDAO indexDAO;

    @Test
    @DisplayName("서비스 조회 view 테스트")
    public void servicesViewTest() throws Exception {
        this.mockMvc.perform(
                        get("/service"))
                .andExpect(model().attributeExists("serviceList"))
                .andExpect(view().name("service"));
    }

    @Test
    @DisplayName("서비스 생성 view 테스트")
    public void createServiceViewTest() throws Exception {
        this.mockMvc.perform(
                        get("/service/create"))
                .andExpect(view().name("createService"));
    }

    @Test
    @DisplayName("서비스 생성 event controller 테스트")
    public void createServiceEventTest() throws Exception{
        //given
        String serviceId = "test-service";
        String sqlString = "select * from members";
        WorkerRequestDto worker = new WorkerRequestDto(serviceId,sqlString);

        //when
        var response = this.mockMvc.perform(
                post("/service")
                        .contentType("application/json;charset=UTF-8")
                        .content(worker.toString()));

        //then
        response.andExpect(status().isOk());
    }

    @Test
    @DisplayName("서비스 생성 event controller 테스트 : *이 아닌 pk가 존재하는 경우")
    public void createServiceEventTestExistPk() throws Exception{
        //given
        String serviceId = "test-service";
        String sqlString = "select mem_idx from members";
        WorkerRequestDto worker = new WorkerRequestDto(serviceId,sqlString);

        //when
        var response = this.mockMvc.perform(
                post("/service")
                        .contentType("application/json;charset=UTF-8")
                        .content(worker.toString()));

        //then
        response.andExpect(status().isOk());
    }

    @Test
    @DisplayName("서비스 생성 event controller 테스트 : * 가 존재하는 경우")
    public void createServiceEventTestExistStar() throws Exception{
        //given
        String serviceId = "test-service";
        String sqlString = "select id, * from members";
        WorkerRequestDto worker = new WorkerRequestDto(serviceId,sqlString);

        //when
        var response = this.mockMvc.perform(
                post("/service")
                        .contentType("application/json;charset=UTF-8")
                        .content(worker.toString()));

        //then
        response.andExpect(status().isOk());
    }

    @Test
    @DisplayName("서비스 생성 event controller 실패 테스트 : 유효성 검사 실패")
    public void createServiceFailTest1() throws Exception{
        //given
        String serviceId = "test-service";
        String sqlString = "insert into members (mem_idx) values (12)";
        WorkerRequestDto worker = new WorkerRequestDto(serviceId,sqlString);

        //when
        var response = this.mockMvc.perform(
                post("/service")
                        .contentType("application/json;charset=UTF-8")
                        .content(worker.toString()));

        //then
        response.andExpect(status().isBadRequest())
                .andExpect(jsonPath("message['valid_sqlString']").value("잘못된 형식입니다."))
                .andExpect(jsonPath("status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @DisplayName("서비스 생성 event controller 실패 테스트 : 중복 service Id")
    public void createServiceFailTest2() throws Exception{
        //given
        String serviceId = "test-service";
        String sqlString = "select * from members";
        Worker worker = Worker.builder()
                .uuid(serviceId)
                .pkName("mem_idx")
                .tableName("members")
                .sqlString(sqlString)
                .build();
        workerService.insert(worker);

        //when
        WorkerRequestDto request = new WorkerRequestDto(serviceId,sqlString);
        var response = this.mockMvc.perform(
                post("/service")
                        .contentType("application/json;charset=UTF-8")
                        .content(request.toString()));

        //then
        response.andExpect(status().isBadRequest())
                .andExpect(jsonPath("message['valid_uuid']").value(ErrorMessage.DUPLICATE_SERVICE_ID.getMessage()))
                .andExpect(jsonPath("status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @DisplayName("서비스 생성 event controller 실패 테스트 : pk not exist")
    public void createServiceFailTest3() throws Exception{
        //given
        String serviceId = "test-service";
        String sqlString = "select id,passwd from members";

        //when
        WorkerRequestDto request = new WorkerRequestDto(serviceId,sqlString);
        var response = this.mockMvc.perform(
                post("/service")
                        .contentType("application/json;charset=UTF-8")
                        .content(request.toString()));

        //then
        response.andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value(ErrorMessage.NOT_EXIST_PK.getMessage()))
                .andExpect(jsonPath("status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @DisplayName("서비스 삭제 event controller 성공 테스트")
    public void deleteServiceSuccessTest() throws Exception{
        //given
        String serviceId = "test-service";
        String sqlString = "select * from members";
        Worker worker = Worker.builder()
                .uuid(serviceId)
                .pkName("mem_idx")
                .tableName("members")
                .sqlString(sqlString)
                .build();
        workerService.insert(worker);

        //when
        var response = this.mockMvc.perform(
                delete("/service/" + serviceId)
                        .contentType("application/json;charset=UTF-8"));

        //then
        response.andExpect(status().isOk());
        assertTrue(workerService.getWorker(serviceId).isEmpty());
    }

    @Test
    @DisplayName("서비스 삭제 event controller 실패 테스트 : 없는 서비스")
    public void deleteServiceFailTest1() throws Exception{
        //given
        String serviceId = "test-service";
        String sqlString = "select * from members";
        String indexName = "indexName";
        Worker worker = Worker.builder()
                .uuid(serviceId)
                .pkName("mem_idx")
                .tableName("members")
                .sqlString(sqlString)
                .indexName(indexName)
                .build();
        workerService.insert(worker);


        //when
        var response = this.mockMvc.perform(
                delete("/service/" + serviceId + "123")
                        .contentType("application/json;charset=UTF-8"));

        //then
        response.andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value(ErrorMessage.NOT_EXIST_SERVICE_ID.getMessage()))
                .andExpect(jsonPath("status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @DisplayName("서비스 삭제 event controller 실패 테스트 : indexName은 설정되었는데 index가 없는 경우")
    public void deleteServiceFailTest2() throws Exception{
        //given
        String serviceId = "test-service";
        String sqlString = "select * from members";
        String indexName = "indexName";
        Worker worker = Worker.builder()
                .uuid(serviceId)
                .pkName("mem_idx")
                .tableName("members")
                .sqlString(sqlString)
                .indexName(indexName)
                .build();
        workerService.insert(worker);


        //when
        var response = this.mockMvc.perform(
                delete("/service/" + serviceId)
                        .contentType("application/json;charset=UTF-8"));

        //then
        response.andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value(ErrorMessage.NOT_EXIST_INDEX.getMessage()))
                .andExpect(jsonPath("status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @DisplayName("서비스 삭제 event controller 성공 테스트 : index 도 삭제")
    public void deleteServiceSuccessTest2() throws Exception{
        //given
        String serviceId = "test-service";
        String sqlString = "select * from members";
        String indexName ="test_index";
        Worker worker = Worker.builder()
                .uuid(serviceId)
                .pkName("mem_idx")
                .tableName("members")
                .indexName(indexName)
                .sqlString(sqlString)
                .build();
        workerService.insert(worker);
        indexDAO.create(indexName, List.of(Map.of("mem_idx","1")));

        //when
        var response = this.mockMvc.perform(
                delete("/service/" + serviceId)
                        .contentType("application/json;charset=UTF-8"));

        //then
        response.andExpect(status().isOk());
        assertTrue(workerService.getWorker(serviceId).isEmpty());
    }
}
