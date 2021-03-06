package net.re_renderreality.permissions.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import net.re_renderreality.permissions.Permissions;
import net.re_renderreality.permissions.config.backend.ConfigUtils;
import net.re_renderreality.permissions.config.backend.Configurable;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

public class Mirrors implements Configurable
{
	private static Mirrors mirrors = new Mirrors();

	private Mirrors()
	{
		;
	}

	public static Mirrors getConfig()
	{
		return mirrors;
	}

	private Path configFile = Permissions.INSTANCE.getConfigPath().resolve("Mirrors.conf");
	private ConfigurationLoader<CommentedConfigurationNode> configLoader = HoconConfigurationLoader.builder().setPath(configFile).build();
	private CommentedConfigurationNode configNode;

	@Override
	public void setup()
	{
		if (!Files.exists(configFile))
		{
			try
			{
				Files.createFile(configFile);
				load();
				populate();
				save();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			load();
		}
	}
	
	@Override
	public void refresh()
	{
		load();
		populate();
		save();
	}

	@Override
	public void load()
	{
		try
		{
			configNode = configLoader.load();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void save()
	{
		try
		{
			configLoader.save(configNode);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void populate()
	{
		ArrayList<String> defaults = new ArrayList<String>();
		defaults.add("users");
		defaults.add("groups");
		String comment = "Worlds listed here have their settings mirrored in their children.\n" +
				"The first element 'world' is the main worlds name, and is the parent world.\n" +
				"subsequent elements 'world_nether' and 'world_the_end' are worlds which will use\n" +
				"the same user/groups files as the parent.\n" +
				"the element 'all_unnamed_worlds' specifies all worlds that aren't listed, and automatically mirrors them to it's parent.\n" +
				"Each child world can be configured to mirror the 'groups', 'users' or both files from its parent.";
		ConfigUtils.createCommentNode(get().getNode("Mirrors", "world"), comment);
		ConfigUtils.createNode(get().getNode("Mirrors", "world", "world_nether"), defaults);
		ConfigUtils.createNode(get().getNode("Mirrors", "world", "world_end"), defaults);
		ConfigUtils.createNode(get().getNode("Mirrors", "world", "all_unnamned_worlds"), defaults);
	}

	@Override
	public CommentedConfigurationNode get()
	{
		return configNode;
	}
}
