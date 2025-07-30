package com.taskmaster.util;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.taskmaster.model.TaskInput;
import com.taskmaster.model.TaskOutput;

public class ExcelUtilTest {
    @Test
    public void testSomethingSimple() {
        assertEquals(2, 1 + 1);
    }
    @Test
    void testCalculateRoundedAreas_basicCase() {
        List<Double> result = ExcelUtil.calculateRoundedAreas(10.0, 3);
        assertEquals(3, result.size());
        double total = result.stream().mapToDouble(Double::doubleValue).sum();
        assertEquals(10.0, total, 0.01); // tolerance 0.01
    }
    @Test
    void testCalculateRoundedAreas_exactDivision() {
        List<Double> result = ExcelUtil.calculateRoundedAreas(9.0, 3);
        assertEquals(List.of(3.0, 3.0, 3.0), result);
    }
    @Test
    void testCalculateRoundedAreas_rounding() {
        List<Double> result = ExcelUtil.calculateRoundedAreas(7.0, 3);
        assertEquals(3, result.size());
        double total = result.stream().mapToDouble(Double::doubleValue).sum();
        assertEquals(7.0, total, 0.01);
    }
    @Test
    void testCalculateRoundedAreas_oneTask() {
        List<Double> result = ExcelUtil.calculateRoundedAreas(5.5, 1);
        assertEquals(List.of(5.5), result);
    }
    @Test
    void testCalculateRoundedAreas_zeroArea() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            ExcelUtil.calculateRoundedAreas(0, 5);
        });
        assertEquals("Total area must be greater than zero.", thrown.getMessage());
    }
    @Test
    void testCalculateRoundedAreas_invalidTaskCount() {
        assertThrows(IllegalArgumentException.class, () -> {
            ExcelUtil.calculateRoundedAreas(10.0, 0);
        });
    }
     @Test
    public void testGenerateTasks_basic() {
        TaskInput input = new TaskInput();
        input.setOu("OU01");
        input.setEstate("EST01");
        input.setBlock("B1234");  // maka divisi = "3"
        input.setTotalHa(10.0);
        input.setTaskCount(4);

        List<TaskOutput> outputs = ExcelUtil.generateTasks(input);

        assertEquals(4, outputs.size(), "Harusnya menghasilkan 4 task");

        // Periksa format task
        for (int i = 0; i < outputs.size(); i++) {
            TaskOutput out = outputs.get(i);
            assertEquals(String.format("T%03d", i + 1), out.getPMS_No_Task());
            assertNotNull(out.getQuery(), "Query SQL tidak boleh null");
            assertTrue(out.getQuery().toLowerCase().contains("insert"), "Query harus mengandung kata INSERT");
        }

        // Periksa akurasi total luas (dengan toleransi 0.01)
        double total = outputs.stream().mapToDouble(TaskOutput::getPMS_Ha).sum();
        assertEquals(10.0, total, 0.01, "Total luas task harus mendekati 10.0");
    }
}