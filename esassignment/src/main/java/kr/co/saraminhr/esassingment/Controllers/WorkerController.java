package kr.co.saraminhr.esassingment.Controllers;

import kr.co.saraminhr.esassingment.Domains.Worker;
import kr.co.saraminhr.esassingment.Dtos.WorkerRequestDto;
import kr.co.saraminhr.esassingment.Services.WorkerService;
import kr.co.saraminhr.esassingment.Utils.ApiError;
import kr.co.saraminhr.esassingment.Utils.OrderChecks;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Controller
@RequestMapping("/service")
@RequiredArgsConstructor
public class WorkerController {
    private final WorkerService workerService;

    /**
     * service 조회하여 table 로 보여주는 controller
     *
     * @param pageable 페이징을 위한 페이징 변수
     * @param model view 에 데이터를 전달하기 위한 model
     * @return ModelAndView
     * */
    @GetMapping
    public ModelAndView getServices(Pageable pageable, ModelMap model){
        Page<Worker> page = workerService.getWorkers(pageable);
        model.addAttribute("serviceList",page);
        return new ModelAndView("service",model);
    }


    /**
     * service 생성 page controller
     *
     * @return View
     * */
    @GetMapping("/create")
    public String createServiceView(){
        return "createService";
    }

    /**
     * service 생성 로직 controller
     *
     * @param request 생성 요청을 위한 dto
     * @param errors @Valid 로 유효성검사후 실패시 error처리 위한 변수
     * @return View
     * */
    @PostMapping("/create")
    public ResponseEntity createService(@Validated({OrderChecks.class}) @RequestBody WorkerRequestDto request, BindingResult errors){
        //유효성 검사 실패
        if(errors.hasErrors()){
            Map<String,String> validateResult = ApiError.validateRequest(errors);
            return ResponseEntity.badRequest().body(new ApiError(validateResult, HttpStatus.BAD_REQUEST));
        }

        //table name get
        Worker worker = workerService.getWorkerWithTableNameAndPKName(request);

        //insert
        workerService.insert(worker);
        return ResponseEntity.ok("");
    }

    /**
     * service 삭제 controller
     *
     * @param serviceId 삭제할 서비스 id
     * @return ResponseEntity
     * */
    @DeleteMapping("/{serviceId}")
    public ResponseEntity deleteWorker(@PathVariable String serviceId){
        workerService.delete(serviceId);

        return ResponseEntity.ok("");
    }
}
