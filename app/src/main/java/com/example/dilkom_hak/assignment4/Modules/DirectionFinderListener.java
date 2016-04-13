package com.example.dilkom_hak.assignment4.Modules;


import java.util.ArrayList;
import java.util.List;


public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route);
}
