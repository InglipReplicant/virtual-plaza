import java.util.*;

public class ENKA {

	private int id;

	private String stateName;

	private String regEx;

	private List<String> actionArgs;

	private Set<ENKAState> eNKAstates = new HashSet<>();

	private int nENKAStates = 0;

	private static int ENKAN = 0;

	private ENKAState[] startAcceptState;

	List<ENKAState> activeStates = new ArrayList<>();

	/**
	 * Empty constructor. Only for testing purposes.
	 */
	public ENKA() {
	}

	public ENKA(LexerRule lexR) {
		this.stateName = lexR.getStateName();
		this.regEx = lexR.getRegEx();
		this.actionArgs = lexR.getActionArgs();

		startAcceptState = convert(regEx);
	}

	public List<String> getActionArgs() { return actionArgs; }


	public String getRegEx() { return regEx; }


	public String getStateName() {
		return stateName;
	}


	public int run(String input) {
		char[] inputArray = input.toCharArray();

		for (ENKAState enk : eNKAstates) {
			enk.triggered = false;
		}

		activeStates.clear();
		startAcceptState[0].checkedTransition();

		for (ENKAState enk : eNKAstates) {
			if (enk.triggered && !activeStates.contains(enk)) {
				activeStates.add(enk);
			}
		}

		for (char c : inputArray) {
			Set<ENKAState> nextActive = new HashSet<>();
			for (ENKAState enk : activeStates) {
				nextActive.addAll(enk.step(Character.toString(c)));
			}

			activeStates.clear();
			activeStates.addAll(nextActive);
			nextActive.clear();

			if (activeStates.isEmpty()) {
				return -1;
			}

			for (ENKAState enk : eNKAstates) {
				enk.triggered = activeStates.contains(enk);
			}

			for (ENKAState enk : activeStates) {
				enk.transition();
			}

			for (ENKAState enk : eNKAstates) {
				if (enk.triggered) {
					activeStates.add(enk);
				}
			}
		}

		return startAcceptState[1].triggered ? 1 : 0;
	}

	
	private ENKAState[] convert(String subRegEx) {
		int bracketNum = 0;

		List<String> options = new ArrayList<>();
		int startExp = 0;

		for (int i = 0; i < subRegEx.length(); i++) {
			if (subRegEx.charAt(i) == '(' && isOperator(subRegEx, i)) {
				bracketNum++;
			} else if (subRegEx.charAt(i) == ')' && isOperator(subRegEx, i)) {
				bracketNum--;
			} else if (bracketNum == 0 && subRegEx.charAt(i) == '|' && isOperator(subRegEx, i)) {


				options.add(subRegEx.substring(startExp, i));
				startExp = i + 1;

			}
		}

		options.add(subRegEx.substring(startExp, subRegEx.length()));

		//a 2 slot array representing lijevo_stanje and desno_stanje respectively
		ENKAState[] states = new ENKAState[] {
			  new ENKAState(), new ENKAState()
		};

		if (options.size() > 1) {
			for (int i = 0; i < options.size(); i++) {
				ENKAState[] buf = convert(options.get(i));
				states[0].addEpsCrossing(buf[0]);
				buf[1].addEpsCrossing(states[1]);
			}
		}

		boolean prefixed = false;

		ENKAState lastState = states[0];

		for (int i = 0; i < subRegEx.length(); i++) {
			ENKAState a = null;
			ENKAState b = null;
			if (prefixed) {
				prefixed = false;
				char buf;
				//HARDKODIRANO, OVO TREBA PROMIJENITI
				if (subRegEx.charAt(i) == 't') {
					buf = '\t';
				} else if (subRegEx.charAt(i) == 'n') {
					buf ='\n';
				} else if (subRegEx.charAt(i) == '_') {
					buf = ' ';
				} else {
					buf = subRegEx.charAt(i);
				}

				a = new ENKAState();
				b = new ENKAState();
				a.addChangeState(Character.toString(buf), b);
			} else {
				if (subRegEx.charAt(i) == '\\') {
					prefixed = true;
					continue;
				}
				if (subRegEx.charAt(i) != '(') {
					a = new ENKAState();
					b = new ENKAState();
					if (subRegEx.charAt(i) == '$') {
						a.addEpsCrossing(b);
					} else {
						a.addChangeState(Character.toString(subRegEx.charAt(i)), b);
					}
				} else {
					int j = findClosedBracket(subRegEx, i);
					ENKAState[] buf = convert(subRegEx.substring(i + 1, j));
					a = buf[0];
					b = buf[1];
					i = j;
				}
			}

			if (i + 1 < subRegEx.length() && subRegEx.charAt(i + 1) == '*') {
				ENKAState x = a;
				ENKAState y = b;
				a = new ENKAState();
				b = new ENKAState();
				a.addEpsCrossing(x);
				y.addEpsCrossing(b);
				a.addEpsCrossing(b);
				y.addEpsCrossing(x);
				i++;
			}

			lastState.addEpsCrossing(a);
			lastState = b;
		}

		lastState.addEpsCrossing(states[1]);

		return states;
	}


	private static boolean isOperator(String s, int index) {
		int br = 0;
		while(index - 1 >= 0 && s.charAt(index - 1) == '\\') {
			br++;
			index--;
		}

		return br % 2 == 0;
	}


	private static int findClosedBracket(String s, int startIndex) {
		int numOfOpenBrackets = 0;

		while (startIndex < s.length()) {
			if (s.charAt(startIndex) == '(' && isOperator(s, startIndex)) {
				numOfOpenBrackets++;
			} else if (s.charAt(startIndex) == ')' && isOperator(s, startIndex)) {
				if (numOfOpenBrackets == 1) {
					return startIndex;
				} else {
					numOfOpenBrackets--;
				}
			}

			startIndex++;
		}

		throw new RuntimeException("No closed bracket ')' was found");
	}


	private class ENKAState {

		private Map<String, Set<ENKAState>> transitions = new HashMap<>();

		private Set<ENKAState> epsTransitions = new HashSet<>();

		private int id;

		private boolean triggered;

		private ENKAState() {
			this.id = nENKAStates++;
			eNKAstates.add(this);
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			ENKAState enkaState = (ENKAState) o;

			return id == enkaState.id;
		}

		@Override
		public int hashCode() {
			return id;
		}


		public void addChangeState(String s, Set<ENKAState> st) {
			transitions.merge(s, new HashSet<>(st), (stanjes, stanjes2) -> {
				stanjes.addAll(st);
				return stanjes;
			});
		}


		private void addChangeState(String s, ENKAState st) {
			addChangeState(s, Collections.singleton(st));
		}


		private void addEpsCrossing(ENKAState st) {
			epsTransitions.add(st);
		}


		public void addEpsCrossing(Set<ENKAState> st) {
			epsTransitions.addAll(st);
		}


		public Set<ENKAState> step(String s) {
			triggered = false;

			Set<ENKAState> buff = transitions.get(s);
			if (buff == null) {
				return Collections.emptySet();
			}

			return buff;
		}


		private void checkedTransition() {
			if (triggered) {
				return;
			}

			triggered = true;
			transition();
		}


		private void transition() {
			for (ENKAState st : epsTransitions) {
				st.checkedTransition();
			}
		}
	}
}
