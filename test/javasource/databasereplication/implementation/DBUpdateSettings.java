package databasereplication.implementation;

import java.util.HashMap;
import java.util.Map.Entry;

import replication.ReplicationSettings;
import replication.implementation.InfoHandler;
import replication.interfaces.IErrorHandler;
import replication.interfaces.IValueParser;

import com.mendix.core.Core;
import com.mendix.core.CoreException;
import com.mendix.systemwideinterfaces.core.IContext;
import com.mendix.systemwideinterfaces.core.IMendixObject;
import com.mendix.systemwideinterfaces.core.meta.IMetaPrimitive;
import com.mendix.systemwideinterfaces.core.meta.IMetaPrimitive.PrimitiveType;

import databasereplication.interfaces.IDatabaseSettings;
import databasereplication.proxies.UpdateType;


/**
 * This class contains all specific settings that can be used in the replication DataManager
 * When this object is created, database settings must be predefined, the object type must be set and the current action context must be set
 * The complete column mapping must be set and
 * 
 * 
 * @version 1.0
 */
public class DBUpdateSettings extends ReplicationSettings {

	private UpdateType updateType;

	/**
	 * Initialize the settings, provide the starting context. If a new context has to be created the settings will be copied from the provided context.
	 * The last parameter ErrorHandler is optional. This parameter should contain a specfic project errorhandler or when null the default errorhandler will be used. This handler aborts the import for eacht exception
	 * 
	 * @param context
	 * @param dbSettings
	 * @param objectType
	 * @param updateType 
	 * @param errorHandler
	 * @throws MendixReplicationException
	 */
	public DBUpdateSettings(IContext context, IDatabaseSettings dbSettings, String objectType, UpdateType updateType, IErrorHandler errorHandler ) throws MendixReplicationException {
		super(context, objectType, errorHandler);
		this.setInfoHandler( new InfoHandler("DBReplication") );
		this.constraint = "";
		this.columnMap = new HashMap<String,ColumnInfo>();
		this.dbSettings = dbSettings;
		this.updateType = updateType;
	}

	private IDatabaseSettings dbSettings;

	private String tableName;
	private String constraint;

	//This attribute contains all selectClause for the current settings
	private HashMap<String,ColumnInfo> columnMap;
	protected class ColumnInfo {
		public String Name;
		public String QueryName;
		public String dateType;
		public PrimitiveType type;
		public ColumnInfo(String name, String queryName, String dataType) {
			this.Name = name;
			this.dateType = dataType;
			this.QueryName = queryName;
		}
		public PrimitiveType getType() {
			if( this.dateType == null )
				return null;
			
			this.dateType = this.dateType.toLowerCase();
			if( this.dateType.contains("char") || this.dateType.contains("varchar") || this.dateType.contains("nchar") ) 
				this.type = PrimitiveType.String;
			else if( this.dateType.contains("int") ) 
				this.type = PrimitiveType.Integer;
			else if( this.dateType.contains("long") ) 
				this.type = PrimitiveType.Long;
			else if( this.dateType.contains("float") || this.dateType.contains("decimal") ) 
				this.type = PrimitiveType.Float;
			else if( this.dateType.contains("date") || this.dateType.contains("time") ) 
				this.type = PrimitiveType.DateTime;
			
			if( this.type == null )
				Core.getLogger("DBUpdateSettings").info("REQUIRES IMPLEMENTATION!!! : " + this.dateType);
			
			return this.type;
		}
	}
	
	private DBValueParser vParser;

	public IDatabaseSettings getDbSettings( ) {
		return this.dbSettings;
	}

//	public void addColumnMapping( String tableName, String columnName, String dataType, String memberName ) throws CoreException {
//		this.addColumnMapping(tableName, columnName, memberName, KeyType.NoKey, false, null);
//	}

