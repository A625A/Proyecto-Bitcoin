import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Script Interpreter Tests")
public class ScriptInterpreterTest {

    private ScriptInterpreter interpreter;
    private ExecutionContext ctx;

    @BeforeEach
    public void setUp() {
        interpreter = new ScriptInterpreter();
        ctx = new ExecutionContext();
    }

    @Test
    @DisplayName("execute empty script should return false")
    public void testEmptyScript() {
        List<Token> tokens = Arrays.asList();
        boolean result = interpreter.execute(tokens, ctx);
        assertFalse(result);
    }

    @Test
    @DisplayName("execute single push with true value should return true")
    public void testSinglePushTrue() {
        List<Token> tokens = Arrays.asList(
            new OpcodeToken(Opcode.OP_1)
        );
        boolean result = interpreter.execute(tokens, ctx);
        assertTrue(result);
    }

    @Test
    @DisplayName("execute single push with false value should return false")
    public void testSinglePushFalse() {
        List<Token> tokens = Arrays.asList(
            new OpcodeToken(Opcode.OP_0)
        );
        boolean result = interpreter.execute(tokens, ctx);
        assertFalse(result);
    }

    @Test
    @DisplayName("OP_IF with true condition should execute then-block")
    public void testOPIfTrue() {
        List<Token> tokens = Arrays.asList(
            new OpcodeToken(Opcode.OP_1),       
            new OpcodeToken(Opcode.OP_IF),      
            new OpcodeToken(Opcode.OP_5),       
            new OpcodeToken(Opcode.OP_ENDIF)
        );
        boolean result = interpreter.execute(tokens, ctx);
        assertTrue(result);
    }

    @Test
    @DisplayName("OP_IF with false condition should skip then-block")
    public void testOPIfFalse() {
        List<Token> tokens = Arrays.asList(
            new OpcodeToken(Opcode.OP_0),       
            new OpcodeToken(Opcode.OP_IF),      
            new OpcodeToken(Opcode.OP_5),       
            new OpcodeToken(Opcode.OP_ENDIF)
        );
        boolean result = interpreter.execute(tokens, ctx);
        assertFalse(result);
    }

    @Test
    @DisplayName("OP_IF with empty stack should throw exception")
    public void testOPIfEmptyStackThrowsException() {
        List<Token> tokens = Arrays.asList(
            new OpcodeToken(Opcode.OP_IF),
            new OpcodeToken(Opcode.OP_1),
            new OpcodeToken(Opcode.OP_ENDIF)
        );
        assertThrows(RuntimeException.class, () -> interpreter.execute(tokens, ctx),
            "Should throw RuntimeException when OP_IF executed on empty stack");
    }

    @Test
    @DisplayName("OP_IF-ELSE with true condition should execute then-block")
    public void testOPIfElseTrue() {
        List<Token> tokens = Arrays.asList(
            new OpcodeToken(Opcode.OP_1),       
            new OpcodeToken(Opcode.OP_IF),      
            new OpcodeToken(Opcode.OP_5),       
            new OpcodeToken(Opcode.OP_ELSE),
            new OpcodeToken(Opcode.OP_10),      
            new OpcodeToken(Opcode.OP_ENDIF)
        );
        boolean result = interpreter.execute(tokens, ctx);
        assertTrue(result); 
    }

    @Test
    @DisplayName("OP_IF-ELSE with false condition should execute else-block")
    public void testOPIfElseFalse() {
        List<Token> tokens = Arrays.asList(
            new OpcodeToken(Opcode.OP_0),       
            new OpcodeToken(Opcode.OP_IF),      
            new OpcodeToken(Opcode.OP_5),      
            new OpcodeToken(Opcode.OP_ELSE),
            new OpcodeToken(Opcode.OP_10),      
            new OpcodeToken(Opcode.OP_ENDIF)
        );
        boolean result = interpreter.execute(tokens, ctx);
        assertTrue(result); 
    }

    @Test
    @DisplayName("OP_ELSE without matching OP_IF should throw exception")
    public void testOPElseWithoutIfThrowsException() {
        List<Token> tokens = Arrays.asList(
            new OpcodeToken(Opcode.OP_1),
            new OpcodeToken(Opcode.OP_ELSE),   
            new OpcodeToken(Opcode.OP_2)
        );
        assertThrows(RuntimeException.class, () -> interpreter.execute(tokens, ctx),
            "Should throw RuntimeException when OP_ELSE has no matching OP_IF");
    }

   

