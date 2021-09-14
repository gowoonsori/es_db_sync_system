package kr.co.saraminhr.esassingment.DAOTests;

import kr.co.saraminhr.esassingment.Daos.IndexDAO;
import kr.co.saraminhr.esassingment.Dtos.SearchRequestDto;
import kr.co.saraminhr.esassingment.Exceptions.BadRequestException;
import kr.co.saraminhr.esassingment.Exceptions.InternalServerException;
import kr.co.saraminhr.esassingment.Utils.ErrorMessage;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.*;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.rest.RestStatus;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
public class IndexDAOIOExceptionTest {
    @InjectMocks
    IndexDAO indexDAO;

    @Mock
    RestHighLevelClient client;

    @Test()
    @DisplayName("index 모두 조회 실패 테스트 : IOException")
    void findAllFailTest() throws IOException {
        //given
        RestClient restClient = mock(RestClient.class);
        when(client.getLowLevelClient()).thenReturn(restClient);
        when(client.getLowLevelClient().performRequest(any(Request.class))).thenThrow(IOException.class);

        //when
        Throwable exception = assertThrows(InternalServerException.class,
                () ->indexDAO.findAll()
        );

        //then
        Assertions.assertEquals(ErrorMessage.INTERNAL_SERVER.getMessage(),exception.getMessage());
    }

    @Test()
    @DisplayName("index 페이징 모두 조회 실패 테스트 : IOException")
    void findAllPagingFailTest() throws IOException {
        //given
        RestClient restClient = mock(RestClient.class);
        when(client.getLowLevelClient()).thenReturn(restClient);
        when(client.getLowLevelClient().performRequest(any(Request.class))).thenThrow(IOException.class);
        Pageable pageable = PageRequest.of(0,10);

        //when
        Throwable exception = assertThrows(InternalServerException.class,
                () ->indexDAO.findAll(pageable)
        );

        //then
        Assertions.assertEquals(ErrorMessage.INTERNAL_SERVER.getMessage(),exception.getMessage());
    }

    @Test()
    @DisplayName("index 페이징 모두 조회 실패 테스트 : IOException")
    void getIndexResponseFailTest() throws IOException {
        //given
        IndicesClient indicesClient = mock(IndicesClient.class);
        when(client.indices()).thenReturn(indicesClient);
        when(client.indices().get(any(GetIndexRequest.class), any(RequestOptions.class))).thenThrow(IOException.class);
        String indexName ="test";

        //when
        Throwable exception = assertThrows(InternalServerException.class,
                () ->indexDAO.findByName(indexName)
        );

        //then
        Assertions.assertEquals(ErrorMessage.INTERNAL_SERVER.getMessage(),exception.getMessage());
    }

    @Test()
    @DisplayName("index 삭제 실패 테스트 : IOException")
    void deleteFailTest() throws IOException {
        //given
        IndicesClient indicesClient = mock(IndicesClient.class);
        when(client.indices()).thenReturn(indicesClient);
        when(client.indices().delete(any(DeleteIndexRequest.class), any(RequestOptions.class))).thenThrow(IOException.class);
        String indexName ="test";

        //when
        Throwable exception = assertThrows(InternalServerException.class,
                () ->indexDAO.delete(indexName)
        );

        //then
        Assertions.assertEquals(ErrorMessage.INTERNAL_SERVER.getMessage(),exception.getMessage());
    }

    @Test()
    @DisplayName("index 삭제 실패 테스트 : ElasticsearchStatusException not 404")
    void deleteFailTest2() throws IOException {
        //given
        IndicesClient indicesClient = mock(IndicesClient.class);
        when(client.indices()).thenReturn(indicesClient);
        ElasticsearchStatusException elasticsearchStatusException = new ElasticsearchStatusException("bad request", RestStatus.BAD_REQUEST);
        when(client.indices().delete(any(DeleteIndexRequest.class), any(RequestOptions.class))).thenThrow(elasticsearchStatusException);
        String indexName ="test";

        //when
        Throwable exception = assertThrows(InternalServerException.class,
                () ->indexDAO.delete(indexName)
        );

        //then
        Assertions.assertEquals(ErrorMessage.INTERNAL_SERVER.getMessage(),exception.getMessage());
    }

