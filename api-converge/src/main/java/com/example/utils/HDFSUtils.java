package com.example.utils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import lombok.extern.slf4j.Slf4j;

/**
 * 專案名稱: api-converge<br>
 * 建立時間: 上午10:30:40<br>
 *
 * @author Stanley
 * @version 1.0
 *
 */
@Slf4j
public class HDFSUtils {

	private static final String LISTSTATUS_URI_PATTERN = "http://%s:%s/webhdfs/v1%s?op=LISTSTATUS&user.name=%s";
	private static final String DELETE_URI_PATTERN = "http://%s:%s/webhdfs/v1%s?&user.name=%s&op=DELETE&recursive=true";
	private static final String RENAME_URI_PATTERN = "http://%s:%s/webhdfs/v1%s?user.name=%s&op=RENAME&destination=%s";
	private static final String MKDIRS_URI_PATTERN = "http://%s:%s/webhdfs/v1%s?user.name=%s&op=MKDIRS";
	private static final String CREATE_URI_PATTERN = "http://%s:%s/webhdfs/v1%s?user.name=%s&op=CREATE&overwrite=true";
	private static final String APPEND_URI_PATTERN = "http://%s:%s/webhdfs/v1%s?op=APPEND&user.name=%s";

	private static Properties config;
	private static String masterHost;
	private static String masterPort;
	private static String slaveHost;
	private static String slavePort;
	private static String authUser;

	private HDFSUtils() {
	}

	static {
		setConfig(FileOperationUtils.getProperties("/webhdfs.properties"));
	}

	public static void setConfig(Properties properties) {
		config = properties;
		masterHost = properties.getProperty("web-hdfs.master.host");
		slaveHost = properties.getProperty("web-hdfs.slave.host");
		masterPort = properties.getProperty("web-hdfs.master.port");
		slavePort = properties.getProperty("web-hdfs.slave.port");
		authUser = properties.getProperty("web-hdfs.user");
	}
	
	public static Properties getConfig() {
		return config;
	}

	private static boolean swap() {
		String tempHost = null;
		String tempPort = null;
		// Change host
		tempHost = masterHost;
		masterHost = slaveHost;
		slaveHost = tempHost;
		// Change port
		tempPort = masterPort;
		masterPort = slavePort;
		slavePort = tempPort;

		return Objects.nonNull(tempHost) && Objects.nonNull(tempPort);
	}

	/**
	 * [方法多載]<br>
	 * 提供預設impala參數設定, 無須額外傳入<br>
	 * 透過WebHDFS的RestAPI檢索指定路徑下是否有包含檔案
	 *
	 * @param dirPath 欲檢索HDFS路徑, String型別
	 * @param retry   是否自動嘗試切換主從節點, boolean型別
	 * 
	 * @return 是否包含檔案, boolean 型別
	 * @throws Exception
	 */
	public static boolean isEmpty(String dirPath, boolean retry) throws Exception {
		return isEmpty(masterHost, masterPort, authUser, dirPath, retry);
	}

	/**
	 * [方法多載]<br>
	 * 支援預設主從切換; 提供預設impala參數設定, 無須額外傳入<br>
	 * 透過WebHDFS的RestAPI檢索指定路徑下是否有包含檔案
	 *
	 * @param dirPath 欲檢索HDFS路徑, String型別
	 * 
	 * @return 是否包含檔案, boolean 型別
	 * @throws Exception
	 */
	public static boolean isEmpty(String dirPath) throws Exception {
		return isEmpty(dirPath, true);
	}

	/**
	 * 透過WebHDFS的RestAPI檢索指定路徑下是否有包含檔案
	 *
	 * @param host    HDFS的host位置, String型別
	 * @param port    port位置, String型別
	 * @param user    使用者名稱, String型別
	 * @param dirPath 欲檢索HDFS路徑, String型別
	 * @param retry   是否自動嘗試切換主從節點, boolean型別
	 * 
	 * @return 是否包含檔案, boolean 型別
	 */
	public static boolean isEmpty(String host, String port, String user, String dirPath, boolean retry)
			throws Exception {
		String url = String.format(LISTSTATUS_URI_PATTERN, host, port, dirPath, user);
		try {
			String jsonResponse = JsonUtils.toJsonString(url);
			jsonResponse = jsonResponse.substring(jsonResponse.indexOf('['), jsonResponse.lastIndexOf(']') + 1);
			List<FileStatus> fileStatusList = JsonUtils.toBeanList(jsonResponse, FileStatus.class);

			return fileStatusList.isEmpty();
		} catch (Exception e) {
			// Retry with switching the master/slave
			if (retry)
				return swap() && isEmpty(dirPath, false);

			log.error(e.getMessage(), e);
			throw e;
		}
	}

