package farmingplugin.farming;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;


public class MobspawnMenu implements Listener {

    private Plugin plug =  Bukkit.getPluginManager().getPlugin("Farming");
    //Creates a menu titled 'Mob Spawning Menu', 9 spaces wide, with the first space being filled with an emerald block
    //renamed to 'Zombie', and the second space being filled with a bone block renamed to 'Skeleton'. It then opens this
    //menu for the player who was passed to the function.
    public void MobMenu(Player player) {
        Inventory menu = plug.getServer().createInventory(null, 9,ChatColor.BLUE + "Mob Spawning Menu");

        ItemStack Zspawn = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta zmeta = Zspawn.getItemMeta();
        zmeta.setDisplayName("Zombie");
        Zspawn.setItemMeta(zmeta);
        menu.setItem(0, Zspawn);

        ItemStack Sspawn = new ItemStack(Material.BONE_BLOCK);
        ItemMeta smeta = Sspawn.getItemMeta();
        smeta.setDisplayName("Skeleton");
        Sspawn.setItemMeta(smeta);
        menu.setItem(1, Sspawn);

        player.openInventory(menu);
    }


}
