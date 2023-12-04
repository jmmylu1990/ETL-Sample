package com.example.test;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.service.interfaces.MailService;

@SpringBootTest
@RunWith(SpringRunner.class)
public class GmailTest {

	@Autowired
	private MailService mailService;
	
	@Test
	public void testMailSend() {
		String toMail = "1704009@example.com";
		String subject = "ETL-KEE Project Package Notify";
		String content = String.format("ETL-KEE has been packaged at %tF %1$tT", new Date());
		Assert.assertNotNull("`MailService` can not be `null`!", mailService);
		boolean result = mailService.sendHtmlMail(subject, content, toMail);
		Assert.assertTrue("Send mail failed!", result);
	}
}
