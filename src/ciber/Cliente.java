/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ciber;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Jesus
 */
public class Cliente {
    
    boolean isProd = false;
    float total = 0;
    String dato;
    int IDPC = 0;
    String shortname = null;
    String tiempoRestante = "0";
    InterfazUsuario UI = null;
    ReadXMLFile xml = null;
    String SERVER_IP = null;
   
    public Cliente(InterfazUsuario iu){
        UI = iu;
        xml = new ReadXMLFile();
        IDPC = xml.getPcId();
        shortname = xml.getShortName();
        SERVER_IP = xml.getMainServerIP();
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //Cliente c = new Cliente();
        //c.requestDetail();        
    }
    
    URLConnection getConnection(){
        URLConnection con = null;
        try {
            // open a connection to the site
            URL url = new URL("http://"+SERVER_IP+"/MyControl/formularios/RegistrarEnDB.php");
            con = url.openConnection();
            // activate the output
            con.setDoOutput(true);
        } catch (MalformedURLException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
        return con;
    }
    
    String request(String accion, String total){
        PrintStream ps = null;
        try {
            URLConnection con = getConnection();
            ps = new PrintStream(con.getOutputStream());
            // send your parameters to your site
            ps.print("id="+IDPC);            
            ps.print("&accion="+accion);
            ps.print("&total="+total);            
            // we have to get the input stream in order to actually send the request
            con.getInputStream();
            //parsearAHTML(con.getInputStream());
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line = null;
            dato="";
            while ((line = in.readLine()) != null) {
            //System.out.println(line);
                dato+=line;
            }
            // close the print stream
            ps.close();
        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            ps.close();
        }
        System.out.println(dato.trim());        
        return dato.trim();
    }
    
    void agregarDetalleVenta(String no_venta){
        //$_POST['cnsdv'],$_POST['id_producto'],$_POST['cantidad'],$_POST['precio'],$_POST['total'];
        PrintStream ps = null;
        try {
            URLConnection con = getConnection();            
            ps = new PrintStream(con.getOutputStream());
            // send your parameters to your site
            ps.print("id="+IDPC);            
            ps.print("&accion=AgregarDetalleDeVenta");            
            ps.print("&id_producto=1"); //internet
            ps.print("&no_venta="+no_venta);
            ps.print("&cantidad=1"); //enviar 1 para que registre en xml
            ps.print("&precio=10");
            ps.print("&total=0");
            // we have to get the input stream in order to actually send the request
            con.getInputStream();
            //parsearAHTML(con.getInputStream());
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line = null;
            dato="";
            while ((line = in.readLine()) != null) {
            //System.out.println(line);
                dato+=line;
            }
            // close the print stream
            ps.close();
        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            ps.close();
        }
        System.out.println(dato.trim());        
    }
    
    String requestDetail(){
        try {
            URLConnection con = getConnection();
            PrintStream ps = new PrintStream(con.getOutputStream());
            // send your parameters to your site
            ps.print("id="+IDPC);
            //ps.print("&nombre=Equipo2");

            // we have to get the input stream in order to actually send the request
            con.getInputStream();

            parsearAHTML(con.getInputStream());
            
            /*BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line = null;
            dato="";
            while ((line = in.readLine()) != null) {
                //System.out.println(line);
                dato+=line;
            }
            System.out.println("Dato:"+dato);            
            */
            // close the print stream
            ps.close();           
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println(dato);
        return dato;
    }
    
    String parsearAHTML(InputStream data){        
        dato="";
        isProd=false;
        total=0;
        try
        {	
	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	Document doc = dBuilder.parse(data);
	
	doc.getDocumentElement().normalize();

	//System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

	NodeList nList = doc.getElementsByTagName("pc");
        /*COMPROBAMOS QUE SIGA EN EJECUCION*/
        Node currentItem = nList.item(0);
        String key = currentItem.getAttributes().getNamedItem("status").getNodeValue();
        if(key.equals("finished")){
            UI.lockScreen();
            return null;
        }
        //System.out.println(key);
        total=0;
	for (int temp = 0; temp < nList.getLength(); temp++)
        {
		Node nNode = nList.item(temp);
                parseXML(nNode,nNode);		
	}
        if(tiempoRestante.equals("0 mins")){
           dato+="<div class='tiempo-off'>SU TIEMPO HA TERMINADO, ¡GRACIAS POR SU PREFERENCIA!.</div>";
       }
    } catch (Exception e) {
	e.printStackTrace();
    }
        return dato;
    }           

    void parseXML(Node node, Node parent)
    {
       if (node.hasChildNodes())
       {
          if("producto".equals(node.getNodeName()))
          {    
              dato+="<div class='producto'>\n";
              isProd=true;
          }
          NodeList childrens = node.getChildNodes();
          for (int i = 0; i < childrens.getLength(); i++)
          {
                parseXML(childrens.item(i), node);
          }//for          
          if("producto".equals(node.getNodeName())){
              dato+="</div>\n";
              isProd=false;
          }
       }//fi:root_childrens
       else
       {          
          String nodeValue = node.getNodeValue().trim();
          if (nodeValue.length() > 0){
              if(parent.getNodeName().equals("total")){
                  total = total+Float.parseFloat(nodeValue);                  
              }
              if(parent.getNodeName().equals("tiempoRestante")){
                  tiempoRestante = nodeValue;
              }
              if(isProd){
                  if(parent.getNodeName().equals("total"))
                    dato+="<label>"+nodeValue+"</label>\n";
                  else
                    dato+="<label>"+nodeValue+",</label>\n"; 
              }else{
                  dato+="<div>"+parent.getNodeName()+": "+nodeValue+"</div>\n";
              }              
          }
       }       
    }

    float getTotal(){
        return total;
    }
    
    String getTiempoRestante(){
        return tiempoRestante;
    }
    
    int getIdPc(){
        return IDPC;
    }
    
    String getShortName(){
        return shortname;
    }
    
    String getServerIp(){
        return this.SERVER_IP;
    }
}
