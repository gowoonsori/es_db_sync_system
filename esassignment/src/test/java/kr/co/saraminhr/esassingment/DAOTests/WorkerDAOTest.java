package kr.co.saraminhr.esassingment.DAOTests;

import kr.co.saraminhr.esassingment.Configs.LocalDataSourceConfig;
import kr.co.saraminhr.esassingment.Daos.CoffeeDAO;
import kr.co.saraminhr.esassingment.Daos.WorkerDAO;
import kr.co.saraminhr.esassingment.Domains.Worker;
import kr.co.saraminhr.esassingment.Exceptions.BadRequestException;
import kr.co.saraminhr.esassingment.Exceptions.DuplicateServiceIdException;
import kr.co.saraminhr.esassingment.Utils.ErrorMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@Transactional
@SpringBootTest(classes = {WorkerDAO.class , LocalDataSourceConfig.class}, properties = "classpath:application.yml")
public class WorkerDAOTest {
    @Autowired
    WorkerDAO workerDAO;

    @Mock
    @Qualifier("localJdbcTemplate")
    JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("서비스 생성 테스트")
    public void createTest(){
        //given
        Worker worker= Worker.builder()
                .uuid("test-service")
                .sqlString("select * from members where mem_idx <10000 limit 1")
                .pkName("mem_idx")
                .tableName("members")
                .build();

        //when
        int result = workerDAO.insert(worker);

        //then
        assertEquals(1,result);
    }

    @Test
    @DisplayName("서비스 생성 실패 테스트 : 중복 service Id")
    public void createFailTest(){
        //given
        Worker worker= Worker.builder()
                .uuid("test-service")
                .sqlString("select * from members where mem_idx <10000 limit 1")
                .pkName("mem_idx")
                .tableName("members")
                .build();
        createService(worker);

        //when
        Exception exception = assertThrows(DuplicateServiceIdException.class, ()->{
            workerDAO.insert(worker);
        });

        //then
        assertEquals(ErrorMessage.DUPLICATE_SERVICE_ID.getMessage(),
                exception.getMessage());
    }

    @Test
    @DisplayName("서비스 생성 실패 테스트 : 잘못된 parameter")
    public void createFailTest2(){
        //given
        Worker worker= Worker.builder()
                .uuid("test-service")
                .sqlString("select * from members where mem_idx <10000 limit 1select * from members where mem_idx <10000 limit 1select * from members where mem_idx <10000 limit 1select * from members where mem_idx <10000 limit 1select * from members where mem_idx <10000 limit 1select * from members where mem_idx <10000 limit 1select * from members where mem_idx <10000 limit 1")
                .pkName("mem_idx")
                .tableName("members")
                .build();

        //when
        Exception exception = assertThrows(DuplicateServiceIdException.class, ()->{
            workerDAO.insert(worker);
        });

        //then
        assertEquals(ErrorMessage.BAD_REQUEST.getMessage(),
                exception.getMessage());
    }

    @Test
    @DisplayName("모든 서비스들 조회 테스트")
    public void findAll(){
        //given
        Worker worker= Worker.builder()
                .uuid("test-service")
                .sqlString("select * from members where mem_idx <10000 limit 1")
                .pkName("mem_idx")
                .tableName("members")
                .createdAt(LocalDateTime.parse("2021-08-31T15:09:32"))
                .updatedAt(LocalDateTime.parse("2021-08-31T15:09:32"))
                .build();
        createService(worker);

        //when
        List<Worker> workerList = workerDAO.findAll();

        //then
        assertEquals("test-service",workerList.get(workerList.size()-1).getUuid());
        assertEquals("select * from members where mem_idx <10000 limit 1",workerList.get(workerList.size()-1).getSqlString());
    }

    @Test
    @DisplayName("모든 서비스들 페이징 조회 테스트")
    public void findAllPaging(){
        //given
        Pageable pageable = PageRequest.of(0,10);

        //when
        Page<Worker> workerList = workerDAO.findAll(pageable);

        //then
        assertEquals(10,workerList.getSize());
        assertEquals(0,workerList.getNumber());
    }

    @Test
    @DisplayName("service Id로 조회 테스트")
    public void findByServiceId(){
        //given
        String serviceId = "test-service";
        createService(Worker.builder()
                .uuid(serviceId)
                .sqlString("select * from members where mem_idx <10000 limit 1")
                .pkName("mem_idx")
                .tableName("members")
                .build());

        //when
        Optional<Worker> worker = workerDAO.findByServiceId(serviceId);

        //then
        assertEquals(serviceId,worker.get().getUuid());
    }

    @Test
    @DisplayName("service Id로 조회 실패 테스트 : 없는 service Id")
    public void findByServiceIdFailTest(){
        //given
        String serviceId = "test-service";

        //when
        Optional<Worker> worker =  workerDAO.findByServiceId(serviceId);

        //then
        assertEquals(true,worker.isEmpty());
    }

