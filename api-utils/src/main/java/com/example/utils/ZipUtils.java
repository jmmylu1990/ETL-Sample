package com.example.utils;

import static com.example.utils.FileOperationUtils.formatVerify;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Stanley
 * @version 1.0
 */
public class ZipUtils {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ZipUtils.class);
	private static final String NOT_ZIP_FILE_MSG = "Filename Extension does not match! The extension must be `zip` or `ZIP`.";
	
	/**
	 * 預設一次串流壓縮的byte數
	 */
	private static int defaultBuffer = 8192;

	/**
	 * 變更預設一次串流壓縮的byte數
	 *
	 * @param bytes
	 *            串流的byte數, int型別
	 */
	public static void setBuffer(int bytes) {
		defaultBuffer = bytes;
	}

	private ZipUtils() {
	}

	/**
	 * 將檔案串流至Zip壓縮檔中
	 *
	 * @see java.io.File
	 * @see java.io.BufferedInputStream
	 * @see java.io.FileInputStream
	 * @see java.util.zip.ZipOutputStream
	 * @param resource
	 *            資料來源, String型別
	 * @param target
	 *            目標檔案, File型別
	 * @param zOut
	 *            Zip輸出串流, ZipOutputStream型別
	 */
	public static void compressFile(String resource, File target, ZipOutputStream zOut) {
		if (target.isDirectory()) {
			File[] files = target.listFiles();
			resource += target.getName() + File.separator;
			if (files == null) return;
			for (File file : files) {
				compressFile(resource, file, zOut);
			}
		} else {
			resource = resource.substring(resource.indexOf('\\') + 1);
			try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(target))) {
				zOut.putNextEntry(new ZipEntry(resource + target.getName()));
				byte[] bytes = new byte[defaultBuffer];
				int size;
				while ((size = in.read(bytes)) != -1) {
					zOut.write(bytes, 0, size);
				}
				zOut.flush();
				zOut.finish();
			} catch (IOException e) {
				LOGGER.info(e.getMessage(), e);
			}
		}
	}

	/**
	 * [方法多載]<br>
	 * 支援字串型的路徑參數; 將指定來源壓縮成Zip壓縮檔
	 *
	 * @see java.io.File
	 * @see java.io.BufferedInputStream
	 * @see java.io.FileInputStream
	 * @see java.util.zip.ZipOutputStream
	 * @param resource
	 *            資料來源完整路徑, String型別
	 * @return 壓縮後的檔案, File型別
	 */
	public static File compressToZip(String resource) {
		File orignFile = new File(resource);
		int endIndex = resource.lastIndexOf('.') == -1 ? resource.length() : resource.lastIndexOf('.');
		String rootPath = resource.substring(0, endIndex);
		File zipFile = new File(rootPath + ".zip");
		try (ZipOutputStream zOut = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)), Charset.forName("Big5"))) {
			zOut.setLevel(5);
			compressFile("", orignFile, zOut);
		} catch (IOException e) {
			LOGGER.info(e.getMessage(), e);
		}
		return zipFile;
	}

	/**
	 * 將指定來源壓縮成Zip壓縮檔
	 *
	 * @see java.io.File
	 * @see java.io.BufferedInputStream
	 * @see java.io.FileInputStream
	 * @see java.util.zip.ZipOutputStream
	 * @param resource
	 *            資料來源完整路徑, File型別
	 * @return 壓縮後的檔案, File型別
	 */
	public static File compressToZip(File resource) {
		try {
			if (resource != null) return compressToZip(resource.getCanonicalPath());
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}

		return resource;
	}

	/**
	 * 解壓縮Zip檔案
	 *
	 * @see java.io.File
	 * @see java.io.InputStream
	 * @see java.io.BufferedOutputStream
	 * @see java.io.FileInputStream
	 * @see java.util.zip.ZipInputStream
	 * @see java.util.zip.ZipOutputStream
	 * @param file
	 *            欲解壓縮的檔案, String型別
	 * @param createRoot
	 *            是否自動創建根目錄, boolean型別
	 * @return 解壓縮後的檔案(可以是資料夾或檔案), File型別
	 */
	public static File decompresstZip(String file, boolean createRoot) {
		// 找出Zip檔案位於的根目錄,並決定是否創建解壓縮根目錄
		int filePathIndex = createRoot ? file.lastIndexOf('.') : file.lastIndexOf('\\');
		String destPath = file.substring(0, filePathIndex);
		return decompresstZip(file, destPath);
	}

	/**
	 * 解壓縮Zip檔案至指定路徑
	 *
	 * @see java.io.File
	 * @see java.io.InputStream
	 * @see java.io.BufferedOutputStream
	 * @see java.io.FileInputStream
	 * @see java.util.zip.ZipInputStream
	 * @see java.util.zip.ZipOutputStream
	 * @param file
	 *            欲解壓縮的檔案, String型別
	 * @param destPath
	 *            解壓縮後的檔案輸出路徑, String型別
	 * @return 解壓縮後的檔案(可以是資料夾或檔案), File型別
	 */
	public static File decompresstZip(String file, String destPath) {
		if (!formatVerify(file, "zip")) {
			LOGGER.error(NOT_ZIP_FILE_MSG);
			return null;
		}
		try (ArchiveInputStream in = new ArchiveStreamFactory().createArchiveInputStream("zip", new FileInputStream(file));) {
			ZipArchiveEntry entry = null;
			while ((entry = (ZipArchiveEntry) in.getNextEntry()) != null) {
				File outFile = new File(destPath, entry.getName());
        		FileOperationUtils.transfer(in, outFile, defaultBuffer);
			}
		} catch (IOException | ArchiveException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return new File(destPath);
	}

	/**
	 * 不解壓縮Zip檔案, 直接讀取裏頭所有文件檔<br>
	 * 並將全部文檔內容寫入於指定路徑下的文件檔
	 *
	 * @see java.io.File
	 * @see java.io.BufferedInputStream
	 * @see java.io.BufferedOutputStream
	 * @see java.io.FileInputStream
	 * @see java.util.zip.ZipInputStream
	 * @see java.util.zip.ZipOutputStream
	 * @param zipFile
	 *            欲讀取的壓縮檔的完整路徑, String型別
	 * @param outputFile
	 *            輸出檔案完整路徑, String型別
	 * @param charset
	 *            讀取的檔案編碼, String型別
	 * @return 合併後的文件檔, File型別
	 */
	public static File mergeContentsWithoutDecompress(String zipFile, String outputFile, String charset) {
		if (!formatVerify(zipFile, "zip")) {
			LOGGER.error(NOT_ZIP_FILE_MSG);
			return null;
		}
		File outFile = new File(outputFile);
		// 開啟檔案串流,並採用Try with Resource方式自動關閉串流
		try (ZipFile zip = new ZipFile(zipFile);
				ZipInputStream zIn = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipFile)))) {
			ZipEntry zipEntry;
			while ((zipEntry = zIn.getNextEntry()) != null) {
				// 如果壓縮檔內容物非資料夾情況,陸續讀取檔案
				if (!zipEntry.isDirectory()) {
					// 輸出檔案的詳細資訊
					LOGGER.info("File - {}: {} bytes", zipEntry.getName(), zipEntry.getSize());
					if (zipEntry.getSize() != 0) {
						// 採用BufferedReader物件去讀檔並將內容寫入於新的檔案
						try (BufferedReader in = new BufferedReader(new InputStreamReader(zip.getInputStream(zipEntry), charset));
								BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile, true)))) {
							String line;
							while ((line = in.readLine()) != null) {
								out.write(line);
								// 輸出一個換行符號
								out.newLine();
							}
							// 將緩衝區資料全部輸出,避免串流未正常關閉導致資料缺失
							out.flush();
						} catch (IOException e) {
							LOGGER.info(e.getMessage(), e);
						}
					}
				}
			}
		} catch (IOException e) {
			LOGGER.info(e.getMessage(), e);
		}

		return outFile;
	}

	/**
	 * 不解壓縮Zip檔案, 直接讀取裏頭所有文件檔<br>
	 * 並將全部文檔內容寫入至指定的文件檔<br>
	 * 檔案路徑為壓縮檔同目錄, 檔名預設指定為壓縮檔名.csv
	 *
	 * @see java.io.File
	 * @see java.io.BufferedInputStream
	 * @see java.io.BufferedOutputStream
	 * @see java.io.FileInputStream
	 * @see java.util.zip.ZipInputStream
	 * @see java.util.zip.ZipOutputStream
	 * @param zipFile
	 *            欲讀取的壓縮檔, File型別
	 * @param charset
	 *            讀取的檔案編碼, String型別
	 * @return 合併後的文件檔, File型別
	 */
	public static File mergeContentsWithoutDecompress(File zipFile, String charset) {
		try {
			if (zipFile != null) {
				// 獲取Date物件並定義日期格式
				String fileName = zipFile.getName();
				fileName = fileName.substring(0, fileName.lastIndexOf('.'));
				// 建立輸出檔案位置(根目錄)
				String zipFilePath = zipFile.getCanonicalPath();
				String outPath = zipFilePath.substring(0, zipFilePath.lastIndexOf(File.separator));
				File outDir = new File(outPath);
				if (!outDir.exists()) outDir.mkdirs();
				String outputFile = outDir.getPath() + File.separator + fileName + ".csv";

				return mergeContentsWithoutDecompress(zipFilePath, outputFile, charset);
			}
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}

		return zipFile;

	}

	/**
	 * 不解壓縮Zip檔案, 直接讀取裏頭指定的文件檔<br>
	 * 並將內容寫入至一個文件檔<br>
	 * 檔案路徑為壓縮檔同目錄, 檔名預設指定為壓縮檔名.csv
	 *
	 * @see java.io.File
	 * @see java.io.BufferedInputStream
	 * @see java.io.BufferedOutputStream
	 * @see java.io.FileInputStream
	 * @see java.util.zip.ZipInputStream
	 * @see java.util.zip.ZipOutputStream
	 * @param zipFile
	 * @param zipFile
	 *            欲讀取的壓縮檔, File型別
	 * @param targetFile
	 *            欲讀取的檔案(單一檔案)名稱, String類別
	 * @param charset
	 *            讀取的檔案編碼, String型別
	 */
	public static File readSingleFile(File zipFile, String targetFile, String charset) {
		if (!formatVerify(zipFile, "zip")) {
			LOGGER.error(NOT_ZIP_FILE_MSG);
			return null;
		}
		String fileName = zipFile.getName();
		String zipFilePath = zipFile.toString();
		fileName = fileName.substring(0, fileName.lastIndexOf('.'));
		// 建立輸出檔案位置
		String outPath = zipFilePath.substring(0, zipFilePath.lastIndexOf('\\'));
		File outFile = new File(outPath, fileName + ".csv");
		if (!outFile.exists())
			outFile.mkdirs();
		// 開啟檔案串流,並採用Try with Resource方式自動關閉串流
		try (ZipFile zip = new ZipFile(zipFile);
				ZipInputStream zIn = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipFile)))) {
			ZipEntry zipEntry;
			while ((zipEntry = zIn.getNextEntry()) != null) {
				// 如果壓縮檔內容物非資料夾情況,陸續讀取檔案
				if (!zipEntry.isDirectory()) {
					// 輸出檔案的詳細資訊
					String fileNameInZip = zipEntry.getName();
					fileNameInZip = fileNameInZip.substring(fileNameInZip.lastIndexOf('/') + 1);
					if (fileNameInZip.equals(targetFile)) {
						// 採用BufferedReader物件去讀檔並將內容寫入於新的檔案
						try (BufferedReader in = new BufferedReader(new InputStreamReader(zip.getInputStream(zipEntry), charset));
								BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile, true)))) {
							String line;
							while ((line = in.readLine()) != null) {
								out.write(line);
								// 輸出一個換行符號
								out.newLine();
							}
							// 將緩衝區資料全部輸出,避免串流未正常關閉導致資料缺失
							out.flush();
						} catch (IOException e) {
							LOGGER.info(e.getMessage(), e);
						}
					}
				}
			}
		} catch (IOException e) {
			LOGGER.info(e.getMessage(), e);
		}

		return outFile;
	}

	
	public static void decompress7z(File resourceFile, File destination) {
		if (!destination.exists()) {
			destination.mkdirs();
		}
        try ( SevenZFile sevenZFile = new SevenZFile(resourceFile)) {
        	SevenZArchiveEntry entry;
			while ((entry = sevenZFile.getNextEntry()) != null) {
				File curfile = new File(destination, entry.getName());
        		if (entry.isDirectory()) continue;
        		
        		File parent = curfile.getParentFile();
        		if (!parent.exists()) parent.mkdirs();
        		
        		byte[] bytes = new byte[(int) entry.getSize()];
        		sevenZFile.read(bytes, 0, bytes.length);
        		FileOperationUtils.writeBytes(curfile, bytes, true);
        	}
        } catch (IOException e) {
        	LOGGER.error(e.getMessage(), e);
        }
    }
}
