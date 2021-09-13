package kr.co.saraminhr.esassingment.Daos;

import kr.co.saraminhr.esassingment.Dtos.Highlight;
import kr.co.saraminhr.esassingment.Dtos.SearchRequestDto;
import kr.co.saraminhr.esassingment.Exceptions.BadRequestException;
import kr.co.saraminhr.esassingment.Exceptions.InternalServerException;
import kr.co.saraminhr.esassingment.Domains.Index;
import kr.co.saraminhr.esassingment.Utils.CustomStringUtil;
import kr.co.saraminhr.esassingment.Utils.ErrorMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static kr.co.saraminhr.esassingment.Utils.PageUtil.listToPage;

@Slf4j
@Repository
@RequiredArgsConstructor
public class IndexDAO implements DataDAO<Index> {

    private final RestHighLevelClient client;
    private final Pattern exceptionTypePattern = Pattern.compile("type=(?<exception>[^,]*),");

    /**
     * 모든 Index (Indices) 조회하는 메서드
     *
     * @return List
     * @throws InternalServerException 응답도중 예기치 못한 에러
     */
    @Override
    public List<Index> findAll() {
        try {
            //request
            var response = client.getLowLevelClient().performRequest(new Request("GET", "/_cat/indices?v"));

            //response
            String indices = EntityUtils.toString(response.getEntity(), "UTF-8");

            //response parse
            String[] lines = indices.split("\n");
            String[] columns = lines[0].split("\\s+");
            return stringToIndexList(columns, lines);
        } catch (IOException e) {
            throw new InternalServerException(ErrorMessage.INTERNAL_SERVER.getMessage());
        }
    }

    /**
     * 모든 Index (Indices) 조회하는 메서드
     *
     * @return List
     * @throws InternalServerException 응답도중 예기치 못한 에러
     */
    @Override
    public Page<Index> findAll(Pageable pageable) {
        try {
            //request
            var response = client.getLowLevelClient().performRequest(new Request("GET", "/_cat/indices?v"));

            //response
            String indices = EntityUtils.toString(response.getEntity(), "UTF-8");

            //response parse
            String[] lines = indices.split("\n");
            String[] columns = lines[0].split("\\s+");

            return listToPage(pageable, stringToIndexList(columns, lines));
        } catch (IOException e) {
            throw new InternalServerException(ErrorMessage.INTERNAL_SERVER.getMessage());
        }
    }

