import java.util.ArrayDeque;
import java.util.Deque;

public class ExecutionContext {

    private final Deque<byte[]> stack = new ArrayDeque<>();

    public void push(byte[] data) {
        stack.push(data);
    }

    public byte[] pop() {
        if (stack.isEmpty())
            throw new RuntimeException("Stack underflow");
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
}