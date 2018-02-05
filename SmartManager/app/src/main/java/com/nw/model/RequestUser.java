package com.nw.model;

import java.util.ArrayList;

/**
 * Created by Akshay on 17-01-2018.
 */

public class RequestUser
{
    private ArrayList<Request> requests;

    public ArrayList<Request> getRequests()
    {
        if (requests == null)
            requests = new ArrayList<Request>();
        return requests;
    }

    public void setRequests(ArrayList<Request> requests)
    {
        this.requests = requests;
    }
}
