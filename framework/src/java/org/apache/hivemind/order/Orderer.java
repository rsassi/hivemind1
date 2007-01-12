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

package org.apache.hivemind.order;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.HiveMind;
import org.apache.hivemind.util.StringUtils;

/**
 * Used to order objects into an "execution" order. Each object must have a name. It may specify a
 * list of pre-requisites and a list of post-requisites.
 * 
 * @author Howard Lewis Ship
 */
public class Orderer
{
    private final Log _log;
    private final ErrorHandler _errorHandler;
    private final String _objectType;
    private List _orderingsList = null;
    private Map _orderingsMap = null;
    private Map _nodeMap = null;
    private Node _leader;
    private Node _trailer;

    /**
     * Creates an instance using <code>org.apache.hivemind.order.Orderer</code> as the Log.
     */
    public Orderer(ErrorHandler errorHandler, String objectType)
    {
        this(LogFactory.getLog(Orderer.class), errorHandler, objectType);
    }

    /**
     * Creates a new instance, but directs all debug and error logging output to the provided log.
     * 
     * @param log
     *            Used for logging any errors
     * @param objectType
     *            user presentable name for the type of object to be ordered; used in some error
     *            messages
     */
    public Orderer(Log log, ErrorHandler errorHandler, String objectType)
    {
        _log = log;
        _errorHandler = errorHandler;
        _objectType = objectType;

        HiveMind.checkNullParameter("log", log);
        HiveMind.checkNullParameter("errorHandler", errorHandler);
        HiveMind.checkNullParameter("objectType", objectType);
    }

    /**
     * Adds a new object. All invocations of {@link #add(Object, String, String, String)}should
     * occur before invoking {@link #getOrderedObjects()}.
     * 
     * @param object
     *            an object to be sorted into order based on prereqs and postreqs
     * @param name
     *            a unique name for the
     * @param prereqs
     *            a comma-separated list of the names of objects that should precede this object in
     *            the list (or null)
     * @param postreqs
     *            a comma-separated list of the names of objects that should follow this object in
     *            the list (or null)
     */
    public void add(Object object, String name, String prereqs, String postreqs)
    {
        if (_orderingsMap == null)
        {
            _orderingsMap = new HashMap();
            _orderingsList = new ArrayList();
        }

        ObjectOrdering o = getOrderable(name);

        if (o != null)
        {
            _errorHandler.error(_log, OrdererMessages.duplicateName(_objectType, name, object, o
                    .getObject()), HiveMind.getLocation(object), null);

            return;
        }

        o = new ObjectOrdering(object, name, prereqs, postreqs);

        _orderingsMap.put(name, o);
        _orderingsList.add(o);
    }

    private ObjectOrdering getOrderable(String name)
    {
        return (ObjectOrdering) _orderingsMap.get(name);
    }

    /**
     * Uses the information provided by {@link #add(Object, String, String, String)}to order the
     * objects into an appropriate order based on the pre- and post-reqts provided. Errors such as
     * cyclic dependencies or unrecognized names are logged and ignored.
     */
    public List getOrderedObjects()
    {
        if (_orderingsMap == null)
            return Collections.EMPTY_LIST;

        try
        {
            _nodeMap = new HashMap();

            initializeGraph();

            return _trailer.getOrder();
        }
        finally
        {
            _nodeMap = null;
            _leader = null;
            _trailer = null;
        }
    }

    private void initializeGraph()
    {
        addNodes();

        if (_leader == null)
            _leader = new Node(null, "*-leader-*");

        if (_trailer == null)
            _trailer = new Node(null, "*-trailer-*");

        addDependencies();
    }

    private Node getNode(String name)
    {
        return (Node) _nodeMap.get(getOrderable(name));
    }

    private void addNodes()
    {
        Iterator i = _orderingsList.iterator();

        while (i.hasNext())
        {
            ObjectOrdering o = (ObjectOrdering) i.next();

            Node node = new Node(o.getObject(), o.getName());

            _nodeMap.put(o, node);

            if ("*".equals(o.getPostreqs()))
            {
                if (_leader == null)
                    _leader = node;
                else
                    _errorHandler.error(_log, OrdererMessages.dupeLeader(_objectType, o,
                            getOrderable(_leader.getName())), HiveMind.getLocation(o.getObject()),
                            null);
            }

            if ("*".equals(o.getPrereqs()))
            {
                if (_trailer == null)
                    _trailer = node;
                else
                    _errorHandler.error(_log, OrdererMessages.dupeTrailer(_objectType, o,
                            getOrderable(_trailer.getName())), HiveMind.getLocation(o.getObject()),
                            null);
            }

        }
    }

