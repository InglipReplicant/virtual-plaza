package lab1;

import java.io.IOException;

public class LA {
    public static void main(String[] args) throws IOException, ClassNotFoundException{
        Lexer lex = new Lexer();
        lex.readInputHopefullyFixed();
    }
}