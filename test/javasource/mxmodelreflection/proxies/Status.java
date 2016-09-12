// This file was generated by Mendix Modeler.
//
// WARNING: Code you write here will be lost the next time you deploy the project.

package mxmodelreflection.proxies;

public enum Status
{
	Valid(new String[][] { new String[] { "en_US", "Valid" }, new String[] { "nl_NL", "Geldig" }, new String[] { "en_GB", "Valid" }, new String[] { "en_ZA", "Valid" } }),
	Invalid(new String[][] { new String[] { "en_US", "Invalid" }, new String[] { "nl_NL", "Ongeldig" }, new String[] { "en_GB", "Invalid" }, new String[] { "en_ZA", "Invalid" } });

	private java.util.Map<String,String> captions;

	private Status(String[][] captionStrings)
	{
		this.captions = new java.util.HashMap<String,String>();
		for (String[] captionString : captionStrings)
			captions.put(captionString[0], captionString[1]);
	}

	public String getCaption(String languageCode)
	{
		if (captions.containsKey(languageCode))
			return captions.get(languageCode);
		return captions.get("en_US");
	}

	public String getCaption()
	{
		return captions.get("en_US");
	}
}
