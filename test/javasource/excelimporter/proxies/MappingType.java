// This file was generated by Mendix Modeler.
//
// WARNING: Code you write here will be lost the next time you deploy the project.

package excelimporter.proxies;

public enum MappingType
{
	Attribute(new String[][] { new String[] { "en_US", "Attribute" }, new String[] { "nl_NL", "Attribuut" }, new String[] { "en_GB", "Attribute" }, new String[] { "en_ZA", "Attribute" } }),
	Reference(new String[][] { new String[] { "en_US", "Reference" }, new String[] { "nl_NL", "Referentie" }, new String[] { "en_GB", "Reference" }, new String[] { "en_ZA", "Reference" } }),
	DoNotUse(new String[][] { new String[] { "en_US", "Do not use" }, new String[] { "nl_NL", "Niet gebruiken" }, new String[] { "en_GB", "Do not use" }, new String[] { "en_ZA", "Do not use" } });

	private java.util.Map<String,String> captions;

	private MappingType(String[][] captionStrings)
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