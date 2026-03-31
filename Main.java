import java.util.List;

public class Main {

    public static void main(String[] args) {
        boolean trace = false;

        for (String arg : args) {
            if (arg.equals("--trace")) {
                trace = true;
            }
        }

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

        boolean result = interpreter.execute(tokens, ctx, trace);

        System.out.println(result ? "Valido" : "No valido");

        byte[] pubKey2 = Hex.hexToBytes("02AABBCCDDEEFF00112233445566778899AABBCCDDEEFF00112233445566778899");
        byte[] pubKeyHash2 = CryptoMock.hash160(pubKey2);
        byte[] signature2 = CryptoMock.sha256(pubKey2);

        String script2 =
                "0x" + Hex.bytesToHex(signature2) + " " +
                "0x" + Hex.bytesToHex(pubKey2) + " " +
                "OP_DUP OP_DUP OP_HASH160 " +
                "0x" + Hex.bytesToHex(pubKeyHash2) + " " +
                "OP_EQUALVERIFY OP_CHECKSIG";

        List<Token> tokens2 = new ScriptParser().parse(script2);

        boolean result2 = interpreter.execute(tokens2, ctx, trace);

        System.out.println(result2 ? "Valido" : "No valido");
    }
}
