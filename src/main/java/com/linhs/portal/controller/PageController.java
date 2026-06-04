package com.linhs.portal.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.linhs.portal.model.BorrowRecord;
import com.linhs.portal.model.ClinicLog;
import com.linhs.portal.model.DocumentRequest;
import com.linhs.portal.model.FacilityLiability;
import com.linhs.portal.model.GuidanceRecord;
import com.linhs.portal.model.SportsEquipmentRecord;
import com.linhs.portal.model.Student;
import com.linhs.portal.model.User;
import com.linhs.portal.repository.BorrowRecordRepository;
import com.linhs.portal.repository.ClinicLogRepository;
import com.linhs.portal.repository.DocumentRequestRepository;
import com.linhs.portal.repository.FacilityLiabilityRepository;
import com.linhs.portal.repository.GuidanceRecordRepository;
import com.linhs.portal.repository.SportsEquipmentRecordRepository;
import com.linhs.portal.repository.StudentRepository;
import com.linhs.portal.repository.UserRepository;
import com.linhs.portal.service.AuthService;

import jakarta.servlet.http.HttpSession;

@Controller
public class PageController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final BorrowRecordRepository borrowRecordRepository;
    private final SportsEquipmentRecordRepository sportsEquipmentRecordRepository;
    private final GuidanceRecordRepository guidanceRecordRepository;
    private final DocumentRequestRepository documentRequestRepository;
    private final ClinicLogRepository clinicLogRepository;
    private final FacilityLiabilityRepository facilityLiabilityRepository;

    @Autowired
    private com.linhs.portal.repository.AnnouncementRepository announcementRepository;

    public PageController(AuthService authService, 
                          UserRepository userRepository, 
                          StudentRepository studentRepository,
                          BorrowRecordRepository borrowRecordRepository,
                          SportsEquipmentRecordRepository sportsEquipmentRecordRepository,
                          GuidanceRecordRepository guidanceRecordRepository,
                          DocumentRequestRepository documentRequestRepository,
                          ClinicLogRepository clinicLogRepository,
                          FacilityLiabilityRepository facilityLiabilityRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.borrowRecordRepository = borrowRecordRepository;
        this.sportsEquipmentRecordRepository = sportsEquipmentRecordRepository;
        this.guidanceRecordRepository = guidanceRecordRepository;
        this.documentRequestRepository = documentRequestRepository;
        this.clinicLogRepository = clinicLogRepository;
        this.facilityLiabilityRepository = facilityLiabilityRepository;
    }

    // --- PUBLIC PORTAL ROUTES ---
    @GetMapping("/") public String homePage() { return "index"; }
    @GetMapping("/about") public String aboutPage() { return "about"; }
    @GetMapping("/announcements") public String announcementsPage() { return "announcements"; }
    @GetMapping("/resources") public String resourcesPage() { return "resources"; }
    @GetMapping("/gallery") public String galleryPage() { return "gallery"; }
    @GetMapping("/clearance") public String clearancePage() { return "clearance-status"; }

    @GetMapping("/requests")
    public String requestsPage() {
        return "requests"; 
    }

    // --- UNIFIED SEARCH & LOOKUP MANAGEMENT SYSTEM ---

    @PostMapping("/search-clearance")
    public String searchClearance(@RequestParam("lrn") Long lrn, Model model) {
        return executeStudentLookup(lrn, model);
    }

    @GetMapping("/clearance/lookup")
    public String checkClearance(@RequestParam(value = "lrn", required = false) Long lrn, Model model) {
        if (lrn == null) {
            return "redirect:/clearance";
        }
        return executeStudentLookup(lrn, model);
    }

    /**
     * Helper method to centralize validation logic, catch runtime errors, 
     * and apply default "PENDING" strings to null clearance fields to prevent 500 errors.
     */
    private String executeStudentLookup(Long lrn, Model model) {
        try {
            Optional<Student> studentOpt = studentRepository.findById(lrn);
            if (studentOpt.isPresent()) {
                Student student = studentOpt.get();
                
                // CRITICAL SAFETY BALANCING: If any profile values are completely blank (null) in the DB ledger, 
                // replace them with "PENDING" so Thymeleaf string manipulation methods do not throw NullPointerExceptions.
                if (student.getAdviserClearance() == null) student.setAdviserClearance("PENDING");
                if (student.getLabClearance() == null) student.setLabClearance("PENDING");
                if (student.getSportsClearance() == null) student.setSportsClearance("PENDING");
                if (student.getGuidanceClearance() == null) student.setGuidanceClearance("PENDING");
                
                model.addAttribute("student", student);
            } else {
                model.addAttribute("errorMessage", "No student record found for LRN: " + lrn);
            }
        } catch (Exception e) {
            model.addAttribute("errorMessage", "An unexpected tracking system anomaly occurred: " + e.getMessage());
        }
        // Both mapping routes now render safely to clearance-status view
        return "clearance-status";
    }

    // Public Endpoint Mocking Landing Page Document Request Action Submission
    @PostMapping("/public/request-document")
    public String publicRequestDocument(@RequestParam("lrn") Long lrn, @RequestParam("documentType") String documentType) {
        Optional<Student> studentOpt = studentRepository.findById(lrn);
        String name = studentOpt.isPresent() ? studentOpt.get().getName() : "Unknown Public Applicant";
        DocumentRequest req = new DocumentRequest(lrn, name, documentType, LocalDateTime.now(), false);
        documentRequestRepository.save(req);
        return "redirect:/clearance?success=document_requested";
    }

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

    // --- SECURED REGISTRAR DASHBOARD SUBSYSTEM ---
    @GetMapping("/registrar-dashboard")
    public String registrarDashboard(HttpSession session, Model model) {
        if (!isAuthorized(session, "REGISTRAR")) return "redirect:/teacher-portal?error=unauthorized";
        populateModel(session, model);
        
        List<DocumentRequest> incomingRequests = documentRequestRepository.findByIsCompletedFalse();
        model.addAttribute("incomingRequests", incomingRequests);
        model.addAttribute("incomingRequestsCount", incomingRequests.size());
        
        List<Student> allStudents = studentRepository.findAll();
        model.addAttribute("students", allStudents);
        model.addAttribute("totalCount", allStudents.size());
        return "registrar-dashboard";
    }

    @PostMapping("/registrar/complete-request")
    public String completeDocumentRequest(@RequestParam("requestId") Long requestId, HttpSession session) {
        if (!isAuthorized(session, "REGISTRAR")) return "redirect:/teacher-portal?error=unauthorized";
        Optional<DocumentRequest> reqOpt = documentRequestRepository.findById(requestId);
        if (reqOpt.isPresent()) {
            DocumentRequest req = reqOpt.get();
            req.setIsCompleted(true);
            documentRequestRepository.save(req);
        }
        return "redirect:/registrar-dashboard?success=request_completed";
    }

    // --- SECURED NURSE DASHBOARD SUBSYSTEM ---
    @GetMapping("/nurse-dashboard")
    public String nurseDashboard(HttpSession session, Model model) {
        if (!isAuthorized(session, "NURSE")) return "redirect:/teacher-portal?error=unauthorized";
        populateModel(session, model);
        
        List<ClinicLog> logs = clinicLogRepository.findAll();
        model.addAttribute("clinicLogs", logs);
        model.addAttribute("clinicLogsCount", logs.size());
        
        List<Student> allStudents = studentRepository.findAll();
        model.addAttribute("students", allStudents);
        return "nurse-dashboard";
    }

    @PostMapping("/nurse/log-visit")
    public String logClinicVisit(@RequestParam("studentLrn") Long studentLrn, @RequestParam("reason") String reason, HttpSession session) {
        if (!isAuthorized(session, "NURSE")) return "redirect:/teacher-portal?error=unauthorized";
        Optional<Student> studentOpt = studentRepository.findById(studentLrn);
        String name = studentOpt.isPresent() ? studentOpt.get().getName() : "Unknown Patient";
        
        ClinicLog log = new ClinicLog(studentLrn, name, reason, LocalDateTime.now());
        clinicLogRepository.save(log);
        return "redirect:/nurse-dashboard?success=visit_logged";
    }

    // --- SECURED FACILITIES ADMIN DASHBOARD SUBSYSTEM ---
    @GetMapping("/facilities-dashboard")
    public String facilitiesDashboard(HttpSession session, Model model) {
        if (!isAuthorized(session, "FACILITIES_ADMIN")) return "redirect:/teacher-portal?error=unauthorized";
        populateModel(session, model);
        
        List<Student> allStudents = studentRepository.findAll();
        Map<String, List<Student>> studentsBySection = allStudents.stream()
                .filter(s -> s.getSection() != null)
                .collect(Collectors.groupingBy(Student::getSection));
        
        List<FacilityLiability> activeLiabilities = facilityLiabilityRepository.findByIsResolvedFalse();
        
        model.addAttribute("studentsBySection", studentsBySection);
        model.addAttribute("activeLiabilities", activeLiabilities);
        model.addAttribute("activeLiabilitiesCount", activeLiabilities.size());
        return "facilities-dashboard";
    }

    @PostMapping("/facilities/log-liability")
    public String logFacilityLiability(@RequestParam("studentLrn") Long studentLrn, @RequestParam("itemDescription") String itemDescription, HttpSession session) {
        if (!isAuthorized(session, "FACILITIES_ADMIN")) return "redirect:/teacher-portal?error=unauthorized";
        Optional<Student> studentOpt = studentRepository.findById(studentLrn);
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            FacilityLiability liability = new FacilityLiability(studentLrn, student.getName(), student.getSection(), itemDescription, LocalDateTime.now(), false);
            facilityLiabilityRepository.save(liability);
            
            student.setLabClearance("PENDING");
            studentRepository.save(student);
        }
        return "redirect:/facilities-dashboard?success=liability_logged";
    }

    @PostMapping("/facilities/resolve-liability")
    public String resolveFacilityLiability(@RequestParam("liabilityId") Long liabilityId, HttpSession session) {
        if (!isAuthorized(session, "FACILITIES_ADMIN")) return "redirect:/teacher-portal?error=unauthorized";
        Optional<FacilityLiability> liabilityOpt = facilityLiabilityRepository.findById(liabilityId);
        if (liabilityOpt.isPresent()) {
            FacilityLiability liability = liabilityOpt.get();
            liability.setIsResolved(true);
            facilityLiabilityRepository.save(liability);
            
            List<FacilityLiability> remaining = facilityLiabilityRepository.findByStudentLrnAndIsResolvedFalse(liability.getStudentLrn());
            if (remaining.isEmpty()) {
                Optional<Student> studentOpt = studentRepository.findById(liability.getStudentLrn());
                if (studentOpt.isPresent()) {
                    Student student = studentOpt.get();
                    student.setLabClearance("CLEARED");
                    studentRepository.save(student);
                }
            }
        }
        return "redirect:/facilities-dashboard?success=liability_resolved";
    }

    // --- OTHER DASHBOARDS (Adviser, Principal) ---
    @GetMapping("/adviser-dashboard")
    public String adviserDashboard(HttpSession session, Model model, @RequestParam(value = "tab", defaultValue = "clearance") String activeTab) {
        if (!isAuthorized(session, "ADVISER")) return "redirect:/teacher-portal?error=unauthorized";
        populateModel(session, model);
        String assignedSection = (String) session.getAttribute("assignedSection");
        List<Student> classroomStudents = studentRepository.findAll().stream()
                .filter(student -> student.getSection() != null && student.getSection().equalsIgnoreCase(assignedSection))
                .collect(Collectors.toList());
        model.addAttribute("classroomStudents", classroomStudents);
        model.addAttribute("classCount", classroomStudents.size());
        model.addAttribute("activeTab", activeTab);
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
    
    @PostMapping("/adviser/delete-student")
    public String deleteStudent(@RequestParam("lrn") Long lrn, HttpSession session) {
        if (!isAuthorized(session, "ADVISER")) return "redirect:/teacher-portal?error=unauthorized";
        
        // Deletes the student from the database using their LRN
        studentRepository.deleteById(lrn);
        
        return "redirect:/adviser-dashboard?success=student_deleted";
    }

    @PostMapping("/adviser/update-grades")
    public String updateStudentGrades(
            @RequestParam("lrn") Long lrn,
            @RequestParam(value = "mathGrade", required = false) Integer mathGrade,
            @RequestParam(value = "scienceGrade", required = false) Integer scienceGrade,
            @RequestParam(value = "englishGrade", required = false) Integer englishGrade,
            @RequestParam(value = "filipinoGrade", required = false) Integer filipinoGrade,
            @RequestParam(value = "historyGrade", required = false) Integer historyGrade,
            @RequestParam(value = "peGrade", required = false) Integer peGrade,
            @RequestParam(value = "tleGrade", required = false) Integer tleGrade,
            @RequestParam(value = "valuesGrade", required = false) Integer valuesGrade,
            @RequestParam(value = "genAvg", required = false) Double genAvg,
            HttpSession session) {
            
        if (!isAuthorized(session, "ADVISER")) return "redirect:/teacher-portal?error=unauthorized";

        java.util.Optional<com.linhs.portal.model.Student> studentOpt = studentRepository.findById(lrn);
        if (studentOpt.isPresent()) {
            com.linhs.portal.model.Student student = studentOpt.get();
            com.linhs.portal.model.Grade grade = student.getGrade();
            if (grade == null) {
                grade = new com.linhs.portal.model.Grade();
                grade.setStudent(student);
            }
            
            grade.setMathGrade(mathGrade);
            grade.setScienceGrade(scienceGrade);
            grade.setEnglishGrade(englishGrade);
            grade.setFilipinoGrade(filipinoGrade);
            grade.setHistoryGrade(historyGrade);
            grade.setPeGrade(peGrade);
            grade.setTleGrade(tleGrade);
            grade.setValuesGrade(valuesGrade);
            grade.setGenAvg(genAvg);
            
            student.setGrade(grade);
            studentRepository.save(student);
        }
        return "redirect:/adviser-dashboard?success=grades_updated";
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
    
    // --- PRINCIPAL ANNOUNCEMENT FEATURES ---
    
    @ModelAttribute("announcements")
    public java.util.List<com.linhs.portal.model.Announcement> getAnnouncements() {
        return announcementRepository.findAllByOrderByCreatedAtDesc();
    }

    @PostMapping("/admin/post-announcement")
    public String postAnnouncement(@RequestParam String title, @RequestParam String content, HttpSession session) {
        // FIXED: Now correctly checks for "ADMIN_PRINCIPAL"
        if (!isAuthorized(session, "ADMIN_PRINCIPAL")) return "redirect:/login"; 
        
        com.linhs.portal.model.Announcement note = new com.linhs.portal.model.Announcement();
        note.setTitle(title);
        note.setContent(content);
        announcementRepository.save(note);
        
        // ADDED tab=announcements to tell the page where to go
        return "redirect:/principal-dashboard?tab=announcements&success=posted";
    }

    @PostMapping("/admin/delete-announcement")
    public String deleteAnnouncement(@RequestParam Long id, HttpSession session) {
        // FIXED: Now correctly checks for "ADMIN_PRINCIPAL"
        if (!isAuthorized(session, "ADMIN_PRINCIPAL")) return "redirect:/login"; 
        announcementRepository.deleteById(id);
        // ADDED tab=announcements to tell the page where to go
        return "redirect:/principal-dashboard?tab=announcements&success=posted";
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