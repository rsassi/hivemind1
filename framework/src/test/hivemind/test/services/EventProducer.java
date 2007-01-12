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

package hivemind.test.services;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Class used by {@link hivemind.test.services.TestEventLinker}.
 * 
 * @author Howard M. Lewis Ship
 */
public class EventProducer
{
    private PropertyChangeSupport _support = new PropertyChangeSupport(this);
    private int _count = 0;

    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        _support.addPropertyChangeListener(listener);

        _count++;
    }

    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
        _support.removePropertyChangeListener(listener);

        _count--;
    }

    public void fire(PropertyChangeEvent event)
    {
        _support.firePropertyChange(event);
    }

    public int getListenerCount()
    {
        return _count;
    }

    public String toString()
    {
        return "EventProducer";
    }
}
