package am.dopomoga.aidtools.airtable;

public class AirtableRequestUtils {

    public static String lastModifiedDateParam(String isoDate) {
        if (isoDate == null) return null;
        String pattern = "IS_AFTER(LAST_MODIFIED_TIME(),DATETIME_PARSE(\"%s\",\"YYYY-MM-DD\"))";
        return pattern.formatted(isoDate);
    }
}
