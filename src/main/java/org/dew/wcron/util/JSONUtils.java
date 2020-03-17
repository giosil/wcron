package org.dew.wcron.util;

import java.io.StringReader;
import java.io.StringWriter;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import java.math.BigDecimal;
import java.math.BigInteger;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;

import javax.json.stream.JsonGenerator;

public 
class JSONUtils 
{
  private final static char[] acDateTime = {'0','0','0','0','-','0','0','-','0','0','T','0','0',':','0','0',':','0','0','.','0','0','0','Z'};
  
  public static boolean DATETIME_TO_STRING = true;
  
  public static 
  String stringify(Object value)
  {
    if(value == null) return "null";
    if(value instanceof Number) {
      return value.toString();
    }
    if(value instanceof Boolean) {
      return value.toString();
    }
    
    boolean valueIsObjectOrArray = isObjectOrArray(value);
    
    StringWriter stringWriter = new StringWriter();
    JsonGenerator jsonGenerator = Json.createGenerator(stringWriter);
    if(!valueIsObjectOrArray) {
      jsonGenerator.writeStartArray();
    }
    write(jsonGenerator, value);
    if(!valueIsObjectOrArray) {
      jsonGenerator.writeEnd();
    }
    jsonGenerator.close();
    
    String result = stringWriter.toString();
    if(!valueIsObjectOrArray) {
      result = removeBrackets(result);
    }
    return result;
  }
  
  public static 
  Object parse(String text)
  {
    if(text == null) {
      return null;
    }
    text = text.trim();
    if(text.length() == 0) {
      return null;
    }
    if(text.equalsIgnoreCase("null")) {
      return null;
    }
    if(text.equalsIgnoreCase("true")) {
      return Boolean.TRUE;
    }
    if(text.equalsIgnoreCase("false")) {
      return Boolean.FALSE;
    }
    char c0 = text.charAt(0);
    boolean valueIsObjectOrArray = c0 == '{' || c0 == '[';
    if(!valueIsObjectOrArray) {
      text = "[" + text + "]";
    }
    
    JsonReader reader = Json.createReader(new StringReader(text));
    
    JsonStructure jsonStructure = reader.read();
    
    Object result = toObject(jsonStructure);
    if(!valueIsObjectOrArray) {
      result = getFirst(result);
    }
    
    return result;
  }
  
  @SuppressWarnings("unchecked")
  public static 
  Map<String, Object> parseObject(String text, boolean notNull)
  {
    Object result = parse(text);
    
    if(result instanceof Map) {
      return (Map<String, Object>) result;
    }
    
    if(notNull) {
      return new HashMap<String, Object>();
    }
    
    return null;
  }
  
  @SuppressWarnings("unchecked")
  public static 
  List<Object> parseArray(String text, boolean notNull)
  {
    Object result = parse(text);
    
    if(result instanceof List) {
      return (List<Object>) result;
    }
    
    if(notNull) {
      return new ArrayList<Object>();
    }
    
    return null;
  }
  
  public static 
  Object toObject(JsonValue jsonValue)
  {
    if(jsonValue == null) return null;
    
    ValueType valueType = jsonValue.getValueType();
    if(valueType == null) return null;
    
    switch (valueType) {
    case STRING:
      if(jsonValue instanceof JsonString) {
        JsonString jsonString = (JsonString) jsonValue;
        String result = jsonString.getString();
        if(isDateTime(result)) {
          return stringToDateTime(result);
        }
        return result;
      }
      break;
    case NUMBER:
      if(jsonValue instanceof JsonNumber) {
        JsonNumber jsonNumber = (JsonNumber) jsonValue;
        if(jsonNumber.isIntegral()) {
          long lngValue = jsonNumber.longValue();
          int  intValue = jsonNumber.intValue();
          String sLngValue = String.valueOf(lngValue);
          String sIntValue = String.valueOf(intValue);
          if(sLngValue.equals(sIntValue)) {
            return intValue;
          }
          return lngValue;
        }
        else {
          return jsonNumber.doubleValue();
        }
      }
      break;
    case ARRAY:
      if(jsonValue instanceof JsonArray) {
        return toList((JsonArray) jsonValue);
      }
      break;
    case OBJECT:
      if(jsonValue instanceof JsonObject) {
        return toMap((JsonObject) jsonValue);
      }
      break;
    case TRUE:
      return Boolean.TRUE;
    case FALSE:
      return Boolean.FALSE;
    case NULL:
      return null;
    }
    return jsonValue.toString();
  }
  
