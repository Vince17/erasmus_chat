/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import addon.MyDate;

/**
 *
 * @author damie
 */
public class ClientBack {
    Socket sckt;
    
    private PrintWriter writer = null;
    BufferedInputStream reader = null;
    
    int IDClient;
    String host;
    int port;
    boolean isRunning;
    
    DateTimeFormatter  toSend = DateTimeFormatter.ofPattern("dd-MM-yyyy-HH-mm-ss");
    
    public ClientBack(){
        host = "127.0.0.1";
        port = 1201;    
        try{
        sckt = new Socket(host, port);  
        writer = new PrintWriter(sckt.getOutputStream());
        reader = new BufferedInputStream(sckt.getInputStream());
        writer.write("join@" + IDClient);
        writer.flush();
        } catch (Exception e) {}
    }
    
    public ClientBack (String _host, int _port){
        host = _host;
        port = _port;    
        try{
        sckt = new Socket(host, port);
        writer = new PrintWriter(sckt.getOutputStream());
        reader = new BufferedInputStream(sckt.getInputStream());
        } catch (Exception e) {}
    }
    
    public void setIDClient (int _IDClient){
        IDClient = _IDClient;
        try {  
            writer.write("join@" + IDClient);
            writer.flush();
        } catch (Exception e) {}
    }
    
    public void registerRequest(String name, String surname, String mail, String pass) {
    	try {
			String msgOut = "register@" + name + "@" + surname +  "@" + pass + "@" + "19-04-2020-00-00-00" + "@" + "M" + "@" + mail + "|";
			writer.write(msgOut);
			writer.flush();
			System.out.print("register gone");
		} catch (Exception e) {}
    }
    
    //send message to the server
    public void sendMsg(DataMessage msgOut){
        try {
            writer.write(msgOut.ToSend());
            writer.flush();            
        } catch (Exception e) {}
    }
    
    public void send(String msg){
        try {
            writer.write(msg);
            writer.flush();            
        } catch (Exception e) {}
    }
    
    public void loadMessages(int convID, MyDate date){
        try {
            System.out.println("Trying to load");
            String msgOut = "load@"+ convID + "@" + date.ToSend();
            writer.write(msgOut);
            writer.flush();            
        } catch (Exception e) {}
    }
    
    //read responses
    public String read() throws IOException{  
        String response = "";
        int stream;
        byte[] b = new byte[4096];
        stream = reader.read(b);
        response = new String(b, 0, stream);
        return response;
    }
}
