package org.bjason.taskdrag.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.hateoas.RepresentationModel;

import java.util.Date;
import java.util.Objects;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public
class FreeText extends RepresentationModel<Work> {

    private @Id @GeneratedValue Long id;
    @CreatedDate
    private Date created= new Date();
    private String text;
    //@OneToOne(cascade = CascadeType.ALL)
    @OneToOne()
    @JoinColumn(name = "work_id", referencedColumnName = "id")
    private Work work;

    public FreeText() {}

    public FreeText(String text, Work work) {
        this.text = text;
        this.work = work;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Work getWork() {
        return work;
    }

    public void setWork(Work work) {
        this.work = work;
    }

    @Override
    public String toString() {
        return "FreeText{" +
                "id=" + id +
                ", created=" + created +
                ", text='" + text + '\'' +
                ", work=" + work +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FreeText)) return false;
        FreeText freeText = (FreeText) o;
        return Objects.equals(id, freeText.id) && Objects.equals(created, freeText.created) && Objects.equals(text, freeText.text) && Objects.equals(work, freeText.work);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, created, text, work);
    }
}
