package org.gotson.jidouhanbaiki.homebrew;

import com.fasterxml.jackson.databind.JsonNode;
import feign.Param;
import feign.RequestLine;

public interface HomebrewApi {
    @RequestLine("GET /api/formula/{formula}.json")
    JsonNode formula(@Param("formula") String formula);
}
