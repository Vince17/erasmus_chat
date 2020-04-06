/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author damie
 */
public class DataMessage {

    String sender;
    LocalDateTime date;
    int convID;
    String msgText;
    DateTimeFormatter  toPrint = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    DateTimeFormatter  toSend = DateTimeFormatter.ofPattern("dd-MM-yyyy-HH-mm-ss");
    
    DataMessage(String _sender, int _convID, LocalDateTime _date, String _msgText){
        sender = _sender;
        date = _date;
        convID = _convID;
        msgText = _msgText;
    }
    
    public String ToPrint(){
        String toReturn = sender + " [" + toPrint.format(date) + "] : " + msgText;
        return toReturn;
    }
    
    public String ToSend(){
        String toReturn = "msg@" + sender + "@" + convID + "@" + toSend.format(date) + "@" + msgText;
        return toReturn;
    }
    
    public String getSender() {
        return sender;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public int getConvID() {
        return convID;
    }

    public String getMsgText() {
        return msgText;
    }
}
