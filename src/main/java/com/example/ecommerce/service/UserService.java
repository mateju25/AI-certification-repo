package com.example.ecommerce.service;

import com.example.ecommerce.dto.user.UserRequestDTO;
import com.example.ecommerce.dto.user.UserResponseDTO;
import com.example.ecommerce.dto.user.UserUpdateDTO;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.exception.DuplicateResourceException;
import com.example.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponseDTO createUser(UserRequestDTO requestDTO) {
        if (userRepository.existsByEmail(requestDTO.getEmail())) {
            throw new DuplicateResourceException("Email already exists: " + requestDTO.getEmail());
        }

        User user = new User();
        user.setName(requestDTO.getName());
        user.setEmail(requestDTO.getEmail());
        user.setPassword(passwordEncoder.encode(requestDTO.getPassword()));

        User savedUser = userRepository.save(user);
        return mapToResponseDTO(savedUser);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return mapToResponseDTO(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserResponseDTO updateUser(Long id, UserUpdateDTO updateDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (updateDTO.getName() != null) {
            user.setName(updateDTO.getName());
        }

        if (updateDTO.getEmail() != null) {
            if (!user.getEmail().equals(updateDTO.getEmail())
                    && userRepository.existsByEmail(updateDTO.getEmail())) {
                throw new DuplicateResourceException("Email already exists: " + updateDTO.getEmail());
            }
            user.setEmail(updateDTO.getEmail());
        }

        if (updateDTO.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(updateDTO.getPassword()));
        }

        User updatedUser = userRepository.save(user);
        return mapToResponseDTO(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    private UserResponseDTO mapToResponseDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }
}
