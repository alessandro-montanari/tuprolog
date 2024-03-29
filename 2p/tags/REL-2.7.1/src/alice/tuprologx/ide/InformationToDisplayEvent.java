package alice.tuprologx.ide;

import alice.tuprolog.Prolog;
import alice.tuprolog.SolveInfo;
import alice.tuprolog.event.PrologEvent;
import alice.tuprolog.event.QueryEvent;

import java.util.ArrayList;

/**
 * This class represents events concerning information to display in the console.
 * 
 * 
 *
 */
@SuppressWarnings("serial")
public class InformationToDisplayEvent extends PrologEvent {

    private ArrayList<QueryEvent> queryEventList;
    private int solveType;

    public InformationToDisplayEvent(Prolog source, ArrayList<QueryEvent> queryEventList, int solveType){
        super(source);
        this.queryEventList=queryEventList;
        this.solveType=solveType;
    }
    
    public int getSolveType()
    {
        return solveType;
    }

    public QueryEvent[] getQueryResults()
    {
        return (QueryEvent[]) queryEventList.toArray(new QueryEvent[queryEventList.size()]);
    }

    public SolveInfo getQueryResult()
    {
        return ( (QueryEvent) queryEventList.get(0)).getSolveInfo();
    }

    public int getListSize()
    {
        return queryEventList.size();
    }
}