	/**
	 * 透過WebHDFS的RestAPI檢索指定路徑下是否有存在指定檔案
	 *
	 * @param dirPath  欲檢索HDFS路徑, String型別
	 * @param fileName 欲判斷是否存在的檔案名稱
	 * @param retry    是否自動嘗試切換主從節點, boolean型別
	 * 
	 * @return 指定檔案是否存在, boolean 型別
	 */
	public static boolean exists(String dirPath, String fileName, boolean retry) {
		return exists(masterHost, masterPort, authUser, dirPath, fileName, retry);
	}

	/**
	 * 透過WebHDFS的RestAPI檢索指定路徑下是否有存在指定檔案
	 *
	 * @param dirPath  欲檢索HDFS路徑, String型別
	 * @param fileName 欲判斷是否存在的檔案名稱
	 * 
	 * @return 指定檔案是否存在, boolean 型別
	 */
	public static boolean exists(String dirPath, String fileName) {
		return exists(masterHost, masterPort, authUser, dirPath, fileName, true);
	}

	/**
	 * 透過WebHDFS的RestAPI檢索指定路徑下是否有存在指定檔案
	 *
	 * @param host     HDFS的host位置, String型別
	 * @param port     port位置, String型別
	 * @param user     使用者名稱, String型別
	 * @param dirPath  欲檢索HDFS路徑, String型別
	 * @param fileName 欲判斷是否存在的檔案名稱
	 * @param retry    是否自動嘗試切換主從節點, boolean型別
	 * 
	 * @return 指定檔案是否存在, boolean 型別
	 */
	public static boolean exists(String host, String port, String user, String dirPath, String fileName,
			boolean retry) {
		String url = String.format(LISTSTATUS_URI_PATTERN, host, port, dirPath, user);
		try {
			String jsonResponse = JsonUtils.toJsonString(url);
			jsonResponse = jsonResponse.substring(jsonResponse.indexOf('['), jsonResponse.lastIndexOf(']') + 1);
			List<FileStatus> fileStatusList = JsonUtils.toBeanList(jsonResponse, FileStatus.class);

			return fileStatusList.parallelStream().map(FileStatus::getPathSuffix).anyMatch(fileName::equals);
		} catch (FileNotFoundException e) {
			return false;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return retry && swap() && exists(dirPath, fileName, false);
		}
	}

	/**
	 * [方法多載]<br>
	 * 提供預設impala參數設定, 無須額外傳入; 透過WebHDFS的RestAPI列出指定路徑下的所有檔案路徑
	 *
	 * @param dirPath 需要檢索的路徑, String型別
	 * @param retry   是否自動嘗試切換主從節點, boolean型別
	 * 
	 * @return HDFS指定路徑下的檔案路徑字串list, List型別
	 */
	public static List<String> listFiles(String dirPath, boolean retry) {
		return listFiles(masterHost, masterPort, authUser, dirPath, retry);
	}

	/**
	 * [方法多載]<br>
	 * 提供預設impala參數設定, 無須額外傳入; 透過WebHDFS的RestAPI列出指定路徑下的所有檔案路徑
	 *
	 * @param dirPath 需要檢索的路徑, String型別
	 * @param retry   是否自動嘗試切換主從節點, boolean型別
	 * 
	 * @return HDFS指定路徑下的檔案路徑字串list, List型別
	 */
	public static List<String> listFiles(String dirPath) {
		return listFiles(masterHost, masterPort, authUser, dirPath, true);
	}

	/**
	 * 透過WebHDFS的RestAPI列出指定路徑下的所有檔案路徑
	 *
	 * @param host    HDFS的host位置, String型別
	 * @param port    port位置, String型別
	 * @param user    使用者名稱, String型別
	 * @param dirPath 需要檢索的路徑, String型別
	 * @param retry   是否自動嘗試切換主從節點, boolean型別
	 * 
	 * @return HDFS指定路徑下的檔案路徑字串list, List型別
	 */
	public static List<String> listFiles(String host, String port, String user, String dirPath, boolean retry) {
		try {
			String url = String.format(LISTSTATUS_URI_PATTERN, host, port, dirPath, user);
			String jsonResponse = JsonUtils.toJsonString(url);
			jsonResponse = jsonResponse.substring(jsonResponse.indexOf('['), jsonResponse.lastIndexOf(']') + 1);
			List<FileStatus> fileStatusList = JsonUtils.toBeanList(jsonResponse, FileStatus.class);

			return fileStatusList.stream().filter(f -> f.getType().equalsIgnoreCase("FILE"))
					.map(f -> String.format("%s/%s", dirPath, f.getPathSuffix())).collect(Collectors.toList());
		} catch (Exception e) {
			if (retry && swap())
				return listFiles(dirPath, false);

			log.error(e.getMessage(), e);
			return new ArrayList<>();
		}
	}
	
