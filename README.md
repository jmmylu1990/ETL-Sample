ETLExecutor: The ETL framework with quarz
===================================
## **前言**
---------------

> ETL，是英文 Extract-Transform-Load 的縮寫，用來描述將資料從來源端經過萃取 (`extract`)、轉置 (`transform`)、載入 (`load`) 至目的端的過程。

以上內容節錄自 [Wikipedia](https://zh.wikipedia.org/wiki/ETL "ETL Introduction")，由於在資料介接上需要考慮的議題往往不單只有執行結果，還需額外考量例外處理、記錄、通報等後處理，故本模組基於 Spring Boot 和 Quartz 為底進行相關介面的實作，旨在讓開法人員可只專注於 ETL 邏輯上的實現。

## **執行架構**
---------------

<p align="center">
  <img src="https://upload.cc/i1/2020/07/08/Hx6RPY.png" />
</p>

## **核心組件**
---------------

- `api-converge` - ETL 核心專案
  - `com.example.controller.ScheduleController` - 排程控制接口
  - `com.example.listener.RetryableJobListener` - 容錯排程監聽器
  - `com.example.job.ETLJobExecutor` - ETL 核心調度邏輯
  - `com.example.component.ApiRewriteComponent` - API 源改寫組件 (配合 `api-proxy` 專案)
  - `com.example.strategy.ETLStrategy` - ETL 執行策略 (介面)
  - `com.example.strategy.FileImportStrategy` - 檔案匯入執行策略 (介面)
  - `com.example.service.impl.MailServiceImpl` - 信件服務實作
  - `com.example.service.impl.NotifyServiceImpl` - 預設 ETL 信件通報
- `api-proxy` - 介接源 Proxy 專案
  - `com.example.controller.GenericApiProxyController` - 通用性 API Proxy 接口
  - `com.example.controller.TdcsFileProxyController` - TDCS 檔案 API Proxy 接口
- `api-utils` - ETL 常用 Utils 類
    - `com.example.utils.ClassUtils` - 類別處理工具
    - `com.example.utils.CMDUtils` - 指令調用工具
    - `com.example.utils.CryptUtils` - 加解密處理工具
    - `com.example.utils.DateUtils` - 日期時間處理工具
    - `com.example.utils.DownloadHelper` - 下載輔助工具
    - `com.example.utils.FileOperationUtils` - 檔案類處理工具
    - `com.example.utils.GPSHelper` - 地理資訊處理工具
    - `com.example.utils.GzUtils` - Gz 格式壓縮檔處理工具
    - `com.example.utils.HttpUtils` - Http 處理工具
    - `com.example.utils.JsonUtils` - JSON 處理工具
    - `com.example.utils.SqlUtils` - SQL 處理工具
    - `com.example.utils.StringTools` - 字串處理工具
    - `com.example.utils.XmlUtils` - XML 處理工具
    - `com.example.utils.ZipUtils` - Zip 格式壓縮檔處理工具

## **使用說明**
---------------

### **Git repository**

```bash
# 移動當前目錄至開發用的工作目錄
cd <YOUR_WORKSPACE>

# 複製到本機目錄
git clone http://scm.example.com/scm/svn/TDD52/git/JobManagement_GIT
```

### **Quartz cluster requirement**
該架構採用 Quazrtz Cluster 作為排程調度的解決方案，由於 Quartz Cluster 的實現上是基於關聯性資料庫來實現鎖的機制，故專案啟動前務必先確保所需**資料表都已建立** (建立 SQL 放置位置為`src\main\resources\quartz`)
- MySQL / Mariadb - `src\main\resources\quartz\mysql`
- SQL Server - `src\main\resources\quartz\mssql`
- Oracle - `src\main\resources\quartz\oracle`
- PostgreSQL - `src\main\resources\quartz\postgres`

### **Customize ScheduleJob & ScheduleLog**
- 自訂 `ScheduleJob` 資料模型
```java
/**
* 繼承 AbstractScheduleJob 並添加額外所需自訂欄位
*/
@Entity
@DynamicUpdate
@Table(name = "schedule_job")
@EqualsAndHashCode(callSuper = false)
public @Data class ScheduleJob extends AbstractScheduleJob {
    
    private static final long serialVersionUID = 1L;

    // ... other fields
}
```
- 自訂 `ScheduleLog` 資料模型
```java
/**
* 繼承 AbstractScheduleLog 並添加額外所需自訂欄位
*/
@Entity
@Table(name = "schedule_log")
@EqualsAndHashCode(callSuper = false)
public @Data class ScheduleLog extends AbstractScheduleLog {
    
    private static final long serialVersionUID = 1L;

    // ... other fields
}
```

### **ETLStrategy Implementation**
該專案已預先實現了 `GeneralApiETLStrategy` 一般情況可直接作為預設 `jobStrategy`，當碰到不適用的情境時，此時可自訂類別且繼承 `GeneralApiETLStrategy` 並抽換其需調整的方法實作 (`extract` 或 `transform` 或 `load`)，亦或直接實作 `ETLStrategy` 自行實現 ETL 的所有方法

- 繼承 `GeneralApiETLStrategy` 類別 - 抽換 `extract` 方法實作示意
```java
@Slf4j
@Component
public class PtxApiETLStrategy extends GeneralApiETLStrategy  {

    @Value("${ptx.api.app-id}")
    private String appId;
    @Value("${ptx.api.app-key}")
    private String appKey;
    
    /**
     * 由於 PTX API 需要提供 AppID 與 AppKey 才可取得資源，故此處覆寫 extract 方法
     * */
    @Override
    public ExtractResult extract(ResourceInfo resourceInfo) throws ResourceException {
        try {
            // Step 1: Check resource available or not
            Date updateTime = new Date();
            String resource = resourceInfo.getResource();
            Map<String, String> headers = HttpUtils.customHeaderForPTX(appId, appKey);
            log.info("Resource: `{}`", resource);

            // Step 2: Extract the array part of resource
            String resourceContent = JsonUtils.toJsonString(resource, headers);
            
            // ... your code
                
        } catch (Exception e) {
            throw new ResourceException(e);
        }
    }
}
```

- 實作 `ETLStrategy` 介面 - 實作所有方法示意
```java
/**
*   實作 ETLStrategy 介面，複寫 extract, transform, load 三個定義方法
*/
@Slf4j
@Component
public class CustomApiETLStrategy implements ETLStrategy {
    
    /**
    *   來源資料萃取邏輯
    */
    @Override
    public ExtractResult extract(ResourceInfo resourceInfo) throws ResourceException {
        return null;
    }

    /**
    *   來源萃取資料轉換邏輯
    */
    @Override
    public TransformResult transform(ResourceInfo resourceInfo, ExtractResult extractResult) throws ResourceFormatErrorException {
        return null;
    }

    /**
    *   轉換結果匯入 (儲存)邏輯
    */
    @Override
    public Map<DbSourceEnum, ImportResult> load(TransformResult transformResult, boolean clearFirst, DbSourceEnum... dbSourceEnums) throws ImportException {
        return null;
    }
}
```

### **FileImportStrategy Implementation**
該專案已針對以下三種 RDBMS 實作檔案格式的大量匯入機制，一般情況直接調用即可
- MySQL / Mariadb - `LOAD DATA INFILE` ([官方文件](https://dev.mysql.com/doc/refman/8.0/en/load-data.html))
- SQL Server - `BULK INSERT` ([官方文件](https://docs.microsoft.com/zh-tw/sql/t-sql/statements/bulk-insert-transact-sql?view=sql-server-ver15))
- Oracle - `SQL*Loader Control File` ([官方文件](https://docs.oracle.com/cd/E11882_01/server.112/e22490/ldr_control_file.htm))


## **相關知識**
--------------------
- Spring Boot 2.x.x
- Spring Data JPA
- Quartz 2