public class PushDataToken implements Token {

    private final byte[] data;

    public PushDataToken(byte[] data) {
        this.data = data;
    }

    public void execute(ExecutionContext ctx) {
        ctx.push(data);
    }
}