  private static 
  List<Object> toList(JsonArray jsonArray)
  {
    if(jsonArray == null) return null;
    List<Object> listResult = new ArrayList<Object>(jsonArray.size());
    jsonArray.forEach(jsonValue -> listResult.add(toObject(jsonValue)));
    return listResult;
  }
  
  private static 
  Map<String, Object> toMap(JsonObject jsonObject)
  {
    if(jsonObject == null) return null;
    Map<String, Object> mapResult = new HashMap<String, Object>(jsonObject.size());
    jsonObject.forEach((key, jsonValue) -> mapResult.put(key, toObject(jsonValue)));
    return mapResult;
  }
  
  private static 
  void write(JsonGenerator jsonGenerator, Object value)
  {
    if(value == null) {
      jsonGenerator.write(JsonValue.NULL);
      return;
    }
    
    if(value instanceof Map) {
      jsonGenerator.writeStartObject();
      Map<?, ?> map = (Map<?, ?>) value;
      map.forEach((k, v) -> write(jsonGenerator, k.toString(), v));
      jsonGenerator.writeEnd();
    }
    else if(value instanceof Collection) {
      jsonGenerator.writeStartArray();
      Collection<?> collection = (Collection<?>) value;
      collection.forEach(item -> write(jsonGenerator, item));
      jsonGenerator.writeEnd();
    }
    else if(value.getClass().isArray()) {
      jsonGenerator.writeStartArray();
      int length = Array.getLength(value);
      for(int i = 0; i < length; i++) {
        Object item = Array.get(value, i);
        write(jsonGenerator, item);
      }
      jsonGenerator.writeEnd();
    }
    else if(value instanceof String) {
      jsonGenerator.write((String) value);
    }
    else if(value instanceof Integer) {
      jsonGenerator.write(((Integer) value).intValue());
    }
    else if(value instanceof Long) {
      jsonGenerator.write(((Long) value).longValue());
    }
    else if(value instanceof Double) {
      jsonGenerator.write(((Double) value).doubleValue());
    }
    else if(value instanceof BigDecimal) {
      jsonGenerator.write((BigDecimal) value);
    }
    else if(value instanceof BigInteger) {
      jsonGenerator.write((BigInteger) value);
    }
    else if(value instanceof Boolean) {
      jsonGenerator.write(((Boolean) value).booleanValue());
    }
    else if(value instanceof Date) {
      long millis = ((Date) value).getTime();
      if(DATETIME_TO_STRING) {
        jsonGenerator.write(dateTimeToString(millis));
      }
      else {
        jsonGenerator.write(millis);
      }
    }
    else if(value instanceof Calendar) {
      long millis = ((Calendar) value).getTimeInMillis();
      if(DATETIME_TO_STRING) {
        jsonGenerator.write(dateTimeToString(millis));
      }
      else {
        jsonGenerator.write(millis);
      }
    }
    else if(value instanceof LocalDate) {
      long millis = ((LocalDate) value).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
      if(DATETIME_TO_STRING) {
        jsonGenerator.write(dateTimeToString(millis));
      }
      else {
        jsonGenerator.write(millis);
      }
    }
    else if(value instanceof LocalDateTime) {
      long millis = ((LocalDateTime) value).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
      if(DATETIME_TO_STRING) {
        jsonGenerator.write(dateTimeToString(millis));
      }
      else {
        jsonGenerator.write(millis);
      }
    }
    else {
      jsonGenerator.writeStartObject();
      Map<String, Object> map = beanToMap(value);
      map.forEach((k, v) -> write(jsonGenerator, k.toString(), v));
      jsonGenerator.writeEnd();
    }
  }
  
