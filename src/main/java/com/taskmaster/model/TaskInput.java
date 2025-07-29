package com.taskmaster.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskInput {
    private String ou;
    private String estate;
    private double totalHa;
    private String block;
    private int taskCount;
}
