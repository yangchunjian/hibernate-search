/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.integrationtest.mapper.pojo.mapping.definition;

import java.lang.invoke.MethodHandles;
import java.util.List;

import org.hibernate.search.integrationtest.mapper.pojo.testsupport.util.rule.JavaBeanMappingSetupHelper;
import org.hibernate.search.mapper.pojo.bridge.ValueBridge;
import org.hibernate.search.mapper.pojo.bridge.runtime.ValueBridgeToIndexedValueContext;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.DocumentId;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.ValueBridgeBeanReference;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.ValueBridgeBuilderBeanReference;
import org.hibernate.search.util.SearchException;
import org.hibernate.search.util.impl.integrationtest.common.FailureReportUtils;
import org.hibernate.search.util.impl.integrationtest.common.rule.BackendMock;
import org.hibernate.search.util.impl.test.SubTest;

import org.junit.Rule;
import org.junit.Test;

/**
 * Test common use cases of the {@code @GenericField} annotation.
 * <p>
 * Does not test default bridges, which are tested in {@link FieldDefaultBridgeIT}.
 * <p>
 * Does not test uses of container value extractors, which are tested in {@link FieldContainerValueExtractorBaseIT}
 * (and others, see javadoc on that class).
 */
public class FieldBaseIT {

	@Rule
	public BackendMock backendMock = new BackendMock( "stubBackend" );

	@Rule
	public JavaBeanMappingSetupHelper setupHelper = new JavaBeanMappingSetupHelper( MethodHandles.lookup() );

	@Test
	public void error_unableToResolveDefaultValueBridgeFromSourceType() {
		@Indexed
		class IndexedEntity {
			Integer id;
			Object myProperty;
			@DocumentId
			public Integer getId() {
				return id;
			}
			@GenericField
			public Object getMyProperty() {
				return myProperty;
			}
		}
		SubTest.expectException(
				() -> setupHelper.withBackendMock( backendMock ).setup( IndexedEntity.class )
		)
				.assertThrown()
				.isInstanceOf( SearchException.class )
				.hasMessageMatching( FailureReportUtils.buildSingleContextFailureReportPattern()
						.typeContext( IndexedEntity.class.getName() )
						.pathContext( ".myProperty" )
						.failure(
								"Unable to find a default value bridge implementation for type '"
										+ Object.class.getName() + "'"
						)
						.build()
				);
	}

	@Test
	public void error_unableToResolveDefaultValueBridgeFromSourceType_enumSuperClassRaw() {
		@Indexed
		class IndexedEntity {
			Integer id;
			Enum myProperty;
			@DocumentId
			public Integer getId() {
				return id;
			}
			@GenericField
			public Enum getMyProperty() {
				return myProperty;
			}
		}
		SubTest.expectException(
				() -> setupHelper.withBackendMock( backendMock ).setup( IndexedEntity.class )
		)
				.assertThrown()
				.isInstanceOf( SearchException.class )
				.hasMessageMatching( FailureReportUtils.buildSingleContextFailureReportPattern()
						.typeContext( IndexedEntity.class.getName() )
						.pathContext( ".myProperty" )
						.failure(
								"Unable to find a default value bridge implementation for type 'java.lang.Enum'"
						)
						.build()
				);
	}

	@Test
	public void error_unableToResolveDefaultValueBridgeFromSourceType_enumSuperClassWithWildcard() {
		@Indexed
		class IndexedEntity {
			Integer id;
			Enum<?> myProperty;
			@DocumentId
			public Integer getId() {
				return id;
			}
			@GenericField
			public Enum<?> getMyProperty() {
				return myProperty;
			}
		}
		SubTest.expectException(
				() -> setupHelper.withBackendMock( backendMock ).setup( IndexedEntity.class )
		)
				.assertThrown()
				.isInstanceOf( SearchException.class )
				.hasMessageMatching( FailureReportUtils.buildSingleContextFailureReportPattern()
						.typeContext( IndexedEntity.class.getName() )
						.pathContext( ".myProperty" )
						.failure(
								"Unable to find a default value bridge implementation for type 'java.lang.Enum<?>'"
						)
						.build()
				);
	}