    @Test()
    @DisplayName("index 존재하는지 확인 실패 테스트 : IOException")
    void existFailTest() throws IOException {
        //given
        IndicesClient indicesClient = mock(IndicesClient.class);
        when(client.indices()).thenReturn(indicesClient);
        when(client.indices().exists(any(GetIndexRequest.class), any(RequestOptions.class))).thenThrow(IOException.class);
        String indexName ="test";

        //when
        Throwable exception = assertThrows(InternalServerException.class,
                () ->indexDAO.exists(indexName)
        );

        //then
        Assertions.assertEquals(ErrorMessage.INTERNAL_SERVER.getMessage(),exception.getMessage());
    }

    @Test()
    @DisplayName("index 생성 실패 테스트 : ElasticsearchStatusException not 404")
    void createFailTest2() throws IOException {
        //given
        IndicesClient indicesClient = mock(IndicesClient.class);
        when(client.indices()).thenReturn(indicesClient);
        ElasticsearchStatusException elasticsearchStatusException = new ElasticsearchStatusException("bad request", RestStatus.NOT_FOUND);
        when(client.indices().create(any(CreateIndexRequest.class), any(RequestOptions.class))).thenThrow(elasticsearchStatusException);
        String indexName = "test_index";
        List<Map<String,Object>> dataList = new ArrayList<>();
        dataList.add(new HashMap<>());

        //when
        Throwable exception = assertThrows(InternalServerException.class,
                () ->indexDAO.create(indexName,dataList)
        );

        //then
        Assertions.assertEquals(ErrorMessage.INTERNAL_SERVER.getMessage(),exception.getMessage());
    }

    @Test()
    @DisplayName("index 생성 실패 테스트 : ElasticsearchStatusException not 400(Invalid index name)")
    void createFailTest3() throws IOException {
        //given
        IndicesClient indicesClient = mock(IndicesClient.class);
        when(client.indices()).thenReturn(indicesClient);
        ElasticsearchStatusException elasticsearchStatusException = new ElasticsearchStatusException("type=invalid_index_name_exception,", RestStatus.BAD_REQUEST);
        when(client.indices().create(any(CreateIndexRequest.class), any(RequestOptions.class))).thenThrow(elasticsearchStatusException);
        String indexName = "test_index";
        List<Map<String,Object>> dataList = new ArrayList<>();
        dataList.add(new HashMap<>());

        //when
        Throwable exception = assertThrows(BadRequestException.class,
                () ->indexDAO.create(indexName,dataList)
        );

        //then
        Assertions.assertEquals(ErrorMessage.INVALID_INDEX_NAME.getMessage(),exception.getMessage());
    }

    @Test()
    @DisplayName("index 생성 메서드 실패 테스트 : IOException")
    void createFailTest() throws IOException {
        //given
        IndicesClient indicesClient = mock(IndicesClient.class);
        when(client.indices()).thenReturn(indicesClient);
        when(client.indices().create(any(CreateIndexRequest.class), any(RequestOptions.class))).thenThrow(IOException.class);
        String indexName = "test_index";
        List<Map<String,Object>> dataList = new ArrayList<>();
        dataList.add(new HashMap<>());

        //when
        Throwable exception = assertThrows(InternalServerException.class,
                () ->indexDAO.create(indexName,dataList)
        );

        //then
        Assertions.assertEquals(ErrorMessage.INTERNAL_SERVER.getMessage(),exception.getMessage());
    }

    @Test()
    @DisplayName("index refrsh 메서드 실패 테스트 : IOException")
    void refreshFailTest() throws IOException {
        //given
        IndicesClient indicesClient = mock(IndicesClient.class);
        when(client.indices()).thenReturn(indicesClient);
        when(client.indices().refresh(any(RefreshRequest.class), any(RequestOptions.class))).thenThrow(IOException.class);
        String indexName = "test_index";

        //when
        Throwable exception = assertThrows(InternalServerException.class,
                () ->indexDAO.refresh(indexName)
        );

        //then
        Assertions.assertEquals(ErrorMessage.INTERNAL_SERVER.getMessage(),exception.getMessage());
    }

