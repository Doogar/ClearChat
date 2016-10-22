package plugins.astro.clearchat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ClearChat extends JavaPlugin implements Listener
{
	public void onEnable()
	{
		super.onEnable();
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);
		saveDefaultConfig();
		config.addDefault("default.prefix", "&8[&c&lClearChat&8]");
		config.addDefault("default.clearmsg", "&c%displayname &6has cleared the chat.");

		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(this, this);
	}

	public void onDisable()
	{
		super.onDisable();
	}

	public static String createPlaceholders(Player player, String format)
	{
		return format.replaceAll("%displayname", player.getDisplayName()).replaceAll("%name", player.getName());
	}

	public static void clearChat(Player player, String prefix, String message)
	{
		for (int i = 0; i < 105; i++) 
		{
			Bukkit.broadcastMessage("");
		}
		String clearMsg = prefix + " " + message;
		Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', createPlaceholders(player, clearMsg)));
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		Player player = (Player)sender;
		if (cmd.getName().equalsIgnoreCase("cc"))
		{
			String prefix = getConfig().getString("default.prefix");
			String message = getConfig().getString("default.clearmsg");

			if (sender.hasPermission("cc.use")) 
			{
				if (args.length == 0)
				{
					clearChat(player, prefix, message);
				}
				else
				{
					String newArgPrefix;
					if (args[0].equalsIgnoreCase("add"))
					{
						String newArgName = args[1];
						newArgPrefix = args[2];

						StringBuilder buffer = new StringBuilder();
						for (int i = 3; i < args.length; i++) 
						{
							buffer.append(args[i]).append(' ');
						}
						String newArgClearMsg = buffer.toString();

						getConfig().set("customargs." + newArgName.toLowerCase() + ".prefix", newArgPrefix);
						getConfig().set("customargs." + newArgName.toLowerCase() + ".clearmsg", newArgClearMsg);
						saveConfig();
						reloadConfig();
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Successfully added " + newArgName + " &6with Prefix: " + newArgPrefix + " &6and Clear Message: " + newArgClearMsg));
					}
					else if (args[0].equalsIgnoreCase("remove"))
					{
						if (args.length > 2)
						{
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + " &cError: Too many arguments! /cc help."));
						}
						else if (args.length < 2)
						{
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + " &cError: Not enough arguments! /cc help."));
						}
						else if (getConfig().getConfigurationSection("customargs").getKeys(false).contains(args[1]))
						{
							getConfig().set("customargs." + args[1].toLowerCase(), null);
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + " &6Sucessfully removed argument: " + args[1].toLowerCase() + " &6from config!"));
							saveConfig();
							reloadConfig();
						}
						else
						{
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + " &cError: That argument does not exist!"));
						}
					}
					else if (args[0].equalsIgnoreCase("list"))
					{
						if (args.length > 1)
						{
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + " &cError: Too many arguments! /cc help."));
						}
						else
						{
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + " &6Clear Chat Argument List"));
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6All previews are &cbelow the argument to use!"));
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8&m+&c&l&m-------------------------&8+"));
							if(getConfig().getConfigurationSection("customargs") != null)
							{
								if(!getConfig().getConfigurationSection("customargs").getKeys(false).isEmpty())
								{
									for (String s : getConfig().getConfigurationSection("customargs").getKeys(false))
									{
										String clearprefix = getConfig().getString("customargs." + s + ".prefix");
										String clearmsg = getConfig().getString("customargs." + s + ".clearmsg");
										sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Argument to use: &c" + s));
										sender.sendMessage(ChatColor.translateAlternateColorCodes('&', clearprefix + " " + clearmsg));
										sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8&m+&c&l&m-------------------------&8+"));
									}
								}
								else
								{
									sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cError: No custom arguments have been set! /cc help to learn more."));
								}
							}
							else
							{
								sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cError: No custom arguments have been set! /cc help to learn more."));
							}
						}
					}
					else if (args[0].equalsIgnoreCase("reload"))
					{
						if (args.length > 1) 
						{
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + " &cError: Too many arguments! /cc help."));
						} 
						else if (args.length < 1) 
						{
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + " &cError: Not enough arguments! /cc help."));
						} 
						else 
						{
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + " &6Reloaded ClearChat from config."));
							reloadConfig();
						}
					}
					else if (args[0].equalsIgnoreCase("help"))
					{
						if (args.length > 1)
						{
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + " &cError: Too many arguments! /cc help."));
						}
						else if (args.length < 1)
						{
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + " &cError: Not enough arguments! /cc help."));
						}
						else
						{
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + " &6Clear Chat Help Menu"));
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8&m+&c&l&m-------------------------&8+"));
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8+ &6/cc help &c-- &6Displays this menu!"));
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8+ &6/cc list &c-- &6Displays a list of valid arguments you can use!"));
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8+ &6/cc add <arg> <prefix> <clearmsg> &c-- &6Adds a new Clear Chat argument!"));
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8+ &6/cc remove <arg> &c-- &6Removes a Clear Chat argument!"));
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8+ &6/cc reload &c-- &6Reloads custom arguments from the config!"));
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8+ &6Coded by: &bAstro"));
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8&m+&c&l&m-------------------------&8+"));
						}
					}
					else if ((getConfig().getString("customargs." + args[0].toLowerCase()) != null) && (getConfig().getString("customargs." + args[0].toLowerCase() + ".prefix") != null) && (getConfig().getString("customargs." + args[0].toLowerCase() + ".clearmsg") != null))
					{
						String clearprefix = getConfig().getString("customargs." + args[0].toLowerCase() + ".prefix");
						String clearmessage = getConfig().getString("customargs." + args[0].toLowerCase() + ".clearmsg");
						clearChat(player, clearprefix, clearmessage);
					}
					else
					{
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + " &cError: Invalid arguments! /cc help."));
					}
				}
			}
		}
		return true;
	}
}
