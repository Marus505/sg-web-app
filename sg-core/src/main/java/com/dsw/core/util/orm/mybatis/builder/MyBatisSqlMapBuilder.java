package com.dsw.core.util.orm.mybatis.builder;

import com.dsw.core.util.builder.SourceBuilder;
import com.dsw.core.util.builder.SourceBuilderProperties;
import com.dsw.core.util.builder.SourceBuilderUtil;
import com.dsw.core.util.builder.template.XmlSourceTemplate;
import com.dsw.core.util.db.sql.builder.FieldExpression;
import com.dsw.core.util.db.sql.builder.SqlSource;

/**
 * Class Name : MyBatisSqlMapBuilder.java
 * Description : MyBatisSqlMapBuilder class
 * Modification Information
 *
 * @author Y.S.Kim
 * @since 2013. 11. 14.
 * @version 1.0
 *
 */
public class MyBatisSqlMapBuilder implements SourceBuilder {

	/**
	 * Template 파일 (tpl)
	 */
	public final String TEMPLATE;

	/**
	 * Template Loading Encoding
	 */
	private final String ENCODING;

	/**
	 * Generated SqlSource
	 */
	public static final String PROP_SQL_SOURCE = "PROP_SQL_SOURCE";

	/**
	 * SqlMap Root Node Name Space
	 */
	public static final String PROP_NAMESPACE = "PROP_NAMESPACE";

	/**
	 * Value Object Package
	 */
	public static final String PROP_DOMAIN_PACKAGE_NAME = "PROP_DOMAIN_PACKAGE_NAME";

	/**
	 * Value Object Class Name
	 */
	public static final String PROP_DOMAIN_CLASS_NAME = "PROP_DOMAIN_CLASS_NAME";

	/**
	 * Business Name
	 */
	public static final String PROP_BUSINESS_NAME = "PROP_BUSINESS_NAME";

	/**
	 * Title
	 */
	public static final String PROP_TITLE = "PROP_TITLE";

	/**
	 * Author
	 */
	public static final String PROP_AUTHOR = "PROP_AUTHOR";

	/**
	 * Build Select Statements
	 */
	public static final String PROP_BUILD_OPTION = "PROP_BUILD_OPTION";


	public static final String PROP_REPOSITORY_PACKAGE_NAME = "PROP_REPOSITORY_PACKAGE_NAME";

	public static final String PROP_REPOSITORY_CLASS_NAME = "PROP_REPOSITORY_CLASS_NAME";

	/**
	 * PARAMETER_CLASS_CUSTOM 이나 RESULT_CLASS_CUSTOM 이 0이 아닌경우
	 * PROP_PARAMETER_CLASS_TYPE 이나 PROP_RESULT_CLASS_TYPE 으로 지정
	 */
	public static final String MAP = "map";


	/**
	 * Generated XML Source
	 */
	private StringBuffer source;

	/**
	 * Generated SQL Source
	 */
	private SqlSource sqlSource;

	/**
	 *
	 */
	public MyBatisSqlMapBuilder(){
		this.TEMPLATE = "/static/tpl/myBatisSqlMapTemplate.template";
		this.ENCODING = "UTF-8";
	}

	/**
	 * @param template
	 * @param encoding
	 */
	public MyBatisSqlMapBuilder(String template, String encoding){
		if(template == null){
			this.TEMPLATE = "/static/tpl/myBatisSqlMapTemplate.template";
		}else{
			this.TEMPLATE = template;
		}
		if(encoding == null){
			this.ENCODING = "UTF-8";
		}else{
			this.ENCODING = encoding;
		}
	}

    /**
     *
     * @param properties
     * @throws Exception
     */
	public void build(SourceBuilderProperties<String, Object> properties) throws Exception {
		this.sqlSource = (SqlSource) properties.get(PROP_SQL_SOURCE);

		//Prepare Variables
		String repositoryPackage = properties.getString(PROP_REPOSITORY_PACKAGE_NAME);
		String repositoryClass = properties.getString(PROP_REPOSITORY_CLASS_NAME);
		String domainPackage = properties.getString(PROP_DOMAIN_PACKAGE_NAME);
		String domainClass = properties.getString(PROP_DOMAIN_CLASS_NAME);
		String businessName = properties.getString(PROP_BUSINESS_NAME);
		String title = properties.getString(PROP_TITLE);
		String author = properties.getString(PROP_AUTHOR);
		String namespace = repositoryPackage + "." + repositoryClass;

		int buildOption = properties.getInt(PROP_BUILD_OPTION, 0);

		//Build
		source = buildSqlMap(ENCODING, namespace, businessName, domainPackage, domainClass, title, author, buildOption);
	}

