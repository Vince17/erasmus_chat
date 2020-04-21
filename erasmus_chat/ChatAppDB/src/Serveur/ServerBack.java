
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
import java.sql.SQLException;
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
    
    //database functions
    
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
            String query = "INSERT INTO messages (id_user, id_conv, issue_date, text) VALUES (?, ?, ?, ?)";
            PreparedStatement prepare = conn.prepareStatement(query);
            prepare.setInt(1, msg.getSenderID());
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
            String query = "SELECT id_user, name, surname, id_conv, issue_date, text \r\n" + 
            		"FROM messages JOIN user_data ON user_data.id_user = messages.id_msg\r\n" + 
            		"WHERE id_conv = \"+convID+\" AND issue_date >= '\"+lastDate.ToPrint()+\"' ORDER BY issue_date;";

            System.out.println(query);
            ResultSet res = state.executeQuery(query);
            int nbRow = countRows(res);
            System.out.println(nbRow);
            DataMessage[] messageList = new DataMessage[nbRow];
            for (int i = 0; i<nbRow; i++){
                res.absolute(i+1);
                int senderID = res.getInt("id_user");
                int conv = res.getInt("id_conv");
                java.sql.Timestamp dateE = res.getTimestamp("issue_date");
                System.out.println(dateE);
                MyDate date = new MyDate(dateE);
                String text = res.getString("text");
                String sender = res.getString("name") + " " + res.getString("surname");
                
                messageList[i] = new DataMessage(senderID, sender,conv,date,text);
                System.out.println(messageList[i].ToPrint());
            }
            return messageList;
        } catch (Exception e){
            System.out.println(e);
            return new DataMessage[0];
        }
    }
    
    public boolean UsedMail(String mail){
    	try {
			String query = "Select * FROM user_data WHERE mail = \"" + mail +"\"";
			System.out.println(query);
			ResultSet res = state.executeQuery(query);
			return res.next();
		} catch (SQLException e) {
			System.err.println("Can't find mail");
			return true;
		}
    }
    
    public int RegisterAccount(String name, String surname, String mail, String pass, MyDate birthdate, String gender) {
    	try {
			String query = "INSERT INTO user_data (name, surname, mail, password, birth_date, gender) VALUES (?, ?, ?, ?, ?, ?)";
			PreparedStatement prepare = conn.prepareStatement(query);
			prepare.setString(1, name);
			prepare.setString(2, surname);
			prepare.setString(3, mail);
			prepare.setString(4, pass);
			prepare.setTimestamp(5, birthdate.ToSQLTimestamp());
			prepare.setString(6, gender);
			System.out.println(prepare);
            
            prepare.executeUpdate();
            
            String queryId = "Select id_user FROM user_data WHERE mail = \"" + mail +"\"";
			System.out.println(queryId);
			ResultSet res = state.executeQuery(queryId);
			if (res.next()) {
				return res.getInt("id_user");
			}
			else {
				return 0;
			}
		} catch (SQLException e) {
			System.err.println("Can't save user");
			return 0;
		}
    }
}