	@Test
	public void error_unableToResolveDefaultValueBridgeFromSourceType_enumSuperClassWithParameters() {
		@Indexed
		class IndexedEntity {
			Integer id;
			Enum<EnumForEnumSuperClassTest> myProperty;
			@DocumentId
			public Integer getId() {
				return id;
			}
			@GenericField
			public Enum<EnumForEnumSuperClassTest> getMyProperty() {
				return myProperty;
			}
		}
		SubTest.expectException(
				() -> setupHelper.withBackendMock( backendMock ).setup( IndexedEntity.class )
		)
				.assertThrown()
				.isInstanceOf( SearchException.class )
				.hasMessageMatching( FailureReportUtils.buildSingleContextFailureReportPattern()
						.typeContext( IndexedEntity.class.getName() )
						.pathContext( ".myProperty" )
						.failure(
								"Unable to find a default value bridge implementation for type 'java.lang.Enum<"
										+ EnumForEnumSuperClassTest.class.getName() + ">'"
						)
						.build()
				);
	}

	enum EnumForEnumSuperClassTest {
		VALUE1,
		VALUE2
	}

	@Test
	public void error_invalidInputTypeForValueBridge() {
		@Indexed
		class IndexedEntity {
			Integer id;
			@DocumentId
			@GenericField(valueBridge = @ValueBridgeBeanReference(type = MyStringBridge.class))
			public Integer getId() {
				return id;
			}
		}
		SubTest.expectException(
				() -> setupHelper.withBackendMock( backendMock ).setup( IndexedEntity.class )
		)
				.assertThrown()
				.isInstanceOf( SearchException.class )
				.hasMessageMatching( FailureReportUtils.buildSingleContextFailureReportPattern()
						.typeContext( IndexedEntity.class.getName() )
						.pathContext( ".id" )
						.failure(
								"Value bridge '" + MyStringBridge.TOSTRING + "' cannot be applied to input type '"
										+ Integer.class.getName() + "'"
						)
						.build()
				);
	}

	@Test
	public void error_invalidInputTypeForValueBridge_implicitContainerValueExtractor() {
		@Indexed
		class IndexedEntity {
			Integer id;
			List<Integer> numbers;
			@DocumentId
			public Integer getId() {
				return id;
			}
			@GenericField(valueBridge = @ValueBridgeBeanReference(type = MyStringBridge.class))
			public List<Integer> getNumbers() {
				return numbers;
			}
		}
		SubTest.expectException(
				() -> setupHelper.withBackendMock( backendMock ).setup( IndexedEntity.class )
		)
				.assertThrown()
				.isInstanceOf( SearchException.class )
				.hasMessageMatching( FailureReportUtils.buildSingleContextFailureReportPattern()
						.typeContext( IndexedEntity.class.getName() )
						.pathContext( ".numbers" )
						.failure(
								"Value bridge '" + MyStringBridge.TOSTRING + "' cannot be applied to input type '"
										+ Integer.class.getName() + "'"
						)
						.build()
				);
	}

	public static class MyStringBridge implements ValueBridge<String, String> {
		private static String TOSTRING = "<MyStringBridge toString() result>";
		@Override
		public String cast(Object value) {
			throw new UnsupportedOperationException( "Should not be called" );
		}
		@Override
		public String toIndexedValue(String value,
				ValueBridgeToIndexedValueContext context) {
			throw new UnsupportedOperationException( "Should not be called" );
		}
		@Override
		public String toString() {
			return TOSTRING;
		}
	}

	@Test
	public void error_definingBothBridgeReferenceAndBridgeBuilderReference() {
		@Indexed
		class IndexedEntity {
			Integer id;
			@DocumentId
			@GenericField(
					valueBridge = @ValueBridgeBeanReference(name = "foo"),
					valueBridgeBuilder = @ValueBridgeBuilderBeanReference(name = "bar")
			)
			public Integer getId() {
				return id;
			}
		}
		SubTest.expectException(
				() -> setupHelper.withBackendMock( backendMock ).setup( IndexedEntity.class )
		)
				.assertThrown()
				.isInstanceOf( SearchException.class )
				.hasMessageMatching( FailureReportUtils.buildSingleContextFailureReportPattern()
						.typeContext( IndexedEntity.class.getName() )
						.pathContext( ".id" )
						.annotationContextAnyParameters( GenericField.class )
						.failure(
								"Annotation @GenericField on property 'id' defines both valueBridge and valueBridgeBuilder."
										+ " Only one of those can be defined, not both."
						)
						.build()
				);
	}

}