  private static 
  void write(JsonGenerator jsonGenerator, String key, Object value)
  {
    if(value == null) {
      jsonGenerator.write(key, JsonValue.NULL);
      return;
    }
    
    if(value instanceof Map) {
      jsonGenerator.writeStartObject(key);
      Map<?, ?> map = (Map<?, ?>) value;
      map.forEach((k, v) -> write(jsonGenerator, k.toString(), v));
      jsonGenerator.writeEnd();
    }
    else if(value instanceof Collection) {
      jsonGenerator.writeStartArray(key);
      Collection<?> collection = (Collection<?>) value;
      collection.forEach(item -> write(jsonGenerator, item));
      jsonGenerator.writeEnd();
    }
    else if(value.getClass().isArray()) {
      jsonGenerator.writeStartArray(key);
      int length = Array.getLength(value);
      for(int i = 0; i < length; i++) {
        Object item = Array.get(value, i);
        write(jsonGenerator, item);
      }
      jsonGenerator.writeEnd();
    }
    else if(value instanceof String) {
      jsonGenerator.write(key, (String) value);
    }
    else if(value instanceof Integer) {
      jsonGenerator.write(key, ((Integer) value).intValue());
    }
    else if(value instanceof Long) {
      jsonGenerator.write(key, ((Long) value).longValue());
    }
    else if(value instanceof Double) {
      jsonGenerator.write(key, ((Double) value).doubleValue());
    }
    else if(value instanceof BigDecimal) {
      jsonGenerator.write(key, (BigDecimal) value);
    }
    else if(value instanceof BigInteger) {
      jsonGenerator.write(key, (BigInteger) value);
    }
    else if(value instanceof Boolean) {
      jsonGenerator.write(key, ((Boolean) value).booleanValue());
    }
    else if(value instanceof Date) {
      long millis = ((Date) value).getTime();
      if(DATETIME_TO_STRING) {
        jsonGenerator.write(key, dateTimeToString(millis));
      }
      else {
        jsonGenerator.write(key, millis);
      }
    }
    else if(value instanceof Calendar) {
      long millis = ((Calendar) value).getTimeInMillis();
      if(DATETIME_TO_STRING) {
        jsonGenerator.write(key, dateTimeToString(millis));
      }
      else {
        jsonGenerator.write(key, millis);
      }
    }
    else if(value instanceof LocalDate) {
      long millis = ((LocalDate) value).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
      if(DATETIME_TO_STRING) {
        jsonGenerator.write(key, dateTimeToString(millis));
      }
      else {
        jsonGenerator.write(key, millis);
      }
    }
    else if(value instanceof LocalDateTime) {
      long millis = ((LocalDateTime) value).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
      if(DATETIME_TO_STRING) {
        jsonGenerator.write(key, dateTimeToString(millis));
      }
      else {
        jsonGenerator.write(key, millis);
      }
    }
    else {
      jsonGenerator.writeStartObject(key);
      Map<String, Object> map = beanToMap(value);
      map.forEach((k, v) -> write(jsonGenerator, k.toString(), v));
      jsonGenerator.writeEnd();
    }
  }
  
  public static 
  Map<String, Object> beanToMap(Object bean) 
  {
    Map<String, Object> mapResult = new HashMap<String, Object>();
    
    if(bean == null) return mapResult;
    
    Class<?> klass = bean.getClass();
    String className = klass.getName();
    if(className.startsWith("java.")) {
      mapResult.put("value", bean);
      return mapResult;
    }
    
    // If klass is a System class then set includeSuperClass to false.
    boolean includeSuperClass = klass.getClassLoader() != null;
    Method[] methods  = includeSuperClass ? klass.getMethods() : klass.getDeclaredMethods();
    String methodName = null;
    for(int i = 0; i < methods.length; i++) {
      try {
        Method method = methods[i];
        if(Modifier.isPublic(method.getModifiers())) {
          methodName = method.getName();
          String key = "";
          if(methodName.startsWith("get")) {
            if("getClass".equals(methodName) || "getDeclaringClass".equals(methodName)) {
              key = "";
            } 
            else {
              key = methodName.substring(3);
            }
          } 
          else
          if(methodName.startsWith("is")) {
            key = methodName.substring(2);
          }
          if(key.length() > 0 && key.charAt(0) < 97 && method.getParameterTypes().length == 0) {
            if(key.length() == 1) {
              key = key.toLowerCase();
            } 
            else
            if(!Character.isUpperCase(key.charAt(1))) {
              key = key.substring(0, 1).toLowerCase() + key.substring(1);
            }
            Object result = method.invoke(bean,(Object[]) null);
            if(result instanceof Collection) {
              mapResult.put(key, collectionToNormalizedList((Collection<?>) result));
            }
            else
            if(result != null) {
              Class<?> klassResult = result.getClass();
              if(klassResult.isEnum()) {
                mapResult.put(key, result.toString());
              }
              else
              if(klassResult.isArray()) {
                mapResult.put(key, arrayToNormalizedList(result));
              }
              else {
                String classNameResult = klassResult.getName();
                if(!classNameResult.startsWith("java.")) {
                  mapResult.put(key, beanToMap(result));
                }
                else {
                  mapResult.put(key, result);
                }
              }
            }
          }
        }
      } 
      catch(Exception ex) {
        System.err.println("[JSONUtils.beanToMap] (bean=" + bean.getClass() + ",methodName=" + methodName + "): " + ex);
      }
    }
    return mapResult;
  }
  
