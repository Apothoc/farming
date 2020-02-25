package farmingplugin.farming;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import java.util.*;
import org.bukkit.block.data.Ageable;


//I've included enchant functionality if you want to apply fortune or silk touch to a hoe, even though those things normally don't work.

public class BlockListener implements Listener {

    //This is a list of all plants this plugin handles. There are some other things which I've not coded in for now (sugarcane, the pumpkin block),
    //but not sure if you wanted that included.
    private final List plants = new ArrayList(Arrays.asList(Material.MELON, Material.WHEAT, Material.BEETROOTS, Material.POTATOES, Material.CARROTS, Material.NETHER_WART, Material.PUMPKIN_STEM, Material.ATTACHED_PUMPKIN_STEM, Material.MELON_STEM, Material.ATTACHED_MELON_STEM, Material.RED_MUSHROOM, Material.BROWN_MUSHROOM));

    //If you click in an inventory, (1) checks if the inventory is nonexistent and then cancels the check if it is, and
    //(2) checks if the inventory is the mob spawning menu, cancels the event if it is, and then checks if the item
    //clicked was within the Mob Spawning Menu. If it was an Emerald block, it spawns a zombie. If it was a Bone block,
    //it spawns a skeleton. Convienantly, players can't color menus in regular Minecraft.
    @EventHandler
    public void OnInventoryClick(InventoryClickEvent event) {
        Inventory here = event.getClickedInventory();
        if (here == null) {
            return;
        }
        if (event.getView().getTitle().equals(ChatColor.BLUE + "Mob Spawning Menu")) {
            event.setCancelled(true);
            if (event.getClickedInventory().getHolder() == null) {
                Material clickeditem = event.getCurrentItem().getType();
                Player player = (Player) event.getWhoClicked();
                if (clickeditem == Material.EMERALD_BLOCK) {
                    event.getWhoClicked().getWorld().spawnEntity(player.getLocation(), EntityType.ZOMBIE);
                } else if (clickeditem == Material.BONE_BLOCK) {
                    event.getWhoClicked().getWorld().spawnEntity(player.getLocation(), EntityType.SKELETON);
                }
            }
        }

    }

    //If you attempt to drag an object, (1) checks if the inventory is nonexistent and then cancels the check if it is,
    // and (2) checks if the inventory is the mob spawning menu, and cancels the event if it is.
    @EventHandler
    public void OnInventoryDrag(InventoryDragEvent event) {
        Inventory here = event.getInventory();
        if (here == null) {
            return;
        }
        if (event.getView().getTitle().equals(ChatColor.BLUE + "Mob Spawning Menu")) {
            event.setCancelled(true);
        }
    }

    //here.getHolder() == null && here.getSize() == 9 && e

    //When a player joins the server, checks if the config file already has them set to some trampling status, and if not,
    //sets them to not being a trampler.
    @EventHandler
    public void onJoin(PlayerLoginEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if ((Bukkit.getPluginManager().getPlugin("Farming").getConfig().getString("Players." + uuid + ".trample") == "YES") || (Bukkit.getPluginManager().getPlugin("Farming").getConfig().getString("Players." + uuid + ".trample") == "NO")) {
        } else {
            Bukkit.getPluginManager().getPlugin("Farming").getConfig().set("Players." + uuid + ".trample", "NO");
            Bukkit.getPluginManager().getPlugin("Farming").saveConfig();
        }
    }

    //When a player interacts with a farmland block by trampling it, checks if they are set to be a trampler, and if not,
    //cancels the event.
    @EventHandler
    public void OnPlayerInteractEvent(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if ((event.getAction() == Action.PHYSICAL) && (block.getType() == Material.FARMLAND)) {
            UUID uuid = event.getPlayer().getUniqueId();
            if (Bukkit.getPluginManager().getPlugin("Farming").getConfig().getString("Players." + uuid + ".trample") == "NO") {
                event.setCancelled(true);
            }
        }
    }

