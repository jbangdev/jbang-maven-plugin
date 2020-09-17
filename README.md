## JBang Maven Plugin 

The JBang Maven plugin allows JBang scripts to be executed during a Maven build.

The plugin attempts to use any existing JBang installation. If no JBang installation is found, the plugin will install JBang by downloading and caching the latest version binaries (in your local maven repository) for subsequent runs. 

### Arguments

- `script`: The script to be executed by JBang

#### Example 

```xml
      <plugin>
        <groupId>dev.jbang</groupId>
        <artifactId>jbang-maven-plugin</artifactId>
        <version>0.0.1</version>
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
        <version>0.0.1</version>
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
        <version>0.0.1</version>
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