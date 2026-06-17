package com.example.onlineedu.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 邮件发送工具类
 */
@Slf4j
@Component
public class MailUtil {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String from;

    @Autowired
    public MailUtil(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * 同步发送简单文本邮件（有返回值，用于需要感知发送结果的场景）
     */
    public boolean sendTextMail(String to, String subject, String content) {
        if (from == null || from.isEmpty()) {
            log.error("未配置发件人(spring.mail.username)，无法发送邮件：{}", subject);
            return false;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            mailSender.send(message);
            log.info("邮件发送成功。收件人: {}, 主题: {}", to, subject);
            return true;
        } catch (Exception e) {
            log.error("邮件发送异常。收件人: {}, 主题: {}", to, subject, e);
            return false;
        }
    }

    /**
     * 异步发送简单文本邮件（@Async，接口立即返回，邮件在后台线程发送）
     * 适用于验证码发送等对实时性要求不高、不需要感知结果的场景
     */
    @Async
    public void sendTextMailAsync(String to, String subject, String content) {
        if (from == null || from.isEmpty()) {
            log.error("未配置发件人(spring.mail.username)，无法发送邮件：{}", subject);
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            mailSender.send(message);
            log.info("异步邮件发送成功。收件人: {}, 主题: {}", to, subject);
        } catch (Exception e) {
            log.error("异步邮件发送异常。收件人: {}, 主题: {}", to, subject, e);
        }
    }
}
