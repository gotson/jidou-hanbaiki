package org.gotson.jidouhanbaiki.homebrew;

import java.util.List;
import java.util.Map;

public record Formula(
    String formula,
    Map<MacVersion, Bottle> bottles,
    List<String> dependencies
) {
}
