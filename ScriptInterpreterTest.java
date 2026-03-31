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

    // Basic Execution Tests 

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

    // OP_IF Tests 

    @Test
    @DisplayName("OP_IF with true condition should execute then-block")
    public void testOPIfTrue() {
        List<Token> tokens = Arrays.asList(
            new OpcodeToken(Opcode.OP_1),       // Push true
            new OpcodeToken(Opcode.OP_IF),      // If true
            new OpcodeToken(Opcode.OP_5),       // Push 5 (in "then")
            new OpcodeToken(Opcode.OP_ENDIF)
        );
        boolean result = interpreter.execute(tokens, ctx);
        assertTrue(result); // 5 is true
    }

    @Test
    @DisplayName("OP_IF with false condition should skip then-block")
    public void testOPIfFalse() {
        List<Token> tokens = Arrays.asList(
            new OpcodeToken(Opcode.OP_0),       // Push false
            new OpcodeToken(Opcode.OP_IF),      // If false
            new OpcodeToken(Opcode.OP_5),       // Push 5 (should be skipped)
            new OpcodeToken(Opcode.OP_ENDIF)
        );
        boolean result = interpreter.execute(tokens, ctx);
        assertFalse(result); // Stack is empty
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

    // OP_ELSE Tests

    @Test
    @DisplayName("OP_IF-ELSE with true condition should execute then-block")
    public void testOPIfElseTrue() {
        List<Token> tokens = Arrays.asList(
            new OpcodeToken(Opcode.OP_1),       // Push true
            new OpcodeToken(Opcode.OP_IF),      // If true
            new OpcodeToken(Opcode.OP_5),       // Push 5 (in "then")
            new OpcodeToken(Opcode.OP_ELSE),
            new OpcodeToken(Opcode.OP_10),      // Push 10 (in "else", skipped)
            new OpcodeToken(Opcode.OP_ENDIF)
        );
        boolean result = interpreter.execute(tokens, ctx);
        assertTrue(result); // 5 is true
    }

    @Test
    @DisplayName("OP_IF-ELSE with false condition should execute else-block")
    public void testOPIfElseFalse() {
        List<Token> tokens = Arrays.asList(
            new OpcodeToken(Opcode.OP_0),       // Push false
            new OpcodeToken(Opcode.OP_IF),      // If false
            new OpcodeToken(Opcode.OP_5),       // Push 5 (in "then", skipped)
            new OpcodeToken(Opcode.OP_ELSE),
            new OpcodeToken(Opcode.OP_10),      // Push 10 (in "else")
            new OpcodeToken(Opcode.OP_ENDIF)
        );
        boolean result = interpreter.execute(tokens, ctx);
        assertTrue(result); // 10 is true
    }

    @Test
    @DisplayName("OP_ELSE without matching OP_IF should throw exception")
    public void testOPElseWithoutIfThrowsException() {
        List<Token> tokens = Arrays.asList(
            new OpcodeToken(Opcode.OP_1),
            new OpcodeToken(Opcode.OP_ELSE),    // No OP_IF
            new OpcodeToken(Opcode.OP_2)
        );
        assertThrows(RuntimeException.class, () -> interpreter.execute(tokens, ctx),
            "Should throw RuntimeException when OP_ELSE has no matching OP_IF");
    }

    // OP_ENDIF Tests 

    @Test
    @DisplayName("OP_ENDIF without matching OP_IF should throw exception")
    public void testOPEndifWithoutIfThrowsException() {
        List<Token> tokens = Arrays.asList(
            new OpcodeToken(Opcode.OP_1),
            new OpcodeToken(Opcode.OP_ENDIF)    // No OP_IF
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
            // No OP_ENDIF
        );
        assertThrows(RuntimeException.class, () -> interpreter.execute(tokens, ctx),
            "Should throw RuntimeException for unclosed OP_IF");
    }

    // Nested OP_IF Tests 

    @Test
    @DisplayName("nested OP_IF with both true should execute innermost code")
    public void testNestedOPIfBothTrue() {
        List<Token> tokens = Arrays.asList(
            new OpcodeToken(Opcode.OP_1),       // Outer condition: true
            new OpcodeToken(Opcode.OP_IF),
            new OpcodeToken(Opcode.OP_1),       // Inner condition: true
            new OpcodeToken(Opcode.OP_IF),
            new OpcodeToken(Opcode.OP_7),       // Push 7
            new OpcodeToken(Opcode.OP_ENDIF),
            new OpcodeToken(Opcode.OP_ENDIF)
        );
        boolean result = interpreter.execute(tokens, ctx);
        assertTrue(result); // 7 is true
    }

    @Test
    @DisplayName("nested OP_IF with outer false should skip all inner code")
    public void testNestedOPIfOuterFalse() {
        List<Token> tokens = Arrays.asList(
            new OpcodeToken(Opcode.OP_0),       // Outer condition: false
            new OpcodeToken(Opcode.OP_IF),
            new OpcodeToken(Opcode.OP_1),       // Inner condition (not evaluated)
            new OpcodeToken(Opcode.OP_IF),
            new OpcodeToken(Opcode.OP_7),       // Push 7 (not executed)
            new OpcodeToken(Opcode.OP_ENDIF),
            new OpcodeToken(Opcode.OP_ENDIF)
        );
        boolean result = interpreter.execute(tokens, ctx);
        assertFalse(result); // Stack is empty
    }

    @Test
    @DisplayName("nested OP_IF with inner false should not execute innermost code")
    public void testNestedOPIfInnerFalse() {
        List<Token> tokens = Arrays.asList(
            new OpcodeToken(Opcode.OP_1),       // Outer condition: true
            new OpcodeToken(Opcode.OP_IF),
            new OpcodeToken(Opcode.OP_0),       // Inner condition: false
            new OpcodeToken(Opcode.OP_IF),
            new OpcodeToken(Opcode.OP_7),       // Push 7 (not executed)
            new OpcodeToken(Opcode.OP_ENDIF),
            new OpcodeToken(Opcode.OP_ENDIF)
        );
        boolean result = interpreter.execute(tokens, ctx);
        assertFalse(result); // Stack is empty
    }

    // Complex Control Flow Tests 

    @Test
    @DisplayName("IF-ELSE-IF structure should work correctly")
    public void testIfElseIfStructure() {
        List<Token> tokens = Arrays.asList(
            new OpcodeToken(Opcode.OP_0),       // First condition: false
            new OpcodeToken(Opcode.OP_IF),
            new OpcodeToken(Opcode.OP_1),       // Skip this
            new OpcodeToken(Opcode.OP_ELSE),
            new OpcodeToken(Opcode.OP_1),       // Condition in else: true
            new OpcodeToken(Opcode.OP_IF),
            new OpcodeToken(Opcode.OP_9),       // Execute this
            new OpcodeToken(Opcode.OP_ENDIF),
            new OpcodeToken(Opcode.OP_ENDIF)
        );
        boolean result = interpreter.execute(tokens, ctx);
        assertTrue(result); // 9 is true
    }

    // Empty Stack in Complex Scenarios 

    @Test
    @DisplayName("executing without final value should return false")
    public void testNoFinalValue() {
        List<Token> tokens = Arrays.asList(
            new OpcodeToken(Opcode.OP_5),
            new OpcodeToken(Opcode.OP_DROP)     // Drop the only value
        );
        boolean result = interpreter.execute(tokens, ctx);
        assertFalse(result);
    }

    @Test
    @DisplayName("OP_DUP on empty stack should throw exception")
    public void testOPDupEmptyStackInScript() {
        List<Token> tokens = Arrays.asList(
            new OpcodeToken(Opcode.OP_DUP)      // Empty stack
        );
        assertThrows(RuntimeException.class, () -> interpreter.execute(tokens, ctx),
            "Should throw RuntimeException when OP_DUP executed on empty stack");
    }

    @Test
    @DisplayName("OP_DROP on empty stack should throw exception")
    public void testOPDropEmptyStackInScript() {
        List<Token> tokens = Arrays.asList(
            new OpcodeToken(Opcode.OP_DROP)     // Empty stack
        );
        assertThrows(RuntimeException.class, () -> interpreter.execute(tokens, ctx),
            "Should throw RuntimeException when OP_DROP executed on empty stack");
    }

    @Test
    @DisplayName("OP_EQUAL with insufficient operands should throw exception")
    public void testOPEqualInsufficientOperands() {
        List<Token> tokens = Arrays.asList(
            new OpcodeToken(Opcode.OP_5),
            new OpcodeToken(Opcode.OP_EQUAL)    // Only one operand
        );
        assertThrows(RuntimeException.class, () -> interpreter.execute(tokens, ctx),
            "Should throw RuntimeException when OP_EQUAL has insufficient operands");
    }

    // Mixed Operations Tests 

    @Test
    @DisplayName("complex script with multiple operations")
    public void testComplexScript() {
        List<Token> tokens = Arrays.asList(
            new OpcodeToken(Opcode.OP_5),       // Stack: [5]
            new OpcodeToken(Opcode.OP_DUP),     // Stack: [5, 5]
            new OpcodeToken(Opcode.OP_EQUAL),   // Stack: [1]
            new OpcodeToken(Opcode.OP_IF),
            new OpcodeToken(Opcode.OP_1),       // Stack: [1]
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
        assertTrue(result); // Both values equal, so true
    }

    // Edge Cases 

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
