package lekser;

import java.util.LinkedList;
import java.util.Scanner;

import lekser.AutomataGenerator.AutomataList;

public class Lexer {
    private int currentLine;
    private String currentState;

    private String NOVI_REDAK;
    private String UDJI_U_STANJE;
    private String VRATI_SE;

    /*
    RuleList rules;
    AutomataList automata;

    public Lexer(RuleList rules, AutomataList automata) {
        this.currentLine = 1;
        this.NOVI_REDAK = "NOVI_REDAK";
        this.UDJI_U_STANJE = "UDJI_U_STANJE";
        this.VRATI_SE = "VRATI_SE";
        this.rules = rules;
        this.automata = automata;
    }
    */

    //todo add methods for parsing text files with rules

    public void simulateAutomata(LinkedList<Character[]> potentialTokens) {

    }

    public LinkedList<Character[]> groupPotentialTokens() {
        Scanner sc = new Scanner(System.in);
        LinkedList<Character[]> tokenList = new LinkedList<>();
        while(sc.hasNextLine()) {
            String line = sc.nextLine();
            line = line + "\n";
            char[] lineChar = line.toCharArray();
            LinkedList<Character> singleToken = new LinkedList<Character>();
            //populate the list of tokens to be sent to the automata
            for (int i = 0; i < lineChar.length; i++) { //iterate one line

                if(lineChar[i] == ' ' || lineChar[i] == '\t') {
                    if(i > 0 && (lineChar[i-1] != ' ' || lineChar[i-1] == '\t')) {
                        Character[] token;
                        token = new Character[singleToken.size()];
                        for (int j = 0; j < singleToken.size(); j++) {
                            token[j] = singleToken.get(j);
                        }
                        tokenList.add(token);
                        singleToken = new LinkedList<Character>();
                    }
                    continue;
                }
                singleToken.add(lineChar[i]);
            }
        }

        sc.close();
        return tokenList;
    }

    public void findNextAction() {
        //TODO: retrieve ActionList from current state and satisfied regexps

        for (String action : actionList) {
            String currentAction = action.trim();
            if (currentAction.equals("-")) {
                this.skip();
                continue;
            }

            if (!currentAction.contains(" ")) {
                this.addToUniformList(currentAction);
            } else {
                String[] parts = currentAction.split(" ");
                if (parts[0].equals(this.UDJI_U_STANJE)) {
                    this.changeState(parts[1]);
                } else {                        //nije udji_u_stanje nego vrati_se
                    this.getBack(Integer.parseInt(parts[1]));
                }
            }
        }
    }

    public void newLine() {
        this.currentLine += 1;
    }

    public void changeState (String newState) {
        this.currentState = newState;
    }

    public void skip() {}

    public void addToUniformList(String tokenClass) {
        //TODO: Add to list of uniform characters
    }

    public void getBack(int p){
        //TODO: placeholder my boi
    }
}