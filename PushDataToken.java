public class PushDataToken implements Token {

    private final byte[] data;

    public PushDataToken(byte[] data) {
        this.data = data;
    }

    public void execute(ExecutionContext ctx) {
        ctx.push(data);
    }
        @Override
    public String toString() {
        return "0x" + Hex.bytesToHex(data);
    }

}