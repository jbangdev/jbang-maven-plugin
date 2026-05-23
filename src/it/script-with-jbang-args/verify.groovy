File log = new File(basedir, "build.log" );
assert log.text.contains("--option1=foo")
assert log.text.contains("foo => bar")