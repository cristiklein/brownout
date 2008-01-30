package edu.rice.rubis.hibernate;

import java.util.Set;

public class Category
{
    private Integer id;
    private String name;
    private Set items;
    
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
    
    public Set getItems()
    {
      return items;
    }
    
    public void setItems(Set items)
    {
      this.items = items;
    }
}
