package databasereplication.implementation;

import com.mendix.core.CoreRuntimeException;

import databasereplication.interfaces.IDatabaseSettings;
import databasereplication.proxies.DBType;


public class DatabaseConnectorType {

	public static DatabaseConnectorType getSQLServerInfo( IDatabaseSettings settings ) {
		DatabaseConnectorType dbtype = new DatabaseConnectorType(DBType.SQLServer2008, "com.microsoft.sqlserver.jdbc.SQLServerDriver","[","]");
		dbtype.setAllowASToken(true);
		dbtype.setEscapeColumnAlias(true);
		dbtype.setEscapeColumnNames(true);
		dbtype.setEscapeTableAlias(true);
		dbtype.setEscapeTableNames(true);
		dbtype.setSeparatEscapeSchemaTable(false);

		String connectionString = "jdbc:sqlserver://" + settings.getAddress() +
		(settings.getPort()!=null && settings.getPort().length() > 0 ? ":" + settings.getPort() : "" ) +
		(settings.getServiceName()!=null && settings.getServiceName().length() > 0 ? "\\" + settings.getServiceName() : "" ) + ";" +
		"databaseName=" + settings.getDatabaseName() ;

		if( settings.useIntegratedAuthentication() ) {
			connectionString += ";integratedSecurity=true";
		}
		dbtype.setConnectionString( connectionString );

		return dbtype;
	}
	public static DatabaseConnectorType getOracleInfo( IDatabaseSettings settings ) {
		DatabaseConnectorType dbtype = new DatabaseConnectorType(DBType.Oracle, "oracle.jdbc.OracleDriver","\"","\"");
		dbtype.setAllowASToken(false);
		dbtype.setEscapeColumnAlias(true);
		dbtype.setEscapeColumnNames(true);
		dbtype.setEscapeTableAlias(true);
		dbtype.setEscapeTableNames(true);
		dbtype.setSeparatEscapeSchemaTable(true);
		
		String connectionString = "jdbc:oracle:thin:" + settings.getUserName() + "/" +
				settings.getPassword() + "@" + settings.getAddress();
		if( settings.getPort()!=null && settings.getPort().length() > 0 )
			connectionString += ":" + settings.getPort();
		
		if( settings.getServiceName() != null && !"".equals(settings.getServiceName()) ) {
			if( settings.getDatabaseName() != null && !"".equals(settings.getDatabaseName()) )
				throw new CoreRuntimeException( "Invalid configuration, you cannot use both the SID(" + settings.getServiceName() + ") and a Service Name (" + settings.getServiceName() + ")" );
			connectionString += "/" + settings.getServiceName();
		}
		else if( settings.getDatabaseName() != null && !"".equals(settings.getDatabaseName()) )
			connectionString += ":" + settings.getDatabaseName();
		
		dbtype.setConnectionString( connectionString );

		return dbtype;
	}
	public static DatabaseConnectorType getPostgreSQLInfo( IDatabaseSettings settings ) {
		DatabaseConnectorType dbtype = new DatabaseConnectorType(DBType.Postgres, "org.postgresql.Driver","\"","\"");
		dbtype.setAllowASToken(true);
		dbtype.setEscapeColumnAlias(true);
		dbtype.setEscapeColumnNames(true);
		dbtype.setEscapeTableAlias(true);
		dbtype.setEscapeTableNames(true);
		dbtype.setSeparatEscapeSchemaTable(false);
		dbtype.setConnectionString( "jdbc:postgresql://" + settings.getAddress() +
				(settings.getPort()!=null && settings.getPort().length() > 0 ? ":" + settings.getPort() : "" )
				+ "/" + settings.getDatabaseName() );

		return dbtype;
	}
	public static DatabaseConnectorType getInformixInfo( IDatabaseSettings settings ) {
		DatabaseConnectorType dbtype = new DatabaseConnectorType(DBType.Informix, "com.informix.jdbc.IfxDriver","","");
		dbtype.setAllowASToken(true);
		dbtype.setEscapeColumnAlias(true);
		dbtype.setEscapeColumnNames(true);
		dbtype.setEscapeTableAlias(true);
		dbtype.setEscapeTableNames(true);
		dbtype.setSeparatEscapeSchemaTable(false);
		dbtype.setConnectionString( "jdbc:informix-sqli://" + settings.getAddress() + ":" +
				(settings.getPort()!=null && settings.getPort().length() > 0 ? settings.getPort() : "" ) +
				(settings.getDatabaseName()!=null && settings.getDatabaseName().length() > 0 ? "/" + settings.getDatabaseName() + ":" : "" ) +
				"informixserver=" + settings.getServiceName() );
		//+ ";" + "user=" + settings.getUserName() + ";password=" + settings.getPassword()

		return dbtype;
	}
	public static DatabaseConnectorType getDMS2Info( IDatabaseSettings settings ) {
		DatabaseConnectorType dbtype = new DatabaseConnectorType(DBType.DMS2, "com.unisys.jdbc.dmsql.Driver","\"","\"");
		dbtype.setAllowASToken(false);
		dbtype.setEscapeColumnAlias(true);
		dbtype.setEscapeColumnNames(true);
		dbtype.setEscapeTableAlias(true);
		dbtype.setEscapeTableNames(false);
		dbtype.setSeparatEscapeSchemaTable(false);
		dbtype.setConnectionString( "jdbc:unisys:dmsql:Unisys.DMSII:resource=" + settings.getDatabaseName() +
				((settings.getAddress() != null && !"".equals(settings.getAddress())) ? ";host=" + settings.getAddress() : "") +
				((settings.getPort() != null && !"".equals(settings.getPort())) ? ";port=" + settings.getPort() : "") +
				";user=" + settings.getUserName()+
				";password=" + settings.getPassword() );

		return dbtype;
	}
	public static DatabaseConnectorType getAS400Info( IDatabaseSettings settings ) {
		DatabaseConnectorType dbtype = new DatabaseConnectorType(DBType.Postgres, "com.ibm.as400.access.AS400JDBCDriver","\"","\"");
		dbtype.setAllowASToken(false);
		dbtype.setEscapeColumnAlias(true);
		dbtype.setEscapeColumnNames(true);
		dbtype.setEscapeTableAlias(true);
		dbtype.setEscapeTableNames(true);
		dbtype.setSeparatEscapeSchemaTable(true);
		dbtype.setConnectionString( "jdbc:as400://" + settings.getAddress() +
				(settings.getDatabaseName()!=null && settings.getDatabaseName().length() > 0 ? "/" + settings.getDatabaseName() : "" ));

		return dbtype;
	}
	public static DatabaseConnectorType getMSAccessInfo( IDatabaseSettings settings ) {
		DatabaseConnectorType dbtype = new DatabaseConnectorType(DBType.MSAccess, "com.hxtt.sql.access.AccessDriver","\"","\"");
		dbtype.setAllowASToken(true);
		dbtype.setEscapeColumnAlias(true);
		dbtype.setEscapeColumnNames(true);
		dbtype.setEscapeTableAlias(true);
		dbtype.setEscapeTableNames(true);
		dbtype.setSeparatEscapeSchemaTable(false);
		dbtype.setConnectionString( "jdbc:access:/" + settings.getDatabaseName() );

//		database+= filename.trim() + ";DriverID=22;READONLY=true}"; // add on to the end 

		return dbtype;
	}

