package android.example.food.model;

public class MenuItem {
    private int menu_id;
    private String menu_name;
    private String description;
    private double price;

    public MenuItem(int menu_id, String menu_name, String description, double price) {
        this.menu_id = menu_id;
        this.menu_name = menu_name;
        this.description = description;
        this.price = price;
    }

    // Getters and Setters
    public int getMenu_id() { return menu_id; }
    public String getMenu_name() { return menu_name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
}
