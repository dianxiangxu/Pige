<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  
   <xsl:output method="xml" encoding="iso-8859-1" omit-xml-declaration="no" indent="yes"/>
   
   <xsl:strip-space elements="*"/>
   
   <xsl:template match="gxml">	
      <xsl:element name="tools">
         <xsl:attribute name="id">
            <xsl:value-of select="graph/@id"/>
         </xsl:attribute>	
         <xsl:attribute name="type">
            <xsl:value-of select="graph/@type"/>
         </xsl:attribute>		
         <xsl:apply-templates select="graph"/>
      </xsl:element>
   </xsl:template>
   
   <xsl:template match="graph">
      <xsl:apply-templates select="labels"/>
      <xsl:apply-templates select="node"/>
      <xsl:apply-templates select="arc"/>
   </xsl:template>
	
   <xsl:template match="labels">
      <xsl:element name="labels">
         <xsl:attribute name="xPosition">
            <xsl:value-of select="@x"/>
         </xsl:attribute>
         <xsl:attribute name="yPosition">
            <xsl:value-of select="@y"/>
         </xsl:attribute>
         <xsl:attribute name="w">
            <xsl:value-of select="@width"/>
         </xsl:attribute>
         <xsl:attribute name="h">
            <xsl:value-of select="@height"/>
         </xsl:attribute>
         <xsl:attribute name="border">
            <xsl:value-of select="@border"/>
         </xsl:attribute>
         <xsl:attribute name="txt">
            <xsl:value-of select="text"/>
         </xsl:attribute>                        
      </xsl:element>
   </xsl:template>
   
   <xsl:template match="node">
      <xsl:element name="node">
         <xsl:call-template name="abstractnode"/>
      </xsl:element>
   </xsl:template>
   
   <xsl:template match="arc">
      <xsl:element name="arc">
         <xsl:attribute name="id">
            <xsl:value-of select="id/value"/>
         </xsl:attribute>
         <xsl:attribute name="source">
            <xsl:value-of select="@source"/>
         </xsl:attribute>
         <xsl:attribute name="target">
            <xsl:value-of select="@target"/>
         </xsl:attribute>
         <xsl:attribute name="event">
            <xsl:value-of select="event/value"/>
         </xsl:attribute>
          <xsl:attribute name="precondition">
            <xsl:value-of select="precondition/value"/>
         </xsl:attribute>
         <xsl:attribute name="postcondition">
            <xsl:value-of select="postcondition/value"/>
         </xsl:attribute>
         <xsl:attribute name="inscription">
            <xsl:value-of select="inscription/value"/>
         </xsl:attribute>
         <xsl:attribute name="inscriptionOffsetX">
            <xsl:value-of select="inscription/graphics/offset/@x"/>
         </xsl:attribute>
         <xsl:attribute name="inscriptionOffsetY">
            <xsl:value-of select="inscription/graphics/offset/@y"/>
         </xsl:attribute>
         <xsl:apply-templates select="arcpath"/>		
         <xsl:apply-templates select="type"/>		
      </xsl:element>
   </xsl:template>
          
   <xsl:template name="abstractnode">
      <xsl:attribute name="name">
         <xsl:value-of select="name/value"/>
      </xsl:attribute>
      <xsl:attribute name="nameOffsetX">
         <xsl:value-of select="name/graphics/offset/@x"/>		
      </xsl:attribute>
      <xsl:attribute name="nameOffsetY">
         <xsl:value-of select="name/graphics/offset/@y"/>		
      </xsl:attribute>			
      <xsl:call-template name="graphobject"/>	
   </xsl:template>	
   
   <xsl:template name="graphobject">
      <xsl:attribute name="id">
         <xsl:value-of select="@id"/>
      </xsl:attribute>
      <xsl:attribute name="positionX">
         <xsl:value-of select="graphics/position/@x"/>
      </xsl:attribute>
      <xsl:attribute name="positionY">
         <xsl:value-of select="graphics/position/@y"/>
      </xsl:attribute>
    </xsl:template>
    
   <xsl:template match="arcpath">
      <xsl:element name="arcpath">
         <xsl:attribute name="x">
            <xsl:value-of select="@x"/>
         </xsl:attribute>
         <xsl:attribute name="y">
            <xsl:value-of select="@y"/>
         </xsl:attribute>
         <xsl:attribute name="arcPointType">
            <xsl:value-of select="@curvePoint"/>
         </xsl:attribute>
      </xsl:element>
   </xsl:template>
   
   <xsl:template match="type">
      <xsl:element name="type">        		
         <xsl:attribute name="type">				
            <xsl:value-of select="@value"/>
         </xsl:attribute>
      </xsl:element>
   </xsl:template>      
    
</xsl:stylesheet>
