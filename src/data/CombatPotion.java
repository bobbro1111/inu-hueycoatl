package data;



public enum CombatPotion {
    None(0,""),
    Divine_Super_Combat(23685, "Divine super combat potion(4)"),
    Super_Combat(12695, "Super combat potion(4)"),
    Super_Strength(2440, "Super strength(4)");

    private final int id;
    private final String name;

    CombatPotion(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}