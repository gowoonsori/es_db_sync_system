package kr.co.saraminhr.esassingment.Services;

import kr.co.saraminhr.esassingment.Daos.CoffeeDAO;
import kr.co.saraminhr.esassingment.Dtos.BulkRequestDto;
import kr.co.saraminhr.esassingment.Domains.Index;
import kr.co.saraminhr.esassingment.Domains.Worker;
import kr.co.saraminhr.esassingment.Daos.IndexDAO;
import kr.co.saraminhr.esassingment.Daos.TableDAO;
import kr.co.saraminhr.esassingment.Daos.WorkerDAO;
import kr.co.saraminhr.esassingment.Dtos.DocumentSource;
import kr.co.saraminhr.esassingment.Dtos.SearchRequestDto;
import kr.co.saraminhr.esassingment.Dtos.SearchResponseDto;
import kr.co.saraminhr.esassingment.Exceptions.BadRequestException;
import kr.co.saraminhr.esassingment.Utils.CustomStringUtil;
import kr.co.saraminhr.esassingment.Utils.ErrorMessage;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static kr.co.saraminhr.esassingment.Utils.PageUtil.listToPage;

@Service
@RequiredArgsConstructor
@Transactional
public class IndexService {

    private final IndexDAO indexDAO;
    private final WorkerDAO workerDAO;
    private final TableDAO tableDAO;
    private final CoffeeDAO coffeeDAO;

    /**
     * Indices 를 조회하여 모두 반환하는 메서드
     *
     * @return List
     * */
    @Transactional(readOnly = true)
    public List<Index> getIndices() {
        return indexDAO.findAll();
    }


    /**
     * Indices 를 조회하여 page 로 변환하여 반환해주는 메서드
     *
     * @param pageable 페이징을 위한 페이징 변수
     * @return Page
     * */
    @Transactional(readOnly = true)
    public Page<Index> getIndices(Pageable pageable){
        List<Index> indices =  indexDAO.findAll();
        return listToPage(pageable, indices);
    }

    /**
     * BulkRequest(service id, index name) 을 이용해
     * service의 sql문을 index name으로 index생성하여 bulk하는 메서드
     *
     * @param request
     *       - serviceId : SQL문을 조회하기 위한 서비스 id
     *       - indexName : 생성할 index 이름
     * @return BulkResponse
     * */
    public BulkResponse bulkIndex(BulkRequestDto request){
        //service id 로 worker 조회
        Optional<Worker> optionalWorker = workerDAO.findByServiceId(request.getServiceId());
        if(optionalWorker.isEmpty())throw new BadRequestException(ErrorMessage.NOT_EXIST_SERVICE_ID.getMessage());

        //worker 에서 sql get
        Worker worker = optionalWorker.get();
        String sqlString = worker.getSqlString();
        String indexName = worker.getIndexName(); //index name get

        //sql 문 실행하여 data 조회
        List<Map<String,Object>> data;
        if("coffee".equals(worker.getTableName()))  data = coffeeDAO.findBySQL(sqlString);
        else data = tableDAO.findBySQL(sqlString);

        //worker 에 index 이름 정보 update
        worker.setIndexName(request.getIndexName());
        workerDAO.updateIndexName(worker);

        //기존의 index 존재하면 삭제
        if(CustomStringUtil.isNotEmptyAndBlank(indexName) && indexDAO.exists(indexName)) {
            indexDAO.delete(indexName);
        }

        //index 생성
        indexDAO.create(request.getIndexName(),data);

        //bulk
        return indexDAO.bulk(request.getIndexName(),data,worker.getPkName());
    }

    /**
     * documents 조회 메서드
     *
     * @param indexName 검색할 index 명
     * @param request 검색하기 위한 text, column 등의 정보
     * */
    public SearchResponseDto getDocuments(String indexName, SearchRequestDto request){
        //index query
        SearchResponse response = indexDAO.search(indexName,request);

        //source parsing
        return  SearchResponseDto.builder()
                .total(response.getHits().getTotalHits().value)
                .result(Arrays.stream(response.getHits().getHits()).map(DocumentSource::fromSearchHit).toArray(DocumentSource[]::new))
                .page(request.getPageNo())
                .pageSize(request.getPageSize())
                .build();
    }


    /**
     * document 삽입 메서드
     *  - document 없는 경웃 : 생성
     *  - document 있는 경우 : 수정
     * @param worker indexing 할 index 명 과 pk 등의 정보
     * @param data indexing 할 데이터
     * @return IndexResponse
     * */
    public IndexResponse saveDocument(Worker worker, Map<String,Object> data){
        //index가 존재하지 않는 경우
        if(!indexDAO.exists(worker.getIndexName())) throw new BadRequestException(ErrorMessage.NOT_EXIST_INDEX.getMessage());
        return indexDAO.indexing(worker.getIndexName(),worker.getPkName(),data);
    }

    /**
     * document 삭제 메서드
     *  - document 없는 경웃 : 생성
     *  - document 있는 경우 : 수정
     * @param worker 삭제 할 index 명 과 pk 등의 정보
     * @param id 삭제할 doc id
     * @return IndexResponse
     * */
    public DeleteResponse deleteDocument(Worker worker, String id){
        //index가 존재하지 않는 경우
        if(!indexDAO.exists(worker.getIndexName())) throw new BadRequestException(ErrorMessage.NOT_EXIST_INDEX.getMessage());

        //delete
        DeleteResponse response =  indexDAO.deleteDocument(worker.getIndexName(),id);

        //document가 존재하지 않는 경우
        if(response.getResult() == DocWriteResponse.Result.NOT_FOUND) throw new BadRequestException(ErrorMessage.NOT_EXIST_DOCUMENT.getMessage());

        return response;
    }
}
