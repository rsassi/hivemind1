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

package org.apache.hivemind.management.log4j;

import org.apache.hivemind.impl.BaseLocatable;
import org.apache.hivemind.util.ToStringBuilder;

/**
 * Contains contributions defined by the <code>hivemind.management.Logger</code> schema.
 * 
 * @author Achim Huegen
 * @since 1.1
 */
public class LoggerContribution extends BaseLocatable
{
    private String _loggerPattern;

    public String getLoggerPattern()
    {
        return _loggerPattern;
    }

    public void setLoggerPattern(String string)
    {
        _loggerPattern = string;
    }

    public String toString()
    {
        ToStringBuilder builder = new ToStringBuilder(this);

        builder.append("loggerPattern", _loggerPattern);

        return _loggerPattern;
    }
}