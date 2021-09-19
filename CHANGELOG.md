# Change Log

## [v2.1.0](https://github.com/dipien/releases-hub-gradle-plugin/tree/v2.1.0) (2021-09-18)
[Full Changelog](https://github.com/dipien/releases-hub-gradle-plugin/compare/v2.0.3...v2.1.0)

**Implemented enhancements:**

- Include a link to all the official development resources on the PR description [\#112](https://github.com/dipien/releases-hub-gradle-plugin/issues/112)
- Add link to Android permission documentation [\#83](https://github.com/dipien/releases-hub-gradle-plugin/issues/83)

**Fixed bugs:**

- com.jdroid.github.client.RequestException: Bad credentials \(401\) [\#107](https://github.com/dipien/releases-hub-gradle-plugin/issues/107)
- Gradle Distribution changed as part of other upgrades [\#105](https://github.com/dipien/releases-hub-gradle-plugin/issues/105)

## [v2.0.3](https://github.com/dipien/releases-hub-gradle-plugin/tree/v2.0.3) (2021-08-29)
[Full Changelog](https://github.com/dipien/releases-hub-gradle-plugin/compare/v2.0.2...v2.0.3)

**Fixed bugs:**

- Stash changes before upgrading dependencies [\#110](https://github.com/dipien/releases-hub-gradle-plugin/issues/110)

## [v2.0.2](https://github.com/dipien/releases-hub-gradle-plugin/tree/v2.0.2) (2021-03-26)
[Full Changelog](https://github.com/dipien/releases-hub-gradle-plugin/compare/v2.0.1...v2.0.2)

**Fixed bugs:**

- Unexpected behaviour when maven\_metadata.xml is not properly formatted [\#104](https://github.com/dipien/releases-hub-gradle-plugin/issues/104)

## [v2.0.1](https://github.com/dipien/releases-hub-gradle-plugin/tree/v2.0.1) (2021-03-25)
[Full Changelog](https://github.com/dipien/releases-hub-gradle-plugin/compare/v2.0.0...v2.0.1)

**Fixed bugs:**

- Some upgrades not detected due to an issue when comparing versions [\#103](https://github.com/dipien/releases-hub-gradle-plugin/issues/103)

## [v2.0.0](https://github.com/dipien/releases-hub-gradle-plugin/tree/v2.0.0) (2021-03-01)
[Full Changelog](https://github.com/dipien/releases-hub-gradle-plugin/compare/v1.7.0...v2.0.0)

**Closed issues:**

- Change dependenciesClassNames property to required [\#100](https://github.com/dipien/releases-hub-gradle-plugin/issues/100)
- Migrate to new "com.dipien" groupId & "com.dipien.releaseshub.gradle.plugin" plugin id [\#99](https://github.com/dipien/releases-hub-gradle-plugin/issues/99)
- Enable pullRequestEnabled property by default [\#94](https://github.com/dipien/releases-hub-gradle-plugin/issues/94)
- Remove headBranch, commitMessage & pullRequestTitle extension properties [\#86](https://github.com/dipien/releases-hub-gradle-plugin/issues/86)
- Remove countDependenciesToUpgrade task [\#85](https://github.com/dipien/releases-hub-gradle-plugin/issues/85)

## [v1.7.0](https://github.com/dipien/releases-hub-gradle-plugin/tree/v1.7.0) (2021-02-28)
[Full Changelog](https://github.com/dipien/releases-hub-gradle-plugin/compare/v1.6.1...v1.7.0)

**Implemented enhancements:**

- Include release date on PR description [\#88](https://github.com/dipien/releases-hub-gradle-plugin/issues/88)

## [v1.6.1](https://github.com/dipien/releases-hub-gradle-plugin/tree/v1.6.1) (2020-10-21)
[Full Changelog](https://github.com/dipien/releases-hub-gradle-plugin/compare/v1.6.0...v1.6.1)

**Fixed bugs:**

- Fix memory leak on ProjectExtensions.propertyResolverCache [\#93](https://github.com/dipien/releases-hub-gradle-plugin/issues/93)

**Closed issues:**

- Running upgradeDependencies as a GitHub Action [\#87](https://github.com/dipien/releases-hub-gradle-plugin/issues/87)

## [v1.6.0](https://github.com/dipien/releases-hub-gradle-plugin/tree/v1.6.0) (2020-05-31)
[Full Changelog](https://github.com/dipien/releases-hub-gradle-plugin/compare/v1.5.1...v1.6.0)

**Closed issues:**

- Sort dependencies to upgrade by the days since last update [\#76](https://github.com/dipien/releases-hub-gradle-plugin/issues/76)

## [v1.5.1](https://github.com/dipien/releases-hub-gradle-plugin/tree/v1.5.1) (2020-05-21)
[Full Changelog](https://github.com/dipien/releases-hub-gradle-plugin/compare/v1.5.0...v1.5.1)

**Implemented enhancements:**

- Generate dependencies\_to\_upgrade\_count.txt file [\#78](https://github.com/dipien/releases-hub-gradle-plugin/issues/78)

**Fixed bugs:**

- Avoid fails on timeouts when fetching upgrades [\#77](https://github.com/dipien/releases-hub-gradle-plugin/issues/77)

## [v1.5.0](https://github.com/dipien/releases-hub-gradle-plugin/tree/v1.5.0) (2020-05-15)
[Full Changelog](https://github.com/dipien/releases-hub-gradle-plugin/compare/v1.4.4...v1.5.0)

**Implemented enhancements:**

- New countDependenciesToUpgrade task [\#75](https://github.com/dipien/releases-hub-gradle-plugin/issues/75)
- Improve gradle upgrade [\#59](https://github.com/dipien/releases-hub-gradle-plugin/issues/59)
- Check for resolved dependencies not defined on dependenciesClassNames [\#11](https://github.com/dipien/releases-hub-gradle-plugin/issues/11)

## [v1.4.4](https://github.com/dipien/releases-hub-gradle-plugin/tree/v1.4.4) (2020-05-07)
[Full Changelog](https://github.com/dipien/releases-hub-gradle-plugin/compare/v1.4.3...v1.4.4)

**Fixed bugs:**

- Run a hard reset after a failed merge [\#73](https://github.com/dipien/releases-hub-gradle-plugin/issues/73)

## [v1.4.3](https://github.com/dipien/releases-hub-gradle-plugin/tree/v1.4.3) (2020-04-30)
[Full Changelog](https://github.com/dipien/releases-hub-gradle-plugin/compare/v1.4.2...v1.4.3)

**Fixed bugs:**

- Don't fail if there is a conflict when merging [\#72](https://github.com/dipien/releases-hub-gradle-plugin/issues/72)

## [v1.4.2](https://github.com/dipien/releases-hub-gradle-plugin/tree/v1.4.2) (2020-04-11)
[Full Changelog](https://github.com/dipien/releases-hub-gradle-plugin/compare/v1.4.1...v1.4.2)

**Implemented enhancements:**

- Includes & excludes props are now optional [\#70](https://github.com/dipien/releases-hub-gradle-plugin/issues/70)
- Improved git commands logs [\#69](https://github.com/dipien/releases-hub-gradle-plugin/issues/69)

**Fixed bugs:**

- Fix excludes property default value when it's empty [\#68](https://github.com/dipien/releases-hub-gradle-plugin/issues/68)

## [v1.4.1](https://github.com/dipien/releases-hub-gradle-plugin/tree/v1.4.1) (2020-03-28)
[Full Changelog](https://github.com/dipien/releases-hub-gradle-plugin/compare/v1.4.0...v1.4.1)

**Fixed bugs:**

- This release is the same than v1.4.0. Ignore it. [\#71](https://github.com/dipien/releases-hub-gradle-plugin/issues/71)

## [v1.4.0](https://github.com/dipien/releases-hub-gradle-plugin/tree/v1.4.0) (2020-03-13)
[Full Changelog](https://github.com/dipien/releases-hub-gradle-plugin/compare/v1.3.1...v1.4.0)

**Implemented enhancements:**

- Display android permissions [\#61](https://github.com/dipien/releases-hub-gradle-plugin/issues/61)
- Display artifact size [\#60](https://github.com/dipien/releases-hub-gradle-plugin/issues/60)

## [v1.3.1](https://github.com/dipien/releases-hub-gradle-plugin/tree/v1.3.1) (2019-12-12)
[Full Changelog](https://github.com/dipien/releases-hub-gradle-plugin/compare/v1.3.0...v1.3.1)

**Fixed bugs:**

- Upgrades not detected for some artifacts  [\#49](https://github.com/dipien/releases-hub-gradle-plugin/issues/49)

## [v1.3.0](https://github.com/dipien/releases-hub-gradle-plugin/tree/v1.3.0) (2019-12-08)
[Full Changelog](https://github.com/dipien/releases-hub-gradle-plugin/compare/v1.2.1...v1.3.0)

**Implemented enhancements:**

- Improve git errors logs [\#48](https://github.com/dipien/releases-hub-gradle-plugin/issues/48)
- Support GitHub Enterprise [\#47](https://github.com/dipien/releases-hub-gradle-plugin/issues/47)
- Display issue tracker url for artifacts upgrades [\#41](https://github.com/dipien/releases-hub-gradle-plugin/issues/41)
- Check for unused libraries [\#35](https://github.com/dipien/releases-hub-gradle-plugin/issues/35)

## [v1.2.1](https://github.com/dipien/releases-hub-gradle-plugin/tree/v1.2.1) (2019-11-01)
[Full Changelog](https://github.com/dipien/releases-hub-gradle-plugin/compare/v1.2.0...v1.2.1)

**Fixed bugs:**

- Circle CI workflow is not executed under race conditions [\#44](https://github.com/dipien/releases-hub-gradle-plugin/issues/44)

## [v1.2.0](https://github.com/dipien/releases-hub-gradle-plugin/tree/v1.2.0) (2019-10-15)
[Full Changelog](https://github.com/dipien/releases-hub-gradle-plugin/compare/v1.1.0...v1.2.0)

**Implemented enhancements:**

- Support upgrading Gradle version [\#27](https://github.com/dipien/releases-hub-gradle-plugin/issues/27)
- Add support to assign labels to the generated PR  [\#26](https://github.com/dipien/releases-hub-gradle-plugin/issues/26)
- Validate dynamic and snapshot versions [\#10](https://github.com/dipien/releases-hub-gradle-plugin/issues/10)
- Add support to assign reviewers to the generated PR [\#2](https://github.com/dipien/releases-hub-gradle-plugin/issues/2)

**Fixed bugs:**

- Upgrades reverted when upgrading multiples artifacts on same PR [\#38](https://github.com/dipien/releases-hub-gradle-plugin/issues/38)

**Closed issues:**

- Integration with plugins.id\("de.fayard.buildSrcVersions"\) [\#33](https://github.com/dipien/releases-hub-gradle-plugin/issues/33)
- Please publish com.releasehub on the Gradle plugin portal [\#31](https://github.com/dipien/releases-hub-gradle-plugin/issues/31)
- Publish on Gradle plugin portal [\#32](https://github.com/dipien/releases-hub-gradle-plugin/issues/32)

## [v1.1.0](https://github.com/dipien/releases-hub-gradle-plugin/tree/v1.1.0) (2019-09-20)
[Full Changelog](https://github.com/dipien/releases-hub-gradle-plugin/compare/v1.0.2...v1.1.0)

**Implemented enhancements:**

- Create one commit per dependency upgraded [\#30](https://github.com/dipien/releases-hub-gradle-plugin/issues/30)
- New validateDependencies task [\#28](https://github.com/dipien/releases-hub-gradle-plugin/issues/28)
- Sort alphabetically the listDependenciesToUpgrade results [\#24](https://github.com/dipien/releases-hub-gradle-plugin/issues/24)
- Include releases notes, documentation & source code on listDependenciesToUpgrade task [\#20](https://github.com/dipien/releases-hub-gradle-plugin/issues/20)
- Create one PR per groupId instead of one per dependency [\#14](https://github.com/dipien/releases-hub-gradle-plugin/issues/14)
- Log not resolved and excluded dependencies on listDependenciesToUpgrade task [\#12](https://github.com/dipien/releases-hub-gradle-plugin/issues/12)

**Fixed bugs:**

- Fix crash for dependencies without stable releases [\#29](https://github.com/dipien/releases-hub-gradle-plugin/issues/29)

## [v1.0.2](https://github.com/dipien/releases-hub-gradle-plugin/tree/v1.0.2) (2019-09-20)
[Full Changelog](https://github.com/dipien/releases-hub-gradle-plugin/compare/v1.0.1...v1.0.2)

**Fixed bugs:**

- Resolve upgrades on plugin \(instead of server\) to avoid timeouts [\#19](https://github.com/dipien/releases-hub-gradle-plugin/issues/19)

## [v1.0.1](https://github.com/dipien/releases-hub-gradle-plugin/tree/v1.0.1) (2019-09-16)
[Full Changelog](https://github.com/dipien/releases-hub-gradle-plugin/compare/v1.0.0...v1.0.1)

**Fixed bugs:**

- excludes property is using includes command line property [\#18](https://github.com/dipien/releases-hub-gradle-plugin/issues/18)

## [v1.0.0](https://github.com/dipien/releases-hub-gradle-plugin/tree/v1.0.0) (2019-09-15)
[Full Changelog](https://github.com/dipien/releases-hub-gradle-plugin/compare/v0.2.0...v1.0.0)

**Implemented enhancements:**

- Support dependenciesBasePath property [\#15](https://github.com/dipien/releases-hub-gradle-plugin/issues/15)
- Use https on server requests [\#8](https://github.com/dipien/releases-hub-gradle-plugin/issues/8)

## [v0.2.0](https://github.com/dipien/releases-hub-gradle-plugin/tree/v0.2.0) (2019-09-13)
[Full Changelog](https://github.com/dipien/releases-hub-gradle-plugin/compare/v0.1.0...v0.2.0)

**Fixed bugs:**

- Duplicated PR descriptions [\#9](https://github.com/dipien/releases-hub-gradle-plugin/issues/9)

**Closed issues:**

- Migrated repo to new owner [\#13](https://github.com/dipien/releases-hub-gradle-plugin/issues/13)

## [v0.1.0](https://github.com/dipien/releases-hub-gradle-plugin/tree/v0.1.0) (2019-09-04)
**Implemented enhancements:**

- New upgradeDependencies task [\#7](https://github.com/dipien/releases-hub-gradle-plugin/issues/7)
- New listDependenciesToUpgrade task [\#6](https://github.com/dipien/releases-hub-gradle-plugin/issues/6)
- New listDependencies task [\#5](https://github.com/dipien/releases-hub-gradle-plugin/issues/5)

**Closed issues:**

- Migrate group id to com.releaseshub [\#1](https://github.com/dipien/releases-hub-gradle-plugin/issues/1)



\* *This Change Log was automatically generated by [github_changelog_generator](https://github.com/skywinder/Github-Changelog-Generator)*