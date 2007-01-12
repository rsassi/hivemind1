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

package org.apache.hivemind.tools.forrest;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import org.apache.cocoon.transformation.AbstractSAXTransformer;
import com.uwyn.jhighlight.highlighter.JavaHighlighter;
import com.uwyn.jhighlight.tools.StringUtils;

/**
 * Renders java code as xml markup that can be transformed to html.
 * Parses the code using the JHighlight library and outputs
 * the code embedded in special xml-elements that mark the type
 * of the tokens. For example a java keyword is embedded
 * in a &lt;java-keyword&gt; element.
 * 
 * @author Achim Huegen
 */
public class Java2XmlRenderer
{

    /**
     * Processes java code. Outputs the xml code by creating sax events in the sax transformer.
     * 
     * @param in
     * @param sax
     * @param spacesPerTab
     * @throws Exception
     */
    public void process(InputStream in, AbstractSAXTransformer sax, int spacesPerTab) throws Exception
    {
        JavaHighlighter highlighter = new JavaHighlighter();

        JavaHighlighter.ASSERT_IS_KEYWORD = true;
        Reader isr = new InputStreamReader(in);

        BufferedReader r = new BufferedReader(isr);
        String line;
        String token;
        int length;
        int style;
        String css_class;
        String previous_class = null;

        int previous_style = 0;

        // Read source line by line and apply styles
        while ((line = r.readLine()) != null)
        {
            line += "\n";
            line = StringUtils.convertTabsToSpaces(line, spacesPerTab);

            Reader lineReader = new StringReader(line);

            highlighter.setReader(lineReader);

            int index = 0;
            while (index < line.length())
            {
                style = highlighter.getNextToken();
                length = highlighter.getTokenLength();
                token = line.substring(index, index + length);
                if (style != previous_style)
                {
                    css_class = getCssClass(style);
                    if (css_class != null)
                    {
                        if (previous_class != null)
                        {
                            sax.sendEndElementEvent(previous_class);
                        }
                        sax.sendStartElementEvent(css_class);
                        previous_class = css_class;
                        previous_style = style;
                    }
                }

                sax.sendTextEvent(token);
                index += length;

            }

        }
        if (previous_class != null)
        {
            sax.sendEndElementEvent(previous_class);
        }
    }

    /**
     * Map the jhighlighter styles to css styles
     * @param style
     * @return css style
     */
    protected String getCssClass(int style)
    {
        switch (style)
        {
        case JavaHighlighter.PLAIN_STYLE:
            return "java-plain";
        case JavaHighlighter.KEYWORD_STYLE:
            return "java-keyword";
        case JavaHighlighter.TYPE_STYLE:
            return "java-type";
        case JavaHighlighter.OPERATOR_STYLE:
            return "java-operator";
        // ignore separators, special formatting not needed
        // case JavaHighlighter.SEPARATOR_STYLE:
        // return "java-separator";
        case JavaHighlighter.LITERAL_STYLE:
            return "java-literal";
        case JavaHighlighter.JAVA_COMMENT_STYLE:
            return "java-comment";
        case JavaHighlighter.JAVADOC_COMMENT_STYLE:
            return "java-javadoc_comment";
        case JavaHighlighter.JAVADOC_TAG_STYLE:
            return "java-javadoc_tag";
        }

        return null;

    }

}