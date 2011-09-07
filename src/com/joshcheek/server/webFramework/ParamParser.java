package com.joshcheek.server.webFramework;

/**
 * Created by IntelliJ IDEA.
 * User: joshuajcheek
 * Date: 9/7/11
 * Time: 3:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class ParamParser {
    private String uriMatcher;

    public ParamParser(String uriMatcher) {
        this.uriMatcher = uriMatcher;
    }

    public boolean doesItMatch(String uri) {
        return uriMatcher.equals(uri);
    }

    public String paramFor(String uri, String name) {
        return "index";
    }
}
