package fr.lygaen.discord.events;

import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EventWaiter implements EventListener
{
    private final HashMap<Class<?>, Set<WaitingEvent<?>>> waitingEvents;
    private final ScheduledExecutorService threadpool;
    private final boolean shutdownAutomatically;

    public EventWaiter()
    {
        this(Executors.newSingleThreadScheduledExecutor(), true);
    }

    public EventWaiter(ScheduledExecutorService threadpool, boolean shutdownAutomatically)
    {
        Checks.notNull(threadpool, "ScheduledExecutorService");
        Checks.check(!threadpool.isShutdown(), "Cannot construct EventWaiter with a closed ScheduledExecutorService!");

        this.waitingEvents = new HashMap<>();
        this.threadpool = threadpool;
        this.shutdownAutomatically = shutdownAutomatically;
    }

    public boolean isShutdown()
    {
        return threadpool.isShutdown();
    }

    public <T extends Event> void waitForEvent(Class<T> classType, Predicate<T> condition, Consumer<T> action)
    {
        waitForEvent(classType, condition, action, -1, null, null);
    }

    public <T extends Event> void waitForEvent(Class<T> classType, Predicate<T> condition, Consumer<T> action,
                                               long timeout, TimeUnit unit, Runnable timeoutAction)
    {
        Checks.check(!isShutdown(), "Attempted to register a WaitingEvent while the EventWaiter's threadpool was already shut down!");
        Checks.notNull(classType, "The provided class type");
        Checks.notNull(condition, "The provided condition predicate");
        Checks.notNull(action, "The provided action consumer");

        WaitingEvent<?> we = new WaitingEvent<>(condition, action);
        Set<WaitingEvent<?>> set = waitingEvents.computeIfAbsent(classType, c -> new HashSet<>());
        set.add(we);

        if(timeout > 0 && unit != null)
        {
            threadpool.schedule(() ->
            {
                try
                {
                    if(set.remove(we) && timeoutAction != null)
                        timeoutAction.run();
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }, timeout, unit);
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void onEvent(@NotNull GenericEvent event) {
        Class c = event.getClass();

        while(c != null)
        {
            if(waitingEvents.containsKey(c))
            {
                Set<WaitingEvent<?>> set = waitingEvents.get(c);
                WaitingEvent[] toRemove = set.toArray(new WaitingEvent[0]);

                //noinspection unchecked
                set.removeAll(Stream.of(toRemove).filter(i -> i.attempt(event)).collect(Collectors.toSet()));
            }
            if(event instanceof ShutdownEvent && shutdownAutomatically)
            {
                threadpool.shutdown();
            }
            c = c.getSuperclass();
        }
    }

    public void shutdown()
    {
        if(shutdownAutomatically)
            throw new UnsupportedOperationException("Shutting down EventWaiters that are set to automatically close is unsupported!");

        threadpool.shutdown();
    }

    private static class WaitingEvent<T extends GenericEvent>
    {
        final Predicate<T> condition;
        final Consumer<T> action;

        WaitingEvent(Predicate<T> condition, Consumer<T> action)
        {
            this.condition = condition;
            this.action = action;
        }

        boolean attempt(T event)
        {
            if(condition.test(event))
            {
                action.accept(event);
                return true;
            }
            return false;
        }
    }
}
