package org.dew.wcron.rest;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.config.BinaryDataStrategy;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public 
class CustomJsonBProvider implements ContextResolver<Jsonb> 
{
  protected Jsonb jsonb;
  
  public CustomJsonBProvider()
  {
    System.out.println("CustomJsonBProvider()");
    
    JsonbConfig config = new JsonbConfig();
    config.withBinaryDataStrategy(BinaryDataStrategy.BASE_64);
    config.withDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", null);
    
    jsonb = JsonbBuilder.newBuilder().withConfig(config).build();
  }
  
  @Override
  public 
  Jsonb getContext(Class<?> type) 
  {
    return jsonb;
  }
}