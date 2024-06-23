package youtube.bartuabihd.CommandSyncBungee;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import java.util.Set;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;
import youtube.bartuabihd.CommandSyncBungee.Metrics.Graph;

public class CSS extends Plugin {

	public ServerSocket server;
	public Set<String> c = Collections.synchronizedSet(new HashSet<String>());
	public List<String> oq = Collections.synchronizedList(new ArrayList<String>());
	public Map<String, List<String>> pq = Collections.synchronizedMap(new HashMap<String, List<String>>());
	public Map<String, Integer> qc = Collections.synchronizedMap(new HashMap<String, Integer>());
	public String spacer = "@#@";
	public Debugger debugger;
	
	public void onEnable() {
		String[] data = loadConfig();
		if(data[3].equals("UNSET")) {
		    debugger.debug("!!! TURKISH: YAPILANDIRMA DOSYASI BELIRSIZ DEGERLER ICERIR - EKLENTI CALISMADAN ONCE BUNLARI DUZELTMELISIN !!! ");
		    debugger.debug("!!! ENGLISH: THE CONFIG FILE CONTAINS UNSET VALUES - YOU MUST FIX THEM BEFORE THE PLUGIN WILL WORK !!! ");
			return;
		}
		try {
			server = new ServerSocket(Integer.parseInt(data[1]), 50, InetAddress.getByName(data[0]));
			debugger.debug("Turkish: " + data[0] + ":" + data[1] + " uzerinde sunucu acildi.");
			debugger.debug("English: Opened server on " + data[0] + ":" + data[1] + ".");
			new ClientListener(this, Integer.parseInt(data[2]), data[3]).start();
		} catch(Exception e) {
			e.printStackTrace();
		}
		loadData();
		try {
		    Metrics metrics = new Metrics(this);
		    Graph graph1 = metrics.createGraph("Total queries sent");
		    graph1.addPlotter(new Metrics.Plotter() {
				public int getValue() {
					return oq.size();
				}
				public String getColumnName() {
					return "Total queries sent";
				}
			});
		    Graph graph2 = metrics.createGraph("Total servers linked");
		    graph2.addPlotter(new Metrics.Plotter() {
				public int getValue() {
					return qc.keySet().size();
				}
				public String getColumnName() {
					return "Total servers linked";
				}
			});
		    metrics.start();
		    getProxy().getPluginManager().registerListener(this, new EventListener(this));
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		this.getLogger().info("Bungee icin CommandSync etkinlestirildi! (CommandSync for Bungee was enabled!)");
        try {
            final File f = new File("plugins/CommandSync/data.txt");
            if (f.exists()) {
                if (f.delete()) {
                    this.getLogger().info(ChatColor.WHITE + "---------------------------------------------------------------");
                    this.getLogger().info(ChatColor.GREEN + "Turkish: CommandSync'den data.txt dosyasi basariyla silindi!");
                    this.getLogger().info(ChatColor.GREEN + "English: File data.txt from CommandSync was successful deleted!");
                    this.getLogger().info(ChatColor.WHITE + "---------------------------------------------------------------");              
                }
                else {
                    this.getLogger().info(ChatColor.WHITE + "-----------------------------------");
                    this.getLogger().info(ChatColor.RED +   "Turkish: Dosya data.txt silinmedi!");
                    this.getLogger().info(ChatColor.RED +   "English: File data.txt not deleted!");
                    this.getLogger().info(ChatColor.WHITE + "-----------------------------------");
                }
            }
            else {
                this.getLogger().info(ChatColor.WHITE + "------------------------------------------------------------");
                this.getLogger().info(ChatColor.YELLOW + "Turkish: Data.txt dosyasi mevcut degil! Her sey yolunda.");
                this.getLogger().info(ChatColor.YELLOW + "English: File data.txt not exist! Everything it is alright.");
                this.getLogger().info(ChatColor.WHITE + "------------------------------------------------------------");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
		
	}
	
	public void onDisable() {
		saveData();
		debugger.close();
		this.getLogger().info("Bungee icin CommandSync devre disi birakildi! (CommandSync for Bungee was disabled!)");
        try {
            final File f = new File("plugins/CommandSync/data.txt");
            if (f.exists()) {
                if (f.delete()) {
                	this.getLogger().info(ChatColor.WHITE + "---------------------------------------------------------------");
                    this.getLogger().info(ChatColor.GREEN + "Turkish: CommandSync'den data.txt dosyasi başarıyla silindi!");
                    this.getLogger().info(ChatColor.GREEN + "English: File data.txt from CommandSync was successful deleted!");
                    this.getLogger().info(ChatColor.WHITE + "---------------------------------------------------------------");              
                }
                else {
                	this.getLogger().info(ChatColor.WHITE + "-----------------------------------");
                    this.getLogger().info(ChatColor.RED +   "Turkish: Dosya data.txt silinmedi!");
                    this.getLogger().info(ChatColor.RED +   "English: File data.txt not deleted!");
                    this.getLogger().info(ChatColor.WHITE + "-----------------------------------");
                }
            }
            else {
            	this.getLogger().info(ChatColor.WHITE + "------------------------------------------------------------");
                this.getLogger().info(ChatColor.YELLOW + "Turkish: Data.txt dosyasi mevcut degil! Her şey yolunda.");
                this.getLogger().info(ChatColor.YELLOW + "English: File data.txt not exist! Everything it is alright.");
                this.getLogger().info(ChatColor.WHITE + "------------------------------------------------------------");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	private String[] loadConfig() {
		String[] defaults = new String[] {
			"ip=localhost", "port=9190", "heartbeat=1000", "pass=UNSET", "debug=false"
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
			debugger = new Debugger(this, Boolean.valueOf(data[4]));
			debugger.debug("Turkish: Yapilandirma dosyasi yuklendi.");
			debugger.debug("English: Configuration file loaded.");
		} catch(IOException e) {
			e.printStackTrace();
		}
		return data;
	}
	
	private void saveData() {
		try {
			OutputStream os = new FileOutputStream(new File(getDataFolder(), "data.txt"));
			PrintStream ps = new PrintStream(os);
			for(String s : oq) {
				ps.println("oq:" + s);
			}
			for(Entry<String, List<String>> e : pq.entrySet()) {
				String name = e.getKey();
				for(String command : e.getValue()) {
					ps.println("pq:" + name + spacer + command);
				}
			}
			for(Entry<String, Integer> e : qc.entrySet()) {
				ps.println("qc:" + e.getKey() + spacer + String.valueOf(e.getValue()));
			}
			ps.close();
			debugger.debug("Turkish: Tum veriler kaydedildi.");
			debugger.debug("English: All data saved.");
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	private void loadData() {
		try {
			File file = new File(getDataFolder(), "data.txt");
			if(file.exists()) {
				BufferedReader br = new BufferedReader(new FileReader(file));
				try {
					String l = br.readLine();
					while(l != null) {
						if(l.startsWith("oq:")) {
							oq.add(new String(l.substring(3)));
						} else if(l.startsWith("pq:")) {
							String[] parts = new String(l.substring(3)).split(spacer);
							if(pq.containsKey(parts[0])) {
								List<String> commands = pq.get(parts[0]);
								commands.add(parts[1]);
								pq.put(parts[0], commands);
							} else {
								List<String> commands = new ArrayList<String>(Arrays.asList(parts[1]));
								pq.put(parts[0], commands);
							}
						} else if(l.startsWith("qc:")) {
							String[] parts = new String(l.substring(3)).split(spacer);
							qc.put(parts[0], Integer.parseInt(parts[1]));
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
