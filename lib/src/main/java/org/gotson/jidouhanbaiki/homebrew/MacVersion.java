package org.gotson.jidouhanbaiki.homebrew;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public enum MacVersion {
    ARM64_14("arm64_sonoma", "arm", 14),
    ARM64_13("arm64_ventura", "arm", 13),
    ARM64_12("arm64_monterey", "arm", 12),
    ARM64_11("arm64_big_sur", "arm", 11),
    ARM64_LOWEST("arm64_lowest", "arm", null),
    X64_14("sonoma", "x64", 14),
    X64_13("ventura", "x64", 13),
    X64_12("monterey", "x64", 12),
    X64_11("big_sur", "x64", 11),
    X64_LOWEST("lowest", "x64", null);

    private final String key;
    private final String architecture;
    private final Integer version;

    MacVersion(String key, String architecture, Integer version) {
        this.key = key;
        this.architecture = architecture;
        this.version = version;
    }

    public static MacVersion match(Set<MacVersion> available, MacVersion target) {
        if (target.version == null) {
            List<MacVersion> candidates = Arrays.stream(MacVersion.values())
                .filter(v -> v.architecture.equals(target.architecture))
                .filter(v -> v.version != null)
                .sorted(Comparator.comparing(v -> v.version))
                .toList();
            for (MacVersion candidate : candidates) {
                if (available.contains(candidate)) return candidate;
            }
            return null;
        } else {
            if (available.contains(target)) {
                return target;
            } else {
                return null;
            }
        }
    }

    public String getKey() {
        return key;
    }

    public String getArchitecture() {
        return architecture;
    }

    public Integer getVersion() {
        return version;
    }
}
