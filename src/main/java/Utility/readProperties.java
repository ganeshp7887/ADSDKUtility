package Utility;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class readProperties {
	
	Properties prop = new Properties();
	
	public Properties readProperties() {
		try (FileInputStream input = new FileInputStream("config.properties")) {
			 prop.load(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return prop;
	}
	
	public String getPropertyValues(String Name) {
		Properties property = readProperties();
		return property.getProperty(Name);
		
	}
	
}