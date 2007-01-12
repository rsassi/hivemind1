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

package org.apache.examples.panorama.discussions;

/**
 * Placeholder for startup logic for the Discussions tool. This style reflects the
 * "legacy" approach in Panorama, which relied on a startup class invoking a central EJB, which
 * then invoked public static methods.  As tools are updated to make use of HiveMind, these
 * startup classes are converted into startup services.
 *
 * @author Howard Lewis Ship
 */
public class DiscussionsStartup
{
    public static void init()
    {
        System.out.println("DiscussionsStartup invoked.");
    }
}
