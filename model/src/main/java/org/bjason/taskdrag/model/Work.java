package org.bjason.taskdrag.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.hateoas.RepresentationModel;

import java.util.Date;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class Work extends RepresentationModel<Work> {

    private @Id @GeneratedValue Long id;
    private long displayOrder;
    private String status;
    @CreatedDate
    private Date created= new Date();

    private String assignedTo;
    private String title;

    public Work() {
    }
    public Work(long displayOrder, String status, String title) {
        this.displayOrder = displayOrder;
        this.status = status;
        this.title = title;
        this.assignedTo=null;
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

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDisplayOrder(long displayOrder) {
            this.displayOrder = displayOrder;
        }

    public long getDisplayOrder() {
        return displayOrder;
    }
    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Work)) return false;
        if (!super.equals(o)) return false;
        Work work = (Work) o;
        return displayOrder == work.displayOrder && Objects.equals(id, work.id) && Objects.equals(status, work.status) && Objects.equals(created, work.created) && Objects.equals(assignedTo, work.assignedTo) && Objects.equals(title, work.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, displayOrder, status, created, assignedTo, title);
    }

    @Override
    public String toString() {
        return "Work{" +
                "id=" + id +
                ", displayOrder=" + displayOrder +
                ", status='" + status + '\'' +
                ", created=" + created +
                ", assignedTo='" + assignedTo + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}

