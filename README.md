## JBang Maven Plugin 

![GitHub Workflow Status](https://img.shields.io/github/workflow/status/jbangdev/jbang-maven-plugin/JBang%20CI?style=for-the-badge)
![Maven Central](https://img.shields.io/maven-central/v/dev.jbang/jbang-maven-plugin?style=for-the-badge)

The JBang Maven plugin allows JBang scripts to be executed during a Maven build.

The plugin attempts to use an existing JBang installation. If no JBang installation is found, the plugin will install JBang by downloading and caching the latest version binaries (in your local maven repository) for subsequent runs. 

### Arguments

- `script`: The script to be executed by JBang

#### Example 

```xml
      <plugin>
        <groupId>dev.jbang</groupId>
        <artifactId>jbang-maven-plugin</artifactId>
        <version>0.0.4</version>
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
    
- `args` : The arguments to be used in the JBang script (if any)

#### Example 

```xml
      <plugin>
        <groupId>dev.jbang</groupId>
        <artifactId>jbang-maven-plugin</artifactId>
        <version>0.0.4</version>
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
        <version>0.0.4</version>
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

- `jbangVersion`: If your envronment lacks the JBang binaries in the PATH, you can specify the JBang version to be installed. The default value will be the plugin's version


#### Example 

```xml
      <plugin>
        <groupId>dev.jbang</groupId>
        <artifactId>jbang-maven-plugin</artifactId>
        <version>0.0.4</version>
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

### Reporting bugs/issues/features

Please use https://github.com/jbangdev/jbang for reporting bugs/issues/features. 
