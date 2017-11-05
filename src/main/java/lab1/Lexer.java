package lab1;

import java.util.Scanner;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static lab1.Util.readSerFile;

public class Lexer {
	private int currentLine;
	private String currentState;

	// kao za konstante
	private final String NOVI_REDAK = "NOVI_REDAK";
	private final String UDJI_U_STANJE = "UDJI_U_STANJE";
	private final String VRATI_SE = "VRATI_SE";
	// vracam boolean tamo gdje provjeravam tako da moram negdje pohraniti
	// i int koji mi VRATI_SE predaje, tako da to cinim ovdje
	private int VRATI_PARAMETAR;

	private LexerData data;
	private List<ENKA> automata;

	public Lexer() throws IOException, ClassNotFoundException {
		this.currentLine = 1;
		this.data = readSerFile();

		List<String> states = data.getLexerStates();
		this.currentState = states.get(0);

		automata = spawnAutomata();

		this.VRATI_PARAMETAR = 0;

		//testPrint();
	}

	private void testPrint() {
		System.err.println(data.getLexerStates().toString());
		System.err.println(data.getTokens().toString());
	}

	public List<ENKA> spawnAutomata() {
		List<LexerRule> ruleList = data.getLexerRules();
		List<ENKA> automataList = new ArrayList<>();

		for (LexerRule rule : ruleList) {
			ENKA automat = new ENKA(rule);
			automataList.add(automat);
		}
		return automataList;
	}

	public void readInputHopefullyFixed() throws IOException {
		Scanner sc = new Scanner(System.in);
		LinkedList<Character[]> tokenList = new LinkedList<>();
		String wholeFuckingProgram = "";

		while (sc.hasNextLine()) {
			String line = sc.nextLine();
			wholeFuckingProgram += line + '\n';
		}

		char[] lineChar = wholeFuckingProgram.toCharArray();
		int len = lineChar.length;

		List<Character> candidate = new ArrayList<>();
		int safeSpot = 0;

		for (int i = 0; i < len; i++) {
			candidate.add(lineChar[i]);
			if (candidate.isEmpty()) {
				safeSpot = i;
				if (i == len) {
					break;
				}
				if (i < len) {
					candidate.add(lineChar[i]);
					continue;
				}
			}

			List<ENKA> tempAutomata = getActiveAutomata();
			List<Integer> flags = runAllAutomata(tempAutomata, candidate);

			if (flags.contains(1)) {
				continue;
			} else if (flags.contains(0)) {
				continue;
			} else {
				// finding last accepted candidate
				List<Character> tempCandidate = new ArrayList<>(candidate);
				int goBack = 0;

				while (true) {
					if (tempCandidate.size() < 2) {
						break;
					}
					goBack++;

					tempCandidate.remove(tempCandidate.size() - 1);
					if (!runAllAutomata(tempAutomata, tempCandidate).contains(1)) {
						continue;
					} else {
						break;
					}
				} // we have a tempCandidate that was accepted

				if (!runAllAutomata(tempAutomata, tempCandidate).contains(1)) {
					System.err.println("Provodi se postupak oporavka od pogreske.");
					candidate.remove(0);
					continue;
				} else {
					boolean changeInner = findActions(hyperbruh(tempCandidate));
					//safeSpot = i - goBack - 1;
					safeSpot = i - (candidate.size()-goBack); 
					i -= goBack;

					if (changeInner) {
						i = safeSpot + VRATI_PARAMETAR - 1;
						candidate.clear();
						continue;
					}
					candidate.clear();
				}
			}

		}
		while (!candidate.isEmpty()) {
			List<Character> safeCandidate = new ArrayList<>(candidate);

			List<ENKA> tempAutomata = getActiveAutomata();
			List<Integer> flags = runAllAutomata(tempAutomata, candidate);

			if (flags.contains(1)) {
				boolean changeInner = findActions(hyperbruh(candidate));

				if (changeInner) {

					for (int j = 0; j < VRATI_PARAMETAR; j++) {
						safeCandidate.remove(0);
					}
					candidate.clear();
					candidate = new ArrayList<>(safeCandidate);
					continue;
				}
				candidate.clear();
			} else {
				List<Character> tempCandidate = new ArrayList<>(candidate);
				int goBack = 0;

				while (true) {
					if (tempCandidate.size() == 0 || tempCandidate.size() == 1)
						break;
					goBack++;
					tempCandidate.remove(tempCandidate.size() - 1);
					if (runAllAutomata(tempAutomata, tempCandidate).contains(1)) {
						break;
					} else {
						continue;
					}
				}
				if (runAllAutomata(tempAutomata, tempCandidate).contains(1)) {
					boolean changeInner = findActions(hyperbruh(tempCandidate));
					if (changeInner) {
						for (int j = 0; j < VRATI_PARAMETAR; j++) {
							safeCandidate.remove(0);
						}
						candidate.clear();
						candidate = new ArrayList<>(safeCandidate);
						continue;
					}
					for (int i = 0; i < tempCandidate.size(); i++) {
						candidate.remove(0);
					}
					continue;
				} else {
					System.err.println("Provodi se postupak oporavka od pogreske.");
					candidate.remove(0);
					continue;
				}

			}
		}
		sc.close();
	}

