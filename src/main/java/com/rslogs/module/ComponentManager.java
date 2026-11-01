package com.rslogs.module;

import com.rslogs.RunescapeLogsConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.util.GameEventManager;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@Slf4j
public class ComponentManager {
    private final EventBus eventBus;
    private final GameEventManager gameEventManager;
    private final RunescapeLogsConfig config;

    private final Set<PluginLifecycleComponent> components;
    private final Map<PluginLifecycleComponent, Boolean> states = new HashMap<>();

    public void onPluginStart() {
        eventBus.register(this);
        components.forEach(c -> states.put(c, false));
        revalidateComponentStates();
    }

    public void onPluginStop() {
        eventBus.unregister(this);
        components.stream()
                .filter(states::get)
                .forEach(this::tryShutDown);
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged e)
    {
        if (!RunescapeLogsConfig.CONFIG_GROUP.equals(e.getGroup()))
        {
            return;
        }

        revalidateComponentStates();
    }

    private void revalidateComponentStates()
    {
        components.forEach(c ->
        {
            boolean shouldBeEnabled = c.isEnabled(config);
            boolean isEnabled = states.get(c);
            if (shouldBeEnabled == isEnabled)
            {
                return;
            }

            if (shouldBeEnabled)
            {
                tryStartUp(c);
            }
            else
            {
                tryShutDown(c);
            }
        });
    }

    private void tryStartUp(PluginLifecycleComponent component)
    {
        if (states.get(component))
        {
            return;
        }

        if (log.isDebugEnabled())
        {
            log.debug("Enabling RsLogs plugin component [{}]", component.getClass().getName());
        }

        try
        {
            component.startUp();
            gameEventManager.simulateGameEvents(component);
            states.put(component, true);
        }
        catch (Throwable e)
        {
            log.error("Failed to start RsLogs plugin component [{}]", component.getClass().getName(), e);
        }
    }

    private void tryShutDown(PluginLifecycleComponent component)
    {
        if (!states.get(component))
        {
            return;
        }

        if (log.isDebugEnabled())
        {
            log.debug("Disabling RsLogs plugin component [{}]", component.getClass().getName());
        }

        try
        {
            component.shutDown();
        }
        catch (Throwable e)
        {
            log.error("Failed to cleanly shut down RsLogs plugin component [{}]", component.getClass().getName());
        }
        finally
        {
            states.put(component, false);
        }
    }
}
