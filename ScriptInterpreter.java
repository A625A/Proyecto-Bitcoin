import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class ScriptInterpreter {

    public boolean execute(List<Token> tokens, ExecutionContext ctx) {
        return execute(tokens, ctx, false);
    }

    public boolean execute(List<Token> tokens, ExecutionContext ctx, boolean trace) {
        Deque<Boolean> ifStack = new ArrayDeque<>();
        boolean executing = true;

        for (Token token : tokens) {
            if (trace) {
                System.out.println("Token actual: " + token);
                System.out.println("Pila antes: " + ctx);
            }

            if (token instanceof OpcodeToken) {
                Opcode opcode = ((OpcodeToken) token).getOpcode();

                if (opcode == Opcode.OP_IF) {
                    if (executing) {
                        if (ctx.isEmpty()) {
                            throw new RuntimeException("Pila vacia en OP_IF");
                        }
                        ifStack.push(ExecutionContext.isTrue(ctx.pop()));
                    } else {
                        ifStack.push(false);
                    }
                    executing = !ifStack.contains(false);
                    if (trace) {
                        System.out.println("Pila despues: " + ctx);
                        System.out.println();
                    }
                    continue;
                }

                if (opcode == Opcode.OP_ELSE) {
                    if (ifStack.isEmpty()) {
                        throw new RuntimeException("Falta OP_IF para OP_ELSE");
                    }
                    ifStack.push(!ifStack.pop());
                    executing = !ifStack.contains(false);
                    if (trace) {
                        System.out.println("Pila despues: " + ctx);
                        System.out.println();
                    }
                    continue;
                }

                if (opcode == Opcode.OP_ENDIF) {
                    if (ifStack.isEmpty()) {
                        throw new RuntimeException("Falta OP_IF para OP_ENDIF");
                    }
                    ifStack.pop();
                    executing = !ifStack.contains(false);
                    if (trace) {
                        System.out.println("Pila despues: " + ctx);
                        System.out.println();
                    }
                    continue;
                }
            }

            if (!executing) {
                if (trace) {
                    System.out.println("Pila despues: " + ctx);
                    System.out.println();
                }
                continue;
            }

            token.execute(ctx);

            if (trace) {
                System.out.println("Pila despues: " + ctx);
                System.out.println();
            }
        }

        if (!ifStack.isEmpty()) {
            throw new RuntimeException("Falta cerrar OP_IF");
        }

        return !ctx.isEmpty() && ExecutionContext.isTrue(ctx.pop());
    }
}
