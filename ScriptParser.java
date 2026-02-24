import java.util.ArrayList;
import java.util.List;

public class ScriptParser {

    public List<Token> parse(String script) {

        String[] parts = script.split(" ");
        List<Token> tokens = new ArrayList<>();

        for (String p : parts) {

            if (p.startsWith("0x")) {
                tokens.add(new PushDataToken(Hex.hexToBytes(p.substring(2))));
            }

            else if (p.startsWith("OP_")) {
                tokens.add(new OpcodeToken(Opcode.valueOf(p)));
            }

            else {
                throw new RuntimeException("Invalid token");
            }
        }

        return tokens;
    }
}