## How to release

Just push a tag to git repository with release version set:

```
mvn versions:set -DnewVersion=0.0.x
git commit -m 'release 0.0.x' -a
git tag v0.0.x
git push --tags
mvn versions:set -DnewVersion=0.0.x+1-SNAPSHOT
git commit -m '0.0.x+1-SNAPSHOT'
```

When `mvn deploy` returns, the Central validation passed and publishing process is already ongoing (otherwise build will fail).
At the end of the steps, you should push changes to remote git repository.