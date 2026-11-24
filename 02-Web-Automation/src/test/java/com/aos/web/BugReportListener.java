package com.aos.web;

import org.testng.IConfigurationListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Writes test-reports/BugReport.html after the run.
 * One bug entry per FAILED test, filled only from what TestNG actually recorded.
 * Does not touch any test class. Register it once in MainClass:
 *     testng.addListener(new BugReportListener());
 */
public class BugReportListener implements ITestListener, IConfigurationListener {

    private static final List<String[]> BUGS = new ArrayList<>();
    private static final List<String[]> OTHER = new ArrayList<>(); // skipped tests and setup/teardown failures
    private static int passed = 0;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void onTestFailure(ITestResult r) {
        Throwable t = r.getThrowable();
        String message = t == null ? "" : String.valueOf(t.getMessage());
        int nl = message.indexOf('\n');
        if (nl > 0) message = message.substring(0, nl);
        String desc = r.getMethod().getDescription();
        synchronized (BUGS) {
            BUGS.add(new String[]{
                    String.format("BUG-%03d", BUGS.size() + 1),
                    r.getTestClass().getRealClass().getSimpleName() + "." + r.getMethod().getMethodName(),
                    desc == null || desc.isEmpty() ? "-" : desc,
                    t == null ? "-" : t.getClass().getSimpleName(),
                    message,
                    FMT.format(LocalDateTime.now()),
                    String.valueOf((r.getEndMillis() - r.getStartMillis())) + " ms"
            });
        }
    }

    @Override
    public void onTestSuccess(ITestResult r) {
        synchronized (BUGS) { passed++; }
    }

    @Override
    public void onTestSkipped(ITestResult r) {
        Throwable t = r.getThrowable();
        addOther("SKIPPED", r, t);
    }

    @Override
    public void onConfigurationFailure(ITestResult r) {
        addOther("SETUP/TEARDOWN FAILED", r, r.getThrowable());
    }

    @Override
    public void onConfigurationSkip(ITestResult r) {
        addOther("SETUP SKIPPED", r, r.getThrowable());
    }

    private void addOther(String kind, ITestResult r, Throwable t) {
        String m = t == null ? "-" : String.valueOf(t.getMessage());
        int nl = m.indexOf('\n');
        if (nl > 0) m = m.substring(0, nl);
        synchronized (BUGS) {
            OTHER.add(new String[]{kind,
                    r.getTestClass().getRealClass().getSimpleName() + "." + r.getMethod().getMethodName(), m});
        }
    }

    @Override
    public void onFinish(ITestContext ctx) {
        StringBuilder h = new StringBuilder();
        h.append("<!DOCTYPE html><html><head><meta charset='UTF-8'><title>AOS Bug Report</title>")
                .append("<style>body{font-family:Arial,sans-serif;margin:24px}table{border-collapse:collapse;width:100%}")
                .append("th,td{border:1px solid #bbb;padding:6px 8px;text-align:left;vertical-align:top}")
                .append("th{background:#b71c1c;color:#fff}tr:nth-child(even){background:#f6f6f6}</style></head><body>");
        h.append("<h1>AOS Automation - Bug Report</h1>")
                .append("<p><b>Failed-test diagnostic.</b> Each row is a test that failed in this run. Check each one by hand to decide if it is a site bug or a test problem.</p>");
        h.append("<p>Site: https://www.advantageonlineshopping.com/#/<br>Generated: ")
                .append(FMT.format(LocalDateTime.now()))
                .append("<br>Environment: Java ").append(esc(System.getProperty("java.version")))
                .append(", ").append(esc(System.getProperty("os.name")))
                .append("<br>Suite: ").append(esc(ctx.getSuite().getName()))
                .append("<br>Passed: ").append(passed).append(" | Failed: ").append(BUGS.size())
                .append(" | Skipped / setup problems: ").append(OTHER.size()).append("</p>");
        h.append("<table><tr><th>Bug ID</th><th>Test (Class.method)</th><th>Test Case ID / description</th>")
                .append("<th>Error type</th><th>Actual result (assertion / error message)</th><th>Time</th><th>Duration</th></tr>");
        synchronized (BUGS) {
            for (String[] b : BUGS) {
                h.append("<tr>");
                for (String c : b) h.append("<td>").append(esc(c)).append("</td>");
                h.append("</tr>");
            }
        }
        h.append("</table>");
        if (!OTHER.isEmpty()) {
            h.append("<h2>Skipped tests and setup/teardown problems</h2><table><tr><th>Type</th><th>Method</th><th>Message</th></tr>");
            synchronized (BUGS) {
                for (String[] o : OTHER) {
                    h.append("<tr>");
                    for (String c : o) h.append("<td>").append(esc(c)).append("</td>");
                    h.append("</tr>");
                }
            }
            h.append("</table>");
        }
        h.append("<p>Expected result and steps: see the matching manual bug report / test case sheet.</p></body></html>");
        try {
            Path dir = Paths.get("test-reports");
            Files.createDirectories(dir);
            Files.write(dir.resolve("BugReport.html"), h.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.err.println("Could not write BugReport.html: " + e.getMessage());
        }
    }

    private static String esc(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
