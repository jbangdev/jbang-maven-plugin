import java.nio.charset.Charset

File log = new File(basedir, "build.log" );
assert log.text.contains(Locale.getDefault().getLanguage() + "." + Charset.defaultCharset().displayName())
