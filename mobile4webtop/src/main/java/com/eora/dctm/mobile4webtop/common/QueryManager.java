package com.eora.dctm.mobile4webtop.common;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

/**
 * 
 * @author S.Jonckheere
 * @since 1.0.0
 * 
 */
public class QueryManager {

	/** The logger instance. */
	private static final Logger LOGGER = Logger.getLogger(QueryManager.class);

	/** Resource bundle containing all DQL queries. */
	private static ResourceBundle queries = null;

	static {
		try {
			queries = ResourceBundle.getBundle("com.eora.dctm.mobile4webtop.common.mobile_queries");
			
		} catch (MissingResourceException ex){
			LOGGER.error("Queries file not found");
		}
	}

	/**
	 * Hide constructor. 
	 */
	private QueryManager() {
		// No implementation
	}

	/**
	 * Get a DQL query with the given key. Queries can be configured in the file
	 * 'queries.properties'.
	 * 
	 * @param key
	 *            the key of a query
	 * @return the corresponding query
	 */
	public static String getQuery(String key) {
		return queries.getString(key);
	}

	/**
	 * Get a DQL query with the given key and replace {0} with the given
	 * parameter.
	 * 
	 * @param key
	 *            the key of a query
	 * @param aParameter
	 *            one parameter that will be inserted into the query
	 * @return the corresponding query
	 */
	public static String getQuery(String key, String aParameter) {
		return getQuery(key, new String[] { aParameter });
	}

	/**
	 * Get a DQL query with the given key and replace {0}, {1}, etc. with the
	 * given parameters.
	 * 
	 * @param key
	 *            the key of a query
	 * @param someParameters
	 *            parameters that will be inserted into the query
	 * @return the corresponding query
	 */
	public static String getQuery(String key, String[] someParameters) {

		String query = getQuery(key);

		char singleQuote = '\'';
		char replaceChar = '¿';

		// The queries can contain single quotes, however single quotes have
		// a special meaning in MessageFormat. To avoid problems we replace
		// single quotes with replaceChar, then we use MessageFormat to
		// replace the parameters {0}, {1}, etc. and finally we replace the
		// replaceChar with the original single quotes.
		query = query.replace(singleQuote, replaceChar);
		query = MessageFormat.format(query, (Object[]) escapeParameters(someParameters));
		return query.replace(replaceChar, singleQuote);
	}

	/**
	 * Escapes the query parameters. Replaces:
	 * <ul>
	 * <li>a single quote (') by a double quote.</li>
	 * <li>a single quote (¿) by a double quote.</li>
	 * </ul>
	 * 
	 * @param parameters
	 * @return
	 */
	private static String[] escapeParameters(String[] parameters) {

		if (parameters == null) {
			return null;
		}

		for (int i = 0; i < parameters.length; i++) {
			String parameter = parameters[i];
			if (parameter != null) {
				parameters[i] = parameter.replaceAll("'", "''").replaceAll("´", "''");
			}
		}

		return parameters;
	}
}
