package org.bjason.taskdrag.apimodel;

import org.bjason.taskdrag.model.Work;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkRepository extends JpaRepository<Work, Long> {

    Work findTopDisplayOrderByStatusOrderByDisplayOrderDesc(String status);

    List<Work> findByStatusOrAssignedTo(String status, String assignedTo, Sort sort);

}