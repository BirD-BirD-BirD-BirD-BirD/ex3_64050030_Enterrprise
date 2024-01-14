/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

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
                System.out.println("Reading message: " + msg.getText() + " " + 
                        msg.getJMSCorrelationID());
                
                if(msg.equals("Q") || msg.equals("q")){
                    System.out.println("Closing program");
                }else{
                    String input = msg.getText();
                    System.out.println(input);
                    String[] arr = input.split(",");
                    //TextMessage response = session.createTextMessage("Hello back"); 
                    TextMessage response = session.createTextMessage(noOfPrime(arr[0],arr[1]));
                    response.setJMSCorrelationID(message.getJMSCorrelationID());
                    System.out.println("sending message " + response.getText());
                    //หา destination จาก message (getJMSReplyTo) แล้วตอบกลับด้วย response พร้อม correlationID ไปยัง temp dest
                    replyProducer.send(message.getJMSReplyTo(), response);
                }
            } else {
                System.err.println("Message is not a TextMessage");
            }
        } catch (JMSException e) {
            System.err.println("JMSException in onMessage(): " + e.toString());
        } catch (Throwable t) {
            System.err.println("Exception in onMessage():" + t.getMessage());
        }
    }
    
    private String noOfPrime(String firstNumber, String secondNumber) {
        int counter = 0;
        for (int i = Integer.parseInt(firstNumber); i < Integer.parseInt(secondNumber); i++) {
            if (isPrime(i)) {
                counter++;
            }
        }
        return Integer.toString(counter);
    }
    
    private boolean isPrime(int n) {
        if (n <= 1) {
            return false;
        }
        for (int i = 2; i * i <= n; i++) {
            if ((n % i) == 0) {
                return false;
            }
        }
        return true;
    }
}
