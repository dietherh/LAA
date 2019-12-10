package LAA.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.core.Response;

import LAA.parser.FileLog;

public class LAAService {
	
private static final String UPLOAD_FOLDER = System.getenv("HOMEPATH");
	
	private final List<FileLog> files = new ArrayList<FileLog>();
	
	public Response uploadFile(
			InputStream uploadedInputStream) {
		
		if (uploadedInputStream == null)
			return Response.status(400).entity("Invalid form data").build();
		
		try {
			createFolderIfNotExists(UPLOAD_FOLDER);
		} catch (SecurityException se) {
			return Response.status(500)
					.entity("Can not create destination folder on server")
					.build();
		}
		String uploadedFileLocation = UPLOAD_FOLDER + "test.log";
		try {
			saveToFile(uploadedInputStream, uploadedFileLocation);
		} catch (IOException e) {
			return Response.status(500).entity("Can not save file").build();
		}
		return Response.status(200)
				.entity("File saved to " + uploadedFileLocation).build();
	}
	
	private void saveToFile(InputStream inStream, String target)
			throws IOException {
		OutputStream out = null;
		int read = 0;
		byte[] bytes = new byte[1024];
		out = new FileOutputStream(new File(target));
		
		while ((read = inStream.read(bytes)) != -1) {
			out.write(bytes, 0, read);
		}
		
		out.flush();
		out.close();
		
		BufferedReader reader = new BufferedReader(new FileReader(target));
		String line = reader.readLine();
		
		String[] lineParts = new String[5];
		FileLog fileLog = new FileLog();
		
		while (line != null) {
			lineParts = line.split(" ");
			
			fileLog.setUrl(lineParts[0]);
			fileLog.setTimeStamp(Long.parseLong(lineParts[1]));
			fileLog.setId(lineParts[2]);
			fileLog.setRegion(Integer.parseInt(lineParts[3]));
			
			files.add(fileLog);
			
			line = reader.readLine();
			
			lineParts = null;
		}
		
		reader.close();
	}
	
	private void createFolderIfNotExists(String dirName)
			throws SecurityException {
		File theDir = new File(dirName);
		if (!theDir.exists()) {
			theDir.mkdir();
		}
	}

	public Response getMetrics(String metric, long timestamp, String type) {
		List<FileLog> topFiles = new ArrayList<FileLog>();
		switch (metric) {
		case "UW":
			topFiles = getTopAcessedAllWorld();
			break;
		case "UR":
			topFiles = getTopAcessedPerRegion();
			break;
		case "UL":
			topFiles = getLessAcessedAllWorld();
			break;
		case "DA":
			topFiles = getTopAcessedByPeriod(timestamp, type);
			break;	
		case "ML":
			topFiles = getLessAcessedMinute();
			break;
		default:
			break;
		}
		return Response.status(200)
				.entity(topFiles).build();
	}

	private List<FileLog> getLessAcessedMinute() {
		Comparator<FileLog> compareByPriod = Comparator
	            .comparing(FileLog::getTimeStamp).thenComparing(FileLog::getUrl);
		
		List<FileLog> sortedFileLogs = files.stream().sorted(compareByPriod).collect(Collectors.toList());
		
		List<FileLog> topFiles = new ArrayList<FileLog>();
		FileLog oldFile = null;
		int count = 0;
		
		for (FileLog fileLog : sortedFileLogs) {
			if (oldFile != null && oldFile.getTimeStamp() != fileLog.getTimeStamp()) {
				oldFile.setAmount(count);
				topFiles.add(oldFile);
				count = 0;
			}
			count++;
			oldFile = fileLog;
		} 
		
		topFiles.sort((FileLog f1, FileLog f2)->f1.getAmount().compareTo(f2.getAmount())); 
		
		return topFiles.subList(0, 3);
	}

