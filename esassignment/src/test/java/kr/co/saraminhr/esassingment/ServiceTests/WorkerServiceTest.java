package kr.co.saraminhr.esassingment.ServiceTests;

import kr.co.saraminhr.esassingment.Daos.IndexDAO;
import kr.co.saraminhr.esassingment.Daos.TableDAO;
import kr.co.saraminhr.esassingment.Daos.WorkerDAO;
import kr.co.saraminhr.esassingment.Domains.Worker;
import kr.co.saraminhr.esassingment.Dtos.WorkerRequestDto;
import kr.co.saraminhr.esassingment.Exceptions.BadRequestException;
import kr.co.saraminhr.esassingment.Services.WorkerService;
import kr.co.saraminhr.esassingment.Utils.ErrorMessage;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.common.io.stream.StreamInput;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static kr.co.saraminhr.esassingment.Utils.PageUtil.listToPage;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WorkerServiceTest {
    @InjectMocks
    WorkerService workerService;

    @Mock
    WorkerDAO workerDAO;
    @Mock
    TableDAO tableDAO;
    @Mock
    IndexDAO indexDAO;

    @Test
    @DisplayName("모든 서비스 페이징 조회")
    public void getWorkers() {
        //given
        Pageable pageable = PageRequest.of(0,10);
        when(workerDAO.findAll(pageable))
                .thenReturn(listToPage(pageable,createWorkerList(20)));

        //when
        Page<Worker> workers = workerService.getWorkers(pageable);

        //then
        assertAll(
                () -> assertEquals(2,workers.getTotalPages()),
                () -> assertEquals(20,workers.getTotalElements()),
                () -> assertEquals(10,workers.getSize()),
                () -> assertEquals(0,workers.getNumber())
        );
    }

    @Test
    @DisplayName("service ids 로 서비스들 조회")
    public void getWorkersByServiceIds() {
        //given
        String[] serviceIds = new String[]{};
        List<Worker> workers = createWorkerList(20);
        when(workerDAO.findByServiceIds(serviceIds))
                .thenReturn(workers);

        //when
        List<Worker> result = workerService.getWorkers(serviceIds);

        //then
        assertAll(
                () -> assertEquals("0",result.get(0).getUuid()),
                () -> assertEquals("test-index0",result.get(0).getIndexName()),
                () -> assertEquals("id",result.get(0).getPkName()),
                () -> assertEquals("test",result.get(0).getTableName()),
                () -> assertEquals("select * from test",result.get(0).getSqlString())
        );
    }

    @Test
    @DisplayName("service id 로 서비스 조회")
    public void getWorkerByServiceId() {
        //given
        String serviceId = "0";
        Worker worker = createWorker(serviceId);
        when(workerDAO.findByServiceId(serviceId))
                .thenReturn(Optional.of(worker));

        //when
        Optional<Worker> result = workerService.getWorker(serviceId);

        //then
        assertAll(
                () -> assertTrue(result.isPresent()),
                () -> assertEquals("0",result.get().getUuid()),
                () -> assertEquals("test-index0",result.get().getIndexName()),
                () -> assertEquals("id",result.get().getPkName()),
                () -> assertEquals("test",result.get().getTableName()),
                () -> assertEquals("select * from test",result.get().getSqlString())
        );
    }

    @Test
    @DisplayName("서비스 삭제")
    public void deleteWorker() throws IOException {
        //given
        String serviceId = "0";
        Worker worker = createWorker(serviceId);
        when(workerDAO.findByServiceId(serviceId))
                .thenReturn(Optional.of(worker));
        when(workerDAO.delete(worker)).thenReturn(1);
        when(indexDAO.delete(worker.getIndexName())).thenReturn(new AcknowledgedResponse(StreamInput.wrap("hi".getBytes(StandardCharsets.UTF_8)),false));

        //when
        workerService.delete(serviceId);
    }

    @Test
    @DisplayName("서비스 삭제 실패 :서비스 id 존재 x")
    public void deleteWorkerFail1() throws IOException {
        //given
        String serviceId = "0";
        when(workerDAO.findByServiceId(serviceId))
                .thenReturn(Optional.empty());

        //when
        Throwable exception = assertThrows(BadRequestException.class,
                ()->workerService.delete(serviceId));

        //then
        assertEquals(ErrorMessage.NOT_EXIST_SERVICE_ID.getMessage(),exception.getMessage());
    }

    @Test
    @DisplayName("서비스 삭제 : index 존재 x")
    public void deleteWorkerTest() {
        //given
        String serviceId = "0";
        Worker worker = createEmptyIndexNameWorker(serviceId);
        when(workerDAO.findByServiceId(serviceId))
                .thenReturn(Optional.of(worker));

        //when
        workerService.delete(serviceId);
    }

    @Test
    @DisplayName("WorkerRequest에서 table 이름과 pk이름을 parsing 한 서비스 리턴 : coffee 테이블")
    public void getWorkTableAndPK() {
        //given
        String serviceId = "0";
        String sqlString = "select * from coffee";
        String tableName = "coffee";
        String pkName = "id";
        WorkerRequestDto request = new WorkerRequestDto(serviceId,sqlString);
        when(workerDAO.findPKName(tableName))
                .thenReturn(pkName);

        //when
        Worker result = workerService.getWorkerWithTableNameAndPKName(request);

        //then
        assertAll(
                () -> assertEquals(serviceId,result.getUuid()),
                () -> assertEquals(sqlString,result.getSqlString()),
                () -> assertEquals(tableName,result.getTableName()),
                () -> assertEquals(pkName,result.getPkName())
        );
    }

    @Test
    @DisplayName("WorkerRequest에서 table 이름과 pk이름을 parsing 한 서비스 리턴 : saramin db 테이블")
    public void getWorkTableAndPKBySaramin() {
        //given
        String serviceId = "0";
        String sqlString = "select * from members";
        String tableName = "members";
        String pkName = "mem_idx";
        WorkerRequestDto request = new WorkerRequestDto(serviceId,sqlString);
        when(tableDAO.findPKName(tableName))
                .thenReturn(pkName);

        //when
        Worker result = workerService.getWorkerWithTableNameAndPKName(request);

        //then
        assertAll(
                () -> assertEquals(serviceId,result.getUuid()),
                () -> assertEquals(sqlString,result.getSqlString()),
                () -> assertEquals(tableName,result.getTableName()),
                () -> assertEquals(pkName,result.getPkName())
        );
    }

    @Test
    @DisplayName("WorkerRequest에서 table 이름과 pk이름을 parsing 한 서비스 리턴 실패 테스트 : pk 존재 x")
    public void getWorkTableAndPKFailTest() {
        //given
        String serviceId = "0";
        String sqlString = "select passwd from coffee";
        String tableName = "coffee";
        String pkName = "id";
        WorkerRequestDto request = new WorkerRequestDto(serviceId,sqlString);
        when(workerDAO.findPKName(tableName))
                .thenReturn(pkName);

        //when
        Throwable exception = assertThrows(BadRequestException.class,
                () -> workerService.getWorkerWithTableNameAndPKName(request)
        );

        //then
        assertEquals(ErrorMessage.NOT_EXIST_PK.getMessage(),exception.getMessage());
    }

    private List<Worker> createWorkerList(int max){
        return IntStream.range(0,max).mapToObj(i -> createWorker(String.valueOf(i))).collect(Collectors.toList());
    }

    private Worker createWorker(String id){
        return Worker.builder()
                .indexName("test-index" + id)
                .pkName("id")
                .tableName("test")
                .uuid(id)
                .sqlString("select * from test")
                .build();
    }

    private Worker createEmptyIndexNameWorker(String id){
        return Worker.builder()
                .pkName("id")
                .tableName("test")
                .uuid(id)
                .sqlString("select * from test")
                .build();
    }
}
