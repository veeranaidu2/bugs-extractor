package org.dsrt;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import com.opencsv.CSVWriter;
import model.Issue;
import org.kohsuke.github.*;

public class IssueContainer {
    public static void main(String[] args) throws IOException {

        if(args.length == 0){
            System.out.println("Please enter the project name. The command should go : bugs-extractor ");
            System.out.println("Working Directory = " + System.getProperty("user.dir"));
        }

        String projectName = args[0];
        GitHubBuilder builder = new GitHubBuilder();
        Path authTokenFile = Path.of("resources/GitHubTokens/authToken.token");
        String oauthTokenValue = Files.readString(authTokenFile);
        GitHub github = builder.withOAuthToken(oauthTokenValue).build();

        GHRepository ghRepository =
                github.getRepository(projectName);
        System.out.println("Connected successfully? -> " +
                github.isCredentialValid() +
                " Current Repo " + ghRepository.getFullName());

        List<Issue> issuesWrappers =
                new ArrayList<Issue>();
        GHIssueState ghIssueState = GHIssueState.CLOSED;
        PagedIterable <GHIssue> ghIssues =
                ghRepository.listIssues(ghIssueState);
        System.out.println("Size: " + ghIssues.asList().size());

        for (GHIssue ghIssue: ghIssues) {
            Collection< GHLabel > ghLabels = ghIssue.getLabels();
            for (GHLabel ghLabel: ghLabels) {
                if (ghLabel.getName().contains("bug")) {
                    Issue issue = new Issue();
                    issue.issueId = ghIssue.getNumber();
                    issue.body = ghIssue.getBody();
                    issue.title = ghIssue.getTitle();
                    issue.bugLabel = ghLabel.getName();
                    GHIssueState state = ghIssue.getState();
                    issue.state = state.name();
                    System.out.println("Added " + issue.title +
                            " size is " + issuesWrappers.size());
                    issuesWrappers.add(issue);

                    GHEventPayload.IssueComment issueEventPayload =
                            new GHEventPayload.IssueComment();
                    issueEventPayload.setRepository(ghRepository);
                    issueEventPayload.setIssue(ghIssue);
                    System.out.println(" Action: " +
                            issueEventPayload.getAction() + " Comment: " +
                            issueEventPayload.getComment());
                }
            }
        }

        System.out.println("Starting CSV Job for Issues");
        CSVWriter csvWriter = null;
        try {
            csvWriter = new CSVWriter(new FileWriter(projectName.replace("/", "_")+"_issues.csv"));
            for (Issue issuewrapper: issuesWrappers) {
                String[] row = new String[] {
                        String.
                                valueOf(issuewrapper.issueId), issuewrapper.title,
                        issuewrapper.body, issuewrapper.state,
                        issuewrapper.bugLabel
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