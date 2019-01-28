package com.emaraic.utils;

import java.util.Comparator;
import org.deeplearning4j.clustering.cluster.Cluster;



public class LinesComparator implements Comparator<Cluster> {

	
	
    public int compare(Cluster t1, Cluster t2) {
        return Integer.valueOf((int)t1.getCenter().getArray().getFloat(0)).compareTo(Integer.valueOf((int) t2.getCenter().getArray().getFloat(0)));
    }

        

}