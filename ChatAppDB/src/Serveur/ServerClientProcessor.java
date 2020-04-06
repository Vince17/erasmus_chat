package Serveur;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import addon.MyDate;

public class ServerClientProcessor implements Runnable{

    private Socket sock;
    private PrintWriter writer = null;
    private BufferedInputStream reader = null;
    String IDClient;
    ServerBack bs;
    int ID;
    boolean closeConnexion;
   
    public ServerClientProcessor(Socket pSock, ServerBack _bs, int _ID){
        sock = pSock;
        bs = _bs;
        ID = _ID;
    }
   
    public void run(){
        System.err.println("starting client connexion");
        closeConnexion = false;
        
        while(!sock.isClosed()){
            try {
                writer = new PrintWriter(sock.getOutputStream());
                reader = new BufferedInputStream(sock.getInputStream());
            
                
                System.out.println("waiting for response");
                String response = read();
                System.out.println("response receive, interprete it");
                
                InterpretResponse(response);
            
                if(closeConnexion){
                    System.err.println("CLOSE COMMANDE DETECTED ! ");
                    writer = null;
                    reader = null;
                    sock.close();
                    break;
                }
            }catch(SocketException e){
                System.err.println("CONNEXION WAS INTERRUPTED ! ");
                break;
            } catch (IOException e) {
                e.printStackTrace();
            }         
        }
    }
    
    //read the buffer, return it as a string
    private String read() throws IOException{      
        String response;
        int stream;
        byte[] b = new byte[4096];
        stream = reader.read(b);
        response = new String(b, 0, stream);
        //int taille = response.length();
        //response = response.substring(2,taille);
        return response;
    }
   
    //interpret what was in the buffer, core fonction
    private void InterpretResponse(String response){
        String[] responseA = response.split("@",2);
        System.out.println(response);
        switch (responseA[0]){
            case "msg" : 
                System.out.println("interpreted as message");
                DataMessage msg = TranslateToMsg(responseA[1]);
                bs.SaveMessageToDB(msg);
                bs.broadcast(msg, ID);
                ServeurInterface.PrintNewMessage(msg);
                break;
            
            case "join":
                System.out.println("interpreted as join");
                bs.SetClientID(ID, IDClient, this);
                ServeurInterface.PrintLogin(responseA[1]);
                break;
                
            case "load":
                System.out.println("interpreted as load");
                LoadAndSendMessages(responseA[1]);
                break;
                
            case "close" : 
                closeConnexion = true;
                break;
            
            default:
        }
    }
    
    //send the msg to the client
    public void send(String msg){
        System.out.println("sending message");
        writer.write(msg);
        writer.flush();
    }
    
    //if response was a message, translate it in object message
    private DataMessage TranslateToMsg(String responce){
        String[] data = responce.split("@",4);
        MyDate date = new MyDate(data[2]);
        return new DataMessage(data[0], Integer.parseInt(data[1]), date, data[3]);
    }  
    
    void LoadAndSendMessages(String response){
        String[] data = response.split("@");
        MyDate date = new MyDate(data[1]);
        System.out.println(date.ToPrint());
        DataMessage[] previousMessages = bs.LoadMessageFromDB(Integer.parseInt(data[0]), date);
        if(previousMessages.length > 0){
            for (DataMessage previousMessage : previousMessages) {
                System.out.println(previousMessage.ToPrint());
                send(previousMessage.ToSend());
            }
        }
        else{
            System.out.println("no message");
        }
    }
}