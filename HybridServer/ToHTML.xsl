<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:c="http://www.esei.uvigo.es/dai/hybridserver"
>
	<xsl:output method="html" indent="yes" encoding="utf-8"/>
	
	<xsl:template match="/" >
		<html>
			<head>
				<title>Configuracion</title>
			</head>
			<body>
				<div id="container">
					<h1>Configuracion</h1>
					<div id="configuration">
						<xsl:apply-templates select="c:configuration/c:connections"/>
						<xsl:apply-templates select="c:configuration/c:database"/>
						<xsl:apply-templates select="c:configuration/c:servers"/>
					</div>
				</div>
			</body>
			
		</html>
	</xsl:template>
	
	<xsl:template match="c:connections">
		<div>
			<h3>Conexion</h3>
			<ul>
				<li><strong>HTTP:</strong>&#160;<xsl:value-of select="c:http"/></li>
				<li><strong>Servicio Web:</strong>&#160;<xsl:value-of select="c:webservice"/></li>
				<li><strong>Numero de Clientes:</strong>&#160;<xsl:value-of select="c:numClients"/></li>
			</ul>
		</div>
	</xsl:template>
	
	<xsl:template match="c:database">
		<div>
			<h3>Base de datos</h3>
			<li>
				<ul><strong>Usuario:</strong>&#160;<xsl:value-of select="c:user"/></ul>
				<ul><strong>Clave:</strong>&#160;<xsl:value-of select="c:password"/></ul>
				<ul><strong>URL:</strong>&#160;<xsl:value-of select="c:url"/></ul>
			</li>
		</div>
	</xsl:template>
	
	<xsl:template match="c:server">
		<div>
			<h5><xsl:value-of select="@name"/></h5>
			<li>
				<ul><strong>wsdl:</strong>&#160;<xsl:value-of select="@wsdl"/></ul>
				<ul><strong>Espacio de nombre:</strong>&#160;<xsl:value-of select="@namespace"/></ul>
				<ul><strong>Servicio:</strong>&#160;<xsl:value-of select="@service"/></ul>
				<ul><strong>Direccion HTTP:</strong>&#160;<xsl:value-of select="@httpAddress"/></ul>
			</li>
		</div>
	</xsl:template>
	
	<xsl:template match="c:servers">
		<div>
			<h3>Servidores</h3>
			<xsl:apply-templates select="c:server"/>
		</div>	
	</xsl:template>
	
	
	</xsl:stylesheet>