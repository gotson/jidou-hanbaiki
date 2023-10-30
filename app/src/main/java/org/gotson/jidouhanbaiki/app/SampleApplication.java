package org.gotson.jidouhanbaiki.app;

import org.gotson.jidouhanbaiki.dylib.Dylib;
import org.gotson.jidouhanbaiki.dylib.LibFinder;
import org.gotson.jidouhanbaiki.homebrew.BottleDispenser;
import org.gotson.jidouhanbaiki.homebrew.MacVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SampleApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(SampleApplication.class);

    public static void main(String[] args) throws IOException {
        var architectures = Map.of("aarch64", MacVersion.ARM64_LOWEST, "x64", MacVersion.X64_LOWEST);
        var dispenser = new BottleDispenser();
        var formulae = Set.of("jpeg-xl", "libheif");
        var libs = Set.of("libjxl.dylib", "libheif.dylib");

        for (String archName : architectures.keySet()) {
            LOGGER.info("Processing architecture: {}", archName);
            var libDir = Path.of("../output/homebrew/" + archName);
            var minVersion = dispenser.downloadWithDependencies(formulae, architectures.get(archName), libDir, true);

            LOGGER.info("Download finished. Minimum usable version: {}", minVersion);

            var targetDir = Path.of("../output/processed/" + archName);
            LibFinder.copyWithDependencies(libs, libDir, targetDir);

            try (var pathStream = Files.walk(targetDir)) {
                pathStream
                    .filter(p -> !Files.isDirectory(p))
                    .forEach(Dylib::updateDependencies);
            }

            try (var pathStream = Files.walk(targetDir)) {
                Set<Float> versions = pathStream
                    .filter(p -> !Files.isDirectory(p))
                    .map(Dylib::minVersion)
                    .collect(Collectors.toSet());
                LOGGER.info("Versions of libs: {}", versions);
            }
        }
    }
}