	/**
	 *
	 * @return
	 */
	public String getSourceString() {
		return this.source.toString();
	}


    /**
     *
     * @param encoding
     * @param namespace
     * @param businessName
     * @param domainPackage
     * @param domainClass
     * @param title
     * @param author
     * @param buildOption
     * @return
     * @throws Exception
     */
	StringBuffer buildSqlMap(final String encoding, final String namespace, final String businessName, final String domainPackage, final String domainClass, final String title, final String author, int buildOption) throws Exception{
		XmlSourceTemplate template = new XmlSourceTemplate(TEMPLATE);
		template.loadTemplate(encoding);

		final String xmlsDefine = template.getXmlDefine("xml");
		final String docTypeDefine = template.getDocTypeDefine("docType");
		final String rootDefine = template.getRootDefine("root");
        final String resultMapDefine = template.getNodeDefine("resultMap");
        final String defaultSelectClauseDefine = template.getNodeDefine("defaultSelectClause");
        final String selectStatementCountDefine = template.getNodeDefine("selectCount");
        final String selectStatementDefine = template.getNodeDefine("select");
        final String selectListStatementDefine = template.getNodeDefine("selectList");
        final String insertStatementDefine = template.getNodeDefine("insert");
        final String updateStatementDefine = template.getNodeDefine("update");
        final String deleteStatementDefine = template.getNodeDefine("updateDelete");

		String createDate = template.creationDate();

		StringBuffer source = new StringBuffer();
		source.append(SourceBuilderUtil.replaceVariables(xmlsDefine, "encode", ENCODING));
		source.append(docTypeDefine);

		//Create ROOT
		String rootNode = rootDefine;
		rootNode = SourceBuilderUtil.replaceVariables(rootNode, "namespace", namespace);
		rootNode = SourceBuilderUtil.replaceVariables(rootNode, "title", title);
		rootNode = SourceBuilderUtil.replaceVariables(rootNode, "author", author);
		rootNode = SourceBuilderUtil.replaceVariables(rootNode, "created", createDate);

        //Create RESULT MAP
        String resultMap = null;

        //Create Default Select Clause
        String defaultSelectClause = null;

		//Create SELECT Statement
		String selectQuery = null;

		//Create SELECT List Statement
		String selectListQuery = null;

		//Create INSERT Statement
		String insertQuery = null;

		//Create UPDATE Statement
		String updateQuery = null;

		//Create DELETE Statement
		String deleteQuery = null;

		if(buildOption == 1 || buildOption == 0) {
            resultMap = createResultMapNode(resultMapDefine, businessName, Utility.toFieldName(domainClass));
			defaultSelectClause = createDefaultSelectClauseNode(defaultSelectClauseDefine, businessName, Utility.toFieldName(domainClass));
            //Create SELECT Statement
            selectQuery = createSelectStatementNode(selectStatementDefine, namespace, title, author, createDate, businessName, Utility.toFieldName(domainClass), MAP);
            //Create SELECT List Statement
            selectListQuery = createSelectListStatementNode(selectListStatementDefine, namespace, title, author, createDate, businessName, Utility.toFieldName(domainClass), MAP);
        }

		if(buildOption == 2 || buildOption == 0) {
            //Create INSERT Statement
            insertQuery = createInsertStatementNode(insertStatementDefine, namespace, title, author, createDate, businessName, Utility.toFieldName(domainClass));
            //Create UPDATE Statement
            updateQuery = createUpdateStatementNode(updateStatementDefine, namespace, title, author, createDate, businessName, Utility.toFieldName(domainClass));
            //Create DELETE Statement
            deleteQuery = createDeleteStatementNode(deleteStatementDefine, namespace, title, author, createDate, businessName, Utility.toFieldName(domainClass));
        }

		StringBuffer nodesString = new StringBuffer();
        nodesString.append(resultMap);
        nodesString.append(CRLF);
		nodesString.append(defaultSelectClause);
		nodesString.append(CRLF);
		nodesString.append(selectListQuery);
		nodesString.append(CRLF);
		nodesString.append(selectQuery);
		nodesString.append(CRLF);
		nodesString.append(insertQuery);
		nodesString.append(CRLF);
		nodesString.append(updateQuery);
		nodesString.append(CRLF);
		nodesString.append(deleteQuery);

		rootNode = SourceBuilderUtil.replaceVariables(rootNode, "typeAlias", "");
		rootNode = SourceBuilderUtil.replaceVariables(rootNode, "nodes", nodesString.toString());

		source.append(rootNode);
		return source;
	}

