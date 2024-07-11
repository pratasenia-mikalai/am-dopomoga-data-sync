package am.dopomoga.aidtools.airtable.dto.request;

import am.dopomoga.aidtools.airtable.dto.response.AbstractAirtableTableResponse;

@FunctionalInterface
public interface AirtableTableSaveFunction<I, O> {

    AbstractAirtableTableResponse<O> send(String baseid, AirtableTableSaveRequest<I> body);

}
