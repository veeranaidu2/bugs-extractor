package org.dsrt;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import model.CommitIssue;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class IssueExtractor {

    public static void main(String[] args) throws IOException {
        ArrayList<CommitIssue> wrappers = new ArrayList<CommitIssue>();
        CSVReader reader = new CSVReader(new FileReader("CommitSummary.csv"), ',');

        String[] record = null;
        while ((record = reader.readNext()) != null) {
            String bugNumber = "";
            if (record[2] != null && !record[2].isEmpty()) {
                System.out.println("records - " + record[2]);
                int bugIndex = record[2].indexOf("#");
                if (bugIndex > 0) {
                    bugIndex++;
                    while (bugIndex < record[2].length() &&
                            Character.isDigit(record[2].charAt(bugIndex))) {
                        bugNumber = bugNumber + record[2].charAt(bugIndex);
                        bugIndex++;
                    }
                    CommitIssue commitIssue = new CommitIssue(record[1], record[2], new URL(record[3]), record[0], bugNumber);
                    wrappers.add(commitIssue);
                    System.out.println("bug# - " + bugNumber);
                }
            }
        }
        System.out.println("Starting CSV Job for Commits Summary");
        CSVWriter csvWriter = null;
        try {
            csvWriter = new CSVWriter(new FileWriter("CommitSummaryPerfect.csv"));
            for (CommitIssue wrapper: wrappers) {
                String[] row = new String[] {
                        wrapper.getDate(),
                        wrapper.getIssueId(), wrapper.getFileName(),
                        wrapper.getMessage(), wrapper.getUrl().toString()
                };
                csvWriter.writeNext(row);
            }
        } catch (Exception ee) {
            ee.printStackTrace();
        } finally {
            try {
                System.out.println("Completed!");
                csvWriter.close();
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
    }

}
