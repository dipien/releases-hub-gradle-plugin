# Releases Hub Gradle Plugin
Gradle Plugin to automatically upgrade the project dependencies and send a GiHhub pull request with the changes

## Continuous Integration
|Branch|Status|Workflows|Insights|
| ------------- | ------------- | ------------- | ------------- |
|master|[![CircleCI](https://circleci.com/gh/maxirosson/releases-hub-gradle-plugin/tree/master.svg?style=svg&circle-token=80e1d7174b6216fa8403143541fd455672ba614c)](https://circleci.com/gh/maxirosson/releases-hub-gradle-plugin/tree/master)|[Workflows](https://circleci.com/gh/maxirosson/workflows/jdroid-googleplay-publisher/tree/master)|[Insights](https://circleci.com/build-insights/gh/maxirosson/jdroid-googleplay-publisher/master)|

## Setup

Add the following configuration to your `build.gradle`, replacing X.Y.Z by the [latest version](https://github.com/maxirosson/releases-hub-gradle-plugin/releases/latest)

```groovy
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath("com.jdroidtools:releases-hub-gradle-plugin:X.Y.Z")
    }
}
    
apply plugin: "com.releaseshub.gradle.plugin"
```

All the plugin configuration properties can be added using any of the following ways:

* Using the `releasesHub` extension on the build.gradle. For example:

```groovy
releasesHub {
    gitHubWriteToken = "123"
}
```

* As a command line parameter. For example:
```
./gradlew listDependencies -PgitHubWriteToken=123
```
* As a property on a `gradle.properties` file. For example:
```
gitHubWriteToken = "123"
```
* As an extra property on the build.gradlee. For example:
```
ext.gitHubWriteToken = "123"
```
* As a System Environment property

#### Common Properties

###### Dependencies class names

The class names where the dependencies are defined. The default value is `["Libs.kt", "BuildLibs.kt"]`. This property is required
    
    dependenciesClassNames = ["Libs.kt", "BuildLibs.kt"]
    
###### Includes

The dependencies to include. 
You can define a `groupId` to match all the artifacts for that group id, or `groupId:artifactId` to match a particular artifact.
By default all the dependencies found on `dependenciesClassNames` are included.

    includes = ["com.groupid1", "com.groupid2:artifact1"]
    
###### Excludes

The dependencies to exclude. 
You can define a `groupId` to match all the artifacts for that group id, or `groupId:artifactId` to match a particular artifact.
By default there aren't excluded dependencies.

    excludes = ["com.groupid1", "com.groupid2:artifact1"]

## Usage

We suggest to define your dependencies on `/buildSrc/src/main/kotlin/Libs.kt` and `/buildSrc/src/main/kotlin/BuildLibs.kt` classes. For example:

##### Libs.kt

```kotlin
object Libs {
    const val kotlin = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.40"
}
```

##### BuildLibs.kt

```kotlin
object BuildLibs {
    const val kotlin_plugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.41"
}
```

##### build.gradle

```groovy
apply plugin: "kotlin"
apply plugin: "com.jdroidtools.releaseshub.gradle.plugin"

buildscript {
    dependencies {
        classpath(BuildLibs.kotlin_plugin)
    }
}

dependencies {
    compile(Libs.kotlin)
}
```

### Tasks

#### List dependencies

Print all the dependencies that will be analyzed to upgrade.

    ./gradlew listDependencies
    
    
#### List dependencies to upgrade

Print all the dependencies that are upgradeable.

    ./gradlew listDependenciesToUpgrade
    
#### Upgrade dependencies

This task execute the following steps if the project have at least one dependency to upgrade:

* Creates the `headBranch` (if not exists)
* Merge from the `baseBranch` to the `headBranch`
* Upgrade all the dependencies on the `dependenciesClassNames`
* Commit all the modified files
* Push the previous commit to the `headBranch`
* Create a GitHub pull request from the `headBranch` to the `baseBranch`

```
./gradlew upgradeDependencies
```

##### Properties

###### Head Branch

The branch where the commit will be pushed. Also, the head branch of the pull request to create. Required String (only if `pullRequestEnabled` is `true`).

    headBranch = "branch_name"

###### Base Branch

The pull request base branch. Optional String. The default value is `master`.

    baseBranch = "master"

###### GitHub User Name

The GitHub user name used by the commit command. Optional String.

    gitHubUserName = "user"
    
###### GitHub User Email

The GitHub user email used by the commit command. Optional String.

    gitHubUserEmail = "email@mail.com"

###### Commit Message

The commit message. Required String (only if `pullRequestEnabled` is `true`). The default value is `Upgraded dependencies`

    commitMessage = "Upgraded dependencies"

###### Pull Request Title

The pull request title. Optional String. The default value is the `commitMessage` property value.

    pullRequestTitle = "Upgraded dependencies"

###### Pull Request Enabled

Whether a pull request with all the upgrades should be created or not. The default value is `false`

    pullRequestEnabled = true

###### GitHub Repository Owner

The GitHub repository owner where the pull request will be created. Required String (only if `pullRequestEnabled` is `true`).

    gitHubRepositoryOwner = "repo_owner"

###### GitHub Repository Name

The GitHub repository name where the pull request will be created. Required String (only if `pullRequestEnabled` is `true`).

    gitHubRepositoryName = "repo_name"

###### GitHub Write Token

The GitHub write token needed to access the GitHub API to create the pull request. 
We strongly recommend to not use the `releasesHub` extension for this property, to avoid exposing it on the git repository.
Required String (only if `pullRequestEnabled` is `true`).

    gitHubWriteToken = "123"

## Donations
Help us to continue with this project:

[![Donate](https://www.paypalobjects.com/en_US/i/btn/btn_donate_LG.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=2UEBTRTSCYA9L)
