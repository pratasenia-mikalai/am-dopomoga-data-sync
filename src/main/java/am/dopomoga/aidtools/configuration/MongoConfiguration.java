package am.dopomoga.aidtools.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Configuration
@EnableMongoRepositories(basePackages = "am.dopomoga.aidtools.repository.mongo")
public class MongoConfiguration {

    @Bean
    public MongoCustomConversions customConversions() {
        List<Converter<?, ?>> converterList = new ArrayList<>();
        converterList.add(new DateToOffsetDateTimeConverter());
        converterList.add(new OffsetDateTimeToDateConverter());
        return new MongoCustomConversions(converterList);
    }

    public static class OffsetDateTimeToDateConverter implements Converter<OffsetDateTime, Date> {

        @Override
        public Date convert(OffsetDateTime source) {
            return source == null ? null : Date.from(source.toInstant());
        }
    }

    public static class DateToOffsetDateTimeConverter implements Converter<Date, OffsetDateTime> {

        @Override
        public OffsetDateTime convert(Date source) {
            return source == null ? null : OffsetDateTime.ofInstant(source.toInstant(), ZoneId.systemDefault());
        }
    }

}
