import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class Util {

	/**
	 * Used for deserialization of file 'lexerdata.ser'.
	 *
	 * @return object {@link lekser.LexerData} with all the data from the lexer specification
	 * @throws IOException if file was not found or readable
	 * @throws ClassNotFoundException if class LexerData was not found
	 */
	public static LexerData readSerFile() throws IOException, ClassNotFoundException {
		LexerData data;
		try (FileInputStream fileIn = new FileInputStream("lexerdata.ser"); //TODO: vrati analizator/ il kajvec
			 ObjectInputStream in = new ObjectInputStream(fileIn)
		) {
			data = (LexerData) in.readObject();
		}
		return data;
	}
}
