## How to release

This should get automated in github actions but until then:

```
mvn versions:set -DnewVersion=0.0.x
git commit -m 'release 0.0.x' -a
git tag v0.0.x
mvn clean verify
mvn -Drelease deploy
mvn versions:set -DnewVersion=0.0.x+1-SNAPSHOT
git commit -m '0.0.x+1-SNAPSHOT'
```
