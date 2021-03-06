/*
 * Copyright (c) 2019, 2021, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package jdk.javadoc.internal.doclets.formats.html.markup;

import jdk.javadoc.internal.doclets.toolkit.Content;

import java.io.IOException;
import java.io.Writer;

/**
 * A representation of HTML entities.
 */
public class Entity extends Content {
    public static final Entity LESS_THAN = new Entity("&lt;");
    public static final Entity GREATER_THAN = new Entity("&gt;");
    public static final Entity AMPERSAND = new Entity("&amp;");
    public static final Entity NO_BREAK_SPACE = new Entity("&nbsp;");

    public final String text;

    private Entity(String text) {
        this.text = text;
    }

    @Override
    public boolean write(Writer writer, boolean atNewline) throws IOException {
        writer.write(text);
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public int charCount() {
        return 1;
    }


    /**
     * Escapes the special HTML characters in a given string using the appropriate
     * entities.
     *
     * @param s the string to escape
     * @return the string with all of the HTML characters escaped
     */
    static String escapeHtmlChars(CharSequence s) {
        // Convert to string as CharSequence implementations can be slow - see JDK-8263321
        String str = s.toString();
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            switch (ch) {
                // only start building a new string if we need to
                case '<': case '>': case '&':
                    StringBuilder sb = new StringBuilder(str.substring(0, i));
                    escapeHtmlChars(str, i, sb);
                    return sb.toString();
            }
        }
        return str;
    }

    /**
     * Escapes the special HTML characters in a given string using the appropriate
     * entities, appending the results into a string builder.
     *
     * @param s the string
     * @param sb the string builder
     */
    static void escapeHtmlChars(CharSequence s, StringBuilder sb) {
        escapeHtmlChars(s.toString(), 0, sb);
    }

    private static void escapeHtmlChars(String s, int start, StringBuilder sb) {
        for (int i = start ; i < s.length(); i++) {
            char ch = s.charAt(i);
            switch (ch) {
                case '<': sb.append(Entity.LESS_THAN.text);     break;
                case '>': sb.append(Entity.GREATER_THAN.text);  break;
                case '&': sb.append(Entity.AMPERSAND.text);     break;
                default:  sb.append(ch);                        break;
            }
        }
    }

}
