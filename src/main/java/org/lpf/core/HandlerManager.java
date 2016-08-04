package org.lpf.core;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import org.dom4j.*;
import org.dom4j.io.SAXReader;
import org.lpf.handler.IHandler;

public class HandlerManager {
	private static volatile HandlerManager instance;
	private HashMap<Integer, IHandler> handlerMap = new HashMap<>();
	private HandlerManager(){
		//���������ļ�
		readXML("src/configure.xml");
	}
	public static HandlerManager getInstance(){
		if (instance == null)
			synchronized (HandlerManager.class) {
				if (instance == null)
					instance = new HandlerManager();
			}
		return instance;
	}
	
	 private void readXML(String filename) {  
        SAXReader reader = new SAXReader();  
        Document document;
		try {
			document = reader.read(new File(filename));
			Element root = document.getRootElement();
			Iterator<Element> iterator = root.elementIterator();
	        while(iterator.hasNext()){
	        	Element e = iterator.next();
	        	try{
	        		String className = e.attributeValue("class");
	        		IHandler handler = (IHandler) Class.forName(className).newInstance();
	        		handlerMap.put(handler.msgCode(), handler);
	        		System.out.println("load the class: "+className+" msgCode: "+handler.msgCode());
	        	}
	        	catch (Exception ex){
	        		ex.printStackTrace();
	        	}
	        }
		} catch (DocumentException e) {
			System.out.println("Document not found.");
			e.printStackTrace();
		}  
	 }
	 
	 public IHandler getHandler(int key){
		 if (handlerMap.containsKey(key))
			 return handlerMap.get(key);
		 else
			 return null;
	 }
}
