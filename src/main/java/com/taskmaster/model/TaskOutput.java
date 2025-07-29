package com.taskmaster.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskOutput {
    private String PMS_Area_Label1ID;
    private String PMS_Area_Label2ID;
    private String PMS_Area_Label3ID;
    private String PMS_Area_Label4ID;
    private int LNITMSEQ;
    private String PMS_No_Task;
    private double PMS_Ha;
    private String query;
}
