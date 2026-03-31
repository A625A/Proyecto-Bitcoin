import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas del interprete")
public class ScriptInterpreterTest {

    private ScriptInterpreter interpreter;
    private ExecutionContext ctx;

    @BeforeEach
    public void setUp() {
        interpreter = new ScriptInterpreter();
        ctx = new ExecutionContext();
    }

    @Test
    @DisplayName("un script vacio regresa falso")
    public void testEmptyScript() {
        List<Token> tokens = Arrays.asList();
        boolean result = interpreter.execute(tokens, ctx);
        assertFalse(result);
    }

    @Test
    @DisplayName("un valor verdadero regresa verdadero")
    public void testSinglePushTrue() {
        List<Token> tokens = Arrays.asList(
            new OpcodeToken(Opcode.OP_1)
        );
        boolean result = interpreter.execute(tokens, ctx);
        assertTrue(result);
    }

    @Test
    @DisplayName("un valor falso regresa falso")
    public void testSinglePushFalse() {
        List<Token> tokens = Arrays.asList(
            new OpcodeToken(Opcode.OP_0)
        );
        boolean result = interpreter.execute(tokens, ctx);
        assertFalse(result);
    }

    @Test
    @DisplayName("OP_IF con verdadero ejecuta el bloque")
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
    @DisplayName("OP_IF con falso salta el bloque")
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
    @DisplayName("OP_IF en pila vacia lanza error")
    public void testOPIfEmptyStackThrowsException() {
        List<Token> tokens = Arrays.asList(
            new OpcodeToken(Opcode.OP_IF),
            new OpcodeToken(Opcode.OP_1),
            new OpcodeToken(Opcode.OP_ENDIF)
        );
        assertThrows(RuntimeException.class, () -> interpreter.execute(tokens, ctx),
            "Debe lanzar error si OP_IF usa una pila vacia");
    }

    @Test
    @DisplayName("OP_IF-ELSE con verdadero ejecuta el primer bloque")
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
    @DisplayName("OP_IF-ELSE con falso ejecuta el otro bloque")
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
    @DisplayName("OP_ELSE sin OP_IF lanza error")
    public void testOPElseWithoutIfThrowsException() {
        List<Token> tokens = Arrays.asList(
            new OpcodeToken(Opcode.OP_1),
            new OpcodeToken(Opcode.OP_ELSE),   
            new OpcodeToken(Opcode.OP_2)
        );
        assertThrows(RuntimeException.class, () -> interpreter.execute(tokens, ctx),
            "Debe lanzar error si OP_ELSE no tiene OP_IF");
    }

   

    @Test
    @DisplayName("OP_ENDIF sin OP_IF lanza error")
    public void testOPEndifWithoutIfThrowsException() {
        List<Token> tokens = Arrays.asList(
            new OpcodeToken(Opcode.OP_1),
            new OpcodeToken(Opcode.OP_ENDIF)    
        );
        assertThrows(RuntimeException.class, () -> interpreter.execute(tokens, ctx),
            "Debe lanzar error si OP_ENDIF no tiene OP_IF");
    }

    @Test
    @DisplayName("OP_IF sin cerrar lanza error")
    public void testUnclosedOPIfThrowsException() {
        List<Token> tokens = Arrays.asList(
            new OpcodeToken(Opcode.OP_1),
            new OpcodeToken(Opcode.OP_IF),
            new OpcodeToken(Opcode.OP_5)
            
        );
        assertThrows(RuntimeException.class, () -> interpreter.execute(tokens, ctx),
            "Debe lanzar error si falta cerrar OP_IF");
    }

   

    @Test
    @DisplayName("dos OP_IF con true ejecutan el bloque interno")
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
    @DisplayName("si el OP_IF externo es false se salta todo")
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
    @DisplayName("si el OP_IF interno es false no ejecuta el bloque")
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
    @DisplayName("una estructura IF-ELSE-IF funciona")
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
    @DisplayName("si no queda valor final regresa falso")
    public void testNoFinalValue() {
        List<Token> tokens = Arrays.asList(
            new OpcodeToken(Opcode.OP_5),
            new OpcodeToken(Opcode.OP_DROP)
        );
        boolean result = interpreter.execute(tokens, ctx);
        assertFalse(result);
    }

    @Test
    @DisplayName("OP_DUP en pila vacia lanza error")
    public void testOPDupEmptyStackInScript() {
        List<Token> tokens = Arrays.asList(
            new OpcodeToken(Opcode.OP_DUP)
        );
        assertThrows(RuntimeException.class, () -> interpreter.execute(tokens, ctx),
            "Debe lanzar error si OP_DUP usa una pila vacia");
    }

    @Test
    @DisplayName("OP_DROP en pila vacia lanza error")
    public void testOPDropEmptyStackInScript() {
        List<Token> tokens = Arrays.asList(
            new OpcodeToken(Opcode.OP_DROP)
        );
        assertThrows(RuntimeException.class, () -> interpreter.execute(tokens, ctx),
            "Debe lanzar error si OP_DROP usa una pila vacia");
    }

    @Test
    @DisplayName("OP_EQUAL con pocos operandos lanza error")
    public void testOPEqualInsufficientOperands() {
        List<Token> tokens = Arrays.asList(
            new OpcodeToken(Opcode.OP_5),
            new OpcodeToken(Opcode.OP_EQUAL)
        );
        assertThrows(RuntimeException.class, () -> interpreter.execute(tokens, ctx),
            "Debe lanzar error si OP_EQUAL no tiene suficientes valores");
    }

    @Test
    @DisplayName("un script con varias operaciones funciona")
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
    @DisplayName("PushDataToken funciona con el interprete")
    public void testPushDataToken() {
        List<Token> tokens = Arrays.asList(
            new PushDataToken(new byte[]{1, 2, 3})
        );
        boolean result = interpreter.execute(tokens, ctx);
        assertTrue(result);
    }

    @Test
    @DisplayName("OpcodeToken y PushDataToken juntos funcionan")
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
    @DisplayName("ejecutar cero regresa falso")
    public void testExecuteZero() {
        List<Token> tokens = Arrays.asList(
            new OpcodeToken(Opcode.OP_0)
        );
        boolean result = interpreter.execute(tokens, ctx);
        assertFalse(result);
    }

    @Test
    @DisplayName("cualquier valor distinto de cero regresa verdadero")
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
            assertTrue(result, "OP_" + i + " debe regresar verdadero");
        }
    }
}