	/**
	 * 透過WebHDFS的RestAPI列出指定路徑下的所有檔案路徑
	 *
	 * @param host    HDFS的host位置, String型別
	 * @param port    port位置, String型別
	 * @param user    使用者名稱, String型別
	 * @param dirPath 需要檢索的路徑, String型別
	 * 
	 * @return HDFS指定路徑下的檔案路徑字串list, List型別
	 */
	public static List<String> listFiles(String host, String port, String user, String dirPath) {
		return listFiles(host, port, user, dirPath, true); 
	}

	/**
	 * 透過WebHDFS的RestAPI刪除指定資料夾或檔案
	 *
	 * @param host  HDFS的host位置, String型別
	 * @param port  port位置, String型別
	 * @param user  使用者名稱, String型別
	 * @param path  欲刪除的檔案路徑, String型別
	 * @param retry 是否自動嘗試切換主從節點, boolean型別
	 * 
	 * @return 是刪除成功, boolean型別
	 */
	public static boolean remove(String host, String port, String user, String path, boolean retry) {
		String url = String.format(DELETE_URI_PATTERN, host, port, path, user);
		try {
			String jsonResponse = JsonUtils.toJsonString(url, HttpMethod.DELETE.name(), new HashMap<>());
			return Boolean.valueOf(jsonResponse.substring(jsonResponse.indexOf(':') + 1, jsonResponse.length() - 1));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return retry && swap() && remove(path, false);
		}
	}

	/**
	 * [方法多載]<br>
	 * 提供預設impala參數設定, 無須額外傳入; 透過WebHDFS的RestAPI刪除指定資料夾或檔案
	 *
	 * @param path  欲刪除的檔案路徑, String型別
	 * @param retry 是否自動嘗試切換主從節點, boolean型別
	 * 
	 * @return 是刪除成功, boolean型別
	 */
	public static boolean remove(String path, boolean retry) {
		return remove(masterHost, masterPort, authUser, path, retry);
	}

	/**
	 * [方法多載]<br>
	 * 提供預設impala參數設定, 無須額外傳入; 透過WebHDFS的RestAPI刪除指定資料夾或檔案
	 *
	 * @param path 欲刪除的檔案路徑, String型別
	 * 
	 * @return 是刪除成功, boolean型別
	 */
	public static boolean remove(String path) {
		return remove(masterHost, masterPort, authUser, path, true);
	}

	/**
	 * [方法多載]<br>
	 * 提供預設impala參數設定, 無須額外傳入; 透過WebHDFS的RestAPI變更指定路徑的檔案名稱(可做檔案搬移)
	 *
	 * @param orignPath 需要搬移的檔案路徑, String型別
	 * @param destPath  搬移至的路徑, String型別
	 * @param retry     是否自動嘗試切換主從節點, boolean型別
	 * 
	 * @return 是否搬移成功, boolean型別
	 */
	public static boolean rename(String orignPath, String destPath, boolean retry) {
		return rename(masterHost, masterPort, authUser, orignPath, destPath, retry);
	}

	/**
	 * [方法多載]<br>
	 * 提供預設impala參數設定, 無須額外傳入; 透過WebHDFS的RestAPI變更指定路徑的檔案名稱(可做檔案搬移)
	 *
	 * @param orignPath 需要搬移的檔案路徑, String型別
	 * @param destPath  搬移至的路徑, String型別
	 * 
	 * @return 是否搬移成功, boolean型別
	 */
	public static boolean rename(String orignPath, String destPath) {
		return rename(masterHost, masterPort, authUser, orignPath, destPath, true);
	}

	/**
	 * 透過WebHDFS的RestAPI變更指定路徑的檔案名稱(可做檔案搬移)
	 *
	 * @param host      HDFS的host位置, String型別
	 * @param port      port位置, String型別
	 * @param user      使用者名稱, String型別
	 * @param orignPath 需要搬移的檔案路徑, String型別
	 * @param destPath  搬移至的路徑, String型別
	 * @param retry     是否自動嘗試切換主從節點, boolean型別
	 * 
	 * @return 是否搬移成功, boolean型別
	 */
	public static boolean rename(String host, String port, String user, String orignPath, String destPath,
			boolean retry) {
		String url = String.format(RENAME_URI_PATTERN, host, port, orignPath, user, destPath);
		try {
			String jsonResponse = JsonUtils.toJsonString(url, HttpMethod.PUT.name(), new HashMap<>());

			return Boolean.valueOf(jsonResponse.substring(jsonResponse.indexOf(':') + 1, jsonResponse.length() - 1));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return retry && swap() && rename(orignPath, destPath, false);
		}
	}

