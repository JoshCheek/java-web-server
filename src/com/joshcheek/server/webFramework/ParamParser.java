package com.joshcheek.server.webFramework;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: joshuajcheek
 * Date: 9/7/11
 * Time: 3:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class ParamParser {
    private String          preCompiled;
    private String          compiled;
    private List<String>    names;

    public ParamParser(String uriMatcher) {
        this.preCompiled = uriMatcher;
        compile(uriMatcher);
    }

    public boolean doesItMatch(String uri) {
        return uri.matches(compiled);
    }

    public String paramFor(String uri, String name) {
        if(!doesItMatch(uri)) return null;
        Matcher matcher = Pattern.compile(compiled).matcher(uri);
        matcher.matches();
        return matcher.group(indexFor(name));
    }

    private int indexFor(String name) {
        return names.indexOf(name) + 1;
    }

    private void compile(String uriMatcher) {
        names = new ArrayList<String>();
        compiled = "";
        String working = uriMatcher;
        while(working.length() > 0) {
            String token = getToken(working);
            working = removeToken(working);
            if(token.equals("*")) {
                names.add("splat");
                compiled += "(.*?)";
            } else if(".+()$".contains(token)) {
                compiled += "\\" + token;
            } else if(beginsWithColon(token)) {
                names.add(token.replaceFirst(":",""));
                compiled += "([^/?#]+)";
            } else {
                compiled += token;
            }
        }
    }

    private String removeToken(String working) {
        String token = getToken(working);
        return working.substring(token.length());
    }

    private String getToken(String working) {
        if(beginsWithColon(working))
            return colonAndWord(working);
        else
            return firstChar(working);
    }

    private String colonAndWord(String working) {
        String token = ":";
        working = removeFirstChar(working);
        while(firstChar(working).matches("\\w")) {
            token += firstChar(working);
            working = removeFirstChar(working);
        }
        return token;
    }

    private String removeFirstChar(String working) {
        return working.substring(1);
    }

    private String firstChar(String working) {
        if(working.length() == 0) return "";
        return Character.toString(working.charAt(0));
    }

    private boolean beginsWithColon(String working) {
        return working.charAt(0) == ':';
    }

}
