import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class ScriptInterpreter {

    public boolean execute(List<Token> tokens, ExecutionContext ctx) {
        Deque<Boolean> ifStack = new ArrayDeque<>();
        boolean executing = true;

        for (Token token : tokens) {
            if (token instanceof OpcodeToken) {
                Opcode opcode = ((OpcodeToken) token).getOpcode();

                if (opcode == Opcode.OP_IF) {
                    if (executing) {
                        if (ctx.isEmpty()) {
                            throw new RuntimeException("Empty stack on OP_IF");
                        }
                        ifStack.push(ExecutionContext.isTrue(ctx.pop()));
                    } else {
                        ifStack.push(false);
                    }
                    executing = !ifStack.contains(false);
                    continue;
                }

                if (opcode == Opcode.OP_ELSE) {
                    if (ifStack.isEmpty()) {
                        throw new RuntimeException("Empty stack on OP_ELSE");
                    }
                    ifStack.push(!ifStack.pop());
                    executing = !ifStack.contains(false);
                    continue;
                }

                if (opcode == Opcode.OP_ENDIF) {
                    if (ifStack.isEmpty()) {
                        throw new RuntimeException("Empty stack on OP_ENDIF");
                    }
                    ifStack.pop();
                    executing = !ifStack.contains(false);
                    continue;
                }
            }

            if (!executing) {
                continue;
            }

            token.execute(ctx);
        }

        if (!ifStack.isEmpty()) {
            throw new RuntimeException("Unclosed OP_IF block");
        }

        return !ctx.isEmpty() && ExecutionContext.isTrue(ctx.pop());
    }
}
