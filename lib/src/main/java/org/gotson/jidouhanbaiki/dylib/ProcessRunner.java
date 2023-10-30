package org.gotson.jidouhanbaiki.dylib;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

public class ProcessRunner {
    String runAndWaitFor(String command) throws IOException, InterruptedException {
        Process p = Runtime.getRuntime().exec(command);
        p.waitFor();

        return getProcessOutput(p);
    }

    String runAndWaitFor(String command, long timeout, TimeUnit unit) throws IOException, InterruptedException {
        Process p = Runtime.getRuntime().exec(command);
        p.waitFor(timeout, unit);

        return getProcessOutput(p);
    }

    static String getProcessOutput(Process process) throws IOException {
        try (InputStream in = process.getInputStream()) {
            return IOUtils.toString(in, Charset.defaultCharset());
        }
    }
}