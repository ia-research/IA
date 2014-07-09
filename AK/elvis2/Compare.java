
package elvis2;

import java.util.Comparator;

public class Compare implements Comparator<RoomTime>{

    @Override
    public int compare(RoomTime o1, RoomTime o2) {
        return (int)(o1.getTime()-o2.getTime());
    }
    
}
