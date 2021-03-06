/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.backend.elasticsearch.search.impl;

import org.hibernate.search.backend.elasticsearch.document.model.impl.ElasticsearchIndexSchemaFieldNode;
import org.hibernate.search.util.EventContext;
import org.hibernate.search.util.SearchException;

public interface IndexSchemaFieldNodeComponentRetrievalStrategy<T> {

	T extractComponent(ElasticsearchIndexSchemaFieldNode<?> schemaNode);

	boolean areCompatible(T component1, T component2);

	SearchException createCompatibilityException(String absoluteFieldPath,
			T component1, T component2,
			EventContext context);
}
