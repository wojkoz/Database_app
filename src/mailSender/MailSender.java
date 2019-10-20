package mailSender;

import java.util.ArrayList;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class MailSender implements Runnable{
    public static final String MAIL_SCHEME = "roznosci", MAIL_TABLE = "subskrypcje";
    public static final String[] MAIL_COLUMNS = {"email", "subskrypcja"};
    
    private final String USERNAME = "testtest54321123456@gmail.com";
    private final String PASSWORD = "testTest";
    
    private String sendTo;
    
    public void makeEmailList( ArrayList<String> list){
        sendTo = list.toString().replace("[", "");
        sendTo = sendTo.replace("]", "");
    }
    
    private void sendMails(){
       

        Properties prop = new Properties();
	prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true"); //TLS
        prop.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        
        Session session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(USERNAME, PASSWORD);
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(USERNAME));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(sendTo)
            );
            message.setSubject("Testing Gmail TLS");
            message.setText("Dear Mail Crawler,"
                    + "\n\n Please do not spam my email!");

            Transport.send(message);

            JOptionPane.showMessageDialog(null, "Done", "MailSender", JOptionPane.INFORMATION_MESSAGE);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        sendMails();
    }
}
