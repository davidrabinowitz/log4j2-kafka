/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2014 All Rights Reserved.
 *
 * Author: wuya
 * Create Date: 2014年10月23日 上午9:28:11
 */
package com.github.stuxuhai.log4j2;

import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import org.apache.logging.log4j.core.appender.AbstractManager;
import org.apache.logging.log4j.core.config.Property;

/**
 * @author wuya
 *
 */
public class KafkaManager extends AbstractManager {

	private ProducerConfig config;
	private Producer<String, String> producer;
	private final String topic;

	protected KafkaManager(String name, String topic, Property[] properties) {
		super(name);
		this.topic = topic;
		Properties props = new Properties();
		for (Property property : properties) {
			props.put(property.getName(), property.getValue());
		}
		this.config = new ProducerConfig(props);
	}

	public static KafkaManager getKafkaManager(String name, String topic, Property[] properties) {
		return new KafkaManager(name, topic, properties);
	}

	public void startup() {
		producer = new Producer<String, String>(config);
	}

	public void send(String msg) {
		if (producer != null) {
			KeyedMessage<String, String> data = new KeyedMessage<String, String>(topic, msg);
			producer.send(data);
		}
	}

	@Override
	public final void releaseSub() {
		if (producer != null) {
			producer.close();
		}
	}

}
