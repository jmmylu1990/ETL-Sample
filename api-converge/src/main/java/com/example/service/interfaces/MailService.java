package com.example.service.interfaces;

import java.io.File;
import java.util.Map;

public interface MailService {

	public boolean sendSimpleMail(String subject, String content, String... toUsers);
	
	public boolean sendHtmlMail(String subject, String content, Map<String, File> attachmentMap, String... toUsers);

	public boolean sendHtmlMail(String subject, String content, String... toUsers);

	public boolean sendTemplateMail(String subject, String tempalteName, Object params, Map<String, File> attachmentMap, String... toUsers);

	public boolean sendTemplateMail(String subject, String tempalteName, Object params, String... toUsers);

}
