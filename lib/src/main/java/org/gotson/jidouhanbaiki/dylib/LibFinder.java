package org.gotson.jidouhanbaiki.dylib;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Set;

public class LibFinder {
    private static final Logger LOGGER = LoggerFactory.getLogger(LibFinder.class);

    public static void copyWithDependencies(Set<String> rootLibs, Path rootDir, Path destDir) throws IOException {
        if (!Files.isDirectory(rootDir)) {
            throw new IllegalArgumentException("Not a directory: " + rootDir);
        }
        Files.createDirectories(destDir);

        var libs = new ArrayList<>(rootLibs);

        while (!libs.isEmpty()) {
            var lib = libs.remove(0);
            var depDest = locateAndCopy(rootDir, lib, destDir);
            if (depDest != null) {
                libs.addAll(Dylib.dependenciesNameFiltered(depDest));
            }
        }
    }

    private static Path locateAndCopy(Path rootDir, String fileName, Path destDir) throws IOException {
        var src = locate(rootDir, fileName);
        if (src == null) {
            LOGGER.error("Could not locate: " + fileName);
            return null;
        }
        var dest = destDir.resolve(fileName);
        LOGGER.info("Copy {} to {}", src, dest);
        Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
        return dest;
    }

    private static Path locate(Path rootDir, String fileName) throws IOException {
        try (var stream = Files.walk(rootDir)) {
            return stream
                .filter(p -> p.getFileName().toString().equals(fileName))
                .findFirst().orElse(null);
        }
    }
}
