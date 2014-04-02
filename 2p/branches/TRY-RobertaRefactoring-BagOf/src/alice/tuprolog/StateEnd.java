/*
 * tuProlog - Copyright (C) 2001-2002  aliCE team at deis.unibo.it
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package alice.tuprolog;

import java.util.*;

import alice.tuprolog.Struct;

/**
 * @author Alex Benini
 *
 * End state of demostration.
 */
public class StateEnd extends State {
    
    private int endState;    
    private Struct goal;
    private List<Var> vars;
    
    /**
     * Constructor
     * @param end Terminal state of computation
     */
    public StateEnd(EngineRunner c, int end) {
    	this.c=c;
        endState = end;
    }
    
    public int getResultDemo() {
        return endState;
    }
    
    public Struct getResultGoal() {
        return goal;
    }
    
    public List<Var> getResultVars() {
        return vars;
    }
    
    
    void doJob(Engine e) {
        vars = new ArrayList<Var>();
        goal = (Struct)e.startGoal.copyResult(e.goalVars,vars);
        System.out.println("STATE END: STAMPO LE VAR del GOAL****"+e.goalVars);
        System.out.println("STATE END: STATE END "+vars+ " GOAL "+goal);  
        
        if(this.endState==EngineRunner.TRUE || this.endState==EngineRunner.TRUE_CP)
        	relinkVar(e);      
    }

