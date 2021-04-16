package com.dsw.sgweb.generator.domain;




/**
 * Description :
 * @author YSKim
 * @since 2013. 11. 7.
 */
public class SourceGenerator {
	private String projectName = "";
	private String author;
	private String serviceKorean;
	private String serviceEnglish;
	private String modulePackage = "";
	private String tableName;
	private String tablePrefix;
	private String basePackage;
	private String packageController;
	private String packageBizService;
	private String packageRepository;
	private String packageDomain;
	private String classController;
	private String classBizService;
	private String classRepository;
	private String classDomain;

	private String workspacePath;	// 소스가 존재하는 워크스페이스 절대경로 by K.S.JANG

	private String sourceNo;
	private String sourcePath;
	private String sourceName;
	private String sourceText;

	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getServiceKorean() {
		return serviceKorean;
	}
	public void setServiceKorean(String serviceKorean) {
		this.serviceKorean = serviceKorean;
	}
	public String getServiceEnglish() {
		return serviceEnglish;
	}
	public void setServiceEnglish(String serviceEnglish) {
		this.serviceEnglish = serviceEnglish;
	}
	public String getModulePackage() {
		return modulePackage;
	}
	public void setModulePackage(String modulePackage) {
		this.modulePackage = modulePackage;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getTablePrefix() { return tablePrefix; }
	public void setTablePrefix(String tablePrefix) { this.tablePrefix = tablePrefix; }
	public String getBasePackage() {
		return basePackage;
	}
	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}
	public String getPackageController() {
		return packageController;
	}
	public void setPackageController(String packageController) {
		this.packageController = packageController;
	}
	public String getPackageBizService() {
		return packageBizService;
	}
	public void setPackageBizService(String packageBizService) {
		this.packageBizService = packageBizService;
	}
	public String getPackageRepository() {
		return packageRepository;
	}
	public void setPackageRepository(String packageRepository) {
		this.packageRepository = packageRepository;
	}
	public String getPackageDomain() {
		return packageDomain;
	}
	public void setPackageDomain(String packageDomain) {
		this.packageDomain = packageDomain;
	}
	public String getClassController() {
		return classController;
	}
	public void setClassController(String classController) {
		this.classController = classController;
	}
	public String getClassBizService() {
		return classBizService;
	}
	public void setClassBizService(String classBizService) {
		this.classBizService = classBizService;
	}
	public String getClassRepository() {
		return classRepository;
	}
	public void setClassRepository(String classRepository) {
		this.classRepository = classRepository;
	}
	public String getClassDomain() {
		return classDomain;
	}
	public void setClassDomain(String classDomain) {
		this.classDomain = classDomain;
	}

	public String getWorkspacePath() {
		return workspacePath;
	}

	public void setWorkspacePath(String workspacePath) {
		this.workspacePath = workspacePath;
	}

	public String getSourcePath() {
		return sourcePath;
	}

	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public String getSourceText() {
		return sourceText;
	}

	public void setSourceText(String sourceText) {
		this.sourceText = sourceText;
	}

	public String getSourceNo() {
		return sourceNo;
	}

	public void setSourceNo(String sourceNo) {
		this.sourceNo = sourceNo;
	}
}
