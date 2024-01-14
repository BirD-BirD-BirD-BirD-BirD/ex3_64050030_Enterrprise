/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

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
/**
 *
 * @author Admin
 */
public class Main {
    @Resource(mappedName = "jms/ConnectionFactory")
    private static ConnectionFactory connectionFactory;
    @Resource(mappedName = "jms/TempQueue")
    private static Queue queue;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Connection connection = null;
        TextListener listener = null;
        
         try {
            connection = connectionFactory.createConnection();
            Session session = connection.createSession(
                        false,
                        Session.AUTO_ACKNOWLEDGE);
            listener = new TextListener(session);
            //Create a temporary queue that this client will listen for responses on then create a consumer
            //that consumes message from this temporary queue...for a real application a client should reuse
            //the same temp queue for each message to the server...one temp queue per client
            
            //สร้าง temp destination เพื่อรอรับข้อมูล
            Queue tempDest = session.createTemporaryQueue();
            //ทำให้ตัวเองเป็น consumer เพื่อรอการตอบกลับด้วย
            MessageConsumer responseConsumer = session.createConsumer(tempDest);
            //ทำตัวเป็น async (listener)เพราะไม่รู้ว่าฝั่งผู้รับจะรับแล้วตอบกลับมาเมื่อไหร่
            responseConsumer.setMessageListener(listener);
            
            
            //สร้างออกมาตามปกติ Producer 
            MessageProducer producer = session.createProducer(queue);
            TextMessage message = session.createTextMessage();
            message.setText("Please enter" );
            message.setJMSReplyTo(tempDest); //set การตอบกลับ
            //Set a correlation ID so when you get a response you know which sent message the response is for
            //If there is never more than one outstanding message to the server then the
            //same correlation ID can be used for all the messages...if there is more than one outstanding
            //message to the server you would presumably want to associate the correlation ID with this
            //message somehow...a Map works good
            String correlationId = "12345"; //set ID การพูดคุย
            message.setJMSCorrelationID(correlationId); 
            connection.start();
            System.out.println("Sending message: " + message.getText());
            producer.send(message);
            
            String ch = "";
            Scanner inp = new Scanner(System.in);
            while(true) {
                System.out.print("Press q to quit ");
                ch = inp.nextLine();
                if (ch.equals("q")) {
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
    
    private static boolean isPrime(int n){
        int i;
        for(i = 2; i*i <= n; i++){
            if((n % i) == 0){
                return false;
            }
        }
        return true;
    }
    
    private static int noOfPrime(int firstNumber, int secondNumber){
        int counter = 0;
        for(int i = firstNumber; i < secondNumber; i++){
            if(isPrime(i)){
                counter++;
            }
        }
        return counter;
    }
    
}
