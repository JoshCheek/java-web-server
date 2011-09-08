package com.joshcheek.server;

import com.joshcheek.server.webFramework.ParamParser;

/**
 * Created by IntelliJ IDEA.
 * User: joshuajcheek
 * Date: 9/7/11
 * Time: 3:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class ParamParserTest extends junit.framework.TestCase  {
    public void testItRecognizesExactMatches() {
        assertParserMatches("/index", "/index");
    }

    public void testItSupportsParamsInURL() {
        assertParserMatches("/hello/:person", "/hello/Frank", "person", "Frank");
    }

    public void testItSupportsSingleSplatParams() {
        assertParserMatches("/*", "/foo", "splat", "foo");
        assertParserMatches("/*", "/foo/bar/baz", "splat", "foo/bar/baz");
    }

    public void testItSupportsMixingNamedAndSplatParams() {
        assertParserMatches("/:foo/*", "/flap/bar/baz", "foo", "flap", "splat", "bar/baz" );
    }

    public void testItMatchesDotAsPartOfTheName() {
        assertParserMatches("/:foo/:bar", "/user@example.com/name", "foo", "user@example.com", "bar", "name");
    }

    public void testItMatchesALiteralDotOutsideOfNamedParams() {
        assertParserMatches("/:file.:ext", "/pony.jpg", "file", "pony", "ext", "jpg");
    }

    public void testItLiterallyMatchesDotInPath() {
        assertParserMatches("/test.bar", "/test.bar");
        assertParserDoesntMatch("/test.bar", "/test0bar");
    }

    public void testItLiterallyMatchesDollarSignInPath() {
        assertParserMatches("/test$/", "/test$/");
    }

    public void testItLiterallyMatchesPlusSignInPaths() {
        assertParserMatches("/te+st/", "/te+st/");
        assertParserDoesntMatch("/te+st/", "/teeeeeeest/");
    }

    public void testItLiterallyMatchesParensInPath() {
        assertParserMatches("/test(bar)/", "/test(bar)/");
    }

    public void testItMatchesPathsThatIncludeSpaces() {
        assertParserMatches("/path with spaces", "/path with spaces");
    }




    private void assertParserMatches(String uriPattern, String uri, String ... matches) {
        ParamParser parser = new ParamParser(uriPattern);
        assertMatch(uriPattern, uri);
        for(int i=0; i<matches.length; i+=2) {
            String name     = matches[i];
            String expected = matches[i+1];
            String actual   = parser.paramFor(uri, name);
            assertEquals(expected, actual);
        }
    }

    private void assertMatch(String uriPattern, String uri) {
        ParamParser parser = new ParamParser(uriPattern);
        assertTrue("Expected \"" + uriPattern + "\" to match \"" + uri + "\"",
                parser.doesItMatch(uri));
    }

    private void assertParserDoesntMatch(String uriPattern, String uri) {
        ParamParser parser = new ParamParser(uriPattern);
        assertFalse("Expected \"" + uriPattern + "\" NOT to match \"" + uri + "\"",
                parser.doesItMatch(uri));
    }



}