    /**
     * index 이름으로 index 조회
     *
     * @param name index 이름
     * @return GetIndexResponse
     * @throws InternalServerException 응답도중 예기치 못한 에러
     */
    public GetIndexResponse findByName(String name) {
        try {
            GetIndexRequest request = new GetIndexRequest(name);
            return client.indices().get(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new InternalServerException(ErrorMessage.INTERNAL_SERVER.getMessage());
        }
    }

    /**
     * index 삭제
     *
     * @param name index 이름
     * @return AcknowledgeResponse
     * @throws InternalServerException 응답도중 예기치 못한 에러
     * @throws BadRequestException     index 명이 없는 경우
     */
    public AcknowledgedResponse delete(String name) {
        try {
            DeleteIndexRequest request = new DeleteIndexRequest(name);
            return client.indices().delete(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new InternalServerException(ErrorMessage.INTERNAL_SERVER.getMessage());
        } catch (ElasticsearchStatusException e) {
            if (e.status().getStatus() == 404) throw new BadRequestException(ErrorMessage.NOT_EXIST_INDEX.getMessage());
            else throw new InternalServerException(ErrorMessage.INTERNAL_SERVER.getMessage());
        }
    }

    /**
     * index 존재 여부
     *
     * @param name index 이름
     * @return boolean
     * @throws InternalServerException 응답도중 예기치 못한 에러
     */
    public boolean exists(String name) {
        try {
            return client.indices().exists(new GetIndexRequest(name), RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new InternalServerException(ErrorMessage.INTERNAL_SERVER.getMessage());
        }
    }

    /**
     * index 생성
     *
     * @param indexName index 이름
     * @param fields    Map 형태의 row 들을 담은 list
     * @return CreateIndexResponse
     * @throws InternalServerException 응답도중 예기치 못한 에러
     * @throws BadRequestException     index 명이 중복되는 경우
     */
    public CreateIndexResponse create(String indexName, List<Map<String, Object>> fields) {
        try {
            CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName);

            //settings
            createIndexRequest.settings(Settings.builder()
                    .put("index.number_of_shards", 1)
                    .put("index.number_of_replicas", 0)
                    .put("index.analysis.analyzer.default_analyzer.type", "custom")
                    .put("index.analysis.analyzer.default_analyzer.tokenizer", "custom_nori")
                    .put("index.analysis.tokenizer.custom_nori.type", "nori_tokenizer")
                    .put("index.analysis.tokenizer.custom_nori.decompound_mode", "mixed"));

            //mappings
            XContentBuilder builder = XContentFactory.jsonBuilder();
            builder.startObject();
            if (!fields.isEmpty()) {
                builder.startObject("properties");
                for (Map.Entry<String, Object> entry : fields.get(0).entrySet()) {
                    builder.startObject(entry.getKey());
                    if (entry.getValue() instanceof Integer) builder.field("type", "integer");
                    else if (entry.getValue() instanceof Long) builder.field("type", "long");
                    else if (entry.getValue() instanceof Float) builder.field("type", "float");
                    else if (entry.getValue() instanceof Double) builder.field("type", "double");
                    else if (entry.getValue() instanceof Date || entry.getValue() instanceof Timestamp ||
                            entry.getValue() instanceof LocalDateTime || entry.getValue() instanceof DateTime)
                        builder.field("type", "date");
                    else {
                        builder.field("type", "text");
                        builder.field("analyzer", "nori");
                        builder.field("search_analyzer", "nori");
                    }
                    builder.endObject();
                }
                builder.endObject();
            }
            builder.endObject();
            createIndexRequest.mapping(builder);

            //response
            return client.indices().create(createIndexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new InternalServerException(ErrorMessage.INTERNAL_SERVER.getMessage());
        } catch (ElasticsearchStatusException e) {
            if (e.status().getStatus() == 400) {
                Matcher matcher = exceptionTypePattern.matcher(e.getMessage());matcher.find();
                String exception = matcher.group("exception");
                if(exception.equals("invalid_index_name_exception")) throw new BadRequestException(ErrorMessage.INVALID_INDEX_NAME.getMessage());
                else throw new BadRequestException(ErrorMessage.DUPLICATE_INDEX_NAME.getMessage());
            } else throw new InternalServerException(ErrorMessage.INTERNAL_SERVER.getMessage());
        }
    }

    /**
     * index refresh
     *
     * @param indexName index 이름
     * @return RefreshResponse
     * @throws InternalServerException 응답도중 예기치 못한 에러
     */
    public RefreshResponse refresh(String indexName) {
        try {
            RefreshRequest refreshRequest = new RefreshRequest(indexName);

            //response
            return client.indices().refresh(refreshRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new InternalServerException(ErrorMessage.INTERNAL_SERVER.getMessage());
        }
    }

    /**
     * index bulk : pk 필드가 있을 경우
     *
     * @param indexName index 이름
     * @param fields    Map 형태의 row 들을 담은 list
     * @param pkName    pk 필드명
     * @return BulkResponse
     * @throws InternalServerException 응답도중 예기치 못한 에러
     */
    public BulkResponse bulk(String indexName, List<Map<String, Object>> fields, String pkName) {
        try {
            if(fields.isEmpty()) return null;
            BulkRequest bulkRequest = new BulkRequest();
            //IndexRequest 생성
            for (Map<String, Object> field : fields) {
                for(Map.Entry<String,Object> entry : field.entrySet()){
                    if(entry.getValue() instanceof Timestamp) entry.setValue(((Timestamp) entry.getValue()).toLocalDateTime());
                    else if(entry.getValue() instanceof Date) entry.setValue(((Date) entry.getValue()).toLocalDate());
                }

                //pk가 포함된 데이터라면 pk로 doc id 설정
                if (!(field.get(pkName) instanceof String)) field.put(pkName, String.valueOf(field.get(pkName)));
                bulkRequest.add(new IndexRequest(indexName)
                        .id(field.get(pkName).toString())
                        .source(field));
            }
            //response
            return client.bulk(bulkRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new InternalServerException(ErrorMessage.INTERNAL_SERVER.getMessage());
        }
    }

    /**
     * index bulk : pk 필드가 없을 경우
     *
     * @param indexName index 이름
     * @param fields    Map 형태의 row 들을 담은 list
     * @return BulkResponse
     * @throws InternalServerException 응답도중 예기치 못한 에러
     */
    public BulkResponse bulk(String indexName, List<Map<String, Object>> fields) {
        try {
            BulkRequest bulkRequest = new BulkRequest();
            int index = 1;      //doc _id

            //IndexRequest 생성
            for (Map<String, Object> field : fields) {
                bulkRequest.add(new IndexRequest(indexName)
                        .id(String.valueOf(index))
                        .source(field));
                index++;
            }
            //response
            return client.bulk(bulkRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new InternalServerException(ErrorMessage.INTERNAL_SERVER.getMessage());
        }
    }


    /**
     * index 정보들을 한줄로 응답 받아 컬럼별로 쪼개서 Index List 만드는 메서드
     *
     * @param indexName 검색할 index 명
     * @param request   검색하기 위한 text, column 등의 정보
     * @return SearchResponse
     * @throws InternalServerException 응답도중 예기치 못한 에러
     * @throws BadRequestException     요청 파라미터가 잘못되었을 경우
     */
    public SearchResponse search(String indexName, SearchRequestDto request) {
        //indexName으로 search request생성
        SearchRequest searchRequest = new SearchRequest(indexName);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        //text not exist
        if(CustomStringUtil.isEmptyAndBlank(request.getSearchText())){
            searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        } else if( request.getResultColumns() == null || request.getResultColumns().length == 0 ){
            //text exist  & query field not exist
            searchSourceBuilder.query(QueryBuilders.queryStringQuery(request.getSearchText()));
        }else {
            //text exist & query field exist
            for (String column : request.getResultColumns()) {
                searchSourceBuilder.query(QueryBuilders.matchQuery(column, request.getSearchText()));
            }
        }

        //paging
        if (request.getPageSize() != null){
            searchSourceBuilder.size(request.getPageSize());
        }
        if(request.getPageNo() != null) {
            searchSourceBuilder.from(request.getPageNo() * request.getPageSize());
        }

        //highlighting
        if (request.getHighlight() != null && request.getHighlight().getColumns().length > 0) {
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            for (Highlight.Column column : request.getHighlight().getColumns()) {
                HighlightBuilder.Field field = new HighlightBuilder.Field(column.getColumn());
                field.preTags(request.getHighlight().getPrefixTag());
                field.postTags(request.getHighlight().getPostfixTag());
                highlightBuilder.field(field);
            }
            searchSourceBuilder.highlighter(highlightBuilder);
        }

        //request
        try {
            searchRequest.source(searchSourceBuilder);
            log.info("Search Query : {}", searchRequest.source().toString());
            return client.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new InternalServerException(ErrorMessage.INTERNAL_SERVER.getMessage(), e.getCause());
        } catch (ElasticsearchStatusException e) {
            if (e.status().getStatus() == 400)
                throw new BadRequestException(ErrorMessage.BAD_REQUEST.getMessage(), e.getCause());
            else throw new InternalServerException(ErrorMessage.INSERT_ERROR.getMessage(), e.getCause());
        }
    }

    /**
     * index 에 한개 document indexing
     *
     * @param indexName 검색할 index 명
     * @param data   검색하기 위한 text, column 등의 정보
     * @return SearchResponse
     * @throws InternalServerException 응답도중 예기치 못한 에러
     */
    public IndexResponse indexing(String indexName,String pkName, Map<String,Object> data) {
        try {
            IndexRequest request = new IndexRequest(indexName)
                    .id(data.get(pkName).toString())
                    .source(data);

            return client.index(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new InternalServerException(ErrorMessage.INTERNAL_SERVER.getMessage());
        }
    }

    /**
     * index 에 한개 document 삭제
     *
     * @param indexName 삭제 할 index 명
     * @param id 삭제할 doc id
     * @return DeleteResponse
     * @throws InternalServerException 응답도중 예기치 못한 에러
     */
    public DeleteResponse deleteDocument(String indexName, String id) {
        try {
            DeleteRequest request = new DeleteRequest(indexName,id);

            return client.delete(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new InternalServerException(ErrorMessage.INTERNAL_SERVER.getMessage());
        }
    }
    /**
     * index 정보들을 한줄로 응답 받아 컬럼별로 쪼개서 Index List 만드는 메서드
     *
     * @param fields  column 들 이름
     * @param indices index 정보들
     */
    private List<Index> stringToIndexList(String[] fields, String[] indices) {
        List<Index> indexList = new ArrayList<>();

        //index 정보를 컬럴별 분리하여 Map 으로 생성
        for (int i = 1; i < indices.length; i++) {
            Map<String, String> index = new HashMap<>();

            String[] states = indices[i].split("\\s+");
            for (int j = 0; j < states.length; j++) {
                index.put(fields[j], states[j]);
            }
            //Map 을 이룡해 static factory method 로 instance 생성
            indexList.add(Index.newInstance(index));
        }
        return indexList;
    }


}
