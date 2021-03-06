package net.craftingstore.bukkit.listeners;

import net.craftingstore.Category;
import net.craftingstore.Package;
import net.craftingstore.bukkit.CraftingStoreBukkit;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.logging.Level;

public class InventoryClickListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {

        // Check if a player clicked.
        if (!(e.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) e.getWhoClicked();
        Inventory inventory = e.getInventory();
        ItemStack itemClicked = e.getCurrentItem();

        // Ignore if it's not our inventory.
        if (!CraftingStoreBukkit.getInstance().getQueryCache().hasInventory(inventory.getName())) {
            return;
        }

        if (itemClicked == null || !itemClicked.hasItemMeta()) {
            return;
        }

        // Make sure that the items stays locked.
        e.setCancelled(true);

        // Get all categories.
        Category categories[] = CraftingStoreBukkit.getInstance().getQueryCache().getCategories();

        // Pre-define rows
        Integer inventorySlots = 9;

        // Set boolean if inventory is ready.
        Inventory packagesInventory = null;
        for (Category category : categories) {

            // Only show main categories.
            if (category.isSubCategory()) {
                continue;
            }

            // Get packages
            Package packages[] = category.getpackages();


            // Check if we're already in the category menu, and if we are.. check the items.
            if (inventory.getName().equals(CraftingStoreBukkit.getInstance().getConfig().getString("gui-prefix") + ": " + category.getName())) {
                for (Package packageItem : packages) {
                    if (itemClicked.getItemMeta().getDisplayName().equals(packageItem.getName())) {
                        player.sendMessage(CraftingStoreBukkit.getInstance().prefix + "You can buy \"" + packageItem.getName() + "\" by clicking on this link: " + packageItem.getUrl());
                        player.closeInventory();
                        if (CraftingStoreBukkit.getInstance().getDebug()) {
                            CraftingStoreBukkit.getInstance().getLogger().log(Level.INFO, "Removed inventory from our storage. Name: " + inventory.getName());
                        }
                        CraftingStoreBukkit.getInstance().getQueryCache().removeInventory(inventory.getName());
                        return;
                    }
                }
            }

            // We are in the category selection, if one of them matches this category, go though.
            if (!itemClicked.getItemMeta().getDisplayName().equals(category.getName())) {
                continue;
            }

            // Calculate inventory size.
            Integer packageCount = packages.length;
            while (packageCount > inventorySlots) {
                inventorySlots = inventorySlots + 9;
            }

            // Create inventory
            packagesInventory = Bukkit.createInventory(null, inventorySlots, CraftingStoreBukkit.getInstance().getConfig().getString("gui-prefix") + ": " + category.getName());

            // Add inventory to our data model.
            if (CraftingStoreBukkit.getInstance().getDebug()) {
                CraftingStoreBukkit.getInstance().getLogger().log(Level.INFO, "Added inventory to our storage. Name: " + packagesInventory.getName());
            }
            CraftingStoreBukkit.getInstance().getQueryCache().addInventory(packagesInventory.getName());

            Integer loop = 0;
            for (Package packageItem : packages) {

                // Get material by name.
                Material material = Material.PAPER;

                try {
                    material = packageItem.getMinecraftIconName() == null ? Material.PAPER : Material.valueOf(packageItem.getMinecraftIconName().toUpperCase());
                } catch (IllegalArgumentException el) {
                    // Error in name, using the default instead.
                }

                // Set item meta.
                ItemStack item = new ItemStack(material, 1);
                ItemMeta im = item.getItemMeta();
                im.setDisplayName(packageItem.getName());
                ArrayList<String> lore = new ArrayList<String>();
                if (packageItem.getIngameDescription() != null) {
                    for (String description : packageItem.getIngameDescription().split("\n")) {
                        lore.add(description.replace("\n", "").replace("\r", "").replace("&", "§"));
                    }
                }
                im.setLore(lore);

                item.setItemMeta(im);

                packagesInventory.setItem(loop, item);
                loop++;
            }
        }

        // Check if the inventory is created.
        if (packagesInventory == null) {
            return;
        }

        // Close old inventory, and remove it from our storage.
        if (CraftingStoreBukkit.getInstance().getDebug()) {
            CraftingStoreBukkit.getInstance().getLogger().log(Level.INFO, "Removed inventory from our storage. Name: " + inventory.getName());
        }
        CraftingStoreBukkit.getInstance().getQueryCache().removeInventory(inventory.getName());
        player.closeInventory();

        // Open inventory!
        player.openInventory(packagesInventory);
    }

}
