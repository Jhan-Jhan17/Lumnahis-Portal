package com.linhs.portal.controller;

import com.linhs.portal.model.User;
import com.linhs.portal.model.Student;
import com.linhs.portal.model.BorrowRecord;
import com.linhs.portal.model.SportsEquipmentRecord;
import com.linhs.portal.model.GuidanceRecord;
import com.linhs.portal.repository.UserRepository;
import com.linhs.portal.repository.StudentRepository;
import com.linhs.portal.repository.BorrowRecordRepository;
import com.linhs.portal.repository.SportsEquipmentRecordRepository;
import com.linhs.portal.repository.GuidanceRecordRepository;
import com.linhs.portal.service.AuthService;
import jakarta.servlet.http.HttpSession;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class PageController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final BorrowRecordRepository borrowRecordRepository;
    private final SportsEquipmentRecordRepository sportsEquipmentRecordRepository;
    private final GuidanceRecordRepository guidanceRecordRepository;

    public PageController(AuthService authService, 
                          UserRepository userRepository, 
                          StudentRepository studentRepository,
                          BorrowRecordRepository borrowRecordRepository,
                          SportsEquipmentRecordRepository sportsEquipmentRecordRepository,
                          GuidanceRecordRepository guidanceRecordRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.borrowRecordRepository = borrowRecordRepository;
        this.sportsEquipmentRecordRepository = sportsEquipmentRecordRepository;
        this.guidanceRecordRepository = guidanceRecordRepository;
    }

    // --- PUBLIC PORTAL ROUTES ---
    @GetMapping("/") public String homePage() { return "index"; }
    @GetMapping("/about") public String aboutPage() { return "about"; }
    @GetMapping("/announcements") public String announcementsPage() { return "announcements"; }
    @GetMapping("/resources") public String resourcesPage() { return "resources"; }
    @GetMapping("/gallery") public String galleryPage() { return "gallery"; }
    @GetMapping("/clearance") public String clearancePage() { return "clearance-status"; }

    // --- PORTAL SECURITY SIGNIN CONTROLS ---
    @GetMapping("/login")
    public String catchStandardLogin(HttpSession session) {
        if (session.getAttribute("roleName") != null) {
            return "redirect:/dashboard-router";
        }
        return "redirect:/teacher-portal";
    }

    @GetMapping("/teacher-portal")
    public String loginPage(HttpSession session, 
                            @RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {
        if (error != null) {
            session.removeAttribute("roleName");
            session.removeAttribute("userName");
            session.removeAttribute("assignedSection");
            model.addAttribute("errorMessage", "Invalid email or password.");
            return "login";
        }
        if (logout != null) {
            model.addAttribute("logoutMessage", "You have been logged out successfully.");
            return "login";
        }
        if (session.getAttribute("roleName") != null) {
            return "redirect:/dashboard-router";
        }
        return "login";
    }

    // REVISED: Accepts POST form submissions targeted at either /teacher-portal or /login
    @PostMapping({"/teacher-portal", "/login"})
    public String processLogin(@RequestParam String email, @RequestParam String password, HttpSession session) {
        try {
            User user = authService.authenticate(email, password);
            if (user != null) {
                session.setAttribute("userName", user.getName());
                session.setAttribute("userEmail", user.getEmail());
                String section = "None";
                try {
                    section = user.getAssignedSection() != null ? user.getAssignedSection() : "None";
                } catch (Exception e) {}
                session.setAttribute("assignedSection", section);
                session.setAttribute("roleName", String.valueOf(user.getRole()).toUpperCase().trim());
                return "redirect:/dashboard-router";
            }
            return "redirect:/teacher-portal?error=invalid";
        } catch (Exception ex) {
            return "redirect:/teacher-portal?error=database_mismatch";
        }
    }

    @GetMapping("/dashboard-router")
    public String router(HttpSession session) {
        String role = (String) session.getAttribute("roleName");
        if (role == null) return "redirect:/teacher-portal";

        switch (role) {
            case "ADMIN_PRINCIPAL": return "redirect:/principal-dashboard";
            case "ADVISER": return "redirect:/adviser-dashboard";
            case "SPORTS_ADMIN": return "redirect:/sports-dashboard";
            case "LAB_ADMIN": return "redirect:/lab-dashboard";
            case "REGISTRAR": return "redirect:/registrar-dashboard";
            case "GUIDANCE_COUNSELOR": return "redirect:/guidance-dashboard";
            case "NURSE": return "redirect:/nurse-dashboard";
            case "FACILITIES_ADMIN": return "redirect:/facilities-dashboard";
            default: 
                session.invalidate();
                return "redirect:/teacher-portal?error=role_mismatch";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        if (session != null) session.invalidate();
        return "redirect:/teacher-portal?logout=true";
    }

    // --- SECURED GUIDANCE COUNSELOR DASHBOARD SUBSYSTEM ---
    @GetMapping("/guidance-dashboard")
    public String guidanceDashboard(HttpSession session, Model model) {
        if (!isAuthorized(session, "GUIDANCE_COUNSELOR")) return "redirect:/teacher-portal?error=unauthorized";
        populateModel(session, model);

        List<GuidanceRecord> activeCases = guidanceRecordRepository.findByIsResolvedFalse();
        model.addAttribute("activeCases", activeCases);
        model.addAttribute("activeCasesCount", activeCases.size());

        List<Student> allStudents = studentRepository.findAll();
        model.addAttribute("pendingCount", allStudents.stream().filter(s -> "PENDING".equalsIgnoreCase(s.getGuidanceClearance())).count());
        model.addAttribute("clearedCount", allStudents.stream().filter(s -> "CLEARED".equalsIgnoreCase(s.getGuidanceClearance())).count());
        model.addAttribute("totalCount", allStudents.size());
        model.addAttribute("students", allStudents);

        return "guidance-dashboard";
    }

    @PostMapping("/guidance/log-case")
    public String logGuidanceCase(@RequestParam("studentLrn") Long studentLrn,
                                  @RequestParam("infractionType") String infractionType,
                                  @RequestParam("details") String details,
                                  HttpSession session) {
        if (!isAuthorized(session, "GUIDANCE_COUNSELOR")) return "redirect:/teacher-portal?error=unauthorized";

        Optional<Student> targetedStudent = studentRepository.findById(studentLrn);
        String calculatedName = targetedStudent.isPresent() ? targetedStudent.get().getName() : "Unknown Student";

        GuidanceRecord record = new GuidanceRecord(studentLrn, calculatedName, infractionType, details, LocalDateTime.now(), false);
        guidanceRecordRepository.save(record);

        if (targetedStudent.isPresent()) {
            Student student = targetedStudent.get();
            student.setGuidanceClearance("PENDING");
            studentRepository.save(student);
        }

        return "redirect:/guidance-dashboard?success=case_logged";
    }

    @PostMapping("/guidance/resolve-case")
    public String resolveGuidanceCase(@RequestParam("recordId") Long recordId, HttpSession session) {
        if (!isAuthorized(session, "GUIDANCE_COUNSELOR")) return "redirect:/teacher-portal?error=unauthorized";

        Optional<GuidanceRecord> recordOpt = guidanceRecordRepository.findById(recordId);
        if (recordOpt.isPresent()) {
            GuidanceRecord record = recordOpt.get();
            record.setIsResolved(true);
            guidanceRecordRepository.save(record);

            List<GuidanceRecord> remainingCases = guidanceRecordRepository.findByStudentLrnAndIsResolvedFalse(record.getStudentLrn());
            if (remainingCases.isEmpty()) {
                Optional<Student> studentOpt = studentRepository.findById(record.getStudentLrn());
                if (studentOpt.isPresent()) {
                    Student student = studentOpt.get();
                    student.setGuidanceClearance("CLEARED");
                    studentRepository.save(student);
                }
            }
        }
        return "redirect:/guidance-dashboard?success=case_resolved";
    }

    // --- SECURED SPORTS ADMIN DASHBOARD SUBSYSTEM ---
    @GetMapping("/sports-dashboard")
    public String sportsDashboard(HttpSession session, Model model) {
        if (!isAuthorized(session, "SPORTS_ADMIN")) return "redirect:/teacher-portal?error=unauthorized";
        populateModel(session, model);

        List<SportsEquipmentRecord> activeBorrows = sportsEquipmentRecordRepository.findByStatus("BORROWED");
        model.addAttribute("activeBorrows", activeBorrows);
        model.addAttribute("activeBorrowsCount", activeBorrows.size());

        List<Student> allStudents = studentRepository.findAll();
        model.addAttribute("pendingCount", allStudents.stream().filter(s -> "PENDING".equalsIgnoreCase(s.getSportsClearance())).count());
        model.addAttribute("clearedCount", allStudents.stream().filter(s -> "CLEARED".equalsIgnoreCase(s.getSportsClearance())).count());
        model.addAttribute("totalCount", allStudents.size());
        model.addAttribute("students", allStudents);

        return "sports-dashboard";
    }

    @PostMapping("/sports/borrow")
    public String registerSportsBorrow(@RequestParam("lrn") Long lrn, @RequestParam("equipmentName") String equipmentName, HttpSession session) {
        if (!isAuthorized(session, "SPORTS_ADMIN")) return "redirect:/teacher-portal?error=unauthorized";
        Optional<Student> targetedStudent = studentRepository.findById(lrn);
        String calculatedName = targetedStudent.isPresent() ? targetedStudent.get().getName() : "Unknown Student";
        SportsEquipmentRecord record = new SportsEquipmentRecord(lrn, calculatedName, equipmentName, LocalDateTime.now(), "BORROWED");
        sportsEquipmentRecordRepository.save(record);
        if (targetedStudent.isPresent()) {
            Student student = targetedStudent.get();
            student.setSportsClearance("PENDING");
            studentRepository.save(student);
        }
        return "redirect:/sports-dashboard?success=item_borrowed";
    }

    @PostMapping("/sports/return")
    public String processSportsReturn(@RequestParam("recordId") Long recordId, HttpSession session) {
        if (!isAuthorized(session, "SPORTS_ADMIN")) return "redirect:/teacher-portal?error=unauthorized";
        Optional<SportsEquipmentRecord> recordOpt = sportsEquipmentRecordRepository.findById(recordId);
        if (recordOpt.isPresent()) {
            SportsEquipmentRecord record = recordOpt.get();
            record.setStatus("RETURNED");
            sportsEquipmentRecordRepository.save(record);
            List<SportsEquipmentRecord> remainingLiabilities = sportsEquipmentRecordRepository.findByLrnAndStatus(record.getLrn(), "BORROWED");
            if (remainingLiabilities.isEmpty()) {
                Optional<Student> studentOpt = studentRepository.findById(record.getLrn());
                if (studentOpt.isPresent()) {
                    Student student = studentOpt.get();
                    student.setSportsClearance("CLEARED");
                    studentRepository.save(student);
                }
            }
        }
        return "redirect:/sports-dashboard?success=item_returned";
    }

    // --- SECURED LAB ADMIN DASHBOARD SUBSYSTEM ---
    @GetMapping("/lab-dashboard")
    public String labDashboard(HttpSession session, Model model) {
        if (!isAuthorized(session, "LAB_ADMIN")) return "redirect:/teacher-portal?error=unauthorized"; 
        populateModel(session, model);
        List<BorrowRecord> activeBorrows = borrowRecordRepository.findByStatus("BORROWED");
        model.addAttribute("activeBorrows", activeBorrows);
        model.addAttribute("activeBorrowsCount", activeBorrows.size());
        List<Student> allStudents = studentRepository.findAll();
        model.addAttribute("pendingCount", allStudents.stream().filter(s -> "PENDING".equalsIgnoreCase(s.getLabClearance())).count());
        model.addAttribute("clearedCount", allStudents.stream().filter(s -> "CLEARED".equalsIgnoreCase(s.getLabClearance())).count());
        model.addAttribute("totalCount", allStudents.size());
        model.addAttribute("students", allStudents);
        return "lab-dashboard";
    }

    @PostMapping("/lab/borrow")
    public String registerBorrowRecord(@RequestParam("lrn") Long lrn, 
                                       @RequestParam("equipmentName") String equipmentName, 
                                       HttpSession session,
                                       RedirectAttributes redirectAttributes) {
        if (!isAuthorized(session, "LAB_ADMIN")) return "redirect:/teacher-portal?error=unauthorized";
        Optional<Student> targetedStudent = studentRepository.findById(lrn);
        String calculatedName = targetedStudent.isPresent() ? targetedStudent.get().getName() : "Unknown Student";
        BorrowRecord record = new BorrowRecord(lrn, calculatedName, equipmentName, LocalDateTime.now(), "BORROWED");
        borrowRecordRepository.save(record);
        if (targetedStudent.isPresent()) {
            Student student = targetedStudent.get();
            student.setLabClearance("PENDING");
            studentRepository.save(student);
        }
        return "redirect:/lab-dashboard?success=item_borrowed";
    }

    @PostMapping("/lab/return")
    public String processReturnTransaction(@RequestParam("recordId") Long recordId, HttpSession session) {
        if (!isAuthorized(session, "LAB_ADMIN")) return "redirect:/teacher-portal?error=unauthorized";
        Optional<BorrowRecord> recordOpt = borrowRecordRepository.findById(recordId);
        if (recordOpt.isPresent()) {
            BorrowRecord record = recordOpt.get();
            record.setStatus("RETURNED"); 
            borrowRecordRepository.save(record);
            List<BorrowRecord> remainingLiabilities = borrowRecordRepository.findByLrnAndStatus(record.getLrn(), "BORROWED");
            if (remainingLiabilities.isEmpty()) {
                Optional<Student> studentOpt = studentRepository.findById(record.getLrn());
                if (studentOpt.isPresent()) {
                    Student student = studentOpt.get();
                    student.setLabClearance("CLEARED"); 
                    studentRepository.save(student);
                }
            }
        }
        return "redirect:/lab-dashboard?success=item_returned";
    }

    @PostMapping("/lab/update-clearance")
    public String updateLabClearance(@RequestParam("lrn") Long lrn, @RequestParam("status") String status, HttpSession session) {
        if (!isAuthorized(session, "LAB_ADMIN")) return "redirect:/teacher-portal?error=unauthorized";
        Optional<Student> studentOpt = studentRepository.findById(lrn);
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            student.setLabClearance(status.toUpperCase().trim());
            studentRepository.save(student);
        }
        return "redirect:/lab-dashboard?success=clearance_updated";
    }

    // --- OTHER DASHBOARDS (Adviser, Principal) ---
    @GetMapping("/adviser-dashboard")
    public String adviserDashboard(HttpSession session, Model model) {
        if (!isAuthorized(session, "ADVISER")) return "redirect:/teacher-portal?error=unauthorized";
        populateModel(session, model);
        String assignedSection = (String) session.getAttribute("assignedSection");
        List<Student> classroomStudents = studentRepository.findAll().stream()
                .filter(student -> student.getSection() != null && student.getSection().equalsIgnoreCase(assignedSection))
                .collect(Collectors.toList());
        model.addAttribute("classroomStudents", classroomStudents);
        model.addAttribute("classCount", classroomStudents.size());
        return "adviser-dashboard";
    }

    @PostMapping("/adviser/add-student")
    public String adviserAddStudent(@RequestParam("lrn") Long lrn, @RequestParam("name") String name, HttpSession session) {
        if (!isAuthorized(session, "ADVISER")) return "redirect:/teacher-portal?error=unauthorized";
        Student student = new Student();
        student.setLrn(lrn);
        student.setName(name.trim());
        student.setSection((String) session.getAttribute("assignedSection"));
        student.setAdviserClearance("PENDING");
        student.setLabClearance("PENDING");
        student.setSportsClearance("PENDING");
        student.setGuidanceClearance("PENDING");
        studentRepository.save(student);
        return "redirect:/adviser-dashboard?success=student_added";
    }

    @PostMapping("/adviser/update-clearance")
    public String updateStudentClearance(@RequestParam("lrn") Long lrn, @RequestParam("status") String status, HttpSession session) {
        if (!isAuthorized(session, "ADVISER")) return "redirect:/teacher-portal?error=unauthorized";
        Optional<Student> studentOpt = studentRepository.findById(lrn);
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            student.setAdviserClearance(status);
            studentRepository.save(student);
        }
        return "redirect:/adviser-dashboard?success=clearance_updated";
    }

    @GetMapping("/principal-dashboard")
    public String principalDashboard(HttpSession session, Model model) {
        if (!isAuthorized(session, "ADMIN_PRINCIPAL")) return "redirect:/teacher-portal?error=unauthorized";
        populateModel(session, model);
        model.addAttribute("facultyList", userRepository.findAll());
        return "principal-dashboard";
    }

    @PostMapping("/principal/edit-faculty")
    public String editFacultyAccount(@RequestParam("id") @NonNull Long id, @RequestParam("name") String name, @RequestParam("email") String email, @RequestParam("assignedSection") String assignedSection, HttpSession session) {
        if (!isAuthorized(session, "ADMIN_PRINCIPAL")) return "redirect:/teacher-portal?error=unauthorized";
        User user = userRepository.findById(id).orElseThrow();
        user.setName(name);
        user.setEmail(email);
        user.setAssignedSection(assignedSection.trim().isEmpty() ? "Not Assigned" : assignedSection);
        userRepository.save(user);
        return "redirect:/principal-dashboard";
    }

    private boolean isAuthorized(HttpSession session, String requiredRole) {
        String role = (String) session.getAttribute("roleName");
        return role != null && role.equals(requiredRole);
    }

    private void populateModel(HttpSession session, Model model) {
        model.addAttribute("userName", session.getAttribute("userName"));
        model.addAttribute("userEmail", session.getAttribute("userEmail"));
        model.addAttribute("roleName", session.getAttribute("roleName"));
    }
}