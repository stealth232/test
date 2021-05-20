package ru.clevertec.check.service.impl;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.clevertec.check.service.MailService;

import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

@Slf4j
@AllArgsConstructor
@Service
public class MailServiceImpl implements MailService {
    private final JavaMailSender mailSender;

    @SneakyThrows
    @Override
    public void send(String emailTo, String attach) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(emailTo));
        mimeMessage.setSubject("Hello from Clevertec");
        Multipart emailContent = new MimeMultipart();
        MimeBodyPart textBodyPart = new MimeBodyPart();
        textBodyPart.setText("This is your last check");
        MimeBodyPart pdfBodyPart = new MimeBodyPart();
        pdfBodyPart.attachFile(attach);
        emailContent.addBodyPart(textBodyPart);
        emailContent.addBodyPart(pdfBodyPart);
        mimeMessage.setContent(emailContent);
        mailSender.send(mimeMessage);
    }
}
