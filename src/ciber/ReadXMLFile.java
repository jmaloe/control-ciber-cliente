/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ciber;

/**
 *
 * @author Jesus
 */
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import java.net.InetAddress;

public class ReadXMLFile {
    int idpc = 0;
    String nombrecorto = null, mainserverip;
  public int getPcId(){

    try {
        /*String path = ReadXMLFile.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        path = path.replace("malos.ciber.jar", "");
        */
        File f = new File(System.getProperty("java.class.path"));
        File dir = f.getAbsoluteFile().getParentFile();
        String path = dir.toString();
        
	File fXmlFile = new File(path+"/config.xml");
	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	Document doc = dBuilder.parse(fXmlFile);
	
	doc.getDocumentElement().normalize();

	//System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

	NodeList nList = doc.getElementsByTagName("pc");

	for (int temp = 0; temp < nList.getLength(); temp++) {

		Node nNode = nList.item(temp);

		//System.out.println("\nCurrent Element :" + nNode.getNodeName());

		if (nNode.getNodeType() == Node.ELEMENT_NODE)
                {
			Element eElement = (Element) nNode;
                        mainserverip = eElement.getElementsByTagName("mainserver").item(0).getTextContent();
			idpc = Integer.parseInt(eElement.getAttribute("id"));
                        InetAddress inetAddress = InetAddress.getByName(mainserverip);
                        mainserverip = inetAddress.getHostAddress();                        
                        nombrecorto = eElement.getElementsByTagName("shortname").item(0).getTextContent();
                        /*System.out.println("PC ID:"+idpc);
			System.out.println("Short Name : " + eElement.getElementsByTagName("shortname").item(0).getTextContent());
			System.out.println("Name : " + eElement.getElementsByTagName("name").item(0).getTextContent());*/
		}
	}
    } catch (Exception e) {
	e.printStackTrace();
    }
    return idpc;
  }

    String getShortName() {
        return nombrecorto;
    }
    
    String getMainServerIP(){        
        return mainserverip;
    }

}