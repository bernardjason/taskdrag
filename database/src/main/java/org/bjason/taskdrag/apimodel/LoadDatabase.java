package org.bjason.taskdrag.apimodel;

import org.bjason.taskdrag.model.TaskStates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(TaskStatesRepository taskStatesRepository) {

        return args -> {
            if ( taskStatesRepository.findAll().size() == 0 ) {
                log.info("*************** Creating default states ******************");
                taskStatesRepository.save(new TaskStates(1, "created", "#ffb3b3"));
                taskStatesRepository.save(new TaskStates(2, "inprogress", "#ccffcc"));
                taskStatesRepository.save(new TaskStates(3, "waiting", "#ff8888"));
                taskStatesRepository.save(new TaskStates(4, "done", "#ffff99"));
            }
            log.info("*************** Creating default states ******************" + taskStatesRepository.findAll().size());

        };
    }
}

