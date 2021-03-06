/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.backend.elasticsearch.types.projection.impl;

import java.lang.invoke.MethodHandles;

import org.hibernate.search.backend.elasticsearch.logging.impl.Log;
import org.hibernate.search.backend.elasticsearch.search.projection.impl.ElasticsearchDistanceToFieldProjectionBuilder;
import org.hibernate.search.backend.elasticsearch.search.projection.impl.ElasticsearchFieldProjectionBuilder;
import org.hibernate.search.backend.elasticsearch.types.codec.impl.ElasticsearchFieldCodec;
import org.hibernate.search.engine.backend.document.converter.FromDocumentFieldValueConverter;
import org.hibernate.search.engine.logging.spi.EventContexts;
import org.hibernate.search.engine.search.projection.spi.DistanceToFieldProjectionBuilder;
import org.hibernate.search.engine.search.projection.spi.FieldProjectionBuilder;
import org.hibernate.search.engine.spatial.GeoPoint;
import org.hibernate.search.util.impl.common.LoggerFactory;

public class ElasticsearchGeoPointFieldProjectionBuilderFactory implements ElasticsearchFieldProjectionBuilderFactory {

	private static final Log log = LoggerFactory.make( Log.class, MethodHandles.lookup() );

	private final boolean projectable;

	private final FromDocumentFieldValueConverter<? super GeoPoint, ?> converter;
	private final ElasticsearchFieldCodec<GeoPoint> codec;

	public ElasticsearchGeoPointFieldProjectionBuilderFactory(boolean projectable,
			FromDocumentFieldValueConverter<? super GeoPoint, ?> converter,
			ElasticsearchFieldCodec<GeoPoint> codec) {
		this.projectable = projectable;
		this.converter = converter;
		this.codec = codec;
	}

	@Override
	@SuppressWarnings("unchecked") // We check the cast is legal by asking the converter
	public <T> FieldProjectionBuilder<T> createFieldValueProjectionBuilder(String absoluteFieldPath,
			Class<T> expectedType) {
		checkProjectable( absoluteFieldPath, projectable );

		if ( !converter.isConvertedTypeAssignableTo( expectedType ) ) {
			throw log.invalidProjectionInvalidType( absoluteFieldPath, expectedType,
					EventContexts.fromIndexFieldAbsolutePath( absoluteFieldPath ) );
		}

		return (FieldProjectionBuilder<T>) new ElasticsearchFieldProjectionBuilder<>( absoluteFieldPath, converter, codec );
	}

	@Override
	public DistanceToFieldProjectionBuilder createDistanceProjectionBuilder(String absoluteFieldPath,
			GeoPoint center) {
		checkProjectable( absoluteFieldPath, projectable );

		return new ElasticsearchDistanceToFieldProjectionBuilder( absoluteFieldPath, center );
	}

	@Override
	public boolean isDslCompatibleWith(ElasticsearchFieldProjectionBuilderFactory obj) {
		if ( this == obj ) {
			return true;
		}
		if ( obj.getClass() != ElasticsearchGeoPointFieldProjectionBuilderFactory.class ) {
			return false;
		}

		ElasticsearchGeoPointFieldProjectionBuilderFactory other = (ElasticsearchGeoPointFieldProjectionBuilderFactory) obj;

		return projectable == other.projectable
				&& converter.isCompatibleWith( other.converter )
				&& codec.isCompatibleWith( other.codec );
	}

	private static void checkProjectable(String absoluteFieldPath, boolean projectable) {
		if ( !projectable ) {
				throw log.nonProjectableField( absoluteFieldPath,
						EventContexts.fromIndexFieldAbsolutePath( absoluteFieldPath ) );
		}
	}
}
