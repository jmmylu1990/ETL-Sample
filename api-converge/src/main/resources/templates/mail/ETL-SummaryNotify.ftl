<html>
    <style type="text/css">
        body {
            font-family: 'DengXian', 'LiHei Pro', 'Microsoft YaHei', 'Microsoft JhengHei', '微軟正黑體';
        }
        table {
            width: 100%;
            border-collapse: collapse;
        }
        td, th {
            border: 1px solid #ddd;
            padding: 5px;
        } 
        tr:nth-child(even) {
            background-color: #eee;
        }
        .fixed-header th {
            background: white;
            position: sticky;
            top: 0;
            box-shadow: 0 2px 2px -1px rgba(0, 0, 0, 0.4);
        }
        .default {
            color: #000;
        }
        .blue {
            font-weight: 700;
            color: #34989b;
        }
        .green {
            font-weight: 700;
            color: #1abb9c;
        }
        .red {
            font-weight: 700;
            color: #e74c3c;
        }
        .orange {
            font-weight: 700;
            color: #e49709;
        }
        .external-link {
            color: #606163;
            text-decoration: underline;
        }
    </style>
    <body>
        <b>*註1:</b> 實際成功次數為【執行成功】+【重做成功】。<br />
        <b>*註2:</b>【重做成功】表示第一次排程失敗後重新嘗試執行且成功。<br />
        <b>*註3:</b> 介接總次數公式為 【執行成功】+【重做成功】+【來源未更新】+【來源異常】+【匯入異常】。<br />
        <b>*註4:</b> 比率的計算公式為<b>【介接狀態次數】/【介接總次數】</b>。 Ex: 匯入異常率 = 【匯入異常】/【介接總次數】。
        <hr />
        <table>
            <thead class="fixed-header">
                <tr>
                    <th>排程名稱</th>
                    <th>執行成功</th>
                    <th>重做成功</th>
                    <th>來源未更新</th>
                    <th>來源異常</th>
                    <th>匯入異常</th>
                    <th>介接總次數</th>
                    <th>執行成功率</th>
                    <th>來源未更新率</th>
                    <th>來源異常率</th>
                    <th>匯入異常率</th>
                </tr>
            </thead>
            <tbody>
                <#list etlSummaryList as etlSummary>
                    <tr>
                        <td>
                            <a class="external-link" href="https://ticp.motc.gov.tw/motcManagement/harvestMonitor?setIds=${etlSummary.setId}">
                                ${etlSummary.jobName}
                            </a>
                        </td>
                        <td>${etlSummary.successCount}</td>
                        <td>${etlSummary.refireSuccessCount}</td>
                        <td>${etlSummary.srcNoUpdateCount}</td>
                        <td>${etlSummary.srcErrorCount}</td>
                        <td>${etlSummary.importFailedCount}</td>
                        <td>${etlSummary.totalCountPerJob}</td>
                        <!-- SuccessRate -->
                        <#if etlSummary.successRate == 100>
                            <td class="blue">${etlSummary.successRate}%</td>
                        <#else>
                            <td class="${(etlSummary.successRate < 80)?string('red', 'default')}">${etlSummary.successRate}%</td>
                        </#if>
                        <!-- SrcNoUpdateRate -->
                        <#if etlSummary.totalCountPerJob == 1 && etlSummary.srcNoUpdateRate == 100>
                            <td class="orange">${etlSummary.srcNoUpdateRate}%</td>
                        <#else>
                            <td class="${(etlSummary.srcNoUpdateRate > 80)?string('red', 'default')}">${etlSummary.srcNoUpdateRate}%</td>
                        </#if>
                        <!-- SrcErrorRate -->
                        <td class="${(etlSummary.srcErrorRate == 0)?string('default', 'red')}">
                            ${etlSummary.srcErrorRate}%
                        </td>
                        <!-- ImportFailedRate -->
                        <td class="${(etlSummary.importFailedRate == 0)?string('default', 'red')}">
                            ${etlSummary.importFailedRate}%
                        </td>
                    </tr>
                </#list>
            </tbody>
        </table>
    </body>
</html>