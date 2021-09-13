package kr.co.saraminhr.esassingment.DAOTests;

import kr.co.saraminhr.esassingment.Configs.*;
import kr.co.saraminhr.esassingment.Daos.IndexDAO;
import kr.co.saraminhr.esassingment.Daos.TableDAO;
import kr.co.saraminhr.esassingment.Daos.WorkerDAO;
import kr.co.saraminhr.esassingment.Domains.Index;
import kr.co.saraminhr.esassingment.Domains.Worker;
import kr.co.saraminhr.esassingment.Dtos.SearchRequestDto;
import kr.co.saraminhr.esassingment.Exceptions.BadRequestException;
import kr.co.saraminhr.esassingment.Utils.ErrorMessage;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(classes = {
        TableDAO.class , IndexDAO.class, WorkerDAO.class,
        LocalDataSourceConfig.class, TableDataSourceConfig.class,TransactionConfig.class,
        ElasticsearchProperties.class, ElasticSearchConfig.class}, properties = "classpath:application.yml")
public class IndexDAOTest {
    @Autowired
    IndexDAO indexDAO;

    @Autowired
    WorkerDAO workerDAO;

    @Autowired
    TableDAO tableDAO;

    @Test()
    @DisplayName("index 생성 메서드 테스트")
    void create(){
        //given
        String indexName = "test_index";
        List<Map<String,Object>> dataList = new ArrayList<>();
        dataList.add(new HashMap<>());

        //when
        CreateIndexResponse response = indexDAO.create(indexName,dataList);

        //then
        Assertions.assertEquals(indexName,response.index());
        deleteIndex(indexName);
    }

    @Test
    @DisplayName("index 중복 생성시 예외 테스트")
    void createDuplicate(){
        //given
        String indexName = "test_index";
        List<Map<String,Object>> dataList = new ArrayList<>();
        dataList.add(new HashMap<>());
        createIndex(indexName);


        //when
        Exception e = Assertions.assertThrows(BadRequestException.class,()->{
            indexDAO.create(indexName,dataList);
        });

        //then
        assertEquals(e.getMessage(),ErrorMessage.DUPLICATE_INDEX_NAME.getMessage());
        deleteIndex(indexName);
    }

    @Test()
    @DisplayName("index 생성 메서드 실패 테스트 : IOException")
    void createFailTest(){
        //given
        String indexName = "test_index";
        List<Map<String,Object>> dataList = new ArrayList<>();
        dataList.add(new HashMap<>());

        //when
        CreateIndexResponse response = indexDAO.create(indexName,dataList);

        //then
        Assertions.assertEquals(indexName,response.index());
        deleteIndex(indexName);
    }

    @Test
    @DisplayName("이름으로 index 삭제 메서드 테스트")
    void deleteTest() {
        //given
        String indexName = "test_index";
        createIndex(indexName);

        //when
        AcknowledgedResponse response = indexDAO.delete(indexName);

        //then
        assertEquals(true,response.isAcknowledged());
    }

    @Test
    @DisplayName("없는 index삭제시 예외 테스트")
    void deleteTestNotExist() {
        //given
        String indexName = "test_index";

        //when
        Exception e = Assertions.assertThrows(BadRequestException.class,()->{
            indexDAO.delete(indexName);
        });

        //then
        assertEquals(e.getMessage(),ErrorMessage.NOT_EXIST_INDEX.getMessage());
    }

    @Test
    @DisplayName("모든 index 조회 메서드 테스트")
    void findAll(){
        //given
        String indexName = "test_index";
        createIndex(indexName);

        //when
        List<Index> response = indexDAO.findAll();

        //then
        Assertions.assertEquals(Index.class.getName(), response.get(0).getClass().getName());
        deleteIndex(indexName);
    }

    @Test
    @DisplayName("Paging 된 index 조회 메서드 테스트")
    void findAllPaging(){
        //given
        Pageable pageable = PageRequest.of(0,10);

        //when
        Page<Index> response = indexDAO.findAll(pageable);

        //then
        Assertions.assertEquals(10, response.getSize());
        Assertions.assertEquals(0,response.getNumber() );
    }

