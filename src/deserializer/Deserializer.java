
package deserializer;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import logic.game.collection.LogicGameCollection;

public class Deserializer {
    private List<Class<?>> registeredClasses;
    private InputStream xmlFile = this.getClass().getClassLoader().getResourceAsStream("main/games.xml");
    private static final Logger LOGGER = Logger.getLogger(LogicGameCollection.class.getName());
    
    public Deserializer(){
        registeredClasses = new ArrayList<>();
        
    }
    
    public List<Object> deserialize() throws Exception {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = factory.createXMLStreamReader(xmlFile);
        return createObjects(reader);  
    }
    
    private List<Object> createObjects(XMLStreamReader reader) throws Exception{
        List<Object> objects = new ArrayList<>();
        String className = "";
        while(reader.hasNext()){
            switch(reader.next()){
                case XMLStreamReader.START_ELEMENT:
                    className = reader.getLocalName();
                    if (! className.equals("games")){   //ignore root tag
                        objects.add(createObject(reader, className));
                    }
                    break;
            }
        }
        reader.close();
        return objects;
    }
    
    private Object createObject(XMLStreamReader reader, String className) throws Exception{
        Class<?> objectClass = null;
        
        for (Class<?> c : registeredClasses){
            Deserialize classAnnotation = c.getDeclaredAnnotation(Deserialize.class);
            if ((classAnnotation != null) && (className.equals(classAnnotation.as()))){
                objectClass = c;
                break;
            }
        }
        if (objectClass == null){
            throw new NoClassFoundException();
        }
        Object obj = objectClass.newInstance();
        fillObject(reader, obj, className);
        return obj;
    }
    
    private boolean fillObject(XMLStreamReader reader, Object o, String className) throws Exception{
        Method currentMethod = null;
        String text = "";
        while(reader.hasNext()){
            switch(reader.next()){
                case XMLStreamReader.START_ELEMENT:
                    if (reader.getAttributeCount() > 0){
                        processAttributes(reader, o);
                    }
                    String tag = reader.getLocalName();
                    currentMethod = findProperMethod(tag, o.getClass());
                    if (currentMethod == null){
                        LOGGER.log(Level.WARNING, "Method {0} does not exist and was ignored.", tag);
                    }
                    break;
                case XMLStreamReader.CHARACTERS:
                    text = reader.getText().trim();
                    break;
                case XMLStreamReader.END_ELEMENT:
                    String s = reader.getLocalName();
                    if (s.equals(className))
                        return true;
                    if (!text.isEmpty())
                        invokeMethod(currentMethod, text.trim(), o);
                    break;
            }
        }
        return false;   //no end tag encountered == wrong formatting in xml file
        
    }
    
    private void processAttributes(XMLStreamReader reader, Object o) throws Exception{
        String startTag = reader.getLocalName();
        Method properMethod = findProperMethod(startTag, o.getClass());
        
        if (properMethod != null){
            if (properMethod.getParameterCount() == reader.getAttributeCount()){
                List<String> args = new ArrayList<>();
                for (int i = 0; i < reader.getAttributeCount(); i++){
                    String attrValue = reader.getAttributeValue(i).trim();
                    args.add(attrValue);
                }
                invokeMethod(properMethod, args, o);
            } else LOGGER.log(Level.WARNING, "Invalid argument count for method {0}.", properMethod.getName());
        } else LOGGER.log(Level.WARNING, "Method {0} does not exist and was ignored.", startTag);
        
    }
    
    
    private void invokeMethod(Method m, String param, Object o) throws IllegalAccessException,
            IllegalArgumentException, InvocationTargetException{
        Class<?>[]paramTypes = m.getParameterTypes();
        try{
            if (paramTypes[0].equals(String.class)){
                m.invoke(o, param);
            }else if (paramTypes[0].equals(int.class)){
                m.invoke(o, Integer.parseInt(param));
            }else if (paramTypes[0].equals(float.class)){
                m.invoke(o, Float.parseFloat(param));
            }else if (paramTypes[0].equals(double.class)){
                m.invoke(o, Double.parseDouble(param));
            }else if (paramTypes[0].equals(boolean.class)){
                m.invoke(o, Boolean.parseBoolean(param));
            } 
        }catch (NumberFormatException ex){
            LOGGER.log(Level.SEVERE, "Invalid argument type for method {0}. {1}",
                    new Object[]{m.getName(), ex.getMessage()});
        }
    } 
    
    private void invokeMethod(Method m, List<String> param, Object o) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
        Class<?>[]paramTypes = m.getParameterTypes();
        Object[] parsedParams = new Object[paramTypes.length];
        try{
            for (int i = 0; i<paramTypes.length; i++) { 
                if (paramTypes[i].equals(String.class)){
                    parsedParams[i] = param.get(i);
                }else if (paramTypes[i].equals(int.class)){
                    parsedParams[i] = Integer.parseInt(param.get(i));
                }else if (paramTypes[i].equals(float.class)){
                    parsedParams[i] = Float.parseFloat(param.get(i));
                }else if (paramTypes[i].equals(double.class)){
                    parsedParams[i] =  Double.parseDouble(param.get(i));
                }else if (paramTypes[i].equals(boolean.class)){
                   parsedParams[i] = Boolean.parseBoolean(param.get(i));
                }   
            }
            m.invoke(o, parsedParams);
        }catch (NumberFormatException ex){
            LOGGER.log(Level.SEVERE, "Invalid argument type for method {0}. {1}", 
                    new Object[]{m.getName(), ex.getMessage()});
        }
    }
    
    private Method findProperMethod(String shortcut, Class<?> objectClass){
        
        for (Method m : objectClass.getDeclaredMethods()){
            Deserialize d = m.getDeclaredAnnotation(Deserialize.class);
            if (d != null && (shortcut.equals(d.as()))){
                return m;
            }
        }
        return null;
    }
    
    public void registerClass(Class<?> c){
        registeredClasses.add(c);
    }
    
    public void unregisterClass(Class<?> c){
        registeredClasses.remove(c);
    }
    
    public List<Class<?>> getRegisteredClasses(){
        return registeredClasses;
    }
    
}
