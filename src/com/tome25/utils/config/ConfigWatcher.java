package com.tome25.utils.config;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.function.Consumer;

/**
 * A thread that watches a config directory.
 * 
 * @author ToMe25
 *
 */
public class ConfigWatcher implements Runnable {

	private static final ThreadGroup WATCHERS_THREAD_GROUP = new ThreadGroup("Config-Watchers");
	private final Thread thread;
	private static int nr = 0;
	private final Path toWatch;
	private boolean running = true;
	private final Consumer<File> consumer;

	/**
	 * Creates a new ConfigWatcher.
	 * 
	 * @param toWatch  the directory to watch.
	 * @param consumer the consumer to call when something changed.
	 */
	public ConfigWatcher(File toWatch, Consumer<File> consumer) {
		this(toWatch.toPath(), consumer);
	}

	/**
	 * Creates a new ConfigWatcher.
	 * 
	 * @param toWatch  the directory to watch.
	 * @param consumer the consumer to call when something changed.
	 */
	public ConfigWatcher(Path toWatch, Consumer<File> consumer) {
		thread = new Thread(WATCHERS_THREAD_GROUP, this, "Config-Watcher-Thread-" + nr);
		thread.setDaemon(true);
		nr++;
		this.toWatch = toWatch;
		this.consumer = consumer;
		thread.start();
		String os = System.getProperty("os.name").toLowerCase();
		if (os.contains("mac") || os.contains("darwin")) {
			System.out.println(
					"Warning: Watching for file or directory changes is neither reliable, nor fast on Mac OS X!");
		}
	}

	@Override
	public void run() {
		try {
			final WatchService watcher = FileSystems.getDefault().newWatchService();
			toWatch.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY,
					StandardWatchEventKinds.ENTRY_DELETE);
			while (running) {
				try {
					WatchKey key;
					try {
						key = watcher.take();
					} catch (InterruptedException x) {
						return;
					}
					for (WatchEvent<?> event : key.pollEvents()) {
						WatchEvent.Kind<?> kind = event.kind();
						if (kind == StandardWatchEventKinds.OVERFLOW) {
							continue;
						}
						@SuppressWarnings("unchecked")
						WatchEvent<Path> ev = (WatchEvent<Path>) event;
						Path changed = toWatch.resolve(ev.context());
						consumer.accept(changed.toFile());
					}
					key.reset();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Stops this ConfigWatcher thread.
	 */
	public void stop() {
		running = false;
		thread.interrupt();
	}

}
