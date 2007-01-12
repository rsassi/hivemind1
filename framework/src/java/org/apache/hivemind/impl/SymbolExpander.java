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

package org.apache.hivemind.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.Location;
import org.apache.hivemind.SymbolSource;

/**
 * A simple parser used to identify symbols in a string and expand them via
 * a {@link org.apache.hivemind.SymbolSource}.
 *
 * @author Howard Lewis Ship
 */
public class SymbolExpander
{
    private ErrorHandler _errorHandler;
    private SymbolSource _source;

    public SymbolExpander(ErrorHandler handler, SymbolSource source)
    {
        _errorHandler = handler;
        _source = source;
    }

    private static final Log LOG = LogFactory.getLog(SymbolExpander.class);

    private static final int STATE_START = 0;
    private static final int STATE_DOLLAR = 1;
    private static final int STATE_COLLECT_SYMBOL_NAME = 2;

    /**
     * <p>Identifies symbols in the text and expands them, using the
     * {@link SymbolSource}.  Returns the modified text.  May return text if text
     * does not contain any symbols.
     * 
     * @param text the text to scan
     * @param location the location to report errors (undefined symbols)
     * 
     * Note: a little cut-n-paste from {@link org.apache.tapestry.script.AbstractTokenRule}.
     */
    public String expandSymbols(String text, Location location)
    {
        StringBuffer result = new StringBuffer(text.length());
        char[] buffer = text.toCharArray();
        int state = STATE_START;
        int blockStart = 0;
        int blockLength = 0;
        int symbolStart = -1;
        int symbolLength = 0;
        int i = 0;
        int braceDepth = 0;
        boolean anySymbols = false;

        while (i < buffer.length)
        {
            char ch = buffer[i];

            switch (state)
            {
                case STATE_START :

                    if (ch == '$')
                    {
                        state = STATE_DOLLAR;
                        i++;
                        continue;
                    }

                    blockLength++;
                    i++;
                    continue;

                case STATE_DOLLAR :

                    if (ch == '{')
                    {
                        state = STATE_COLLECT_SYMBOL_NAME;
                        i++;

                        symbolStart = i;
                        symbolLength = 0;
                        braceDepth = 1;

                        continue;
                    }
                    
                    // Any time two $$ appear, it is collapsed down to a single $,
                    // but the next character is passed through un-interpreted (even if it
                    // is a brace).
                    
                    if (ch == '$')
                    {                    	
                    	// This is effectively a symbol, meaning that the input string
                    	// will not equal the output string.
                    	
                    	anySymbols = true;
                    	
						if (blockLength > 0)
							result.append(buffer, blockStart, blockLength);      
							
						result.append(ch);							              	
                    	
                    	i++;
                    	blockStart = i;
                    	blockLength = 0;
                    	state = STATE_START;
                    	
                    	continue;
                    }

                    // The '$' was just what it was, not the start of a ${} expression
                    // block, so include it as part of the static text block.

                    blockLength++;

                    state = STATE_START;
                    continue;

                case STATE_COLLECT_SYMBOL_NAME :

                    if (ch != '}')
                    {
                        if (ch == '{')
                            braceDepth++;

                        i++;
                        symbolLength++;
                        continue;
                    }

                    braceDepth--;

                    if (braceDepth > 0)
                    {
                        i++;
                        symbolLength++;
                        continue;
                    }

                    // Hit the closing brace of a symbol.

                    // Degenerate case:  the string "${}".

                    if (symbolLength == 0)
                        blockLength += 3;

                    // Append anything up to the start of the sequence (this is static
                    // text between symbol references).

                    if (blockLength > 0)
                        result.append(buffer, blockStart, blockLength);

                    if (symbolLength > 0)
                    {
                        String variableName =
                            text.substring(symbolStart, symbolStart + symbolLength);

                        result.append(expandSymbol(variableName, location));

                        anySymbols = true;
                    }

                    i++;
                    blockStart = i;
                    blockLength = 0;

                    // And drop into state start

                    state = STATE_START;

                    continue;
            }

        }

        // If get this far without seeing any variables, then just pass
        // the input back.

        if (!anySymbols)
            return text;

        // OK, to handle the end.  Couple of degenerate cases where
        // a ${...} was incomplete, so we adust the block length.

        if (state == STATE_DOLLAR)
            blockLength++;

        if (state == STATE_COLLECT_SYMBOL_NAME)
            blockLength += symbolLength + 2;

        if (blockLength > 0)
            result.append(buffer, blockStart, blockLength);

        return result.toString();
    }

    private String expandSymbol(String name, Location location)
    {
        String value = _source.valueForSymbol(name);

        if (value != null)
            return value;

        _errorHandler.error(LOG, ImplMessages.noSuchSymbol(name), location, null);

        return "${" + name + "}";
    }

}
