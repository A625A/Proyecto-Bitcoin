public class OpcodeToken implements Token {

    private final Opcode opcode;

    public OpcodeToken(Opcode opcode) {
        this.opcode = opcode;
    }

    public Opcode getOpcode() {
        return opcode;
    }

    public void execute(ExecutionContext ctx) {

        switch (opcode) {

            case OP_0:
                ctx.push(new byte[]{0});
                break;

            case OP_1:
                ctx.push(new byte[]{1});
                break;

            case OP_2:
                ctx.push(new byte[]{2});
                break;

            case OP_3:
                ctx.push(new byte[]{3});
                break;

            case OP_4:
                ctx.push(new byte[]{4});
                break;

            case OP_5:
                ctx.push(new byte[]{5});
                break;

            case OP_6:
                ctx.push(new byte[]{6});
                break;

            case OP_7:
                ctx.push(new byte[]{7});
                break;

            case OP_8:
                ctx.push(new byte[]{8});
                break;

            case OP_9:
                ctx.push(new byte[]{9});
                break;

            case OP_10:
                ctx.push(new byte[]{10});
                break;

            case OP_11:
                ctx.push(new byte[]{11});
                break;

            case OP_12:
                ctx.push(new byte[]{12});
                break;

            case OP_13:
                ctx.push(new byte[]{13});
                break;

            case OP_14:
                ctx.push(new byte[]{14});
                break;

            case OP_15:
                ctx.push(new byte[]{15});
                break;

            case OP_16:
                ctx.push(new byte[]{16});
                break;

            case OP_DUP:
                byte[] top = ctx.pop();
                ctx.push(top);
                ctx.push(top);
                break;

            case OP_DROP:
                ctx.pop();
                break;

            case OP_EQUAL:
                byte[] a = ctx.pop();
                byte[] b = ctx.pop();
                ctx.push(java.util.Arrays.equals(a, b) ? new byte[]{1} : new byte[]{0});
                break;

            case OP_EQUALVERIFY:
                byte[] x = ctx.pop();
                byte[] y = ctx.pop();
                if (!java.util.Arrays.equals(x, y))
                    throw new RuntimeException("OP_EQUALVERIFY fallo");
                break;

            case OP_HASH160:
                byte[] data = ctx.pop();
                ctx.push(CryptoMock.hash160(data));
                break;

            case OP_CHECKSIG:
                byte[] pubKey = ctx.pop();
                byte[] sig = ctx.pop();
                boolean ok = java.util.Arrays.equals(sig, CryptoMock.sha256(pubKey));
                ctx.push(ok ? new byte[]{1} : new byte[]{0});
                break;

            case OP_IF:
            case OP_ELSE:
            case OP_ENDIF:
                throw new RuntimeException("Estos opcodes los maneja ScriptInterpreter");
        }
    }

    @Override
    public String toString() {
        return opcode.toString();
    }
}
