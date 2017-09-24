/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package infostetic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Javi
 */
interface ObtenerModelo 
{
    abstract public DefaultTableModel selectModelo(String table, String fields, String where, String[] columNames);    
}

class SelectModelo implements ObtenerModelo
{
    public DefaultTableModel selectModelo(String table, String fields, String where, String[] columNames)
    {    
        Conexion conexion = new Conexion();
        Connection conn = conexion.getConnection();
        DefaultTableModel modelo;
        
        int registros = 0;      
        String colname[] = fields.split(",");

        //Consultas SQL
        String q ="SELECT " + fields + " FROM " + table;
        String q2 = "SELECT count(*) as total FROM " + table;

        if(where!=null)
        {
            q = q + where;
            q2 = q2 + where;
        }

         try
         {
              PreparedStatement pstm = conn.prepareStatement(q2);
              ResultSet res = pstm.executeQuery();
              res.next();
              registros = res.getInt("total");
              res.close();
        }
         catch(SQLException e)
         {
              System.out.println(e);
        }

      //se crea una matriz con tantas filas y columnas que necesite
      Object[][] data = new String[registros][fields.split(",").length];
      //realizamos la consulta sql y llenamos los datos en la matriz "Object"
      try
      {
           PreparedStatement pstm = conn.prepareStatement(q);
           ResultSet res = pstm.executeQuery();

           int i = 0;         


           while(res.next())
           {                              
                for(int j = 0; j <= fields.split(",").length - 1; j++)
                {
                    data[i][j] = res.getString( colname[j].trim());
                }
                i++;         

           }

           res.close();
      }
      catch(SQLException e)
      {
           System.out.println(e);
      }
      //return data;    
      
        if( data.length > 0) 
        {
            // se colocan los datos en la tabla
            modelo = new DefaultTableModel(data, columNames){
                @Override
                public boolean isCellEditable(int fila, int columna) 
                {
                        return false;
                }
            };       
                       
        }  
        else 
        {
            modelo = new DefaultTableModel(null,columNames){
                @Override
                public boolean isCellEditable(int fila, int columna) {
                        return false;
                }
            };             
        }
        
        return modelo;      
    }    
}

