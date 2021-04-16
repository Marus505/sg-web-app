package com.dsw.sgweb.generator.web;

import com.dsw.core.util.builder.SourceBuilder.Utility;
import com.dsw.core.util.db.sql.builder.SqlSource;
import com.dsw.sgweb.generator.domain.SourceGenerator;
import com.dsw.sgweb.generator.service.SourceGeneratorService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description : 소스 제네레이터
 * @author YSKim
 * @since 2013. 11. 6.
 */
@Controller
public class SourceGeneratorController {

	@Autowired
	private SourceGeneratorService service;

	@Autowired
	private Environment common; //application.properties 객체

	private final static String PRE_PACKAGE_STR = "";

	private final static String[] SOURCE_TYPE_LIST = {"sql", "domain", "repository", "service", "resource", "controller", "htmlList", "jsApp", "jsList"};    // 생성 파일 타입 목록 (순서 및 이름 jsp와 일치해야함)

	@RequestMapping(value = {"/", "/sourceGenerator"}, method = RequestMethod.GET)
	public String sourceGeneratorConfig(@ModelAttribute("vo") SourceGenerator vo, ModelMap model) throws SQLException, JsonProcessingException {
		List<String> list = service.getTableNames();

		ObjectMapper mapper = new ObjectMapper();
		String tableNames = mapper.writeValueAsString(list);
		model.addAttribute("tableNames", tableNames);
		model.addAttribute("projectName", common.getProperty("project.name"));
		model.addAttribute("basePackage", common.getProperty("project.base-package"));
		return "/source-generator";
	}

	@RequestMapping(value = "/sourceGeneratorResult", method = RequestMethod.POST)
	public @ResponseBody
	Map<String, Object> sourceGeneratorResult(@RequestBody SourceGenerator vo) {
		Map<String, Object> resultMap = new HashMap();

		Object[] targetFileList = new Object[SOURCE_TYPE_LIST.length];

		SqlSource sqlSource = service.getSqlSource(vo);

		// 1. SQL Query 생성
		targetFileList[0] = makeSql(vo, sqlSource);
		// 2. Domain 생성
		targetFileList[1] = makeDomain(vo, sqlSource);
		// 3. Repository 생성
		targetFileList[2] = makeRepository(vo, sqlSource);
		// 4. Service 생성
		targetFileList[3] = makeService(vo, sqlSource);
		// 5. REST API Controller 생성
		targetFileList[4] = makeResource(vo, sqlSource);
		// Controller 생성
		//targetFileList[4] = makeController(vo, sqlSource);

		resultMap.put("targetFileList", targetFileList);
		resultMap.put("result", true);
		return resultMap;
	}

