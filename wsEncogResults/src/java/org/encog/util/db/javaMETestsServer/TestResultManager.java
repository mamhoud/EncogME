
/**
Copyright 2010 Sean Barbeau

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
package org.encog.util.db.javaMETestsServer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This object connects to a database and dumps the test results to a table
 * @author Sean
 */
public class TestResultManager {

    Connection dbConnection;
    boolean useGlassfishPool = false;  //This varables is set to define whether the manager uses the Glassfish connection pool, or manages its own connections
    String userName = ""; //add your database username here
    String password = "";  //add your password for your database here
    String serverName = "";  //add your server name for your database here
    int portNumber = 0;  //Add your port number for your database here
    String databaseName = ""; //Add your database name here


    public TestResultManager(boolean useGlassfishPool) {
        this.useGlassfishPool = useGlassfishPool;

        try {
            if (!useGlassfishPool) {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                dbConnection = DriverManager.getConnection("jdbc:sqlserver://" + serverName + ":" + portNumber + ";databasename= " + databaseName + ";User= " + userName + ";Password= " + password + ";SelectMethod=direct;");
            } else {
                //Use Glassfish connection pool
                javax.naming.InitialContext ic  = new javax.naming.InitialContext();
                javax.sql.DataSource dataSource = (javax.sql.DataSource)ic.lookup("jdbc/_sqlserverdm");
                dbConnection = dataSource.getConnection();

            }
        } catch (Exception ex) {
            System.out.println("Error initializing SQL connection: " + ex);
        }

    }

    public void saveTestResults(TestResult[] results) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {


        PreparedStatement stmt = null;
        String sql;

        sql = "INSERT INTO tblANNXORBackPropTests" + "(Platform," + "NumEpochs," + "MinStopError," + "MathPOWSol," + "LearningRate," + "Momemtum," + "TrainingTime," + "FinalTrainError," + "Test00Actual," + "Test00Error," + "Test00Time," + "Test10Actual," + "Test10Error," + "Test10Time," + "Test01Actual," + "Test01Error," + "Test01Time," + "Test11Actual," + "Test11Error," + "Test11Time" + ") " + "VALUES " + "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        for (int i = 0; i < results.length; i++) {
            try {

                stmt = dbConnection.prepareStatement(sql);

                stmt.setInt(1, results[i].getPlatform());
                stmt.setInt(2, results[i].getNumEpochs());
                stmt.setDouble(3, results[i].getMinStopError());
                stmt.setInt(4, results[i].getMathPOWSolution());
                stmt.setDouble(5, results[i].getLearningRate());
                stmt.setDouble(6, results[i].getMomemtum());
                stmt.setLong(7, results[i].getTrainingTime());
                stmt.setDouble(8, results[i].getFinalTrainError());
                //Test00
                stmt.setDouble(9, results[i].getTest00Actual());
                stmt.setDouble(10, results[i].getTest00Error());
                stmt.setLong(11, results[i].getTest00Time());

                //Test10
                stmt.setDouble(12, results[i].getTest10Actual());
                stmt.setDouble(13, results[i].getTest10Error());
                stmt.setLong(14, results[i].getTest10Time());

                //Test01
                stmt.setDouble(15, results[i].getTest01Actual());
                stmt.setDouble(16, results[i].getTest01Error());
                stmt.setLong(17, results[i].getTest01Time());

                //Test11
                stmt.setDouble(18, results[i].getTest11Actual());
                stmt.setDouble(19, results[i].getTest11Error());
                stmt.setLong(20, results[i].getTest11Time());

                stmt.execute();
                System.out.println("Executed insert for testResult[" + i + "].");

            } catch (Exception e) {
                e.printStackTrace();
            }

            dbConnection.commit();
            stmt.close();

            //Deallocate all variables
            stmt = null;

        }
    }

    public void saveTestResults(TestResult result) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {


        PreparedStatement stmt = null;
        String sql;

        sql = "INSERT INTO tblANNXORBackPropTests" + "(Platform," + "NumEpochs," + "MinStopError," + "MathPOWSol," + "LearningRate," + "Momemtum," + "TrainingTime," + "FinalTrainError," + "Test00Actual," + "Test00Error," + "Test00Time," + "Test10Actual," + "Test10Error," + "Test10Time," + "Test01Actual," + "Test01Error," + "Test01Time," + "Test11Actual," + "Test11Error," + "Test11Time" + ") " + "VALUES " + "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        try {

            stmt = dbConnection.prepareStatement(sql);

            stmt.setInt(1, result.getPlatform());
            stmt.setInt(2, result.getNumEpochs());
            stmt.setDouble(3, result.getMinStopError());
            stmt.setInt(4, result.getMathPOWSolution());
            stmt.setDouble(5, result.getLearningRate());
            stmt.setDouble(6, result.getMomemtum());
            stmt.setLong(7, result.getTrainingTime());
            stmt.setDouble(8, result.getFinalTrainError());
            //Test00
            stmt.setDouble(9, result.getTest00Actual());
            stmt.setDouble(10, result.getTest00Error());
            stmt.setLong(11, result.getTest00Time());

            //Test10
            stmt.setDouble(12, result.getTest10Actual());
            stmt.setDouble(13, result.getTest10Error());
            stmt.setLong(14, result.getTest10Time());

            //Test01
            stmt.setDouble(15, result.getTest01Actual());
            stmt.setDouble(16, result.getTest01Error());
            stmt.setLong(17, result.getTest01Time());

            //Test11
            stmt.setDouble(18, result.getTest11Actual());
            stmt.setDouble(19, result.getTest11Error());
            stmt.setLong(20, result.getTest11Time());

            stmt.execute();
            System.out.println("Executed insert for test result.");

        } catch (Exception e) {
            e.printStackTrace();
        }

        dbConnection.commit();
        stmt.close();

        //Deallocate all variables
        stmt = null;

    }

    public String getModel(int id){
    //  travis

        String sql;
        String xmlModel = null;
        Statement stmt = null;

        sql = "SELECT id, strXmlModel FROM nnModel WHERE id=" + id;

        try {
            stmt = dbConnection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            rs.next();  //  move to first record
            xmlModel = rs.getString("strXmlModel");
//            System.out.println ("xml model: ");
//            System.out.println (s);
            rs.close();
            stmt.close();
//            System.out.println ("end xml model");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Deallocate all variables
        stmt = null;

        return xmlModel;
    }
}
