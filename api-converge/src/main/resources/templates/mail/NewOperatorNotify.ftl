<html>
    <style type="text/css">
        body {
            font-family: 'DengXian', 'LiHei Pro', 'Microsoft YaHei', 'Microsoft JhengHei', '微軟正黑體';
        }
    </style>
    <body>
        系統於${.now?string("yyyy-MM-dd HH:mm:ss")}檢測到Operator API中有${newOperatorList?size}位<b>新進業者</b>，清單如下: 
        <ol>
            <#list newOperatorList as newOperator>
                <li>
                    ${newOperator.operatorName.zhTw}<small>(${newOperator.subAuthorityCode!newOperator.authorityCode})</small> 
                    [<a href="http://ptx.transportdata.tw/MOTC/v2/Basic/Operator?$format=JSON&$filter=OperatorName/Zh_tw eq '${newOperator.operatorName.zhTw}'">API連結</a>]
                </li>
            </#list>
        </ol>
        敬請平臺相關人員協助處理
    </body>
</html>