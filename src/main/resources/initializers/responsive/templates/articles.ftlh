<#import "_common.ftlh" as c />
<#--
    Description and title parameters are pulled from content markdown if defined there.
    You can add any template/page specific js / css in here. Example:

    <@c.page js=['custom.js', 'highlight.js'] css=['custom.css'] description=description!"" title=title>
-->
<@c.page description=description!"" title=title>
<@c.header />
<div class="main-container">
    <div class="main wrapper clearfix">
        <article>
            <section>
                <h1>${title}</h1>
                ${content?no_esc}
            </section>
        </article>

        <@c.sidebar />

    </div>
</div>
<@c.footer js=[] ga=googleAnalytics!"" />
</@c.page>
