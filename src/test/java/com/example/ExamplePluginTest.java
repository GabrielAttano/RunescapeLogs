package com.example;

import com.rslogs.RunescapeLogsPlugin;
import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class ExamplePluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(RunescapeLogsPlugin.class);
		RuneLite.main(args);
	}
}