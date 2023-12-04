<html>
    <style type="text/css">
        body {
            font-family: 'DengXian', 'LiHei Pro', 'Microsoft YaHei', 'Microsoft JhengHei', '微軟正黑體';
        }
    </style>
    <body>
        <p>-- 排程【${jobName}】 --</p>
        於${.now?string("yyyy-MM-dd HH:mm:ss")}發生資料<b>匯入異常</b><br />
        介接來源:  <a href="${resourceUrl}">${resourceUrl}</a><br />
		敬請相關人員檢視來源資料<b><u>內容是否有誤</u></b>及<b><u>資料庫連線情況</u></b><br />
		匯入失敗累積次數: ${errorAccumulation}<br /><br />
        <b>錯誤訊息: </b><br />
        <div style="background: #f6f6f6; padding: 10px; border-radius: 3px;">${exception}</div><br />
    </body>
</html>