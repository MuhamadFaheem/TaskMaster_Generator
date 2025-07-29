
# 🌾 TaskMaster Generator

A simple Java utility to generate plantation task master data from an Excel input.  
It splits total hectares into equal-sized tasks and generates both Excel and SQL outputs.

---

## 📥 Input Format

Place input Excel files into the `input/` folder. The Excel must contain the following columns:

| OU   | Estate | Total Ha | Blok       | Jml task |
|------|--------|----------|------------|----------|
| GIDE | ID     | 30.95    | SID1P01AA  | 5        |

- **Divisi** is automatically derived from the 3rd character of the `Blok` code  
  (e.g., `SID1P01AA` → Divisi = `1`)
- No need to manually input Divisi

---

## ⚙️ How It Works

1. Reads Excel input from the `input/` directory
2. For each row:
   - Parses and calculates task areas (with 2 decimal precision)
   - Automatically assigns task numbers and sequence
3. Outputs:
   - `output/Task Master <Prefix> Result.xlsx` → Task breakdown per block
   - `output/Task Master <Prefix> Result.sql` → SQL `INSERT` statements

---

## ▶️ How to Run

Ensure you have **Maven** and **JDK 11+** installed.

```bash
# Run the program via Maven
mvn clean compile exec:java
```

---

## 🛠 Built With

- [Java 11](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html) – Core language
- [Apache POI](https://poi.apache.org/) – Excel reading and writing
- [Maven](https://maven.apache.org/) – Dependency & build management
- [Lombok](https://projectlombok.org/) – Boilerplate reduction

---

## 📁 Folder Structure

```
├── input/        # Place input Excel files here
├── output/       # Output Excel and SQL files will be saved here
├── src/
│   └── main/
│       └── java/
│           └── com/taskmaster/
│               ├── Main.java
│               ├── model/
│               ├── util/
│               └── service/
```

---