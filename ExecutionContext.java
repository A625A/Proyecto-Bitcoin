import java.util.ArrayDeque;
import java.util.Deque;

public class ExecutionContext {

    private final Deque<byte[]> stack = new ArrayDeque<>();

    public void push(byte[] data) {
        stack.push(data);
    }

    public byte[] pop() {
        if (stack.isEmpty())
            throw new RuntimeException("Pila vacia");
        return stack.pop();
    }

    public byte[] peek() {
        return stack.peek();
    }

    public boolean isEmpty() {
        return stack.isEmpty();
    }

    public static boolean isTrue(byte[] bytes) {
        for (byte b : bytes)
            if (b != 0) return true;
        return false;
    }
        @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        boolean primero = true;

        for (byte[] item : stack) {
            if (!primero) {
                sb.append(", ");
            }
            sb.append(Hex.bytesToHex(item));
            primero = false;
        }

        sb.append("]");
        return sb.toString();
    }

}
