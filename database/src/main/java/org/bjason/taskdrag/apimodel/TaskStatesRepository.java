package org.bjason.taskdrag.apimodel;

import org.bjason.taskdrag.model.TaskStates;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskStatesRepository extends JpaRepository<TaskStates, Long> {

}