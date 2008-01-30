package edu.rice.rubis.hibernate;

import java.util.Date;

public class Comment
{
    private Integer id;
    private User fromUser;
    private User toUser;
    private Item item;
    private Integer rating;
    private Date date;
    private String comment;
    
    public Integer getId()
    {
        return id;
    }
    
    public void setId(Integer id)
    {
        this.id = id;
    }
    
    public User getFromUser()
    {
        return fromUser;
    }
    
    public void setFromUser(User fromUser)
    {
        this.fromUser = fromUser;
    }
    
    public User getToUser()
    {
        return toUser;
    }
    
    public void setToUser(User toUser)
    {
        this.toUser = toUser;
    }
    
    public Item getItem()
    {
        return item;
    }
    
    public void setItem(Item item)
    {
        this.item = item;
    }
    
    public Integer getRating()
    {
        return rating;
    }
    
    public void setRating(Integer rating)
    {
        this.rating = rating;
    }
    
    public Date getDate()
    {
        return date;
    }
    
    public void setDate(Date date)
    {
        this.date = date;
    }
    
    public String getComment()
    {
        return comment;
    }
    
    public void setComment(String comment)
    {
        this.comment = comment;
    }
}
