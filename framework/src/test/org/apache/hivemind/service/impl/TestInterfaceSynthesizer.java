// Copyright 2005 The Apache Software Foundation
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

package org.apache.hivemind.service.impl;

import org.apache.hivemind.service.ClassFactory;
import org.apache.hivemind.service.InterfaceFab;
import org.apache.hivemind.service.MethodSignature;
import org.apache.hivemind.test.HiveMindTestCase;
import org.apache.hivemind.test.TypeMatcher;
import org.easymock.MockControl;

/**
 * @author Howard M. Lewis Ship
 */
public class TestInterfaceSynthesizer extends HiveMindTestCase
{
    private ClassFactory newClassFactory(InterfaceFab fab)
    {
        MockControl control = newControl(ClassFactory.class);
        ClassFactory cf = (ClassFactory) control.getMock();

        cf.newInterface("UNKNOWN");
        control.setMatcher(new TypeMatcher());
        control.setReturnValue(fab);

        return cf;
    }

    public void testSimple()
    {
        MockControl control = newControl(InterfaceFab.class);
        InterfaceFab fab = (InterfaceFab) control.getMock();

        ClassFactory cf = newClassFactory(fab);

        fab.addMethod(new MethodSignature(void.class, "doStuff", null, null));

        fab.createInterface();
        control.setReturnValue(null);

        replayControls();

        InterfaceSynthesizerImpl is = new InterfaceSynthesizerImpl();
        is.setClassFactory(cf);

        is.synthesizeInterface(SimpleBean.class);

        verifyControls();
    }

    public void testExtendingInterface()
    {
        MockControl control = newControl(InterfaceFab.class);
        InterfaceFab fab = (InterfaceFab) control.getMock();

        ClassFactory cf = newClassFactory(fab);

        fab.addInterface(BeanInterface.class);
        fab.addMethod(new MethodSignature(String.class, "beanMethod", null, null));

        fab.createInterface();
        control.setReturnValue(null);

        replayControls();

        InterfaceSynthesizerImpl is = new InterfaceSynthesizerImpl();
        is.setClassFactory(cf);

        is.synthesizeInterface(ExtendingInterfaceBean.class);

        verifyControls();
    }

    public void testExtendingSubInterface()
    {
        // We can't predict the order in which the methods will be invoked (it's based
        // on things like hash values and order of objects inside a Set), so
        // we use a nice control.

        MockControl control = MockControl.createNiceControl(InterfaceFab.class);
        addControl(control);

        InterfaceFab fab = (InterfaceFab) control.getMock();

        ClassFactory cf = newClassFactory(fab);

        fab.addInterface(BeanInterface.class);
        fab.addInterface(BeanSubInterface.class);
        fab.addMethod(new MethodSignature(String.class, "beanMethod", null, null));

        fab.createInterface();
        control.setReturnValue(null);

        replayControls();

        InterfaceSynthesizerImpl is = new InterfaceSynthesizerImpl();
        is.setClassFactory(cf);

        is.synthesizeInterface(ExtendingSubInterfaceBean.class);

        verifyControls();
    }

    public void testIgnoreStaticAndPrivateMethods()
    {
        MockControl control = newControl(InterfaceFab.class);
        InterfaceFab fab = (InterfaceFab) control.getMock();

        ClassFactory cf = newClassFactory(fab);

        fab.addInterface(BeanInterface.class);
        fab.addMethod(new MethodSignature(String.class, "beanMethod", null, null));

        fab.createInterface();
        control.setReturnValue(null);

        replayControls();

        InterfaceSynthesizerImpl is = new InterfaceSynthesizerImpl();
        is.setClassFactory(cf);

        is.synthesizeInterface(IgnoreStaticAndPrivateMethodsBean.class);

        verifyControls();
    }

}