	/**
	 * * [方法多載]<br>
	 * 提供預設impala參數設定, 無須額外傳入; 透過WebHDFS的RestAPI創建資料目錄結構(可巢狀)
	 *
	 * @param dirs  需要創建的目錄結構, String型別
	 * @param retry 是否自動嘗試切換主從節點, boolean型別
	 * 
	 * @return 是否搬移成功, boolean型別
	 */
	public static boolean mkdirs(String dirs, boolean retry) {
		return mkdirs(masterHost, masterPort, authUser, dirs, retry);
	}

	/**
	 * * [方法多載]<br>
	 * 提供預設impala參數設定, 無須額外傳入; 透過WebHDFS的RestAPI創建資料目錄結構(可巢狀)
	 *
	 * @param dirs 需要創建的目錄結構, String型別
	 * 
	 * @return 是否搬移成功, boolean型別
	 */
	public static boolean mkdirs(String dirs) {
		return mkdirs(masterHost, masterPort, authUser, dirs, true);
	}

	/**
	 * 透過WebHDFS的RestAPI創建資料目錄結構(可巢狀)
	 *
	 * @param host  HDFS的host位置, String型別
	 * @param port  port位置, String型別
	 * @param user  使用者名稱, String型別
	 * @param dirs  需要創建的目錄結構, String型別
	 * @param retry 是否自動嘗試切換主從節點, boolean型別
	 * 
	 * @return 是否搬移成功, boolean型別
	 */
	public static boolean mkdirs(String host, String port, String user, String dirs, boolean retry) {
		String url = String.format(MKDIRS_URI_PATTERN, host, port, dirs, user);
		try {
			String jsonResponse = JsonUtils.toJsonString(url, HttpMethod.PUT.name(), new HashMap<>());
			return Boolean.valueOf(jsonResponse.substring(jsonResponse.indexOf(':') + 1, jsonResponse.length() - 1));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return retry && swap() && mkdirs(dirs, false);
		}
	}

	/**
	 * [方法多載]<br>
	 * 提供預設impala參數設定, 無須額外傳入; 透過WebHDFS的RestAPI將指定的本機檔案推送至HDFS端的指定位置
	 *
	 * @param uploadFile 需要上傳的的檔案完整路徑, String型別
	 * @param path       推送至HDFS上的路徑, String型別
	 * @param retry      是否自動嘗試切換主從節點, boolean型別
	 * 
	 * @return 上傳至HDFS上的檔案路徑, String型別
	 * @throws IOException
	 */
	public static String upload(String uploadFile, String path, boolean retry) throws IOException {
		return upload(masterHost, masterPort, authUser, uploadFile, path, retry);
	}

	/**
	 * [方法多載]<br>
	 * 提供預設impala參數設定, 無須額外傳入; 透過WebHDFS的RestAPI將指定的本機檔案推送至HDFS端的指定位置
	 *
	 * @param uploadFile 需要上傳的的檔案完整路徑, String型別
	 * @param path       推送至HDFS上的路徑, String型別
	 * 
	 * @return 上傳至HDFS上的檔案路徑, String型別
	 * @throws IOException
	 */
	public static String upload(String uploadFile, String path) throws IOException {
		return upload(masterHost, masterPort, authUser, uploadFile, path, false);
	}

	/**
	 * [方法多載]<br>
	 * 提供預設impala參數設定, 無須額外傳入且支援傳入檔案型別物件; 透過WebHDFS的RestAPI將指定的本機檔案推送至HDFS端的指定位置
	 *
	 * @param uploadFile 需要上傳的的檔案完整路徑, File型別
	 * @param path       推送至HDFS上的路徑, String型別
	 * @param retry      是否自動嘗試切換主從節點, boolean型別
	 * 
	 * @return 上傳至HDFS上的檔案路徑, String型別
	 * @throws IOException
	 */
	public static String upload(File uploadFile, String path, boolean retry) throws IOException {
		return upload(masterHost, masterPort, authUser, uploadFile.getAbsolutePath(), path, retry);
	}

