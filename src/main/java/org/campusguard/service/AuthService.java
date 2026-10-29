package org.campusguard.service;

import org.campusguard.audit.AuditService;
import org.campusguard.dto.JwtResponse;
import org.campusguard.dto.LoginRequest;
import org.campusguard.dto.RegisterRequest;
import org.campusguard.dto.UserDto;
import org.campusguard.dto.StudentProfileDto;
import org.campusguard.entity.Department;
import org.campusguard.entity.StudentProfile;
import org.campusguard.entity.TrustScore;
import org.campusguard.entity.User;
import org.campusguard.entity.enums.AuditAction;
import org.campusguard.entity.enums.Role;
import org.campusguard.exception.DuplicateResourceException;
import org.campusguard.exception.ResourceNotFoundException;
import org.campusguard.exception.UnauthorizedException;
import org.campusguard.repository.DepartmentRepository;
import org.campusguard.repository.StudentProfileRepository;
import org.campusguard.repository.TrustScoreRepository;
import org.campusguard.repository.UserRepository;
import org.campusguard.security.CustomUserDetails;
import org.campusguard.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final DepartmentRepository departmentRepository;
    private final TrustScoreRepository trustScoreRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final AuditService auditService;

    public AuthService(UserRepository userRepository,
                       StudentProfileRepository studentProfileRepository,
                       DepartmentRepository departmentRepository,
                       TrustScoreRepository trustScoreRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtTokenProvider tokenProvider,
                       AuditService auditService) {
        this.userRepository = userRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.departmentRepository = departmentRepository;
        this.trustScoreRepository = trustScoreRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.auditService = auditService;
    }

    @Transactional
    public UserDto registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("College email " + request.getEmail() + " is already registered.");
        }

        User user = new User(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getFullName(),
                request.getRole() != null ? request.getRole() : Role.STUDENT
        );

        User savedUser = userRepository.save(user);

        // Initialize Trust Score
        TrustScore trustScore = new TrustScore(savedUser, 100);
        trustScoreRepository.save(trustScore);

        StudentProfileDto profileDto = null;
        if (savedUser.getRole() == Role.STUDENT) {
            if (request.getStudentId() != null && studentProfileRepository.existsByStudentId(request.getStudentId())) {
                throw new DuplicateResourceException("Student ID " + request.getStudentId() + " already exists.");
            }

            Department department = null;
            if (request.getDepartmentId() != null) {
                department = departmentRepository.findById(request.getDepartmentId())
                        .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
            } else {
                department = departmentRepository.findAll().stream().findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("No departments available"));
            }

            StudentProfile profile = new StudentProfile(
                    savedUser,
                    request.getStudentId() != null ? request.getStudentId() : "SRM" + System.currentTimeMillis() % 100000,
                    department,
                    request.getYear() != null ? request.getYear() : "1st Year",
                    request.getSection() != null ? request.getSection() : "A",
                    request.getPhoneNumber()
            );

            StudentProfile savedProfile = studentProfileRepository.save(profile);
            profileDto = mapProfileToDto(savedProfile);
        }

        auditService.logAction(savedUser, AuditAction.LOGIN, "User", savedUser.getId(), "User Registered", null);

        return mapUserToDto(savedUser, profileDto);
    }

    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.generateToken(authentication);

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            User user = userRepository.findByEmail(userDetails.getEmail())
                    .orElseThrow(() -> new UnauthorizedException("User not found"));

            auditService.logAction(user, AuditAction.LOGIN, "User", user.getId(), "User Logged In", null);

            return new JwtResponse(jwt, userDetails.getId(), userDetails.getEmail(), userDetails.getFullName(), userDetails.getRole().name());
        } catch (Exception ex) {
            throw new UnauthorizedException("Invalid college email or password.");
        }
    }

    public User getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new UnauthorizedException("User is not authenticated");
        }
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }

    public UserDto getCurrentUserDto() {
        User user = getCurrentAuthenticatedUser();
        StudentProfileDto profileDto = null;
        if (user.getStudentProfile() != null) {
            profileDto = mapProfileToDto(user.getStudentProfile());
        }
        return mapUserToDto(user, profileDto);
    }

    public UserDto mapUserToDto(User user, StudentProfileDto profileDto) {
        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole(),
                user.getEnabled(),
                profileDto
        );
    }

    public StudentProfileDto mapProfileToDto(StudentProfile profile) {
        if (profile == null) return null;
        return new StudentProfileDto(
                profile.getStudentId(),
                profile.getDepartment() != null ? profile.getDepartment().getCode() : "",
                profile.getDepartment() != null ? profile.getDepartment().getName() : "",
                profile.getYear(),
                profile.getSection(),
                profile.getPhoneNumber()
        );
    }
}
