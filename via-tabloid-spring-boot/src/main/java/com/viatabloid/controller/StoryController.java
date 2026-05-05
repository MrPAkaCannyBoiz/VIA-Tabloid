package com.viatabloid.controller;

import com.viatabloid.dto.StoryDto;
import com.viatabloid.entities.StoryEntity;
import com.viatabloid.repositories.DepartmentRepository;
import com.viatabloid.repositories.StoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/stories")
public class StoryController
{

    private final DepartmentRepository departmentRepository;
    private final StoryRepository storyRepository;

    @Autowired
    public StoryController(DepartmentRepository departmentRepository, StoryRepository storyRepository)
    {
        this.departmentRepository = departmentRepository;
        this.storyRepository = storyRepository;
    }

    @GetMapping()
    public List<StoryDto> getAllStories()
    {
        var stories = storyRepository.findAll();
        return stories.stream()
                .map(this::mapToDto)
                .toList();
    }

    @GetMapping("/{storyId}")
    public ResponseEntity<?> getStoryById(@PathVariable int storyId)
    {
        try
        {
            var story = storyRepository.findById(storyId);
            var storyDto = story.map(this::mapToDto).orElseThrow(
                    () -> new IllegalArgumentException("Story not found with id: " + storyId));
            return ResponseEntity.ok(storyDto);
        }
        catch (IllegalArgumentException e)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createStory(@RequestBody StoryDto storyDto)
    {
        var department = departmentRepository.findById(storyDto.departmentId()).orElse(null);
        if (department == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Department not found with id: " + storyDto.departmentId());
        var newStory = new StoryEntity(storyDto.title(), storyDto.description(), department);
        return ResponseEntity.ok(mapToDto(storyRepository.save(newStory)));
    }

    @PutMapping(value = "/{storyId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateStory(@PathVariable int storyId, @RequestBody StoryDto storyDto)
    {
        var existing = storyRepository.findById(storyId).orElse(null);
        if (existing == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Story not found with id: " + storyId);
        var department = departmentRepository.findById(storyDto.departmentId()).orElse(null);
        if (department == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Department not found with id: " + storyDto.departmentId());
        existing.setTitle(storyDto.title());
        existing.setDescription(storyDto.description());
        existing.setDepartment(department);
        return ResponseEntity.ok(mapToDto(storyRepository.save(existing)));
    }

    @DeleteMapping("/{storyId}")
    public ResponseEntity<?> deleteStory(@PathVariable int storyId)
    {
        if (!storyRepository.existsById(storyId))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Story not found with id: " + storyId);
        storyRepository.deleteById(storyId);
        return ResponseEntity.noContent().build();
    }

    private StoryDto mapToDto(StoryEntity storyEntity)
    {
        return new StoryDto(storyEntity.getId(),
                storyEntity.getTitle(),
                storyEntity.getDescription(),
                storyEntity.getDepartment().getId());
    }
}
