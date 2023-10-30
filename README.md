# 自動 販売機 (Jidō hanbaiki)

_From the Japanese: Vending Machine._

A Java library that can:
- download [Homebrew Bottles](https://docs.brew.sh/Bottles) for different architecture and macOS version, along with dependencies.
- modify the Mach-O headers of `.dylib` files, so they can be included in a single folder distribution.

## Dependencies

`org.gotson.jidouhanbaiki.dylib.Dylib` expects `dyld_info` and `install_name_tool` to be available in the `PATH`.

## Motivation

When building macOS apps with [Conveyor](https://www.hydraulic.dev/), native libraries can be packaged along with the application. Conveyor will conveniently sign those libraries along with your app.

In order to include `libheif` and `libjxl` into [Komga](https://komga.org) I needed to get hold of the native libraries, compiled for both Intel and ARM64, with the lowest possible target macOS version. Homebrew conveniently provides those binaries as Bottles.

macOS also requires that libraries use an absolute path for dependencies, for security reasons. Conveyor adds `@rpath` into the main binary, which can then be used in the libraries for dependents.

## How to use?

Check [SampleApplication.java](./app/src/main/java/org/gotson/jidouhanbaiki/app/SampleApplication.java).

The library is not available on Maven Central, but I would consider publishing if there's a need for it.