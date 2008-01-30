package edu.rice.rubis.hibernate;

import java.util.Date;

public class Buy
{
    private Integer id;
    private User buyer;
    private Item item;
    private Integer qty;
    private Date date;
    
    public Integer getId()
    {
        return id;
    }
    
    public void setId(Integer id)
    {
        this.id = id;
    }
    
    public User getBuyer()
    {
        return buyer;
    }
    
    public void setBuyer(User buyer)
    {
        this.buyer = buyer;
    }
    
    public Item getItem()
    {
      return item;
    }
    
    public void setItem(Item item)
    {
      this.item = item;
    }
    
    public Integer getQty()
    {
        return qty;
    }
    
    public void setQty(Integer qty)
    {
        this.qty = qty;
    }
    
    public Date getDate()
    {
        return date;
    }
    
    public void setDate(Date date)
    {
        this.date = date;
    }
}