	/**
	 * [方法多載]<br>
	 * 提供預設impala參數設定, 無須額外傳入且支援傳入檔案型別物件; 透過WebHDFS的RestAPI將指定的本機檔案推送至HDFS端的指定位置
	 *
	 * @param uploadFile 需要上傳的的檔案完整路徑, File型別
	 * @param path       推送至HDFS上的路徑, String型別
	 * 
	 * @return 上傳至HDFS上的檔案路徑, String型別
	 * @throws IOException
	 */
	public static String upload(File uploadFile, String path) throws IOException {
		return upload(masterHost, masterPort, authUser, uploadFile.getAbsolutePath(), path, false);
	}

	/**
	 * 透過WebHDFS的RestAPI將指定的本機檔案推送至HDFS端的指定位置
	 *
	 * @param host       HDFS的host位置, String型別
	 * @param port       port位置, String型別
	 * @param user       使用者名稱, String型別
	 * @param uploadFile 需要上傳的的檔案完整路徑, String型別
	 * @param path       推送至HDFS上的路徑, String型別
	 * @param retry      是否自動嘗試切換主從節點, boolean型別
	 * 
	 * @return 上傳至HDFS上的檔案路徑, String型別
	 * @throws IOException
	 */
	public static String upload(String host, String port, String user, String uploadFile, String path, boolean retry)
			throws IOException {
		String fileName = null;
		if (uploadFile.contains(File.separator)) {
			fileName = uploadFile.substring(uploadFile.lastIndexOf(File.separator) + 1);
		} else {
			fileName = uploadFile.substring(uploadFile.lastIndexOf('/') + 1);
		}

		return upload(host, port, user, uploadFile, path, fileName, retry);
	}

	/**
	 * [方法多載]<br>
	 * 提供預設impala參數設定, 無須額外傳入並支援上傳後的檔案重新賦予檔名。<br>
	 * 透過WebHDFS的RestAPI將指定的本機檔案推送至HDFS端的指定位置
	 *
	 * @param uploadFile 需要上傳的的檔案完整路徑, String型別
	 * @param path       推送至HDFS上的路徑, String型別
	 * @param fileName   指定推送至HDFS上的檔名, String型別
	 * @param retry      是否自動嘗試切換主從節點, boolean型別
	 * 
	 * @return 上傳至HDFS上的檔案路徑, String型別
	 * @throws IOException
	 */
	public static String upload(String uploadFile, String path, String fileName, boolean retry) throws IOException {
		return upload(masterHost, masterPort, authUser, uploadFile, path, fileName, retry);
	}

	/**
	 * [方法多載]<br>
	 * 提供預設impala參數設定, 無須額外傳入並支援上傳後的檔案重新賦予檔名。<br>
	 * 透過WebHDFS的RestAPI將指定的本機檔案推送至HDFS端的指定位置
	 *
	 * @param uploadFile 需要上傳的的檔案完整路徑, String型別
	 * @param path       推送至HDFS上的路徑, String型別
	 * @param fileName   指定推送至HDFS上的檔名, String型別
	 * 
	 * @return 上傳至HDFS上的檔案路徑, String型別
	 * @throws IOException
	 */
	public static String upload(String uploadFile, String path, String fileName) throws IOException {
		return upload(masterHost, masterPort, authUser, uploadFile, path, fileName, true);
	}

	/**
	 * [方法多載]<br>
	 * 提供預設impala參數設定, 無須額外傳入並支援上傳後的檔案重新賦予檔名。<br>
	 * 透過WebHDFS的RestAPI將指定的本機檔案推送至HDFS端的指定位置
	 *
	 * @param uploadFile 需要上傳的的檔案完整路徑, File型別
	 * @param path       推送至HDFS上的路徑, String型別
	 * @param fileName   指定推送至HDFS上的檔名, String型別
	 * @param retry      是否自動嘗試切換主從節點, boolean型別
	 * 
	 * @return 上傳至HDFS上的檔案路徑, String型別
	 * @throws IOException
	 */
	public static String upload(File uploadFile, String path, String fileName, boolean retry) throws IOException {
		return upload(masterHost, masterPort, authUser, uploadFile.getAbsolutePath(), path, fileName, retry);
	}

	/**
	 * [方法多載]<br>
	 * 提供預設impala參數設定, 無須額外傳入並支援上傳後的檔案重新賦予檔名。<br>
	 * 透過WebHDFS的RestAPI將指定的本機檔案推送至HDFS端的指定位置
	 *
	 * @param uploadFile 需要上傳的的檔案完整路徑, File型別
	 * @param path       推送至HDFS上的路徑, String型別
	 * @param fileName   指定推送至HDFS上的檔名, String型別
	 * 
	 * @return 上傳至HDFS上的檔案路徑, String型別
	 * @throws IOException
	 */
	public static String upload(File uploadFile, String path, String fileName) throws IOException {
		return upload(masterHost, masterPort, authUser, uploadFile.getAbsolutePath(), path, fileName, true);
	}

