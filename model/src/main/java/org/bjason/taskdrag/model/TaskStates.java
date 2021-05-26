package org.bjason.taskdrag.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class TaskStates extends RepresentationModel<TaskStates> {

    private @Id @GeneratedValue Long id;
    private long displayOrder;
    private String status;
    private String colour;


    public TaskStates() {
    }
    public TaskStates(long displayOrder, String status,String colour) {
        this.displayOrder = displayOrder;
        this.status = status;
        this.colour=colour;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(long displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskStates)) return false;
        if (!super.equals(o)) return false;
        TaskStates that = (TaskStates) o;
        return displayOrder == that.displayOrder && Objects.equals(id, that.id) && Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, displayOrder, status);
    }

    @Override
    public String toString() {
        return "TaskStates{" +
                "id=" + id +
                ", displayOrder=" + displayOrder +
                ", status='" + status + '\'' +
                ", colour='" + colour + '\'' +
                '}';
    }
}