	private int maxRetrieveSize = 1000;
	private String driverClass = "";
	private String escapeOpen = "";
	private String escapeClose = "";
	private boolean escapeColumnNames = false;
	private boolean escapeTableNames = false;
	private boolean escapeColumnAlias = true;
	private boolean escapeTableAlias = true;
	private boolean allowASToken = true;
	private boolean separatEscapeSchemaTable = false;
	private String connectionString = "";
	private DBType dbType;

	public DatabaseConnectorType( DBType dbType, String driverClass, String escapeCharOpen, String escapeCharClose ) {
		this.driverClass = driverClass;
		this.dbType = dbType;
		if( escapeCharClose != null ) 
			this.escapeClose = escapeCharClose;
		if( escapeCharOpen != null ) 
			this.escapeOpen = escapeCharOpen;
	}

	public String getConnectionString() {
		return this.connectionString;
	}
	public void setConnectionString(String connectionString) {
		this.connectionString = connectionString;
	}

	public String getDriverClass() {
		return this.driverClass;
	}
	public void setMaxRetrieveSize(int maxRetrieveSize) {
		this.maxRetrieveSize = maxRetrieveSize;
	}
	public int getMaxRetrieveSize() {
		return this.maxRetrieveSize;
	}

	public String getEscapeClose() {
		return this.escapeClose;
	}

	public String getEscapeOpen() {
		return this.escapeOpen;
	}

	public void setEscapeColumnNames(boolean escapeColumnNames) {
		this.escapeColumnNames = escapeColumnNames;
	}

	public boolean shouldEscapeColumnNames() {
		return this.escapeColumnNames;
	}

	public void setEscapeTableNames(boolean escapeTableNames) {
		this.escapeTableNames = escapeTableNames;
	}

	public boolean shouldEscapeTableNames() {
		return this.escapeTableNames;
	}

	public void setEscapeColumnAlias(boolean escapeColumnAlias) {
		this.escapeColumnAlias = escapeColumnAlias;
	}

	public boolean shouldEscapeColumnAlias() {
		return this.escapeColumnAlias;
	}

	public void setEscapeTableAlias(boolean escapeTableAlias) {
		this.escapeTableAlias = escapeTableAlias;
	}

	public boolean shouldEscapeTableAlias() {
		return this.escapeTableAlias;
	}

	public void setAllowASToken(boolean escapeAllowASToken) {
		this.allowASToken = escapeAllowASToken;
	}

	public boolean allowASToken() {
		return this.allowASToken;
	}

	public void setSeparatEscapeSchemaTable(boolean separatEscapeSchemaTable) {
		this.separatEscapeSchemaTable = separatEscapeSchemaTable;
	}

	public boolean separatEscapeSchemaTable() {
		return this.separatEscapeSchemaTable;
	}

	public DBType getDbType() {
		return this.dbType;
	}
}