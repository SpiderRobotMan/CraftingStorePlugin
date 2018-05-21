package net.craftingstore.bukkit.commands;

import net.craftingstore.Category;
import net.craftingstore.bukkit.CraftingStoreBukkit;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BuyCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // Get all categories.
        Category categories[] = CraftingStoreBukkit.getInstance().getQueryCache().getCategories();

        // Pre-define rows
        Integer inventorySlots = 9;

        Integer categoryCount = categories.length;
        while(categoryCount > inventorySlots) {
            inventorySlots = inventorySlots + 9;
            System.out.println("Now: " + inventorySlots);
        }

        // Create inventory
        Inventory categoriesInventory = Bukkit.createInventory(null, inventorySlots, "Store Categories");

        // Get player
        Player player = (Player) sender;

        Integer loop = 0;

        // Walk though categories to build inventory
        for (Category category : categories) {
            System.out.println(category.getName());

            // Get material
            Material material = Material.getMaterial(category.getMinecraftIconName());
            if (material == null) {
                material = Material.DIRT;
            }

            // Set item meta.
            ItemStack item = new ItemStack(material, 1);
            ItemMeta im = item.getItemMeta();
            im.setDisplayName(category.getName());
            item.setItemMeta(im);

            categoriesInventory.setItem(loop, item);
            loop++;
        }

        // Open inventory
        player.openInventory(categoriesInventory);

        return true;

    }

}