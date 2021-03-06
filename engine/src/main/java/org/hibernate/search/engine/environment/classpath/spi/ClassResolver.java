/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.engine.environment.classpath.spi;

import org.hibernate.search.engine.environment.service.spi.Service;

/**
 * A resolver of Java classes.
 *
 * @author Steve Ebersole
 * @author Hardy Ferentschik
 */
public interface ClassResolver {
	/**
	 * Locate a class by name.
	 *
	 * @param className The name of the class to locate
	 * @param <T> The returned class type.
	 *
	 * @return The class reference
	 *
	 * @throws ClassLoadingException Indicates the class could not be found
	 */
	<T> Class<T> classForName(String className);

	/**
	 * Discovers and instantiates implementations of the named service contract.
	 * <p>
	 * NOTE : the term service here is used differently than {@link Service}.
	 * Instead here we are talking about services as defined by {@link java.util.ServiceLoader}.
	 *
	 * @param serviceContract The java type defining the service contract
	 * @param <T> The type of the service contract
	 *
	 * @return The ordered set of discovered services.
	 */
	<T> Iterable<T> loadJavaServices(Class<T> serviceContract);
}



