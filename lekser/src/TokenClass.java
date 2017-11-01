package lekser;

public class TokenClass {
    private String className;
    private int lineNumber;
    private int index;

    public void TokenClass(String name, int line, int index) {
        this.className = name;
        this.lineNumber = line;
        this.index = index;
    }

}
