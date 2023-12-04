<html>
    <style type="text/css">
        body {
            font-family: 'DengXian', 'LiHei Pro', 'Microsoft YaHei', 'Microsoft JhengHei', '微軟正黑體';
        }
    </style>
    <body>
        <p>-- 排程【${jobName}】 --</p>
        於${.now?string("yyyy-MM-dd HH:mm:ss")}監測到來源<b><u>資料未異動</u></b><br />
        介接來源: <a href="${resourceUrl}">${resourceUrl}</a><br />
        經系統重新嘗試${processIndex}次後來源資料依舊未更新<br />
		敬請相關人員檢視介接<b><u>來源未異動</u></b>是否屬正常現象<br />
		來源未更新累積次數: ${errorAccumulation}<br />
    </body>
</html>