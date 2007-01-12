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

package org.apache.hivemind.schema.impl;

import org.apache.hivemind.parse.BaseAnnotationHolder;
import org.apache.hivemind.schema.AttributeModel;

/**
 * Implementation of {@link org.apache.hivemind.schema.AttributeModel}.
 *
 * @author Howard Lewis Ship
 */
public final class AttributeModelImpl extends BaseAnnotationHolder implements AttributeModel
{
    private String _name;
    private boolean _required;
	private boolean _unique;
    private String _translator;

    public String getName()
    {
        return _name;
    }

    public boolean isRequired()
    {
        return _required;
    }

    public void setName(String string)
    {
        _name = string;
    }

    public void setRequired(boolean b)
    {
        _required = b;
    }

	public void setUnique(boolean b)
	{
		_unique = b;
	}

	public boolean isUnique()
	{
		return _unique;
	}

    public String getTranslator()
    {
        return _translator;
    }

    public void setTranslator(String string)
    {
        _translator = string;
    }

}