	/**
	 * [方法多載]<br>
	 * 支援上傳後的檔案重新賦予檔名; 透過WebHDFS的RestAPI將指定的本機檔案推送至HDFS端的指定位置
	 *
	 * @param host       HDFS的host位置, String型別
	 * @param port       port位置, String型別
	 * @param user       使用者名稱, String型別
	 * @param uploadFile 需要上傳的的檔案完整路徑, String型別
	 * @param path       推送至HDFS上的路徑, String型別
	 * @param fileName   指定推送至HDFS上的檔名, String型別
	 * @param retry      是否自動嘗試切換主從節點, boolean型別
	 * 
	 * @return 上傳至HDFS上的檔案路徑, String型別
	 * @throws IOException
	 */
	public static String upload(String host, String port, String user, String uploadFile, String path, String fileName,
			boolean retry) throws IOException {
		HttpURLConnection conn = null;
		InputStream in = null;
		DataOutputStream out = null;
		try {
			String file = path.replaceAll("/$", "") + "/" + fileName;
			String url = String.format(CREATE_URI_PATTERN, host, port, file, user);
			// Step 1: Submit a HTTP PUT request without automatically following
			// redirects and without sending the file data.
			String redirectUrl = null;
			conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setRequestMethod(HttpMethod.PUT.name());
			conn.setInstanceFollowRedirects(false);
			conn.connect();
			int statusCode = conn.getResponseCode();
			if (statusCode == 307) {
				redirectUrl = conn.getHeaderField(HttpHeaders.LOCATION);
			} else if (statusCode == 403 && retry) {
				conn.disconnect();
				swap();

				return upload(uploadFile, path, fileName, false);
			}
			conn.disconnect();
			// Step 2: Submit another HTTP PUT request using the URL in the
			// Location header with the file data to be written.
			if (redirectUrl != null) {
				conn = (HttpURLConnection) new URL(redirectUrl).openConnection();
				conn.setDoInput(true);
				conn.setDoOutput(true);
				conn.setUseCaches(false);
				conn.setRequestMethod(HttpMethod.PUT.name());
				conn.setRequestProperty(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
				in = new FileInputStream(new File(uploadFile));
				final int SIZE = in.available();
				conn.setRequestProperty(HttpHeaders.CONTENT_LENGTH, String.valueOf(SIZE));
				conn.setFixedLengthStreamingMode(SIZE);
				// conn.setChunkedStreamingMode(0);
				conn.connect();
				out = new DataOutputStream(conn.getOutputStream());
				IOUtils.copy(in, out, 12288);
				// FileOperationUtils.transfer(in, out, SIZE);
				out.flush();
				Thread.sleep(500);

				return file;
			}
		} catch (IOException e) {
			if (retry) {
				swap();
				return upload(uploadFile, path, fileName, false);
			}
			log.error(e.getMessage(), e);
			throw e;
		} catch (InterruptedException e) {
			log.error(e.getMessage(), e);
			Thread.currentThread().interrupt();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
			}
			if (conn != null)
				conn.disconnect();
		}

		return null;
	}

	/**
	 * [方法多載]<br>
	 * 提供預設impala參數設定, 無須額外傳入;支援上傳後的檔案重新賦予檔名。<br>
	 * 透過WebHDFS的RestAPI將指定的本機檔案串流推送至HDFS端的指定位置
	 *
	 * @param in       檔案串流, InputStream型別
	 * @param path     指定推送至HDFS上的位置, String型別
	 * @param fileName 指定推送至HDFS上的檔名, String型別
	 * @param retry    是否自動嘗試切換主從節點, boolean型別
	 * 
	 * @return 上傳至HDFS上的檔案路徑, String型別
	 */
	public static String upload(InputStream in, String path, String fileName, boolean retry) {
		return upload(masterHost, masterPort, authUser, in, path, fileName, retry);
	}

	/**
	 * [方法多載]<br>
	 * 提供預設impala參數設定, 無須額外傳入;支援上傳後的檔案重新賦予檔名。<br>
	 * 透過WebHDFS的RestAPI將指定的本機檔案串流推送至HDFS端的指定位置
	 *
	 * @param in       檔案串流, InputStream型別
	 * @param path     指定推送至HDFS上的位置, String型別
	 * @param fileName 指定推送至HDFS上的檔名, String型別
	 * 
	 * @return 上傳至HDFS上的檔案路徑, String型別
	 */
	public static String upload(InputStream in, String path, String fileName) {
		return upload(masterHost, masterPort, authUser, in, path, fileName, true);
	}

