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

package org.apache.hivemind.lib.strategy;

import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;

import org.apache.hivemind.ErrorLog;
import org.apache.hivemind.Location;
import org.apache.hivemind.Registry;
import org.apache.hivemind.ServiceImplementationFactoryParameters;
import org.apache.hivemind.lib.strategy.StrategyContribution;
import org.apache.hivemind.lib.strategy.StrategyMessages;
import org.apache.hivemind.lib.strategy.StrategyParameter;
import org.apache.hivemind.lib.strategy.StrategyFactory;
import org.apache.hivemind.lib.util.StrategyRegistry;
import org.apache.hivemind.service.ClassFab;
import org.apache.hivemind.service.ClassFabUtils;
import org.apache.hivemind.service.ClassFactory;
import org.apache.hivemind.service.MethodFab;
import org.apache.hivemind.service.MethodSignature;
import org.apache.hivemind.test.AggregateArgumentsMatcher;
import org.apache.hivemind.test.ArgumentMatcher;
import org.apache.hivemind.test.ArrayMatcher;
import org.apache.hivemind.test.HiveMindTestCase;
import org.apache.hivemind.test.TypeMatcher;
import org.easymock.MockControl;

/**
 * Test for the {@link org.apache.hivemind.lib.strategy.StrategyFactory} service
 * implementation factory.
 * 
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public class TestStrategyFactory extends HiveMindTestCase
{
    private List buildContributions(Class registerClass, Object adapter, Location location)
    {
        StrategyContribution c = new StrategyContribution();

        c.setRegisterClass(registerClass);
        c.setStrategy(adapter);
        c.setLocation(location);

        return Collections.singletonList(c);
    }

    private StrategyParameter buildParameter(Class registerClass, Object adapter,
            Location contributionLocation, Location parameterLocation)
    {
        StrategyParameter result = new StrategyParameter();

        result.setContributions(buildContributions(registerClass, adapter, contributionLocation));
        result.setLocation(parameterLocation);

        return result;
    }

    private StrategyParameter buildParameter(Class registerClass, Object adapter)
    {
        return buildParameter(registerClass, adapter, null, null);
    }

    public void testBuildRegistry()
    {
        StrategyRegistry ar = (StrategyRegistry) newMock(StrategyRegistry.class);
        ToStringStrategy adapter = (ToStringStrategy) newMock(ToStringStrategy.class);

        MockControl fpc = newControl(ServiceImplementationFactoryParameters.class);
        ServiceImplementationFactoryParameters fp = (ServiceImplementationFactoryParameters) fpc
                .getMock();

        fp.getServiceInterface();
        fpc.setReturnValue(ToStringStrategy.class);

        StrategyParameter p = buildParameter(Number.class, adapter);

        fp.getFirstParameter();
        fpc.setReturnValue(p);

        ar.register(Number.class, adapter);

        replayControls();

        new StrategyFactory().buildRegistry(fp, ar);

        verifyControls();
    }

    public void testBuildRegistryWrongAdapterType()
    {
        Location l = newLocation();

        StrategyRegistry ar = (StrategyRegistry) newMock(StrategyRegistry.class);
        ToStringStrategy adapter = (ToStringStrategy) newMock(ToStringStrategy.class);

        MockControl fpc = newControl(ServiceImplementationFactoryParameters.class);
        ServiceImplementationFactoryParameters fp = (ServiceImplementationFactoryParameters) fpc
                .getMock();

        MockControl logc = newControl(ErrorLog.class);
        ErrorLog log = (ErrorLog) logc.getMock();

        fp.getServiceInterface();
        fpc.setReturnValue(Runnable.class);

        StrategyParameter p = buildParameter(Number.class, adapter, l, null);

        fp.getFirstParameter();
        fpc.setReturnValue(p);

        fp.getErrorLog();
        fpc.setReturnValue(log);

        log.error(
                StrategyMessages.strategyWrongInterface(adapter, Number.class, Runnable.class),
                l,
                new ClassCastException());
        logc.setMatcher(new AggregateArgumentsMatcher(new ArgumentMatcher[]
        { null, null, new TypeMatcher() }));

        replayControls();

        new StrategyFactory().buildRegistry(fp, ar);

        verifyControls();
    }

    public void testBuildImplementationClass()
    {
        MockControl factoryControl = newControl(ClassFactory.class);
        ClassFactory factory = (ClassFactory) factoryControl.getMock();

        MockControl cfc = newControl(ClassFab.class);
        ClassFab cf = (ClassFab) cfc.getMock();

        MethodFab mf = (MethodFab) newMock(MethodFab.class);

        MockControl fpc = newControl(ServiceImplementationFactoryParameters.class);
        ServiceImplementationFactoryParameters fp = (ServiceImplementationFactoryParameters) fpc
                .getMock();

        fp.getServiceInterface();
        fpc.setReturnValue(ToStringStrategy.class);

        factory.newClass("NewClass", Object.class);
        factoryControl.setReturnValue(cf);

        cf.addInterface(ToStringStrategy.class);
        cf.addField("_registry", StrategyRegistry.class);

        cf.addConstructor(new Class[]
        { StrategyRegistry.class }, null, "_registry = $1;");
        cfc.setMatcher(new AggregateArgumentsMatcher(new ArrayMatcher()));

        cf
                .addMethod(
                        Modifier.PRIVATE,
                        new MethodSignature(ToStringStrategy.class, "_getStrategy", new Class[]
                        { Object.class }, null),
                        "return (org.apache.hivemind.lib.strategy.ToStringStrategy) _registry.getStrategy($1.getClass());");
        cfc.setReturnValue(mf);

        cf.addMethod(Modifier.PUBLIC, new MethodSignature(String.class, "toString", new Class[]
        { Object.class }, null), "return ($r) _getStrategy($1).toString($$);");
        cfc.setReturnValue(mf);

        fp.getServiceId();
        fpc.setReturnValue("foo.Bar");

        ClassFabUtils.addToStringMethod(cf, StrategyMessages.toString(
                "foo.Bar",
                ToStringStrategy.class));
        cfc.setReturnValue(mf);

        cf.createClass();
        cfc.setReturnValue(String.class);

        replayControls();

        StrategyFactory f = new StrategyFactory();
        f.setClassFactory(factory);

        f.buildImplementationClass(fp, "NewClass");

        verifyControls();
    }

    public void testBuildImplementationClassImproperMethod()
    {
        Location l = newLocation();

        MockControl factoryControl = newControl(ClassFactory.class);
        ClassFactory factory = (ClassFactory) factoryControl.getMock();

        MockControl cfc = newControl(ClassFab.class);
        ClassFab cf = (ClassFab) cfc.getMock();

        MethodFab mf = (MethodFab) newMock(MethodFab.class);

        MockControl fpc = newControl(ServiceImplementationFactoryParameters.class);
        ServiceImplementationFactoryParameters fp = (ServiceImplementationFactoryParameters) fpc
                .getMock();

        ErrorLog log = (ErrorLog) newMock(ErrorLog.class);

        fp.getServiceInterface();
        fpc.setReturnValue(Runnable.class);

        factory.newClass("NewClass", Object.class);
        factoryControl.setReturnValue(cf);

        cf.addInterface(Runnable.class);
        cf.addField("_registry", StrategyRegistry.class);

        cf.addConstructor(new Class[]
        { StrategyRegistry.class }, null, "_registry = $1;");
        cfc.setMatcher(new AggregateArgumentsMatcher(new ArrayMatcher()));

        cf.addMethod(
                Modifier.PRIVATE,
                new MethodSignature(Runnable.class, "_getStrategy", new Class[]
                { Object.class }, null),
                "return (java.lang.Runnable) _registry.getStrategy($1.getClass());");
        cfc.setReturnValue(mf);

        MethodSignature sig = new MethodSignature(void.class, "run", null, null);

        cf.addMethod(Modifier.PUBLIC, sig, "{  }");
        cfc.setReturnValue(mf);

        fp.getErrorLog();
        fpc.setReturnValue(log);

        fp.getFirstParameter();
        // Slight fudge: we return the location itself when we should return
        // an object with this location.
        fpc.setReturnValue(l);

        log.error(StrategyMessages.improperServiceMethod(sig), l, null);

        fp.getServiceId();
        fpc.setReturnValue("foo.Bar");

        ClassFabUtils.addToStringMethod(cf, StrategyMessages.toString("foo.Bar", Runnable.class));
        cfc.setReturnValue(mf);

        cf.createClass();

        cfc.setReturnValue(String.class);

        replayControls();

        StrategyFactory f = new StrategyFactory();
        f.setClassFactory(factory);

        f.buildImplementationClass(fp, "NewClass");

        verifyControls();
    }

    public void testIntegration() throws Exception
    {
        Registry r = buildFrameworkRegistry("AdapterFactoryIntegration.xml");

        ToStringStrategy ts = (ToStringStrategy) r.getService(ToStringStrategy.class);

        assertEquals("5150", ts.toString(new Integer(5150)));
    }
}