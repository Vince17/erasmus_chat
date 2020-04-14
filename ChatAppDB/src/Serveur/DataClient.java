/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Serveur;

/**
 *
 * @author damie
 */
public class DataClient {
    ServerClientProcessor processor;
    String IDClient;
    int ID;
    
    DataClient (int _ID){
        ID = _ID;
    }
    
    public void SetClientID(String _IDClient){
        IDClient = _IDClient;
    }
    public void SetClientProcessor (ServerClientProcessor _processor){
        processor = _processor;
    }
}
