package org.gotson.jidouhanbaiki.dylib;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.List;

public class Dylib {
    private static final Logger LOGGER = LoggerFactory.getLogger(Dylib.class);
    protected static ProcessRunner processRunner = new ProcessRunner();

    public static Float minVersion(Path lib) {
        var command = "dyld_info -platform %s".formatted(lib);
        try {
            var output = processRunner.runAndWaitFor(command);
            var lines = output.lines().toList();
            var line = lines.get(3);
            var tokens = line.trim().split("\\s+");
            return Float.parseFloat(tokens[1]);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> dependenciesNameFiltered(Path lib) {
        return dependencies(lib)
            .stream()
            .filter(s -> !s.startsWith("/usr/"))
            .map(s -> Path.of(s).getFileName().toString())
            .toList();
    }

    public static List<String> dependencies(Path lib) {
        var command = "dyld_info -dependents %s".formatted(lib);
        try {
            var output = processRunner.runAndWaitFor(command);
            var lines = output.lines().toList();
            return lines.subList(3, lines.size()).stream().map(String::trim).toList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void updateDependencies(Path lib) {
        for (String original : dependencies(lib)) {
            if (original.startsWith("@@HOMEBREW")) {
                var depName = Path.of(original).getFileName().toString();
                var loaderPath = "@rpath/" + depName;
                var command = "install_name_tool -change %s %s %s".formatted(original, loaderPath, lib);
                try {
                    LOGGER.info("Run: {}", command);
                    processRunner.runAndWaitFor(command);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
