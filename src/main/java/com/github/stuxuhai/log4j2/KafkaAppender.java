package com.github.stuxuhai.log4j2;

import java.io.Serializable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.util.Booleans;

@Plugin(name = "Kafka", category = "Core", elementType = "appender", printObject = true)
public final class KafkaAppender extends AbstractAppender {

	private final Lock lock = new ReentrantLock();

	private KafkaManager manager;

	protected KafkaAppender(String name, Filter filter, boolean ignoreExceptions, KafkaManager manager) {
		super(name, filter, null, ignoreExceptions);
		this.manager = manager;
	}

	@PluginFactory
	public static KafkaAppender createAppender(@PluginAttribute("name") final String name, @PluginElement("Filter") final Filter filter,
			@PluginAttribute("ignoreExceptions") final String ignore, @PluginAttribute("topic") final String topic,
			@PluginElement("Properties") final Property[] properties) {
		boolean ignoreExceptions = Booleans.parseBoolean(ignore, true);
		KafkaManager kafkaManager = KafkaManager.getKafkaManager(name, topic, properties);
		if (kafkaManager == null) {
			return null;
		}
		return new KafkaAppender(name, filter, ignoreExceptions, kafkaManager);
	}

	@Override
	public final void start() {
		if (this.getManager() == null) {
			LOGGER.error("No KafkaManager set for the appender named [{}].", this.getName());
		}
		super.start();
		if (this.getManager() != null) {
			this.getManager().startup();
		}
	}

	@Override
	public final void stop() {
		super.stop();
		if (this.getManager() != null) {
			this.getManager().release();
		}
	}

	public final KafkaManager getManager() {
		return this.manager;
	}

	public void append(LogEvent event) {
		this.lock.lock();
		try {
			this.getManager().send(event.getMessage().getFormattedMessage());
		} catch (final Exception e) {
			LOGGER.error("Unable to write to kafka [{}] for appender [{}].", this.getManager().getName(), this.getName(), e);
			throw new AppenderLoggingException("Unable to write to kafka in appender: " + e.getMessage(), e);
		} finally {
			this.lock.unlock();
		}
	}

	@Override
	public Layout<? extends Serializable> getLayout() {
		return null;
	}

}
