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

package org.apache.hivemind.lib.chain;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.apache.hivemind.Registry;
import org.apache.hivemind.impl.RegistryBuilder;
import org.apache.hivemind.service.BodyBuilder;
import org.apache.hivemind.service.ClassFab;
import org.apache.hivemind.service.MethodFab;
import org.apache.hivemind.service.MethodSignature;
import org.apache.hivemind.test.HiveMindTestCase;
import org.easymock.MockControl;

/**
 * Tests for {@link org.apache.hivemind.lib.chain.ChainBuilderImpl} and
 * {@link org.apache.hivemind.lib.chain.ChainFactory}.
 * 
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public class TestChainBuilder extends HiveMindTestCase
{
    public void testDefaultforReturnType()
    {
        ChainBuilderImpl cb = new ChainBuilderImpl();

        assertEquals("null", cb.defaultForReturnType(Object.class));
        assertEquals("false", cb.defaultForReturnType(boolean.class));
        assertEquals("null", cb.defaultForReturnType(Boolean.class));
        assertEquals("null", cb.defaultForReturnType(boolean[].class));
        assertEquals("0", cb.defaultForReturnType(int.class));
        assertEquals("null", cb.defaultForReturnType(Integer.class));
    }

    private MethodFab newMethodFab()
    {
        return (MethodFab) newMock(MethodFab.class);
    }

    /**
     * Tests adding a void method, also tests creation of a toString() method.
     */
    public void testAddVoidMethod()
    {
        MockControl cfc = newControl(ClassFab.class);
        ClassFab cf = (ClassFab) cfc.getMock();

        MethodSignature sig = new MethodSignature(void.class, "run", null, null);

        BodyBuilder builder = new BodyBuilder();
        builder.begin();
        builder.addln("for (int i = 0; i < _commands.length; i++)");
        builder.addln("_commands[i].run($$);");
        builder.end();

        cf.addMethod(Modifier.PUBLIC, sig, builder.toString());
        cfc.setReturnValue(newMethodFab());

        replayControls();

        ChainBuilderImpl cb = new ChainBuilderImpl();

        cb.addMethod(cf, Runnable.class, sig);

        verifyControls();
    }

    public void testAddNonVoidMethod()
    {
        MockControl cfc = newControl(ClassFab.class);
        ClassFab cf = (ClassFab) cfc.getMock();

        MethodSignature sig = new MethodSignature(boolean.class, "execute", new Class[]
        { String.class }, null);

        BodyBuilder builder = new BodyBuilder();
        builder.begin();
        builder.addln("boolean result = false;");
        builder.addln("for (int i = 0; i < _commands.length; i++)");
        builder.begin();
        builder.addln("result = _commands[i].execute($$);");
        builder.addln("if (result != false) break;");
        builder.end();
        builder.addln("return result;");
        builder.end();

        cf.addMethod(Modifier.PUBLIC, sig, builder.toString());
        cfc.setReturnValue(newMethodFab());

        replayControls();

        ChainBuilderImpl cb = new ChainBuilderImpl();

        cb.addMethod(cf, ChainInterface.class, sig);

        verifyControls();
    }

    /**
     * Test it all together inside the descriptor.
     */

    private ChainInterface newCommand(String parameter, boolean returnValue)
    {
        MockControl control = newControl(ChainInterface.class);
        ChainInterface chain = (ChainInterface) control.getMock();

        chain.execute(parameter);
        control.setReturnValue(returnValue);

        return chain;
    }

    public void testIntegration()
    {
        Registry r = RegistryBuilder.constructDefaultRegistry();

        ChainBuilder cb = (ChainBuilder) r.getService(ChainBuilder.class);

        List commands = new ArrayList();

        commands.add(newCommand("fred", false));
        commands.add(newCommand("fred", false));

        ChainInterface chain = (ChainInterface) cb.buildImplementation(
                ChainInterface.class,
                commands,
                "<Chain>");

        ChainInterface chain2 = (ChainInterface) cb.buildImplementation(
                ChainInterface.class,
                commands,
                "<Chain2>");

        replayControls();

        assertEquals(false, chain.execute("fred"));
        assertEquals("<Chain>", chain.toString());

        // This checks that implementations are cached, but that toString is unique
        // for each instance.

        assertEquals("<Chain2>", chain2.toString());
        assertSame(chain.getClass(), chain2.getClass());

        verifyControls();
    }

    /**
     * Confirm that proceses of commands stops with the first one that returns a non-default value
     * (i.e., true).
     */

    public void testIntegrationWithCancel()
    {
        Registry r = RegistryBuilder.constructDefaultRegistry();

        ChainBuilder cb = (ChainBuilder) r.getService(ChainBuilder.class);

        List commands = new ArrayList();

        commands.add(newCommand("barney", true));
        commands.add(newMock(ChainInterface.class));

        ChainInterface chain = (ChainInterface) cb.buildImplementation(
                ChainInterface.class,
                commands,
                "<Chain>");

        replayControls();

        assertEquals(true, chain.execute("barney"));

        verifyControls();
    }

    public void testChainFactoryIntegration() throws Exception
    {
        Registry r = buildFrameworkRegistry("ChainFactoryIntegration.xml");

        ChainInterface chain = (ChainInterface) r.getService(ChainInterface.class);

        assertEquals(true, chain.execute("whatever"));
    }

}