	/**
	 * create resultMapNode
	 * @param nodeDefine
	 * @param businessName
	 * @param parameterClass
	 * @return
	 */
    private String createResultMapNode(final String nodeDefine, final String businessName, final String parameterClass) {

        String node = nodeDefine;

        node = SourceBuilderUtil.replaceVariables(node, "businessName", businessName);
        node = SourceBuilderUtil.replaceVariables(node, "parameterClass", parameterClass);


        String query = sqlSource.getResultMapSql();
        node = SourceBuilderUtil.replaceVariables(node, "resultMap", query);
        return node;
    }

	/**
	 * create defaultSelectClause
	 * @param nodeDefine
	 * @param businessName
	 * @param parameterClass
	 * @return
	 */
    private String createDefaultSelectClauseNode(final String nodeDefine, final String businessName, final String parameterClass) {

        String node = nodeDefine;

        node = SourceBuilderUtil.replaceVariables(node, "businessName", businessName);
        node = SourceBuilderUtil.replaceVariables(node, "parameterClass", parameterClass);


        String query = sqlSource.getDefaultSelectClauseSql();
        node = SourceBuilderUtil.replaceVariables(node, "defaultSelectClause", query);
        return node;
    }


	/**
	 *
	 * @return
	 */
	private String[][] getParams() {
		String[] javaFields = sqlSource.getJavaFields();
		String[] fields = sqlSource.getFields();
		FieldExpression fieldExpression = sqlSource.getSelectQueryFieldExpression();
		if(fieldExpression == null)
			fieldExpression = new FieldExpression();

		String[][] params = new String[javaFields.length][2];
		for(int i = 0; i < params.length; i++){
			if(!fieldExpression.isSkipField(fields[i])){
				params[i][0] = javaFields[i];
				params[i][1] = fields[i];
			}else{
				params[i] = null;
			}
		}

		return params;
	}

    /**
     * create selectCntNode
     * @param nodeDefine
     * @param namespace
     * @param title
     * @param author
     * @param created
     * @param businessName
     * @param parameterClass
     * @param resultMapClass
     * @return
     */
	private String createSelectRecordCountStatementNode(final String nodeDefine, final String namespace, final String title, final String author, final String created, final String businessName, final String parameterClass, final String resultMapClass){

		String node = nodeDefine;

		node = SourceBuilderUtil.replaceVariables(node, "title", title);
		node = SourceBuilderUtil.replaceVariables(node, "author", author);
		node = SourceBuilderUtil.replaceVariables(node, "created", created);

		node = SourceBuilderUtil.replaceVariables(node, "parameterClass", parameterClass);
		node = SourceBuilderUtil.replaceVariables(node, "resultMapClass", resultMapClass);
		node = SourceBuilderUtil.replaceVariables(node, "attributes", "");
		node = SourceBuilderUtil.replaceVariables(node, "businessName", businessName);
		node = SourceBuilderUtil.replaceVariables(node, "namespace", namespace);

		String[][] params = getParams();
		node = SourceBuilderUtil.replaceIterates(node, "results", params);
		return node;
	}

    /**
     * create
     * @param nodeDefine
     * @param namespace
     * @param title
     * @param author
     * @param created
     * @param businessName
     * @param parameterClass
     * @param resultMapClass
     * @return
     */
	private String createSelectStatementNode(final String nodeDefine, final String namespace, final String title, final String author, final String created, final String businessName, final String parameterClass, final String resultMapClass){

		String node = nodeDefine;

		node = SourceBuilderUtil.replaceVariables(node, "title", title);
		node = SourceBuilderUtil.replaceVariables(node, "author", author);
		node = SourceBuilderUtil.replaceVariables(node, "created", created);

		node = SourceBuilderUtil.replaceVariables(node, "parameterClass", parameterClass);
		node = SourceBuilderUtil.replaceVariables(node, "resultMapClass", resultMapClass);
		node = SourceBuilderUtil.replaceVariables(node, "attributes", "");
		node = SourceBuilderUtil.replaceVariables(node, "businessName", businessName);
		node = SourceBuilderUtil.replaceVariables(node, "namespace", namespace);

		String[][] params = getParams();
		node = SourceBuilderUtil.replaceIterates(node, "results", params);

		String query = sqlSource.getSelectSql();
		node = SourceBuilderUtil.replaceVariables(node, "query", query);
		return node;
	}