    @Test
    @DisplayName("OP_ENDIF without matching OP_IF should throw exception")
    public void testOPEndifWithoutIfThrowsException() {
        List<Token> tokens = Arrays.asList(
            new OpcodeToken(Opcode.OP_1),
            new OpcodeToken(Opcode.OP_ENDIF)    
        );
        assertThrows(RuntimeException.class, () -> interpreter.execute(tokens, ctx),
            "Should throw RuntimeException when OP_ENDIF has no matching OP_IF");
    }

    @Test
    @DisplayName("unclosed OP_IF should throw exception")
    public void testUnclosedOPIfThrowsException() {
        List<Token> tokens = Arrays.asList(
            new OpcodeToken(Opcode.OP_1),
            new OpcodeToken(Opcode.OP_IF),
            new OpcodeToken(Opcode.OP_5)
            
        );
        assertThrows(RuntimeException.class, () -> interpreter.execute(tokens, ctx),
            "Should throw RuntimeException for unclosed OP_IF");
    }

   

    @Test
    @DisplayName("nested OP_IF with both true should execute innermost code")
    public void testNestedOPIfBothTrue() {
        List<Token> tokens = Arrays.asList(
            new OpcodeToken(Opcode.OP_1),
            new OpcodeToken(Opcode.OP_IF),
            new OpcodeToken(Opcode.OP_1),
            new OpcodeToken(Opcode.OP_IF),
            new OpcodeToken(Opcode.OP_7),
            new OpcodeToken(Opcode.OP_ENDIF),
            new OpcodeToken(Opcode.OP_ENDIF)
        );
        boolean result = interpreter.execute(tokens, ctx);
        assertTrue(result);
    }

    @Test
    @DisplayName("nested OP_IF with outer false should skip all inner code")
    public void testNestedOPIfOuterFalse() {
        List<Token> tokens = Arrays.asList(
            new OpcodeToken(Opcode.OP_0),
            new OpcodeToken(Opcode.OP_IF),
            new OpcodeToken(Opcode.OP_1),
            new OpcodeToken(Opcode.OP_IF),
            new OpcodeToken(Opcode.OP_7),
            new OpcodeToken(Opcode.OP_ENDIF),
            new OpcodeToken(Opcode.OP_ENDIF)
        );
        boolean result = interpreter.execute(tokens, ctx);
        assertFalse(result);
    }

    @Test
    @DisplayName("nested OP_IF with inner false should not execute innermost code")
    public void testNestedOPIfInnerFalse() {
        List<Token> tokens = Arrays.asList(
            new OpcodeToken(Opcode.OP_1),
            new OpcodeToken(Opcode.OP_IF),
            new OpcodeToken(Opcode.OP_0),
            new OpcodeToken(Opcode.OP_IF),
            new OpcodeToken(Opcode.OP_7),
            new OpcodeToken(Opcode.OP_ENDIF),
            new OpcodeToken(Opcode.OP_ENDIF)
        );
        boolean result = interpreter.execute(tokens, ctx);
        assertFalse(result);
    }

    @Test
    @DisplayName("IF-ELSE-IF structure should work correctly")
    public void testIfElseIfStructure() {
        List<Token> tokens = Arrays.asList(
            new OpcodeToken(Opcode.OP_0),
            new OpcodeToken(Opcode.OP_IF),
            new OpcodeToken(Opcode.OP_1),
            new OpcodeToken(Opcode.OP_ELSE),
            new OpcodeToken(Opcode.OP_1),
            new OpcodeToken(Opcode.OP_IF),
            new OpcodeToken(Opcode.OP_9),
            new OpcodeToken(Opcode.OP_ENDIF),
            new OpcodeToken(Opcode.OP_ENDIF)
        );
        boolean result = interpreter.execute(tokens, ctx);
        assertTrue(result);
    }

    @Test
    @DisplayName("executing without final value should return false")
    public void testNoFinalValue() {
        List<Token> tokens = Arrays.asList(
            new OpcodeToken(Opcode.OP_5),
            new OpcodeToken(Opcode.OP_DROP)
        );
        boolean result = interpreter.execute(tokens, ctx);
        assertFalse(result);
    }

