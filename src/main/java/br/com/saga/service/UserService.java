package br.com.saga.service;

import br.com.saga.controller.UserController;
import br.com.saga.domain.entity.RoleEntity;
import br.com.saga.domain.entity.UserEntity;
import br.com.saga.dto.request.UpdateUserRequest;
import br.com.saga.dto.response.UserResponse;
import br.com.saga.exception.ResourceNotFoundException;
import br.com.saga.mapper.UserMapper;
import br.com.saga.repository.RoleRepository;
import br.com.saga.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public Page<UserResponse> findAll(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::toResponseWithLinks);
    }

    public UserResponse findById(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + id));
        return toResponseWithLinks(user);
    }

    @Transactional
    public UserResponse update(Long id, UpdateUserRequest request) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + id));

        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getEnabled() != null) {
            user.setEnabled(request.getEnabled());
        }
        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            Set<RoleEntity> roles = new HashSet<>();
            for (String roleName : request.getRoles()) {
                RoleEntity role = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new ResourceNotFoundException("Role não encontrada: " + roleName));
                roles.add(role);
            }
            user.setRoles(roles);
        }

        userRepository.save(user);
        return toResponseWithLinks(user);
    }

    @Transactional
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuário não encontrado: " + id);
        }
        userRepository.deleteById(id);
    }

    private UserResponse toResponseWithLinks(UserEntity user) {
        UserResponse response = userMapper.toResponse(user);
        response.add(WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(UserController.class).findById(user.getId())).withSelfRel());
        response.add(WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(UserController.class).findAll(null)).withRel("users"));
        return response;
    }
}