	/**
	 * 
	 * @param tableName, The name or alias from the table in the external DB
	 * @param columnName, Name of the column in the external DB
	 * @param memberName, The name of the member where the value should be stored in
	 * @param isKey, Is this member a key column, i.e. should the DataManager search for any other objects with this value
	 * @param parser, The parser that is going to be used to change the value of this member before storing it
	 * @throws CoreException
	 * @throws CoreException
	 */
	public void addColumnMapping( String tableName, String columnName, String dataType, String memberName, KeyType isKey, Boolean isCaseSensitive, IValueParser parser ) throws CoreException {
		this.columnMap.put(memberName, new ColumnInfo( columnName, DatabaseConnector.procesSetStatement(this.dbSettings.getDatabaseConnectionType(), tableName, columnName).toString(), dataType) );

		this.addMappingForAttribute(memberName, memberName, isKey, isCaseSensitive, parser);
	}

	public void addAssociationMapping(String tableName, String columnName, String dataType, String associationName, String associatedObjectType, String memberName, IValueParser parser, KeyType isKey, Boolean isCaseSensitive) throws CoreException {
		this.columnMap.put(memberName, new ColumnInfo( columnName, DatabaseConnector.procesSetStatement(this.dbSettings.getDatabaseConnectionType(), tableName, columnName).toString(), dataType));
		
		this.addMappingForAssociation(tableName+"."+columnName, associationName, associatedObjectType, memberName, parser, isKey, isCaseSensitive);
	}


	/**
	 * Set the table from where this query should fetch it's result
	 * @param tableName
	 * @param tableAlias
	 * @return tableAlias
	 * @throws CoreException
	 */
	public String setUpdateTable( String tableName) throws MendixReplicationException {
		if( this.tableName != null )
			throw new MendixReplicationException( "The from table may only be defined once. If you wan't to use a second table you should join it." );

		this.tableName = tableName;

		return this.tableName;
	}

	/**
	 * Add any additional constraints that have to be used in this query.
	 * There are no limitations to what is allowed as constraint. As long as the constraint is valid SQL
	 * 
	 * @param constraint
	 */
	public void setConstraint( String constraint ) {
		this.constraint = constraint;
	}

	
	public String getQuery(IMendixObject mappedObject) throws MendixReplicationException {
		return this.getUpdateQuery(this.updateType, mappedObject);
	}
	
