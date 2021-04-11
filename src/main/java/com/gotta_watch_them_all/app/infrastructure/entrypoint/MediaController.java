package com.gotta_watch_them_all.app.infrastructure.entrypoint;

import com.gotta_watch_them_all.app.core.entity.Media;
import com.gotta_watch_them_all.app.core.exception.AlreadyCreatedException;
import com.gotta_watch_them_all.app.core.exception.NotFoundException;
import com.gotta_watch_them_all.app.infrastructure.entrypoint.request.CreateMediaRequest;
import com.gotta_watch_them_all.app.usecase.media.AddMedia;
import com.gotta_watch_them_all.app.usecase.media.DeleteMedia;
import com.gotta_watch_them_all.app.usecase.media.FindAllMedias;
import com.gotta_watch_them_all.app.usecase.media.FindOneMedia;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.net.URI;
import java.util.List;

import static org.springframework.http.ResponseEntity.*;

@RestController
@RequestMapping("api/media")
@Validated
@RequiredArgsConstructor
public class MediaController {
    private final FindAllMedias findAllMedias;
    private final FindOneMedia findOneMedia;
    private final AddMedia addMedia;
    private final DeleteMedia deleteMedia;

    @GetMapping
    public ResponseEntity<List<Media>> findAll() {
        return ok(findAllMedias.execute());
    }

    @GetMapping("{id}")
    public ResponseEntity<Media> findById(
            @PathVariable("id")
            @Pattern(regexp = "^\\d$", message = "id has to be an integer")
            @Min(value = 1, message = "id has to be equal or more than 1") String mediaId
    ) throws NotFoundException {
        return ok(findOneMedia.execute(Long.parseLong(mediaId)));
    }

    @PostMapping
    public ResponseEntity<URI> saveOne(@Valid @RequestBody CreateMediaRequest request) throws AlreadyCreatedException {
        var newMediaId = addMedia.execute(request.getName());
        var uid = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newMediaId)
                .toUri();
        return created(uid).build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteOne(
            @PathVariable("id")
            @Pattern(regexp = "^\\d$", message = "id has to be an integer")
            @Min(value = 1, message = "id has to be equal or more than 1") String mediaId
    ) throws NotFoundException {
        deleteMedia.execute(Long.parseLong(mediaId));
        return noContent().build();
    }
}