    private void relinkVar(Engine e){
	    if(c.getEngineMan().getRelinkVar()){ //devo fare il relink solo se ho var libere nella bagof E SE NON CI SONO ESISTENZE
	   
	    	/*
	    	 * STEP1: dalla struttura risultato bagof (bag = (c.getEngineMan()).getBagOFres())
	    	 * estraggo la lista di tutte le variabili
	    	 * memorizzate nell'ArrayList<String> lSolVar
	    	 * lSolVar = [H_e2301, H_e2302, H_e2303, H_e2304, H_e2305, H_e2306, H_e2307, H_e2308]
	    	 */
	    	System.out.println("STATE END relinkvar(): Le var del risultato sono "+(c.getEngineMan()).getBagOFres());     	
	    	ArrayList<Term> bag = (c.getEngineMan()).getBagOFres();
	    	ArrayList<String> lSolVar=new ArrayList<String>() ;
	    	//System.out.println("Le var del risultato sono BAG "+bag); 
	    	/*NB lSolVar ha lunghezza multipla di lGoal var, se ho più soluzioni si ripete 
	    	 * servirebbe esempio con 2 bag */
	    	for (int i=0; i<bag.size();i++) {
	    		Var resVar = (Var)bag.get(i);
	    		System.out.println("RESVAR BAG "+resVar); 
	    		Term t = resVar.getLink();
	    		System.out.println("RESVAR BAG LINK "+resVar);
	    		if(t!=null){
	    			if(t instanceof Struct){
	    				Struct t1 = ((Struct)t);
	    				System.out.println("RESVAR BAG LINK è STRUCT "+t1);
	    				//uso lista temporanea per aggiustare ordine, dalla struct con findvar escono al contrario
	    				ArrayList<String> l_temp=new ArrayList<String>();
	    				l_temp= findVar(t1,l_temp);
	    				for(int w=l_temp.size()-1; w>=0;w--){
	    					lSolVar.add(l_temp.get(w));
	    				}
	    			}
	    			else if(t instanceof Var){
	    				while(t!=null && t instanceof Var){
		    				resVar = (Var)t;
		    	    		System.out.println("---RESVAR BAG è VAR "+resVar); 
		    	    		t = resVar.getLink();
		    	    		System.out.println("---RESVAR BAG LINK "+resVar);	
	    				}
	    				lSolVar.add(((Var)resVar).getName());
	    				bag.set(i, resVar);
	    			}
	    		}
	    		else lSolVar.add(resVar.getName());		
	    	}
	    	System.out.println("le variabili nella sol sono lSolVar "+lSolVar);
	    	/*
	    	 * STEP2: dalla struttura goal bagof (goalBO = (Var)(c.getEngineMan()).getBagOFgoal())
	    	 * estraggo la lista di tutte le variabili
	    	 * memorizzate nell'ArrayList<String> lgoalBOVar
	    	 * lgoalBOVar = [Z_e0, X_e73, Y_e74, V_e59, WithRespectTo_e31, U_e588, V_e59, H_e562, X_e73, Y_e74, F_e900]
	    	 */
	    	//System.out.println("il goal interno bag of è "+(c.getEngineMan()).getBagOFgoal());
	    	Var goalBO = (Var)(c.getEngineMan()).getBagOFgoal();
	    	//System.out.println("il goal interno bag of è var con link "+goalBO.getLink());
	    	ArrayList<String> lgoalBOVar=new ArrayList<String>() ; 
	    	Term goalBOvalue = goalBO.getLink();
	    	if(goalBOvalue instanceof Struct){
	    		Struct t1 = ((Struct)goalBOvalue);
	    		ArrayList<String> l_temp=new ArrayList<String>();
	    		l_temp= findVar(t1,l_temp);
	    		for(int w=l_temp.size()-1; w>=0;w--){
	    			lgoalBOVar.add(l_temp.get(w));
	    		}
	    	}//esistono casi in cui il goal non sia STRUCT ????
	    	//System.out.println("le variabili nella goal della bagof sono lgoalBOVar "+lgoalBOVar);
	    	
	    	/*
	    	 * STEP3: prendere il set di variabili libere della bagof
	    	 * fare il match con le variabili del goal in modo da avere i nomi del goal esterno
	    	 * questo elenco ci servirà per eliminare le variabili in più che abbiamo in lgoalBOVar
	    	 * ovvero tutte le variabili associate al template
	    	 * lGoalVar [Y_e74, U_e588, V_e59, X_e73, Y_e74, U_e588, F_e900]
	    	 * mette quindi in lGoalVar le variabili che compaiono in goalVars e sono anche libere 
	    	 * per la bagof c.getEngineMan().getBagOFvarSet()
	    	 */
	    	//System.out.println("Le var della bagof sono "+c.getEngineMan().getBagOFvarSet());
	    	Var v=(Var)c.getEngineMan().getBagOFvarSet();
	    	Struct varList=(Struct)v.getLink(); //lista delle variabili nel goal bagof con nomi interni alla bagof
	    	ArrayList<String> lGoalVar=new ArrayList<String>() ; //lista delle variabili nel goal bagof con nomi goal
	    	//ArrayList<String> lGoalVar_copy=new ArrayList<String>() ; //????????mi serve la copia per sostituire le var sia nel goal originale che nel risultato
	    	//System.out.println("Lista variabili goal bagof nomi interni alla bagof varList "+varList);
	    	Object[] a=(e.goalVars).toArray();
	    	for (java.util.Iterator<? extends Term> it = varList.listIterator(); it.hasNext();) {
	    		Term var=it.next();
	    		for(int y=0; y<a.length;y++){
	    			Var vv=(Var)a[y];
	    			if(vv.getLink().isEqual(var)){
	    				lGoalVar.add(vv.getName());
	    			}
	    		}
	    	}
	    	//System.out.println("Lista variabili goal bagof nomi goal lGoalVar "+lGoalVar);
	    	
	    	/*
	    	 * STEP4: pulisco lgoalBOVar lasciando solo i nomi che compaiono effettivamente in 
	    	 * lGoalVar (che è la rappresentazione con nomi esterni delle variabili libere nel
	    	 * goal della bagof
	    	 */
	    	lgoalBOVar.retainAll(lGoalVar);
	    	//System.out.println("Lista variabili goal bagof nomi goal pulite da template lgoalBOVar "+lgoalBOVar);
	    	if(lGoalVar.size()>lgoalBOVar.size()){
	    		System.out.println("Entro nell'if ");
	    		for(int h=0; h<lGoalVar.size(); h++)
	    			if(h>=lgoalBOVar.size()){
	    				System.out.println("Aggiungo elemento ");
	    				lgoalBOVar.add(lGoalVar.get(h));
	    			}	
	    	}
	    	/*
	    	 * STEP5: sostituisco le variabili nel risultato (sia in goals che vars)
	    	 * a) cerco l'indice della variabile in lSolVar
	    	 * b) sostituisco con quella di stesso indice in lgoalBOVar
	    	 */
	    	String bagVarName = null;
	    	Var goalSolution=new Var();
	    	
	    	if(lSolVar.size()>0 && lgoalBOVar.size()>0 && !varList.isGround() &&!goalBO.isGround()){
		    	for (int i=0; i<bag.size();i++) {
		    		//System.out.println("BAG SIZE "+bag.size());
		    		Var resVar = (Var)bag.get(i);
		    		//System.out.println("SOSTITUZIONE VAR "+resVar);
		    		Term t = resVar.getLink();
		    		if(t==null){
		    			//System.out.println("----link null");
		    			t=resVar;
		    		}
		    		//System.out.println("----link NOT null"+t);
		    		//scorro le variabili del goal per vedere quale è il risultato e ne memorizzo il nome
		    		bagVarName = null;
		    		for(int y=0; y<a.length;y++){
		    			Var vv=(Var)a[y];
		    			Var vv_link=structValue(vv,i);
		    			//System.out.println("NOME di vv_link "+vv_link);
		    			if(vv_link.isEqual(t)){
		    				//System.out.println("NOME della BAG "+vv.getName());
		    				//System.out.println("NOME di vv "+vv);
		    				if(bagVarName==null){
		    					bagVarName=vv.getOriginalName();
		    					goalSolution=vv;
		    				}
		    				//sostituzione delle var nella Struct della sol
		    				System.out.println("Sostituisco vv_link "+vv_link.getLink());
		    				System.out.println("Sostituisco vv "+vv_link);
		    				if(vv_link.getLink()!=null){
		    					Struct s = substituteVar((Struct)vv_link.getLink(),lSolVar,lgoalBOVar);
		    					//System.out.println("****Nuovo link della var "+vv.getOriginalName()+" link "+s);	    	
		    				}
		    				else{
		    					int index = lSolVar.indexOf(resVar.getName());
		    					//System.out.println("Index i "+i);
		    					//come mai era lgoalBOVar ????
		    					setStructValue(vv,i,new Var(lgoalBOVar.get(index)));
		    					//System.out.println("****Nuovo link della var "+vv.getOriginalName()+" valore "+vv);
		    				}
		    			}
		   			}
		    		/*}
		    		else {
		    			System.out.println("----link null ");
		    			//scorro le variabili del goal per vedere quale è il risultato
		    			System.out.println("----RESVAR "+resVar);
		    			for(int y=0; y<a.length;y++){
		    				//System.out.println("----a[y] prima sostituzione "+a[y]);
		        			Var vv_link=this.structValue((Var)a[y],i);
		        			System.out.println("----vv_link "+vv_link+" ho estratto indice i "+resVar);
		        			if(vv_link.isEqual(resVar)){
		        				//System.out.println("----trovata uguaglianza set link di a[y] "+lGoalVar.get(0));
		        				//((Var)a[y]).setLink(new Var(lGoalVar.get(0)));
		        				
		        				int index = lSolVar.indexOf(resVar.getName());
		        				vars.set(y, new Var(lgoalBOVar.get(index)));
		        				//((Var)vars.get(y)).setLink(new Var(lgoalBOVar.get(index)));
		        			}
		        			//System.out.println("----a[y] dopo sostituzione "+a[y]);
		        		}
		    		}*/
		    	}
		    	//System.out.println("La variabile da sostituire è "+bagVarName+" con valore "+goalSolution);
		    	for(int j=0; j<vars.size(); j++){
		    		Var vv=(Var)vars.get(j);
		    		if(vv.getOriginalName().equals(bagVarName)){
		    			Var solVar=varValue2(goalSolution);
		    			// qui sarebbe bello fare un set del nome
		    			solVar.setName(vv.getOriginalName());
		    			solVar.rename(0, 0);
		    			//System.out.println("Sol var "+solVar.getOriginalName()+" nome "+solVar.getName()+" con valore "+solVar.getLink());
		    			vars.set(j, solVar);
		    			break;
		    		}
		    	}
	    	}
	    }
	    
	    
	    
	    //System.out.println("----goal vars a[y] dopo sostituzione "+e.goalVars);
	    //System.out.println("----dopo sostituzione STATE END "+vars+ " GOAL "+goal);  
		    c.getEngineMan().setRelinkVar(false);
		    c.getEngineMan().setBagOFres(null);
		    c.getEngineMan().setBagOFgoal(null);
		    c.getEngineMan().setBagOFvarSet(null);
	  
	}
    