	/**
	 * @return the query that can be executed based on all mapped columns and associations
	 * @throws CoreException
	 */
	public String getUpdateQuery(UpdateType updateType, IMendixObject mappedObject) throws MendixReplicationException {
		StringBuilder query = new StringBuilder(200);
		
		if( this.vParser == null )
			this.vParser = new DBValueParser(this.getValueParsers(), this);
		DatabaseConnectorType dbType = this.dbSettings.getDatabaseConnectionType();
		
		if( updateType == UpdateType.UpdateOnly )
			query.append("UPDATE ").append(DatabaseConnector.procesUpdateTable(dbType, this.tableName)).append("SET ");
		else if( updateType == UpdateType.AlwaysInsert ) {
			query.append("INSERT INTO ").append(DatabaseConnector.procesUpdateTable(dbType, this.tableName));
			boolean isFirst = true;
			for (Entry<String, IMetaPrimitive> memberMapping : this.memberInfo.entrySet())
			{
				if(!isFirst)
					query.append(", ");
				else {
					query.append(" ( ");
					isFirst = false;
				}
				ColumnInfo columnInfo = this.columnMap.get(memberMapping.getValue().getName());
				
				query.append(DatabaseConnector.processTableAlias(dbType, columnInfo.QueryName));
			}
			query.append( " ) VALUES ");
		}
		else 
			throw new MendixReplicationException("Unsupported update type: " + updateType + " - Not Implemented!");
		

		//Validate if the query can be created or if there is still any information missing
		if( this.columnMap.size() == 0 )
			throw new MendixReplicationException("No set clause found for this query.");
		if( this.tableName == null )
			throw new MendixReplicationException("The 'update table' is not defined.");

		//Append all Select clauses to the query

		boolean isFirst = true;
		boolean isFirstConstraint = true;
		for (Entry<String, IMetaPrimitive> memberMapping : this.memberInfo.entrySet())
		{
			String memberName = memberMapping.getValue().getName(),
					memberAlias = memberMapping.getKey();
			Boolean isKey = this.getMainObjectConfig().getKeys().get(memberName);
			
			String value = (String) this.vParser.getValue(PrimitiveType.String, memberAlias, this.vParser.getValueFromObject(mappedObject, memberMapping.getKey()));
			ColumnInfo columnInfo = this.columnMap.get(memberName);

			PrimitiveType type = columnInfo.getType();
			if( type == null )
				type = memberMapping.getValue().getType();
				
			if (isKey != null)
			{
				switch(type)
				{
				case AutoNumber:
				case Currency:
				case Integer:
				case Float:
				case Long:
					this.constraint += (!isFirstConstraint ? " AND " : " WHERE ") + DatabaseConnector.processTableAlias(dbType, columnInfo.QueryName) + "=" + value;
					isFirstConstraint = false;
					break;
				default:
					this.constraint += (!isFirstConstraint ? " AND " : " WHERE ") + DatabaseConnector.processTableAlias(dbType, columnInfo.QueryName) + "='" + value + "'";
					isFirstConstraint = false;
					break;
				}
			}
			else
			{
				if(!isFirst)
					query.append(", ");
				else {
					isFirst = false;

					if( updateType == UpdateType.AlwaysInsert )
						query.append(" ( ");
				}
				if( updateType == UpdateType.UpdateOnly )
					query.append( 
							DatabaseConnector.processTableAlias(dbType, columnInfo.QueryName)).append("=");

				switch(type)
				{
					case AutoNumber:
					case Currency:
					case Integer:
					case Float:
					case Long:
						query.append(value);
						break;
					default:
						query.append("'").append(value).append("'");
						break;
				}
			}

		}

		if( updateType == UpdateType.AlwaysInsert )
			query.append(" ) ");
		
		//When a constraint is set, append the constraint to the query
		if( this.constraint != null && this.constraint.length() > 0 )
			query.append( this.constraint ).append( " " );

		return query.append("").toString();
	}

	public String getUpdateTableName() {
		return this.tableName;
	}

//	public String getInsertQuery(IMendixObject updatedObject) throws MendixReplicationException {
//		StringBuilder query = new StringBuilder();
//		StringBuilder columns = new StringBuilder();
//		StringBuilder values = new StringBuilder();
//		
//		if( this.vParser == null )
//			this.vParser = new DBValueParser(this.getValueParsers(), this);
//
//		query.append("INSERT ");
//		query.append(DatabaseConnector.procesUpdateTable(this.dbSettings.getDatabaseConnectionType(), this.tableName));
//
//		//Validate if the query can be created or if there is still any information missing
//		if( this.columnMap.size() == 0 )
//			throw new MendixReplicationException("No set clause found for this query.");
//		if( this.tableName == null )
//			throw new MendixReplicationException("The 'update table' is not defined.");
//		
//		boolean isFirst = true;
//		for (Entry<String, String> memberMapping : this.memberNames.entrySet())
//		{
//			String memberName = memberMapping.getValue(),
//					memberAlias = memberMapping.getKey();
//			
//			String value = (String) this.vParser.getValue(PrimitiveType.String, memberAlias, this.vParser.getValueFromObject(updatedObject, memberMapping.getKey()));
//			ColumnInfo columnInfo = this.columnMap.get(memberName);
//
//			
//			PrimitiveType type = columnInfo.getType();
//			if( type == null )
//				type = this.memberTypeMap.get(memberMapping.getKey());
//			
//			if(!isFirst) {
//				columns.append(", ");
//				values.append(", ");
//			}
//			else
//				isFirst = false;
//			
//			columns.append(columnInfo.QueryName);
//			
//
//			switch(type)
//			{
//			case AutoNumber:
//			case Currency:
//			case Integer:
//			case Float:
//			case Long:
//				values.append(value);
//				break;
//			default:
//				values.append("'").append(value).append("'");
//				break;
//			}
//
//		}
//		
//		query.append("(").append(columns).append(") VALUES (").append(values).append(");");
//		
//		return query.toString();
//	}
}
