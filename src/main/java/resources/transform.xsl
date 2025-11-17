<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <!-- Formato de salida -->
    <xsl:output method="xml" indent="yes"/>

    <!-- Raíz del documento -->
    <xsl:template match="/students">
        <classroom>
            <xsl:apply-templates select="student"/>
        </classroom>
    </xsl:template>

    <!-- Transformar cada estudiante -->
    <xsl:template match="student">
        <person>
            <!-- Copiamos el id como atributo -->
            <xsl:attribute name="codigo">
                <xsl:value-of select="@id"/>
            </xsl:attribute>

            <fullname>
                <xsl:value-of select="name"/>
            </fullname>

            <years>
                <xsl:value-of select="age"/>
            </years>
        </person>
    </xsl:template>

</xsl:stylesheet>


