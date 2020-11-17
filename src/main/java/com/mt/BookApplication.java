package com.mt;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.fasterxml.jackson.jaxrs.xml.JacksonXMLProvider;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.server.filter.HttpMethodOverrideFilter;
import org.glassfish.jersey.server.filter.UriConnegFilter;

import javax.ws.rs.core.MediaType;
import java.util.HashMap;

public class BookApplication extends ResourceConfig {

    BookApplication(final BookDao dao){
        packages("com.mt");
        JacksonJsonProvider json = new JacksonJsonProvider()
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,false )
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,false)
                .configure(SerializationFeature.INDENT_OUTPUT,true);

        JacksonXMLProvider xmlProvider=new JacksonXMLProvider()
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,false)
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,false)
                .configure(SerializationFeature.INDENT_OUTPUT,true);

        HashMap<String, MediaType> mappings = new HashMap<>();
        mappings.put("xml", MediaType.APPLICATION_XML_TYPE);
        mappings.put("json", MediaType.APPLICATION_JSON_TYPE);
        UriConnegFilter uriConnegFilter = new UriConnegFilter(mappings, null);

        register(new AbstractBinder() {@Override protected void configure() {bind(dao).to(BookDao.class); }});
        register(json);
        register(xmlProvider);
        register(HttpMethodOverrideFilter.class);
        register(uriConnegFilter);

        property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE,true);

    }
}
