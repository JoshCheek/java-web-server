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
        assertMatch("/index", "/index");
    }

    private void assertMatch(String uriPattern, String uri) {
        ParamParser parser = new ParamParser(uriPattern);
        assertTrue("Expected \"" + uriPattern + "\" to match \"" + uri + "\"",
                parser.doesItMatch(uri));
    }
}