    /**
     *
     * @param nodeDefine
     * @param namespace
     * @param title
     * @param author
     * @param created
     * @param businessName
     * @param parameterClass
     * @param resultMapClass
     * @return
     */
	private String createSelectListStatementNode(final String nodeDefine, final String namespace, final String title, final String author, final String created, final String businessName, final String parameterClass, final String resultMapClass){

		String node = nodeDefine;

		node = SourceBuilderUtil.replaceVariables(node, "title", title);
		node = SourceBuilderUtil.replaceVariables(node, "author", author);
		node = SourceBuilderUtil.replaceVariables(node, "created", created);

		node = SourceBuilderUtil.replaceVariables(node, "parameterClass", parameterClass);
		node = SourceBuilderUtil.replaceVariables(node, "resultMapClass", resultMapClass);
		node = SourceBuilderUtil.replaceVariables(node, "attributes", "");
		node = SourceBuilderUtil.replaceVariables(node, "businessName", businessName);
		node = SourceBuilderUtil.replaceVariables(node, "namespace", namespace);

		String[][] params = getParams();
		node = SourceBuilderUtil.replaceIterates(node, "results", params);

		String query = sqlSource.getSelectListSql();
		node = SourceBuilderUtil.replaceVariables(node, "query", query);
		return node;
	}

	/**
	 * @param nodeDefine
	 * @param namespace
	 * @param title
	 * @param author
	 * @param created
	 * @param businessName
	 * @param parameterClass
	 * @return String
	 */
	private String createInsertStatementNode(final String nodeDefine, final String namespace, final String title, final String author, final String created, final String businessName, final String parameterClass) {
		String node = nodeDefine;

		node = SourceBuilderUtil.replaceVariables(node, "title", title);
		node = SourceBuilderUtil.replaceVariables(node, "author", author);
		node = SourceBuilderUtil.replaceVariables(node, "created", created);

		node = SourceBuilderUtil.replaceVariables(node, "parameterClass", parameterClass);
		node = SourceBuilderUtil.replaceVariables(node, "attributes", "");
		node = SourceBuilderUtil.replaceVariables(node, "businessName", businessName);
		node = SourceBuilderUtil.replaceVariables(node, "namespace", namespace);

		String query = sqlSource.getInsertSql();
		node = SourceBuilderUtil.replaceVariables(node, "query", query);
		return node;
	}


	/**
	 * @param nodeDefine
	 * @param namespace
	 * @param title
	 * @param author
	 * @param created
	 * @param businessName
	 * @param parameterClass
	 * @return String
	 */
	private String createUpdateStatementNode(final String nodeDefine, final String namespace, final String title, final String author, final String created, final String businessName, final String parameterClass) {
		String node = nodeDefine;

		node = SourceBuilderUtil.replaceVariables(node, "title", title);
		node = SourceBuilderUtil.replaceVariables(node, "author", author);
		node = SourceBuilderUtil.replaceVariables(node, "created", created);

		node = SourceBuilderUtil.replaceVariables(node, "parameterClass", parameterClass);
		node = SourceBuilderUtil.replaceVariables(node, "attributes", "");
		node = SourceBuilderUtil.replaceVariables(node, "businessName", businessName);
		node = SourceBuilderUtil.replaceVariables(node, "namespace", namespace);

		String query = sqlSource.getUpdateSql();
		node = SourceBuilderUtil.replaceVariables(node, "query", query);
		return node;
	}


	/**
	 * @param nodeDefine
	 * @param namespace
	 * @param title
	 * @param author
	 * @param created
	 * @param businessName
	 * @param parameterClass
	 * @return String
	 */
	private String createDeleteStatementNode(final String nodeDefine, final String namespace, final String title, final String author, final String created, final String businessName, final String parameterClass) {
		String node = nodeDefine;

		node = SourceBuilderUtil.replaceVariables(node, "title", title);
		node = SourceBuilderUtil.replaceVariables(node, "author", author);
		node = SourceBuilderUtil.replaceVariables(node, "created", created);

		node = SourceBuilderUtil.replaceVariables(node, "parameterClass", parameterClass);
		node = SourceBuilderUtil.replaceVariables(node, "attributes", "");
		node = SourceBuilderUtil.replaceVariables(node, "businessName", businessName);
		node = SourceBuilderUtil.replaceVariables(node, "namespace", namespace);

		String query = sqlSource.getDeleteSql();
		node = SourceBuilderUtil.replaceVariables(node, "query", query);
		return node;
	}

}
