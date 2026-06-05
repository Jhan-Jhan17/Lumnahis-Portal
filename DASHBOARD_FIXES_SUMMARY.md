# Dashboard Error 500 Fixes Summary

## Status Overview
- ✅ **Admin Dashboard** - FIXED and WORKING
- ✅ **Registrar Dashboard** - Already working (reference implementation)
- ❌ **Guidance Dashboard** - Controller fixed, template property names corrected, still debugging 500 error
- ⚠️ **Remaining 6 Dashboards** - Require similar fixes

---

## Root Cause Analysis
The Error 500 issues are caused by mismatches between:
1. **Controller model attribute names** - what PageController passes to the template
2. **Template variable names** - what HTML/Thymeleaf tries to access
3. **Model object property names** - field names in entity models don't match template usage

---

## Fixes Applied

### 1. Admin Dashboard (FIXED ✅)

**File**: `src/main/java/com/linhs/portal/controller/PageController.java` (Lines 309-327)

**Changed From**:
```java
model.addAttribute("username", user.getUsername());
model.addAttribute("advisers", userRepository.findByRoleName("ADVISER"));
model.addAttribute("allStudents", studentRepository.findAll());
```

**Changed To**:
```java
model.addAttribute("userName", user.getName());
model.addAttribute("userEmail", user.getEmail());
List<User> allFaculty = userRepository.findByRoleName("ADVISER");
model.addAttribute("facultyList", allFaculty != null ? allFaculty : new ArrayList<>());
Long totalStudents = (long) studentRepository.findAll().size();
model.addAttribute("totalStudents", totalStudents);
```

**Template expects** (`admin-dashboard.html`):
- `userName` (Line 69)
- `userEmail` (Line 70)
- `totalStudents` (Line 121)
- `facultyList` (for null-check)

**Also Fixed Template**:
- Line 129: Added null-check: `th:text="${facultyList != null ? #lists.size(facultyList) : 0}"`

---

### 2. Guidance Dashboard (PARTIALLY FIXED ⚠️)

**File**: `src/main/java/com/linhs/portal/controller/PageController.java` (Lines 602-618)

**Changed Controller To**:
```java
model.addAttribute("adminName", user.getName() != null ? user.getName() : "Guidance Counselor");

List<Student> students = studentRepository.findAll();
model.addAttribute("students", students != null ? students : new ArrayList<>());

List<GuidanceRecord> guidanceLogs = guidanceRecordRepository.findAll();
model.addAttribute("guidanceLogs", guidanceLogs != null ? guidanceLogs : new ArrayList<>());
```

**Template Property Name Fixes** (`guidance-dashboard.html`):
- Line 197: Changed `log.lrn` → `log.studentLrn`
- Line 197: Also verified `log.studentName` is correct
- Line 200: Changed `log.loggedAt` → `log.createdAt`

**GuidanceRecord Model Properties**:
- `studentLrn` (not `lrn`)
- `studentName` (correct)
- `createdAt` (not `loggedAt`)

**Status**: Controller and template fixes applied. Still investigating 500 error.

---

## Remaining Dashboards to Fix

### 3. Clinic Dashboard (Nurse Dashboard)

**File**: `src/main/resources/templates/nurse-dashboard.html`

**Controller Method**: `showClinicDashboard()` (Lines 617-632)

**Current Attributes Being Passed**:
- `username`
- `students`
- `logs`

**Template Expects**:
- `username` (Line 127)
- `studentsBySection` (Map of Section → List<Student>) (Lines 145, 163, 238-239)
- `clinicLogs` (Line 213, not `logs`)

**Fixes Needed**:
1. Change `model.addAttribute("logs", ...)` → `model.addAttribute("clinicLogs", ...)`
2. Create `Map<String, List<Student>>` grouped by section for `studentsBySection`
3. Fix template: Change `log.loggedAt` → `log.createdAt`

---

### 4. Library Dashboard

**File**: `src/main/resources/templates/library-dashboard.html`

**Controller Method**: `showLibraryDashboard()` (Lines 640-654)

**Current Attributes Being Passed**:
- `students`
- `username`
- `libraryRecords`

**Need to Verify**:
1. What attributes library-dashboard template expects
2. Match controller attributes to template variable names
3. Fix any property name mismatches in LibraryBorrowRecord

---

### 5. Sports Dashboard

**File**: `src/main/resources/templates/sports-dashboard.html`

**Controller Method**: `showSportsDashboard()` (Lines 549-560)

**Current Attributes Being Passed**:
- `username`
- `students`
- `equipments`

