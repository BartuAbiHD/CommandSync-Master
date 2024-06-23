package youtube.bartuabihd.CommandSync;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandSynchronize implements CommandExecutor {

	private CSC plugin;
	private String plVersion = "1.1";
	
	public CommandSynchronize(CSC plugin) {
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender.hasPermission("sync.use")) {
			if(args.length >= 0) {
				if(args.length <= 2) {
					sender.sendMessage(ChatColor.BLUE + "CommandSync by BartuAbiHD");
					if(args.length >= 1) {
						if(args[0].equalsIgnoreCase("console")){
							sender.sendMessage(ChatColor.GREEN + "/sync console <server> <command args...>");
							sender.sendMessage(ChatColor.GREEN + "/sync console all <command args...>");
							sender.sendMessage(ChatColor.GREEN + "/sync console bungee <command args...>");
						} else if(args[0].equalsIgnoreCase("player")) {
							sender.sendMessage(ChatColor.GREEN + "/sync player <player> <command args...>");
							sender.sendMessage(ChatColor.GREEN + "/sync player all <command args...>");
						} else if(args[0].equalsIgnoreCase("info")) {
							sender.sendMessage(ChatColor.GOLD + "Created by" + " " + ChatColor.GREEN + "BartuAbiHD");
							sender.sendMessage(ChatColor.GREEN + "Version:" + " " + ChatColor.RED + plVersion);
						} else if(args[0].equalsIgnoreCase("bilgi")) {
							sender.sendMessage(ChatColor.GOLD + "Yapımcı:" + " " + ChatColor.GREEN + "BartuAbiHD");
							sender.sendMessage(ChatColor.GREEN + "Sürüm:" + " " + ChatColor.RED + plVersion);
						} else {
							sender.sendMessage(ChatColor.RED + "Turkish: Yardım için /sync yazın.");
							sender.sendMessage(ChatColor.RED + "English: Type /sync for help.");
						}
					} else {
						sender.sendMessage("");
						sender.sendMessage(ChatColor.GREEN + "/sync console");
						sender.sendMessage(ChatColor.GREEN + "/sync player");
						sender.sendMessage(ChatColor.GREEN + "/sync info");
						sender.sendMessage("");
						sender.sendMessage(ChatColor.BLUE + "Turkish: Daha fazla bilgi için komutu yazın.");
						sender.sendMessage("");
						sender.sendMessage(ChatColor.BLUE + "English: Type the command for more info.");
					}
					sender.sendMessage("");
					sender.sendMessage(ChatColor.BLUE + "Turkish: Yardim icin www.spigotmc.org/resources/commandsync.86053 adresini ziyaret edin.");
					sender.sendMessage(ChatColor.BLUE + "English: Visit www.spigotmc.org/resources/commandsync.86053 for help.");
				} else if(args.length >= 3) {
					if(args[0].equalsIgnoreCase("console") || args[0].equalsIgnoreCase("player")) {
					    String[] newArgs = new String[3];
					    newArgs[0] = args[0];
					    newArgs[1] = args[1];
					    StringBuilder sb = new StringBuilder();
					    for(int i = 2; i < args.length; i++) {
					        sb.append(args[i]);
					        if(i < args.length - 1) {
					            sb.append("+");
					        }
					    }
					    newArgs[2] = sb.toString();
						if(args[1].equalsIgnoreCase("all") || args[1].equalsIgnoreCase("bungee")) {
							makeData(newArgs, false, sender);
						} else {
						    makeData(newArgs, true, sender);
						}
					} else {
						sender.sendMessage(ChatColor.RED + "Turkish: Yardım için /sync yazın.");
						sender.sendMessage(ChatColor.RED + "English: Type /sync for help.");
					}
				}
			}
		} else {
			sender.sendMessage(ChatColor.RED + "Turkish: Bu komutu kullanma izniniz yok.");
			sender.sendMessage(ChatColor.RED + "English: You do not have permission to use that command.");
		}
		return true;
	}
	
	private void makeData(String[] args, Boolean single, CommandSender sender) {
		String data;
		String message = ChatColor.GREEN + "Syncing command /" + args[2].replaceAll("\\+", " ") + " to " + args[0];
		if(single) {
		    data = args[0].toLowerCase() + plugin.spacer + "single" + plugin.spacer + args[2] + plugin.spacer + args[1];
			message = message + " [" + args[1] + "]...";
		} else {
		    data = args[0].toLowerCase() + plugin.spacer + args[1].toLowerCase() + plugin.spacer + args[2];
			message = message + " [" + WordUtils.capitalizeFully(args[1]) + "]...";
		}
		plugin.oq.add(data);
		sender.sendMessage(message);
	}
}
