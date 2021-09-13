package kr.co.saraminhr.esassingment.Controllers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kr.co.saraminhr.esassingment.Domains.Worker;
import kr.co.saraminhr.esassingment.Dtos.BulkRequestDto;
import kr.co.saraminhr.esassingment.Domains.Index;
import kr.co.saraminhr.esassingment.Dtos.IndexingDto;
import kr.co.saraminhr.esassingment.Dtos.SearchRequestDto;
import kr.co.saraminhr.esassingment.Dtos.SearchResponseDto;
import kr.co.saraminhr.esassingment.Exceptions.BadRequestException;
import kr.co.saraminhr.esassingment.Services.CoffeeService;
import kr.co.saraminhr.esassingment.Services.IndexService;
import kr.co.saraminhr.esassingment.Services.TableService;
import kr.co.saraminhr.esassingment.Services.WorkerService;
import kr.co.saraminhr.esassingment.Utils.ApiError;
import kr.co.saraminhr.esassingment.Utils.CustomStringUtil;
import kr.co.saraminhr.esassingment.Utils.ErrorMessage;
import kr.co.saraminhr.esassingment.Utils.OrderChecks;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;


@Slf4j
@RestController
@RequestMapping(value = "/index", produces="application/json;charset=UTF-8")
@RequiredArgsConstructor
public class IndexController {

    private final IndexService indexService;
    private final WorkerService workerService;
    private final TableService tableService;
    private final CoffeeService coffeeService;

    /**
     * 검색엔진 내 모든 index 정보 조회
     * @return ResponseEntity
     * */
    @GetMapping("/search/indices")
    public ResponseEntity getIndices() {
        //request
        List<Index> indices = indexService.getIndices();

        JsonObject jsonObject = new JsonObject();
        jsonObject.add("indices", JsonParser.parseString(indices.toString()));

        return ResponseEntity.ok(jsonObject.toString());
    }

    /**
     * index bulk
     *
     * @param  request bulk 위한 요청 dto
     * @param  errors @Valid 로 유효성검사후 실패한 경우 error잡기 위함
     * @return ResponseEntity
     * */
    @PostMapping("/bulk")
    public ResponseEntity bulkIndex(@Validated({OrderChecks.class}) @RequestBody BulkRequestDto request, BindingResult errors)  {
        //validate
        if(errors.hasErrors()){
            Map<String,String> validateResult = ApiError.validateRequest(errors);
            return ResponseEntity.badRequest().body(new ApiError(validateResult, HttpStatus.BAD_REQUEST));
        }

        //request
        indexService.bulkIndex(request);

        return ResponseEntity.ok("");
    }


    /**
     * index 검색
     *
     * @param  request 검색 위한 요청 dto
     * @param  errors @Valid 로 유효성검사후 실패한 경우 error잡기 위함
     * @return ResponseEntity
     * */
    @PostMapping("/search/contents")
    public ResponseEntity search(@Validated({OrderChecks.class}) @RequestBody SearchRequestDto request, BindingResult errors)  {
        //validate
        if(errors.hasErrors()){
            Map<String,String> validateResult = ApiError.validateRequest(errors);
            return ResponseEntity.badRequest().body(new ApiError(validateResult, HttpStatus.BAD_REQUEST));
        }

        //service id로 index 조회
        Optional<Worker> optionalWorker = workerService.getWorker(request.getServiceId());
        if(optionalWorker.isEmpty() ){
            throw new BadRequestException(ErrorMessage.NOT_EXIST_SERVICE_ID.getMessage());
        }
        Worker worker = optionalWorker.get();
        if(CustomStringUtil.isEmptyAndBlank(worker.getIndexName())){
            throw new BadRequestException(ErrorMessage.NOT_EXIST_INDEX.getMessage());
        }

        //index 명을 가지고 검색
        SearchResponseDto response = indexService.getDocuments(worker.getIndexName(), request);
        if(response.getResult().length == 0) return ResponseEntity.noContent().build();

        return ResponseEntity.ok(response);
    }

    /**
     * index 부분 indexing (create / update)
     *
     * @param  request indexing 위한 요청 dto
     * @param  errors @Valid 로 유효성검사후 실패한 경우 error잡기 위함
     * @return ResponseEntity
     * */
    @RequestMapping(method = {RequestMethod.POST,RequestMethod.PUT},  value="/search/indexing" )
    public ResponseEntity saveDocument(@Validated({OrderChecks.class}) @RequestBody IndexingDto request, BindingResult errors){
        //validate
        if(errors.hasErrors()){
            Map<String,String> validateResult = ApiError.validateRequest(errors);
            return ResponseEntity.badRequest().body(new ApiError(validateResult, HttpStatus.BAD_REQUEST));
        }

        //service get
        List<Worker> workerList = workerService.getWorkers(request.getServiceIds());
        if(workerList.isEmpty()) throw new BadRequestException(ErrorMessage.NOT_EXIST_SERVICE_ID.getMessage());
        for(Worker worker : workerList){
            if(CustomStringUtil.isEmptyAndBlank(worker.getIndexName())) throw new BadRequestException(ErrorMessage.NOT_EXIST_INDEX.getMessage());
        }

        //doc create & update
        for(Worker worker : workerList){
            Map<String,Object> data;
            //data get
            if("coffee".equals(worker.getTableName())) data = coffeeService.getData(request.getContentsIdValue());
            else data = tableService.getData(worker.getTableName(),worker.getPkName(),request.getContentsIdValue());

            indexService.saveDocument(worker,data);
        }

        return ResponseEntity.ok("");
    }

    /**
     * 부분 indexing (delete
     *
     * @param  request indexing (delete) 위한 요청 dto
     * @param  errors @Valid 로 유효성검사후 실패한 경우 error잡기 위함
     * @return ResponseEntity
     * */
    @DeleteMapping( "/search/indexing" )
    public ResponseEntity deleteDocument(@Validated({OrderChecks.class}) @RequestBody IndexingDto request, BindingResult errors){
        //validate
        if(errors.hasErrors()){
            Map<String,String> validateResult = ApiError.validateRequest(errors);
            return ResponseEntity.badRequest().body(new ApiError(validateResult, HttpStatus.BAD_REQUEST));
        }

        //service get
        List<Worker> workerList = workerService.getWorkers(request.getServiceIds());
        if(workerList.isEmpty()) throw new BadRequestException(ErrorMessage.NOT_EXIST_SERVICE_ID.getMessage());
        for(Worker worker : workerList){
            if(CustomStringUtil.isEmptyAndBlank(worker.getIndexName())) throw new BadRequestException(ErrorMessage.NOT_EXIST_INDEX.getMessage());
        }

        //doc delete
        for(Worker worker : workerList){
            indexService.deleteDocument(worker,request.getContentsIdValue());
        }

        return ResponseEntity.ok("");
    }
}