**Need to Verify**:
1. What attributes sports-dashboard template expects
2. Check if equipment/equipments naming is correct
3. Fix any mismatches

---

### 6. Lab Dashboard

**File**: `src/main/resources/templates/lab-dashboard.html`

**Controller Method**: `showLabDashboard()` (Lines 562-582)

**Current Attributes Being Passed**:
- `username`
- `adminName`
- `students`
- `activeBorrows`

**Need to Verify**:
1. What attributes lab-dashboard template expects
2. Match naming conventions

---

### 7. Facilities/Custodian Dashboard

**File**: `src/main/resources/templates/facilities-dashboard.html`

**Controller Method**: `showCustodianDashboard()` (Lines 528-547)

**Current Attributes Being Passed**:
- `username`
- `students`
- `borrowRecords`
- `liabilities`

**Need to Verify**:
1. What attributes facilities-dashboard template expects

---

### 8. Adviser Dashboard

**File**: `src/main/resources/templates/adviser-dashboard.html`

**Controller Method**: `showAdviserDashboard()` (Lines 333-360)

**Current Attributes Being Passed**:
- `username`
- `adviserSection`
- `students`

**Template Expects**:
- `liabilities` (Line 176)
- `classSubjects` (Lines 243, 257, 300)
- `classroomStudents` (Line 249)

**Fixes Needed**:
1. Provide `liabilities` model attribute
2. Provide `classSubjects` model attribute
3. Rename `students` to `classroomStudents` OR change template usage
4. Check if there are missing repositories for Liability and Subject

---

### 9. Admin Dashboard (Already Fixed - Registrar reference)

**File**: `src/main/resources/templates/registrar-dashboard.html`

**Status**: ✅ WORKING - Use as reference implementation

---

## Implementation Pattern

For each dashboard:

```java
@GetMapping("/dashboard-name")
public String showDashboard(HttpSession session, Model model) {
    User user = (User) session.getAttribute("user");
    if (user == null || !"ROLE_NAME".equalsIgnoreCase(user.getRoleName()))
        return "redirect:/login";
    
    // Add all required attributes with null-safety
    model.addAttribute("attribute1", value1 != null ? value1 : defaultValue);
    model.addAttribute("attribute2", value2 != null ? value2 : new ArrayList<>());
    
    return "template-name";
}
```

---

## Testing Procedure

1. **Rebuild Project**:
   ```bash
   mvn clean package -DskipTests
   ```

2. **Start Application**:
   ```bash
   java -jar target/portal-0.0.1-SNAPSHOT.jar
   ```

3. **Test Each Dashboard**:
   - Admin: `admin@linhs.edu.ph` / `password123`
   - Registrar: `registrar@linhs.edu.ph` / `password123`
   - Guidance: `guidance@linhs.edu.ph` / `password123`
   - Clinic (Nurse): `clinic@linhs.edu.ph` / `password123`
   - Library: `library@linhs.edu.ph` / `password123`
   - Sports: `sports@linhs.edu.ph` / `password123`
   - Lab: `lab@linhs.edu.ph` / `password123`
   - Facilities: `facilities@linhs.edu.ph` / `password123`
   - Adviser: `adviser@linhs.edu.ph` / `password123`

---

## Entity Model Properties Reference

### Student
- `lrn` (String)
- `name` (String)
- `section` (String)
- Various clearance fields

### GuidanceRecord
- `studentLrn` (not `lrn`)
- `studentName` (not `name`)
- `incidentDetails`
- `actionTaken`
- `createdAt` (not `loggedAt`)
- `status`

### ClinicLog
- `studentLrn`
- `studentName`
- `reason`
- `medicineGiven`
- `createdAt` (not `loggedAt`)

### LibraryBorrowRecord
- `studentLrn`
- `studentName`
- `bookDetails`
- `borrowDate`
- `returnDate`
- `createdAt`

---

## Troubleshooting Tips

1. **500 Error**: Check template for:
   - Undefined variables
   - Accessing properties that don't exist
   - Null pointer exceptions in Thymeleaf expressions
   - Syntax errors in th: attributes

2. **Attribute Mismatch**: Verify:
   - Model attribute name matches template variable usage
   - Entity property names match template access patterns
   - Null values are handled with default values or null-checks

3. **Debug approach**:
   - Start with simplifying template to minimum viable HTML
   - Gradually add complexity
   - Check browser console for JavaScript errors
   - Review server logs for Thymeleaf exceptions

---

## Next Steps

1. For each remaining dashboard, inspect the template to identify all `${}` variables
2. Match them with controller attributes
3. Apply fixes following the admin-dashboard pattern
4. Rebuild and test
5. Move to next dashboard when one is working

