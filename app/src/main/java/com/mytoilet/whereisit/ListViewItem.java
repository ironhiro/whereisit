package com.mytoilet.whereisit;

public class ListViewItem implements Comparable<ListViewItem>{
    private String item_name;
    private String item_addr;
    private double item_dist;

    public void setTitle(String item_name)
    {
        this.item_name=item_name;
    }
    public void setAddr(String item_addr)
    {
        this.item_addr=item_addr;
    }
    public void setDist(double item_dist)
    {
        this.item_dist=Math.round(item_dist*100)/100.0;
    }

    public String getTitle()
    {
        return item_name;
    }

    public String getAddr()
    {
        return item_addr;
    }

    public double getDist()
    {
        return item_dist;
    }


    @Override
    public int compareTo(ListViewItem o) {
        if(this.item_dist < o.item_dist)
            return -1;
        else if(o.item_dist < this.item_dist)
            return 1;
        return 0;
    }
}
