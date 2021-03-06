import java.util.List;
import java.io.Serializable;

public class LexerRule implements Serializable {
        private String stateName;
        private String regEx;
        private List<String> actionArgs;

        public static final long serialVersionUID = 42L;

        public LexerRule(String stateName, String regEx, List<String> actionArgs) {
            this.stateName = stateName;
            this.regEx = regEx;
            this.actionArgs = actionArgs;
        }

        public String getStateName() {
            return stateName;
        }

        public String getRegEx() {
            return regEx;
        }

        public List<String> getActionArgs() {
            return actionArgs;
        }

        public void setRegEx(String regEx) {
            this.regEx = regEx;
        }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('<').append(stateName).append('>').append(regEx).append('\n');
        sb.append(actionArgs);
        return sb.toString();
    }
}