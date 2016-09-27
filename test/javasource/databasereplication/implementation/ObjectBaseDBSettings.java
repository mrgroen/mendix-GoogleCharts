package databasereplication.implementation;

import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.axis2.util.Base64;

import com.mendix.core.Core;
import com.mendix.core.CoreException;
import com.mendix.core.CoreRuntimeException;
import com.mendix.systemwideinterfaces.MendixRuntimeException;
import com.mendix.systemwideinterfaces.core.IContext;
import com.mendix.systemwideinterfaces.core.IMendixIdentifier;
import com.mendix.systemwideinterfaces.core.IMendixObject;

import databasereplication.interfaces.IDatabaseSettings;
import databasereplication.proxies.CustomConnectionInfo;
import databasereplication.proxies.DBType;
import databasereplication.proxies.Database;
import databasereplication.proxies.TableFilter;

public class ObjectBaseDBSettings extends IDatabaseSettings {

	private IMendixObject dbObject;
	private IContext context;
	private DatabaseConnectorType dbType = null;
	private FilterList tablesFilter;

	public ObjectBaseDBSettings( IContext context, IMendixObject databaseObject ) {
		this.dbObject = databaseObject;
		this.context = context;
	}

	@Override
	public String getAddress() {
		return this.dbObject.getValue(this.getContext(), Database.MemberNames.DatabaseURL.toString());
	}

	@Override
	public String getDatabaseName() {
		return this.dbObject.getValue(this.getContext(), Database.MemberNames.DatabaseName.toString());
	}


	@Override
	public DatabaseConnectorType getDatabaseConnectionType() throws CoreRuntimeException {
		if( this.dbType == null ) {
			switch (DBType.valueOf((String)this.dbObject.getValue(this.getContext(), Database.MemberNames.DatabaseType.toString())) ) {
			case Oracle:
				this.dbType = DatabaseConnectorType.getOracleInfo(this);
				break;
			case Postgres:
				this.dbType = DatabaseConnectorType.getPostgreSQLInfo(this);
				break;
			case SQLServer2005:
			case SQLServer2008:
				this.dbType = DatabaseConnectorType.getSQLServerInfo(this);
				break;
			case Informix:
				this.dbType = DatabaseConnectorType.getInformixInfo(this);
				break;
			case AS_400:
				this.dbType = DatabaseConnectorType.getAS400Info(this);
				break;
			case DMS2:
				this.dbType = DatabaseConnectorType.getDMS2Info(this);
				break;
			case Custom:
				IMendixIdentifier dbConnectionId = this.dbObject.getValue(this.context, Database.MemberNames.Database_CustomConnectionInfo.toString());
				if( dbConnectionId != null ) {
					try {
						IMendixObject dbConnectionInfo = Core.retrieveId(this.context, dbConnectionId);
						this.dbType = new DatabaseConnectorType(DBType.Custom, (String)dbConnectionInfo.getValue(this.context, CustomConnectionInfo.MemberNames.DriverClass.toString()),
								(String)dbConnectionInfo.getValue(this.context, CustomConnectionInfo.MemberNames.EscapeCharacterOpen.toString()),
								(String)dbConnectionInfo.getValue(this.context, CustomConnectionInfo.MemberNames.EscapeCharacterClose.toString()) );

						this.dbType.setAllowASToken( (Boolean)dbConnectionInfo.getValue(this.context, CustomConnectionInfo.MemberNames.AllowsASToken.toString()) );
						this.dbType.setConnectionString( (String)dbConnectionInfo.getValue(this.context, CustomConnectionInfo.MemberNames.ConnectionString.toString()) );
						this.dbType.setEscapeColumnAlias( (Boolean)dbConnectionInfo.getValue(this.context, CustomConnectionInfo.MemberNames.EscapeColumnAlias.toString()) );
						this.dbType.setEscapeColumnNames( (Boolean)dbConnectionInfo.getValue(this.context, CustomConnectionInfo.MemberNames.EscapeColumnNames.toString()) );
						this.dbType.setEscapeTableAlias( (Boolean)dbConnectionInfo.getValue(this.context, CustomConnectionInfo.MemberNames.EscapeTableAlias.toString()) );
						this.dbType.setEscapeTableNames( (Boolean)dbConnectionInfo.getValue(this.context, CustomConnectionInfo.MemberNames.EscapeTableNames.toString()) );
						this.dbType.setSeparatEscapeSchemaTable( (Boolean)dbConnectionInfo.getValue(this.context, CustomConnectionInfo.MemberNames.SeparateEscapeSchemaTableName.toString()) );
					}
					catch (Exception e) {
						throw new CoreRuntimeException(e);
					}
				}
				else
					throw new CoreRuntimeException("When type custom is selected there should be Custom connection information");
				break;
			case MSAccess:
				this.dbType = DatabaseConnectorType.getMSAccessInfo(this);
				break;
			}
		}

		return this.dbType;
	}

