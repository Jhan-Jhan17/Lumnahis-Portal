package com.linhs.portal.component;

import com.linhs.portal.model.Student;
import com.linhs.portal.model.User;
import com.linhs.portal.model.Role;
import com.linhs.portal.repository.StudentRepository;
import com.linhs.portal.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.LinkedHashMap;

@Component
public class DataInitializer implements CommandLineRunner {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(StudentRepository studentRepository,
                        UserRepository userRepository,
                        PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🌱 Starting database seeding sequence...");

        // 1. Seed Test Student
        Long testLrn = 101234567890L;
        if (!studentRepository.existsById(testLrn)) {
            Student student = new Student();
            student.setLrn(testLrn);
            student.setName("John Denver Robles");
            student.setSection("Grade 12 - Azurite");
            studentRepository.save(student);
            System.out.println("✅ Test Student account seeded successfully.");
        }

        // 2. Define All 8 Department Portal Accounts
        Map<String, String> clearanceRoles = new LinkedHashMap<>();
        clearanceRoles.put("ADMIN_PRINCIPAL", "principal@linhs.edu.ph");
        clearanceRoles.put("ADVISER", "adviser@linhs.edu.ph");
        clearanceRoles.put("SPORTS_ADMIN", "sports@linhs.edu.ph");
        clearanceRoles.put("LAB_ADMIN", "lab@linhs.edu.ph");
        clearanceRoles.put("REGISTRAR", "registrar@linhs.edu.ph");
        clearanceRoles.put("GUIDANCE_COUNSELOR", "guidance@linhs.edu.ph");
        clearanceRoles.put("NURSE", "nurse@linhs.edu.ph");
        clearanceRoles.put("FACILITIES_ADMIN", "facilities@linhs.edu.ph");

        String defaultPassword = "password123";
        String encryptedPassword = passwordEncoder.encode(defaultPassword);

        for (Map.Entry<String, String> entry : clearanceRoles.entrySet()) {
            String roleStr = entry.getKey();
            String email = entry.getValue();

            if (!userRepository.existsByEmail(email)) {
                User user = new User();
                
                user.setEmail(email);
                user.setPassword(encryptedPassword);
                user.setRole(Role.valueOf(roleStr));
                
                // Assign explicit personal names based on application role
                String personalName;
                switch (roleStr) {
                    case "ADMIN_PRINCIPAL":
                        personalName = "Randy Falculan";
                        break;
                    case "ADVISER":
                        personalName = "Robert De Gula";
                        break;
                    case "SPORTS_ADMIN":
                        personalName = "Ma'am Ghenalyn Campus";
                        break;
                    case "LAB_ADMIN":
                        personalName = "Ma'am Marissa Nario";
                        break;
                    case "REGISTRAR":
                        personalName = "Ma'am Aicel Llanes";
                        break;
                    case "GUIDANCE_COUNSELOR":
                        personalName = "Ma'am Gonzales";
                        break;
                    case "NURSE":
                        personalName = "Sir Lawrence Leyesa";
                        break;
                    case "FACILITIES_ADMIN":
                        personalName = "Sir Marcel";
                        break;
                    default:
                        personalName = "LINHS Faculty";
                        break;
                }
                user.setName(personalName);
                
                // Assign matching section to Adviser to fix the redirect loop
                if (roleStr.equals("ADVISER")) {
                    user.setAssignedSection("Grade 12 - Azurite");
                } else {
                    user.setAssignedSection("None");
                }

                userRepository.save(user);
                System.out.println("✅ Seeded account: [" + roleStr + "] " + personalName + " -> " + email);
            }
        }
        System.out.println("🚀 All departmental accounts are initialized and ready!");
    }
}