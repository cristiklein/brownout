package edu.rice.rubis.hibernate;

import java.util.Set;

public class Region
{
    private Integer id;
    private String name;
    private Set users;
    
    public Integer getId()
    {
        return id;
    }
    
    public void setId(Integer id)
    {
        this.id = id;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public Set getUsers()
    {
      return users;
    }
    
    public void setUsers(Set users)
    {
      this.users = users;
    }
}
