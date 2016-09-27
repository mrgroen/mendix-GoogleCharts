package databasereplication.implementation;

import java.util.HashMap;
import java.util.Map;

import replication.MetaInfo;
import replication.ReplicationSettings.MendixReplicationException;
import replication.helpers.ObjectStatistics.Stat;
import replication.implementation.InfoHandler;

import com.mendix.core.Core;
import com.mendix.core.CoreException;
import com.mendix.systemwideinterfaces.core.IMendixObject;
import com.mendix.systemwideinterfaces.core.UserAction;

import databasereplication.proxies.ReplicationStatus;
import databasereplication.proxies.ReplicationStatusValues;

public abstract class IDataManager {

	private UserAction<?> currentAction;
	protected String replicationName;
	protected DBReplicationSettings settings;
	protected MetaInfo info;
	protected DBValueParser valueParser;
	protected RunningState state;

	protected enum RunningState {
			Running,
			AbortRollback,
			AbortCommit,
			Completed
		}

	protected static Map<String, IDataManager> _instances = new HashMap<String, IDataManager>();

	public static IDataManager getIntance(String mappingName) {
		return _instances.get(mappingName);
	}

	public static IDataManager instantiate( String replicationName, DBReplicationSettings settings ) {
		if(settings.getDbSettings().getDatabaseConnectionType().getConnectionString().contains("jdbc:access")) {
			MSAccessDataManager msAccessManager = new MSAccessDataManager( replicationName, settings );
			
			_instances.put(replicationName, msAccessManager);
			return msAccessManager;
		}
		else {
			DatabaseDataManager dbDataManager = new DatabaseDataManager( replicationName, settings );
		
			_instances.put(replicationName, dbDataManager);
			return dbDataManager;
		}
	}
	
	protected IDataManager( String replicationName, DBReplicationSettings settings ) {
		this.replicationName = replicationName;
		this.settings = settings;
		this.state = RunningState.Running;
	}

	/**
	 * Start synchronizing the objects
	 *  First a connection will be made with the database based on the database type and connection information which was declared in the SynchronizerHandler
	 *  Execute the query and create or synchronize a MendixObject for each row in the result
	 * @param applyEntityAcces
	 * 
	 * @param UserAction, this action can be used to return any feedback to the current user
	 * @throws CoreException
	 */
	public abstract void startSynchronizing(UserAction<?> action, Boolean applyEntityAcces) throws CoreException;

	protected void prepareForSynchronization(UserAction<?> action, Boolean applyEntityAcces) throws MendixReplicationException {
		if( !applyEntityAcces )
			this.settings.setContext( this.settings.getContext().getSudoContext() );
		else if( this.settings.importInNewContext() )
			this.settings.setContext( this.settings.getContext().getSession().createContext() );
		
		
		
		this.valueParser = new DBValueParser( this.settings.getValueParsers(), this.settings );
		this.info = new MetaInfo( this.settings, this.valueParser, this.replicationName );
		this.info.TimeMeasurement.startPerformanceTest("Over all");
		this.currentAction = action;
		
		if( this.settings.getInfoHandler() == null )
			this.settings.setInfoHandler(new InfoHandler(this.replicationName));
	
		this.settings.validateSettings();
	}

	protected void callFinishingMicroflow(ReplicationStatusValues status) throws MendixReplicationException {
		
		try { 
			if( this.settings.getFinishingMicroflowName() != null ) {
				HashMap<String, Object> params = new HashMap<String, Object>();
				if( this.settings.getFinishingMicroflowParamName() != null ) {
					IMendixObject replStatus = Core.instantiate(this.settings.getContext(), ReplicationStatus.getType());
					if( this.info != null ) {
						replStatus.setValue(this.settings.getContext(), ReplicationStatus.MemberNames.NrOfObjectsCreated.toString(), this.info.getObjectStats( Stat.Created ) );
						replStatus.setValue(this.settings.getContext(), ReplicationStatus.MemberNames.NrOfObjectsNotFound.toString(), this.info.getObjectStats( Stat.NotFound) );
						replStatus.setValue(this.settings.getContext(), ReplicationStatus.MemberNames.NrOfObjectsRemoved.toString(), this.info.getObjectStats( Stat.Removed) );
						replStatus.setValue(this.settings.getContext(), ReplicationStatus.MemberNames.NrOfObjectsSkipped.toString(), this.info.getObjectStats( Stat.ObjectsSkipped) );
						replStatus.setValue(this.settings.getContext(), ReplicationStatus.MemberNames.NrOfObjectsSynchronized.toString(), this.info.getObjectStats( Stat.Synchronized) );
						replStatus.setValue(this.settings.getContext(), ReplicationStatus.MemberNames.NewRemoveIndicatorValue.toString(), this.settings.getMainObjectConfig().getNewRemoveIndicatorValue());
						replStatus.setValue(this.settings.getContext(), ReplicationStatus.MemberNames.PreviousRemoveIndicatorValue.toString(), this.settings.getMainObjectConfig().getCurrentRemoveIndicatorValue());
					}
					replStatus.setValue(this.settings.getContext(), ReplicationStatus.MemberNames.ReplicationStatus.toString(), status.toString());
					Core.commit(this.settings.getContext(), replStatus);
					params.put(this.settings.getFinishingMicroflowParamName(), replStatus);
				}
				Core.execute(this.settings.getContext(), this.settings.getFinishingMicroflowName(), params);
			}
		}
		catch (Exception e) {
			throw new MendixReplicationException("Unable to execute finishing microflow: " + this.settings.getFinishingMicroflowName() + " because of the following exception: ", MetaInfo._version, e);
		}
	}

	public DBReplicationSettings getSettings() {
		return this.settings;
	}

	public UserAction<?> getCurrentAction() {
		return this.currentAction;
	}

	public String getSynchronizerName() {
		return this.replicationName;
	}

	public void abortAndCommit() {
		this.state = RunningState.AbortCommit;
	}

	public void abortAndRollback() {
		this.state = RunningState.AbortRollback;
	}

	public Integer getRemoveIndicatorValue() {
		if( this.info == null )
			return null;
	
		return this.settings.getMainObjectConfig().getNewRemoveIndicatorValue();
	}

	public void removeUnchangedObjectsByQuery(String xPath) throws MendixReplicationException {
		this.info.removeUnchangedObjectsByQuery( xPath );
	}

	public void abortImport(boolean abortWithCommit) {
		if( abortWithCommit ) 
			this.state = RunningState.AbortCommit;
		else 
			this.state = RunningState.AbortRollback;
	}

}