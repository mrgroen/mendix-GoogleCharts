// This file was generated by Mendix Modeler.
//
// WARNING: Code you write here will be lost the next time you deploy the project.

package databasereplication.proxies;

public class UpdateConfiguration
{
	private final com.mendix.systemwideinterfaces.core.IMendixObject updateConfigurationMendixObject;

	private final com.mendix.systemwideinterfaces.core.IContext context;

	/**
	 * Internal name of this entity
	 */
	public static final java.lang.String entityName = "DatabaseReplication.UpdateConfiguration";

	/**
	 * Enum describing members of this entity
	 */
	public enum MemberNames
	{
		UpdateType("UpdateType"),
		UpdateConfiguration_TableMapping("DatabaseReplication.UpdateConfiguration_TableMapping");

		private java.lang.String metaName;

		MemberNames(java.lang.String s)
		{
			metaName = s;
		}

		@Override
		public java.lang.String toString()
		{
			return metaName;
		}
	}

	public UpdateConfiguration(com.mendix.systemwideinterfaces.core.IContext context)
	{
		this(context, com.mendix.core.Core.instantiate(context, "DatabaseReplication.UpdateConfiguration"));
	}

	protected UpdateConfiguration(com.mendix.systemwideinterfaces.core.IContext context, com.mendix.systemwideinterfaces.core.IMendixObject updateConfigurationMendixObject)
	{
		if (updateConfigurationMendixObject == null)
			throw new java.lang.IllegalArgumentException("The given object cannot be null.");
		if (!com.mendix.core.Core.isSubClassOf("DatabaseReplication.UpdateConfiguration", updateConfigurationMendixObject.getType()))
			throw new java.lang.IllegalArgumentException("The given object is not a DatabaseReplication.UpdateConfiguration");

		this.updateConfigurationMendixObject = updateConfigurationMendixObject;
		this.context = context;
	}

	/**
	 * @deprecated Use 'UpdateConfiguration.load(IContext, IMendixIdentifier)' instead.
	 */
	@Deprecated
	public static databasereplication.proxies.UpdateConfiguration initialize(com.mendix.systemwideinterfaces.core.IContext context, com.mendix.systemwideinterfaces.core.IMendixIdentifier mendixIdentifier) throws com.mendix.core.CoreException
	{
		return databasereplication.proxies.UpdateConfiguration.load(context, mendixIdentifier);
	}

	/**
	 * Initialize a proxy using context (recommended). This context will be used for security checking when the get- and set-methods without context parameters are called.
	 * The get- and set-methods with context parameter should be used when for instance sudo access is necessary (IContext.getSudoContext() can be used to obtain sudo access).
	 */
	public static databasereplication.proxies.UpdateConfiguration initialize(com.mendix.systemwideinterfaces.core.IContext context, com.mendix.systemwideinterfaces.core.IMendixObject mendixObject)
	{
		return new databasereplication.proxies.UpdateConfiguration(context, mendixObject);
	}

	public static databasereplication.proxies.UpdateConfiguration load(com.mendix.systemwideinterfaces.core.IContext context, com.mendix.systemwideinterfaces.core.IMendixIdentifier mendixIdentifier) throws com.mendix.core.CoreException
	{
		com.mendix.systemwideinterfaces.core.IMendixObject mendixObject = com.mendix.core.Core.retrieveId(context, mendixIdentifier);
		return databasereplication.proxies.UpdateConfiguration.initialize(context, mendixObject);
	}

	public static java.util.List<databasereplication.proxies.UpdateConfiguration> load(com.mendix.systemwideinterfaces.core.IContext context, java.lang.String xpathConstraint) throws com.mendix.core.CoreException
	{
		java.util.List<databasereplication.proxies.UpdateConfiguration> result = new java.util.ArrayList<databasereplication.proxies.UpdateConfiguration>();
		for (com.mendix.systemwideinterfaces.core.IMendixObject obj : com.mendix.core.Core.retrieveXPathQuery(context, "//DatabaseReplication.UpdateConfiguration" + xpathConstraint))
			result.add(databasereplication.proxies.UpdateConfiguration.initialize(context, obj));
		return result;
	}

	/**
	 * Commit the changes made on this proxy object.
	 */
	public final void commit() throws com.mendix.core.CoreException
	{
		com.mendix.core.Core.commit(context, getMendixObject());
	}

	/**
	 * Commit the changes made on this proxy object using the specified context.
	 */
	public final void commit(com.mendix.systemwideinterfaces.core.IContext context) throws com.mendix.core.CoreException
	{
		com.mendix.core.Core.commit(context, getMendixObject());
	}

	/**
	 * Delete the object.
	 */
	public final void delete()
	{
		com.mendix.core.Core.delete(context, getMendixObject());
	}

