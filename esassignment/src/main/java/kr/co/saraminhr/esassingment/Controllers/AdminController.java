package kr.co.saraminhr.esassingment.Controllers;

import kr.co.saraminhr.esassingment.Domains.Index;
import kr.co.saraminhr.esassingment.Services.IndexService;
import kr.co.saraminhr.esassingment.Services.WorkerService;
import kr.co.saraminhr.esassingment.Services.TableService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class AdminController {

    private final TableService tableService;
    private final IndexService indexService;
    private final WorkerService workerService;

    /**
     * Home page
     * */
    @GetMapping
    public String home()  {
        return "home";
    }

    /**
     * 테이블들을 조회하여 table 로 보여주는 controller
     *
     * @param pageable 페이징을 위한 페이징 변수
     * @param model view 에 데이터를 전달하기 위한 model
     * @return ModelAndView
     * */
    @GetMapping("table")
    public ModelAndView getTables(Pageable pageable,ModelMap model){
        Page<String> page = tableService.getTables(pageable);
        model.addAttribute("tableList",page);
        return new ModelAndView("table",model);
    }



    /**
     * 사전들 (index 들)을 조회하여 table 로 보여주는 controller
     *
     * @param pageable 페이징을 위한 페이징 변수
     * @param model view 에 데이터를 전달하기 위한 model
     * @return ModelAndView
     * @throws IOException indices 조회할 때 발생할 수 있는 exception
     * */
    @GetMapping("dictionary")
    public ModelAndView getDocuments(Pageable pageable, ModelMap model) {
        Page<Index> dictionaries = indexService.getIndices(pageable);
        model.addAttribute("indexList", dictionaries);

        return new ModelAndView("dictionary",model);
    }

    /**
     * 검색 page controller
     *
     * @return ModelAndView
     * */
    @GetMapping("search")
    public ModelAndView search(){
        return new ModelAndView("search");
    }

}
