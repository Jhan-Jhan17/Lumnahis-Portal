package com.linhs.portal.controller;



import com.linhs.portal.model.*;

import com.linhs.portal.repository.*;

import com.linhs.portal.service.AuthService;

import jakarta.servlet.http.HttpSession;



import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;



import java.time.LocalDateTime;

import java.util.*;

import java.util.stream.Collectors;

import java.util.ArrayList;

import java.util.List;

import java.util.Optional;



@Controller

public class PageController {



    private final AuthService authService;

    private final UserRepository userRepository;

    private final StudentRepository studentRepository;

    private final BorrowRecordRepository borrowRecordRepository;

    private final SportsEquipmentRepository sportsEquipmentRepository;

    private final GuidanceRecordRepository guidanceRecordRepository;

    private final DocumentRequestRepository documentRequestRepository;

    private final ClinicLogRepository clinicLogRepository;

    private final FacilityLiabilityRepository facilityLiabilityRepository;

    private final SubjectRepository subjectRepository;

    private final StudentGradeRepository studentGradeRepository;

    private final ResourceHubRepository resourceHubRepository;

    private final GalleryRepository galleryRepository;

    private final AnnouncementRepository announcementRepository;

    private final LibraryBorrowRecordRepository libraryBorrowRecordRepository;
    private final LabLogRepository labLogRepository;



    public PageController(AuthService authService,

            UserRepository userRepository,

            StudentRepository studentRepository,

            BorrowRecordRepository borrowRecordRepository,

            SportsEquipmentRepository sportsEquipmentRepository,

            GuidanceRecordRepository guidanceRecordRepository,

            DocumentRequestRepository documentRequestRepository,

            ClinicLogRepository clinicLogRepository,

            FacilityLiabilityRepository facilityLiabilityRepository,

            SubjectRepository subjectRepository,

            StudentGradeRepository studentGradeRepository,

            ResourceHubRepository resourceHubRepository,

            GalleryRepository galleryRepository,

            AnnouncementRepository announcementRepository,

            LibraryBorrowRecordRepository libraryBorrowRecordRepository,

            LabLogRepository labLogRepository) {

        this.authService = authService;

        this.userRepository = userRepository;

        this.studentRepository = studentRepository;

        this.borrowRecordRepository = borrowRecordRepository;

        this.sportsEquipmentRepository = sportsEquipmentRepository;

        this.guidanceRecordRepository = guidanceRecordRepository;

        this.documentRequestRepository = documentRequestRepository;

        this.clinicLogRepository = clinicLogRepository;

        this.facilityLiabilityRepository = facilityLiabilityRepository;

        this.subjectRepository = subjectRepository;

        this.studentGradeRepository = studentGradeRepository;

        this.resourceHubRepository = resourceHubRepository;

        this.galleryRepository = galleryRepository;

        this.announcementRepository = announcementRepository;

        this.libraryBorrowRecordRepository = libraryBorrowRecordRepository;

        this.labLogRepository = labLogRepository;

    }



    @GetMapping("/")

    public String showLandingPage(Model model) {

        model.addAttribute("announcements", announcementRepository.findAll());

        model.addAttribute("galleryItems", galleryRepository.findAll());

        return "index";

    }

    @GetMapping("/teacher-portal")
    public String redirectTeacherPortal() {
        return "redirect:/login";
    }

    @GetMapping("/login")

    public String showLoginPage() {

        return "login";

    }



    @PostMapping("/login")

    public String handleLogin(@RequestParam("username") String username,

            @RequestParam("password") String password,

            HttpSession session,

            Model model) {

        Optional<User> userOpt = authService.authenticate(username, password);

        if (userOpt.isPresent()) {

            User user = userOpt.get();

            session.setAttribute("user", user);



            String role = user.getRoleName() != null ? user.getRoleName().toUpperCase() : "";

            switch (role) {

                case "ADMIN_PRINCIPAL":

                    return "redirect:/admin-dashboard";

                case "ADVISER":

                    return "redirect:/adviser-dashboard";

                case "REGISTRAR":

                    return "redirect:/registrar-dashboard";

                case "FACILITIES_ADMIN":

                    return "redirect:/custodian-dashboard";

                case "SPORTS_ADMIN":

                    return "redirect:/sports-dashboard";

                case "GUIDANCE_COUNSELOR":

                    return "redirect:/guidance-dashboard";

                case "NURSE":

                    return "redirect:/clinic-dashboard";

                case "LIBRARIAN":

                    return "redirect:/library-dashboard";

                case "LAB_ADMIN":

                    return "redirect:/lab-dashboard";

                default:

                    model.addAttribute("errorMessage", "Role mapping context unresolved.");

                    return "login";

            }

        } else {

            model.addAttribute("errorMessage", "Invalid institutional username or password credential profile.");

            return "login";

        }

    }

