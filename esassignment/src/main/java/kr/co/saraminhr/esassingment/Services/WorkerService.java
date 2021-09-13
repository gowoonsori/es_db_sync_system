package kr.co.saraminhr.esassingment.Services;

import kr.co.saraminhr.esassingment.Daos.IndexDAO;
import kr.co.saraminhr.esassingment.Daos.TableDAO;
import kr.co.saraminhr.esassingment.Domains.Worker;
import kr.co.saraminhr.esassingment.Daos.WorkerDAO;
import kr.co.saraminhr.esassingment.Dtos.WorkerRequestDto;
import kr.co.saraminhr.esassingment.Exceptions.BadRequestException;
import kr.co.saraminhr.esassingment.Utils.CustomStringUtil;
import kr.co.saraminhr.esassingment.Utils.ErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional
public class WorkerService {
    private Pattern tablePattern = Pattern.compile("(select )[0-9_a-zA-Z, *]* from (?<table>[0-9a-zA-Z_]*).*");
    private final WorkerDAO workerDAO;
    private final TableDAO tableDAO;
    private final IndexDAO indexDAO;

    /**
     * 서비스들을 페이징 조회하는 메서드
     *
     * @param pageable pagination 조회를 위한 pageable 변수
     * @return Page
     * */
    @Transactional(readOnly = true)
    public Page<Worker> getWorkers(Pageable pageable){
        return workerDAO.findAll(pageable);
    }

    /**
     *서비스 한개 조회
     *
     * @param serviceId 조회할 서비스 id
     * @return Optional
     * */
    @Transactional(readOnly = true)
    public Optional<Worker> getWorker(String serviceId){
        return workerDAO.findByServiceId(serviceId);
    }

    /**
     *서비스 한개 조회
     *
     * @param serviceIds 조회할 서비스 id
     * @return Optional
     * */
    @Transactional(readOnly = true)
    public List<Worker> getWorkers(String[] serviceIds){
        return workerDAO.findByServiceIds(serviceIds);
    }

    /**
     * 서비스 생성 메서드
     *
     * @param worker 생성할 서비스
     * */
    public void insert(Worker worker){
        workerDAO.insert(worker);
    }

    /**
     * 서비스 삭제 메서드
     *  서비스 삭제시 해당 인덱스도 삭제
     *
     * @param serviceId 삭제할 서비스 id
     * */
    public void delete(String serviceId){
        //service get
        Optional<Worker> optionalWorker = workerDAO.findByServiceId(serviceId);
        if(optionalWorker.isEmpty()) throw new BadRequestException(ErrorMessage.NOT_EXIST_SERVICE_ID.getMessage());
        Worker worker = optionalWorker.get();

        //service delete
        workerDAO.delete(worker);

        //index delete
        if(CustomStringUtil.isNotEmptyAndBlank(worker.getIndexName()) && indexDAO.exists(worker.getIndexName())){
            indexDAO.delete(worker.getIndexName());
        }
    }

    /**
     * Worker Request에서 sql문을 통해 table이름과 pk명을 찾아 Worker반환해주는 메서드
     *
     * @param requestDto 요청 dto
     * @return Worker */
    @Transactional(readOnly = true)
    public Worker getWorkerWithTableNameAndPKName(WorkerRequestDto requestDto){
        //get PK field name
        Matcher matcher = tablePattern.matcher(requestDto.getSqlString()); matcher.find();
        String tableName = matcher.group("table");
        String pkName;
        if("coffee".equals(tableName)) pkName = workerDAO.findPKName(tableName);
        else pkName = tableDAO.findPKName(tableName);
        if(!requestDto.isExistPk(pkName)) throw new BadRequestException(ErrorMessage.NOT_EXIST_PK.getMessage());

        return Worker.builder()
                .uuid(requestDto.getUuid())
                .sqlString(requestDto.getSqlString())
                .tableName(tableName)
                .pkName(pkName)
                .build();
    }
}