	private List<ENKA> getActiveAutomata() {
		List<ENKA> tempAutomata = new ArrayList<>();
		for (ENKA autom : automata) {
			if (autom.getStateName().equals(currentState)) {
				tempAutomata.add(autom);
			}
		}
		return tempAutomata;
	}

	private List<Integer> runAllAutomata(List<ENKA> tempAutomata, List<Character> candidate) {
		List<Integer> results = new ArrayList<>();

		StringBuilder sb = new StringBuilder();
		for (char c : candidate) {
			sb.append(c);
		}
		String currentString = sb.toString();

		for (ENKA automaton : tempAutomata) {
			results.add(automaton.run(currentString));
		}
		return results;
	}

	public String hyperbruh(List<Character> forsen) {
		StringBuilder sb = new StringBuilder();

		for (char c : forsen) {
			sb.append(c);
		}

		return sb.toString();
	}

	public boolean findActions(String input) {
		List<String> actionList = new ArrayList<>();
		String satisfiedRegex = "";

		for (ENKA automaton : automata) {
			if (automaton.getStateName().equals(currentState)) {

				// System.err.println(input);
				// System.err.println(currentState);

				int var = automaton.run(input);

				if (var == 1) {
					actionList = automaton.getActionArgs();
					satisfiedRegex = automaton.getRegEx();

					// System.err.println("Usao u drugi za input: " + input);
					if (!actionList.isEmpty()) {
						// System.err.println("Postoje akcije za: " + input + "
						// " + actionList.toString());
					}

					break;
				}
			}
		}

		if (actionList == null || actionList.isEmpty()) {
			System.err.println("Something wrong with my actions.");
		}

		boolean returnFlag = false;
		boolean hasVrati = false;
		int vratiPar = 0;

		for (String action : actionList) {
			String currentAction = action.trim();
			if (currentAction.contains("VRATI_SE")) {
				hasVrati = true;
				vratiPar = Integer.parseInt(currentAction.split(" ")[1]);
			}
		}

		for (String action : actionList) {
			String currentAction = action.trim();
			if (currentAction.equals("-")) {
				this.skip();
				continue;
			}

			if (!currentAction.contains(" ") && data.getTokens().contains(currentAction)
					&& !currentAction.equals(NOVI_REDAK)) {
				if (hasVrati) {
					this.addToUniformList(currentAction, input, vratiPar);
				} else {
					this.addToUniformList(currentAction, input, 0);
				}

			} else if (currentAction.equals(NOVI_REDAK)) {
				currentLine++;
			} else {
				String[] parts = currentAction.split(" ");
				if (parts[0].equals(this.UDJI_U_STANJE)) {
					this.changeState(parts[1]);
				} else { // nije udji_u_stanje nego vrati_se
					returnFlag = true;
					VRATI_PARAMETAR = Integer.parseInt(parts[1]);
				}
			}
		}
		return returnFlag;
	}

	private void skip() {
		// do nothing
	}

	private void addToUniformList(String outString, String input, int length) {
		StringBuilder sb;
		sb = new StringBuilder(outString).append(" ");

		sb.append(String.valueOf(currentLine)).append(" ");
		if (length < 1) {
			sb.append(input);
		} else {
			sb.append(input.substring(0, length));
		}
		System.out.println(sb.toString());
	}

	private void changeState(String newState) {
		// System.err.println("Mijenjam stanje iz " + currentState + " u " +
		// newState);
		this.currentState = newState;
	}
}