	private List<FileLog> getTopAcessedByPeriod(long timestamp, String type) {
		Comparator<FileLog> compareByPriod = Comparator
	            .comparing(FileLog::getTimeStamp).thenComparing(FileLog::getUrl);
		
		List<FileLog> sortedFileLogs = files.stream().sorted(compareByPriod).collect(Collectors.toList());
		
		List<FileLog> topFiles = new ArrayList<FileLog>();
		FileLog oldFile = null;
		int count = 0;
		
		switch (type) {
		case "D":
			for (FileLog fileLog : sortedFileLogs) {
				if (oldFile != null && !oldFile.getDay().equals(fileLog.getDay())
						&& !oldFile.getWeek().equals(fileLog.getWeek())) {
					oldFile.setAmount(count);
					topFiles.add(oldFile);
					count = 0;
				}
				count++;
				oldFile = fileLog;
			} 
			break;
		case "W":
			for (FileLog fileLog : sortedFileLogs) {
				if (oldFile != null && !oldFile.getWeek().equals(fileLog.getWeek())) {
					oldFile.setAmount(count);
					topFiles.add(oldFile);
					count = 0;
				}
				count++;
				oldFile = fileLog;
			} 
			break;
		case "Y":
			for (FileLog fileLog : sortedFileLogs) {
				if (oldFile != null && !oldFile.getYear().equals(fileLog.getYear())) {
					oldFile.setAmount(count);
					topFiles.add(oldFile);
					count = 0;
				}
				count++;
				oldFile = fileLog;
			} 
			break;

		default:
			break;
		}
		
		topFiles.sort((FileLog f1, FileLog f2)->f1.getAmount().compareTo(f2.getAmount())); 
		
		return topFiles.subList(0, 3);
	}

	private List<FileLog> getLessAcessedAllWorld() {
		Comparator<FileLog> compareByRegion = Comparator
                .comparing(FileLog::getUrl);
		
		List<FileLog> sortedFileLogs = files.stream().sorted(compareByRegion).collect(Collectors.toList());
		
		List<FileLog> topFiles = new ArrayList<FileLog>();
		FileLog oldFile = null;
		int count = 0;
		
		for (FileLog fileLog : sortedFileLogs) {
			if (oldFile != null && !oldFile.getUrl().equals(fileLog.getUrl())) {
				oldFile.setAmount(count);
				topFiles.add(oldFile);
				count = 0;
			}
			count++;
			oldFile = fileLog;
		}
		
		topFiles.sort((FileLog f1, FileLog f2)->f1.getAmount().compareTo(f2.getAmount())); 
		
		return topFiles.subList(topFiles.size()-1, 1);
	}

	private List<FileLog> getTopAcessedPerRegion() {
		Comparator<FileLog> compareByRegion = Comparator
                .comparing(FileLog::getRegion)
                .thenComparing(FileLog::getUrl);
		
		List<FileLog> sortedFileLogs = files.stream().sorted(compareByRegion).collect(Collectors.toList());
		
		List<FileLog> topFiles = new ArrayList<FileLog>();
		FileLog oldFile = null;
		int count = 0;
		
		for (FileLog fileLog : sortedFileLogs) {
			if (oldFile != null && !oldFile.getUrl().equals(fileLog.getUrl())
					&& !oldFile.getRegion().equals(fileLog.getRegion())) {
				oldFile.setAmount(count);
				topFiles.add(oldFile);
				count = 0;
			}
			count++;
			oldFile = fileLog;
		}
		
		
		topFiles.sort((FileLog f1, FileLog f2)->f1.getAmount().compareTo(f2.getAmount())); 
		
		return topFiles.subList(0, 3);
	}

	private List<FileLog> getTopAcessedAllWorld() {
		Comparator<FileLog> compareByRegion = Comparator
                .comparing(FileLog::getUrl);
		
		List<FileLog> sortedFileLogs = files.stream().sorted(compareByRegion).collect(Collectors.toList());
		
		List<FileLog> topFiles = new ArrayList<FileLog>();
		FileLog oldFile = null;
		int count = 0;
		
		for (FileLog fileLog : sortedFileLogs) {
			if (oldFile != null && !oldFile.getUrl().equals(fileLog.getUrl())) {
				oldFile.setAmount(count);
				topFiles.add(oldFile);
				count = 0;
			}
			count++;
			oldFile = fileLog;
		}
		
		topFiles.sort((FileLog f1, FileLog f2)->f1.getAmount().compareTo(f2.getAmount())); 
		
		return topFiles.subList(0, 3);
	}
	

}
