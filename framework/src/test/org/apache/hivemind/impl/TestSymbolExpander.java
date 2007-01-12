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

import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.Location;
import org.apache.hivemind.SymbolSource;
import org.apache.hivemind.test.HiveMindTestCase;
import org.easymock.MockControl;

/**
 * Tests for {@link org.apache.hivemind.impl.SymbolExpander}. 
 *
 * @author Howard Lewis Ship
 * @since 3.1
 */
public class TestSymbolExpander extends HiveMindTestCase
{
    private class SymbolSourceFixture implements SymbolSource
    {
        public String valueForSymbol(String name)
        {
            return name.toUpperCase();
        }
    }

    private void attempt(String expected, String text)
    {
        SymbolExpander e = new SymbolExpander(null, new SymbolSourceFixture());

        String actual = e.expandSymbols(text, null);

        assertEquals(expected, actual);
    }

    public void testSimple()
    {
        attempt("Now is the TIME", "Now is the ${time}");
    }

    public void testNoSymbols()
    {
        attempt("No symbols in here", "No symbols in here");
    }

    public void testFalseStart()
    {
        attempt("The cost of the ITEM is $1,000.", "The cost of the ${item} is $1,000.");
    }

    public void testNestedBraces()
    {
        attempt("Nested {BRACES}", "Nested ${{braces}}");
    }

    public void testEmptySymbol()
    {
        attempt("An empty ${} symbol", "An empty ${} symbol");
    }

    public void testTrailingDollar()
    {
        attempt("SYMBOL Ends with $", "${symbol} Ends with $");
    }

    public void testEndsWithPartialSymbol()
    {
        attempt("SYMBOL Ends with ${partial", "${symbol} Ends with ${partial");
    }

    public void testMissingSymbol()
    {
        ErrorHandler eh = (ErrorHandler) newMock(ErrorHandler.class);
        Location l = fabricateLocation(2828);

        MockControl control = newControl(SymbolSource.class);
        SymbolSource source = (SymbolSource) control.getMock();

        // Training

        source.valueForSymbol("symbol");
        control.setReturnValue(null);

        eh.error(
            LogFactory.getLog(SymbolExpander.class),
            ImplMessages.noSuchSymbol("symbol"),
            l,
            null);

        replayControls();

        SymbolExpander e = new SymbolExpander(eh, source);

        String actual = e.expandSymbols("Unknown ${symbol}", l);

        assertEquals("Unknown ${symbol}", actual);

        verifyControls();
    }

    public void testEscaped()
    {
        attempt("This is a SYMBOL, this is ${not}.", "This is a ${symbol}, this is $${not}.");
    }
    
    public void testEscapedAtStart()
    {
    	attempt("${not-a-symbol}", "$${not-a-symbol}");
    }
}
