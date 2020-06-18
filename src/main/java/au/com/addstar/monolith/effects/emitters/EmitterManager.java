/*
 * Copyright (c) 2020. AddstarMC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 *  and associated documentation files (the "Software"), to deal in the Software without restriction,
 *  including without limitation the rights to use, copy, modify, merge, publish, distribute,
 *  sublicense, and/or copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
 * OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *
 */

package au.com.addstar.monolith.effects.emitters;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import com.google.common.collect.Lists;

public class EmitterManager {
    private final Plugin plugin;
    private BukkitTask task;

    private final List<Emitter> allEmitters;
    private final List<Emitter> activeEmitters;

    public EmitterManager(Plugin plugin) {
        this.plugin = plugin;

        allEmitters = Lists.newArrayList();
        activeEmitters = Lists.newArrayList();
    }

    public void launchTickTask() {
        task = Bukkit.getScheduler().runTaskTimer(plugin, this::doTick, 1, 1);
    }

    public void stopTickTask() {
        task.cancel();
    }

    /**
     * Adds an emitter
     *
     * @param emitter the emitter
     */
    public void addEmitter(Emitter emitter) {
        allEmitters.add(emitter);
        emitter.setManager(this);
    }

    /**
     * Removes an emitter
     *
     * @param emitter the emitter
     */
    public void removeEmitter(Emitter emitter) {
        allEmitters.remove(emitter);
        activeEmitters.remove(emitter);
        emitter.setManager(null);
    }

    /**
     * Gets all the added emitters.
     *
     * @return An unmodifiable list of emitters
     */
    public List<Emitter> getEmitters() {
        return Collections.unmodifiableList(allEmitters);
    }

    void setActive(Emitter emitter) {
        if (!activeEmitters.contains(emitter))
            activeEmitters.add(emitter);
    }

    void removeActive(Emitter emitter) {
        activeEmitters.remove(emitter);
    }

    private void doTick() {
        Iterator<Emitter> it = activeEmitters.iterator();

        while (it.hasNext()) {
            Emitter emitter = it.next();

            emitter.onTick();

            if (!emitter.isRunning())
                it.remove();
        }
    }
}
