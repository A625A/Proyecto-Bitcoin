import java.util.List;

public class Main {

    public static void main(String[] args) {

        // --- P2PKH Example ---

        byte[] pubKey = Hex.hexToBytes("02AABBCCDDEEFF00112233445566778899AABBCCDDEEFF00112233445566778899");
        byte[] pubKeyHash = CryptoMock.hash160(pubKey);
        byte[] signature = CryptoMock.sha256(pubKey);

        String script =
                "0x" + Hex.bytesToHex(signature) + " " +
                "0x" + Hex.bytesToHex(pubKey) + " " +
                "OP_DUP OP_HASH160 " +
                "0x" + Hex.bytesToHex(pubKeyHash) + " " +
                "OP_EQUALVERIFY OP_CHECKSIG";

        List<Token> tokens = new ScriptParser().parse(script);

        ExecutionContext ctx = new ExecutionContext();
        ScriptInterpreter interpreter = new ScriptInterpreter();

        boolean result = interpreter.execute(tokens, ctx);

        System.out.println(result ? "VALID" : "INVALID");
    }
}