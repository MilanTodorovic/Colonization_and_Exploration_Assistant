/*
 * Copyright (c) 2008, 2010, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package core.data;

import javafx.beans.property.*;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.*;


// TODO changing watched file needs to destroy this thread
//  https://stackoverflow.com/questions/57979834/javafx-how-to-create-a-new-stage-open-new-window-when-a-file-is-created-in-s
//  https://docs.oracle.com/javase/tutorial/essential/io/notification.html

public class ListenForSaveUpdate extends ScheduledService<List<WatchEvent<Path>>> {

    private final WatchService watcher;
    private final Map<WatchKey,Path> keys;
    private boolean trace = false;

    // TODO don't forget to change these two
    private final String queue = "";
    private final String saveFileName = "";


    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>)event;
    }

    /**
     * Creates a WatchService and registers the given directory
     */
    public ListenForSaveUpdate(Path dir) throws IOException {
        this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<WatchKey,Path>();

        //Path _dir = Paths.get(dir); TODO if the file explorer returns a string, else ignore

        register(dir);

        // enable trace after initial registration
        this.trace = true;
    }

    /**
     * Register the given directory with the WatchService
     */
    private void register(Path dir) throws IOException {
        // TODO change all system.out to either print to Log or show pop-ups
        WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        if (trace) {
            Path prev = keys.get(key);
            if (prev == null) {
                System.out.format("register: %s\n", dir);
            } else {
                if (!dir.equals(prev)) {
                    System.out.format("update: %s -> %s\n", prev, dir);
                }
            }
        }
        keys.put(key, dir);
    }

    @Override
    protected Task<List<WatchEvent<Path>>> createTask() {
        return new WatchTask();
    }

    /**
     * Process all events for keys queued to the watcher
     */
    class WatchTask extends Task<List<WatchEvent<Path>>> {
        @Override
        protected List<WatchEvent<Path>> call() {
            // wait for key to be signalled
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                if (isCancelled()) {
                    updateMessage("Cancelled");
                }

                return Collections.emptyList();
            }

            Path dir = keys.get(key);
            if (dir == null) {
                System.err.println("WatchKey not recognized");
                return Collections.emptyList();
            }

            List<WatchEvent<Path>> interestingEvents = new ArrayList<>();
            for (WatchEvent<?> event: key.pollEvents()) {
                WatchEvent.Kind kind = event.kind();

                if (kind == OVERFLOW) {
                    continue;
                }

                // Context for directory entry event is the file name of entry
                WatchEvent<Path> pathWatchEvent = cast(event);
                interestingEvents.add(pathWatchEvent);

            }

            // reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);

                // if all directories are inaccessible
                // even the root watch directory
                // might wight want to cancel the service.
                if (keys.isEmpty()) {
                    // TODO log or ignore?
                    System.out.println("No directories being watched");
                }
            }

            return Collections.unmodifiableList(
                    interestingEvents
            );
        }
    }
}