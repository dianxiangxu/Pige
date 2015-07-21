<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

   <xsl:output method="xml" encoding="iso-8859-1" omit-xml-declaration="no" indent="yes" version="1.0"/>

   <xsl:template match="/">
      <xsl:element name="gxml">
         <xsl:apply-templates select="gxml"/>
      </xsl:element>
   </xsl:template>
   <xsl:template match="gxml">
      <xsl:element name="graph">
         <xsl:attribute name="id">
            <xsl:value-of select="graph/@id"/>
         </xsl:attribute>
         <xsl:attribute name="type">
            <xsl:value-of select="graph/@type"/>
         </xsl:attribute>
         <xsl:apply-templates select="graph/labels">
            <xsl:sort select="@text" data-type="text"/>
         </xsl:apply-templates>
         <xsl:apply-templates select="graph/node">
            <xsl:sort select="@id" data-type="text"/>
         </xsl:apply-templates>
         <xsl:apply-templates select="graph/arc">
            <xsl:sort select="@id" data-type="text"/>
         </xsl:apply-templates>
      </xsl:element>
   </xsl:template>
      
   <xsl:template match="graph/node">
      <xsl:element name="node">
         <xsl:call-template name="abstractnode"/>
      </xsl:element>
   </xsl:template>
   
   <xsl:template match="graph/labels">
      <xsl:element name="labels">
         <xsl:attribute name="x">
            <xsl:value-of select="@positionX"/>
         </xsl:attribute>
         <xsl:attribute name="y">
            <xsl:value-of select="@positionY"/>
         </xsl:attribute>
         <xsl:attribute name="width">
            <xsl:value-of select="@width"/>
         </xsl:attribute>
         <xsl:attribute name="height">
            <xsl:value-of select="@height"/>
         </xsl:attribute>
         <xsl:attribute name="border">
            <xsl:value-of select="@border"/>
         </xsl:attribute>
         <xsl:element name="text">
            <xsl:value-of select="@text"/>
         </xsl:element>
      </xsl:element>
   </xsl:template>
   
   <xsl:template name="abstractnode">
      <xsl:attribute name="id">
         <xsl:value-of select="@id"/>
      </xsl:attribute>
      <xsl:call-template name="graphics"/>
      <xsl:call-template name="name"/>
   </xsl:template>
   
   <xsl:template match="graph/arc">
      <xsl:element name="arc">
         <xsl:attribute name="id">
            <xsl:value-of select="@id"/>
         </xsl:attribute>
         <xsl:attribute name="source">
            <xsl:value-of select="@source"/>
         </xsl:attribute>
         <xsl:attribute name="target">
            <xsl:value-of select="@target"/>
         </xsl:attribute>
         <xsl:call-template name="event"/>
         <xsl:call-template name="precondition"/>
         <xsl:call-template name="postcondition"/>
         <xsl:call-template name="graphics"/>
         <xsl:apply-templates select="arcpath">
            <xsl:sort select="@id" data-type="text"/>
         </xsl:apply-templates>
         <xsl:element name="type">
            <xsl:attribute name="value">
               <xsl:value-of select="@type"/>
            </xsl:attribute>
         </xsl:element>
      </xsl:element>
   </xsl:template>
   
   <xsl:template match="arcpath">
      <xsl:element name = "arcpath">
         <xsl:attribute name = "id">
            <xsl:value-of select ="@id"/>
         </xsl:attribute>
         <xsl:attribute name = "x">
            <xsl:value-of select ="@xCoord"/>
         </xsl:attribute>
         <xsl:attribute name = "y">
            <xsl:value-of select ="@yCoord"/>
         </xsl:attribute>
         <xsl:attribute name = "curvePoint">
            <xsl:value-of select="@arcPointType"/>
         </xsl:attribute>
      </xsl:element>
   </xsl:template>
   
   <xsl:template name="graphics">
      <xsl:element name="graphics">
         <xsl:if test="(string-length(@positionX) > 0) and (string-length(@positionY) > 0)">
            <xsl:element name="position">
               <xsl:attribute name="x">
                  <xsl:value-of select="@positionX"/>
               </xsl:attribute>
               <xsl:attribute name="y">
                  <xsl:value-of select="@positionY"/>
               </xsl:attribute>
            </xsl:element>
         </xsl:if>
      </xsl:element>
   </xsl:template>
   
   <xsl:template name="name">
      <xsl:element name="name">
         <xsl:element name="value">
            <xsl:value-of select="@name"/>
         </xsl:element>
         <xsl:element name="graphics">
            <xsl:if test="(string-length(@nameOffsetX) > 0)and (string-length(@nameOffsetY) > 0)">
               <xsl:element name="offset">
                  <xsl:attribute name="x">
                     <xsl:value-of select="@nameOffsetX"/>
                  </xsl:attribute>
                  <xsl:attribute name="y">
                     <xsl:value-of select="@nameOffsetY"/>
                  </xsl:attribute>
               </xsl:element>
            </xsl:if>
         </xsl:element>
      </xsl:element>
   </xsl:template>
      
   <xsl:template name="inscription">
      <xsl:element name="inscription">
         <xsl:element name="value">
            <xsl:value-of select="@inscription"/>
         </xsl:element>
         <xsl:element name="graphics">
            <xsl:if test="(string-length(@inscriptionOffsetX) > 0)  and (string-length(@inscriptionOffsetY) > 0)">
               <xsl:element name="offset">
                  <xsl:attribute name="x">
                     <xsl:value-of select="@inscriptionOffsetX"/>
                  </xsl:attribute>
                  <xsl:attribute name="y">
                     <xsl:value-of select="@inscriptionOffsetY"/>
                  </xsl:attribute>
               </xsl:element>
            </xsl:if>
         </xsl:element>
      </xsl:element>
   </xsl:template>

   <xsl:template name="event">
      <xsl:element name = "event">
         <xsl:element name = "value">
            <xsl:value-of select="@event"/>
         </xsl:element>
      </xsl:element>
   </xsl:template>

   <xsl:template name="precondition">
      <xsl:element name = "precondition">
         <xsl:element name = "value">
            <xsl:value-of select="@precondition"/>
         </xsl:element>
      </xsl:element>
   </xsl:template>

   <xsl:template name="postcondition">
      <xsl:element name = "postcondition">
         <xsl:element name = "value">
            <xsl:value-of select="@postcondition"/>
         </xsl:element>
      </xsl:element>
   </xsl:template>
      
</xsl:stylesheet>