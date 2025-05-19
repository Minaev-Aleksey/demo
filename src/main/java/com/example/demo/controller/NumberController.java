package com.example.demo.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.PriorityQueue;

@RestController
public class NumberController {

    @Operation(summary = "Find Nth smallest number from local XLSX file")
    @GetMapping("/findNthSmallest")
    public ResponseEntity<?> findNthSmallest(
            @Parameter(description = "Path to local XLSX file with numbers in a column")
            @RequestParam("filePath") String filePath,

            @Parameter(description = "N for Nth smallest number to find")
            @RequestParam("n") int n) throws IOException {

        if (n <= 0) {
            return ResponseEntity.badRequest().body("N must be positive");
        }

        File file = new File(filePath);
        if (!file.exists()) {
            return ResponseEntity.badRequest().body("File not found");
        }

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            PriorityQueue<Integer> maxHeap = new PriorityQueue<>(n, (a, b) -> b - a);

            for (Row row : sheet) {
                Cell cell = row.getCell(0);
                if (cell != null && cell.getCellType() == CellType.NUMERIC) {
                    int num = (int) cell.getNumericCellValue();

                    if (maxHeap.size() < n) {
                        maxHeap.offer(num);
                    } else if (num < maxHeap.peek()) {
                        maxHeap.poll();
                        maxHeap.offer(num);
                    }
                }
            }

            if (maxHeap.size() < n) {
                return ResponseEntity.badRequest().body("File doesn't contain enough numbers");
            }

            return ResponseEntity.ok(maxHeap.peek());
        }
    }
}