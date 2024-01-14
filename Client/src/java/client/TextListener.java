/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.Scanner;
/**
 *
 * @author Admin
 */
public class TextListener implements MessageListener {
    private MessageProducer replyProducer;
    private Session session;
    
    public TextListener(Session session) {
              
        this.session = session;
        try {
            replyProducer = session.createProducer(null);
        } catch (JMSException ex) {
            Logger.getLogger(TextListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @Override
    public void onMessage(Message message) {
        TextMessage msg = null;

        try {
            if (message instanceof TextMessage) {
                msg = (TextMessage) message;
                System.out.println("Reading message: " + msg.getText());
            } else {
                System.err.println("Message is not a TextMessage");
            }
            // set up the reply message 
            Scanner sc = new Scanner(System.in);
            String inp = sc.nextLine();
            TextMessage response = session.createTextMessage(inp); 
            response.setJMSCorrelationID(message.getJMSCorrelationID());
            System.out.println("sending message " + response.getText());
            //หา destination จาก message (getJMSReplyTo) แล้วตอบกลับด้วย response พร้อม correlationID ไปยัง temp dest
            replyProducer.send(message.getJMSReplyTo(), response);
        } catch (JMSException e) {
            System.err.println("JMSException in onMessage(): " + e.toString());
        } catch (Throwable t) {
            System.err.println("Exception in onMessage():" + t.getMessage());
        }
        
    }
}
