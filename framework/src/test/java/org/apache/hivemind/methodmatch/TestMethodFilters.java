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

package org.apache.hivemind.methodmatch;

import java.util.ArrayList;
import java.util.List;

import org.apache.hivemind.service.MethodSignature;

/**
 * Tests for various {@link org.apache.hivemind.methodmatch.MethodFilter}s
 * as well as {@link org.apache.hivemind.methodmatch.MethodPatternParser}.
 *
 * @author Howard Lewis Ship
 */
public class TestMethodFilters extends AbstractMethodTestCase
{
    public void testExactNameFilter() throws Exception
    {
        MethodFilter f = new ExactNameFilter("hashCode");

        assertEquals(true, f.matchMethod(getMethodSignature(this, "hashCode")));
        assertEquals(false, f.matchMethod(getMethodSignature(this, "toString")));
    }

    public void testNamePrefixFilter() throws Exception
    {
        MethodFilter f = new NamePrefixFilter("hash");

        assertEquals(true, f.matchMethod(getMethodSignature(this, "hashCode")));
        assertEquals(false, f.matchMethod(getMethodSignature(this, "toString")));
    }

    public void testNameSuffixFilter() throws Exception
    {
        MethodFilter f = new NameSuffixFilter("Code");

        assertEquals(true, f.matchMethod(getMethodSignature(this, "hashCode")));
        assertEquals(false, f.matchMethod(getMethodSignature(this, "toString")));
    }

    public void testInfixNameFilter() throws Exception
    {
        MethodFilter f1 = new InfixNameFilter("od");

        assertEquals(true, f1.matchMethod(getMethodSignature(this, "hashCode")));
        assertEquals(false, f1.matchMethod(getMethodSignature(this, "toString")));

        MethodFilter f2 = new InfixNameFilter("hashCode");
        assertEquals(true, f2.matchMethod(getMethodSignature(this, "hashCode")));
        assertEquals(false, f2.matchMethod(getMethodSignature(this, "toString")));
    }

    public void testParameterCountFilter() throws Exception
    {
        MethodFilter f = new ParameterCountFilter(0);

        assertEquals(true, f.matchMethod(getMethodSignature(this, "hashCode")));
        assertEquals(true, f.matchMethod(getMethodSignature(this, "toString")));
        assertEquals(false, f.matchMethod(getMethodSignature(this, "equals")));

        f = new ParameterCountFilter(1);

        assertEquals(false, f.matchMethod(getMethodSignature(this, "hashCode")));
        assertEquals(false, f.matchMethod(getMethodSignature(this, "toString")));
        assertEquals(true, f.matchMethod(getMethodSignature(this, "equals")));
    }

    public void testParameterFilterIntArg()
    {
        MethodFilter f = new ParameterFilter(0, "int");

        assertEquals(true, f.matchMethod(getMethodSignature(MethodSubject.class, "intArg")));
        assertEquals(false, f.matchMethod(getMethodSignature(MethodSubject.class, "integerArg")));
    }

    public void testParameterFilterIntegerArg()
    {
        MethodFilter f = new ParameterFilter(0, "java.lang.Integer");

        assertEquals(false, f.matchMethod(getMethodSignature(MethodSubject.class, "intArg")));
        assertEquals(true, f.matchMethod(getMethodSignature(MethodSubject.class, "integerArg")));
    }

    public void testParameterFilterArrayArg()
    {
        MethodFilter f = new ParameterFilter(0, "java.lang.Integer[]");

        assertEquals(false, f.matchMethod(getMethodSignature(MethodSubject.class, "intArg")));
        assertEquals(false, f.matchMethod(getMethodSignature(MethodSubject.class, "integerArg")));
        assertEquals(true, f.matchMethod(getMethodSignature(MethodSubject.class, "arrayArg")));
        assertEquals(false, f.matchMethod(getMethodSignature(MethodSubject.class, "scalarArrayArg")));
    }

    public void testParameterFilterScalarArrayArg()
    {
        MethodFilter f = new ParameterFilter(0, "int[]");

        assertEquals(false, f.matchMethod(getMethodSignature(MethodSubject.class, "intArg")));
        assertEquals(false, f.matchMethod(getMethodSignature(MethodSubject.class, "integerArg")));
        assertEquals(false, f.matchMethod(getMethodSignature(MethodSubject.class, "arrayArg")));
        assertEquals(true, f.matchMethod(getMethodSignature(MethodSubject.class, "scalarArrayArg")));
    }

    public void testCompositeFilter()
    {
        List l = new ArrayList();
        l.add(new ExactNameFilter("intArg"));

        CompositeFilter f = new CompositeFilter(l);
        MethodSignature sig = getMethodSignature(MethodSubject.class, "intArg");

        assertEquals(true, f.matchMethod(sig));

        l.add(new ParameterCountFilter(0));

        assertEquals(false, f.matchMethod(sig));
    }

    public void testMatchAllFilter()
    {
        MethodFilter f = new MatchAllFilter();

        assertEquals(true, f.matchMethod(null));
    }

}