  private static
  List<Object> collectionToNormalizedList(Collection<?> collection)
  {
    if(collection == null) {
      return new ArrayList<Object>(0);
    }
    List<Object> listResult = new ArrayList<Object>(collection.size());
    Iterator<?> iterator = collection.iterator();
    while(iterator.hasNext()) {
      Object item = iterator.next();
      if(item == null) {
        listResult.add(item);
      }
      else
      if(item instanceof Collection) {
        listResult.add(collectionToNormalizedList((Collection<?>) item));
      }
      else {
        Class<?> klass = item.getClass();
        if(klass.isArray()) {
          listResult.add(arrayToNormalizedList(item));
        }
        else {
          String className = klass.getName();
          if(!className.startsWith("java.")) {
            listResult.add(beanToMap(item));
          }
          else {
            listResult.add(item);
          }
        }
      }
    }
    return listResult;
  }
  
  private static 
  List<Object> arrayToNormalizedList(Object array) 
  {
    if(array == null) {
      return new ArrayList<Object>(0);
    }
    if(!array.getClass().isArray()) {
      return new ArrayList<Object>(0);
    }
    int length = Array.getLength(array);
    if(length == 0) {
      return new ArrayList<Object>(0);
    }
    List<Object> listResult = new ArrayList<Object>(length);
    for(int i = 0; i < length; i++) {
      Object item = Array.get(array, i);
      if(item == null) {
        listResult.add(item);
      }
      else
      if(item instanceof Collection) {
        listResult.add(collectionToNormalizedList((Collection<?>) item));
      }
      else {
        Class<?> klass = item.getClass();
        if(klass.isArray()) {
          listResult.add(arrayToNormalizedList(item));
        }
        else {
          String className = klass.getName();
          if(!className.startsWith("java.")) {
            listResult.add(beanToMap(item));
          }
          else {
            listResult.add(item);
          }
        }
      }
    }
    return listResult;
  }
  
  private static
  String dateTimeToString(long millis)
  {
    Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis(millis);
    
    int iZoneOffset = cal.get(Calendar.ZONE_OFFSET);
    cal.add(Calendar.MILLISECOND, -iZoneOffset);
    int iDST_Offset = cal.get(Calendar.DST_OFFSET);
    cal.add(Calendar.MILLISECOND, -iDST_Offset);
    
    int iYear  = cal.get(Calendar.YEAR);
    int iMonth = cal.get(Calendar.MONTH) + 1;
    int iDay   = cal.get(Calendar.DATE);
    int iHour  = cal.get(Calendar.HOUR_OF_DAY);
    int iMin   = cal.get(Calendar.MINUTE);
    int iSec   = cal.get(Calendar.SECOND);
    int iMill  = cal.get(Calendar.MILLISECOND);
    String sYear   = String.valueOf(iYear);
    String sMonth  = iMonth < 10 ? "0" + iMonth : String.valueOf(iMonth);
    String sDay    = iDay   < 10 ? "0" + iDay   : String.valueOf(iDay);
    String sHour   = iHour  < 10 ? "0" + iHour  : String.valueOf(iHour);
    String sMin    = iMin   < 10 ? "0" + iMin   : String.valueOf(iMin);
    String sSec    = iSec   < 10 ? "0" + iSec   : String.valueOf(iSec);
    String sMill   = String.valueOf(iMill);
    if(iYear < 10) {
      sYear = "000" + sYear;
    }
    else if(iYear < 100) {
      sYear = "00" + sYear;
    }
    else if(iYear < 1000) {
      sYear = "0" + sYear;
    }
    if(iMill < 10) {
      sMill = "00" + sMill;
    }
    else if(iMill < 100) {
      sMill = "0" + sMill;
    }
    return sYear + "-" + sMonth + "-" + sDay + "T" + sHour + ":" + sMin + ":" + sSec + "." + sMill + "Z";
  }
  
