package lab1;

import java.util.LinkedList;
import java.util.Scanner;

public class Privremeni {
    public void readInput() {

        Scanner sc = new Scanner(System.in);
        LinkedList<Character[]> tokenList = new LinkedList<>();

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            line = line + "\n";
            char[] lineChar = line.toCharArray();
            int len = lineChar.length;


        }
    }
}

    /*public LinkedList<Character[]> groupPotentialTokens() {
        Scanner sc = new Scanner(System.in);
        LinkedList<Character[]> tokenList = new LinkedList<>();

        while(sc.hasNextLine()) {
            String line = sc.nextLine();
            line = line + "\n";
            char[] lineChar = line.toCharArray();
            LinkedList<Character> singleToken = new LinkedList<Character>();

            System.out.println(line);
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
    */