    @Test
    @DisplayName("이름으로 index 조회 메서드 테스트")
    void findByNameTest() {
        //given
        String indexName = "test_index";
        createIndex(indexName);

        //when
        GetIndexResponse response = indexDAO.findByName(indexName);

        //then
        assertArrayEquals(response.getIndices(),new String[]{indexName});
        deleteIndex(indexName);
    }

    @Test
    @DisplayName("index 존재여부 테스트 : 존재")
    void existsTest() {
        //given
        String indexName = "test_index";
        createIndex(indexName);

        //when
        boolean response = indexDAO.exists(indexName);

        //then
        assertEquals(true,response);
        deleteIndex(indexName);
    }

    @Test
    @DisplayName("index 존재여부 테스트 : 존재x")
    void notExistsTest() {
        //given
        String indexName = "test_index_not_exist";

        //when
        boolean response = indexDAO.exists(indexName);

        //then
        assertEquals(false,response);
    }

    @Test
    @DisplayName("index bulk")
    void bulkTest() {
        //given
        String indexName = "test_index";
        createIndex(indexName);
        List<Map<String,Object>> dataList = List.of(Map.of("content","Hi"));

        //when
        BulkResponse response = indexDAO.bulk(indexName,dataList);

        //then
        assertEquals(200,response.status().getStatus());
        deleteIndex(indexName);
    }

    @Test
    @DisplayName("index search")
    void search() {
        //given
        String serviceId = "test-service";
        String indexName = "test_index";
        String searchText = "hi";
        String[] resultColumns = new String[]{"content"};

        workerDAO.insert(Worker.builder()
                .uuid(serviceId)
                .sqlString("select * from members where mem_idx <10000 limit 1")
                .pkName("mem_idx")
                .tableName("members")
                .indexName(indexName)
                .build());

        createIndex(indexName);
        List<Map<String,Object>> dataList = List.of(Map.of("content","hi hi hi hi hi"));
        indexDAO.bulk(indexName,dataList);
        indexDAO.refresh(indexName);

        //when
        SearchResponse response = indexDAO.search(indexName,SearchRequestDto.builder()
                .searchText(searchText)
                .resultColumns(resultColumns)
                .serviceId(serviceId)
                .build());

        //then
        assertEquals(200,response.status().getStatus());
        assertEquals(1,response.getHits().getTotalHits().value);
        deleteIndex(indexName);
    }

    @Test
    @DisplayName("index search")
    void search1() {
        //given
        String serviceId = "test-service";
        String indexName = "test_index";
        String searchText = "hi";
        String[] resultColumns = new String[]{"content"};

        workerDAO.insert(Worker.builder()
                .uuid(serviceId)
                .sqlString("select * from members where mem_idx <10000 limit 1")
                .pkName("mem_idx")
                .tableName("members")
                .indexName(indexName)
                .build());

        createIndex(indexName);
        List<Map<String,Object>> dataList = List.of(Map.of("content","hi hi hi hi hi"));
        indexDAO.bulk(indexName,dataList);
        indexDAO.refresh(indexName);

        //when
        SearchResponse response = indexDAO.search(indexName,SearchRequestDto.builder()
                .searchText(searchText)
                .resultColumns(resultColumns)
                .serviceId(serviceId)
                .build());

        //then
        assertEquals(200,response.status().getStatus());
        assertEquals(1,response.getHits().getTotalHits().value);
        deleteIndex(indexName);
    }

    @Test
    @DisplayName("index search : text 존재 x")
    void search2() {
        //given
        String serviceId = "test-service";
        String indexName = "test_index";
        String[] resultColumns = new String[]{"content"};

        workerDAO.insert(Worker.builder()
                .uuid(serviceId)
                .sqlString("select * from members where mem_idx <10000 limit 1")
                .pkName("mem_idx")
                .tableName("members")
                .indexName(indexName)
                .build());

        createIndex(indexName);
        List<Map<String,Object>> dataList = List.of(Map.of("content","hi hi hi hi hi"));
        indexDAO.bulk(indexName,dataList);
        indexDAO.refresh(indexName);

        //when
        SearchResponse response = indexDAO.search(indexName,SearchRequestDto.builder()
                .resultColumns(resultColumns)
                .serviceId(serviceId)
                .build());

        //then
        assertEquals(200,response.status().getStatus());
        assertEquals(1,response.getHits().getTotalHits().value);
        deleteIndex(indexName);
    }

