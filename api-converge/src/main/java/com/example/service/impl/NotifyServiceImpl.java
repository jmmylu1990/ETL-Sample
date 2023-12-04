package com.example.service.impl;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.model.FtlMailTemplateConstant;
import com.example.model.dto.NotifyArgument;
import com.example.model.enums.ETLResultEnum;
import com.example.service.interfaces.MailService;
import com.example.service.interfaces.NotifyService;
import com.example.utils.ClassUtils;
import com.example.utils.FileOperationUtils;

@Service
public class NotifyServiceImpl implements NotifyService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(NotifyServiceImpl.class);
	
	@Autowired
    private MailService mailService;
	
	@Override
	public boolean successNotify(NotifyArgument notifyArg) {
		String jobName = notifyArg.getJobName();
		long notifyUserCount = notifyArg.getNotifyMails().stream().filter(notifyMail -> {
			String notifySubject = String.format("【介接正常】%s介接成功通報", jobName);
			
			return mailService.sendTemplateMail(notifySubject, FtlMailTemplateConstant.ETL_SUCCESS_NOTIFY, notifyArg, notifyMail);
		}).count();
		if (notifyUserCount > 0) LOGGER.info("介接成功信發送成功, 通報人數: {}", notifyUserCount);
		
		return notifyUserCount > 0;
	}

	@Override
	public boolean failedNotify(NotifyArgument notifyArg) {
		String jobName = notifyArg.getJobName();
		ETLResultEnum apistatus = notifyArg.getApistatus();
		Exception exception = notifyArg.getException();
		int errorAccumulation = notifyArg.getErrorAccumulation();
		String notifyTypeDesc = apistatus == ETLResultEnum.IMPORT_FAIL ? "匯入" : "介接";
		long notifyUserCount = notifyArg.getNotifyMails().stream().filter(notifyMail -> {
			String notifySubject = String.format("【%s異常】%s錯誤未排除通報", notifyTypeDesc, jobName);
			String stackTrace = ClassUtils.fetchStackTrace(exception);
			String attachmentName = String.format("%s%s異常.log", jobName, notifyTypeDesc);
			File logFile = FileOperationUtils.generateTextFile(String.format("./%tQ.log", new Date()), stackTrace);
			Map<String, File> attachments = new HashMap<>();
			attachments.put(attachmentName, logFile);
			
			String tempalteName = apistatus == ETLResultEnum.IMPORT_FAIL ? 
					FtlMailTemplateConstant.IMPORT_FAIL_NOTIFY : FtlMailTemplateConstant.ETL_FAIL_NOTIFY; 
		
			// No matter send mail notify successed or failed, then remove the temp log file
			boolean boo = mailService.sendTemplateMail(notifySubject, tempalteName, notifyArg, attachments, notifyMail);
			FileOperationUtils.remove(logFile);
			return boo;
		}).count();
		if (notifyUserCount > 0) LOGGER.info("{}錯誤信發送成功, 通報人數: {}, 失敗累積: {}", notifyTypeDesc, notifyUserCount, errorAccumulation);
		
		return notifyUserCount > 0;
	}

	@Override
	public boolean noUpdateNotify(NotifyArgument notifyArg) {
		String jobName = notifyArg.getJobName();
		int errorAccumulation = notifyArg.getErrorAccumulation();
		long notifyUserCount = notifyArg.getNotifyMails().stream().filter(notifyMail -> {
			String notifySubject = String.format("【來源未更新】%s來源持續未異動通報", jobName);
			
			return mailService.sendTemplateMail(notifySubject, FtlMailTemplateConstant.ETL_SRC_NO_UPDATE_NOTIFY, notifyArg, notifyMail);
		}).count();
		if (notifyUserCount > 0) LOGGER.info("來源未異動信發送成功, 通報人數: {}, 失敗累積: {}", notifyUserCount, errorAccumulation);

		return notifyUserCount > 0;
	}
}
