package org.gotson.jidouhanbaiki.dylib;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DylibTest {

    @AfterEach
    public void afterEach() {
        Dylib.processRunner = new ProcessRunner();
    }

    @Test
    public void testDyldInfoVersion() throws Exception {
        var mockResponse = Files.readString(Path.of(getClass().getResource("dyld_info/platform.txt").toURI()));
        var mockRunner = mock(ProcessRunner.class);
        Dylib.processRunner = mockRunner;
        when(mockRunner.runAndWaitFor(any())).thenReturn(mockResponse);

        var minVersion = Dylib.minVersion(Path.of("aLib"));

        assertThat(minVersion).isEqualTo(11.0f);
    }

    @Test
    public void testDyldInfoDependencies() throws Exception {
        var mockResponse = Files.readString(Path.of(getClass().getResource("dyld_info/dependents.txt").toURI()));
        var mockRunner = mock(ProcessRunner.class);
        Dylib.processRunner = mockRunner;
        when(mockRunner.runAndWaitFor(any())).thenReturn(mockResponse);

        var dependencies = Dylib.dependencies(Path.of("aLib"));

        assertThat(dependencies).containsExactlyInAnyOrder(
            "@@HOMEBREW_PREFIX@@/opt/highway/lib/libhwy.1.dylib",
            "@@HOMEBREW_PREFIX@@/opt/brotli/lib/libbrotlidec.1.dylib",
            "@@HOMEBREW_PREFIX@@/opt/brotli/lib/libbrotlicommon.1.dylib",
            "@@HOMEBREW_PREFIX@@/opt/brotli/lib/libbrotlienc.1.dylib",
            "@@HOMEBREW_PREFIX@@/opt/little-cms2/lib/liblcms2.2.dylib",
            "/usr/lib/libc++.1.dylib",
            "/usr/lib/libSystem.B.dylib"
        );
    }

    @Test
    public void testDyldInfoDependenciesNameFiltered() throws Exception {
        var mockResponse = Files.readString(Path.of(getClass().getResource("dyld_info/dependents.txt").toURI()));
        var mockRunner = mock(ProcessRunner.class);
        Dylib.processRunner = mockRunner;
        when(mockRunner.runAndWaitFor(any())).thenReturn(mockResponse);

        var dependencies = Dylib.dependenciesNameFiltered(Path.of("aLib"));

        assertThat(dependencies).containsExactlyInAnyOrder(
            "libhwy.1.dylib",
            "libbrotlidec.1.dylib",
            "libbrotlicommon.1.dylib",
            "libbrotlienc.1.dylib",
            "liblcms2.2.dylib"
        );
    }
}