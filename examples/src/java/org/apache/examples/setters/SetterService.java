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

package org.apache.examples.setters;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.apache.examples.Adder;
import org.apache.hivemind.Resource;

/**
 * Demonstrates the use of setter based dependency injection using the BuilderFactory.
 * 
 * @author Achim Huegen
 */
public class SetterService implements Runnable
{
    private String _stringValue;

    private int _intValue;

    private double _doubleValue;

    private Adder _adderService;

    private List _configuration;

    private Collection _container;

    private Resource _textResource;

    public void setAdderService(Adder adderService)
    {
        _adderService = adderService;
    }

    public void setConfiguration(List configuration)
    {
        _configuration = configuration;
    }

    public void setContainer(Collection container)
    {
        _container = container;
    }

    public void setDoubleValue(double doubleValue)
    {
        _doubleValue = doubleValue;
    }

    public void setIntValue(int intValue)
    {
        _intValue = intValue;
    }

    public void setStringValue(String stringValue)
    {
        _stringValue = stringValue;
    }

    public void setTextResource(Resource textResource)
    {
        _textResource = textResource;
    }

    public void run()
    {
        System.out.println("StringValue: " + _stringValue);
        System.out.println("IntValue: " + _intValue);
        System.out.println("DoubleValue: " + _doubleValue);
        System.out.println("AdderService result: " + _adderService.add(10, 20));
        System.out.println("Configuration size: " + _configuration.size());
        System.out.println("Container type: " + _container.getClass().getName());
        Properties properties = loadResource();
        System.out.println("Text resource content: " + properties.toString());
    }

    /**
     * Loads the properties file reference by property textResource
     */
    private Properties loadResource()
    {
        Properties properties = new Properties();
        try
        {
            properties.load(_textResource.getResourceURL().openStream());
        }
        catch (IOException ignore)
        {
        }
        return properties;
    }
}
