/**
Copyright 2010 University of South Florida

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

**/

package object;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import tools.OsmDistance;

/**
 *
 * @author Khoa Tran
 */

public class Stop extends OsmPrimitive implements Comparable{
    private final double ERROR_TO_ZERO = 0.5;
    private final String GTFS_STOP_ID_KEY = "gtfs_id";
    private final String GTFS_OPERATOR_KEY = "operator";
    private final String NTD_ID_KEY = "ntd_id";
    private String stopName, lat, lon;
    public Stop(String stopID, String operatorName, String stopName, String lat, String lon) {
        osmTags = new Hashtable();
        if (operatorName == null || operatorName.equals("")) operatorName="none";
        else osmTags.put(NTD_ID_KEY, OperatorInfo.getNTDID());
        if (stopID == null || stopID.equals("")) stopID="none";
        if (stopName == null || stopName.equals("")) stopName="none";
        osmTags.put("highway", "bus_stop");
        osmTags.put(GTFS_STOP_ID_KEY, stopID);
        osmTags.put(GTFS_OPERATOR_KEY, operatorName);
        this.stopName = stopName;
        this.lat = lat;
        this.lon = lon;
    }

    public Stop(Stop s) {
        this.osmTags = new Hashtable();
        this.osmTags.putAll(s.osmTags);
        this.stopName = s.getStopName();
        this.osmTags.put("highway", "bus_stop");
        this.osmTags.put(GTFS_STOP_ID_KEY, s.getStopID());
        this.osmTags.put(GTFS_OPERATOR_KEY, s.getOperatorName());
        if (!s.getOperatorName().equals("none")) osmTags.put(NTD_ID_KEY, OperatorInfo.getNTDID());
        this.lat = s.lat;
        this.lon = s.lon;
    }

    public String getStopID(){
        return (String)osmTags.get(GTFS_STOP_ID_KEY);
    }

    public String getOperatorName(){
        return (String)osmTags.get(GTFS_OPERATOR_KEY);
    }

    public String getStopName(){
        return stopName;
    }

    public String getLat(){
        return lat;
    }

    public String getLon(){
        return lon;
    }

    public boolean compareOperatorName(Stop o) {
        if ((!this.getOperatorName().equals("none")) && (!o.getOperatorName().equals("none"))) {
            return OperatorInfo.isTheSameOperator(this.getOperatorName())
                    && OperatorInfo.isTheSameOperator(o.getOperatorName());
        }
        else if ((this.getOperatorName().equals("none")) && (o.getOperatorName().equals("none"))) {
            return true;
        }
        return false;
    }
    
    public int compareTo(Object o){
        Stop s = (Stop) o;
        double distance = OsmDistance.distVincenty(this.getLat(), this.getLon(),
                            s.getLat(), s.getLon());
/*        boolean t1 = s.getStopID().equals(this.getStopID());
        boolean t2 = this.compareOperatorName(s);
        boolean t3 = s.getStopName().equals(this.getStopName());*/
        if (!(this.getStopID().equals("none")) && !(this.getStopID().equals("missing")) 
                && (!(s.getStopID().equals("none"))) && (!(s.getStopID().equals("missing")))
                && (!this.getOperatorName().equals("none")) && (!s.getOperatorName().equals("none"))
                && (!this.getOperatorName().equals("missing")) && (!s.getOperatorName().equals("missing"))) {
            if ((s.getStopID().equals(this.getStopID())) && (this.compareOperatorName(s))) {
                return 0;
            }
        }
        else {
            if ((s.getStopID().equals(this.getStopID())) && (this.compareOperatorName(s)) && (distance < ERROR_TO_ZERO)) {
                return 0;
            }
        }
        return 1;
    }

    @Override
    public boolean equals(Object o){
        if (o instanceof Stop) {
            if (this.compareTo((Stop) o)==0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode(){
        String id = this.getStopID();
        return id.hashCode();
    }

    public String printOSMStop(){
        String temp="";
        Stop st = new Stop(this);
        if (st.getTag("OSM_NODE_ID")!=null) {
            temp = temp+"node_id:"+st.getTag("OSM_NODE_ID")+";name:"+st.getStopName()+";lat:"+
                    st.getLat()+";lon:"+st.getLon();
        }
        else {
            temp = temp+"name:"+st.getStopName()+";lat:"+
                    st.getLat()+";lon:"+st.getLon();
        }
        st.removeTag("OSM_NODE_ID");
        st.removeTag("REPORT");
        st.removeTag("version");
        st.removeTag("REPORT_CATEGORY");
        HashSet<String> keys = st.keySet();
        Iterator it = keys.iterator();
        while (it.hasNext()){
            String k = (String)it.next();
            temp = temp+";"+k+":"+st.getTag(k);
        }
        return temp;
    }
}