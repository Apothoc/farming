package farmingplugin.farming;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public class CommandHandler implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Plugin plug =  Bukkit.getPluginManager().getPlugin("Farming");

        //If the name of the command is mobspawn, then it runs the mobmenu function for the player who entered the command.
        if (cmd.getName().equalsIgnoreCase("mobspawn")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                MobspawnMenu me = new MobspawnMenu();
                me.MobMenu(player);
            }

        //If the name of the command is trampleperm, then it first checks if there is only one arg, in which case it is
        //assumed the player is trying to change their own perms, in which case it is just 'yes' or 'no' for whether they
            // want to trample plants or not. If there are more args than this, the second arg is assumed to be the name
            // of the player they are wishing to change. It is first checked that they are an OP, and then grabs online players
            // to change the setting for that particular player. Note that this doesn't work if the player is offline!
        } else if (cmd.getName().equalsIgnoreCase("trampleperm")) {
            if (args.length == 1) {
                if(sender instanceof Player) {
                    Player player = (Player) sender;
                    UUID uuid = player.getUniqueId();
                    if (args[0].equalsIgnoreCase("yes")) {
                        player.sendMessage("You will now trample farmland!");
                        plug.getConfig().set("Players." + uuid + ".trample", "YES");
                        plug.saveConfig();
                        return false;
                    } else if (args[0].equalsIgnoreCase("no")) {
                        player.sendMessage("You won't trample farmland!");
                        plug.getConfig().set("Players." + uuid + ".trample", "NO");
                        plug.saveConfig();
                        return false;
                    }
                }
            } else if (args.length != 0){
                if(sender instanceof Player) {
                    Player player = (Player) sender;
                    if (player.isOp()) {
                        for (Player play : Bukkit.getOnlinePlayers()) {
                            if (play.getDisplayName().equalsIgnoreCase(args[1])) {
                                UUID uuid = play.getUniqueId();
                                if (args[0].equalsIgnoreCase("yes")) {
                                    player.sendMessage("You just made " + play.getDisplayName() + " now trample farmland!");
                                    plug.getConfig().set("Players." + uuid + ".trample", "YES");
                                    plug.saveConfig();
                                } else if (args[0].equalsIgnoreCase("no")) {
                                    player.sendMessage("You just made " + play.getDisplayName() + " not trample farmland!");
                                    plug.getConfig().set("Players." + uuid + ".trample", "NO");
                                    plug.saveConfig();
                                } else {
                                    return false;
                                }
                            }

                        }

                    }
                }
            }
        }



    return false;
    }
}
