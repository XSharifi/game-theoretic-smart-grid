import java.util.Arrays;

// Class for grouping multiple fields
public class Collection
{
    private Object[] items;
    private final int size;

    // Constructs an object array and initialize it with given values
    public Collection(Object... values)
    {
        this.size = values.length;
        this.items = Arrays.copyOf(values, size);
    }

    public Object get(int i) {
        return items[i];
    }

    public int getSize() {
        return size;
    }

    @Override
    public String toString() {
        return Arrays.toString(items);
    }
}