	@Override
	public String getPassword() {
		String dbPassword = this.dbObject.getValue(this.getContext(), Database.MemberNames.DatabasePassword_Encrypted.toString());
		if ( dbPassword != null && !"".equals(dbPassword) )
			return decryptString(dbPassword);

		return this.dbObject.getValue(this.getContext(), Database.MemberNames.DatabasePassword.toString());
	}

	@Override
	public String getPort() {
		Integer value = this.dbObject.getValue(this.getContext(), Database.MemberNames.Port.toString());
		if ( value != null )
			return String.valueOf(value);

		return null;
	}

	@Override
	public String getServiceName() {
		return this.dbObject.getValue(this.getContext(), Database.MemberNames.ServiceName.toString());
	}

	@Override
	public String getUserName() {
		return this.dbObject.getValue(this.getContext(), Database.MemberNames.DatabaseUser.toString());

	}

	@Override
	public IContext getContext() {
		return this.context;
	}

	@Override
	public boolean useIntegratedAuthentication() {
		return (Boolean) this.dbObject.getValue(this.getContext(), Database.MemberNames.useIntegratedSecurity.toString());
	}

	public static String decryptString( String valueToDecrypt ) {
		if ( valueToDecrypt == null )
			return null;
		try {
			String key = "n0cwq7cmwq978c0m";

			Cipher c = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			SecretKeySpec k = new SecretKeySpec(key.getBytes(), "AES");
			String[] s = valueToDecrypt.split(";");
			if ( s.length < 2 ) // Not an encrypted string, just return the original value.
				return valueToDecrypt;
			byte[] iv = Base64.decode(s[0]);
			byte[] encryptedData = Base64.decode(s[1]);
			c.init(Cipher.DECRYPT_MODE, k, new IvParameterSpec(iv));
			return new String(c.doFinal(encryptedData));
		}
		catch( Exception e ) {
			throw new MendixRuntimeException("Unable to decrypt the password", e);
		}
	}

	public static String encryptString( String valueToEncrypt ) throws Exception {
		if ( valueToEncrypt == null )
			return null;
		String key = "n0cwq7cmwq978c0m";

		Cipher c = Cipher.getInstance("AES/CBC/PKCS5PADDING");
		SecretKeySpec k = new SecretKeySpec(key.getBytes(), "AES");
		c.init(Cipher.ENCRYPT_MODE, k);
		byte[] encryptedData = c.doFinal(valueToEncrypt.getBytes());
		byte[] iv = c.getIV();
		return new StringBuilder(Base64.encode(iv)).append(";").append(Base64.encode(encryptedData)).toString();
	}

	@Override
	public FilterList getTableFilters() throws CoreException {
		this.tablesFilter = null;

		if ( this.tablesFilter == null ) {
			this.tablesFilter = new FilterList();
			List<IMendixObject> result = Core.retrieveXPathQueryEscaped(getContext(), "//%s[%s=%s]", TableFilter.entityName,
					TableFilter.MemberNames.TableFilter_Database.toString(), String.valueOf(this.dbObject.getId().toLong()));
			if ( result.size() > 0 ) {
				for( IMendixObject resultObj : result ) {
					this.tablesFilter.add((String) resultObj.getValue(getContext(), TableFilter.MemberNames.Filter.toString()));
				}
			}
		}

		return this.tablesFilter;
	}
}
