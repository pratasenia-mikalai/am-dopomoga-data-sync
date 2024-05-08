package am.dopomoga.aidtools.airtable.dto.response;

@FunctionalInterface
public interface AirtableTableListFunction<T> {

    AbstractAirtableTableResponse<T> get(String baseid, String offset, String filterByFormula);

}