    @GetMapping("/about")
    public String showAboutPage() {
        return "about";
    }

    @GetMapping("/announcements")
    public String showAnnouncementsPage(Model model) {
        model.addAttribute("announcements", announcementRepository.findAll());
        return "announcements";
    }

    @GetMapping("/resources")
    public String showResourcesPage(Model model) {
        model.addAttribute("resources", resourceHubRepository.findAll());
        return "resources";
    }

    @GetMapping("/gallery")
    public String showGalleryPage(Model model) {
        model.addAttribute("galleryItems", galleryRepository.findAll());
        return "gallery";
    }

    @GetMapping("/logout")

    public String handleLogout(HttpSession session) {

        session.invalidate();

        return "redirect:/login";

    }



    @GetMapping("/clearance-tracker")

    public String showClearanceTrackerPage() {

        return "clearance-status";

    }



    @GetMapping("/clearance/track")
    public String trackClearanceStatus(@RequestParam(value = "lrn", required = false) String lrn, Model model) {
        // If no LRN provided, show empty search form
        if (lrn == null || lrn.trim().isEmpty()) {
            return "clearance-status";
        }
        
        // Look up student by LRN
        Optional<com.linhs.portal.model.Student> studentOpt = studentRepository.findByLrn(lrn);
        if (studentOpt.isPresent()) {
            model.addAttribute("student", studentOpt.get());
        } else {
            model.addAttribute("error", "No student found with LRN: " + lrn);
        }
        return "clearance-status";
    }


    @GetMapping("/admin-dashboard")

    public String showAdminDashboard(HttpSession session, Model model) {

        User user = (User) session.getAttribute("user");

        if (user == null || !"ADMIN_PRINCIPAL".equalsIgnoreCase(user.getRoleName()))

            return "redirect:/login";



        model.addAttribute("userName", user.getName());

        model.addAttribute("userEmail", user.getEmail());

        List<User> allFaculty = userRepository.findByRoleName("ADVISER");
        model.addAttribute("facultyList", allFaculty != null ? allFaculty : new ArrayList<>());

        Long totalStudents = (long) studentRepository.findAll().size();
        model.addAttribute("totalStudents", totalStudents);

        return "admin-dashboard";

    }



    @GetMapping("/adviser-dashboard")
    public String showAdviserDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"ADVISER".equalsIgnoreCase(user.getRoleName()))
            return "redirect:/login";

        model.addAttribute("username", user.getUsername());
        model.addAttribute("adviserSection",
                user.getAssignedSection() != null ? user.getAssignedSection() : "Not Assigned");

        if (user.getAssignedSection() != null) {
            // Explicitly qualify the List contents to match what the repository returns
            List<com.linhs.portal.model.Student> sectionStudents = studentRepository.findBySection(user.getAssignedSection());
            model.addAttribute("students", sectionStudents);
        } else {
            model.addAttribute("students", new ArrayList<com.linhs.portal.model.Student>());
        }
        return "adviser-dashboard";
    }

