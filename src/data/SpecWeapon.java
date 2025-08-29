package data;



public enum SpecWeapon {

    None(0, "", 100),
    Burning_Claws(29577, "Burning claws", 30),
    Dragondagger(5698, "Dragon dagger", 30);

    private final int id;
    private final String name;
    private final int percent;

    SpecWeapon(int id, String name, int percent) {
        this.id = id;
        this.name = name;
        this.percent = percent;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPercent() {
        return percent;
    }
}