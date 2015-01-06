package fr.edf.distribution.linkylog.testing.utils;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 * XML utils
 */
public class XMLUtils {

	public static Object unmarshal(InputStream file,Class classeToBeBound) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance(classeToBeBound);
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		return unmarshaller.unmarshal(file);
	}

}
