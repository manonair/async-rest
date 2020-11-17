package com.mt;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.annotation.Annotation;

@Provider
public class PoweredByFilter implements ContainerResponseFilter {
    @Override
    @PoweredBy
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        for (Annotation a : responseContext.getEntityAnnotations()){
            if(a.annotationType()==PoweredBy.class){
                 String val = ((PoweredBy) a).value();
                responseContext.getHeaders().add("X-Powered-By", val);
            }
        }

    }
}
