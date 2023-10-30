package org.gotson.jidouhanbaiki.homebrew;

import feign.Feign;
import feign.jackson.JacksonDecoder;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.codehaus.plexus.archiver.tar.TarGZipUnArchiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;

public class BottleDispenser {
    private static final Logger LOGGER = LoggerFactory.getLogger(BottleDispenser.class);
    private static final String DEFAULT_URL = "https://formulae.brew.sh";
    private final HomebrewApi api;
    private final String apiUrl;

    public BottleDispenser() {
        this(DEFAULT_URL);
    }

    public BottleDispenser(String apiUrl) {
        this.apiUrl = apiUrl;
        api = Feign.builder()
            .decoder(new JacksonDecoder())
            .target(HomebrewApi.class, apiUrl);
    }

    public Formula fetchInfo(String formula) {
        var rootNode = api.formula(formula);
        var bottlesNode = rootNode.at("/bottle/stable/files");

        var bottles = new HashMap<MacVersion, Bottle>();
        for (MacVersion macVersion : MacVersion.values()) {
            var bottle = bottlesNode.get(macVersion.getKey());
            if (bottle != null) {
                bottles.put(macVersion, new Bottle(bottle.get("sha256").asText(), bottle.get("url").asText()));
            }
        }

        var dependencies = new ArrayList<String>();
        var iterDeps = rootNode.withArray("dependencies").elements();
        while (iterDeps.hasNext()) {
            dependencies.add(iterDeps.next().asText());
        }

        return new Formula(formula, bottles, dependencies);
    }

    public MacVersion download(Formula formula, MacVersion version, Path destDir, boolean unpack) throws IOException {
        var versionExact = MacVersion.match(formula.bottles().keySet(), version);
        if (versionExact == null) {
            throw new IllegalArgumentException("No matching version found");
        }
        LOGGER.info("Download bottle formula:{}, version:{}", formula.formula(), versionExact);
        var bottle = formula.bottles().get(versionExact);
        if (bottle == null) {
            throw new IllegalArgumentException("No bottle for this version");
        }

        var tmpFile = Files.createTempFile("", "");
        var url = new URL(bottle.url());
        var conn = url.openConnection();
        conn.addRequestProperty("Authorization", "Bearer QQ==");
        try (var inputStream = conn.getInputStream();
             var outputStream = new FileOutputStream(tmpFile.toFile())
        ) {
            IOUtils.copyLarge(inputStream, outputStream);
        }

        var sha256 = DigestUtils.sha256Hex(Files.newInputStream(tmpFile));
        if (!sha256.equals(bottle.sha256())) {
            throw new RuntimeException("sha256 do not match");
        }

        Path targetDir = destDir.resolve(versionExact.name());
        Files.createDirectories(targetDir);
        if (unpack) {
            var unarchiver = new TarGZipUnArchiver(tmpFile.toFile());
            unarchiver.setDestDirectory(targetDir.toFile());
            unarchiver.extract();
        } else {
            Files.move(tmpFile, targetDir.resolve(formula.formula() + "_" + versionExact.name() + ".tar.gz"));
        }

        return versionExact;
    }

    public MacVersion downloadWithDependencies(Collection<String> formulae, MacVersion version, Path destDir, boolean unpack) throws IOException {
        var downloaded = new HashSet<String>();
        var depsToDownload = new ArrayList<String>();
        var allVersions = new HashSet<MacVersion>();
        for (String formula : new HashSet<>(formulae)) {
            var formulaInfo = fetchInfo(formula);
            var versionExact = download(formulaInfo, version, destDir, unpack);
            allVersions.add(versionExact);
            downloaded.add(formulaInfo.formula());
            depsToDownload.addAll(formulaInfo.dependencies());
        }
        while (!depsToDownload.isEmpty()) {
            var dep = depsToDownload.remove(0);
            if (!downloaded.contains(dep)) {
                var depInfo = fetchInfo(dep);
                var versionExact = download(depInfo, version, destDir, unpack);
                allVersions.add(versionExact);
                downloaded.add(depInfo.formula());
                depsToDownload.addAll(depInfo.dependencies());
            }
        }

        Optional<MacVersion> greatestVersion = allVersions.stream().max(Comparator.comparing(MacVersion::getVersion));
        return greatestVersion.orElse(null);
    }

    public String getApiUrl() {
        return apiUrl;
    }
}
