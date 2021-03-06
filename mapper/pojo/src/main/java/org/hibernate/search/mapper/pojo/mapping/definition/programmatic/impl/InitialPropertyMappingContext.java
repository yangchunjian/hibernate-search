/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.mapper.pojo.mapping.definition.programmatic.impl;

import org.hibernate.search.engine.environment.bean.BeanReference;
import org.hibernate.search.mapper.pojo.bridge.PropertyBridge;
import org.hibernate.search.mapper.pojo.bridge.impl.BeanBridgeBuilder;
import org.hibernate.search.mapper.pojo.bridge.mapping.BridgeBuilder;
import org.hibernate.search.mapper.pojo.bridge.mapping.MarkerBuilder;
import org.hibernate.search.mapper.pojo.mapping.building.spi.ErrorCollectingPojoPropertyMetadataContributor;
import org.hibernate.search.mapper.pojo.mapping.building.spi.PojoMappingCollectorPropertyNode;
import org.hibernate.search.mapper.pojo.mapping.building.spi.PojoMappingCollectorTypeNode;
import org.hibernate.search.mapper.pojo.mapping.building.spi.PojoTypeMetadataContributor;
import org.hibernate.search.mapper.pojo.mapping.definition.programmatic.AssociationInverseSideMappingContext;
import org.hibernate.search.mapper.pojo.mapping.definition.programmatic.IndexingDependencyMappingContext;
import org.hibernate.search.mapper.pojo.mapping.definition.programmatic.PropertyDocumentIdMappingContext;
import org.hibernate.search.mapper.pojo.mapping.definition.programmatic.PropertyFullTextFieldMappingContext;
import org.hibernate.search.mapper.pojo.mapping.definition.programmatic.PropertyGenericFieldMappingContext;
import org.hibernate.search.mapper.pojo.mapping.definition.programmatic.PropertyIndexedEmbeddedMappingContext;
import org.hibernate.search.mapper.pojo.mapping.definition.programmatic.PropertyKeywordFieldMappingContext;
import org.hibernate.search.mapper.pojo.mapping.definition.programmatic.PropertyMappingContext;
import org.hibernate.search.mapper.pojo.mapping.definition.programmatic.TypeMappingContext;
import org.hibernate.search.mapper.pojo.model.additionalmetadata.building.spi.PojoAdditionalMetadataCollectorPropertyNode;
import org.hibernate.search.mapper.pojo.model.additionalmetadata.building.spi.PojoAdditionalMetadataCollectorTypeNode;
import org.hibernate.search.mapper.pojo.model.path.PojoModelPathValueNode;
import org.hibernate.search.mapper.pojo.model.spi.PropertyHandle;

public class InitialPropertyMappingContext
		implements PropertyMappingContext, PojoTypeMetadataContributor {

	private final TypeMappingContext parent;
	private final PropertyHandle propertyHandle;

	private final ErrorCollectingPojoPropertyMetadataContributor children =
			new ErrorCollectingPojoPropertyMetadataContributor();

	InitialPropertyMappingContext(TypeMappingContext parent, PropertyHandle propertyHandle) {
		this.parent = parent;
		this.propertyHandle = propertyHandle;
	}

	@Override
	public void contributeModel(PojoAdditionalMetadataCollectorTypeNode collector) {
		PojoAdditionalMetadataCollectorPropertyNode collectorPropertyNode =
				collector.property( propertyHandle.getName() );
		children.contributeModel( collectorPropertyNode );
	}

	@Override
	public void contributeMapping(PojoMappingCollectorTypeNode collector) {
		PojoMappingCollectorPropertyNode collectorPropertyNode = collector.property( propertyHandle );
		children.contributeMapping( collectorPropertyNode );
	}

	@Override
	public PropertyDocumentIdMappingContext documentId() {
		PropertyDocumentIdMappingContextImpl child = new PropertyDocumentIdMappingContextImpl( this );
		children.add( child );
		return child;
	}

	@Override
	public PropertyMappingContext property(String propertyName) {
		return parent.property( propertyName );
	}

	@Override
	public PropertyMappingContext bridge(Class<? extends PropertyBridge> bridgeClass) {
		return bridge( BeanReference.of( bridgeClass ) );
	}

	@Override
	public PropertyMappingContext bridge(BeanReference<? extends PropertyBridge> bridgeReference) {
		return bridge( new BeanBridgeBuilder<>( bridgeReference ) );
	}

	@Override
	public PropertyMappingContext bridge(BridgeBuilder<? extends PropertyBridge> builder) {
		children.add( new PropertyBridgeMappingContributor( builder ) );
		return this;
	}

	@Override
	public PropertyMappingContext marker(MarkerBuilder builder) {
		children.add( new MarkerMappingContributor( builder ) );
		return this;
	}

	@Override
	public PropertyGenericFieldMappingContext genericField() {
		return genericField( null );
	}

	@Override
	public PropertyGenericFieldMappingContext genericField(String relativeFieldName) {
		PropertyGenericFieldMappingContextImpl child = new PropertyGenericFieldMappingContextImpl( this, relativeFieldName );
		children.add( child );
		return child;
	}

	@Override
	public PropertyFullTextFieldMappingContext fullTextField() {
		return fullTextField( null );
	}

	@Override
	public PropertyFullTextFieldMappingContext fullTextField(String relativeFieldName) {
		PropertyFullTextFieldMappingContextImpl child = new PropertyFullTextFieldMappingContextImpl( this, relativeFieldName );
		children.add( child );
		return child;
	}

	@Override
	public PropertyKeywordFieldMappingContext keywordField() {
		return keywordField( null );
	}

	@Override
	public PropertyKeywordFieldMappingContext keywordField(String relativeFieldName) {
		PropertyKeywordFieldMappingContextImpl child = new PropertyKeywordFieldMappingContextImpl( this, relativeFieldName );
		children.add( child );
		return child;
	}

	@Override
	public PropertyIndexedEmbeddedMappingContext indexedEmbedded() {
		PropertyIndexedEmbeddedMappingContextImpl child = new PropertyIndexedEmbeddedMappingContextImpl( this );
		children.add( child );
		return child;
	}

	@Override
	public AssociationInverseSideMappingContext associationInverseSide(PojoModelPathValueNode inversePath) {
		AssociationInverseSideMappingContextImpl child = new AssociationInverseSideMappingContextImpl(
				this, inversePath
		);
		children.add( child );
		return child;
	}

	@Override
	public IndexingDependencyMappingContext indexingDependency() {
		IndexingDependencyMappingContextImpl child = new IndexingDependencyMappingContextImpl( this );
		children.add( child );
		return child;
	}
}
