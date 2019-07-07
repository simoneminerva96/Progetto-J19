package persistence;

import application.Critique;
import application.CritiqueSections;
import application.MenuEntry;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;

public class CritiquesMapper extends AbstractPersistenceMapper {
    private HashSet<Critique> critiques;
    private MenuEntryMapper menuEntryMapper;
    private String tableDishCritique;

    public CritiquesMapper(MenuEntryMapper menuEntryMapper) throws SQLException {
        super("critiques");
        this.critiques = new HashSet<>();
        this.menuEntryMapper = menuEntryMapper;
        this.tableDishCritique = "critique_dish";
        setUp();
    }

    @Override
    protected Object getObjectFromTable(String OID) throws SQLException {
        return null;
    }

    @Override
    protected Object getObjectFromCache(String OID) {
        return null;
    }

    @Override
    protected void updateCache(String OID, Object obj) {
        this.critiques.add((Critique)obj);
    }

    @Override
    public void put(String OID, Object obj) {

    }

    public void setUp() {
        try {
            Statement stm = super.conn.createStatement();
            ResultSet rs = stm.executeQuery("select * from "+super.tableName);
            while (rs.next()){
                Critique tmpCrit = createCritique(rs);
                tmpCrit.setComment(rs.getString(9));
                tmpCrit = voteDishes(rs, tmpCrit);
                updateCache(rs.getString(1),tmpCrit);
            }
        }
        catch (SQLException e){
            e.printStackTrace();
            System.out.println("SQLexception: " + e.getMessage());
        }
    }

    /**
     * Method used to create a critique with the data of the DBMS
     *
     * @param rs, the ResultSet used to take the information given by the SQL query
     * @return tmpCrit, the new critique which is ready to be insert in the critique cache
     * @throws SQLException
     */
    private Critique createCritique(ResultSet rs) throws SQLException {
        Critique tmpCrit = new Critique(rs.getString(3), rs.getInt(2),rs.getInt(1));
        tmpCrit = calculateGradeCritique(rs, tmpCrit);
        return tmpCrit;
    }

    /**
     * Method which inserts the grade in a critique during its creation
     *
     * @param rs, the ResultSet used to take the information given by the SQL query
     * @param tmpCrit, the critique which is in creation
     * @throws SQLException
     */
    private Critique calculateGradeCritique(ResultSet rs, Critique tmpCrit) throws SQLException{
        double [] grades = new double[CritiqueSections.values().length-1];
        for (int i = 0; i< CritiqueSections.values().length - 1; i++){
            grades[i] = rs.getDouble(i+4);
        }
        tmpCrit.writeVotes(grades);

        return tmpCrit;
    }

    /**
     * Method which set the grades of the dish in a critique
     *
     * @param rs, the ResultSet used to take the information given by the SQL query
     * @throws SQLException
     */
    private Critique voteDishes(ResultSet rs, Critique tmpCrit) throws  SQLException{
        HashMap<MenuEntry, Double> gradeDish = new HashMap<>();
        PreparedStatement pstm = conn.prepareStatement("SELECT DISH_CODE,VOTO_DISH FROM "+tableDishCritique+" WHERE CRITIQUE_CODE = ?" );
        pstm.setInt(1, rs.getInt(1));
        ResultSet rsDish = pstm.executeQuery();
        while (rsDish.next()){
            gradeDish.put((MenuEntry) menuEntryMapper.get(Integer.toString(rsDish.getInt(1))),
                            Double.parseDouble(rsDish.getString(2)));
            //System.out.println(Integer.toString(rsDish.getInt(1))+"  voto: "+rsDish.getString(2));
        }
        //System.out.println(gradeDish);
        tmpCrit.voteDishes(gradeDish);
        return tmpCrit;
    }
}