  private static
  boolean isDateTime(String s)
  {
    boolean boDateTime = false;
    if(s.length() == acDateTime.length) {
      boDateTime = true;
      for(int i = 0; i < acDateTime.length; i++) {
        char cs = s.charAt(i);
        char cp = acDateTime[i];
        if(cs == cp) continue;
        if(cp == 48) {
          if(cs < 48 || cs > 57) {
            boDateTime = false;
            break;
          }
        }
        else {
          boDateTime = false;
          break;
        }
      }
    }
    return boDateTime;
  }
  
  private static
  Object stringToDateTime(String s)
  {
    if(s == null || s.length() != acDateTime.length) {
      return null;
    }
    
    boolean boDateTime = true;
    for(int i = 0; i < acDateTime.length; i++) {
      char cs = s.charAt(i);
      char cp = acDateTime[i];
      if(cs == cp) continue;
      if(cp == 48) {
        if(cs < 48 || cs > 57) {
          boDateTime = false;
          break;
        }
      }
      else {
        boDateTime = false;
        break;
      }
    }
    
    if(!boDateTime) return null;
    
    int iYear  = Integer.parseInt(s.substring( 0,  4));
    int iMonth = Integer.parseInt(s.substring( 5,  7));
    int iDay   = Integer.parseInt(s.substring( 8, 10));
    int iHour  = Integer.parseInt(s.substring(11, 13));
    int iMin   = Integer.parseInt(s.substring(14, 16));
    int iSec   = Integer.parseInt(s.substring(17, 19));
    int iMill  = Integer.parseInt(s.substring(20, 23));
    
    Calendar c = Calendar.getInstance();
    c.set(Calendar.YEAR,        iYear);
    c.set(Calendar.MONTH,       iMonth-1);
    c.set(Calendar.DATE,        iDay);
    c.set(Calendar.HOUR_OF_DAY, iHour);
    c.set(Calendar.MINUTE,      iMin);
    c.set(Calendar.SECOND,      iSec);
    c.set(Calendar.MILLISECOND, iMill);
    
    int iZoneOffset = c.get(Calendar.ZONE_OFFSET);
    c.add(Calendar.MILLISECOND, iZoneOffset);
    int iDST_Offset = c.get(Calendar.DST_OFFSET);
    c.add(Calendar.MILLISECOND, iDST_Offset);
    
    Calendar r = new GregorianCalendar(c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DATE),c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE),c.get(Calendar.SECOND));
    r.set(Calendar.MILLISECOND,c.get(Calendar.MILLISECOND));
    return r.getTime();
  }
  
  private static 
  boolean isObjectOrArray(Object value)
  {
    if(value == null) {
      return false;
    }
    if(value instanceof Map) {
      return true;
    }
    if(value instanceof Collection) {
      return true;
    }
    Class<?> valueClass = value.getClass();
    if(valueClass.isArray()) {
      return true;
    }
    String valueClassName = valueClass.getName();
    if(!valueClassName.startsWith("java.")) {
      return true;
    }
    return false;
  }
  
  private static 
  String removeBrackets(String text)
  {
    if(text == null) return null;
    text = text.trim();
    if(text.length() == 0) return "";
    char c0 = text.charAt(0);
    char cL = text.charAt(text.length()-1);
    if(c0 == '[' && cL == ']') {
      return text.substring(1, text.length()-1).trim();
    }
    return text;
  }
  
  private static
  Object getFirst(Object object)
  {
    if(object == null) return null;
    if(object instanceof Collection) {
      Iterator<?> iterator = ((Collection<?>) object).iterator();
      if(iterator.hasNext()) return iterator.next();
      return null;
    }
    else if(object.getClass().isArray()) {
      int length = Array.getLength(object);
      if(length == 0) return null;
      return Array.get(object, 0);
    }
    return object;
  }
}