    @Test
    @DisplayName("service Id 들로 조회 테스트")
    public void findByServiceIds(){
        //given
        String[] serviceIds = new String[]{"test-service","test1","test2"};

        //when
        List<Worker> worker = workerDAO.findByServiceIds(serviceIds);

        //then
        assertArrayEquals(new String[]{},worker.toArray());
    }

    @Test
    @DisplayName("service index name update 테스트")
    public void updateIndexName(){
        //given
        String serviceId = "test-service";
        Worker worker = Worker.builder()
                .uuid(serviceId)
                .sqlString("select * from members where mem_idx <10000 limit 1")
                .pkName("mem_idx")
                .tableName("members")
                .build();
        createService(worker);
        String indexName = "test-index";

        //when
        worker.setIndexName(indexName);
        int result = workerDAO.updateIndexName(worker);

        //then
        assertEquals(1,result);
        assertEquals(indexName,workerDAO.findByServiceId(serviceId).get().getIndexName());
    }

    @Test
    @DisplayName("service index name update 실패 테스트 : 없는 serviceId")
    public void updateIndexNameFailTest1(){
        //given
        String serviceId = "test-service";
        String indexName = "test-index";
        Worker worker = Worker.builder()
                .indexName(indexName)
                .uuid(serviceId)
                .sqlString("select * from members where mem_idx <10000 limit 1")
                .pkName("mem_idx")
                .tableName("members")
                .build();

        //when
        Exception exception = assertThrows(BadRequestException.class, ()->{
            workerDAO.updateIndexName(worker);
        });

        //then
        assertEquals(ErrorMessage.NOT_EXIST_SERVICE_ID.getMessage(),exception.getMessage());
    }

    @Test
    @DisplayName("service index name update 실패 테스트 : 중복된 indexName")
    public void updateIndexNameFailTest2(){
        //given
        String preServiceId = "test-service1";
        String indexName = "test-index";
        createService(Worker.builder()
                .uuid(preServiceId)
                .indexName(indexName)
                .sqlString("select * from members where mem_idx <10000 limit 1")
                .pkName("mem_idx")
                .tableName("members")
                .build());

        String serviceId = "test-service2";
        Worker worker = Worker.builder()
                .uuid(serviceId)
                .sqlString("select * from members where mem_idx <10000 limit 1")
                .pkName("mem_idx")
                .tableName("members")
                .build();
        createService(worker);

        //when
        worker.setIndexName(indexName);
        Exception exception = assertThrows(BadRequestException.class, ()->{
            workerDAO.updateIndexName(worker);
        });

        //then
        assertEquals(ErrorMessage.DUPLICATE_INDEX_NAME.getMessage(),exception.getMessage());
    }

    @Test
    @DisplayName("service delete 테스트")
    public void delete(){
        //given
        String serviceId = "test-service";
        Worker worker = Worker.builder()
                .uuid(serviceId)
                .sqlString("select * from members where mem_idx <10000 limit 1")
                .pkName("mem_idx")
                .tableName("members")
                .build();
        createService(worker);

        //when
        int result = workerDAO.delete(worker);

        //then
        assertEquals(1,result);
        assertEquals(true,workerDAO.findByServiceId(serviceId).isEmpty());
    }

    @Test
    @DisplayName("service delete 실패 테스트")
    public void deleteFailTest(){
        //given
        String serviceId = "test-service";
        String indexName = "test-index";
        Worker worker = Worker.builder()
                .uuid(serviceId)
                .indexName(indexName)
                .sqlString("select * from members where mem_idx <10000 limit 1")
                .pkName("mem_idx")
                .tableName("members")
                .build();

        //when
        Exception exception = assertThrows(BadRequestException.class, ()->{
            workerDAO.delete(worker);
        });

        //then
        assertEquals(ErrorMessage.NOT_EXIST_SERVICE_ID.getMessage(),exception.getMessage());
    }

    @Test
    @DisplayName("pk 이름 찾는 테스트")
    public void findPKName(){
        //given
        String tableName = "coffee";
        String pkName = "id";

        //when
        String result = workerDAO.findPKName(tableName);

        //then
        assertEquals(pkName,result);
    }

    @Test
    @DisplayName("pk 이름 찾는 테스트 : 실패 테스트")
    public void findPKNameFailTest() throws SQLException {
        //given
        String tableName = "members";
        String pkName = "id";

        //when
        Throwable exception = assertThrows(BadRequestException.class,
                ()->workerDAO.findPKName(tableName));

        //then
        assertEquals(ErrorMessage.NOT_EXIST_TABLE_NAME.getMessage(),exception.getMessage());
    }


    private void createService(Worker worker){
        workerDAO.insert(worker);
    }
}
