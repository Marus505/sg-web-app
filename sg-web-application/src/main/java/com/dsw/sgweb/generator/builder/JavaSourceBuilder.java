package com.dsw.sgweb.generator.builder;

/**
 * Description :
 * @author YSKim
 * @since 2013. 11. 7.
 */
public interface JavaSourceBuilder {

	String AUTHOR = "author";						// 작성자
	String CREATION_DATE = "creationDate";				// 작성일자
	String PACKAGE = "package";					// 패키지
	String BASE_PACKAGE = "basePackage";					// 프로젝트 기본 패키지. ex> com.dsw.dzz
	String MODULE_PACKAGE = "modulePackage";		// 모듈(pcweb, mobileweb) 패키지
	String CLASS_NAME = "className";				// 객체명
	String SUPER_CLASS_NAME = "superClassName";				// 객체명
	String TABLE_NAME = "tableName";				// 테이블명

	String BUSINESS_ENGLISH_NAME = "businessEnglishName";		// 업무영문명
	String BUSINESS_KOREAN_NAME = "businessKoreanName"	;	// 업무한글명

	String NAME_SPACE = "nameSpace";

	String REPOSITORY_PACKAGE = "repositoryPackage";				// Repository 패키지
	String REPOSITORY_CLASS = "repositoryClass";					// Repository 객체명 (패키지 미포함)
	String REPOSITORY_FULL_CLASS = "repositoryFullClass";			// Repository 객체명 (패키지 포함)
	String REPOSITORY_FIELD = "repositoryField";			// Repository 필드명

	String BUSINESS_PACKAGE = "businessPackage";				// BIZService 패키지
	String BUSINESS_CLASS = "businessClass";					// BIZService 객체명 (패키지 미포함)
	String BUSINESS_CLASS_FULL = "businessClassFull"	;			// BIZService 객체명 (패키지 포함)
	String BUSINESS_FIELD = "businessField";					// BIZService 필드명

	String DOMAIN_PACKAGE = "domainPackage";				// Domain 패키지
	String DOMAIN_CLASS = "domainClass";					// Domain 객체명 (패키지 미포함)
	String DOMAIN_NAME = "domainName";					// Domain 객체명 (패키지 미포함)
	String DOMAIN_CLASS_FULL = "domainClassFull";				// Domain 객체명 (패키지 포함)

	String PARAMETER_TYPES = "parameterTypes";				// Parameter Types
	String PARAMETER_VARS = "parameterVars";					// Parameter Variables
	String PARAMETER_NAMES = "parameterNames";					// Parameter Names
	String CAPITALIZED_PARAMETER_VARS = "capitalizedParameterVars";					// Parameter Variables (Capitalized)
	String PARAMETER_TYPE_AND_VARS = "parameterTypeAndVars";	// Parameter Type And Variables Set

	String THROWS = "throws";						// Throws 절

	String IMPORTS = "imports";						// 구성 Import 영역 (복수)
	String FIELDS = "fields";
	String METHODS = "methods";					// 구성 Method 영역 (복수)
	String TO_STRING_METHOD = "toStringMethod";					// 구성 toStringMethod 영역 (단수)


	String DOMAIN_ALIAS = "domainAlias";					//Domain @Alias
	String CONTROLLER_MAP_NAME = "controllerMapName";			// CONTROLLER 맵 이름
	String RESOURCE_MAP_NAME = "resourceMapName";				// RESOURCE 맵 이름
	String JSP_PREFIX = "jspPrefix";			// Controller 내의 jsp 이름 prefix
	String MODEL_NAME = "modelName";			// Resource 내의 model 이름
}