    @Test
    @DisplayName("index search : field 존재 x")
    void search3() {
        //given
        String serviceId = "test-service";
        String indexName = "test_index";
        String searchText = "hi";
        String[] resultColumns = new String[]{};

        workerDAO.insert(Worker.builder()
                .uuid(serviceId)
                .sqlString("select * from members where mem_idx <10000 limit 1")
                .pkName("mem_idx")
                .tableName("members")
                .indexName(indexName)
                .build());

        createIndex(indexName);
        List<Map<String,Object>> dataList = List.of(Map.of("content","hi hi hi hi hi"));
        indexDAO.bulk(indexName,dataList);
        indexDAO.refresh(indexName);

        //when
        SearchResponse response = indexDAO.search(indexName,SearchRequestDto.builder()
                .searchText(searchText)
                .resultColumns(resultColumns)
                .serviceId(serviceId)
                .build());

        //then
        assertEquals(200,response.status().getStatus());
        assertEquals(1,response.getHits().getTotalHits().value);
        deleteIndex(indexName);
    }

    @Test
    @DisplayName("document indexing : 생성")
    void indexing() {
        //given
        String serviceId = "test-service";
        String indexName = "test_index";
        String pkName = "id";
        String id = "123";

        createIndex(indexName);
        List<Map<String,Object>> dataList = List.of(Map.of("content","hi hi hi hi hi"));
        indexDAO.bulk(indexName,dataList);
        indexDAO.refresh(indexName);

        Map<String,Object> indexingData = Map.of("content","new indexing",pkName,id);


        //when
        IndexResponse response = indexDAO.indexing(indexName,pkName,indexingData);

        //then
        assertEquals(201,response.status().getStatus());
        assertEquals(id,response.getId());
        assertEquals(DocWriteResponse.Result.CREATED,response.getResult());

        deleteIndex(indexName);
    }

    @Test
    @DisplayName("document indexing : 수정")
    void indexing2() {
        //given
        String serviceId = "test-service";
        String indexName = "test_index";
        String pkName = "id";
        String id = "123";

        workerDAO.insert(Worker.builder()
                .uuid(serviceId)
                .sqlString("select * from members where mem_idx <10000 limit 1")
                .pkName(pkName)
                .tableName("members")
                .indexName(indexName)
                .build());

        createIndex(indexName);
        List<Map<String,Object>> dataList = List.of(Map.of("content","hi hi hi hi hi",pkName,id));
        indexDAO.bulk(indexName,dataList,pkName);
        indexDAO.refresh(indexName);

        Map<String,Object> indexingData = Map.of("content","new indexing",pkName,id);


        //when
        IndexResponse response = indexDAO.indexing(indexName,pkName,indexingData);

        //then
        assertEquals(200,response.status().getStatus());
        assertEquals(id,response.getId());
        assertEquals(DocWriteResponse.Result.UPDATED,response.getResult());

        deleteIndex(indexName);
    }

    @Test
    @DisplayName("document indexing : 삭제")
    void indexing3() {
        //given
        String serviceId = "test-service";
        String indexName = "test_index";
        String pkName = "id";
        String id = "123";

        workerDAO.insert(Worker.builder()
                .uuid(serviceId)
                .sqlString("select * from members where mem_idx <10000 limit 1")
                .pkName(pkName)
                .tableName("members")
                .indexName(indexName)
                .build());

        createIndex(indexName);
        List<Map<String,Object>> dataList = List.of(Map.of("content","hi hi hi hi hi",pkName,id));
        indexDAO.bulk(indexName,dataList,pkName);
        indexDAO.refresh(indexName);

        //when
        DeleteResponse response = indexDAO.deleteDocument(indexName,id);

        //then
        assertEquals(200,response.status().getStatus());
        assertEquals(id,response.getId());
        assertEquals(DocWriteResponse.Result.DELETED,response.getResult());

        deleteIndex(indexName);
    }

    private void createIndex(String indexName){
        List<Map<String,Object>> dataList = new ArrayList<>();
        Map<String,Object> map = new HashMap<>();
        map.put("content","Hi");
        dataList.add(map);

        indexDAO.create(indexName,dataList);
    }

    private void deleteIndex(String indexName){
        indexDAO.delete(indexName);
    }

}
