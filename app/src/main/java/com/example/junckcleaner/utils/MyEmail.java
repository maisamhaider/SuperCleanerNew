package com.example.junckcleaner.utils;

import android.content.Context;

import com.example.junckcleaner.annotations.MyAnnotations;
import com.example.junckcleaner.interfaces.TrueFalse;
import com.example.junckcleaner.prefrences.AppPreferences;

import java.util.Properties;
import java.util.Random;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MyEmail implements Runnable {
    Thread thread;
    Context context;
    AppPreferences preferences;
    Message message;
    String rEmail;
    TrueFalse trueFalse;

    public MyEmail(Context context, AppPreferences preferences, String rEmail, TrueFalse trueFalse) {
        this.context = context;
        this.preferences = preferences;
        this.trueFalse = trueFalse;

        thread = new Thread(this);

        String sEmail = "cleaners453@gmail.com";
        String sPassword = "cleaner12345";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(sEmail, sPassword);
            }
        });
        try {
            //email contains
            message = new MimeMessage(session);
            //sender email
            message.setFrom(new InternetAddress(sEmail));

            //Recipient email
            message.setRecipients((Message.RecipientType.TO),
                    InternetAddress.parse(rEmail.trim()));
            //subject
            message.setSubject("Email Verification");
            // email message
            int OTP = randomOTP();
            preferences.addLong(MyAnnotations.OTP, (long) OTP);
            message.setText("your one time password (OTP) is " + OTP);


        } catch (Exception e) {
            preferences.addLong(MyAnnotations.OTP, 1);
            trueFalse.isTrue(false);
        }

        thread.start();
    }

    public int randomOTP() {
        Random random = new Random();
        return random.nextInt(10000);

    }

    @Override
    public void run() {
        try {
            Transport.send(message);
//            Thread.sleep(2000);
            trueFalse.isTrue(true);
        } catch (MessagingException /*| InterruptedException*/ e) {
            trueFalse.isTrue(false);
            preferences.addLong(MyAnnotations.OTP, 1);

        }
    }

}