	/**
	 * [方法多載]<br>
	 * 支援上傳後的檔案重新賦予檔名; 透過WebHDFS的RestAPI將指定的本機檔案串流推送至HDFS端的指定位置
	 *
	 * @param host       HDFS的host位置, String型別
	 * @param port       port位置, String型別
	 * @param user       使用者名稱, String型別
	 * @param uploadFile 需要上傳的的檔案完整路徑, String型別
	 * @param in         檔案串流, InputStream型別
	 * @param fileName   指定推送至HDFS上的檔名, String型別
	 * @param retry      是否自動嘗試切換主從節點, boolean型別
	 * 
	 * @return 上傳至HDFS上的檔案路徑, String型別
	 */
	public static String upload(String host, String port, String user, InputStream in, String path, String fileName,
			boolean retry) {
		HttpURLConnection conn = null;
		DataOutputStream out = null;
		try {
			String file = path.replaceAll("/$", "") + "/" + fileName;
			String url = String.format(CREATE_URI_PATTERN, host, port, file, user);
			// Step 1: Submit a HTTP PUT request without automatically following
			// redirects and without sending the file data.
			String redirectUrl = null;
			conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setRequestMethod(HttpMethod.GET.name());
			conn.setInstanceFollowRedirects(false);
			conn.connect();
			int responseCode = conn.getResponseCode();
			if (responseCode == 307) {
				redirectUrl = conn.getHeaderField(HttpHeaders.LOCATION);
			} else if (responseCode == 403 && retry) {
				conn.disconnect();
				swap();
				return upload(in, path, fileName, false);
			}
			conn.disconnect();
			// Step 2: Submit another HTTP PUT request using the URL in the
			// Location header with the file data to be written.
			if (redirectUrl != null) {
				conn = (HttpURLConnection) new URL(redirectUrl).openConnection();
				conn.setDoInput(true);
				conn.setDoOutput(true);
				conn.setUseCaches(false);
				conn.setRequestMethod(HttpMethod.PUT.name());
				conn.setRequestProperty(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
				final int SIZE = in.available();
				conn.setRequestProperty(HttpHeaders.CONTENT_LENGTH, String.valueOf(SIZE));
				conn.setFixedLengthStreamingMode(SIZE);
				conn.connect();
				out = new DataOutputStream(conn.getOutputStream());
				IOUtils.copy(in, out, 12288);
				out.flush();
				Thread.sleep(500);

				return file;
			}
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			swap();
			return upload(in, path, fileName, false);
		} catch (InterruptedException e) {
			log.error(e.getMessage(), e);
			Thread.currentThread().interrupt();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
			}
			if (conn != null)
				conn.disconnect();
		}

		return null;
	}

