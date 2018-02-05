package com.nw.model;

import java.util.ArrayList;

/**
 * Created by Akshay on 17-01-2018.
 */

public class Reference
{
    private ArrayList<ReferenceID> references;

    public ArrayList<ReferenceID> getRequests()
    {
        if (references == null)
            references = new ArrayList<ReferenceID>();
        return references;
    }

    public void setRequests(ArrayList<ReferenceID> requests)
    {
        this.references = references;
    }
}
