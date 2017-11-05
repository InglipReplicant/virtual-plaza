package lab1;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Contains data for the lexer.
 * This class should also be in the directory 'analizator'
 */
public class LexerData implements Serializable {
	/**
	 * Map of regular definitions.
	 * regularDefName -> regEx
	 */
	private Map<String, String> regDefs;
	/**
	 * List of lexer states.
	 */
	private List<String> lexerStates;
	/**
	 * List of tokens
	 */
	private List<String> tokens;

	/**
	 * List of lexer actionArgs represented by class {@link LexerRule}
	 */
	private List<LexerRule> lexerRules;

	public LexerData(Map<String, String> regDefs, List<String> lexerStates, List<String> tokens, List<LexerRule> lexerRules) {
		this.regDefs = regDefs;
		this.lexerStates = lexerStates;
		this.tokens = tokens;
		this.lexerRules = lexerRules;
	}

	public Map<String, String> getRegDefs() {
		return regDefs;
	}

	public void setRegDefs(Map<String, String> regDefs) {
		this.regDefs = regDefs;
	}

	public List<String> getLexerStates() {
		return lexerStates;
	}

	public void setLexerStates(List<String> lexerStates) {
		this.lexerStates = lexerStates;
	}

	public List<String> getTokens() {
		return tokens;
	}

	public void setTokens(List<String> tokens) {
		this.tokens = tokens;
	}

	public List<LexerRule> getLexerRules() {
		return lexerRules;
	}

	public void setLexerRules(List<LexerRule> lexerRules) {
		this.lexerRules = lexerRules;
	}
}