	/**
	 * 透過WebHDFS的RestAPI將指定的本機檔案添加至HDFS端的指定檔案內
	 *
	 * @param host  HDFS的host位置, String型別
	 * @param port  port位置, String型別
	 * @param user  使用者名稱, String型別
	 * @param path  HDFS上檔案位置, String型別
	 * @param file  欲添加檔案, File型別
	 * @param retry 是否自動嘗試切換主從節點, boolean型別
	 * 
	 * @return 判斷添加內容至HDFS上的檔案結果是否成功, boolean型別
	 */
	public static boolean append(String host, String port, String user, String path, File file, boolean retry) {
		try {
			return append(host, port, user, path, new FileInputStream(file), retry);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		return false;
	}
	
	/**
	 * 透過WebHDFS的RestAPI將指定的本機檔案添加至HDFS端的指定檔案內
	 *
	 * @param host  HDFS的host位置, String型別
	 * @param port  port位置, String型別
	 * @param user  使用者名稱, String型別
	 * @param path  HDFS上檔案位置, String型別
	 * @param file  欲添加檔案, File型別
	 * 
	 * @return 判斷添加內容至HDFS上的檔案結果是否成功, boolean型別
	 */
	public static boolean append(String host, String port, String user, String path, File file) {
		return append(host, port, user, path, file, true);
	}

	/**
	 * [方法多載]<br>
	 * 透過WebHDFS的RestAPI將指定的本機檔案添加至HDFS端的指定檔案內
	 *
	 * @param path  HDFS上檔案位置, String型別
	 * @param file  欲添加檔案, File型別
	 * @param retry 是否自動嘗試切換主從節點, boolean型別
	 * 
	 * @return 判斷添加內容至HDFS上的檔案結果是否成功, boolean型別
	 */
	public static boolean append(String path, File file, boolean retry) {
		return append(masterHost, masterPort, authUser, path, file, retry);
	}
	
	/**
	 * [方法多載]<br>
	 * 透過WebHDFS的RestAPI將指定的本機檔案添加至HDFS端的指定檔案內
	 *
	 * @param path  HDFS上檔案位置, String型別
	 * @param file  欲添加檔案, File型別
	 * 
	 * @return 判斷添加內容至HDFS上的檔案結果是否成功, boolean型別
	 */
	public static boolean append(String path, File file) {
		return append(masterHost, masterPort, authUser, path, file, true);
	}

	/**
	 * [方法多載]<br>
	 * 支援傳入一個串流; 透過WebHDFS的RestAPI將指定的本機檔案串流添加至HDFS端的指定檔案上
	 *
	 * @param host  HDFS的host位置, String型別
	 * @param port  port位置, String型別
	 * @param user  使用者名稱, String型別
	 * @param path  HDFS上檔案位置, String型別
	 * @param in    檔案串流, InputStream型別
	 * @param retry 是否自動嘗試切換主從節點, boolean型別
	 * 
	 * @return 判斷添加內容至HDFS上的檔案結果是否成功, boolean型別
	 */
	public static boolean append(String host, String port, String user, String path, InputStream in, boolean retry) {
		HttpURLConnection conn = null;
		try {
			String url = String.format(APPEND_URI_PATTERN, host, port, path, user);
			// Step 1: Submit a HTTP POST request without automatically
			// following redirects and without sending the file data.
			String redirectUrl = null;
			conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setRequestMethod(HttpMethod.POST.name());
			conn.setConnectTimeout(30000);
			conn.setInstanceFollowRedirects(false);
			conn.connect();
			int responseCode = conn.getResponseCode();
			if (responseCode == 307) {
				redirectUrl = conn.getHeaderField(HttpHeaders.LOCATION);
			} else if (responseCode == 403 && retry) {
				conn.disconnect();
				swap();
				return append(path, in, false);
			}
			conn.disconnect();
			// Step 2: Submit another HTTP POST request using the URL in the
			// Location header with the file data to be appended.
			if (redirectUrl != null) {
				conn = (HttpURLConnection) new URL(redirectUrl).openConnection();
				conn.setDoOutput(true);
				conn.setDoInput(true);
				conn.setUseCaches(false);
				conn.setRequestMethod(HttpMethod.POST.name());
				conn.setRequestProperty(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
				final int SIZE = in.available();
				conn.setRequestProperty(HttpHeaders.CONTENT_LENGTH, String.valueOf(SIZE));
				conn.setFixedLengthStreamingMode(SIZE);
				conn.connect();
				OutputStream out = conn.getOutputStream();
				IOUtils.copy(in, out, 12288);
				out.flush();
				Thread.sleep(500);
				in.close();
				out.close();
				conn.disconnect();

				return true;
			}
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			return retry && swap() && append(path, in, false);
		} catch (InterruptedException e) {
			log.error(e.getMessage(), e);
			Thread.currentThread().interrupt();

			return false;
		}

		return false;
	}

	/**
	 * [方法多載]<br>
	 * 提供預設impala參數設定, 並支援傳入一個串流<br>
	 * 透過WebHDFS的RestAPI將指定的本機檔案串流添加至HDFS端的指定檔案上
	 *
	 * @param path HDFS上檔案位置, String型別
	 * @param in   檔案串流, InputStream型別
	 * @return 判斷添加內容至HDFS上的檔案結果是否成功, boolean型別
	 */
	public static boolean append(String path, InputStream in, boolean retry) {
		return append(masterHost, masterPort, authUser, path, in, retry);
	}

	/**
	 * [方法多載]<br>
	 * 提供預設impala參數設定, 並支援傳入一個串流<br>
	 * 透過WebHDFS的RestAPI將指定的本機檔案串流添加至HDFS端的指定檔案上
	 *
	 * @param path  HDFS上檔案位置, String型別
	 * @param in    檔案串流, InputStream型別
	 * @param retry 是否自動嘗試切換主從節點, boolean型別
	 * 
	 * @return 判斷添加內容至HDFS上的檔案結果是否成功, boolean型別
	 */
	public static boolean append(String path, InputStream in) {
		return append(masterHost, masterPort, authUser, path, in, true);
	}

}
