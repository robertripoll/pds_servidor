package org.udg.pds.cheapy.rest.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by imartin on 14/02/17.
 */
public class JsonDateSerializer extends JsonSerializer<Date> {
  private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

  @Override
  public void serialize(Date date, JsonGenerator gen, SerializerProvider provider)
      throws IOException, JsonProcessingException {
    String formattedDate = dateFormat.format(date);
    gen.writeString(formattedDate);
  }
}
