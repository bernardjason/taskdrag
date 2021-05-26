package org.bjason.taskdrag.database;

import org.bjason.taskdrag.apimodel.FreeTextRepository;
import org.bjason.taskdrag.apimodel.NotFoundException;
import org.bjason.taskdrag.apimodel.WorkRepository;
import org.bjason.taskdrag.model.FreeText;
import org.bjason.taskdrag.model.Work;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
class FreeTextController {

    private final FreeTextRepository repository;

    FreeTextController(FreeTextRepository repository, WorkRepository workRepository) {
        this.repository = repository;
        this.workRepository = workRepository;
    }

    private final WorkRepository workRepository;


    void checkWorkExists(Work work) {
        if (work != null) {
            Long workId = work.getId();
            if (workRepository.existsById(workId)) {
                return;
            }
        }
        throw new NotFoundException("Work item not found " + work);

    }

    @GetMapping("/freetext/{id}")
    FreeText one(@PathVariable Long id) {
        return repository.findById(id) //
                .orElseThrow(() -> new NotFoundException("freetext item " + id));
    }

    @GetMapping("/freetext")
    List<FreeText> all() {
        return repository.findAll(Sort.by(Sort.Direction.DESC,"created"));
    }


    @PostMapping("/freetext")
    FreeText newFreeText(@RequestBody FreeText freetext) {
        checkWorkExists(freetext.getWork());
        return repository.save(freetext);
    }

    @PutMapping("/freetext/{id}")
    FreeText replaceFreeText(@RequestBody FreeText newFreeText, @PathVariable Long id) {
        checkWorkExists(newFreeText.getWork());

        return repository.findById(id)
                .map(freetext -> {
                    freetext.setText(newFreeText.getText());
                    freetext.setWork(newFreeText.getWork());
                    return repository.save(freetext);
                })
                .orElseGet(() -> {
                    newFreeText.setId(id);
                    return repository.save(newFreeText);
                });
    }

    @DeleteMapping("/freetext/{id}")
    void deleteFreeText(@PathVariable Long id) {
        repository.deleteById(id);
    }

/*
    @GetMapping("/hateos/freetext/{id}")
    EntityModel<FreeText> h_one(@PathVariable Long id) {

        FreeText freetext = repository.findById(id) //
                .orElseThrow(() -> new NotFoundException("freetext item " + id));

        return EntityModel.of(freetext, //
                linkTo(methodOn(FreeTextController.class).one(id)).withSelfRel(),
                linkTo(methodOn(FreeTextController.class).all()).withRel("freetext"));
    }

    @GetMapping("/hateos/freetext")
    CollectionModel<FreeText> h_all() {

        List<FreeText> all = repository.findAll();

        for (final FreeText freetext : all) {
            Link selfLink = linkTo(methodOn(FreeTextController.class)
                    .one(freetext.getId())).withSelfRel();
            freetext.add(selfLink);
        }

        Link link = linkTo(methodOn(FreeTextController.class).all()).withSelfRel();
        CollectionModel<FreeText> result = CollectionModel.of(all, link);
        return result;
    }


    @PostMapping("/hateos/freetext")
    EntityModel<FreeText> h_newFreeText(@RequestBody FreeText freetext) {
        checkWorkExists(freetext.getWork());
        FreeText newFreeText = repository.save(freetext);
        return EntityModel.of(newFreeText, //
                linkTo(methodOn(FreeTextController.class).one(newFreeText.getId())).withSelfRel(),
                linkTo(methodOn(FreeTextController.class).all()).withRel("freetext"));
    }

    @PutMapping("/hateos/freetext/{id}")
    EntityModel<FreeText> h_replaceFreeText(@RequestBody FreeText newFreeText, @PathVariable Long id) {
        checkWorkExists(newFreeText.getWork());

        return repository.findById(id)
                .map(freetext -> {
                    freetext.setText(newFreeText.getText());
                    freetext.setWork(newFreeText.getWork());
                    repository.save(freetext);
                    return EntityModel.of(freetext, //
                            linkTo(methodOn(FreeTextController.class).one(id)).withSelfRel(),
                            linkTo(methodOn(FreeTextController.class).all()).withRel("freetext"));
                })
                .orElseGet(() -> {
                    newFreeText.setId(id);
                    repository.save(newFreeText);
                    return EntityModel.of(newFreeText, //
                            linkTo(methodOn(FreeTextController.class).one(id)).withSelfRel(),
                            linkTo(methodOn(FreeTextController.class).all()).withRel("freetext"));
                });
    }

 */
}