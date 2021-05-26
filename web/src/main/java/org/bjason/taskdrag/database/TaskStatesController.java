package org.bjason.taskdrag.database;

import org.bjason.taskdrag.apimodel.TaskStatesRepository;
import org.bjason.taskdrag.model.TaskStates;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
class TaskStatesController {

    private final TaskStatesRepository repository;

    TaskStatesController(TaskStatesRepository repository) {
        this.repository = repository;
    }

    @Bean(name = "allStates")
    public List<String> getAllStateValues() {
        ensureSetupWithSomething();
       return  repository.findAll(Sort.by(Sort.Direction.ASC,"displayOrder"))
        .stream().map(TaskStates::getStatus).collect(Collectors.toList());
    }

    @Bean(name = "getStyle")
    public String getStyle() {
        ensureSetupWithSomething();
        StringBuilder sb = new StringBuilder();
        sb.append("<style>");
        for(TaskStates ts :repository.findAll()){
            sb.append("."+ts.getStatus()+"{ background-color:"+ts.getColour()+";}") ;
            sb.append("\n");
        }
        sb.append("</style>");
        return sb.toString();
    }


    @GetMapping("/taskstates")
    List<TaskStates> all() {
        return repository.findAll(Sort.by(Sort.Direction.ASC,"displayOrder"));
    }

    void ensureSetupWithSomething() {
        if ( repository.findAll().size() == 0 ) {
            repository.save(new TaskStates(1, "created", "#ffb3b3"));
            repository.save(new TaskStates(2, "inprogress", "#ccffcc"));
            repository.save(new TaskStates(3, "waiting", "#ff8888"));
            repository.save(new TaskStates(4, "done", "#ffff99"));
        }
    }

}