    //When an entity explodes, checks all blocks that would be exploded, and for each that are a crop, replaces them with air instead.
    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        List<Block> blocks = event.blockList();
        for (Block block : blocks) {
            if (plants.contains(block.getType())) {
                block.setType(Material.AIR);
            }
        }
    }

    //When a block explodes, checks all blocks that would be exploded, and for each that are a crop, replaces them with air instead.
    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        List<Block> blocks = event.blockList();
        for (Block block : blocks) {
            if (plants.contains(block.getType())) {
                block.setType(Material.AIR);
            }
        }
    }

    //When a piston retracts, checks all the blocks that are affected by it, and if any are crops, replaces them with air.
    @EventHandler
    public void BlockPistonRetractEvent(BlockPistonRetractEvent event) {
        List<Block> blocks = event.getBlocks();
        for (Block block : blocks) {
            if (plants.contains(block.getType())) {
                block.setType(Material.AIR);
            }
        }
    }

    //When a piston extends, checks all blocks that are affected by it, and if any are crops, replaces them with air.
    @EventHandler
    public void BlockPistonExtendEvent(BlockPistonExtendEvent event) {
        List<Block> blocks = event.getBlocks();
        for (Block block : blocks) {
            if (plants.contains(block.getType())) {
                block.setType(Material.AIR);
            }
        }
    }

    //When a fluid flows onto a block, checks if it is a crop besides a melon, and if so, replaces those crops with air.
    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {
        Block blockTo = event.getToBlock();
        if (plants.contains(blockTo.getType()) && (blockTo.getType() != Material.MELON)) {
            blockTo.setType(Material.AIR);
            event.setCancelled(true);
        }
    }

    //When a physics event happens, checks if its block is a crop, and then checks if the block below is not farmland.
    // Then, it also makes sure that the block is NOT a melon, that it is NOT netherwart ontop of soul sand, and that
    // it is NOT a red or brown mushroom on a solid and opaque block. If all this is true, then it replaces the block with air.
    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event) {
        Block block = event.getBlock();
        Material blocktype = block.getType();
        Block BlockBelow = block.getRelative(0, -1, 0);
        Material blockBelowtype = BlockBelow.getType();
        if  (((blockBelowtype != Material.FARMLAND) && (plants.contains(blocktype))) && !((blocktype == Material.MELON) || ((blocktype == Material.NETHER_WART) && (blockBelowtype == Material.SOUL_SAND)) || (((blocktype == Material.BROWN_MUSHROOM) || (blocktype == Material.RED_MUSHROOM)) && (((block.getLightLevel() < 13) && blockBelowtype.isSolid() && blockBelowtype.isOccluding()) || (blockBelowtype == Material.PODZOL) || (blockBelowtype == Material.MYCELIUM))))) {

            block.setType(Material.AIR);
        }
    }

    //When a block is broken. This will be described in more detail below.
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Material blockmat = block.getType();
        //Checks if the block was one of the plants we want to deal with. Below and above this is just declaring some
        //useful variables.
        if (plants.contains(blockmat)) {
            Player player = event.getPlayer();
            Location loc = block.getLocation();
            World wor = block.getWorld();

            int handmultiplier = 0;
            int enchantmultiplier = 0;

            event.setDropItems(false); //Disables drops.

            //If the player is holding an item, then it runs through a series of switches. Since switches without breaks
            //run all the code below them, I figured out this trick to just add one to the handmultiplier as a way to
            //keep track of what multiplier to use. If you aren't using a hoe, it just returns you right out of this thing.
            //Also will always check if whatever you have is enchanted with fortune, and if it is, gives the correct
            //enchant multiplier.
            if ((player.getItemInHand() != null)) {
                ItemStack handitem = player.getItemInHand();
                Material item = handitem.getType();
                switch (item) {
                    case DIAMOND_HOE:
                        handmultiplier = handmultiplier + 1;
                    case IRON_HOE:
                        handmultiplier = handmultiplier + 1;
                    case STONE_HOE:
                    case WOODEN_HOE:
                        handmultiplier = handmultiplier + 1;
                        if (handitem.getEnchantments().containsKey(Enchantment.LOOT_BONUS_BLOCKS)) {
                            enchantmultiplier = handitem.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
                        }
                        break;
                    default:
                        return;
                }
            }

            //First checks if the block has data equal to a fully grown state. If not, then it must
            //be of the class of plants which do not grow. Seeds in Minecraft are increased by fortune, hence how
            //I have handled it here. Handmultiplier only increases crops which yield some non-seed component,
            //or are like potato/carrot/nether_wart, which all just drop themselves. For the stuff which doesn't grow,
            //it's mostly all just handmultiplier, except for melon slices which are done by enchantmultiplier.
            //Note that there is some special code down there to handle silk touch, which gives whole melons,
            //and to add some rare drops as a bonus objective to red mushrooms, which have a 1/10 chance to drop
            //a diamond tool or sword.
            if (block.getBlockData() instanceof Ageable) {
                Ageable age = (Ageable) block.getBlockData();
                if (age.getAge() == age.getMaximumAge()) {
                    switch (blockmat) {
                        case WHEAT:
                            wor.dropItemNaturally(loc, new ItemStack(Material.WHEAT, handmultiplier));
                            wor.dropItemNaturally(loc, new ItemStack(Material.WHEAT_SEEDS, 1 + enchantmultiplier));
                            break;
                        case BEETROOTS:
                            wor.dropItemNaturally(loc, new ItemStack(Material.BEETROOT, handmultiplier));
                            wor.dropItemNaturally(loc, new ItemStack(Material.BEETROOT_SEEDS, 1 + enchantmultiplier));
                            break;
                        case POTATOES:
                            wor.dropItemNaturally(loc, new ItemStack(Material.POTATO, handmultiplier + enchantmultiplier));
                            break;
                        case CARROTS:
                            wor.dropItemNaturally(loc, new ItemStack(Material.CARROT, handmultiplier + enchantmultiplier));
                            break;
                        case NETHER_WART: 
                            wor.dropItemNaturally(loc, new ItemStack(Material.NETHER_WART, handmultiplier + enchantmultiplier));
                            break;
                        case PUMPKIN_STEM:
                        case ATTACHED_PUMPKIN_STEM:
                            wor.dropItemNaturally(loc, new ItemStack(Material.PUMPKIN_SEEDS, 1 + enchantmultiplier));
                            break;
                        case MELON_STEM:
                        case ATTACHED_MELON_STEM:
                            wor.dropItemNaturally(loc, new ItemStack(Material.MELON_SEEDS, 1 + enchantmultiplier));
                            break;
                        default:
                            break;
                    }
                }
            } else {

                switch (blockmat) {
                    case RED_MUSHROOM:
                        if (1 == (int) (Math.random() * 10 + 1)) {
                            Material[] itemreward = new Material[]{Material.DIAMOND_HOE, Material.DIAMOND_AXE, Material.DIAMOND_SWORD, Material.DIAMOND_SHOVEL};
                            wor.dropItemNaturally(loc, new ItemStack(itemreward[(int) (Math.random() * 3 + 1)], handmultiplier));
                        }
                        wor.dropItemNaturally(loc, new ItemStack(Material.RED_MUSHROOM, handmultiplier));
                        break;
                    case BROWN_MUSHROOM:
                        wor.dropItemNaturally(loc, new ItemStack(Material.BROWN_MUSHROOM, handmultiplier));
                        break;
                    case MELON:
                        if (player.getItemInHand() != null) {
                            if (player.getItemInHand().getEnchantments().containsKey(Enchantment.SILK_TOUCH)) {
                                wor.dropItemNaturally(loc, new ItemStack(Material.MELON, 1));
                            } else {
                                wor.dropItemNaturally(loc, new ItemStack(Material.MELON_SLICE, 1 + enchantmultiplier));
                            }
                        } else {
                            wor.dropItemNaturally(loc, new ItemStack(Material.MELON_SLICE, 1 + enchantmultiplier));
                        }
                        break;
                    default:
                        break;
                }

            }

        }


    }
}








