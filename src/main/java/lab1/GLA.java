package lab1;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * To test with file, give the program an argument which is a file name (not the absolute path) of a file
 * that is located at src/main/resources
 */
public class GLA {

	public static void main(String[] args) throws IOException, URISyntaxException {
		Path filePath = Paths.get(GLA.class.getClassLoader().getResource(args[0]).toURI());
		InputFileParser parser = new InputFileParser(filePath);

		// Used for getting the input data from the standard input
//		InputFileParser parser = new InputFileParser(System.in);

		// Creates a serialized object LexerData
		// TODO: Need code for creating the directory 'analizator' if not created manually
		try (FileOutputStream fileOut = new FileOutputStream("lexerdata.ser"); //TODO:VRATI analizator/ ISPRED lexerdata.ser
			 ObjectOutputStream out = new ObjectOutputStream(fileOut);
		){
			out.writeObject(parser.getData());
		}
	}
}
