package com.lmxdawn.api.admin.util;

import com.google.common.collect.ImmutableMap;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeanToXml {

    public static String beanToXml(Object obj, Class<?> load) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(load);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        StringWriter writer = new StringWriter();
        marshaller.marshal(obj, writer);
        return writer.toString();
    }

    public static Object xmlToBean(String xmlContent, Class<?> load) throws JAXBException, XMLStreamException {
        JAXBContext context = JAXBContext.newInstance(load);
        XMLInputFactory xif = XMLInputFactory.newFactory();
        xif.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
        xif.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        XMLStreamReader xsr = xif.createXMLStreamReader(new StringReader(xmlContent));
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return unmarshaller.unmarshal(xsr);
    }

    public static Map<String, String> xmlToMap(String context) throws IOException, DocumentException {
        Map<String, String> map = new HashMap<>();
        SAXReader reader = new SAXReader();
        try (InputStream ins = new ByteArrayInputStream(context.getBytes())) {
            Document doc = reader.read(ins);
            Element root = doc.getRootElement();
            Element dict = root.element("dict");
            List<Element> list = dict.elements();
//            String details[] = new String[list.size()];

            for (int i = 0; i < list.size(); i += 2) {
                map.put(list.get(i).getText(), list.get(i + 1).getText());
            }
            return map;
        }
    }
}
