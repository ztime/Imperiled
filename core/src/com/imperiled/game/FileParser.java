package com.imperiled.game;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class FileParser {
	private FileHandle folder;
	private String suffix;
	private ArrayList<FileHandle> files;
	private static HashMap<String, String[]> reqs = new HashMap<String, String[]>();
	
	public FileParser(String suffix) {
		addReqs();
		if(!reqs.containsKey(suffix)) {
			// Exits if there is no data for this suffix.
			System.err.printf("Get yo shiet right. %s ain't no suffix.%n", suffix);
	        System.exit(1);
		}
		this.suffix = suffix;
		if (Gdx.app.getType() == ApplicationType.Android) {
			folder = Gdx.files.internal("data/events");
		} else {
			folder = Gdx.files.internal("./bin/data/events");
		}
		files = new ArrayList<FileHandle>();
		for(FileHandle file : folder.list()) {
			if(!file.extension().equalsIgnoreCase(suffix) &&
					file.isDirectory()) {
				files.add(file);
			}
		}
		parseFiles();
	}
	
	public FileHandle getFolder() {
		return folder;
	}
	
	private void parseFiles() {
		for(FileHandle file : files) {
			try(BufferedReader in = file.reader(0)) {
				fileParser(in, file.name());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private String fileParser(BufferedReader in, String filename) throws IOException {
		HashMap<String, String> properties = new HashMap<String, String>();
		String line;
		int lnCnt = 0;
		while ((line = in.readLine()) != null) { // reads each line
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
            if(properties.containsKey(parts[0])) {
            	formatError(lnCnt, "Property duplicate.", filename);
            }
            properties.put(parts[0], parts[1]);
		}
		if(properties.size() == 0) {
			formatError(lnCnt, "No information in file.", filename);
		}
		return properties.get("name");
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
    
    private void addReqs() {
    	reqs.put("event", PropertyHandler.eventReqs);
    	reqs.put("music", PropertyHandler.musicReqs);
    }
}
