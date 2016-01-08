package com.dhs.dto;

/**
 * Configuration class which decides the display of message on console or
 * persist the contents of file in DB
 */
public class PersistenceConfiguration {

	String persistenceStrategy;

	public String getPersistenceStrategy() {
		return persistenceStrategy;
	}

	public void setPersistenceStrategy(String persistenceStrategy) {
		this.persistenceStrategy = persistenceStrategy;
	}

}