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

}