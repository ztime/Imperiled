package com.imperiled.game;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

/**
 * FileParser is a class that reads events
 * from a folder "data/(mapname)" and loads
 * them into the PropertyHandler.
 * 
 * Instances of FileParser should not be
 * referenced to any variable.
 * 
 * @author John Wikman
 * @version 2015.05.12
 */
public class FileParser {
	private ArrayList<FileHandle> files;
	private static String prefix = "";
	
	/**
	 * Creates a FileParser that fetches a folder
	 * with the same name as the map which the events
	 * belong to. Then takes all the event files in
	 * that folder and parses them into a MapEvent
	 * stored in PropertyHandler.
	 * 
	 * Event files have the suffix ".jwx2".
	 * 
	 * @param path Name of the map without ".tmx"
	 */
	public FileParser(String mapName, ArrayList<String> listOfEvents) {
		files = new ArrayList<FileHandle>();
		for(String file : listOfEvents) {
			files.add(Gdx.files.internal(prefix + "data/" + mapName + "/" + file + ".jwx2"));
		}
		parseFiles();
	}
	
	/**
	 * A method that parses each file that the
	 * constructor gathered into a MapEvent.
	 * Adds the files to the PropertyHandler
	 * when done.
	 */
	private void parseFiles() {
		ArrayList<MapEvent> events = new ArrayList<MapEvent>();
		for(FileHandle file : files) {
			try(BufferedReader in = new BufferedReader(file.reader())) {
				events.add(new MapEvent(fileParser(in, file.name())));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		PropertyHandler.newEvents(events);
	}
	
	/**
	 * The method called upon by parseFiles to
	 * parse each file into a MapEvent.
	 * 
	 * Syntax structure:
	 * (name)
	 * (property1)=(value1)
	 * (property2)=(value2)
	 * ...
	 * etc.
	 * 
	 * private HashMap<String, String> fileParser(BufferedReader in, String filename) throws IOException {
	 * 
	 * @param in A BufferedReader with the file loaded.
	 * @param filename Name of the file being parsed.
	 * @return A HashMap with each value bound
	 *         to its property.
	 */
	private HashMap<String, String> fileParser(BufferedReader in, String filename) throws IOException {
		HashMap<String, String> properties = new HashMap<String, String>();
		String line;
		int lnCnt = 0;
		while((line = in.readLine()) != null) { // reads each line
			lnCnt++;
            String[] parts = line.trim().split("\\=");
            if(parts[0].equals("")) {
                continue;
            }
            if(parts.length > 2) {
            	formatError(lnCnt, "Undefined line.", filename);
            }
            if(properties.size() == 0) {
            	if(parts.length != 1) {
            		formatError(lnCnt, "First line has to be the eventname.", filename);
            	}
            	properties.put("name", parts[0]);
            	continue;
            }
            if(parts.length != 2) {
        		formatError(lnCnt, "Undefined property. Check for multiple instances of =.", filename);
        	}
            if(properties.containsKey(parts[0])) {
            	formatError(lnCnt, "Property duplicate.", filename);
            }
            properties.put(parts[0], parts[1]);
		}
		if(properties.size() == 0) {
			formatError(0, "No information in file.", filename);
		}
		for(String req : PropertyHandler.eventReqs) {
			if(!properties.containsKey(req)) {
				formatError(0, "File does not contain the required property: " + req, filename);
			}
		}
		return properties;
	}
		
	/**
     * Help method to invoke an error in syntax format.
     * 
     * @param line   Line error occurred on.
     * @param errMsg Specific error message to be printed.
     */
    private void formatError(int line, String errMsg, String filename) {
        System.err.printf("Format error in '%s' at line %d:%n%s%n", filename, line, errMsg);
        System.exit(1);
    }
}
