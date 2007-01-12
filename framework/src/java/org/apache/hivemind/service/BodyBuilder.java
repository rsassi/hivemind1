//  Copyright 2004 The Apache Software Foundation
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

package org.apache.hivemind.service;

import java.text.MessageFormat;

/**
 * Utility class for assembling the <em>body</em> used with
 * Javassist as a method or catch block.
 *
 * @author Howard Lewis Ship
 */

public class BodyBuilder
{
    /**
     * Feels right for the size of a typical body.
     */
    private static final int DEFAULT_LENGTH = 200;

    private static final char QUOTE = '"';

    private StringBuffer _buffer = new StringBuffer(DEFAULT_LENGTH);

    private static final String INDENT = "  ";

    private int _nestingDepth = 0;
    private boolean _atNewLine = true;

    /**
     * Clears the builder, returning it to its initial, empty state.
     */
    public void clear()
    {
        _nestingDepth = 0;
        _atNewLine = true;
        _buffer.setLength(0);
    }

    /**
     * Adds text to the current line, without terminating the line.
     */
    public void add(String text)
    {
        indent();

        _buffer.append(text);
    }

    /**
     * Adds text to the current line, without terminating the line.
     * 
     * @param pattern a string pattern, used with {@link java.text.MessageFormat#format(java.lang.String, java.lang.Object[])}
     * @param arguments arguments used witht the format string
     */

    public void add(String pattern, Object[] arguments)
    {
        add(MessageFormat.format(pattern, arguments));
    }

    /**
     * Convience for {@link #add(String, Object[])
     */

    public void add(String pattern, Object arg0)
    {
        add(pattern, new Object[] { arg0 });
    }

    /**
     * Convience for {@link #add(String, Object[])
     */

    public void add(String pattern, Object arg0, Object arg1)
    {
        add(pattern, new Object[] { arg0, arg1 });
    }

    /**
     * Convience for {@link #add(String, Object[])
     */

    public void add(String pattern, Object arg0, Object arg1, Object arg2)
    {
        add(pattern, new Object[] { arg0, arg1, arg2 });
    }

    /**
     * Adds text to the current line then terminates the line.
     * 
     * @param pattern a string pattern, used with {@link java.text.MessageFormat#format(java.lang.String, java.lang.Object[])}
     * @param arguments arguments used witht the format string
     */

    public void addln(String pattern, Object[] arguments)
    {
        addln(MessageFormat.format(pattern, arguments));
    }

    /**
     * Convience for {@link #addln(String, Object[])
     */

    public void addln(String pattern, Object arg0)
    {
        addln(pattern, new Object[] { arg0 });
    }

    /**
     * Convience for {@link #addln(String, Object[])
     */

    public void addln(String pattern, Object arg0, Object arg1)
    {
        addln(pattern, new Object[] { arg0, arg1 });
    }

    /**
     * Convience for {@link #addln(String, Object[])
     */

    public void addln(String pattern, Object arg0, Object arg1, Object arg2)
    {
        addln(pattern, new Object[] { arg0, arg1, arg2 });
    }

    /**
     * Adds the text to the current line, surrounded by double quotes.
     * <em>Does not escape quotes in the text</em>.
     */

    public void addQuoted(String text)
    {
        indent();
        _buffer.append(QUOTE);
        _buffer.append(text);
        _buffer.append(QUOTE);
    }

    /**
     * Adds the text to the current line, and terminates the line.
     */

    public void addln(String text)
    {
        add(text);

        newline();
    }

    private void newline()
    {
        _buffer.append("\n");
        _atNewLine = true;
    }

    /**
     * Begins a new block.  Emits a "{", properly indented, on a new line.
     */
    public void begin()
    {
        if (!_atNewLine)
            newline();

        indent();
        _buffer.append("{");
        newline();

        _nestingDepth++;
    }

    /**
     * Ends the current block. Emits a "}", propertly indented, on a new line.
     */
    public void end()
    {
        if (!_atNewLine)
            newline();

        _nestingDepth--;

        indent();
        _buffer.append("}");
        
        newline();
    }

    private void indent()
    {
        if (_atNewLine)
        {
            for (int i = 0; i < _nestingDepth; i++)
                _buffer.append(INDENT);

            _atNewLine = false;
        }
    }

    /**
     * Returns the current contents of the buffer.
     */
    public String toString()
    {
        return _buffer.toString();
    }
}
