package kr.co.saraminhr.esassingment.ServiceTests;

import kr.co.saraminhr.esassingment.Daos.CoffeeDAO;
import kr.co.saraminhr.esassingment.Daos.IndexDAO;
import kr.co.saraminhr.esassingment.Daos.TableDAO;
import kr.co.saraminhr.esassingment.Daos.WorkerDAO;
import kr.co.saraminhr.esassingment.Domains.Index;
import kr.co.saraminhr.esassingment.Services.IndexService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class IndexServiceTest {
    @InjectMocks
    IndexService indexService;

    @Mock
    WorkerDAO workerDAO;
    @Mock
    TableDAO tableDAO;
    @Mock
    IndexDAO indexDAO;
    @Mock
    CoffeeDAO coffeeDAO;

    @Test
    @DisplayName("index들 조회 테스트")
    public void getIndicies(){
        //given
        List<Index> indices = createIndexList(20);
        when(indexDAO.findAll()).thenReturn(indices);

        //when
        List<Index> result = indexService.getIndices();

        //then
        assertAll(
                () -> assertEquals(indices.get(0),result.get(0)),
                () -> assertEquals(indices.get(1),result.get(1)),
                () -> assertEquals(20, result.size())
        );
    }

    @Test
    @DisplayName("index들 페이징 조회 테스트")
    public void getIndiciesPaging(){
        //given
        Pageable pageable = PageRequest.of(0,10);
        List<Index> indices = createIndexList(20);
        when(indexDAO.findAll()).thenReturn(indices);

        //when
        Page<Index> result = indexService.getIndices(pageable);

        //then
        assertAll(
                () -> assertEquals(2,result.getTotalPages()),
                () -> assertEquals(20,result.getTotalElements()),
                () -> assertEquals(0,result.getNumber()),
                () -> assertEquals(10,result.getSize()),
                () -> assertEquals(indices.get(0),result.get().findFirst().get())
        );
    }

    private List<Index> createIndexList(int max){
        return IntStream.range(0,max).mapToObj(i -> createIndex(String.valueOf(i))).collect(Collectors.toList());
    }

    private Index createIndex(String id){
        return Index.builder()
                .index("test-index")
                .uuid(id)
                .status("open")
                .health("green")
                .count(0)
                .replica(1)
                .size("1kb")
                .primary(1)
                .primarySize("1kb")
                .deleted(0)
                .build();
    }

}
