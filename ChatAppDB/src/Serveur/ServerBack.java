
package Serveur;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import addon.MyDate;

/**
 *
 * @author damie
 */
public class ServerBack {
    //server data
    private int port = 2345;
    private String host = "127.0.0.1";
    private ServerSocket server = null;
    private boolean isRunning = true;
    
    //client data
    DataClient[] Clients = new DataClient[5];
    int countClient = 0;
    ServerBack bs = this;
    
    //database data
    String url = "jdbc:mysql://localhost:3306/projet_schema?serverTimezone=UTC";
    String user = "damien";
    String passwd = "D6b127b0";
    Connection conn;
    Statement state;
   
    public ServerBack(){
        try {
            server = new ServerSocket(port, 100, InetAddress.getByName(host));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public ServerBack(String pHost, int pPort){
        System.out.println("Server creation");
        host = pHost;
        port = pPort;
        try {
            server = new ServerSocket(port, 100, InetAddress.getByName(host));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void open(){
      
        Thread t = new Thread(() -> {
            while (isRunning == true) {
                try {
                    Socket client = server.accept();
                    
                    System.out.println("Connexion received"); 
                    Clients[countClient] = new DataClient(countClient);
                    Thread t1 = new Thread(new ServerClientProcessor(client, bs , countClient));
                    countClient ++;
                    t1.start();
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
                server = null;
            }
        });
      
        t.start();
        InitDatabase();
    }
   
    public void close(){
        isRunning = false;
    }
    
    //client processing functions
    
    public void SetClientID (int ID, String IDClient, ServerClientProcessor processor){
        System.out.println("Setup id client");
        System.out.println(countClient);
        for (int i = 0; i<= countClient;i++){
            System.out.println(i + " / " + ID);
            if (Clients[i].ID == ID){
                Clients[i].SetClientID(IDClient);
                Clients[i].SetClientProcessor(processor);
                return;
            }
        }
    }
   
    public void broadcast(DataMessage msg, int ID){
        System.out.println("broadcasting message");
        System.out.println(countClient);
        for (int i = 0; i< countClient;i++){
            System.out.println(i + " / " + ID);
            if (Clients[i].ID != ID){
                Clients[i].processor.send(msg.ToSend());
            }
        } 
    }
    
    //database fonctions
    
    private void InitDatabase(){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver O.K.");
            
            conn = DriverManager.getConnection(url, user, passwd);
            System.out.println("Connected !");  
      
            //Creation of Statement object
            state = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        } catch (Exception e){
            System.err.println("Can't connect to DB");
        }
    }
    
    public void SaveMessageToDB(DataMessage msg){
        try{
            String query = "INSERT INTO messages (sender, conv, date, text) VALUES (?, ?, ?, ?)";
            PreparedStatement prepare = conn.prepareStatement(query);
            prepare.setString(1, msg.getSender());
            prepare.setInt(2, msg.getConvID());
            prepare.setTimestamp(3, msg.date.ToSQLTimestamp());
            prepare.setString(4, msg.getMsgText());
            System.out.println(prepare);
            
            prepare.executeUpdate();
        } catch(Exception e){
            System.err.println("Saving didn't worked");
        }
    }
    
    public int countRows(ResultSet res){
        int totalRows = 0;
        try {
            while (res.next()) {
            	totalRows+=1;
            }
            //res.beforeFirst();
        } 
        catch(Exception ex)  {
            System.err.println("err nb row");
            return 0;
        }
        return totalRows ;    
    }
    
    public DataMessage[] LoadMessageFromDB(int convID, MyDate lastDate){
        try {
            System.out.println("loading msg");
            String query = "SELECT * FROM messages WHERE conv = "+convID+" AND dateEnvoie >= '"+lastDate.ToPrint()+"' ORDER BY dateEnvoie";

            System.out.println(query);
            ResultSet res = state.executeQuery(query);
            int nbRow = countRows(res);
            System.out.println(nbRow);
            DataMessage[] messageList = new DataMessage[nbRow];
            for (int i = 0; i<nbRow; i++){
                res.absolute(i+1);
                String sender = res.getString("sender");
                int conv = res.getInt("conv");
                java.sql.Timestamp dateE = res.getTimestamp("dateEnvoie");
                System.out.println(dateE);
                MyDate date = new MyDate(dateE);
                String text = res.getString("text");
                
                messageList[i] = new DataMessage(sender,conv,date,text);
                System.out.println(messageList[i].ToPrint());
            }
            return messageList;
        } catch (Exception e){
            System.out.println(e);
            return new DataMessage[0];
        }
    }
}