    public Var varValue (Var v){ 	
    	while(v.getLink()!=null){ 
    		//System.out.println("+++ VARVALUE cerco il valore v "+v+" link "+v.getLink());
    		if(v.getLink()instanceof Var)
    			v=(Var)v.getLink();
    		else if(v.getLink()instanceof Struct)
    			v=(Var)((Struct)v.getLink()).getArg(0);
    		else break;
    	}
    	return v;
    }
    public Var varValue2 (Var v){ 	
    	while(v.getLink()!=null){ 
    		//System.out.println("+++ VARVALUE cerco il valore v "+v+" link "+v.getLink());
    		if(v.getLink()instanceof Var)
    			v=(Var)v.getLink();
    		else break;
    	}
    	return v;
    }
    
    public Var structValue (Var v, int i){ 	
    	Struct s=new Struct();
    	Var vStruct = new Var();
    	while(v.getLink()!=null){ 
    		//System.out.println("*** cerco il valore v "+v+" link "+v.getLink());
    		if(v.getLink()instanceof Var){
    			//System.out.println("*** il link è var");
    			v=(Var)v.getLink();
    		}
    		else if(v.getLink()instanceof Struct){
    			s=((Struct)v.getLink());
    			//devo prendere l'i_esimo elemento della lista quindi scorro
    			//System.out.println("*** devo prendere l'i_esimo elemento della lista quindi scorro");
    			while(i>0){
    				if(s.getArg(1)instanceof Struct){
    					s=(Struct)s.getArg(1);
    				}else if(s.getArg(1)instanceof Var){
    					vStruct=((Var)s.getArg(1));
    					if(vStruct.getLink()!=null){
    						i--;
    						return structValue(vStruct,i);}
    					return vStruct;
    				}	
    				i--;
    			}
    			vStruct=((Var)s.getArg(0));
    			break;
    		}
    		else break;
    	}
    	//System.out.println("+++ ritorno "+vStruct);
    	return vStruct;
    }
    
