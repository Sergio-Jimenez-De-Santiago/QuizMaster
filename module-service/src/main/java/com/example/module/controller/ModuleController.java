package com.example.module.controller;

import com.example.user.assembler.UserDTOModelAssembler;
import com.example.user.model.User;
import com.example.user.dto.UserProfileDTO;
import com.example.user.service.UserService;

import jakarta.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ModuleController {

    private final ModuleService moduleService;

    public ModuleController(ModuleService moduleService) {
        this.moduleService = moduleService;
    }

    @PostMapping("/modules")
    public ResponseEntity<Module> createModule(@RequestBody Module module) {
        return new ResponseEntity<>(moduleService.createModule(module), HttpStatus.CREATED);
    }

    @GetMapping("/modules/{id}")
    public ResponseEntity<Module> getModuleById(@PathVariable Long id) {
        return ResponseEntity.ok(moduleService.getModuleById(id));
    }

    @GetMapping("/modules")
    public List<Module> getAllModules() {
        return moduleService.getAllModules();
    }

    @GetMapping("/modules/teacher/{teacherId}")
    public List<Module> getModulesByTeacherId(@PathVariable Long teacherId) {
        return moduleService.getModulesByTeacherId(teacherId);
    }
}