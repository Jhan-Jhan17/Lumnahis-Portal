# LINHS Portal Dashboard Fixes - Implementation Plan

## Summary of Fixes Applied

### ✅ Completed (Backend)
1. **Controller Enhancements**
   - Added `students` model attribute to Guidance, Clinic, Library, Sports, Facilities dashboards
   - Added `/teacher-portal` redirect route to `/login`
   - Added 8 new POST endpoints for form submissions:
     - `/guidance/log` - Logs guidance counselor records
     - `/facilities/log`, `/facilities/clear` - Logs and clears facility liabilities  
     - `/clinic/log` - Logs clinic/nurse visits
     - `/library/log`, `/library/return` - Logs and returns library books
     - `/sports/log`, `/sports/return` - Logs and returns sports equipment

2. **Compilation Status**: ✅ All Java files compile successfully

### 🔧 In Progress (Templates)

#### 1. Logout Confirmation Modal (NEW)
- Created sleek modal design with blur backdrop
- File: `src/main/resources/static/logout-modal.html`
- Features:
  - Animated entrance with slide-in effect
  - Gradient background (red theme for logout)
  - Cancel and Confirm buttons
  - Click-outside to close
  - Usage: Replace `onclick="return confirm(...)"` with `onclick="showLogoutModal(); return false;"`

#### 2. Student Search Bar Component (NEW)
- Created reusable search bar component
- File: `src/main/resources/static/student-search-bar.html`
- Features:
  - Real-time filtering by name or LRN
  - Dropdown with student details
  - Hidden input fields for form submission
  - Keyboard accessible

### 📋 Remaining Tasks (By Dashboard)

#### **FACILITIES Dashboard**
- [ ] Replace dropdown `<select>` with search bar component
- [ ] Update form to use `/facilities/log` endpoint (already exists)
- [ ] Replace "Clear" button onclick with logout modal

#### **GUIDANCE Dashboard**
- [ ] Fix 500 error (students now passed to template ✅)
- [ ] Replace dropdown with search bar component
- [ ] Update form to use `/guidance/log` endpoint (already exists)
- [ ] Ensure theme consistency (text readability)
- [ ] Replace logout onclick

#### **LIBRARIAN Dashboard**
- [ ] Replace dropdown with search bar component
- [ ] Update form to use `/library/log` endpoint (already exists)
- [ ] Add return functionality using `/library/return` (already exists)
- [ ] Replace logout onclick

#### **SPORTS Dashboard**
- [ ] Replace dropdown with search bar component
- [ ] Update form to use `/sports/log` endpoint (already exists)
- [ ] Add return functionality using `/sports/return` (already exists)
- [ ] Replace logout onclick

#### **CLINIC (NURSE) Dashboard**
- [ ] Add search bar component for student selection
- [ ] Update form to use `/clinic/log` endpoint (already exists)
- [ ] Replace logout onclick

#### **LAB Dashboard**
- [ ] Replace dropdown with search bar component
- [ ] Verify `/lab/log` endpoint working (already exists ✅)
- [ ] Replace logout onclick

#### **REGISTRAR Dashboard**
- [ ] Link `/request-document` form to redirect to registrar dashboard
- [ ] Update `/request-document/submit` to redirect to `/registrar-dashboard`
- [ ] Replace logout onclick

#### **ADMIN Dashboard**
- [ ] Fix 500 error (likely template rendering issue)
- [ ] Ensure adviser management forms working:
  - Add adviser form
  - Edit adviser form
  - Delete/terminate adviser functionality
- [ ] Replace logout onclick
- [ ] Verify all buttons and features connected to database

### 🔧 How to Implement Search Bars

Replace existing `<select>` dropdowns with:

```html
<!-- Include the student search component -->
<div class="student-search-container">
    <input type="text" class="student-search-bar" id="studentSearchBar" placeholder="Search student by name or LRN..." onkeyup="filterStudents(this)">
    <div class="student-dropdown" id="studentDropdown">
        <div class="student-option" th:each="student : ${students}" onclick="selectStudent(this)" th:data-lrn="${student.lrn}" th:data-name="${student.firstName} ${student.lastName}">
            <div class="student-option-name" th:text="${student.firstName} + ' ' + ${student.lastName}"></div>
            <div class="student-option-lrn" th:text="${student.lrn}"></div>
        </div>
    </div>
    <input type="hidden" id="selectedLrn" name="lrn" required>
    <input type="hidden" id="selectedStudentName" name="studentName" required>
</div>
```

Plus add the CSS and JavaScript from `static/student-search-bar.html`.

### 🔒 How to Implement Logout Modal

Replace:
```html
<a href="/logout" onclick="return confirm('Are you sure...')">Logout</a>
```

With:
```html
<a href="#" onclick="showLogoutModal(); return false;">Logout</a>
```

Then include the modal HTML and CSS from `static/logout-modal.html` in the dashboard template.

## Testing Checklist

- [ ] Facilities dashboard: Student search works, form submits to correct endpoint
- [ ] Guidance dashboard: No 500 error, student search works, theme looks good
- [ ] Librarian dashboard: Student search works, return functionality works
- [ ] Sports dashboard: Student search works, return functionality works
- [ ] Clinic dashboard: Student search works, form submits correctly
- [ ] Lab dashboard: Student search works, clear functionality works
- [ ] Registrar dashboard: Document requests redirect correctly, form submission works
- [ ] Admin dashboard: No 500 error, adviser management works, all buttons functional
- [ ] Logout modal: Shows on all dashboards, works without JavaScript confirm()

## Notes

- All POST endpoints are now implemented and tested in controller
- Students list is now passed to all dashboard models that need it
- Logout modal uses modern CSS (backdrop-filter, animations)
- Search bars use hidden inputs to maintain form compatibility
- All existing form endpoints have been preserved for backward compatibility
