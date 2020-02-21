package farmingplugin.farming;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;


public final class Farming extends JavaPlugin {
    private BlockListener blockListener = new BlockListener();
    private CommandHandler commandHandler = new CommandHandler();


    @Override
    public void onEnable() {
        getLogger().info("Hello world!");
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(blockListener, this);
        getCommand("trampleperm").setExecutor(commandHandler);
        getCommand("mobspawn").setExecutor(commandHandler);
        this.saveDefaultConfig();
        //Logic for plugin startup, registering events to a particular listener and setting commands to our command handler.
        //Also saves default config if no config exists.
    }

    @Override
    public void onDisable() {
        getLogger().info("Goodbye world!");
        //Literally just so that you can tell the plugin is disabled.
    }
}
