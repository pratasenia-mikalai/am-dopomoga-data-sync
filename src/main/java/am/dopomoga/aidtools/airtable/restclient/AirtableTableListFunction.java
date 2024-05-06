package am.dopomoga.aidtools.airtable.restclient;

import am.dopomoga.aidtools.airtable.dto.response.AbstractAirtableTableResponse;

@FunctionalInterface
public interface AirtableTableListFunction<T> {

    AbstractAirtableTableResponse<T> get(String baseid, String offset, String filterByFormula);

}