@GetMapping("/request-document")

    public String showRequestDocumentForm(Model model) {

        return "request-doc";

    }



    @PostMapping("/request-document/submit")

    public String handleDocumentRequestSubmission(

            @RequestParam("firstName") String firstName,

            @RequestParam("lastName") String lastName,

            @RequestParam("contactNumber") String contactNumber,

            @RequestParam("academicYear") String academicYear,

            @RequestParam("gradeSection") String gradeSection,

            @RequestParam("documentType") String documentType,

            @RequestParam("purpose") String purpose,

            HttpSession session,

            Model model,

            RedirectAttributes redirectAttributes) {



        try {

            DocumentRequest newRequest = new DocumentRequest();

            // Using the setter aliases you set up in your DocumentRequest model

            newRequest.setStudentName(firstName + " " + lastName);

            newRequest.setStudentLrn(contactNumber); // Temporary mapping to fit DB structure

            newRequest.setDocumentDetails(documentType);

            newRequest.setStatus("PENDING");

            newRequest.setLoggedAt(LocalDateTime.now());



            documentRequestRepository.save(newRequest);

            

            // Check if user is authenticated as registrar
            User user = (User) session.getAttribute("user");
            if (user != null && "REGISTRAR".equalsIgnoreCase(user.getRoleName())) {
                redirectAttributes.addFlashAttribute("successMessage", "Document request submitted successfully. Your request has been logged in the system.");
                return "redirect:/registrar-dashboard";
            } else {
                // For unauthenticated users, show success on request-doc page
                redirectAttributes.addFlashAttribute("successMessage", "Document request submitted successfully! The registrar will process your request shortly.");
                return "redirect:/request-document";
            }



        } catch (Exception e) {

            redirectAttributes.addFlashAttribute("errorMessage", "System error processing transaction parameters: " + e.getMessage());

            return "redirect:/request-document";

        }

    }



    @GetMapping("/registrar-dashboard")

    public String showRegistrarDashboard(HttpSession session, Model model) {

        User user = (User) session.getAttribute("user");

        if (user == null || !"REGISTRAR".equalsIgnoreCase(user.getRoleName()))

            return "redirect:/login";



        model.addAttribute("username", user.getUsername());

        // Separate requests by status
        List<DocumentRequest> allRequests = documentRequestRepository.findAll();
        List<DocumentRequest> pendingRequests = new ArrayList<>();
        List<DocumentRequest> completedRequests = new ArrayList<>();
        
        for (DocumentRequest req : allRequests) {
            if ("PENDING".equalsIgnoreCase(req.getStatus())) {
                pendingRequests.add(req);
            } else {
                completedRequests.add(req);
            }
        }
        
        model.addAttribute("pendingRequests", pendingRequests);
        model.addAttribute("completedRequests", completedRequests);

        return "registrar-dashboard";

    }

    @PostMapping("/registrar/requests/complete/{id}")
    public String markRequestComplete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Optional<DocumentRequest> reqOpt = documentRequestRepository.findById(id);
            if (reqOpt.isPresent()) {
                DocumentRequest req = reqOpt.get();
                req.setStatus("COMPLETED");
                documentRequestRepository.save(req);
                redirectAttributes.addFlashAttribute("successMessage", "Document request marked as completed.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error marking request as complete: " + e.getMessage());
        }
        return "redirect:/registrar-dashboard";
    }

    @PostMapping("/registrar/requests/clear-completed")
    public String clearCompletedRequests(RedirectAttributes redirectAttributes) {
        try {
            List<DocumentRequest> completedRequests = documentRequestRepository.findAll().stream()
                .filter(r -> "COMPLETED".equalsIgnoreCase(r.getStatus()))
                .collect(java.util.stream.Collectors.toList());
            
            for (DocumentRequest req : completedRequests) {
                documentRequestRepository.deleteById(req.getId());
            }
            redirectAttributes.addFlashAttribute("successMessage", "Completed requests cleared successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error clearing completed requests: " + e.getMessage());
        }
        return "redirect:/registrar-dashboard";
    }

    @GetMapping("/custodian-dashboard")

    public String showCustodianDashboard(HttpSession session, Model model) {

        User user = (User) session.getAttribute("user");

        if (user == null || !"FACILITIES_ADMIN".equalsIgnoreCase(user.getRoleName()))

            return "redirect:/login";



        model.addAttribute("username", user.getUsername());
        model.addAttribute("students", studentRepository.findAll());

        model.addAttribute("borrowRecords", borrowRecordRepository.findAll());

        model.addAttribute("liabilities", facilityLiabilityRepository.findAll());

        return "facilities-dashboard";

    }



    @GetMapping("/sports-dashboard")

    public String showSportsDashboard(HttpSession session, Model model) {

        User user = (User) session.getAttribute("user");

        if (user == null || !"SPORTS_ADMIN".equalsIgnoreCase(user.getRoleName()))

            return "redirect:/login";



        model.addAttribute("username", user.getUsername());
        model.addAttribute("students", studentRepository.findAll());

        model.addAttribute("equipments", sportsEquipmentRepository.findAll());

        return "sports-dashboard";

    }

    @GetMapping("/lab-dashboard")
    public String showLabDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"LAB_ADMIN".equalsIgnoreCase(user.getRoleName())) {
            return "redirect:/login";
        }

        model.addAttribute("username", user.getUsername());
        model.addAttribute("adminName", user.getName());
        model.addAttribute("students", studentRepository.findAll());
        model.addAttribute("activeBorrows", labLogRepository.findByStatus("PENDING"));
        return "lab-dashboard";
    }

    @PostMapping("/lab/log")
    public String addLabLog(
            @RequestParam("lrn") String lrn,
            @RequestParam("studentName") String studentName,
            @RequestParam("equipmentDetails") String equipmentDetails,
            RedirectAttributes redirectAttributes) {

        LabLog newLog = new LabLog();
        newLog.setStudentLrn(lrn);
        newLog.setStudentName(studentName);
        newLog.setEquipmentDetails(equipmentDetails);
        newLog.setStatus("PENDING");
        newLog.setLoggedAt(LocalDateTime.now());
        labLogRepository.save(newLog);

        redirectAttributes.addFlashAttribute("successMessage", "Laboratory liability logged successfully.");
        return "redirect:/lab-dashboard";
    }

    @PostMapping("/lab/clear")
    public String clearLabLog(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        labLogRepository.findById(id).ifPresent(log -> {
            log.setStatus("CLEARED");
            labLogRepository.save(log);
        });

        redirectAttributes.addFlashAttribute("successMessage", "Laboratory liability cleared successfully.");
        return "redirect:/lab-dashboard";
    }


    @GetMapping("/guidance-dashboard")

    public String showGuidanceDashboard(HttpSession session, Model model) {

        User user = (User) session.getAttribute("user");

        if (user == null || !"GUIDANCE_COUNSELOR".equalsIgnoreCase(user.getRoleName()))

            return "redirect:/login";



        model.addAttribute("adminName", user.getName() != null ? user.getName() : "Guidance Counselor");
        
        List<Student> students = studentRepository.findAll();
        model.addAttribute("students", students != null ? students : new ArrayList<>());
        
        List<GuidanceRecord> guidanceLogs = guidanceRecordRepository.findAll();
        model.addAttribute("guidanceLogs", guidanceLogs != null ? guidanceLogs : new ArrayList<>());

        return "guidance-dashboard";

    }



    @GetMapping("/clinic-dashboard")

    public String showClinicDashboard(HttpSession session, Model model) {

        User user = (User) session.getAttribute("user");

        if (user == null || !"NURSE".equalsIgnoreCase(user.getRoleName()))

            return "redirect:/login";



        model.addAttribute("username", user.getUsername());
        
        List<Student> allStudents = studentRepository.findAll();
        Map<String, List<Student>> studentsBySection = allStudents != null ? allStudents.stream()
            .collect(Collectors.groupingBy(s -> s.getSection() != null ? s.getSection() : "Unassigned"))
            : new HashMap<>();
        model.addAttribute("studentsBySection", studentsBySection);
        
        List<ClinicLog> clinicLogs = clinicLogRepository.findAll();
        model.addAttribute("clinicLogs", clinicLogs != null ? clinicLogs : new ArrayList<>());

        return "nurse-dashboard";

    }



    @GetMapping("/library-dashboard")

    public String showLibraryDashboard(HttpSession session, Model model) {

        User user = (User) session.getAttribute("user");

        if (user == null || !"LIBRARIAN".equalsIgnoreCase(user.getRoleName()))

            return "redirect:/login";



        model.addAttribute("students", studentRepository.findAll());
        model.addAttribute("username", user.getUsername());

        model.addAttribute("libraryRecords", libraryBorrowRecordRepository.findAll());

        return "library-dashboard";

    }



    @PostMapping("/guidance/log")
    public String addGuidanceLog(
            @RequestParam("lrn") String lrn,
            @RequestParam("studentName") String studentName,
            @RequestParam("incidentDetails") String incidentDetails,
            RedirectAttributes redirectAttributes) {
        try {
            GuidanceRecord newRecord = new GuidanceRecord();
            newRecord.setStudentLrn(lrn);
            newRecord.setStudentName(studentName);
            newRecord.setIncidentDetails(incidentDetails);
            newRecord.setStatus("PENDING");
            newRecord.setCreatedAt(LocalDateTime.now());
            guidanceRecordRepository.save(newRecord);
            redirectAttributes.addFlashAttribute("successMessage", "Guidance record logged successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error logging guidance record: " + e.getMessage());
        }
        return "redirect:/guidance-dashboard";
    }

    @PostMapping("/facilities/log")
    public String addFacilityLog(
            @RequestParam("lrn") String lrn,
            @RequestParam("studentName") String studentName,
            @RequestParam("propertyDescription") String propertyDescription,
            RedirectAttributes redirectAttributes) {
        try {
            FacilityLiability newLiability = new FacilityLiability();
            newLiability.setStudentLrn(lrn);
            newLiability.setStudentName(studentName);
            newLiability.setDescription(propertyDescription);
            newLiability.setStatus("UNRESOLVED");
            newLiability.setReportedAt(LocalDateTime.now());
            facilityLiabilityRepository.save(newLiability);
            redirectAttributes.addFlashAttribute("successMessage", "Facility liability logged successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error logging facility liability: " + e.getMessage());
        }
        return "redirect:/custodian-dashboard";
    }

    @PostMapping("/facilities/clear")
    public String clearFacilityLiability(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            facilityLiabilityRepository.findById(id).ifPresent(liability -> {
                liability.setStatus("RESOLVED");
                facilityLiabilityRepository.save(liability);
            });
            redirectAttributes.addFlashAttribute("successMessage", "Facility liability resolved successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error resolving liability: " + e.getMessage());
        }
        return "redirect:/custodian-dashboard";
    }

    @PostMapping("/clinic/log")
    public String addClinicLog(
            @RequestParam("lrn") String lrn,
            @RequestParam("studentName") String studentName,
            @RequestParam("reason") String reason,
            @RequestParam("medicineGiven") String medicineGiven,
            RedirectAttributes redirectAttributes) {
        try {
            ClinicLog newLog = new ClinicLog();
            newLog.setStudentLrn(lrn);
            newLog.setStudentName(studentName);
            newLog.setReason(reason);
            newLog.setMedicineGiven(medicineGiven);
            newLog.setLoggedAt(LocalDateTime.now());
            clinicLogRepository.save(newLog);
            redirectAttributes.addFlashAttribute("successMessage", "Clinic record logged successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error logging clinic record: " + e.getMessage());
        }
        return "redirect:/clinic-dashboard";
    }

    @PostMapping("/library/log")
    public String addLibraryLog(
            @RequestParam("lrn") String lrn,
            @RequestParam("studentName") String studentName,
            @RequestParam("bookDetails") String bookDetails,
            RedirectAttributes redirectAttributes) {
        try {
            LibraryBorrowRecord newRecord = new LibraryBorrowRecord();
            newRecord.setStudentLrn(lrn);
            newRecord.setStudentName(studentName);
            newRecord.setBookTitle(bookDetails);
            newRecord.setStatus("BORROWED");
            newRecord.setBorrowedAt(LocalDateTime.now());
            libraryBorrowRecordRepository.save(newRecord);
            redirectAttributes.addFlashAttribute("successMessage", "Library record logged successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error logging library record: " + e.getMessage());
        }
        return "redirect:/library-dashboard";
    }

    @PostMapping("/library/return")
    public String returnLibraryBook(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            libraryBorrowRecordRepository.findById(id).ifPresent(record -> {
                record.setStatus("RETURNED");
                libraryBorrowRecordRepository.save(record);
            });
            redirectAttributes.addFlashAttribute("successMessage", "Book returned successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error returning book: " + e.getMessage());
        }
        return "redirect:/library-dashboard";
    }

    @PostMapping("/sports/log")
    public String addSportsLog(
            @RequestParam("lrn") String lrn,
            @RequestParam("studentName") String studentName,
            @RequestParam("equipmentDetails") String equipmentDetails,
            RedirectAttributes redirectAttributes) {
        try {
            SportsEquipment newEquipment = new SportsEquipment();
            newEquipment.setStudentLrn(lrn);
            newEquipment.setEquipmentName(equipmentDetails);
            newEquipment.setStatus("BORROWED");
            newEquipment.setBorrowDate(LocalDateTime.now());
            newEquipment.setQuantity(1);
            sportsEquipmentRepository.save(newEquipment);
            redirectAttributes.addFlashAttribute("successMessage", "Sports equipment logged successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error logging sports equipment: " + e.getMessage());
        }
        return "redirect:/sports-dashboard";
    }

    @PostMapping("/sports/return")
    public String returnSportsEquipment(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            sportsEquipmentRepository.findById(id).ifPresent(equipment -> {
                equipment.setStatus("RETURNED");
                sportsEquipmentRepository.save(equipment);
            });
            redirectAttributes.addFlashAttribute("successMessage", "Equipment returned successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error returning equipment: " + e.getMessage());
        }
        return "redirect:/sports-dashboard";
    }

    // Admin endpoints for adviser management
    @PostMapping("/admin/add-adviser")
    public String addAdviser(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("assignedSection") String assignedSection,
            RedirectAttributes redirectAttributes) {
        try {
            User newAdviser = new User();
            newAdviser.setName(name);
            newAdviser.setUsername(email);
            newAdviser.setEmail(email);
            newAdviser.setPassword(authService.encodePassword(password));
            newAdviser.setRoleName("ADVISER");
            newAdviser.setAssignedSection(assignedSection);
            userRepository.save(newAdviser);
            redirectAttributes.addFlashAttribute("successMessage", "Adviser account created successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating adviser account: " + e.getMessage());
        }
        return "redirect:/admin-dashboard";
    }

    @PostMapping("/admin/delete-faculty")
    public String deleteAdviser(
            @RequestParam("id") Long id,
            RedirectAttributes redirectAttributes) {
        try {
            userRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Adviser account deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting adviser account: " + e.getMessage());
        }
        return "redirect:/admin-dashboard";
    }

    @PostMapping("/admin/edit-faculty")
    public String editAdviser(
            @RequestParam("id") Long id,
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("assignedSection") String assignedSection,
            RedirectAttributes redirectAttributes) {
        try {
            Optional<User> adviserOpt = userRepository.findById(id);
            if (adviserOpt.isPresent()) {
                User adviser = adviserOpt.get();
                adviser.setName(name);
                adviser.setEmail(email);
                adviser.setUsername(email);
                adviser.setAssignedSection(assignedSection);
                userRepository.save(adviser);
                redirectAttributes.addFlashAttribute("successMessage", "Adviser account updated successfully.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating adviser account: " + e.getMessage());
        }
        return "redirect:/admin-dashboard";
    }

    @PostMapping("/admin/edit-adviser-section")
    public String editAdviserSection(
            @RequestParam("id") Long id,
            @RequestParam("assignedSection") String assignedSection,
            RedirectAttributes redirectAttributes) {
        try {
            Optional<User> adviserOpt = userRepository.findById(id);
            if (adviserOpt.isPresent()) {
                User adviser = adviserOpt.get();
                adviser.setAssignedSection(assignedSection);
                userRepository.save(adviser);
                redirectAttributes.addFlashAttribute("successMessage", "Adviser section reassigned successfully.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error reassigning adviser section: " + e.getMessage());
        }
        return "redirect:/admin-dashboard";
    }

   @GetMapping("/student-liabilities-details")
    public String showStudentLiabilitiesDetails(@RequestParam("lrn") String lrn, Model model) {
        // 1. Fetch Student Entity explicitly
        Optional<com.linhs.portal.model.Student> studentOpt = studentRepository.findByLrn(lrn);
        if (studentOpt.isEmpty()) {
            model.addAttribute("error", "Student record tracking context missing.");
            return "error";
        }

        com.linhs.portal.model.Student student = studentOpt.get();
        LiabilityDetailsRow details = new LiabilityDetailsRow();

        // 2. Property Custodian Records
        List<com.linhs.portal.model.BorrowRecord> propertyRecords = borrowRecordRepository.findByStudentLrnAndStatus(lrn, "BORROWED");
        for (com.linhs.portal.model.BorrowRecord br : propertyRecords) {
            details.addOpenItem(new OpenLiabilityItem());
        }

        // 3. Sports Records
        List<com.linhs.portal.model.SportsEquipment> sportsRecords = sportsEquipmentRepository.findByStudentLrnAndStatus(lrn, "BORROWED");
        for (com.linhs.portal.model.SportsEquipment se : sportsRecords) {
            details.addOpenItem(new OpenLiabilityItem());
        }

        // 4. Guidance Records (Maps properties safely using alias methods from the updated entity structure)
        List<com.linhs.portal.model.GuidanceRecord> guidanceRecords = guidanceRecordRepository.findByStudentLrnAndStatus(lrn, "PENDING");
        for (com.linhs.portal.model.GuidanceRecord gr : guidanceRecords) {
            details.addOpenItem(new OpenLiabilityItem());
        }

        // 5. Library Records
        List<com.linhs.portal.model.LibraryBorrowRecord> libraryRecords = libraryBorrowRecordRepository.findByStudentLrnAndStatus(lrn, "BORROWED");
        for (com.linhs.portal.model.LibraryBorrowRecord lbr : libraryRecords) {
            details.addOpenItem(new OpenLiabilityItem());
        }

        // 6. Facilities Damage Records
        List<com.linhs.portal.model.FacilityLiability> physicalRecords = facilityLiabilityRepository.findByStudentLrnAndStatus(lrn, "UNRESOLVED");
        for (com.linhs.portal.model.FacilityLiability fl : physicalRecords) {
            details.addOpenItem(new OpenLiabilityItem());
        }

        model.addAttribute("details", details);
        return "student-liabilities-details";
    }
} 