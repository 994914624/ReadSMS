package com.test.readsms.util;

import android.util.Log;

import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * 发邮件
 */
public class SendEmailUtil {

	private static final String TAG = "SSS..SendEmailUtil";
	// 电子邮件服务器 smtp 协议的服务
	private static final String HOST = "smtp.163.com";
	// 邮箱的用户名
	private static final String  USER = "18637851080";
	// 邮箱的授权码
	private static final String  PWD = "y123456";
	// 邮件的标题
	private static final String subject = "ReadSMS";
	// 发件人的地址
	private static final String from = "18637851080@163.com";
	// 收件人的地址
	private static final String to = "15510686073@163.com";



	public static  void sendEmail(String emailContent) {
		// 关于邮件设置信息都用这个对象
		Properties properties = new Properties();
		// 设置发送邮件的邮件服务器为 163 服务器
		properties.put("mail.smtp.host", HOST);
		// 需要经过授权,也就是有用户名和密码的校验，这样才能通过验证
		properties.put("mail.smtp.auth", "true");
		// 用刚刚设置好的 properties 对象构建一个 session
		Session session = Session.getDefaultInstance(properties);
		// 调试程序
		session.setDebug(true);
		// 用 session 为参数定义消息对象
		MimeMessage message = new MimeMessage(session);
		try {
			// 加载发件人的地址
			message.setFrom(new InternetAddress(from));
			// 加载收件人的地址
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(
					to));
			// 加载标题
			message.setSubject(subject);

			// 封装邮件内容和各个部分（附件等等）对象
			Multipart multipart = new MimeMultipart();

			// 设置邮件的文本内容
			BodyPart contentPart = new MimeBodyPart();
			contentPart.setText(emailContent);
			multipart.addBodyPart(contentPart);

			// 将 multipart 对象放入 message 中
			message.setContent(multipart);
			// 保存邮件
			message.saveChanges();
			// 发送邮件
			Transport transport = session.getTransport("smtp");
			// 连接服务器的邮箱
			transport.connect(HOST, USER, PWD);
			// 把邮件发出去
			MailcapCommandMap mc = (MailcapCommandMap) CommandMap
					.getDefaultCommandMap();
			// 设置发邮件的时候的 mime 类型组合，下面的设置如果不写，邮件发布出去
			mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
			mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
			mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
			mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
			mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
			CommandMap.setDefaultCommandMap(mc);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			Log.i(TAG,"--sendEmail--OK");
		} catch (Exception e) {
			Log.i(TAG,"--sendEmail--Error");
			e.printStackTrace();
		}
	}
}
