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
package org.apache.hivemind.impl;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.internal.ConfigurationPoint;
import org.apache.hivemind.internal.RegistryInfrastructure;
import org.apache.hivemind.internal.ServicePoint;
import org.apache.hivemind.internal.Visibility;
import org.apache.hivemind.test.HiveMindTestCase;
import org.easymock.MockControl;

import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * Additional tests for {@link RegistryInfrastructureImpl}. Much of the
 * existing testing is, alas, integration (not unit) testing. Gradually hope to move more of that
 * into this class.
 *
 * @author Howard Lewis Ship
 * @since 1.1
 */
public class TestRegistryInfrastructure extends HiveMindTestCase
{
    public void testGetMissingExtensionPoint()
    {
        RegistryInfrastructure r = new RegistryInfrastructureImpl( null, null );
        try
        {
            r.getServicePoint( "foo", null );
            unreachable();
        }
        catch( ApplicationRuntimeException ex )
        {
            assertEquals( ImplMessages.noSuchServicePoint( "foo" ), ex.getMessage() );
        }
    }

    public void testGetUnqualifiedServicePoint()
    {
        RegistryInfrastructureImpl r = new RegistryInfrastructureImpl( null, null );
        final ModuleImpl module1 = new ModuleImpl();
        module1.setModuleId( "module1" );
        r.addServicePoint( createServicePoint( module1, "module1.foo", ResultSet.class, Visibility.PUBLIC ) );
        try
        {
            r.getServicePoint( "foo", null );
            unreachable();
        }
        catch( ApplicationRuntimeException ex )
        {
            assertEquals( ImplMessages.unqualifiedServicePoint( "foo", "\"module1.foo\"" ), ex.getMessage() );
        }
        final ModuleImpl module2 = new ModuleImpl();
        module2.setModuleId( "module2" );
        r.addServicePoint( createServicePoint( module2, "module2.foo", ResultSet.class, Visibility.PUBLIC ) );
        try
        {
            r.getServicePoint( "foo", null );
            unreachable();
        }
        catch( ApplicationRuntimeException ex )
        {
            assertEquals( ImplMessages.unqualifiedServicePoint( "foo", "\"module1.foo\", \"module2.foo\"" ), ex.getMessage() );
        }
    }

    public void testGetServiceIdsNoServices()
    {
        RegistryInfrastructureImpl r = new RegistryInfrastructureImpl( null, null );
        final List serviceIds = r.getServiceIds( ResultSet.class );
        assertNotNull( serviceIds );
        assertTrue( serviceIds.isEmpty() );
    }

    public void testGetServiceIdsInterfaceNotFound()
    {
        RegistryInfrastructureImpl r = new RegistryInfrastructureImpl( null, null );
        final ModuleImpl module1 = new ModuleImpl();
        module1.setClassResolver( new DefaultClassResolver() );
        module1.setModuleId( "module1" );
        r.addServicePoint( createServicePoint( module1, "module1.foo", "com.evilsite.some.bogus.package.SomeBogusInterface.", Visibility.PUBLIC ) );
        assertEquals( new HashSet(), new HashSet( r.getServiceIds( ResultSet.class ) ) );
    }

    public void testGetServiceIds()
    {
        RegistryInfrastructureImpl r = new RegistryInfrastructureImpl( null, null );
        assertTrue( r.getServiceIds( ResultSet.class ).isEmpty() );
        final ModuleImpl module1 = new ModuleImpl();
        module1.setClassResolver( new DefaultClassResolver() );
        module1.setModuleId( "module1" );
        r.addServicePoint( createServicePoint( module1, "module1.foo", ResultSet.class, Visibility.PUBLIC ) );
        r.addServicePoint( createServicePoint( module1, "module1.bar", ResultSet.class, Visibility.PUBLIC ) );
        r.addServicePoint( createServicePoint( module1, "module1.baz", ResultSet.class, Visibility.PRIVATE ) );
        r.addServicePoint( createServicePoint( module1, "module1.string", String.class, Visibility.PUBLIC ) );
        assertEquals( new HashSet( Arrays.asList( new String[]{"module1.foo", "module1.bar"} ) ), new HashSet( r.getServiceIds( ResultSet.class ) ) );
        assertEquals( new HashSet( Arrays.asList( new String[]{"module1.string"} ) ), new HashSet( r.getServiceIds( String.class ) ) );
        List serviceIds = r.getServiceIds( null );
        assertNotNull( serviceIds );
        assertEquals( 0, serviceIds.size() );

    }

    private ServicePointImpl createServicePoint( final ModuleImpl module, String id, Class serviceInterface, Visibility visibility )
    {
        final ServicePointImpl servicePoint2 = new ServicePointImpl();
        servicePoint2.setModule( module );
        servicePoint2.setExtensionPointId( id );
        servicePoint2.setVisibility( visibility );
        servicePoint2.setServiceInterfaceName( serviceInterface.getName() );
        return servicePoint2;
    }

    private ServicePointImpl createServicePoint( final ModuleImpl module, String id, String serviceInterfaceName, Visibility visibility )
    {
        final ServicePointImpl servicePoint2 = new ServicePointImpl();
        servicePoint2.setModule( module );
        servicePoint2.setExtensionPointId( id );
        servicePoint2.setVisibility( visibility );
        servicePoint2.setServiceInterfaceName( serviceInterfaceName );
        return servicePoint2;
    }

    /**
     * Ensure that the Registry "locks down" after being started up.
     *
     * @since 1.1
     */
    public void testDoubleStartup()
    {
        MockControl spc = newControl( ServicePoint.class );
        ServicePoint sp = ( ServicePoint ) spc.getMock();
        Runnable service = ( Runnable ) newMock( Runnable.class );

        // Training
        sp.getExtensionPointId();
        spc.setReturnValue( "hivemind.Startup" );
        sp.getServiceInterfaceClassName();
        spc.setReturnValue( Runnable.class.getName() );
        sp.visibleToModule( null );
        spc.setReturnValue( true );
        sp.getService( Runnable.class );
        spc.setReturnValue( service );
        service.run();
        replayControls();
        RegistryInfrastructureImpl r = new RegistryInfrastructureImpl( null, null );
        r.addServicePoint( sp );
        r.startup();
        verifyControls();
        try
        {
            r.startup();
            unreachable();
        }
        catch( IllegalStateException ex )
        {
            assertEquals( ImplMessages.registryAlreadyStarted(), ex.getMessage() );
        }
    }

    public void testUnknownServiceModelFactory()
    {
        MockControl cpc = newControl( ConfigurationPoint.class );
        ConfigurationPoint cp = ( ConfigurationPoint ) cpc.getMock();

        // Training
        cp.getExtensionPointId();
        cpc.setReturnValue( "hivemind.ServiceModels" );
        cp.visibleToModule( null );
        cpc.setReturnValue( true );
        cp.getElements();
        cpc.setReturnValue( Collections.EMPTY_LIST );
        replayControls();
        RegistryInfrastructureImpl r = new RegistryInfrastructureImpl( null, null );
        r.addConfigurationPoint( cp );
        try
        {
            r.getServiceModelFactory( "unknown" );
            unreachable();
        }
        catch( ApplicationRuntimeException ex )
        {
            assertEquals( ImplMessages.unknownServiceModel( "unknown" ), ex.getMessage() );
        }
        verifyControls();
    }

}