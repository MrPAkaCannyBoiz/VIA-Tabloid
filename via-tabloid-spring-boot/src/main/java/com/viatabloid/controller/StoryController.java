package com.viatabloid.controller;

import com.viatabloid.dto.StoryDto;
import com.viatabloid.entities.StoryEntity;
import com.viatabloid.repositories.DepartmentRepository;
import com.viatabloid.repositories.StoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
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
        // for testing purpose, we will just return the first story
        var stories = storyRepository.findAll();
        List<StoryDto> storyDtos = stories.stream()
                .map(story -> new StoryDto(story.getTitle(),
                        story.getDescription(),
                        story.getDepartment().getId()))
                .toList();
        return storyDtos;
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
            // return 404
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
        catch (Exception e)
        {
            // return 500
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createStory(@RequestBody StoryDto storyDto)
    {
        try
        {
            var department = departmentRepository
                    .findById(storyDto.departmentId())
                    .orElse(null);
            if (department == null) throw new NullPointerException();
            var newStory = new StoryEntity(storyDto.title()
                    ,storyDto.description()
                    ,department);
            newStory = storyRepository.save(newStory);
            return ResponseEntity.ok(mapToDto(newStory));
        }
        catch (NullPointerException e)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Department not found with id: " + storyDto.departmentId());
        }
    }

    private StoryDto mapToDto(StoryEntity storyEntity)
    {
        return new StoryDto(storyEntity.getTitle(),
                storyEntity.getDescription(),
                storyEntity.getDepartment().getId());
    }

}
