import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.*;

public class InputFileParser {

	private LexerData data;

	/**
	 * Constructs a new {@link InputFileParser}.
	 * Use this constructor for testing the parser with data from a file.
	 *
	 * @param path file path
	 * @throws IOException
	 */
	public InputFileParser(Path path) throws IOException {
		Scanner sc = new Scanner(path);
		parse(sc);
	}

	/**
	 * Constructs a new {@link InputFileParser}
	 * User this constructor for testing the parser with data from System.in
	 *
	 * @param inStream input stream
	 */
	public InputFileParser(InputStream inStream) {
		Scanner sc = new Scanner(inStream);
		parse(sc);
	}

	public LexerData getData() {
		return data;
	}

	private void parse(Scanner sc) {
		boolean regDefsParsed = false;

		Map<String, String> regDefs = new HashMap<>();
		List<String> lexerStates = null;
		List<String> tokens = null;
		List<LexerRule> lexerRules = new ArrayList<>();

		while (sc.hasNextLine()) {
			String line = sc.nextLine().trim();

			if (line.isEmpty()) {
				continue;
			}

			if (line.startsWith("%X")) {
				regDefsParsed = true;
				line = line.substring(3, line.length());
				String[] names = line.split(" ");
				lexerStates = Arrays.asList(names);
				continue;
			}

			if (line.startsWith("%L")) {
				line = line.substring(3, line.length());
				String[] names = line.split(" ");
				tokens = Arrays.asList(names);
				continue;
			}


			if (!regDefsParsed) {
				String[] defAndEx = line.split(" ");
				regDefs.put(defAndEx[0], defAndEx[1]);
				continue;
			}

			if (line.startsWith("<")) {
				String stateName = null;
				for (String name : lexerStates) {
					if (line.contains(name)) {
						stateName = name;
						break;
					}
				}
				String regEx = line.replace("<" + stateName + ">", "");

				List<String> actionsArgs = new ArrayList<>();
				sc.nextLine(); // move to '{'

				while (true) {
					line = sc.nextLine().trim();
					if (line.equals("}")) {
						break;
					} else {
						actionsArgs.add(line);
					}
				}
				LexerRule rule = new LexerRule(stateName, regEx, actionsArgs);
				lexerRules.add(rule);
			}
		}
		convertAllRegDefsToRegex(regDefs, lexerRules);

		data = new LexerData(regDefs, lexerStates, tokens, lexerRules);
	}

	private void convertAllRegDefsToRegex(Map<String, String> regDefs, List<LexerRule> lexerRules) {
		for (String regDef : regDefs.keySet()) {
			String regEx = regDefs.get(regDef);
			for (String regDef2 : regDefs.keySet()) {
				if (regEx.contains(regDef2)) {
					regEx = regEx.replace(regDef2, "(" + regDefs.get(regDef2) + ")");
				}
			}
			regDefs.put(regDef, regEx);
		}

		for (LexerRule rule : lexerRules) {
			String regEx = rule.getRegEx();
			for (String regDef : regDefs.keySet()) {
				if (regEx.contains(regDef)) {
					regEx = regEx.replace(regDef, "(" + regDefs.get(regDef) + ")");
				}
			}
			rule.setRegEx(regEx);
		}
	}


}
