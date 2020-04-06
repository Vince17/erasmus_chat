/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Serveur;
import addon.MyDate;

/**
 *
 * @author damie
 */
public class DataMessage {

    String sender;
    MyDate date;
    int convID;
    String msgText;
    
    DataMessage(String _sender, int _convID, MyDate _date, String _msgText){
        sender = _sender;
        date = _date;
        convID = _convID;
        msgText = _msgText;
    }
    
    public String ToPrint(){
        String toReturn = sender + " [" + date.ToPrint() + "] : " + msgText;
        return toReturn;
    }
    
    public String ToSend(){
        String toReturn = "msg@" + sender + "@" + convID + "@" + date.ToSend() + "@" + msgText;
        return toReturn;
    }
    
    public String getSender() {
        return sender;
    }

    public MyDate getDate() {
        return date;
    }

    public int getConvID() {
        return convID;
    }

    public String getMsgText() {
        return msgText;
    }
}
