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

package org.apache.hivemind.methodmatch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.HiveMind;
import org.apache.hivemind.Location;
import org.apache.hivemind.service.MethodSignature;

/**
 * A utility class used for matching a {@link org.apache.hivemind.service.MethodSignature} against
 * a method pattern (this is primarily used by {@link org.apache.hivemind.ServiceInterceptorFactory
 * interceptor factories}). A method pattern consists of a <em>name pattern</em> and an optional
 * <em>parameters pattern</em>.
 * 
 * <p>
 * The name pattern matches against the method name, and can be one of the following:
 * <ul>
 * <li>A single name - which requires an exact match.  Example: <code>perform</code>
 * <li>A name suffix, indicated with a leading '*'.  Example: <code>*form</code>
 * <li>A name prefix, indicated with a trailing '*'.  Example: <code>per*</code>
 * <li>A name substring, indicated with leading and trailing '*'s.  Example: <code>*erfo*</code>.
 * <li>A match any, indicated with a single '*'.  Example: <code>*</code>
 * </ul>
 * 
 * <p>
 * The parameters pattern follows the name pattern and is optional. It is used to check the number
 * of parameters, or their types. When the parameters pattern is omitted, then the number and types
 * of parameters are not considred when matching methods.
 * 
 * <p>
 * The parameters pattern, when present, is contained within open and closed parenthis after the
 * method pattern.  Inside the parenthesis may be a number, indicating the exact number of
 * method parameters to match against. Alternately, a comma-seperated list of Java types is used,
 * which matches against a method that takes the exact set of parameters.  Examples:
 * <ul>
 * <li><code>perform()</code> -- method with no parameters
 * <li><code>perform(2)</code> -- method with two parameters
 * <li><code>perform(java.util.List, int)</code> - method taking a List and an int parameter
 * </ul> 
 *
 * @author Howard Lewis Ship
 */
public class MethodMatcher
{
    private class StoredPattern
    {
        String _methodPattern;
        MethodFilter _filter;
        Object _patternValue;

        StoredPattern(String pattern, Object value)
        {
            _methodPattern = pattern;
            _patternValue = value;
        }

        boolean match(MethodSignature sig)
        {
            if (_filter == null)
            {

                try
                {
                    _filter = parseMethodPattern(_methodPattern);
                }
                catch (RuntimeException ex)
                {
                    Location l = HiveMind.findLocation(new Object[] { _patternValue, ex });

                    if (l == null)
                        throw ex;

                    throw new ApplicationRuntimeException(
                        MethodMatchMessages.exceptionAtLocation(l, ex),
                        ex);
                }
            }

            return _filter.matchMethod(sig);
        }
    }

    private MethodPatternParser _parser = new MethodPatternParser();
    private List _methodInfos;

    private MethodFilter parseMethodPattern(String pattern)
    {
        return _parser.parseMethodPattern(pattern);
    }

    /**
     * Stores a pattern and an associated value. Values can later be
     * accessed via {@link #get(Method)}.
     * 
     * @param methodPattern a pattern that is used to recognize methods
     * @param patternValue a value associated with the pattern
     */
    public synchronized void put(String methodPattern, Object patternValue)
    {
        if (_methodInfos == null)
            _methodInfos = new ArrayList();

        StoredPattern sp = new StoredPattern(methodPattern, patternValue);

        _methodInfos.add(sp);
    }

    /**
     * Returns a pattern value prevoiusly stored via {@link #put(String, Object)}.
     * Iterates over the patterns stored, in the order in which they were stored,
     * until a match is found.
     * 
     * @param sig the MethodSignature to find a matching pattern for
     * @returns the pattern value for the matching pattern, or null if not found.
     */
    public synchronized Object get(MethodSignature sig)
    {
        if (_methodInfos == null)
            return null;

        Iterator i = _methodInfos.iterator();
        while (i.hasNext())
        {
            StoredPattern sp = (StoredPattern) i.next();

            if (sp.match(sig))
                return sp._patternValue;
        }

        // Not found.
        
        return null;
    }
}
