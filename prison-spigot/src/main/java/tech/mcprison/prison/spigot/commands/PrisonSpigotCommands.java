package tech.mcprison.prison.spigot.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;

import tech.mcprison.prison.spigot.SpigotPrison;
import tech.mcprison.prison.spigot.gui.SpigotPrisonGUI;
import tech.mcprison.prison.spigot.gui.mine.SpigotPlayerMinesGUI;
import tech.mcprison.prison.spigot.gui.rank.SpigotPlayerRanksGUI;
import tech.mcprison.prison.spigot.spiget.BluesSpigetSemVerComparator;

/**
 * @author GABRYCA
 * @author RoyalBlueRanger
 */
public class PrisonSpigotCommands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if ( new BluesSpigetSemVerComparator().compareMCVersionTo("1.9.0") < 0 ) {
            sender.sendMessage(SpigotPrison.format("&cSorry, but GUIs don't work with versions prior to 1.9.0 due to issues"));
            return true;
        }

        if(!(sender instanceof Player || sender instanceof tech.mcprison.prison.internal.Player)){
            sender.sendMessage(SpigotPrison.format("&cLooks like you aren't a player"));
            return true;
        }

        Player p = null;
        if (sender instanceof Player) {
            p = (Player) sender;
        }

        // Load config
        Configuration GuiConfig = SpigotPrison.getGuiConfig();

        if (args.length == 0) {
            sender.sendMessage(SpigotPrison.format("&cIncorrect usage, the command should be /prisonmanager -gui_or_ranks_or_mines-"));
            return true;
        }

        if (sender.hasPermission("prison.admin") || sender.hasPermission("prison.prisonmanagergui")) {

            if (args[0].equalsIgnoreCase("gui")){
                SpigotPrisonGUI gui = new SpigotPrisonGUI(p);
                gui.open();
                return true;
            }

        }

        if (args[0].equalsIgnoreCase("ranks")){
            if (GuiConfig.getString("Options.Ranks.GUI_Enabled").equalsIgnoreCase("true")) {
                if (GuiConfig.getString("Options.Ranks.Permission_GUI_Enabled").equalsIgnoreCase("true")) {
                    if (sender.hasPermission(GuiConfig.getString("Options.Ranks.Permission_GUI"))) {
                        SpigotPlayerRanksGUI gui = new SpigotPlayerRanksGUI(p);
                        gui.open();
                        return true;
                    }
                    return true;
                }
                SpigotPlayerRanksGUI gui = new SpigotPlayerRanksGUI(p);
                gui.open();
                return true;
            }
        } else if (args[0].equalsIgnoreCase("mines")){
            if (GuiConfig.getString("Options.Mines.GUI_Enabled").equalsIgnoreCase("true")){
                if (GuiConfig.getString("Options.Mines.Permission_GUI_Enabled").equalsIgnoreCase("true")){
                    if (sender.hasPermission(GuiConfig.getString("Options.Mines.Permission_GUI"))) {
                        SpigotPlayerMinesGUI gui = new SpigotPlayerMinesGUI(p);
                        gui.open();
                        return true;
                    }
                    return true;
                }
                SpigotPlayerMinesGUI gui = new SpigotPlayerMinesGUI(p);
                gui.open();
                return true;
            }
        }

        return true;

    }
}
