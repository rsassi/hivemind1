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

package org.apache.hivemind;


/**
 * Represents a location within a resource; this is universally
 * used to support error reporting, by relating run-time objects
 * back to specific locations wtihin specific resources.
 * 
 * @author Howard M. Lewis Ship, glongman@intelligentworks.com
 */
public interface Location
{
	/**
	 * The resource containing the location.
	 */
    public Resource getResource();
    
    /**
     * The line within the resource containing the location,
     * or -1 if the line number is not known.
     * 
     */
    public int getLineNumber();
    
    /**
     * The column number, or -1 if not known.
     */
    public int getColumnNumber();
}