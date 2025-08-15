# Personal Expense Tracker (Swing + SQLite)

**Ready for NetBeans (Maven)** — Java 17.

## Features
- Add expenses (date, amount, category, note)
- Manage categories (add/delete)
- List/search expenses with filters (date range, category, keyword)
- Shows total for current filter
- SQLite database (`expenses.db`) created automatically in the run folder
- One-click runnable fat JAR via `mvn package`

## How to Import & Run (NetBeans 21/22)
1. **Download** the ZIP and extract it somewhere.
2. Open **NetBeans** → *File* → *Open Project…* → select the folder `expense-tracker` (this project) and open.
3. Right-click the project → **Run**. (Main class: `com.example.App`)
   - First run creates `expenses.db` and default categories.

### Build a single runnable JAR
- Right-click → *Build with Dependencies* (or run in terminal):
  ```bash
  mvn -q -DskipTests package
  ```
- Find the fat JAR at:
  `target/expense-tracker-1.0.0-jar-with-dependencies.jar`
- Run it:
  ```bash
  java -jar target/expense-tracker-1.0.0-jar-with-dependencies.jar
  ```

## Notes
- Date format is **YYYY-MM-DD** (e.g., 2025-07-30).
- If you delete a category that has expenses, those expenses will keep the category as **NULL** (allowed).
- Database file path is relative (same folder you run from). Delete `expenses.db` to reset data.

## Project Structure
```
src/main/java/com/example/
  App.java
  util/DB.java
  model/Category.java
  model/Expense.java
  dao/CategoryDAO.java
  dao/ExpenseDAO.java
  service/ExpenseService.java
  ui/MainFrame.java
  ui/AddExpensePanel.java
  ui/ExpenseListPanel.java
  ui/ManageCategoriesPanel.java
```
