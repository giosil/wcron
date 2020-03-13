package org.dew.wcron.auth;

import java.io.Serializable;

import java.security.Principal;

public 
class WPrincipal implements Principal, Serializable
{
  private static final long serialVersionUID = 4098577524490526135L;
  
  private final String name;
  
  public 
  WPrincipal(String name) 
  {
    this.name = name;
  }
  
  @Override
  public 
  String getName() 
  {
    return name;
  }
  
  @Override
  public 
  boolean equals(Object another) 
  {
    if (!(another instanceof Principal)) return false;
    String anotherName = Principal.class.cast(another).getName();
    if(name == null && anotherName == null) return true;
    return name != null && name.equals(anotherName);
  }
  
  @Override
  public 
  int hashCode() 
  {
    return (name == null ? 0 : name.hashCode());
  }
  
  @Override
  public 
  String toString() 
  {
    return name;
  }
}
