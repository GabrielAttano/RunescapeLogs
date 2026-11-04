package com.rslogs.module;

import com.google.common.collect.ImmutableSet;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.rslogs.RunescapeLogsConfig;
import com.rslogs.loggers.boss.MadAngelLogger;
import com.rslogs.loggers.boss.ScurriusLogger;
import com.rslogs.loggers.player.PlayerAttackLogger;
import com.rslogs.loggers.LogManager;
import net.runelite.client.config.ConfigManager;

import java.util.Set;

public class RunescapeLogsModule extends AbstractModule  {

    @Override
    protected void configure() { bind(ComponentManager.class); }

    @Provides
    Set<PluginLifecycleComponent> lifecycleComponents(
        LogManager logManager,
        MadAngelLogger madAngelLogger,
        ScurriusLogger scurriusLogger,
        PlayerAttackLogger playerAttackLogger

    ) {
        return ImmutableSet.of(
                logManager,
                madAngelLogger,
                scurriusLogger,
                playerAttackLogger
        );
    }

    @Provides
    @Singleton
    RunescapeLogsConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(RunescapeLogsConfig.class);
    }
}
