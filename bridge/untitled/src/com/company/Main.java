package com.company;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.file.*;

import java.nio.file.Files;

public class Main {

    public static void main(String[] args) {


            File fXmlFile = new File("C:\\Users\\arthur\\Desktop\\tst\\tst\\English test\\English test\\Test_Lucoil_fin_140716_SCORM\\1\\runtime.xml");

//            String contents = new String(Files.readAllBytes(Paths.get(fXmlFile.getAbsolutePath())));
//            contents = contents.replaceAll("(\\r|\\n)", "");
         try (PrintStream out = new PrintStream(new FileOutputStream(fXmlFile.getAbsolutePath()+".html"))) {



            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            doc.getDocumentElement().normalize();

//            NodeList q_questions_list = doc.getElementsByTagName("q_questions");
//            for (int temp = 0; temp < q_questions_list.getLength(); temp++) {
//                Element q_questions = (Element) q_questions_list.item(temp);
                NodeList item_list = doc.getElementsByTagName("q_choice_variants");
                int cnt =0;
                for (int q = 0; q < item_list.getLength(); q++) {
                    Element item = (Element) item_list.item(q);
                    String Q = ((Element) item.getParentNode()).getElementsByTagName("q_question").item(0).getTextContent();
                    out.print("[Q]"+(++cnt)+": ");
                    out.println(Q);

                    NodeList items = item.getElementsByTagName("item");
                    for (int i = 0; i < items.getLength(); i++) {
                        Element aitem = (Element) items.item(i);
                        if ("yes".equals(aitem.getElementsByTagName("q_right").item(0).getTextContent())){
                            String A = aitem.getElementsByTagName("q_variant").item(0).getTextContent();
                            out.println("[A]: "+ A);
                        }
                    }
                    out.println("<br/><br/><br/>");
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
