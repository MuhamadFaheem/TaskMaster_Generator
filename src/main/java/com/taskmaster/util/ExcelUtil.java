package com.taskmaster.util;

import com.taskmaster.model.TaskInput;
import com.taskmaster.model.TaskOutput;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class ExcelUtil {

    public static List<TaskInput> readInput(String filePath) throws IOException {
        List<TaskInput> inputs = new ArrayList<>();
        DataFormatter formatter = new DataFormatter();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            String ou = "", estate = "";

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String newOu = formatter.formatCellValue(row.getCell(0));
                if (!newOu.isEmpty()) ou = newOu;

                String newEstate = formatter.formatCellValue(row.getCell(1));
                if (!newEstate.isEmpty()) estate = newEstate;

                String totalHaStr = formatter.formatCellValue(row.getCell(2));
                String block = formatter.formatCellValue(row.getCell(3));
                String taskCountStr = formatter.formatCellValue(row.getCell(4));

                if (block.isEmpty() || totalHaStr.isEmpty() || taskCountStr.isEmpty()) continue;

                double totalHa = Double.parseDouble(totalHaStr);
                int taskCount = Integer.parseInt(taskCountStr);

                TaskInput input = new TaskInput(ou, estate, totalHa, block, taskCount);
                inputs.add(input);
            }
        }

        return inputs;
    }

    public static List<TaskOutput> generateAllTasks(List<TaskInput> inputs) {
        List<TaskOutput> allOutputs = new ArrayList<>();
        for (TaskInput input : inputs) {
            allOutputs.addAll(generateTasks(input));
        }
        return allOutputs;
    }

    public static List<TaskOutput> generateTasks(TaskInput input) {
        List<TaskOutput> outputs = new ArrayList<>();

        double totalArea = input.getTotalHa();
        int numTasks = input.getTaskCount();
        List<Double> taskAreas = calculateRoundedAreas(totalArea, numTasks);

        String block = input.getBlock();
        String divisi = "";
        if (block != null && block.length() >= 3) {
            divisi = String.valueOf(block.charAt(3));
        }
        int lnItemSeq = 16384;
        for (int i = 0; i < numTasks; i++) {
            TaskOutput output = new TaskOutput();
            output.setPMS_Area_Label1ID(input.getOu());
            output.setPMS_Area_Label2ID(input.getEstate());
            output.setPMS_Area_Label3ID(divisi);
            output.setPMS_Area_Label4ID(input.getBlock());
            output.setLNITMSEQ(lnItemSeq);
            output.setPMS_No_Task(String.format("T%03d", i + 1));
            output.setPMS_Ha(taskAreas.get(i));
            String sql = buildQuery(
                input.getOu(),
                input.getEstate(),
                divisi,
                input.getBlock(),
                String.valueOf(lnItemSeq),
                String.format("T%03d", i + 1),
                taskAreas.get(i)
            );
            output.setQuery(sql);
            outputs.add(output);
            lnItemSeq += 16384; 
            // String sql = String.format(
            //     "INSERT INTO task_master " +
            //     "VALUES ('%s', '%s', '%s', '%s', %d, '%s', %.2f);",
            //     input.getOu(),
            //     input.getEstate(),
            //     input.getDivisi(),
            //     input.getBlock(),
            //     lnItemSeq,
            //     String.format("T%03d", i + 1),
            //     taskAreas.get(i)
            // );
        }

        return outputs;
    }

    private static List<Double> calculateRoundedAreas(double totalArea, int numTasks) {
        double rawArea = totalArea / numTasks;
        double roundedDown = Math.floor(rawArea * 100) / 100.0;

        List<Double> areas = new ArrayList<>(Collections.nCopies(numTasks, roundedDown));
        double sum = roundedDown * numTasks;
        int diff = (int) Math.round((totalArea - sum) * 100); // dalam satuan sen (0.01)

        for (int i = numTasks - 1; diff > 0 && i >= 0; i--, diff--) {
            double updated = Math.round((areas.get(i) + 0.01) * 100.0) / 100.0;
            areas.set(i, updated);
        }

        return areas;
    }

    private static String buildQuery(String area1, String area2, String area3, String area4,
                                     String lnitemseq, String noTask, double haPerTask) {
        String haFormatted = String.format(Locale.US, "%.2f", haPerTask);
        return String.format(
            "insert into PMS0102500234 values ('%s','%s','%s','%s','%s','%s',%s)",
            area1, area2, area3, area4, lnitemseq, noTask, haFormatted
        );
    }

    public static void writeOutput(List<TaskOutput> outputs, String outputPath) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Task Output");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("PMS_Area_Label1ID");
        header.createCell(1).setCellValue("PMS_Area_Label2ID");
        header.createCell(2).setCellValue("PMS_Area_Label3ID");
        header.createCell(3).setCellValue("PMS_Area_Label4ID");
        header.createCell(4).setCellValue("LNITMSEQ");
        header.createCell(5).setCellValue("Task Number");
        header.createCell(6).setCellValue("Task Area (Ha)");
        header.createCell(7).setCellValue("Query");

        int rowNum = 1;
        for (TaskOutput output : outputs) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(output.getPMS_Area_Label1ID());
            row.createCell(1).setCellValue(output.getPMS_Area_Label2ID());
            row.createCell(2).setCellValue(output.getPMS_Area_Label3ID());
            row.createCell(3).setCellValue(output.getPMS_Area_Label4ID());
            row.createCell(4).setCellValue(output.getLNITMSEQ());
            row.createCell(5).setCellValue(output.getPMS_No_Task());
            row.createCell(6).setCellValue(output.getPMS_Ha());
            row.createCell(7).setCellValue(output.getQuery());
        }

        for (int i = 0; i <= 7; i++) {
            sheet.autoSizeColumn(i);
        }

        try (FileOutputStream fos = new FileOutputStream(outputPath)) {
            workbook.write(fos);
        }

        workbook.close();
    }
}