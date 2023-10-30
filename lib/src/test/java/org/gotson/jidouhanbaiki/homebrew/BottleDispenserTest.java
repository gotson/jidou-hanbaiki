package org.gotson.jidouhanbaiki.homebrew;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static com.github.tomakehurst.wiremock.client.WireMock.any;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.assertj.core.api.Assertions.assertThat;

@WireMockTest(httpPort = 12345)
class BottleDispenserTest {

    private final BottleDispenser dispenser = new BottleDispenser("http://localhost:12345");

    @Test
    void fetchInfo() throws Exception {
        var mockResponse = Files.readString(Path.of(getClass().getResource("api/formula-jpeg-xl.json").toURI()));
        stubFor(
            any(urlMatching("/api/formula/jpeg-xl.json"))
                .willReturn(ok(mockResponse))
        );

        var formula = dispenser.fetchInfo("jpeg-xl");

        assertThat(formula.bottles()).hasSize(8);
        assertThat(formula.bottles().get(MacVersion.ARM64_11))
            .hasFieldOrPropertyWithValue("sha256", "dfb413003b3ecd2f703b7298362b3cfcb3228e8ee5c71861d6e7c40a85c21fda")
            .hasFieldOrPropertyWithValue("url", "https://ghcr.io/v2/homebrew/core/jpeg-xl/blobs/sha256:dfb413003b3ecd2f703b7298362b3cfcb3228e8ee5c71861d6e7c40a85c21fda");

        assertThat(formula.dependencies()).containsExactlyInAnyOrder(
            "brotli",
            "giflib",
            "highway",
            "imath",
            "jpeg-turbo",
            "libpng",
            "little-cms2",
            "openexr",
            "webp"
        );
    }
}