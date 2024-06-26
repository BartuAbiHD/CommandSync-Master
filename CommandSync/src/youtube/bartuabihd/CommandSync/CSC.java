package youtube.bartuabihd.CommandSync;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class CSC extends JavaPlugin {

	public ClientThread client;
	public List<String> oq = Collections.synchronizedList(new ArrayList<String>());
	public Integer qc = 0;
	public String spacer = "@#@";
	public Debugger debugger;
	
	public void onEnable() {
		String[] data = loadConfig();
		if(data[3].equals("UNSET") || data[4].equals("UNSET")) {
			debugger.debug("TURKISH: YAPILANDIRMA DOSYASI BELIRSIZ DEGERLER ICERIR - EKLENTI CALISMADAN ONCE BUNLARI DUZELTMELISIN !!! ");
			debugger.debug("ENGLISH: THE CONFIG FILE CONTAINS UNSET VALUES - YOU MUST FIX THEM BEFORE THE PLUGIN WILL WORK !!! ");
			return;
		}
		try {
			client = new ClientThread(this, InetAddress.getByName(data[0]), Integer.parseInt(data[1]), Integer.parseInt(data[2]), data[3], data[4]);
			client.start();
		} catch(Exception e) {
			e.printStackTrace();
		}
		loadData();
		getCommand("Sync").setExecutor(new CommandSynchronize(this));
		
		try {
			File folder = getDataFolder();
            final File f = new File(folder, "data.txt");
            if (f.exists()) {
                if (f.delete()) {
                	Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.WHITE + "---------------------------------------------------------------");
                    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Turkish: CommandSync'den data.txt dosyasi basariyla silindi!");
                    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "English: File data.txt from CommandSync was successful deleted!");
                    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.WHITE + "---------------------------------------------------------------");
                }
                else {
                	Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.WHITE + "-----------------------------------");
                    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED +   "Turkish: Dosya data.txt silinmedi!");
                    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED +   "English: File data.txt not deleted!");
                    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.WHITE + "-----------------------------------");
                }
            }
            else {
            	Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.WHITE +  "-----------------------------------------------------------");
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "Turkish: Data.txt dosyasi mevcut degil! Her sey yolunda.");
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "English: File data.txt not exist! Everything it is alright.");
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.WHITE +  "-----------------------------------------------------------");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
		
		new UpdateChecker(this, 86053).getLatestVersion(version -> {
            if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
                getLoggerColored(ChatColor.GRAY + "[" + ChatColor.BLUE + "CommandSync" + ChatColor.GRAY + "] " + ChatColor.RED + "A new update of the plugin is not available.");
            }
            else {
                getLoggerColored(ChatColor.GRAY + "[" + ChatColor.BLUE + "CommandSync" + ChatColor.GRAY + "] " + ChatColor.GREEN + "The plugin has a new update. Download link: https://bit.ly/PLcscm");
            }
        });
		
	}
	
	public void onDisable() {
		saveData();
		debugger.close();
		this.getLogger().info("Spigot icin CommandSync devre disi birakildi! (CommandSync for Spigot was disabled!)");
        try {
        	File folder = getDataFolder();
            final File f = new File(folder, "data.txt");
            if (f.exists()) {
                if (f.delete()) {
                    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.WHITE + "---------------------------------------------------------------");
                    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Turkish: CommandSync'den data.txt dosyasi basariyla silindi!");
                    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "English: File data.txt from CommandSync was successful deleted!");
                    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.WHITE + "---------------------------------------------------------------");
                }
                else {
                    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.WHITE + "-----------------------------------");
                    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED +   "Turkish: Dosya data.txt silinmedi!");
                    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED +   "English: File data.txt not deleted!");
                    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.WHITE + "-----------------------------------");
                }
            }
            else {
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.WHITE +  "-----------------------------------------------------------");
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "Turkish: Data.txt dosyasi mevcut degil! Her sey yolunda.");
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "English: File data.txt not exist! Everything it is alright.");
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.WHITE +  "-----------------------------------------------------------");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
	}

	private String[] loadConfig() {
		String[] defaults = new String[] {
			"ip=localhost", "port=9190", "heartbeat=1000", "name=UNSET", "pass=UNSET", "debug=false"
		};
		String[] data = new String[defaults.length];
        try {
            File folder = getDataFolder();
            if(!folder.exists()) {
                folder.mkdir();
            }
            File file = new File(folder, "config.txt");
            if(!file.exists()) {
                file.createNewFile();
            }
            BufferedReader br = new BufferedReader(new FileReader(file));
            for(int i = 0; i < defaults.length; i++) {
                String l = br.readLine();
                if(l == null || l.isEmpty()) {
                    data[i] = defaults[i].split("=")[1];
                } else {
                    data[i] = l.split("=")[1];
                    defaults[i] = l;
                }
            }
            br.close();
            file.delete();
            file.createNewFile();
            PrintStream ps = new PrintStream(new FileOutputStream(file));
            for(int i = 0; i < defaults.length; i++) {
                ps.println(defaults[i]);
            }
            ps.close();
            debugger = new Debugger(this, Boolean.valueOf(data[5]));
            debugger.debug("Turkish: Yapilandirma dosyasi yuklendi.");
			debugger.debug("English: Configuration file loaded.");
        } catch(IOException e) {
            e.printStackTrace();
        }
        return data;
	}
	
	private void saveData() {
		try{
			OutputStream os = new FileOutputStream(new File(getDataFolder(), "data.txt"));
			PrintStream ps = new PrintStream(os);
			for(String s : oq) {
				ps.println("oq:" + s);
			}
			ps.println("qc:" + String.valueOf(qc));
			ps.close();
			debugger.debug("Turkish: Tum veriler kaydeildi.");
			debugger.debug("English: All data saved.");
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void getLoggerColored(final String msg) {
        Bukkit.getConsoleSender().sendMessage(msg);
    }
	
	private void loadData() {
		try{
			File file = new File(getDataFolder(), "data.txt");
			if(file.exists()) {
				BufferedReader br = new BufferedReader(new FileReader(file));
				try {
					String l = br.readLine();
					while(l != null) {
						if(l.startsWith("oq:")) {
							oq.add(new String(l.substring(3)));
						} else if(l.startsWith("qc:")) {
							qc = Integer.parseInt(new String(l.substring(3)));
						}
						l = br.readLine();
					}
					debugger.debug("Turkish: Tum veriler yuklendi.");
					debugger.debug("English: All data loaded.");
				} finally {
					br.close();
				}
			} else {
				debugger.debug("Turkish: Bir veri dosyasi bulunamadi. Bu, eklenti ile ilk baslatmanizsa, bu normaldir.");
			    debugger.debug("English: A data file was not found. If this is your first start-up with the plugin, this is normal.");
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
