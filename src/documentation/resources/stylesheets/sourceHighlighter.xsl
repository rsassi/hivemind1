<?xml version="1.0"?>
<!-- 
   Copyright 2004, 2005 The Apache Software Foundation

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
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <!-- Template that transforms source code that was already processed
		   by the SourceHighlighter transformer to xhtml markup -->
		
	<xsl:template match="/">
			<xsl:apply-templates/>
	</xsl:template>
	
	<!-- the obligatory copy-everything -->
	
	<xsl:template match="node() | @*">
			<xsl:copy>
				<xsl:apply-templates select="@*"/>
				<xsl:apply-templates/>
			</xsl:copy>
	</xsl:template>
	
	<!-- java code -->
	<xsl:template match="javasource-processed">
			<pre class="java-code">    
				<xsl:apply-templates mode="javasource"/>
			</pre>
	</xsl:template>
	
	<xsl:template match="java-plain" mode="javasource">
			<xsl:apply-templates/>
	</xsl:template>
	
	<xsl:template match="java-keyword" mode="javasource">
			<span class="java-keyword"><xsl:apply-templates/></span>
	</xsl:template>
	
	<xsl:template match="java-type" mode="javasource">
			<span class="java-type"><xsl:apply-templates/></span>
	</xsl:template>

	<xsl:template match="java-operator" mode="javasource">
			<span class="java-operator"><xsl:apply-templates/></span>
	</xsl:template>

	<xsl:template match="java-separator" mode="javasource">
			<span class="java-separator"><xsl:apply-templates/></span>
	</xsl:template>

	<xsl:template match="java-literal" mode="javasource">
			<span class="java-literal"><xsl:apply-templates/></span>
	</xsl:template>

	<xsl:template match="java-comment" mode="javasource">
			<span class="java-comment"><xsl:apply-templates/></span>
	</xsl:template>

	<xsl:template match="java-javadoc_comment" mode="javasource">
			<span class="java-javadoc-comment"><xsl:apply-templates/></span>
	</xsl:template>
	
	<xsl:template match="java-javadoc_tag" mode="javasource">
			<span class="java-javadoc-tag"><xsl:apply-templates/></span>
	</xsl:template>
	
	<!-- xml code -->
	<xsl:template match="xmlsource-processed">
			<pre class="xml-code">    
				<xsl:apply-templates mode="xmlsource"/>
			</pre>
	</xsl:template>
	
	<xsl:template match="xml-plain" mode="xmlsource">
			<span class="xml-plain"><xsl:apply-templates/></span>
	</xsl:template>
	
	<xsl:template match="xml-tag" mode="xmlsource">
			<span class="xml-tag"><xsl:apply-templates/></span>
	</xsl:template>
	
	<xsl:template match="xml-attribute" mode="xmlsource">
			<span class="xml-attribute"><xsl:apply-templates/></span>
	</xsl:template>
	
	<xsl:template match="xml-attribute-value" mode="xmlsource">
			<span class="xml-attribute-value"><xsl:apply-templates/></span>
	</xsl:template>
	
	<xsl:template match="xml-cdata" mode="xmlsource">
			<span class="xml-cdata"><xsl:apply-templates/></span>
	</xsl:template>
	
	<xsl:template match="xml-comment" mode="xmlsource">
			<span class="xml-comment"><xsl:apply-templates/></span>
	</xsl:template>

</xsl:stylesheet>