    @Test
    @DisplayName("OP_DUP on empty stack should throw exception")
    public void testOPDupEmptyStackInScript() {
        List<Token> tokens = Arrays.asList(
            new OpcodeToken(Opcode.OP_DUP)
        );
        assertThrows(RuntimeException.class, () -> interpreter.execute(tokens, ctx),
            "Should throw RuntimeException when OP_DUP executed on empty stack");
    }

    @Test
    @DisplayName("OP_DROP on empty stack should throw exception")
    public void testOPDropEmptyStackInScript() {
        List<Token> tokens = Arrays.asList(
            new OpcodeToken(Opcode.OP_DROP)
        );
        assertThrows(RuntimeException.class, () -> interpreter.execute(tokens, ctx),
            "Should throw RuntimeException when OP_DROP executed on empty stack");
    }

    @Test
    @DisplayName("OP_EQUAL with insufficient operands should throw exception")
    public void testOPEqualInsufficientOperands() {
        List<Token> tokens = Arrays.asList(
            new OpcodeToken(Opcode.OP_5),
            new OpcodeToken(Opcode.OP_EQUAL)
        );
        assertThrows(RuntimeException.class, () -> interpreter.execute(tokens, ctx),
            "Should throw RuntimeException when OP_EQUAL has insufficient operands");
    }

    @Test
    @DisplayName("complex script with multiple operations")
    public void testComplexScript() {
        List<Token> tokens = Arrays.asList(
            new OpcodeToken(Opcode.OP_5),
            new OpcodeToken(Opcode.OP_DUP),
            new OpcodeToken(Opcode.OP_EQUAL),
            new OpcodeToken(Opcode.OP_IF),
            new OpcodeToken(Opcode.OP_1),
            new OpcodeToken(Opcode.OP_ENDIF)
        );
        boolean result = interpreter.execute(tokens, ctx);
        assertTrue(result);
    }

    @Test
    @DisplayName("push data token should work with script interpreter")
    public void testPushDataToken() {
        List<Token> tokens = Arrays.asList(
            new PushDataToken(new byte[]{1, 2, 3})
        );
        boolean result = interpreter.execute(tokens, ctx);
        assertTrue(result);
    }

    @Test
    @DisplayName("mixed OpcodeToken and PushDataToken should work")
    public void testMixedTokens() {
        List<Token> tokens = Arrays.asList(
            new PushDataToken(new byte[]{5}),
            new PushDataToken(new byte[]{5}),
            new OpcodeToken(Opcode.OP_EQUAL)
        );
        boolean result = interpreter.execute(tokens, ctx);
        assertTrue(result);
    }

    @Test
    @DisplayName("executing zero should return false")
    public void testExecuteZero() {
        List<Token> tokens = Arrays.asList(
            new OpcodeToken(Opcode.OP_0)
        );
        boolean result = interpreter.execute(tokens, ctx);
        assertFalse(result);
    }

    @Test
    @DisplayName("executing any non-zero value should return true")
    public void testExecuteNonZero() {
        for (int i = 1; i <= 16; i++) {
            ExecutionContext testCtx = new ExecutionContext();
            OpcodeToken op = null;
            switch (i) {
                case 1: op = new OpcodeToken(Opcode.OP_1); break;
                case 2: op = new OpcodeToken(Opcode.OP_2); break;
                case 3: op = new OpcodeToken(Opcode.OP_3); break;
                case 4: op = new OpcodeToken(Opcode.OP_4); break;
                case 5: op = new OpcodeToken(Opcode.OP_5); break;
                case 6: op = new OpcodeToken(Opcode.OP_6); break;
                case 7: op = new OpcodeToken(Opcode.OP_7); break;
                case 8: op = new OpcodeToken(Opcode.OP_8); break;
                case 9: op = new OpcodeToken(Opcode.OP_9); break;
                case 10: op = new OpcodeToken(Opcode.OP_10); break;
                case 11: op = new OpcodeToken(Opcode.OP_11); break;
                case 12: op = new OpcodeToken(Opcode.OP_12); break;
                case 13: op = new OpcodeToken(Opcode.OP_13); break;
                case 14: op = new OpcodeToken(Opcode.OP_14); break;
                case 15: op = new OpcodeToken(Opcode.OP_15); break;
                case 16: op = new OpcodeToken(Opcode.OP_16); break;
            }
            List<Token> tokens = Arrays.asList(op);
            boolean result = interpreter.execute(tokens, testCtx);
            assertTrue(result, "OP_" + i + " should return true");
        }
    }
}
