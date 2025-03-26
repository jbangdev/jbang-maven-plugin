## JBang Maven Plugin 

![GitHub Workflow Status](https://img.shields.io/github/actions/workflow/status/jbangdev/jbang-maven-plugin/ci.yml?style=for-the-badge)
[![Maven Central](https://img.shields.io/maven-central/v/dev.jbang/jbang-maven-plugin.svg?label=Maven-Central&style=for-the-badge)](https://search.maven.org/search?q=g:%22dev.jbang%22%20AND%20a:%22jbang-maven-plugin%22)

The JBang Maven plugin allows JBang scripts to be executed during a Maven build, or through `mvn` command-line (without pom file).

The plugin attempts to use an existing JBang installation. If no JBang installation is found, the plugin will install JBang by downloading and caching the latest version binaries (by default in your Maven project directory) for subsequent runs. 

### Arguments

- `script`: The script to be executed by JBang

#### Example 

From inside a Maven buid:
```xml
      <plugin>
        <groupId>dev.jbang</groupId>
        <artifactId>jbang-maven-plugin</artifactId>
        <version>0.0.7</version>
        <executions>
          <execution>
            <id>run</id>
            <phase>process-resources</phase>
            <goals>
              <goal>run</goal>
            </goals>
            <configuration>
                <script>hello.java</script>
            </configuration>
          </execution>
        </executions>
      </plugin>
```

Or with command-line `$ mvn dev.jbang:jbang-maven-plugin:0.0.8:run -Djbang.script="helloworld/helloworld.java"`.
    
- `args` : The arguments to be used in the JBang script (if any)

#### Example 

```xml
      <plugin>
        <groupId>dev.jbang</groupId>
        <artifactId>jbang-maven-plugin</artifactId>
        <version>0.0.7</version>
        <executions>
          <execution>
            <id>run</id>
            <phase>process-resources</phase>
            <goals>
              <goal>run</goal>
            </goals>
            <configuration>
                <script>hello.java</script>
                <args>
                  <arg>--option1=foo</arg>
                </args>
            </configuration>
          </execution>
        </executions>
      </plugin>
```

- `trusts`: If the script resides in a remote location, this parameter specifies what URLs should be trusted. See [URLs from Trusted Sources](https://github.com/jbangdev/jbang#urls-from-trusted-sources) for more information


#### Example 

```xml
      <plugin>
        <groupId>dev.jbang</groupId>
        <artifactId>jbang-maven-plugin</artifactId>
        <version>0.0.7</version>
        <executions>
          <execution>
            <id>run</id>
            <phase>process-resources</phase>
            <goals>
              <goal>run</goal>
            </goals>
            <configuration>
                <script>https://github.com/jbangdev/jbang-examples/blob/master/examples/lang.java</script>
                <trusts>
                  <trust>https://github.com</trust>
                </trusts>
            </configuration>
          </execution>
        </executions>
      </plugin>
```

- `jbangargs`: Arguments for `jbang` (not the script)

#### Example

```xml
      <plugin>
        <groupId>dev.jbang</groupId>
        <artifactId>jbang-maven-plugin</artifactId>
        <version>0.0.7</version>
        <executions>
          <execution>
            <id>run</id>
            <phase>process-resources</phase>
            <goals>
              <goal>run</goal>
            </goals>
            <configuration>
                <jbangargs>
                     <jbangarg>--quiet</jbangarg>
                </jbangargs>
                <script>hello.java</script>
            </configuration>
          </execution>
        </executions>
      </plugin>
```
                            
- `jbangVersion`: If your envronment lacks the JBang binaries in the PATH, you can specify the JBang version to be installed. If not specified, the plugin will resolve to the latest JBang release available


#### Example 

```xml
      <plugin>
        <groupId>dev.jbang</groupId>
        <artifactId>jbang-maven-plugin</artifactId>
        <version>0.0.7</version>
        <executions>
          <execution>
            <id>run</id>
            <phase>process-resources</phase>
            <goals>
              <goal>run</goal>
            </goals>
            <configuration>
                <script>hello.java</script>
                <jbangVersion>0.47.1</jbangVersion>
            </configuration>
          </execution>
        </executions>
      </plugin>
```

- `jbangInstallDir`: Alternative location of JBang installation. The default value is `${project.basedir}`


#### Example

```xml
      <plugin>
        <groupId>dev.jbang</groupId>
        <artifactId>jbang-maven-plugin</artifactId>
        <version>0.0.7</version>
        <executions>
          <execution>
            <id>run</id>
            <phase>process-resources</phase>
            <goals>
              <goal>run</goal>
            </goals>
            <configuration>
                <script>hello.java</script>
                <jbangInstallDir>${project.build.directory}</jbangInstallDir>
            </configuration>
          </execution>
        </executions>
      </plugin>
```

### Reporting bugs/issues/features

Please use https://github.com/jbangdev/jbang for reporting bugs/issues/features. 

### Releasing

To release a new version of the plugin, run the following command:

```shell
mvn versions:set -DnewVersion=0.0.Z
git commit -a -m "release 0.0.Z"
git tag -a 0.0.Z -m "release 0.0.Z"
git push
```

When completed correctly, the new version will be available in Maven Central within some time (usually less than 30 minutes).

To prepare for the next development iteration, run the following command:

```shell```
mvn versions:set -DnewVersion=0.0.Z+1-SNAPSHOT
git commit -a -m "prepare for next development iteration"
git push
```
