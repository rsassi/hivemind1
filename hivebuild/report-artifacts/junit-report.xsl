<?xml version="1.0"?>
<!-- 
   Copyright 2005 The Apache Software Foundation

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
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <!-- Transforms to Forrest XDOC format. -->
  <xsl:output method="xml" indent="yes" encoding="US-ASCII" doctype-public="-//APACHE//DTD Documentation V1.3//EN" doctype-system="http://xml.apache.org/forrest/dtd/document-v13.dtd" />
  <xsl:template match="/">
    <document>
      <header>
        <title>JUnit Test Report</title>
      </header>
      <body>
        <section>
          <title>Test Suite Summary</title>
          <p>
            <strong> <xsl:value-of select="sum(//testsuite/@tests)"/> tests total. </strong>
          </p>
          <table>
            <tr>
              <th>TestCase</th>
              <th>Tests</th>
              <th>Time</th>
            </tr>
            <xsl:apply-templates select="//testsuite" mode="summary">
              <xsl:sort select="@package"/>
              <xsl:sort select="@name"/>
            </xsl:apply-templates>
            <tr>
              <td>
                <strong>** Total</strong>
              </td>
              <td>
                <strong>
                  <xsl:value-of select="sum(//testsuite/@tests)"/>
                </strong>
              </td>
              <td>
                <strong>
                  <xsl:value-of select="format-number(sum(//testsuite/@time), '#0.00')"/>
                </strong>
              </td>
            </tr>
          </table>
        </section>
        <section>
          <title>JUnit Test Details</title>
          <table>
            <tr>
              <th>Test</th>
              <th>Time</th>
            </tr>
            <xsl:apply-templates select="//testsuite" mode="detail">
              <xsl:sort select="@package"/>
              <xsl:sort select="@name"/>
            </xsl:apply-templates>
          </table>
        </section>
      </body>
    </document>
  </xsl:template>
  <xsl:template match="testsuite" mode="summary">
    <tr>
      <td>
        <link href="#{@package}.{@name}"><xsl:value-of select="@package"/>.<xsl:value-of select="@name"/></link>
      </td>
      <td>
        <xsl:value-of select="@tests"/>
      </td>
      <td>
        <xsl:value-of select="format-number(@time, '#0.00')"/>
      </td>
    </tr>
  </xsl:template>
  <xsl:template match="testsuite" mode="detail">
    <tr id="{@package}.{@name}">
      <th colspan="2"> <xsl:value-of select="@package"/>.<xsl:value-of select="@name"/> </th>
    </tr>
    <xsl:apply-templates select="testcase"/>
  </xsl:template>
  <xsl:template match="testcase">
    <tr>
      <td>
        <xsl:value-of select="@name"/>
      </td>
      <td>
        <xsl:value-of select="format-number(@time, '#0.00')"/>
      </td>
    </tr>
  </xsl:template>
</xsl:stylesheet>