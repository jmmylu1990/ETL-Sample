<html>
    <style type="text/css">
        body {
            font-family: 'DengXian', 'LiHei Pro', 'Microsoft YaHei', 'Microsoft JhengHei', '微軟正黑體';
        }
    </style>
    <body>
        <p>-- 排程【${jobName}】 --</p>
        於${.now?string("yyyy-MM-dd HH:mm:ss")}<strong>完成執行</strong><br />
        介接來源: <a href="${resourceUrl}">${resourceUrl}</a>
        <ul>
            <li>來源資料數: ${resourceCount?string(",###")}筆</li>
            <li>匯入資料數: ${resourceCount?string(",###")}筆</li>
        </ul>
        <br />
    </body>
</html>