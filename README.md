[![Dipien](https://raw.githubusercontent.com/dipien/dipien-component-builder/master/.github/dipien_logo.png)](http://www.dipien.com)

# Releases Hub Gradle Plugin
Gradle Plugin to automatically upgrade your Java/Kotlin project dependencies and send a GitHub pull request with the changes.

You can read [this](https://medium.com/dipien/automate-dependencies-upgrades-with-releases-hub-8eac6d7f43d6) blog post for more detailed information.

## How it works

1. Apply and configure the plugin according to your needs
2. Invoke the `upgradeDependencies` task on your CI tool (daily, weekly, monthly, as you wish)
3. If any of your dependencies is out-of-date, the plugin will create a pull request to update it.
4. Verify that your PR CI checks pass, scan the included release notes, perform manual tests, and merge the PR.

## Features
* Automatic Pull Request creation, including useful information whenever available:
  * new version release notes link
  * library documentation link
  * library source code link
  * library issue tracker link
  * library size
  * new permissions added by the library (only for android libraries)
* Support to configure which dependencies include and exclude, where to find their definitions, how many pull requests create and more.
* Support any Java/Kotlin project using Gradle.

![](wiki/pull_request.png)

## Setup

Add the following configuration to your root `build.gradle`, replacing X.Y.Z by the [latest version](https://github.com/dipien/releases-hub-gradle-plugin/releases/latest)

Using the plugins DSL + Groovy:

```groovy
plugins {
  id "com.dipien.releaseshub.gradle.plugin" version "X.Y.Z"
}
```


Using the plugins DSL + Kotlin DSL:

```kotlin
plugins {
  id("com.dipien.releaseshub.gradle.plugin").version("X.Y.Z")
}
```

Using legacy plugin application + Groovy:

```groovy
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("com.dipien:releases-hub-gradle-plugin:X.Y.Z")
    }
}
    
apply plugin: "com.dipien.releaseshub.gradle.plugin"
```

Using legacy plugin application + Kotlin DSL:

```kotlin
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("com.dipien:releases-hub-gradle-plugin:X.Y.Z")
    }
}
    
apply(plugin = "com.dipien.releaseshub.gradle.plugin")
```

## Configure

#### How to configure the properties

All the plugin configuration properties can be added using any of the following ways:

* Using the `releasesHub` extension on the build.gradle. For example:
```groovy
releasesHub {
    gitHubRepository = "sample"
}
```
* As a command line parameter. For example:
```
./gradlew listDependencies -PgitHubRepository=sample
```
* As a property on a `gradle.properties` file. For example:
```
gitHubRepository = "sample"
```
* As an extra property on the build.gradle. For example:
```
ext.gitHubRepository = "sample"
```
* As a System Environment property

#### Common Properties

###### Auto Detect Dependencies paths

Whether the plugin should automatically find the files where the dependencies are defined. This property is required. The default value is true

    autoDetectDependenciesPaths = true

The plugin automatically find dependencies on the following files:
* buildSrc/src/main/kotlin/Libs.kt
* buildSrc/src/main/kotlin/BuildLibs.kt
* gradle/libs.versions.toml
* settings.gradle.kts
* settings.gradle
* Any build.gradle or build.gradle.kts file on the root project and all the subprojects

###### Dependencies paths

The custom paths (relative to the project root directory) for the files where the dependencies are defined. This list is used in addition to the auto detected paths (if enabled). This property is optional. For example:

    dependenciesPaths = [
      "dependencies.gradle.kts", 
    ]
    
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
    
If you need to exclude the Gradle upgrade, use "gradle". For example: 

    excludes = ["gradle"]

## Usage

### Version Catalog (libs.versions.toml file) example

You can define your dependencies on the `libs.versions.toml` version catalog.

##### gradle/libs.versions.toml

```
[libraries]
kotlin = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.40"
kotlin-plugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.41"
```

##### Root build.gradle

```groovy
buildscript {
    dependencies {
        classpath(libs.kotlin.plugin)
    }
}

dependencies {
    implementation(libs.kotlin)
}

apply plugin: "kotlin"
apply plugin: "com.dipien.releaseshub.gradle.plugin"
```

### Version Catalog (settings.gradle.kts file) example

You can define your dependencies on the `settings.gradle.kts` version catalog.

##### settings.gradle.kts

```
dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            alias("kotlin").to("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.40")
            alias("kotlin-plugin").to("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.41")
        }
    }
}
```

##### Root build.gradle

```groovy
buildscript {
    dependencies {
        classpath(libs.kotlin.plugin)
    }
}

dependencies {
    implementation(libs.kotlin)
}

apply plugin: "kotlin"
apply plugin: "com.dipien.releaseshub.gradle.plugin"
```

### BuilsSrc example

You can define your dependencies on `/buildSrc/src/main/kotlin/Libs.kt` and `/buildSrc/src/main/kotlin/BuildLibs.kt` classes.

##### buildSrc/src/main/kotlin/Libs.kt

```kotlin
object Libs {
    const val KOTLIN = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.40"
}
```

##### buildSrc/src/main/kotlin/BuildLibs.kt

```kotlin
object BuildLibs {
    const val KOTLIN_PLUGIN = "org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.41"
}
```

##### Root build.gradle

```groovy
buildscript {
    dependencies {
        classpath(BuildLibs.KOTLIN_PLUGIN)
    }
}

dependencies {
    implementation(Libs.KOTLIN)
}

apply plugin: "kotlin"
apply plugin: "com.dipien.releaseshub.gradle.plugin"
```

See the [sample](https://github.com/dipien/releases-hub-gradle-plugin/tree/master/sample) for more details.

To automate your dependencies upgrades, you can follow this guide: [How to automate your dependencies upgrades with GitHub Actions](https://blog.dipien.com/how-to-automate-your-dependencies-upgrades-with-github-actions-bedf1337ca3f)

### Tasks

#### Validate dependencies

Validate all the dependencies.
The following validations are executed:

* All the dependencies defined on each `dependenciesPaths` are sorted alphabetically by `groupId:artifactId`
* There are not duplicated dependencies defined on each `dependenciesPaths`
* There are not dependencies with snapshot or dynamic versions assigned
* There are dependencies on `dependenciesPaths` but not used on the project
* There are dependencies explicitly declared on a .gradle(.kts) file instead of using buildSrc or Version Catalog.

```
./gradlew validateDependencies
```
##### Properties

###### Unused Excludes

The dependencies to exclude from the unused validation. 
You can define a `groupId` to match all the artifacts for that group id, or `groupId:artifactId` to match a particular artifact.
By default there aren't excluded dependencies.

    unusedExcludes = ["com.groupid1", "com.groupid2:artifact1"]
    
###### Unused extensions to search

The file extensions of the files where the artifact's packages will we search to find unused dependendencies. 
By default `[".kt", ".java", ".xml"]`

    unusedExtensionsToSearch = [".kt", ".java"]

#### List dependencies

Print all the dependencies that will be analyzed to upgrade.

    ./gradlew listDependencies
    
    
#### List dependencies to upgrade

Print all the dependencies that are upgradeable. A file `build/releasesHub/dependencies_to_upgrade_count.txt` is generated with the count of dependencies that are upgradeable. This could be useful for metrics.

    ./gradlew listDependenciesToUpgrade

#### Upgrade dependencies

This task creates a Github Pull Request for each groupId that have at least one dependency to upgrade. 

The following steps are executed for each `groupId`: 

* Creates the `headBranch` (`headBranchPrefix` + `groupId`)  (if not exists)
* Merge from the `baseBranch` to the `headBranch`
* Upgrade all the dependencies defined on the `dependenciesClassNames` for the `groupId`
* Create a commit for each dependency upgraded
* Push the previous commits to the `headBranch`
* Create a GitHub pull request from the `headBranch` to the `baseBranch`

```
./gradlew upgradeDependencies
```

##### Properties

###### Pull Request Enabled

Whether a pull request with all the upgrades should be created or not. The default value is `true`

    pullRequestEnabled = false

###### Pull Requests Max

The maximum amount of pull requests to create during the task execution. 
This is useful to avoid creating too much pull requests when you still have many dependencies to upgrade. The default value is `5`

    pullRequestsMax = 10
    
###### Pull Request Labels

The list of labels to assign when creating the pull request. Optional list.

    pullRequestLabels = ["dependencies"]

###### Pull Request Assignee

The user to be assigned to the pull request. Optional string.

    pullRequestAssignee = "octocat"
    
###### Pull Request Reviewers

The list of reviewers to assign when creating the pull request. Optional list.

    pullRequestReviewers = ["octocat", "hubot", "other_user"]
    
###### Pull Request Team Reviewers

The list of team reviewers to assign when creating the pull request. Optional list.

    pullRequestTeamReviewers = ["justice-league"]

###### Head Branch Prefix

The branch's prefix where the commit will be pushed. Also, the head branch's prefix of the pull request to create. Required String (only if `pullRequestEnabled` is `true`). The default value is `releases_hub/`.

    headBranchPrefix = "branch_name_"

###### Base Branch

The pull request base branch. Optional String. The default value is `master`.

    baseBranch = "master"

###### Git User Name

The Git user name used by the commit command. Optional String.

    gitUserName = "user"
    
###### Git User Email

The Git user email used by the commit command. Optional String.

    gitUserEmail = "email@mail.com"

###### GitHub Repository

The GitHub repository where the pull request will be created. Required String (only if `pullRequestEnabled` is `true`).

    gitHubRepository = "repo_owner/repo_name"

###### GitHub Repository Owner

The GitHub repository owner where the pull request will be created. Required String (only if `pullRequestEnabled` is `true` & `gitHubRepository` was not defined).

    gitHubRepositoryOwner = "repo_owner"

###### GitHub Repository Name

The GitHub repository name where the pull request will be created. Required String (only if `pullRequestEnabled` is `true` & `gitHubRepository` was not defined).

    gitHubRepositoryName = "repo_name"

###### GitHub Write Token

The GitHub write token needed to access the GitHub API to create the pull request. Follow these [steps](https://docs.github.com/en/github/authenticating-to-github/keeping-your-account-and-data-secure/creating-a-personal-access-token) to create your token.
We strongly recommend to not use the `releasesHub` extension for this property, to avoid exposing it on the git repository.
Required String (only if `pullRequestEnabled` is `true`).

    gitHubWriteToken = "123"


###### GitHub Api Host Name

The GitHub api host name needed to access the GitHub Enterprise. Optional String.

    gitHubApiHostName = "your.githubenterprise.com"


## Versioning

This project uses the [Semantic Versioning guidelines](http://semver.org/) for transparency into our release cycle.

## Sponsor this project

Sponsor this open source project to help us get the funding we need to continue working on it.

* [Donate with Bitcoin Lightning](https://getalby.com/p/dipien) ⚡️ [dipien@getalby.com](https://getalby.com/p/dipien)
* [Donate with credit card](http://kofi.dipien.com/)
* [Donate on Patreon](http://patreon.dipien.com/)
* [Become a member of Medium](https://membership.medium.dipien.com) [We will receive a portion of your membership fee]

## Follow us
* [Twitter](http://twitter.dipien.com)
* [Medium](http://medium.dipien.com)
* [Instagram](http://instagram.dipien.com)
* [TikTok](https://tiktok.dipien.com)
* [Pinterest](http://pinterest.dipien.com)
* [GitHub](http://github.dipien.com)

