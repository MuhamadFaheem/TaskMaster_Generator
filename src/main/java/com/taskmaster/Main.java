package com.taskmaster;

import com.taskmaster.model.TaskInput;
import com.taskmaster.model.TaskOutput;
import com.taskmaster.util.ExcelUtil;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        File inputFolder = new File("input");
        File outputFolder = new File("output");

        if (!outputFolder.exists()) {
            outputFolder.mkdirs();
        }

        File[] inputFiles = inputFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".xlsx"));

        if (inputFiles == null || inputFiles.length == 0) {
            System.out.println("Tidak ada file Excel di folder input/");
            return;
        }

        for (File inputFile : inputFiles) {
            try {
                String inputFileName = inputFile.getName();
                String baseName = new File(inputFileName).getName().replaceFirst("[.][^.]+$", "");
                String prefix = baseName.split(" ")[0];
                String outputPath = "output/Task Master " + prefix + " Result.xlsx";
                String sqlOutputPath = "output/Task Master " + prefix + " Result.sql";

                List<TaskInput> inputs = ExcelUtil.readInput(inputFile.getAbsolutePath());
                List<TaskOutput> outputs = ExcelUtil.generateAllTasks(inputs);

                ExcelUtil.writeOutput(outputs, outputPath);
                writeSqlFile(outputs, sqlOutputPath);

                System.out.println("Output berhasil dibuat: " + outputPath);
            } catch (Exception e) {
                System.out.println("Gagal memproses file: " + inputFile.getName());
                e.printStackTrace();
            }
        }
    }

    private static void writeSqlFile(List<TaskOutput> outputs, String sqlOutputPath) {
        try (PrintWriter writer = new PrintWriter(sqlOutputPath)) {
            for (TaskOutput output : outputs) {
                String query = output.getQuery();
                if (query != null && !query.trim().isEmpty()) {
                    writer.println(query);
                }
            }
            System.out.println("SQL file berhasil dibuat: " + sqlOutputPath);
        } catch (Exception e) {
            System.out.println("Gagal membuat file SQL: " + sqlOutputPath);
            e.printStackTrace();
        }
    }
}
