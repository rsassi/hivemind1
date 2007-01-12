// Copyright 2004, 2005 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.apache.hivemind.util;

import java.util.ArrayList;
import java.util.List;

/**
 * A subset of the utilities available in commons-lang StringUtils. It's all
 * about reducing dependencies, baby!
 *
 * @author Howard Lewis Ship
 */
public class StringUtils
{

    /**
     * Splits an input string into a an array of strings, seperating
     * at commas.
     * 
     * @param input the string to split, possibly null or empty
     * @return an array of the strings split at commas
     */
    public static String[] split(String input)
    {
        if (input == null)
            return new String[0];

        List strings = new ArrayList();

        int startx = 0;
        int cursor = 0;
        int length = input.length();

        while (cursor < length)
        {
            if (input.charAt(cursor) == ',')
            {
                String item = input.substring(startx, cursor);
                strings.add(item);
                startx = cursor + 1;
            }

            cursor++;
        }

        if (startx < length)
            strings.add(input.substring(startx));

        return (String[]) strings.toArray(new String[strings.size()]);
    }

    /**
     * Converts a string such that the first character is upper case.
     * 
     * @param input the input string (possibly empty)
     * @return the string with the first character converted from lowercase to upper case (may
     * return the string unchanged if already capitalized)
     */

    public static String capitalize(String input)
    {
        if (input.length() == 0)
            return input;

        char ch = input.charAt(0);

        if (Character.isUpperCase(ch))
            return input;

        return String.valueOf(Character.toUpperCase(ch)) + input.substring(1);
    }

    public static String join(String[] input, char separator)
    {
        if (input == null || input.length == 0)
            return null;

        StringBuffer buffer = new StringBuffer();

        for (int i = 0; i < input.length; i++)
        {
            if (i > 0)
                buffer.append(separator);

            buffer.append(input[i]);
        }

        return buffer.toString();
    }

    /**
     * Replaces all occurrences of <code>pattern</code> in
     * <code>string</code> with <code>replacement</code>
     */
    public static String replace(String string, String pattern, String replacement)
    {
        StringBuffer sbuf = new StringBuffer();
        int index = string.indexOf(pattern);
        int pos = 0;
        int patternLength = pattern.length();
        for(; index >= 0; index = string.indexOf(pattern, pos))
        {
            sbuf.append(string.substring(pos, index));
            sbuf.append(replacement);
            pos = index + patternLength;
        }
        sbuf.append(string.substring(pos));
        
        return sbuf.toString();
    }
    
}
