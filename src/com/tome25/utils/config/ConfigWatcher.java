package com.tome25.utils.config;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.function.Consumer;
import java.util.logging.Logger;

import com.tome25.utils.logging.LogTracer;

/**
 * A {@link Thread} that watches a config directory. Should any file change it
 * will notify the {@link Config}.
 * 
 * @author ToMe25
 *
 */
public class ConfigWatcher implements Runnable {

	private static final ThreadGroup WATCHERS_THREAD_GROUP = new ThreadGroup("Config-Watchers");
	private static Logger logger;
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
		if (logger == null) {
			logger = LogTracer.getLogger("Config-Watcher");
		}
		String os = System.getProperty("os.name").toLowerCase();
		if (os.contains("mac") || os.contains("darwin")) {
			logger.warning(
					"Warning: Watching for file or directory changes is neither reliable, nor fast on Mac OS X!");
		}
		thread.start();
	}

	@Override
	public void run() {
		try (WatchService watcher = FileSystems.getDefault().newWatchService()) {
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
	 * Stops this ConfigWatcher {@link Thread}.
	 */
	public void stop() {
		running = false;
		thread.interrupt();
		try {
			thread.join(500);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((consumer == null) ? 0 : consumer.hashCode());
		result = prime * result + (running ? 1231 : 1237);
		result = prime * result + ((toWatch == null) ? 0 : toWatch.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ConfigWatcher other = (ConfigWatcher) obj;
		if (consumer == null) {
			if (other.consumer != null) {
				return false;
			}
		} else if (!consumer.equals(other.consumer)) {
			return false;
		}
		if (running != other.running) {
			return false;
		}
		if (toWatch == null) {
			if (other.toWatch != null) {
				return false;
			}
		} else if (!toWatch.equals(other.toWatch)) {
			return false;
		}
		return true;
	}

}
