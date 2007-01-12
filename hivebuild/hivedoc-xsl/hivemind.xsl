<?xml version="1.0" encoding="UTF-8"?>
<!-- 
   Copyright 2004 The Apache Software Foundation

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" 
  xmlns:redirect="org.apache.xalan.xslt.extensions.Redirect" extension-element-prefixes="redirect">
  <xsl:template match="/registry">
    <html>
      <head>
        <title>HiveMind Registry</title>
        <link rel="STYLESHEET" type="text/css" href="hivemind.css"/>
      </head>
      <body>
        <h1>HiveMind Module Registry</h1>
        <table class="summary">
          <tbody>
            <tr>
              <th class="subhead">Module</th>
              <th class="subhead">Version</th>
              <th class="subhead">Description</th>
            </tr>
            <xsl:for-each select="module">
              <xsl:sort select="@id"/>
              <tr>
                <td>
                  <a href="module/{@id}.html">
                    <xsl:value-of select="@id"/>
                  </a>
                </td>
                <td>
                  <xsl:value-of select="@version"/>
                </td>
                <td>
                  <xsl:value-of select="./text()"/>
                </td>
              </tr>
            </xsl:for-each>
          </tbody>
        </table>
        <table class="summary">
          <!-- Since we know, for a fact, that there are always at least one each of
						 configuration, service and schema, we don't do conditionals here. -->
          <tr>
            <th class="subhead">Configurations</th>
            <th class="subhead">Services</th>
            <th class="subhead">Schemas</th>
          </tr>
          <tr>
            <td>
              <xsl:for-each select="/registry/module/configuration-point">
                <xsl:sort select="@id"/>
                <a href="config/{@id}.html">
                  <xsl:value-of select="@id"/>
                </a>
                <br/>
              </xsl:for-each>
            </td>
            <td>
              <xsl:for-each select="/registry/module/service-point">
                <xsl:sort select="@id"/>
                <a href="service/{@id}.html">
                  <xsl:value-of select="@id"/>
                </a>
                <br/>
              </xsl:for-each>
            </td>
            <td>
              <xsl:for-each select="/registry/module/schema">
                <xsl:sort select="@id"/>
                <a href="schema/{@id}.html">
                  <xsl:value-of select="@id"/>
                </a>
                <br/>
              </xsl:for-each>
            </td>
          </tr>
        </table>
        <xsl:apply-templates select="module"/>
      </body>
    </html>
  </xsl:template>
  <xsl:template match="module" mode="link">
    <a href="../module/{@id}.html">
      <xsl:value-of select="@id"/>
    </a>
  </xsl:template>
  <xsl:template match="module">
    <xsl:message>Writing module <xsl:value-of select="@id"/></xsl:message>
    <redirect:write file="module/{@id}.html">
      <html>
        <head>
          <title>HiveMind Registry - Module <xsl:value-of select="@id"/></title>
          <link rel="stylesheet" type="text/css" href="../hivemind.css"/>
        </head>
        <body>
          <a href="../index.html">Back to index</a>
          <hr/>
          <h1>Module <xsl:value-of select="@id"/> </h1>
          <table class="summary">
            <tbody>
              <tr>
                <th>Version</th>
                <td colspan="2">
                  <xsl:value-of select="@version"/>
                </td>
              </tr>
              <xsl:if test="normalize-space(./text())">
                <tr>
                  <td class="description" colspan="3">
                    <xsl:value-of select="./text()"/>
                  </td>
                </tr>
              </xsl:if>
            </tbody>
          </table>
          <!-- Similar to the master listing, but just for artifacts within this module,
			   and we can make use of the mode=link templates. Unlink the master listing,
			   we need to check to see if a column is included. -->
          <table class="summary">
            <tr>
              <xsl:if test="configuration-point">
                <th class="subhead">Configurations</th>
              </xsl:if>
              <xsl:if test="service-point">
                <th class="subhead">Services</th>
              </xsl:if>
              <xsl:if test="schema">
                <th class="subhead">Schemas</th>
              </xsl:if>
            </tr>
            <tr>
              <xsl:if test="configuration-point">
                <td>
                  <xsl:for-each select="configuration-point">
                    <xsl:sort select="@id"/>
                    <xsl:apply-templates select="." mode="link"/>
                    <br/>
                  </xsl:for-each>
                </td>
              </xsl:if>
              <xsl:if test="service-point">
                <td>
                  <xsl:for-each select="service-point">
                    <xsl:sort select="@id"/>
                    <xsl:apply-templates select="." mode="link"/>
                    <br/>
                  </xsl:for-each>
                </td>
              </xsl:if>
              <xsl:if test="schema">
                <td>
                  <xsl:for-each select="schema">
                    <xsl:sort select="@id"/>
                    <xsl:apply-templates select="." mode="link"/>
                    <br/>
                  </xsl:for-each>
                </td>
              </xsl:if>
            </tr>
          </table>
          <!-- Contributions and implementations go right in the module index. -->
          <xsl:apply-templates select="contribution">
            <xsl:sort select="@configuration-id"/>
          </xsl:apply-templates>
          <xsl:apply-templates select="implementation">
            <xsl:sort select="@service-id"/>
          </xsl:apply-templates>
          <!-- These templates will generate further documents. -->
          <xsl:apply-templates select="configuration-point">
            <xsl:sort select="@id"/>
          </xsl:apply-templates>
          <xsl:apply-templates select="service-point">
            <xsl:sort select="@id"/>
          </xsl:apply-templates>
          <xsl:apply-templates select="schema">
            <xsl:sort select="@id"/>
          </xsl:apply-templates>
          <hr/>
          <a href="../index.html">Back to index</a>
        </body>
      </html>
    </redirect:write>
  </xsl:template>
  <xsl:template match="configuration-point" mode="link">
    <a href="../config/{@id}.html">
      <xsl:value-of select="@id"/>
    </a>
  </xsl:template>
  <!-- Links used in an artifact configuration-point, service-point, schema -->
  <xsl:template name="artifact-links">
    <a href="../index.html">Back to index</a>
    <br/>
    <a href="../module/{../@id}.html"> Back to module <xsl:value-of select="../@id"/> </a>
  </xsl:template>
  <xsl:template match="configuration-point">
    <xsl:message>Writing configuration-point <xsl:value-of select="@id"/></xsl:message>
    <redirect:write file="config/{@id}.html">
      <html>
        <head>
          <title>HiveMind Registry - Configuration Point <xsl:value-of select="@id"/></title>
          <link rel="stylesheet" type="text/css" href="../hivemind.css"/>
        </head>
        <body>
          <xsl:call-template name="artifact-links"/>
          <hr/>
          <h1>Configuration Point <xsl:value-of select="@id"/> </h1>
          <table class="summary">
            <tbody>
              <tr>
                <th>Expected Count</th>
                <td>
                  <xsl:if test="not(@count)">unbounded</xsl:if>
                  <xsl:value-of select="@count"/>
                </td>
              </tr>
              <xsl:if test="@schema-id">
                <tr>
                  <th>Schema</th>
                  <td>
                    <xsl:apply-templates select="/registry/module/schema[@id = current()/@schema-id]" mode="link"/>
                  </td>
                </tr>
              </xsl:if>
              <xsl:if test="normalize-space(./text())">
                <tr>
                  <td colspan="2" class="description">
                    <xsl:value-of select="./text()"/>
                  </td>
                </tr>
              </xsl:if>
            </tbody>
          </table>
          <xsl:apply-templates select="schema" mode="embedded"/>
          <xsl:for-each select="/registry/module/contribution[@configuration-id = current()/@id]">
            <xsl:sort select="../@id"/>
            <h3>Contributions from module <a href="../module/{../@id}.html#{@uid}"> <xsl:value-of select="../@id"/> 
              </a> </h3>
            <ul>
              <xsl:apply-templates select="*" mode="raw"/>
            </ul>
          </xsl:for-each>
          <hr/>
          <xsl:call-template name="artifact-links"/>
        </body>
      </html>
    </redirect:write>
  </xsl:template>
  <xsl:template match="schema" mode="link">
    <a href="../schema/{@id}.html">
      <xsl:value-of select="@id"/>
    </a>
  </xsl:template>
  <xsl:template match="schema">
    <xsl:message>Writing schema <xsl:value-of select="@id"/></xsl:message>
    <redirect:write file="schema/{@id}.html">
      <html>
        <head>
          <title>HiveMind Registry - Schema <xsl:value-of select="@id"/></title>
          <link rel="stylesheet" type="text/css" href="../hivemind.css"/>
        </head>
        <body>
          <xsl:call-template name="artifact-links"/>
          <hr/>
          <h1>Schema <xsl:value-of select="@id"/></h1>
          <xsl:call-template name="schema-details"/>
          <hr/>
          <xsl:call-template name="artifact-links"/>
        </body>
      </html>
    </redirect:write>
  </xsl:template>
  <xsl:template name="schema-details">
    <table class="summary">
      <tbody>
        <xsl:if test="normalize-space(./text())">
          <tr>
            <td/>
            <td colspan="2" class="description">
              <xsl:value-of select="./text()"/>
            </td>
          </tr>
        </xsl:if>
        <xsl:apply-templates select="element">
          <xsl:sort select="@name"/>
        </xsl:apply-templates>
      </tbody>
    </table>
  </xsl:template>
  <xsl:template match="schema" mode="embedded">
    <h3>Schema</h3>
    <xsl:call-template name="schema-details"/>
  </xsl:template>
  <xsl:template match="element">
    <tr>
      <th class="section-id" colspan="3">Element <span class="tag"><xsl:value-of select="@name"/></span></th>
    </tr>
    <xsl:if test="normalize-space(./text())">
      <tr>
        <td colspan="3" class="description">
          <xsl:value-of select="./text()"/>
        </td>
      </tr>
    </xsl:if>
    <xsl:for-each select="attribute">
      <xsl:sort select="name"/>
      <xsl:apply-templates select="."/>
    </xsl:for-each>
    <xsl:if test="rules|conversion">
      <tr>
        <td/>
        <td colspan="2">
          <h3>Conversion Rules</h3>
          <ul>
            <xsl:apply-templates select="conversion|rules/*" mode="raw"/>
          </ul>
        </td>
      </tr>
    </xsl:if>
    <xsl:if test="element">
      <tr>
        <td/>
        <td colspan="2">
          <table class="summary">
            <tbody>
              <xsl:apply-templates select="element"/>
            </tbody>
          </table>
        </td>
      </tr>
    </xsl:if>
  </xsl:template>
  <xsl:template match="attribute">
    <tr>
      <td/>
      <td>Attribute <span class="attribute"><xsl:value-of select="@name"/></span></td>
      <td>
        <xsl:choose>
          <xsl:when test="@required = 'true'"> Required </xsl:when>
          <xsl:otherwise> Optional </xsl:otherwise>
        </xsl:choose>
        <xsl:if test="@unique = 'true'"> (Unique) </xsl:if>
      </td>
    </tr>
    <xsl:if test="@translator">
      <tr>
        <td/>
        <th>Translator</th>
        <td>
          <xsl:value-of select="@translator"/>
        </td>
      </tr>
    </xsl:if>
    <xsl:if test="normalize-space(./text())">
      <tr>
        <td/>
        <td colspan="2" class="description">
          <xsl:value-of select="./text()"/>
        </td>
      </tr>
    </xsl:if>
  </xsl:template>
  <xsl:template match="contribution">
    <h2> <xsl:attribute name="id"><xsl:value-of select="@uid"/></xsl:attribute> Contribution to <xsl:apply-templates 
      select="/registry/module/configuration-point[@id = current()/@configuration-id]" mode="link"/> </h2>
    <ul>
      <xsl:apply-templates mode="raw"/>
    </ul>
  </xsl:template>
  <xsl:template match="service-point" mode="link">
    <a href="../service/{@id}.html">
      <xsl:value-of select="@id"/>
    </a>
  </xsl:template>
  <xsl:template match="service-point">
    <xsl:message>Writing service-point <xsl:value-of select="@id"/></xsl:message>
    <redirect:write file="service/{@id}.html">
      <html>
        <head>
          <title>HiveMind Registry - Service <xsl:value-of select="@id"/></title>
          <link rel="stylesheet" type="text/css" href="../hivemind.css"/>
        </head>
        <body>
          <xsl:call-template name="artifact-links"/>
          <hr/>
          <h1>Service <xsl:value-of select="@id"/> </h1>
          <table class="summary">
            <tbody>
              <tr>
                <th>Interface</th>
                <td>
                  <xsl:value-of select="@interface"/>
                </td>
              </tr>
              <xsl:if test="@parameters-schema-id">
                <tr>
                  <th>Parameters Schema</th>
                  <td>
                    <xsl:apply-templates select="/registry/module/schema[@id = current()/@parameters-schema-id]" 
                      mode="link"/>
                  </td>
                </tr>
              </xsl:if>
              <xsl:if test="@parameters-schema-id or parameters-schema">
                <tr>
                  <th>Parameters Occurs</th>
                  <td>
                    <xsl:if test="not(@parameters-occurs)">required</xsl:if>
                    <xsl:value-of select="@parameters-occurs"/>
                  </td>
                </tr>
              </xsl:if>
              <xsl:if test="normalize-space(./text())">
                <tr>
                  <td colspan="2" class="description">
                    <xsl:value-of select="./text()"/>
                  </td>
                </tr>
              </xsl:if>
            </tbody>
          </table>
          <xsl:apply-templates select="parameters-schema"/>
          <xsl:if test="create-instance|invoke-factory|interceptor">
            <h2>Implementation</h2>
            <ul>
              <xsl:apply-templates select="create-instance|invoke-factory|interceptor"/>
            </ul>
          </xsl:if>
          <xsl:for-each select="/registry/module/implementation[@service-id = current()/@id]">
            <xsl:sort select="../@id"/>
            <h3>Implementations from module <a href="../module/{../@id}.html#{@uid}"> <xsl:value-of select="../@id"/> 
              </a> </h3>
            <ul>
              <xsl:apply-templates/>
            </ul>
          </xsl:for-each>
          <hr/>
          <xsl:call-template name="artifact-links"/>
        </body>
      </html>
    </redirect:write>
  </xsl:template>
  <xsl:template match="parameters-schema">
    <h3>Parameters Schema</h3>
    <xsl:call-template name="schema-details"/>
  </xsl:template>
  <xsl:template match="implementation">
    <h2> <xsl:attribute name="id"><xsl:value-of select="@uid"/></xsl:attribute> Service Implementation 
      <xsl:apply-templates select="/registry/module/service-point[@id = current()/@service-id]" mode="link"/> </h2>
    <ul>
      <xsl:apply-templates/>
    </ul>
  </xsl:template>
  <xsl:template match="create-instance">
    <li> <span class="tag">&lt;create-instance</span> <span class="attribute"> class</span>="<xsl:value-of 
      select="@class"/>" <xsl:if test="@model"> <span class="attribute"> model</span>="<xsl:value-of select="@model"/>" 
      </xsl:if> <span class="tag">/&gt;</span> </li>
  </xsl:template>
  <xsl:template match="invoke-factory">
    <li>
      <span class="tag">&lt;invoke-factory</span>
      <xsl:if test="@service-id"> <span class="attribute"> service-id</span>="<xsl:apply-templates 
        select="/registry/module/service-point[@id = current()/@id]" mode="link"/>" </xsl:if>
      <xsl:if test="@model"> <span class="attribute"> model</span>="<xsl:value-of select="@model"/>" </xsl:if>
      <span class="tag">&gt;</span>
      <xsl:if test="*">
        <br/>
        <ul>
          <xsl:apply-templates mode="raw"/>
        </ul>
        <span class="tag">&lt;/invoke-factory&gt;</span>
      </xsl:if>
    </li>
  </xsl:template>
  <xsl:template match="interceptor">
    <li> <span class="tag">&lt;interceptor</span> <span class="attribute"> service-id</span>="<xsl:apply-templates 
      select="/registry/module/service-point[@id = current()/@service-id]" mode="link"/>" <xsl:if test="@before"> <span 
      class="attribute"> before</span>="<xsl:value-of select="@before"/>" </xsl:if> <xsl:if test="@after"> <span 
      class="attribute"> after</span>="<xsl:value-of select="@after"/>" </xsl:if> <xsl:choose> <xsl:when test="*"> 
      <br/> <span class="tag">&gt;</span> <ul> <xsl:apply-templates mode="raw"/> </ul> <span 
      class="tag">&lt;/<xsl:value-of select="name()"/>&gt;</span> </xsl:when> <xsl:when 
      test="normalize-space(./text())"> <span class="tag">&gt;</span> <br/> <ul> <li><xsl:value-of select="."/></li> 
      </ul> <span class="tag">&lt;/<xsl:value-of select="name()"/>&gt;</span> </xsl:when> <xsl:when 
      test="count(child::*) = 0"> <span class="tag">/&gt;</span> </xsl:when> </xsl:choose> </li>
  </xsl:template>
  <xsl:template match="*" mode="raw">
    <li>
      <span class="tag">&lt;<xsl:value-of select="name()"/></span>
      <xsl:if test="@*">
        <xsl:for-each select="@*" xml:space="preserve">
		  		<span class="attribute"> <xsl:value-of select="name()"/></span>="<xsl:value-of select="."/>"
		  	</xsl:for-each>
      </xsl:if>
      <xsl:choose>
        <xsl:when test="*">
          <span class="tag">&gt;</span>
          <br/>
          <ul>
            <xsl:apply-templates mode="raw"/>
          </ul>
          <span class="tag">&lt;/<xsl:value-of select="name()"/>&gt;</span>
        </xsl:when>
        <xsl:when test="normalize-space()">
          <span class="tag">&gt;</span>
          <br/>
          <ul>
            <li>
              <xsl:value-of select="."/>
            </li>
          </ul>
          <span class="tag">&lt;/<xsl:value-of select="name()"/>&gt;</span>
        </xsl:when>
        <xsl:when test="count(child::*) = 0">
          <span class="tag">/&gt;</span>
        </xsl:when>
      </xsl:choose>
    </li>
  </xsl:template>
</xsl:stylesheet>