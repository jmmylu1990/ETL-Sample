package com.example.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.example.service.interfaces.MailService;
import com.example.utils.ClassUtils;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;

@Service
public class MailServiceImpl implements MailService {

	private static final Logger LOGGER = LoggerFactory.getLogger(MailServiceImpl.class);
	
	@Value("${spring.mail.from}")
	public String mailFrom;
	@Value("${spring.mail.from.alias}")
	public String mailFromAlias;
	@Value("${spring.mail.to}")
	public String[] mailTo;
	@Value("${spring.mail.template.path:/templates/mail}") 
	String mailTemplatePath;
	
	@Autowired
	private Configuration freeMarkerConfig;
	
	@Autowired
    private JavaMailSender javaMailSender;
	
	@Override
	public boolean sendSimpleMail(String subject, String content, String... toUsers) {
		SimpleMailMessage mailMessage = new SimpleMailMessage();
	    mailMessage.setFrom(mailFrom);
		mailMessage.setTo(ClassUtils.isValid(toUsers) ? toUsers : mailTo);
	    mailMessage.setSubject(subject);
	    mailMessage.setText(content);

	    javaMailSender.send(mailMessage);
	    
		return true;
	}
	@Override
	public boolean sendHtmlMail(String subject, String content, Map<String, File> attachmentMap, String... toUsers) {
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
	    // Sencode parameter menaing is multipart mail(i.e. includes attachments or images), default is `false`
		try {
			MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
			messageHelper.setFrom(mailFrom, mailFromAlias);
			messageHelper.setTo(ClassUtils.isValid(toUsers) ? toUsers : mailTo);
			
			messageHelper.setSubject(subject);
			// Sencode parameter menaing is html or not, default is `false`
			messageHelper.setText(content, true);
			// Add attachments into mail
			this.addAttachments(messageHelper, attachmentMap);
			
			javaMailSender.send(mimeMessage);
			
			return true;
		} catch (MessagingException | IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
		
		return false;
	}
	
	@Override
	public boolean sendHtmlMail(String subject, String content, String... toUsers) {
		return sendHtmlMail(subject, content, null, toUsers);
	}
	
	@Override
	public boolean sendTemplateMail(String subject, String tempalteName, Object params,
			Map<String, File> attachmentMap, String... toUsers) {
		try {
			MimeMessage mimeMessage = javaMailSender.createMimeMessage();
			// Sencode parameter menaing is multipart mail(i.e. includes attachments or images), default is `false`
			MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
			messageHelper.setFrom(mailFrom, mailFromAlias);
			messageHelper.setTo(ClassUtils.isValid(toUsers) ? toUsers : mailTo);
			// Set Freemarker config
			freeMarkerConfig.setClassForTemplateLoading(this.getClass(), mailTemplatePath);
			freeMarkerConfig.setEncoding(Locale.TAIWAN, StandardCharsets.UTF_8.name());
			String templateContent = FreeMarkerTemplateUtils.processTemplateIntoString(freeMarkerConfig.getTemplate(tempalteName), params);
			
			messageHelper.setSubject(subject);
			// Sencode parameter menaing is html or not, default is `false`
			messageHelper.setText(templateContent, true);
			// Add attachments into mail
			this.addAttachments(messageHelper, attachmentMap);
			
			javaMailSender.send(mimeMessage);
			
			return true;
		} catch (MessagingException | IOException | TemplateException e) {
			LOGGER.error(e.getMessage(), e);
		}
		
		return false;
	}
	
	@Override
	public boolean sendTemplateMail(String subject, String tempalteName, Object params, String... toUsers) {
		return sendTemplateMail(subject, tempalteName, params, null, toUsers);
	}
	
	private void addAttachments(MimeMessageHelper messageHelper, Map<String, File> attachmentMap) {
		if (Objects.nonNull(attachmentMap)) {
	        attachmentMap.entrySet().stream().filter(entry -> entry.getValue().exists()).forEach(entry -> {
	            try {
					messageHelper.addAttachment(entry.getKey(), new FileSystemResource(entry.getValue()));
	            } catch (MessagingException e) {
	            	LOGGER.error(e.getMessage(), e);
	            }
	        });
	    }
	}

}