    private void addDependencies()
    {
        Iterator i = _orderingsList.iterator();

        while (i.hasNext())
        {
            ObjectOrdering o = (ObjectOrdering) i.next();

            addDependencies(o, getNode(o.getName()));
        }
    }

    private void addDependencies(ObjectOrdering orderable, Node node)
    {
        addPreRequisites(orderable, node);
        addPostRequisites(orderable, node);

        try
        {
            if (node != _leader)
                node.addDependency(_leader);

            if (node != _trailer)
                _trailer.addDependency(node);
        }
        catch (ApplicationRuntimeException ex)
        {
            // This code is unreachable ... but nonetheless.

            String name = node.getName();
            ObjectOrdering trigger = getOrderable(name);

            _errorHandler.error(_log, OrdererMessages.dependencyCycle(_objectType, orderable, ex),
                    HiveMind.getLocation(trigger.getObject()), ex);
        }
    }

    private void addPreRequisites(ObjectOrdering ordering, Node node)
    {
        String prereqs = ordering.getPrereqs();

        if ("*".equals(prereqs))
            return;

        String[] names = StringUtils.split(prereqs);

        for (int i = 0; i < names.length; i++)
        {
            String prename = names[i];

            Node prenode = getNode(prename);

            if (prenode == null)
            {
                _errorHandler.error(_log, OrdererMessages.badDependency(_objectType, prename,
                        ordering), HiveMind.getLocation(ordering.getObject()), null);
                continue;
            }
            else
            {
                try
                {
                    node.addDependency(prenode);
                }
                catch (ApplicationRuntimeException ex)
                {
                    _errorHandler.error(_log, OrdererMessages.dependencyCycle(_objectType,
                            ordering, ex), HiveMind.getLocation(ordering.getObject()), ex);
                }
            }
        }
    }

    private void addPostRequisites(ObjectOrdering ordering, Node node)
    {
        String postreqs = ordering.getPostreqs();

        if ("*".equals(postreqs))
            return;

        String[] names = StringUtils.split(postreqs);

        for (int i = 0; i < names.length; i++)
        {
            String postname = names[i];

            Node postnode = getNode(postname);

            if (postnode == null)
            {
                _errorHandler.error(_log, OrdererMessages.badDependency(_objectType, postname,
                        ordering), HiveMind.getLocation(ordering.getObject()), null);
            }
            else
            {
                try
                {
                    postnode.addDependency(node);
                }
                catch (ApplicationRuntimeException ex)
                {
                    _errorHandler.error(_log, OrdererMessages.dependencyCycle(_objectType,
                            ordering, ex), HiveMind.getLocation(ordering.getObject()), ex);
                }
            }
        }
    }

    private static class Node
    {
        private Object _object;
        private String _name;
        private List _dependencies;

        public Node(Object o, String name)
        {
            _object = o;
            _name = name;
            _dependencies = new ArrayList();
        }

        public String getName()
        {
            return _name;
        }

        public void addDependency(Node n)
        {
            if (n.isReachable(this))
                throw new ApplicationRuntimeException(
                        "A cycle has been detected from the initial object [" + _name + "]",
                        HiveMind.getLocation(_object), null);
            else
                _dependencies.add(n);
        }

        private boolean isReachable(Node n)
        {
            boolean reachable = (n == this);
            Iterator i = _dependencies.iterator();

            while (i.hasNext() && !reachable)
            {
                Node dep = (Node) i.next();
                reachable = (dep == n) ? true : dep.isReachable(n);
            }

            return reachable;
        }

        public List getOrder()
        {
            List result = new ArrayList();
            fillOrder(result);

            return result;
        }

        private void fillOrder(List result)
        {
            if (result.contains(_object))
                return;

            Iterator i = _dependencies.iterator();

            while (i.hasNext())
            {
                Node dep = (Node) i.next();
                dep.fillOrder(result);
            }

            if (_object != null)
                result.add(_object);
        }
    }

}