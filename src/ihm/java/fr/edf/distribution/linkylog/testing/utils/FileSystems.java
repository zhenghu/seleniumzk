package fr.edf.distribution.linkylog.testing.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

/**
 * Utile fonctions pour File System
 */
public class FileSystems {

	public static String readXmlFileToString(String filePath) throws ParserConfigurationException, IOException,
			SAXException {
		StringBuilder stringBuilder = new StringBuilder();
		File fXmlFile = new File(filePath);
		FileReader fileReader = new FileReader(fXmlFile);
		BufferedReader bufferedReader = new BufferedReader(fileReader);

		String line;
		while ((line = bufferedReader.readLine()) != null) {
			stringBuilder.append(line);
		}

		return stringBuilder.toString();
	}
}
