class hello {

    public static void main(String... args) throws Exception {
        System.out.println("HELLO WORLD! " + java.util.Arrays.asList(args));
        System.out.println("foo => " + System.getProperty("foo"));
    }
}
