package com.rslogs.module;

import com.rslogs.RunescapeLogsConfig;

public interface PluginLifecycleComponent {

    default boolean isEnabled(RunescapeLogsConfig config)
    {
        return true;
    }

    void startUp();

    void shutDown();
}
