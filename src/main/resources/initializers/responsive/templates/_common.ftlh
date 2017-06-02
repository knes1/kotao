<#--

This file contains common FreeMarker macros and functions. It is intended to be imported and used in other templates.

-->


<#-- Page macro. Wraps the page markup around the nested content. -->
<#macro page js=[] css=[] description='' title=''>
<!doctype html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7" lang=""> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8" lang=""> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9" lang=""> <![endif]-->
<!--[if gt IE 8]><!--> <html class="no-js" lang=""> <!--<![endif]-->
	<head>
		<meta charset="utf-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
		<title>${title}</title>
		<meta name="description" content="${description}">
		<meta name="viewport" content="width=device-width, initial-scale=1">
		<link rel="apple-touch-icon" href="apple-touch-icon.png">

		<#-- Add CSS that goes to every page in here -->
		<#assign cssIncludes = [
			'normalize.min.css',
			'main.css'
		] />

		<#list cssIncludes + css as cssInc>
		<link rel="stylesheet" href="${rootDir}css/${cssInc}">
		</#list>

		<#-- Add JS that goes to every page in here -->
		<#assign jsIncludes = [
			'vendor/modernizr-2.8.3-respond-1.4.2.min.js'
		] />

		<#list jsIncludes + js as jsInc>
		<script src="${rootDir}js/${jsInc}"></script>
		</#list>
	</head>
	<body>
	<!--[if lt IE 8]>
	<p class="browserupgrade">You are using an <strong>outdated</strong> browser. Please <a href="http://browsehappy.com/">
		upgrade your browser</a> to improve your experience.</p>
	<![endif]-->

		<#nested />

	</body>
</html>
</#macro>

<#-- Sidebar macro. Customize sidebar rendering and content here. -->
<#macro sidebar>
<aside>
    <h3>Sidebar</h3>
    <p>
		Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam sodales urna non odio egestas tempor. Nunc vel
		vehicula ante. Etiam bibendum iaculis libero, eget molestie nisl pharetra in. In semper consequat est, eu
		porta velit mollis nec. Curabitur posuere enim eget turpis feugiat tempor. Etiam ullamcorper lorem dapibus
		velit suscipit ultrices.
	</p>
</aside>
</#macro>


<#-- Header macro. Customize header rendering and navbar here. -->
<#macro header>

	<div class="header-container">
		<header class="wrapper clearfix">
			<h1 class="title">${project?capitalize}</h1>
			<nav>
				<ul>
					<li><a href="${rootDir}">Home</a></li>
					<li><a href="#">About</a></li>
					<li><a href="https://github.com/knes1/kotao">Kotao</a></li>
				</ul>
			</nav>
		</header>
	</div>

</#macro>


<#-- FOOTER MACRO
 Contains footer markup. Takes an array of javascript file names you want to load at the end of
 the html file as the first parameter. Second parameter is site ID for google analytics
-->
<#macro footer js=[] ga=''>
	<div class="footer-container">
		<footer class="wrapper">
			<h4>Brewed in <a href="https://github.com/knes1/kotao">Kotao</a>. Template from <a href="http://www.initializr.com/">Initializr</a>.</h4>
		</footer>
	</div>

	<#-- We define and handle jquery separtely, as we'll first try to download it from CDN -->
	<#assign jQueryVersion = '1.11.2' />

	<#-- Define any additional js that you always want to load in the footer here.
		 e.g. footerJs = ['bootstrap.min.js', 'slider.min.js'] ... -->
	<#assign footerJs = [] />

	<#-- custom handle CDN and failover to local version -->
	<script src="//ajax.googleapis.com/ajax/libs/jquery/${jQueryVersion}/jquery.min.js"></script>
	<script>window.jQuery || document.write('<script src="${rootDir}js/vendor/jquery-${jQueryVersion}.min.js"><\/script>')</script>

	<#list footerJs + js as jsIncludes>
	<script src="${rootDir}js/${jsIncludes}"></script>
	</#list>

	<@c.googleAnalytics ga/>

</#macro>

<#function summary content>
	<#return content?split("<!-- break -->")?first />
</#function>

<!-- Inserts google anaytics tracker and takes site id as parameter -->
<#macro googleAnalytics ua>
<#if ua?has_content && ua != 'UA-XXXXX-X'>
<script>
    (function(b,o,i,l,e,r){b.GoogleAnalyticsObject=l;b[l]||(b[l]=
            function(){(b[l].q=b[l].q||[]).push(arguments)});b[l].l=+new Date;
        e=o.createElement(i);r=o.getElementsByTagName(i)[0];
        e.src='//www.google-analytics.com/analytics.js';
        r.parentNode.insertBefore(e,r)}(window,document,'script','ga'));
    ga('create','${ua?js_string}','auto');ga('send','pageview');
</script>
</#if>
</#macro>