	@RequestMapping(value = "/sourceGeneratorFile", method = RequestMethod.POST)
	public @ResponseBody
    Map<String, Object> sourceGeneratorFile(@RequestBody SourceGenerator vo) {
		Map<String, Object> resultMap = new HashMap();

		String sourcePath = vo.getSourcePath();
		String sourceName = vo.getSourceName();
		String sourceText = vo.getSourceText();

		if(sourceName.contains(common.getProperty("project.base-package"))) { // Java File
			sourcePath = sourcePath + "/" + sourceName.substring(0, sourceName.lastIndexOf(".")).replaceAll("\\.", "\\/");
			sourceName = sourceName.substring(sourceName.lastIndexOf(".") + 1) + ".java";
		}

		String fullPath = sourcePath + "/" + sourceName;
		resultMap.put("sourceNo", vo.getSourceNo());
		resultMap.put("sourceFullPath", fullPath);

		try {
			// 파일 경로 생성
			File filePath = new File(sourcePath);
			if(!filePath.exists()) {
				//없다면 생성
				filePath.mkdirs();
			} else {
				//경로가 존재한다면 파일도 존재하는지 확인
				File file = new File(fullPath);
				if(file.exists()) {
					//존재하면 생성 중단
					resultMap.put("result", "exist");
					return resultMap;
				}
			}

			// 파일 객체 생성
			Writer outFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fullPath), "utf-8"));

			// 파일안에 문자열 쓰기
			outFile.write(sourceText);
			outFile.flush();
			outFile.close();
			resultMap.put("result", "success");
		} catch (Exception e) {
			resultMap.put("result", "fail");
		} finally {
			return resultMap;
		}
	}

	/**
	 * SQL Query 생성
	 *
	 * @param vo
	 * @return
	 */
	private Map<String, Object> makeSql(SourceGenerator vo, SqlSource sqlSource) {
		Map<String, Object> resultMap = new HashMap();

		// 결과값 생성
		resultMap.put("resultPath", PRE_PACKAGE_STR + "core/src/main/resources/mybatis/sql");
		resultMap.put("resultName", vo.getServiceEnglish() + "Repository.xml");
		resultMap.put("resultSource", service.getSqlXmlSourceString(vo, sqlSource));

		return resultMap;
	}

	/**
	 * DOMAIN 생성
	 *
	 * @param vo
	 * @return
	 */
	private Map<String, Object> makeDomain(SourceGenerator vo, SqlSource sqlSource) {
		Map<String, Object> resultMap = new HashMap();

		// 결과값 생성
		resultMap.put("resultPath", PRE_PACKAGE_STR + "/core/src/main/java");
		resultMap.put("resultName", vo.getPackageDomain() + "." + vo.getClassDomain());
		resultMap.put("resultSource", service.getDomainString(vo, sqlSource));

		return resultMap;
	}

	/**
	 * REPOSITORY 생성
	 *
	 * @param vo
	 * @return
	 */
	private Map<String, Object> makeRepository(SourceGenerator vo, SqlSource sqlSource) {
		Map<String, Object> resultMap = new HashMap();

		// 결과값 생성
		resultMap.put("resultPath", PRE_PACKAGE_STR + "/core/src/main/java");
		resultMap.put("resultName", vo.getPackageRepository() + "." + vo.getClassRepository());
		resultMap.put("resultSource", service.getRepositoryString(vo, sqlSource));

		return resultMap;
	}

	/**
	 * Service 생성
	 *
	 * @param vo
	 * @return
	 */
	private Map<String, Object> makeService(SourceGenerator vo, SqlSource sqlSource) {
		Map<String, Object> resultMap = new HashMap();

		// 결과값 생성
		resultMap.put("resultPath", PRE_PACKAGE_STR + "/core/src/main/java");
		resultMap.put("resultName", vo.getPackageBizService() + "." + vo.getClassBizService());
		resultMap.put("resultSource", service.getBusinessServiceImplString(vo, sqlSource));

		return resultMap;
	}

	/**
	 * REST API Controller 생성
	 *
	 * @param vo
	 * @return
	 */
	private Map<String, Object> makeResource(SourceGenerator vo, SqlSource sqlSource) {
		Map<String, Object> resultMap = new HashMap();

		// 결과값 생성
		resultMap.put("resultPath", PRE_PACKAGE_STR + "/hb-bo-back/src/main/java");
		resultMap.put("resultName", vo.getPackageController() + "." + vo.getClassController());
		resultMap.put("resultSource", service.getResourceString(vo, sqlSource));

		return resultMap;
	}

	/**
	 * Controller 생성
	 *
	 * @param vo
	 * @return
	 */
	private Map<String, Object> makeController(SourceGenerator vo, SqlSource sqlSource) {
		Map<String, Object> resultMap = new HashMap();

		// 결과값 생성
		resultMap.put("resultPath", PRE_PACKAGE_STR + "/hb-fo-web/src/main/java");
		resultMap.put("resultName", vo.getPackageController() + "." + vo.getClassController());
		resultMap.put("resultSource", service.getControllerString(vo, sqlSource));

		return resultMap;
	}

	/**
	 * HTML List 생성
	 *
	 * @param vo
	 * @return
	 */
	private Map<String, Object> makeHtmlList(SourceGenerator vo, SqlSource sqlSource) {
		Map<String, Object> resultMap = new HashMap();

		// 결과값 생성
		resultMap.put("resultPath", PRE_PACKAGE_STR + "/admin/src/main/webapp/app/" + vo.getModulePackage().replaceAll("\\.", "\\/") + "/views");
		resultMap.put("resultName", Utility.toFieldName(vo.getServiceEnglish()) + "List.html");
		resultMap.put("resultSource", service.getHtmlListString(vo, sqlSource));

		return resultMap;
	}

	/**
	 * Angular Module 생성
	 *
	 * @param vo
	 * @return
	 */
	private Map<String, Object> makeJsApp(SourceGenerator vo, SqlSource sqlSource) {
		Map<String, Object> resultMap = new HashMap();

		// 결과값 생성
		resultMap.put("resultPath", PRE_PACKAGE_STR + "/admin/src/main/webapp/app/" + vo.getModulePackage().replaceAll("\\.", "\\/"));
		resultMap.put("resultName", Utility.toFieldName(vo.getServiceEnglish()) + "Module.js");
		resultMap.put("resultSource", service.getJsAppString(vo, sqlSource));

		return resultMap;
	}

	/**
	 * Angular List 생성
	 *
	 * @param vo
	 * @return
	 */
	private Map<String, Object> makeJsList(SourceGenerator vo, SqlSource sqlSource) {
		Map<String, Object> resultMap = new HashMap();

		// 결과값 생성
		resultMap.put("resultPath", PRE_PACKAGE_STR + "/admin/src/main/webapp/app/" + vo.getModulePackage().replaceAll("\\.", "\\/"));
		resultMap.put("resultName", Utility.toFieldName(vo.getServiceEnglish()) + "ListCtrl.js");
		resultMap.put("resultSource", service.getJsListString(vo, sqlSource));

		return resultMap;
	}
}