	/**
	 * Delete the object using the specified context.
	 */
	public final void delete(com.mendix.systemwideinterfaces.core.IContext context)
	{
		com.mendix.core.Core.delete(context, getMendixObject());
	}
	/**
	 * Set value of UpdateType
	 * @param updatetype
	 */
	public final databasereplication.proxies.UpdateType getUpdateType()
	{
		return getUpdateType(getContext());
	}

	/**
	 * @param context
	 * @return value of UpdateType
	 */
	public final databasereplication.proxies.UpdateType getUpdateType(com.mendix.systemwideinterfaces.core.IContext context)
	{
		Object obj = getMendixObject().getValue(context, MemberNames.UpdateType.toString());
		if (obj == null)
			return null;

		return databasereplication.proxies.UpdateType.valueOf((java.lang.String) obj);
	}

	/**
	 * Set value of UpdateType
	 * @param updatetype
	 */
	public final void setUpdateType(databasereplication.proxies.UpdateType updatetype)
	{
		setUpdateType(getContext(), updatetype);
	}

	/**
	 * Set value of UpdateType
	 * @param context
	 * @param updatetype
	 */
	public final void setUpdateType(com.mendix.systemwideinterfaces.core.IContext context, databasereplication.proxies.UpdateType updatetype)
	{
		if (updatetype != null)
			getMendixObject().setValue(context, MemberNames.UpdateType.toString(), updatetype.toString());
		else
			getMendixObject().setValue(context, MemberNames.UpdateType.toString(), null);
	}

	/**
	 * @return value of UpdateConfiguration_TableMapping
	 */
	public final databasereplication.proxies.TableMapping getUpdateConfiguration_TableMapping() throws com.mendix.core.CoreException
	{
		return getUpdateConfiguration_TableMapping(getContext());
	}

	/**
	 * @param context
	 * @return value of UpdateConfiguration_TableMapping
	 */
	public final databasereplication.proxies.TableMapping getUpdateConfiguration_TableMapping(com.mendix.systemwideinterfaces.core.IContext context) throws com.mendix.core.CoreException
	{
		databasereplication.proxies.TableMapping result = null;
		com.mendix.systemwideinterfaces.core.IMendixIdentifier identifier = getMendixObject().getValue(context, MemberNames.UpdateConfiguration_TableMapping.toString());
		if (identifier != null)
			result = databasereplication.proxies.TableMapping.load(context, identifier);
		return result;
	}

	/**
	 * Set value of UpdateConfiguration_TableMapping
	 * @param updateconfiguration_tablemapping
	 */
	public final void setUpdateConfiguration_TableMapping(databasereplication.proxies.TableMapping updateconfiguration_tablemapping)
	{
		setUpdateConfiguration_TableMapping(getContext(), updateconfiguration_tablemapping);
	}

	/**
	 * Set value of UpdateConfiguration_TableMapping
	 * @param context
	 * @param updateconfiguration_tablemapping
	 */
	public final void setUpdateConfiguration_TableMapping(com.mendix.systemwideinterfaces.core.IContext context, databasereplication.proxies.TableMapping updateconfiguration_tablemapping)
	{
		if (updateconfiguration_tablemapping == null)
			getMendixObject().setValue(context, MemberNames.UpdateConfiguration_TableMapping.toString(), null);
		else
			getMendixObject().setValue(context, MemberNames.UpdateConfiguration_TableMapping.toString(), updateconfiguration_tablemapping.getMendixObject().getId());
	}

	/**
	 * @return the IMendixObject instance of this proxy for use in the Core interface.
	 */
	public final com.mendix.systemwideinterfaces.core.IMendixObject getMendixObject()
	{
		return updateConfigurationMendixObject;
	}

	/**
	 * @return the IContext instance of this proxy, or null if no IContext instance was specified at initialization.
	 */
	public final com.mendix.systemwideinterfaces.core.IContext getContext()
	{
		return context;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
			return true;

		if (obj != null && getClass().equals(obj.getClass()))
		{
			final databasereplication.proxies.UpdateConfiguration that = (databasereplication.proxies.UpdateConfiguration) obj;
			return getMendixObject().equals(that.getMendixObject());
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return getMendixObject().hashCode();
	}

	/**
	 * @return String name of this class
	 */
	public static java.lang.String getType()
	{
		return "DatabaseReplication.UpdateConfiguration";
	}

	/**
	 * @return String GUID from this object, format: ID_0000000000
	 * @deprecated Use getMendixObject().getId().toLong() to get a unique identifier for this object.
	 */
	@Deprecated
	public java.lang.String getGUID()
	{
		return "ID_" + getMendixObject().getId().toLong();
	}
}