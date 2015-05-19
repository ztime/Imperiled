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
	 * @param mapName Name of the map without ".tmx"
	 * @param listOfEvents A list containing the events to be loaded.
	 */
	public FileParser(String mapName, ArrayList<String> listOfEvents) {
		files = new ArrayList<FileHandle>();
		for(String file : listOfEvents) {
			files.add(Gdx.files.internal(prefix + "data/" + mapName + "/events/" + file + ".jwx2"));
		}
		parseEvents();
	}
	
	/**
	 * Creates a FileParser that fetches a folder
	 * with the same name as the map which the interractions
	 * belong to. Then takes all the event files in
	 * that folder and parses them into a MapEvent
	 * stored in PropertyHandler.
	 * 
	 * Interraction files have the suffix ".jwx2".
	 * 
	 * @param mapName Name of the map without ".tmx"
	 * @param actors A list of the actors loaded into the map.
	 * @param marker This is just to separate from the event constructor.
	 */
	public FileParser(String mapName, ArrayList<Actor> actors, int marker) {
		files = new ArrayList<FileHandle>();
		for(Actor actor : actors) {
			if(actor instanceof NPC) {
				String fileName = prefix + "data/" + mapName + "/interractions/" + actor.getName().substring(mapName.length() + 1) + ".jwx2";
				// This is .jar tested and works fine.
				if(!Gdx.files.internal(fileName).exists()) {
					continue;
				}
				files.add(Gdx.files.internal(fileName));
			}
		}
		parseInterractions(mapName);
	}
	
	/**
	 * A method that parses each file that the
	 * constructor gathered into a MapEvent.
	 * Adds the files to the PropertyHandler
	 * when done.
	 */
	private void parseEvents() {
		ArrayList<MapEvent> events = new ArrayList<MapEvent>();
		for(FileHandle file : files) {
			try(BufferedReader in = new BufferedReader(file.reader())) {
				events.add(new MapEvent(fileParser(in, file.name(), "event")));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		PropertyHandler.newEvents(events);
	}
	
	/**
	 * A method that parses each file
	 * gathered by the constructor to an
	 * interraction and associates it with
	 * an NPC.
	 * 
	 * @param mapName Name of current map.
	 */
	private void parseInterractions(String mapName) {
		for(FileHandle file : files) {
			try(BufferedReader in = new BufferedReader(file.reader())) {
				// Actor naming format: <mapname>-<actorname> (the substring at the end removes the suffix)
				NPC currentNPC = (NPC) PropertyHandler.currentActors.get(mapName + "-" + file.name().substring(0, file.name().length() - 5));
				currentNPC.npcText = fileParser(in, file.name(), "interraction");
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * The method called upon to parse
	 * each file into a HashMap with
	 * properties.
	 * 
	 * Syntax structure:
	 * (name)
	 * (property1)=(value1)
	 * (property2)=(value2)
	 * ...
	 * etc.
	 * 
	 * @param in A BufferedReader with the file loaded.
	 * @param filename Name of the file being parsed.
	 * @param type The type of file to be parsed.
	 * @return A HashMap with each value bound
	 *         to its property.
	 */
	private HashMap<String, String> fileParser(BufferedReader in, String filename, String type) throws IOException {
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
            		formatError(lnCnt, "First line has to be the name.", filename);
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
		if(type.equals("event")) {
			for(String req : PropertyHandler.eventReqs) {
				if(!properties.containsKey(req)) {
					formatError(0, "File does not contain the required property: " + req, filename);
				}
			}
		} else if(type.equals("interraction")) {
			for(String req : PropertyHandler.interractionReqs) {
				if(!properties.containsKey(req)) {
					formatError(0, "File does not contain the required property: " + req, filename);
				}
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
