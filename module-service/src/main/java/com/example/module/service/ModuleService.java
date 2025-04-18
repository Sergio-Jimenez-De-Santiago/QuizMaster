package com.example.module.service;

import com.example.user.model.User;
import com.example.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.validation.Valid;
import java.util.List;

@Service
public class ModuleService {

    private final ModuleRepository moduleRepository;

    public ModuleService(ModuleRepository moduleRepository) {
        this.moduleRepository = moduleRepository;
    }

    public Module createModule(Module module) {
        return moduleRepository.save(module);
    }

    public Module getModuleById(Long id) {
        return moduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Module not found with id " + id));
    }

    public List<Module> getAllModules() {
        return moduleRepository.findAll();
    }

    public List<Module> getModulesByTeacherId(Long teacherId) {
        return moduleRepository.findByTeacherId(teacherId);
    }
}
