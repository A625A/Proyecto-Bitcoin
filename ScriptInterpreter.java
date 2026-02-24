import java.util.List;

public class ScriptInterpreter {

    public boolean execute(List<Token> tokens, ExecutionContext ctx) {
        for (Token token : tokens) {
            token.execute(ctx);
        }
        return !ctx.isEmpty() && ExecutionContext.isTrue(ctx.pop());
    }
    
}