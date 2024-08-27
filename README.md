# Timesheet_backend
 Java Spring boot

The backend is implemented using Java Spring Boot,  and PostgreSQL is used as the database.

The application has two types of users: regular users and admins.  
- Regular users can enter timesheets, search their entries, and export them (reporting).  
- Admin users can search for all users and export their data.
 1. User Registration and Login Page**
- Registration Page**:
  - Includes fields for username, email, and password.
  - The email and username must be unique when registering.
  - The password must be at least 8 characters long and contain one uppercase letter, one lowercase letter, and one special character.
- Login Page:
  - Includes fields for username and password.
  - Upon login, the user's identity and roles are verified.
  - After a successful login, the user is redirected to the Timesheet entry page.

2. Timesheet Application**
- New Timesheet Entry:
  - The user can input fields such as date, start time, end time, and description.
- After logging in, the user will be able to enter a new timesheet.  
   
Timesheet Search and Listing:
- Users can search their past timesheet entries based on a date range.
- The search results will be displayed in a table format.

**Timesheet Update**:
- Users will be able to update previously entered timesheets after searching for them.

**Timesheet Export**:
- Users can export their search results in CSV or Excel format.

3. **Admin Panel**
   
**User Search and Listing**:
- Admins can search for and list all users and their timesheet entries.
- Search criteria include fields such as username, email, and registration date.
- Admins can perform searches using any of these criteria.

**Timesheet Export**:  
