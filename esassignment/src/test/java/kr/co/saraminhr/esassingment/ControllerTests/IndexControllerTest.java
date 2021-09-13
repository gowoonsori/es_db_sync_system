package kr.co.saraminhr.esassingment.ControllerTests;

import kr.co.saraminhr.esassingment.Commons.BaseTest;
import kr.co.saraminhr.esassingment.Daos.IndexDAO;
import kr.co.saraminhr.esassingment.Domains.Worker;
import kr.co.saraminhr.esassingment.Dtos.*;
import kr.co.saraminhr.esassingment.Services.IndexService;
import kr.co.saraminhr.esassingment.Services.WorkerService;
import kr.co.saraminhr.esassingment.Utils.ErrorMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
public class IndexControllerTest extends BaseTest {

    @Autowired
    IndexDAO indexDAO;

    @Autowired
    WorkerService workerService;

    @Autowired
    IndexService indexService;

    @Test
    @DisplayName("전체 index 조회 성공")
    public void getIndices() throws Exception{
        //given
        String indexName = "test_index";
        createIndex(indexName);

        //when
        var response = this.mockMvc.perform(get("/index/search/indices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print());

        //then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("indices").exists())
                .andExpect(jsonPath("indices[0].status").exists())
                .andExpect(jsonPath("indices[0].uuid").exists())
                .andExpect(jsonPath("indices[0].count").exists())
                .andExpect(jsonPath("indices[0].index").exists())
                .andExpect(jsonPath("indices[0].health").exists());
        deleteIndex(indexName);
    }

    @Test
    @DisplayName("처음 index bulk 성공 테스트")
    public void bulk() throws Exception{
        //given
        String indexName = "test_index";
        String serviceId = "test-service";
        String sqlString = "select * from members where mem_idx < 10000 limit 1";
        workerService.insert(Worker.builder()
                .uuid(serviceId)
                .sqlString(sqlString)
                .pkName("mem_idx")
                .tableName("members").build());
        BulkRequestDto bulkRequestDto = BulkRequestDto.builder()
                .indexName(indexName)
                .serviceId(serviceId)
                .build();

        //when
        var response = this.mockMvc.perform(post("/index/bulk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON).content(bulkRequestDto.toString()))
                .andDo(print());

        //then
        response.andExpect(status().isOk());
        deleteIndex(indexName);
    }

    @Test
    @DisplayName("index 재 bulk 테스트")
    public void reBulkTest() throws Exception{
        //given
        String indexName = "test_index";
        String serviceId = "not-exist";
        String sqlString = "select * from members where mem_idx < 10000 limit 1";
        workerService.insert(Worker.builder()
                .uuid(serviceId)
                .sqlString(sqlString)
                .pkName("mem_idx")
                .tableName("members").build());
        BulkRequestDto bulkRequestDto = BulkRequestDto.builder()
                .indexName(indexName)
                .serviceId(serviceId)
                .build();
        indexService.bulkIndex(bulkRequestDto);

        //when
        String reIndexName = "re_bulk_index";
        bulkRequestDto.setIndexName("re_bulk_index");
        var response = this.mockMvc.perform(post("/index/bulk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON).content(bulkRequestDto.toString()))
                .andDo(print());

        //then
        response.andExpect(status().is(200));
        deleteIndex(reIndexName);
    }

    @Test
    @DisplayName("index bulk 실패 테스트 : 없는 service Id")
    public void bulkNotExistServiceId() throws Exception{
        //given
        String indexName = "test_index";
        String serviceId = "not-exist";
        BulkRequestDto bulkRequestDto = BulkRequestDto.builder()
                .indexName(indexName)
                .serviceId(serviceId)
                .build();

        //when
        var response = this.mockMvc.perform(post("/index/bulk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON).content(bulkRequestDto.toString()))
                .andDo(print());

        //then
        response.andExpect(status().is(400))
                .andExpect(jsonPath("message").value(ErrorMessage.NOT_EXIST_SERVICE_ID.getMessage()))
                .andExpect(jsonPath("status").value(400));
    }

    @Test
    @DisplayName("index bulk 실패 테스트 : validateion 에러")
    public void bulkValidate() throws Exception{
        //given
        String serviceId = "test-service";
        String requestIndexName = "test_indextest_indextest_indextest_indextest_indextest_indextest_indextest_indextest_indextest_indextest_indextest_index";

        BulkRequestDto bulkRequestDto = BulkRequestDto.builder()
                .indexName(requestIndexName)
                .serviceId(serviceId)
                .build();

        //when
        var response = this.mockMvc.perform(post("/index/bulk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON).content(bulkRequestDto.toString()))
                .andDo(print());

        //then
        response.andExpect(status().is(400))
                .andExpect(jsonPath("message['valid_indexName']").value("인덱스 명은 1~30자 사이로 입력해주세요."))
                .andExpect(jsonPath("status").value(400));
    }

    @Test
    @DisplayName("index search 테스트")
    public void search() throws Exception{
        //given
        String indexName = "test_index";
        String serviceId = "test-service";
        String sqlString = "select * from members where mem_idx < 10000 limit 100";
        String searchText = "uco";
        String[] resultColumns = new String[]{"id"};

        workerService.insert(Worker.builder()
                .uuid(serviceId)
                .sqlString(sqlString)
                .pkName("mem_idx")
                .tableName("members").build());
        BulkRequestDto bulkRequestDto = BulkRequestDto.builder()
                .indexName(indexName)
                .serviceId(serviceId)
                .build();
        indexService.bulkIndex(bulkRequestDto);

        SearchRequestDto searchRequestDto = SearchRequestDto.builder()
                .pageNo(0)
                .pageSize(10)
                .serviceId(serviceId)
                .searchText(searchText)
                .resultColumns(resultColumns)
                .build();

        //when
        indexDAO.refresh(indexName);
        var response = this.mockMvc.perform(post("/index/search/contents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON).content(searchRequestDto.toString()))
                .andDo(print());

        //then
        response.andExpect(status().is(200))
                .andExpect(jsonPath("total").exists())
                .andExpect(jsonPath("page_size").exists())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("result").exists());
        deleteIndex(indexName);
    }

    @Test
    @DisplayName("index search 테스트 : with highlight")
    public void searchWithHighlight() throws Exception{
        //given
        String indexName = "test_index";
        String serviceId = "test-service";
        String sqlString = "select * from members where mem_idx < 10000 limit 100";
        String searchText = "uco";

        workerService.insert(Worker.builder()
                .uuid(serviceId)
                .sqlString(sqlString)
                .pkName("mem_idx")
                .tableName("members").build());
        BulkRequestDto bulkRequestDto = BulkRequestDto.builder()
                .indexName(indexName)
                .serviceId(serviceId)
                .build();
        indexService.bulkIndex(bulkRequestDto);
        indexDAO.refresh(indexName);

        //when
        var response = this.mockMvc.perform(post("/index/search/contents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{   \n" +
                                "    \"service_id\": \""+ serviceId+"\",\n" +
                                "    \"search_text\": \""+ searchText+"\",\n" +
                                "    \"result_columns\": [\"id\"],\n" +
                                "    \"highlight\" : {\n" +
                                "        \"columns\" : [{\n" +
                                "            \"column\": \"id\"\n" +
                                "        }],\n" +
                                "        \"prefix_tag\": \"<b>\",\n" +
                                "        \"postfix_tag\": \"</b>\"\n" +
                                "    },\n" +
                                "    \"page_size\": 10,\n" +
                                "    \"page_no\": 0\n" +
                                "}"))
                .andDo(print());

        //then
        response.andExpect(status().is(200))
                .andExpect(jsonPath("total").exists())
                .andExpect(jsonPath("page_size").exists())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("result").exists());
        deleteIndex(indexName);
    }

    @Test
    @DisplayName("index search 실패 테스트 : parameter validation 실패")
    public void searchFailTest() throws Exception{
        //given
        String serviceId = "test-service";
        String searchText = "s";
        String[] resultColumns = new String[]{"id"};

        SearchRequestDto searchRequestDto = SearchRequestDto.builder()
                .pageNo(-1)
                .pageSize(10)
                .serviceId(serviceId)
                .searchText(searchText)
                .resultColumns(resultColumns)
                .build();

        //when
        var response = this.mockMvc.perform(post("/index/search/contents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON).content(searchRequestDto.toString()))
                .andDo(print());

        //then
        response.andExpect(status().is(400))
                .andExpect(jsonPath("message['valid_pageNo']").exists())
                .andExpect(jsonPath("status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @DisplayName("index search 실패 테스트 : 없는 serviceId")
    public void searchFailTest2() throws Exception{
        //given
        String searchText = "s";
        String[] resultColumns = new String[]{"id"};

        SearchRequestDto searchRequestDto = SearchRequestDto.builder()
                .pageNo(0)
                .pageSize(10)
                .serviceId("not-exist-service")
                .searchText(searchText)
                .resultColumns(resultColumns)
                .build();

        //when
        var response = this.mockMvc.perform(post("/index/search/contents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON).content(searchRequestDto.toString()))
                .andDo(print());

        //then
        response.andExpect(status().is(400))
                .andExpect(jsonPath("message").value(ErrorMessage.NOT_EXIST_SERVICE_ID.getMessage()))
                .andExpect(jsonPath("status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @DisplayName("index search 실패 테스트 : 없는 index Name")
    public void searchFailTest3() throws Exception{
        //given
        String indexName = "test_index";
        String serviceId = "test-service";
        String sqlString = "select * from members where mem_idx < 10000 limit 100";
        String searchText = "s";
        String[] resultColumns = new String[]{"id"};

        workerService.insert(Worker.builder()
                .uuid(serviceId)
                .sqlString(sqlString)
                .pkName("mem_idx")
                .tableName("members").build());

        SearchRequestDto searchRequestDto = SearchRequestDto.builder()
                .pageNo(0)
                .pageSize(10)
                .serviceId(serviceId)
                .searchText(searchText)
                .resultColumns(resultColumns)
                .build();

        //when
        var response = this.mockMvc.perform(post("/index/search/contents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON).content(searchRequestDto.toString()))
                .andDo(print());

        //then
        response.andExpect(status().is(400))
                .andExpect(jsonPath("message").value(ErrorMessage.NOT_EXIST_INDEX.getMessage()))
                .andExpect(jsonPath("status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @DisplayName("document indexing : 생성")
    public void indexingCreate() throws Exception{
        //given
        String indexName = "test_index";
        String serviceId = "test-service";
        String[] serviceIds = new String[]{"test-service"};
        String sqlString = "select * from members where mem_idx < 10000 limit 10";
        String contentsIdValue = "56295";

        workerService.insert(Worker.builder()
                .uuid(serviceId)
                .sqlString(sqlString)
                .pkName("mem_idx")
                .tableName("members").build());
        BulkRequestDto bulkRequestDto = BulkRequestDto.builder()
                .indexName(indexName)
                .serviceId(serviceId)
                .build();
        indexService.bulkIndex(bulkRequestDto);

        IndexingDto indexingDto = IndexingDto.builder()
                .serviceIds(serviceIds)
                .contentsIdValue(contentsIdValue)
                .build();

        //when
        var response = this.mockMvc.perform(post("/index/search/indexing")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON).content(indexingDto.toString()))
                .andDo(print());

        //then
        response.andExpect(status().is(200));
        indexDAO.refresh(indexName);
        this.mockMvc.perform(post("/index/search/contents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON).content(SearchRequestDto.builder()
                                .serviceId(serviceId)
                                .searchText(contentsIdValue)
                                .build().toString()))
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("result[0]['data']['mem_idx']").value(contentsIdValue));
        deleteIndex(indexName);
    }

    @Test
    @DisplayName("document indexing : 생성(demo 용 coffee 테이블")
    public void indexingCreateDemoTest() throws Exception{
        //given
        String indexName = "test-index";
        String serviceId = "coffee";
        String[] serviceIds = new String[]{serviceId};
        String sqlString = "select * from coffee where id < 0";
        String contentsIdValue = "1";
        String searchText = "Kenya";

        workerService.insert(Worker.builder()
                .uuid(serviceId)
                .sqlString(sqlString)
                .pkName("id")
                .tableName("coffee").build());
        BulkRequestDto bulkRequestDto = BulkRequestDto.builder()
                .indexName(indexName)
                .serviceId(serviceId)
                .build();
        indexService.bulkIndex(bulkRequestDto);

        IndexingDto indexingDto = IndexingDto.builder()
                .serviceIds(serviceIds)
                .contentsIdValue(contentsIdValue)
                .build();

        //when
        var response = this.mockMvc.perform(post("/index/search/indexing")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON).content(indexingDto.toString()))
                .andDo(print());

        //then
        response.andExpect(status().is(200));
        indexDAO.refresh(indexName);
        this.mockMvc.perform(post("/index/search/contents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON).content(SearchRequestDto.builder()
                                .serviceId(serviceId)
                                .searchText(searchText)
                                .build().toString()))
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("result[0]['data']['id']").value(contentsIdValue));
        deleteIndex(indexName);
    }

    @Test
    @DisplayName("document indexing 실패 : validate")
    public void indexingCreateFail1() throws Exception{
        //given
        String contentsIdValue = "";
        String[] serviceIds = new String[]{"test-service"};

        IndexingDto indexingDto = IndexingDto.builder()
                .serviceIds(serviceIds)
                .contentsIdValue(contentsIdValue)
                .build();

        //when
        var response = this.mockMvc.perform(post("/index/search/indexing")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON).content(indexingDto.toString()))
                .andDo(print());

        //then
        response.andExpect(status().is(400))
                .andExpect(jsonPath("message['valid_contentsIdValue']").exists())
                .andExpect(jsonPath("status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @DisplayName("document indexing 실패 : not exist service id")
    public void indexingCreateFail2() throws Exception{
        //given
        String serviceId = "test-service";
        String contentsIdValue = "56295";
        String[] serviceIds = new String[]{"test-service"};

        IndexingDto indexingDto = IndexingDto.builder()
                .serviceIds(serviceIds)
                .contentsIdValue(contentsIdValue)
                .build();

        //when
        var response = this.mockMvc.perform(post("/index/search/indexing")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON).content(indexingDto.toString()))
                .andDo(print());

        //then
        response.andExpect(status().is(400))
                .andExpect(jsonPath("message").value(ErrorMessage.NOT_EXIST_SERVICE_ID.getMessage()))
                .andExpect(jsonPath("status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @DisplayName("document indexing 실패 : not create index")
    public void indexingCreateFail3() throws Exception{
        //given
        String serviceId = "test-service";
        String sqlString = "select * members limit 1";
        String contentsIdValue = "56295";
        String[] serviceIds = new String[]{"test-service"};

        workerService.insert(Worker.builder()
                .uuid(serviceId)
                .sqlString(sqlString)
                .pkName("mem_idx")
                .tableName("members").build());
        IndexingDto indexingDto = IndexingDto.builder()
                .serviceIds(serviceIds)
                .contentsIdValue(contentsIdValue)
                .build();

        //when
        var response = this.mockMvc.perform(post("/index/search/indexing")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON).content(indexingDto.toString()))
                .andDo(print());

        //then
        response.andExpect(status().is(400))
                .andExpect(jsonPath("message").value(ErrorMessage.NOT_EXIST_INDEX.getMessage()))
                .andExpect(jsonPath("status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @DisplayName("document indexing 실패 : not exist index")
    public void indexingCreateFail5() throws Exception{
        //given
        String serviceId = "test-service";
        String sqlString = "select * members limit 1";
        String contentsIdValue = "56295";
        String indexName = "test_index";
        String[] serviceIds = new String[]{"test-service"};

        workerService.insert(Worker.builder()
                .uuid(serviceId)
                .sqlString(sqlString)
                .pkName("mem_idx")
                .indexName(indexName)
                .tableName("members").build());
        IndexingDto indexingDto = IndexingDto.builder()
                .serviceIds(serviceIds)
                .contentsIdValue(contentsIdValue)
                .build();

        //when
        var response = this.mockMvc.perform(post("/index/search/indexing")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON).content(indexingDto.toString()))
                .andDo(print());

        //then
        response.andExpect(status().is(400))
                .andExpect(jsonPath("message").value(ErrorMessage.NOT_EXIST_INDEX.getMessage()))
                .andExpect(jsonPath("status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @DisplayName("document 삭제 성공")
    public void indexingDelete() throws Exception{
        //given
        String indexName = "test_index";
        String serviceId = "test-service";
        String sqlString = "select * from members where mem_idx = 56295";
        String contentsIdValue = "56295";
        String[] serviceIds = new String[]{"test-service"};

        workerService.insert(Worker.builder()
                .uuid(serviceId)
                .sqlString(sqlString)
                .pkName("mem_idx")
                .tableName("members").build());
        BulkRequestDto bulkRequestDto = BulkRequestDto.builder()
                .indexName(indexName)
                .serviceId(serviceId)
                .build();
        indexService.bulkIndex(bulkRequestDto);

        IndexingDto indexingDto = IndexingDto.builder()
                .serviceIds(serviceIds)
                .contentsIdValue(contentsIdValue)
                .build();

        //when
        var response = this.mockMvc.perform(delete("/index/search/indexing")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON).content(indexingDto.toString()))
                .andDo(print());

        //then
        response.andExpect(status().is(200));
        indexDAO.refresh(indexName);
        this.mockMvc.perform(post("/index/search/contents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON).content(SearchRequestDto.builder()
                                .serviceId(serviceId)
                                .searchText(contentsIdValue)
                                .build().toString()))
                .andDo(print())
                .andExpect(status().is(204));
        deleteIndex(indexName);
    }

    @Test
    @DisplayName("document 삭제 실패 : validate")
    public void indexingDeleteFail1() throws Exception{
        //given
        String contentsIdValue = "";
        String[] serviceIds = new String[]{"test-service"};

        IndexingDto indexingDto = IndexingDto.builder()
                .serviceIds(serviceIds)
                .contentsIdValue(contentsIdValue)
                .build();

        //when
        var response = this.mockMvc.perform(delete("/index/search/indexing")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON).content(indexingDto.toString()))
                .andDo(print());

        //then
        response.andExpect(status().is(400))
                .andExpect(jsonPath("message['valid_contentsIdValue']").exists())
                .andExpect(jsonPath("status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @DisplayName("document 삭제 실패 : not exist service id")
    public void indexingDeleteFail2() throws Exception{
        //given
        String serviceId = "test-service";
        String contentsIdValue = "56295";
        String[] serviceIds = new String[]{"test-service"};

        IndexingDto indexingDto = IndexingDto.builder()
                .serviceIds(serviceIds)
                .contentsIdValue(contentsIdValue)
                .build();

        //when
        var response = this.mockMvc.perform(delete("/index/search/indexing")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON).content(indexingDto.toString()))
                .andDo(print());

        //then
        response.andExpect(status().is(400))
                .andExpect(jsonPath("message").value(ErrorMessage.NOT_EXIST_SERVICE_ID.getMessage()))
                .andExpect(jsonPath("status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @DisplayName("document 삭제 실패 : not create index")
    public void indexingDeleteFail3() throws Exception{
        //given
        String serviceId = "test-service";
        String sqlString = "select * members limit 1";
        String contentsIdValue = "56295";
        String[] serviceIds = new String[]{"test-service"};

        workerService.insert(Worker.builder()
                .uuid(serviceId)
                .sqlString(sqlString)
                .pkName("mem_idx")
                .tableName("members").build());
        IndexingDto indexingDto = IndexingDto.builder()
                .serviceIds(serviceIds)
                .contentsIdValue(contentsIdValue)
                .build();

        //when
        var response = this.mockMvc.perform(delete("/index/search/indexing")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON).content(indexingDto.toString()))
                .andDo(print());

        //then
        response.andExpect(status().is(400))
                .andExpect(jsonPath("message").value(ErrorMessage.NOT_EXIST_INDEX.getMessage()))
                .andExpect(jsonPath("status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @DisplayName("document 삭제 실패 : not exist index")
    public void indexingDeleteFail4() throws Exception{
        //given
        String serviceId = "test-service";
        String sqlString = "select * members limit 1";
        String contentsIdValue = "56295";
        String indexName = "test_index";
        String[] serviceIds = new String[]{"test-service"};

        workerService.insert(Worker.builder()
                .uuid(serviceId)
                .sqlString(sqlString)
                .pkName("mem_idx")
                .indexName(indexName)
                .tableName("members").build());
        IndexingDto indexingDto = IndexingDto.builder()
                .serviceIds(serviceIds)
                .contentsIdValue(contentsIdValue)
                .build();

        //when
        var response = this.mockMvc.perform(delete("/index/search/indexing")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON).content(indexingDto.toString()))
                .andDo(print());

        //then
        response.andExpect(status().is(400))
                .andExpect(jsonPath("message").value(ErrorMessage.NOT_EXIST_INDEX.getMessage()))
                .andExpect(jsonPath("status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @DisplayName("document 삭제 실패 : not exist document")
    public void indexingDeleteFail5() throws Exception{
        //given
        String indexName = "test_index";
        String serviceId = "test-service";
        String sqlString = "select * from members where mem_idx = 56295";
        String contentsIdValue = "562911";
        String[] serviceIds = new String[]{"test-service"};

        workerService.insert(Worker.builder()
                .uuid(serviceId)
                .sqlString(sqlString)
                .pkName("mem_idx")
                .tableName("members").build());
        BulkRequestDto bulkRequestDto = BulkRequestDto.builder()
                .indexName(indexName)
                .serviceId(serviceId)
                .build();
        indexService.bulkIndex(bulkRequestDto);

        IndexingDto indexingDto = IndexingDto.builder()
                .serviceIds(serviceIds)
                .contentsIdValue(contentsIdValue)
                .build();

        //when
        var response = this.mockMvc.perform(delete("/index/search/indexing")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON).content(indexingDto.toString()))
                .andDo(print());

        //then
        response.andExpect(status().is(400))
                .andExpect(jsonPath("message").value(ErrorMessage.NOT_EXIST_DOCUMENT.getMessage()))
                .andExpect(jsonPath("status").value(HttpStatus.BAD_REQUEST.value()));
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
