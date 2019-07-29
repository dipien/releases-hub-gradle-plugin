# Releases Hub Gradle Plugin
Gradle Plugin to automatically upgrade the projects dependencies and send a pull request with the changes

## Continuous Integration
|Branch|Status|Workflows|Insights|
| ------------- | ------------- | ------------- | ------------- |
|master|[![CircleCI](https://circleci.com/gh/maxirosson/releases-hub-gradle-plugin/tree/master.svg?style=svg&circle-token=80e1d7174b6216fa8403143541fd455672ba614c)](https://circleci.com/gh/maxirosson/releases-hub-gradle-plugin/tree/master)|[Workflows](https://circleci.com/gh/maxirosson/workflows/jdroid-googleplay-publisher/tree/master)|[Insights](https://circleci.com/build-insights/gh/maxirosson/jdroid-googleplay-publisher/master)|

## Setup

Add the following configuration to your `build.gradle`, replacing X.Y.Z by the [latest version](https://github.com/maxirosson/jdroid-googleplay-publisher/releases/latest)

    buildscript {
      repositories {
        jcenter()
      }
      dependencies {
        classpath("com.jdroidtools:releases-hub-gradle-plugin:X.Y.Z")
      }
    }
    
    apply plugin: "com.releaseshub.gradle.plugin"

## Donations
Help us to continue with this project:

[![Donate](https://www.paypalobjects.com/en_US/i/btn/btn_donate_LG.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=2UEBTRTSCYA9L)