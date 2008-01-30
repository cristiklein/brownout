package edu.rice.rubis.hibernate;

import java.util.Date;

public class Bid
{
    private Integer id;
    private User user;
    private Item item;
    private Integer qty;
    private Float bid;
    private Float maxBid;
    private Date date;
    
    public Integer getId()
    {
        return id;
    }
    
    public void setId(Integer id)
    {
        this.id = id;
    }
    
    public User getUser()
    {
        return user;
    }
    
    public void setUser(User user)
    {
        this.user = user;
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
    
    public Float getBid()
    {
        return bid;
    }
    
    public void setBid(Float bid)
    {
        this.bid = bid;
    }
    
    public Float getMaxBid()
    {
        return maxBid;
    }
    
    public void setMaxBid(Float maxBid)
    {
        this.maxBid = maxBid;
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
