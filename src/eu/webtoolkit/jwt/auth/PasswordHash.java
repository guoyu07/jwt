/*
 * Copyright (C) 2009 Emweb bvba, Leuven, Belgium.
 *
 * See the LICENSE file for terms of use.
 */
package eu.webtoolkit.jwt.auth;

import java.util.*;
import java.util.regex.*;
import java.io.*;
import java.lang.ref.*;
import java.util.concurrent.locks.ReentrantLock;
import javax.servlet.http.*;
import javax.servlet.*;
import eu.webtoolkit.jwt.*;
import eu.webtoolkit.jwt.chart.*;
import eu.webtoolkit.jwt.utils.*;
import eu.webtoolkit.jwt.servlet.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A password hash.
 * <p>
 * 
 * This combines the information for interpreting a hashed password:
 * <ul>
 * <li>the hash value</li>
 * <li>the salt used</li>
 * <li>the hashing function used</li>
 * </ul>
 * <p>
 * 
 * @see HashFunction#compute(String msg, String salt)
 */
public class PasswordHash {
	private static Logger logger = LoggerFactory.getLogger(PasswordHash.class);

	/**
	 * Default constructor.
	 * <p>
	 * Creates an empty password hash, i.e. with empty function, salt and value.
	 */
	public PasswordHash() {
		this.function_ = "";
		this.salt_ = "";
		this.value_ = "";
	}

	/**
	 * Constructor.
	 */
	public PasswordHash(final String function, final String salt,
			final String value) {
		this.function_ = function;
		this.salt_ = salt;
		this.value_ = value;
	}

	/**
	 * Returns whether the password is empty.
	 */
	public boolean isEmpty() {
		return this.value_.length() == 0;
	}

	/**
	 * Returns the function identifier.
	 */
	public String getFunction() {
		return this.function_;
	}

	/**
	 * Returns the salt.
	 */
	public String getSalt() {
		return this.salt_;
	}

	/**
	 * Returns the hash value.
	 */
	public String getValue() {
		return this.value_;
	}

	private String function_;
	private String salt_;
	private String value_;
}