    @Test()
    @DisplayName("index bulk 메서드 실패 테스트 : IOException")
    void bulkFailTest() throws IOException {
        //given
        when(client.bulk(any(BulkRequest.class), any(RequestOptions.class))).thenThrow(IOException.class);
        String indexName = "test_index";
        List<Map<String,Object>> data = List.of(Map.of("id",1));

        //when
        Throwable exception = assertThrows(InternalServerException.class,
                () ->indexDAO.bulk(indexName,data)
        );

        //then
        Assertions.assertEquals(ErrorMessage.INTERNAL_SERVER.getMessage(),exception.getMessage());
    }

    @Test()
    @DisplayName("index bulk 메서드 실패 테스트 : IOException")
    void bulkFailTest2() throws IOException {
        //given
        when(client.bulk(any(BulkRequest.class), any(RequestOptions.class))).thenThrow(IOException.class);
        String indexName = "test_index";
        List<Map<String,Object>> data = List.of(Map.of("id","1"));
        String pkName ="id";

        //when
        Throwable exception = assertThrows(InternalServerException.class,
                () ->indexDAO.bulk(indexName,data,pkName)
        );

        //then
        Assertions.assertEquals(ErrorMessage.INTERNAL_SERVER.getMessage(),exception.getMessage());
    }

    @Test()
    @DisplayName("index search 메서드 실패 테스트 : IOException")
    void searchFailTest() throws IOException {
        //given
        when(client.search(any(SearchRequest.class), any(RequestOptions.class))).thenThrow(IOException.class);
        String indexName = "test_index";
        SearchRequestDto request = SearchRequestDto.builder().serviceId("test").build();

        //when
        Throwable exception = assertThrows(InternalServerException.class,
                () ->indexDAO.search(indexName,request)
        );

        //then
        Assertions.assertEquals(ErrorMessage.INTERNAL_SERVER.getMessage(),exception.getMessage());
    }

    @Test()
    @DisplayName("index search 메서드 실패 테스트 : BadRequest")
    void searchFailTest2() throws IOException {
        //given
        ElasticsearchStatusException statusException = new ElasticsearchStatusException("fail",RestStatus.BAD_REQUEST);
        when(client.search(any(SearchRequest.class), any(RequestOptions.class))).thenThrow(statusException);
        String indexName = "test_index";
        SearchRequestDto request = SearchRequestDto.builder().serviceId("test").build();

        //when
        Throwable exception = assertThrows(BadRequestException.class,
                () ->indexDAO.search(indexName,request)
        );

        //then
        Assertions.assertEquals(ErrorMessage.BAD_REQUEST.getMessage(),exception.getMessage());
    }

    @Test()
    @DisplayName("index search 메서드 실패 테스트 : 404")
    void searchFailTest3() throws IOException {
        //given
        ElasticsearchStatusException statusException = new ElasticsearchStatusException("fail",RestStatus.NOT_FOUND);
        when(client.search(any(SearchRequest.class), any(RequestOptions.class))).thenThrow(statusException);
        String indexName = "test_index";
        SearchRequestDto request = SearchRequestDto.builder().serviceId("test").build();

        //when
        Throwable exception = assertThrows(InternalServerException.class,
                () ->indexDAO.search(indexName,request)
        );

        //then
        Assertions.assertEquals(ErrorMessage.INSERT_ERROR.getMessage(),exception.getMessage());
    }

    @Test()
    @DisplayName("index indexing 메서드 실패 테스트 : IOException")
    void indexingFailTest() throws IOException {
        //given
        when(client.index(any(IndexRequest.class), any(RequestOptions.class))).thenThrow(IOException.class);
        String indexName = "test_index";
        Map<String,Object> data = Map.of("id","1");
        String pkName ="id";

        //when
        Throwable exception = assertThrows(InternalServerException.class,
                () ->indexDAO.indexing(indexName,pkName,data)
        );

        //then
        Assertions.assertEquals(ErrorMessage.INTERNAL_SERVER.getMessage(),exception.getMessage());
    }

    @Test()
    @DisplayName("document delete 메서드 실패 테스트 : IOException")
    void deleteDocumentFailTest() throws IOException {
        //given
        when(client.delete(any(DeleteRequest.class), any(RequestOptions.class))).thenThrow(IOException.class);
        String indexName = "test_index";
        String id ="1";

        //when
        Throwable exception = assertThrows(InternalServerException.class,
                () ->indexDAO.deleteDocument(indexName,id)
        );

        //then
        Assertions.assertEquals(ErrorMessage.INTERNAL_SERVER.getMessage(),exception.getMessage());
    }
}
