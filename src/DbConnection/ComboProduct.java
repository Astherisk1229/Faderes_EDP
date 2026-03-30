package DbConnection;

public class ComboProduct {
    private final int id;
    private final String label;

    public ComboProduct(int id, String label) {
        this.id = id;
        this.label = label;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return label; // displayed in JComboBox
    }
}