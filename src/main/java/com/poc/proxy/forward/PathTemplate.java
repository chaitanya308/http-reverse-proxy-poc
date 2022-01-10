package com.poc.proxy.forward;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

public class PathTemplate {

    private static final Pattern placeholderSearchPattern = compile("<([^>]*)");

    private String value;
    private List<String> placeholders = new ArrayList<>();

    public PathTemplate(String value) {
        this.value = value;
        findPlaceholders(value);
    }

    public String fill(Matcher matcher) {
        String filledValue = value;
        for (String placeholder : placeholders) {
            String group = matcher.group(placeholder);
            filledValue = filledValue.replace("<" + placeholder + ">", group);
        }
        return filledValue;
    }

    private void findPlaceholders(String value) {
        Matcher matcher = placeholderSearchPattern.matcher(value);
        while (matcher.find()) {
            placeholders.add(matcher.group(1));
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
