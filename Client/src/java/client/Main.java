/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.util.Scanner;
import javax.annotation.Resource;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;


public class Main {

    /**
     * @param args the command line arguments
     */
    
    @Resource(mappedName = "jms/ConnectionFactory")
    private static ConnectionFactory connectionFactory;
    @Resource(mappedName = "jms/TempQueue")
    private static Queue queue;
    
    public static void main(String[] args) {
        // TODO code application logic here
        MessageProducer replyProducer = null;
        Connection connection = null;
        Session session = null;
        MessageConsumer consumer = null;
        TextMessage message = null;
        TextListener listener = null;
        
        try {
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            consumer = session.createConsumer(queue);
            listener = new TextListener(session);
            consumer.setMessageListener(listener);
            connection.start();
            String ch = "";
            Scanner inp = new Scanner(System.in);
            while(true) {
                System.out.print("Press q to quit ");
                ch = inp.nextLine();
                if (ch.equals("q") || ch.equals("Q")) {
                    break;
                }
            }
            
            
        }catch (JMSException e) {
            System.err.println("Exception occurred: " + e.toString());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                }
            }
        }
    }
    
}
