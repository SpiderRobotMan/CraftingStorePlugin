package net.craftingstore;

public class Category {

    private int id;
    private String name;
    private String description;
    private String minecraftIconName;
    private String url;
    private Package packages[];

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getMinecraftIconName() {
        return minecraftIconName;
    }

    public String getUrl() {
        return url;
    }

    public Package[] getpackages() {
        return packages;
    }
}