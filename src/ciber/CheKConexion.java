/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ciber;

import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author Jesus
 */
public class CheKConexion {
    public boolean connect(String server_ip){
        String ip = server_ip;
        int puerto = 80;
        Socket s;
        try {
            s = new Socket(ip, puerto);
            if(s.isConnected()){
                s.close();
                return true;
            }            
        } catch (IOException ex) {
            return false;
        }
        return false;
    }    
}
