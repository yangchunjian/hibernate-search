/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.mapper.orm.jpa;

import java.util.List;
import java.util.function.Function;

import org.hibernate.search.engine.search.SearchProjection;
import org.hibernate.search.engine.search.dsl.projection.SearchProjectionFactoryContext;
import org.hibernate.search.engine.search.dsl.query.SearchQueryResultContext;
import org.hibernate.search.mapper.pojo.search.PojoReference;

/**
 * @author Yoann Rodiere
 */
public interface FullTextQueryResultDefinitionContext<O> {

	// TODO add object loading options: ObjectLookupMethod, DatabaseRetrievalMethod, ...

	SearchQueryResultContext<? extends FullTextQuery<O>> asEntity();

	<T> SearchQueryResultContext<? extends FullTextQuery<T>> asProjection(
			Function<? super SearchProjectionFactoryContext<PojoReference, O>, SearchProjection<T>> projectionContributor);

	<T> SearchQueryResultContext<? extends FullTextQuery<T>> asProjection(SearchProjection<T> projection);

	SearchQueryResultContext<? extends FullTextQuery<List<?>>> asProjections(SearchProjection<?>... projections);
}
