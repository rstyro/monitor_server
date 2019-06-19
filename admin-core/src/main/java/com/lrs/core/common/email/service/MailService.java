package com.lrs.core.common.email.service;

public interface MailService {

	/**
	 * 发送普通文本
	 * @param to
	 * @param subject
	 * @param content
	 */
    public void sendSimpleMail(String from,String to, String subject, String content);

    /**
     * 发送 html 代码的邮件
     * @param to
     * @param subject
     * @param content
     */
    public void sendHtmlMail(String from,String to, String subject, String content);

    /**
     * 发送附件的邮件
     * @param to
     * @param subject
     * @param content
     * @param filePath
     */
    public void sendAttachmentsMail(String from,String to, String subject, String content, String filePath);

    /**
     * 发送内嵌的文件
     * @param to
     * @param subject
     * @param content
     * @param rscPath
     * @param rscId
     */
    public void sendInlineResourceMail(String from,String to, String subject, String content, String rscPath, String rscId);

}
