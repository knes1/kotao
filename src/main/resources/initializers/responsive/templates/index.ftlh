<#import "_common.ftlh" as c />
<@c.page description="" title=(project?capitalize + ' Homepage')>
<@c.header />
<div class="main-container">
    <div class="main wrapper clearfix">
        <article>
            <#list collectionPage as article>
                <section>
                    <h2>${article.model.title}</h2>
                    <p>${c.summary(article.content)}</p>
                    <p>
                        <a href="${rootDir + article.link}">Read More</a>
                    </p>
                </section>
            </#list>
        </article>

        <@c.sidebar />

    </div> <#-- #main -->
</div> <#-- #main-container -->
<@c.footer js=[] ga=googleAnalytics!"" />
</@c.page>
