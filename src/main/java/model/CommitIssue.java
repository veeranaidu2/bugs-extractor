package model;

import java.net.URL;

public class CommitIssue {
    String fileName;
    String message;
    URL url;
    String date;
    String issueId;

    public CommitIssue(String fileName, String message, URL url, String date, String issueId) {
        this.fileName = fileName;
        this.message = message;
        this.url = url;
        this.date = date;
        this.issueId = issueId;
    }



    public String getFileName() {
        return fileName;
    }

    public String getMessage() {
        return message;
    }

    public URL getUrl() {
        return url;
    }

    public String getDate() {
        return date;
    }

    public String getIssueId() {
        return issueId;
    }
}