    public void setStructValue (Var v, int i, Var v1){ 	
    	Struct s=new Struct();
    	while(v.getLink()!=null){ 
    		//System.out.println("+++ cerco il valore v "+v+" link "+v.getLink());
    		if(v.getLink()instanceof Var){
    			//System.out.println("+++ il link è var");
    			v=(Var)v.getLink();
    		}
    		else if(v.getLink()instanceof Struct){
    			s=((Struct)v.getLink());
    			//System.out.println("+++ s "+s);
    			//devo prendere l'i_esimo elemento della lista quindi scorro
    			//System.out.println("+++devo prendere l'i_esimo elemento della lista quindi scorro");
    			while(i>0){
    				//System.out.println("+++ s "+s.getArg(1));
    				if(s.getArg(1) instanceof Struct)
    					s=(Struct)s.getArg(1);
    				else if (s.getArg(1) instanceof Var){
    					v=(Var)s.getArg(1);
    					s=((Struct)v.getLink());
    				}
    				i--;
    			}
    			s.setArg(0, v1);
    			break;
    		}
    		else break;
    	}
    	//System.out.println("+++ ritorno "+vStruct);
    }

    
    public ArrayList<String> findVar (Struct s, ArrayList<String> l){ 
    	ArrayList<String> allVar=new ArrayList<String>();
    	if(s.getArity()>0){
			Term t = s.getArg(0);
			Term tt;
			if(s.getArity()>1) {
				tt=s.getArg(1);
				//System.out.println("---Termine "+t+" e termine "+tt);
				if(tt instanceof Var){
					allVar.add(((Var)tt).getName());
				}
				else if(tt instanceof Struct){
					ArrayList<String> l1 = findVar((Struct)tt,l);
					allVar.addAll(l1);
				}
			}
			if(t instanceof Var){
				allVar.add(((Var)t).getName());
			}
			else if(t instanceof Struct){
				ArrayList<String> l1 = findVar((Struct)t,l);
				allVar.addAll(l1);
			}
    	}
    	return allVar;
    }
    
    
    public Struct substituteVar (Struct s, ArrayList<String> lSol, ArrayList<String> lgoal){ 	
		Term t = s.getArg(0);
		//System.out.println("STATE END Substitute var ---Termine "+t);
		Term tt=null;
		if(s.getArity()>1)
			tt = s.getArg(1);
		//System.out.println("Substitute var ---Termine "+t+" e termine "+tt);
		if(tt!=null && tt instanceof Var){
			int index = lSol.indexOf(((Var) tt).getName());
			System.out.println("Substitute var ---Indice di tt in lSol "+index);
			s.setArg(1, new Var(lgoal.get(index)));
			if(t instanceof Var){
				int index1 = lSol.indexOf(((Var) t).getName());
				//System.out.println("Substitute var ---Indice di t in lSol "+index1);
				s.setArg(0, new Var(lgoal.get(index1)));
			}
			if(t instanceof Struct && ((Struct)t).getArity()>0){
				//System.out.println("Substitute var-------Trovata struct t "+t);
				//System.out.println("Substitute var-------Trovata struct t arity "+((Struct)t).getArity());
				Struct s1 = substituteVar((Struct)t,lSol,lgoal);
				//System.out.println("Substitute var ---t è struct ritorno s1 "+s1);
				s.setArg(0, s1);
			}
		}
		else{
			if(t instanceof Var){
				int index1 = lSol.indexOf(((Var) t).getName());
				//System.out.println("Substitute var ---Indice di t in lSol "+index1);
				s.setArg(0, new Var(lgoal.get(index1)));
			}
			if(t instanceof Struct){
				//System.out.println("Substitute var-------Trovata struct ");
				Struct s1 = substituteVar((Struct)t,lSol,lgoal);
				//System.out.println("Substitute var ---t è struct ritorno s1 "+s1);
				s.setArg(0, s1);
			}
		}
		//System.out.println("Substitute var ---t è nullo ritorno s "+s);
		return s;
    }

    
    public String toString() {
        switch(endState){
        case EngineRunner.FALSE  : return "FALSE";
        case EngineRunner.TRUE   : return "TRUE";
        case EngineRunner.TRUE_CP: return "TRUE_CP";
        default                   : return "HALT";
        }
    }
    
}