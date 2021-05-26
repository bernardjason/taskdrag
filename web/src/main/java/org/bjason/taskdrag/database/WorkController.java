package org.bjason.taskdrag.database;

import java.util.List;
import java.util.Optional;

import org.bjason.taskdrag.apimodel.NotFoundException;
import org.bjason.taskdrag.apimodel.WorkRepository;
import org.bjason.taskdrag.model.Work;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
class WorkController {

    private final WorkRepository repository;

    WorkController(WorkRepository repository) {
        this.repository = repository;
    }

    private void changeOwnerShipToCurrentUser(Work work) {
        SecurityContext sc = SecurityContextHolder.getContext();
        org.springframework.security.core.userdetails.User principle = (User) ((UsernamePasswordAuthenticationToken) sc.getAuthentication()).getPrincipal();
        work.setAssignedTo(principle.getUsername());
    }

    @GetMapping("/work/{id}")
    Work one(@PathVariable Long id) {
        return repository.findById(id) //
                .orElseThrow(() -> new NotFoundException("work item "+id));

    }
    @GetMapping("/work")
    List<Work> all() {
        SecurityContext sc = SecurityContextHolder.getContext();
        org.springframework.security.core.userdetails.User principle = (User) ((UsernamePasswordAuthenticationToken) sc.getAuthentication()).getPrincipal();
        String user = principle.getUsername();

        //return repository.findAll(Sort.by("status").and(Sort.by(Sort.Direction.DESC,"displayOrder")));
        return repository.findByStatusOrAssignedTo("created",user,
                Sort.by("status").and(Sort.by(Sort.Direction.DESC,"displayOrder")));
    }

    @DeleteMapping("/work/{id}")
    void deleteWork(@PathVariable Long id) {
        repository.deleteById(id);
    }

    @PostMapping("/work")
    Work newWork(@RequestBody Work work) {
        long maxDisplayOrder = 0;
        Work max = repository.findTopDisplayOrderByStatusOrderByDisplayOrderDesc(work.getStatus());
        if ( max != null ) {
            maxDisplayOrder= max.getDisplayOrder();
        }
        work.setDisplayOrder(maxDisplayOrder+1);
        return repository.save(work);
    }

    @PutMapping("/work/{id}")
    Work  replaceWork(@RequestBody Work newWork, @PathVariable Long id) {

        changeOwnerShipToCurrentUser(newWork);
        return repository.findById(id)
                .map(work -> {
                    work.setDisplayOrder(newWork.getDisplayOrder());
                    work.setStatus(newWork.getStatus());
                    work.setTitle(newWork.getTitle());
                    return repository.save(work);
                })
                .orElseGet(() -> {
                    newWork.setId(id);
                    return repository.save(newWork);
                });
    }
    @PutMapping("/work/{id}/{status}")
    Work  replaceStatus(@PathVariable Long id, @PathVariable String status, @RequestParam Optional<Long> displayOrder) {
        return repository.findById(id)
                .map(work -> {
                    work.setDisplayOrder(displayOrder.orElse(work.getDisplayOrder()));
                    changeOwnerShipToCurrentUser(work);
                    work.setStatus(status);
                    return repository.save(work);
                })
                .orElseThrow(() -> new NotFoundException("work item "+id));
    }

/*
    @PostMapping("/hateos/work")
    EntityModel<Work> h_newWork(@RequestBody Work work) {
        Work newWork = repository.save(work);
        return EntityModel.of(newWork, //
                linkTo(methodOn(WorkController.class).one(newWork.getId())).withSelfRel(),
                linkTo(methodOn(WorkController.class).all()).withRel("work"));
    }

    @PutMapping("/hateos/work/{id}")
    EntityModel<Work>  h_replaceWork(@RequestBody Work newWork, @PathVariable Long id) {

        return repository.findById(id)
                .map(work -> {
                    work.setDisplayOrder(newWork.getDisplayOrder());
                    work.setStatus(newWork.getStatus());
                    work.setTitle(newWork.getTitle());
                    repository.save(work);
                    return EntityModel.of(work, //
                            linkTo(methodOn(WorkController.class).one(id)).withSelfRel(),
                            linkTo(methodOn(WorkController.class).all()).withRel("work"));
                })
                .orElseGet(() -> {
                    newWork.setId(id);
                    repository.save(newWork);
                    return EntityModel.of(newWork, //
                            linkTo(methodOn(WorkController.class).one(id)).withSelfRel(),
                            linkTo(methodOn(WorkController.class).all()).withRel("work"));
                });
    }

    @GetMapping("/hateos/work")
    CollectionModel<Work> h_all() {

        List<Work> all = repository.findAll(Sort.by("status").and(Sort.by(Sort.Direction.DESC,"displayOrder")));

        for (final Work work : all) {
            Link selfLink = linkTo(methodOn(WorkController.class)
                    .one(work.getId())).withSelfRel();
            work.add(selfLink);
        }

        Link link = linkTo(methodOn(WorkController.class).all()).withSelfRel();
        CollectionModel<Work> result = CollectionModel.of(all, link);
        return result;
    }
    @GetMapping("/hateos/work/{id}")
    EntityModel<Work> h_one(@PathVariable Long id) {

        Work work = repository.findById(id) //
                .orElseThrow(() -> new NotFoundException("work item "+id));

        return EntityModel.of(work, //
                linkTo(methodOn(WorkController.class).one(id)).withSelfRel(),
                linkTo(methodOn(WorkController.class).all()).withRel("work"));
    }

 */
}