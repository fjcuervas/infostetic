
package infostetic;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
//import net.sf.jasperreports.engine.util.JRStyledTextParser;

public class Conexion 
{
       /* DATOS PARA LA CONEXION */
  private String bd = "db_infostetic";
  private String login = "root";
  private String password = "toor";

  private String url = "jdbc:mysql://localhost/"+bd;
  private Connection conn = null;

  String accion = "";
  DefaultTableModel modelo;
  String sSQL="";
  
  Boolean fromHistorico = false;
  
  public Conexion()
{

            try
            {    
               //obtenemos el driver de para mysql
               Class.forName("com.mysql.jdbc.Driver");
               //obtenemos la conexión
               conn = DriverManager.getConnection(url,login,password);
               
               if (conn!=null)
               {
                  System.out.println("OK base de datos "+bd+" listo");
               }

            }catch(SQLException e)
            {
               System.out.println(e);
            }
            catch(ClassNotFoundException e)
            {
               System.out.println(e);
            }     
        /*    sSQL="SET SESSION autocommit = 0";
                          //preparo la sentencia SQL
            PreparedStatement pstm = conn.prepareStatement(sSQL);
                          //Almaceno el resultado de la ejecución de la sentencia SQL
            pstm.executeQuery(sSQL);      
        } catch (SQLException ex) {
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
        }*/
  }
  public Connection getConnection()
   {
        return this.conn;
   }
  
  public void reporte(String[][] parametros)
  {
      try
      {
          //Obtiene la ruta donde se encuentra nuestro reporte
          String master = System.getProperty("user.dir") + 
                          "\\src\\reportes\\report1.jasper";
          
          System.out.println("master: " + master);
          //Realizar comparación de la ruta de reporte, si esta no es hallada          
          //nos mostrará un error
          if (master == null)
          {
              System.out.println("No se encuentra el archivo del reporte.");
              //System.exit(2);
          }

          JasperReport masterReport = null;
          try
          {
              masterReport = (JasperReport) JRLoader.loadObjectFromFile(master);

          }
          catch (JRException e)
          {
              System.out.println("Error al cargar el reporte: " + e.getMessage());
              //System.exit(3);
          }
          
          //Este es el parámetro, se pueden agregar más parámetros
          //gracias a parametro.put
          Map parameters = new HashMap();

          for (int i=0; parametros.length > i; i++)
          {          
              parameters.put(parametros[i][0], parametros[i][1]);
          }
          
          //Reporte diseñado y compilado con iReport
          JasperPrint jasperPrint;

          jasperPrint = JasperFillManager.fillReport(masterReport, parameters, conn);
          
          JasperViewer jviewer = new JasperViewer (jasperPrint, false);
          
          jviewer.setTitle("Histórico clientes"); //Le doy un título a la ventana                   
          jviewer.setSize(1228, 910); //configuro el tamaño de la ventana (tamaño igual que la principal)
          jviewer.setLocationRelativeTo(null); //Lo centro en medio de la pantalla
          jviewer.setZoomRatio((float)0.55); //configuro el zoom de la página, para que salga entera (55%)
          jviewer.setVisible(true); //Lo hago visible

          
      }
          catch (Exception e)
          {
              System.out.println("Error al cargar el reporte: " + e.getMessage());
              //System.exit(3);
          }      
  }
  
  public DefaultTableModel selectModelo(String table, String fields, String where, String[] columNames)
  {            
        Connection conn = this.getConnection();
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
  
  public ResultSet selectResult(String table, String fields, String where)
  {            
      Connection conn = this.getConnection();
      ResultSet res = null;  
     // int registros = 0;      
      //String colname[] = fields.split(",");
 
      //Consultas SQL
      String q ="SELECT " + fields + " FROM " + table;
      String q2 = "SELECT count(*) as total FROM " + table;

      
    if(where!=null)
      {
          q = q + where;
          q2 = q2 + where;
      }
    System.out.println("SELECT RESULT: "+q);
/*
      try
      {
           PreparedStatement pstm = conn.prepareStatement(q2);
           res = pstm.executeQuery();
           res.next();
           registros = res.getInt("total");
           res.close();
      }
      catch(SQLException e)
      {
           System.out.println(e);
      }
*/
      try
      {
           PreparedStatement pstm = conn.prepareStatement(q);
           res = pstm.executeQuery();
      }
      catch(SQLException e)
      {
           System.out.println(e);
      } 
      
      return res;
  } 
 
  //método para encontrar un producto en la tabla al hacer clic con el ratón en la tabla
  public ResultSet buscarRegistro (JTable tabla)
  {
    int filasel;
    String valor, sSQL;
    ResultSet rs = null;

    try
    {
            //almaceno la fila seleccionada en la tabla
            filasel = tabla.getSelectedRow();
           // System.out.println("filas: "+(int[])tabla.getSelectedRows());
           // System.out.println("filas: "+(int[])tabla.getSelectedRows());
            //si no se ha seleccionado ninguna fila getSelectedRow() devuelve -1
            if(filasel == -1)
            {
                JOptionPane.showMessageDialog(null, "No se ha seleccionado ninguna fila");
            }
            else
            {
                accion = "Modificar";
                //almaceno el modelo de tabla que se está utilizando en la tabla real
                modelo = (DefaultTableModel) tabla.getModel();
                //almaceno el valor de la columna 0 de la fila seleccionada 
                //dependiendo de si se quiere hacer una búsqueda desde el txtBuscarProducto o desde la tabla
                valor = (String) modelo.getValueAt(filasel, 0);
                //habilitar();
                
                //realizo la consulta para localizar el producto seleccionado
                sSQL = "SELECT id, codigo, producto, proveedor, pvp, cantidad, fecha, identificador FROM productos WHERE id="+valor;
                //Abro una nueva conexión con la base de datos        
                //Conexion cn = new Conexion();  
                //conn=cn.getConnection();
                
                try
                {
                    //preparo la sentencia SQL
                    PreparedStatement pstm = conn.prepareStatement(sSQL);
                    //Almaceno el resultado de la ejecución de la sentencia SQL
                    rs = pstm.executeQuery(sSQL);
                }
                catch (SQLException ex)
                {
                    JOptionPane.showMessageDialog(null, ex);
                }
            }

        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(null, e);
        }
        return rs;
        
    }   
  
    public ResultSet buscarRegistro (String tabla, String campos, String where, String ordenar)
    {
        String sSQL;
        ResultSet rs = null;

        try
        {
                if (ordenar==null && where!=null)
                {
                    sSQL = "SELECT "+campos+" FROM "+tabla+" "+where;
                }
                else if (where==null && ordenar!=null)
                {
                     //realizo la consulta para localizar el producto seleccionado
                     sSQL = "SELECT "+campos+" FROM "+tabla+" "+ordenar;                     
                }
                else if (where==null && ordenar==null)
                {
                     sSQL = "SELECT "+campos+" FROM "+tabla;                     
                }                
                else
                {                   
                    sSQL = "SELECT "+campos+" FROM "+tabla+" "+where+ordenar;
                    //System.out.println("sSQL4: "+sSQL);
                }

                //Abro una nueva conexión con la base de datos        
                //Conexion cn = new Conexion();  
                //conn=cn.getConnection();
                System.out.println("sql: "+ sSQL);
                try
                {
                    //preparo la sentencia SQL
                    PreparedStatement pstm = conn.prepareStatement(sSQL);
                    //Almaceno el resultado de la ejecución de la sentencia SQL
                    rs = pstm.executeQuery(sSQL);
                }
                catch (SQLException ex)
                {
                    JOptionPane.showMessageDialog(null, ex);
                }
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(null, e);
        }
        return rs;
        
    }
    
    public Object [][] select(String table, String fields, String where)
    {
      int registros = 0;      
      String colname[] = fields.split(",");

      //Consultas SQL
      String q ="SELECT " + fields + " FROM " + table;
      String q2 = "SELECT count(*) as total FROM " + table;
      
      if(where!=null)
      {
          q = q + where;
          q2 = q2 + where;
          /*q+= " WHERE CONCAT ("+fields+") LIKE '%"+where+"%'";
          q2+= " WHERE " + where;*/
      }
//System.out.println(q);
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
    return data;
 }
/*
          BigDecimal totalGastado = new BigDecimal (0);
         
         //Si vengo de getHistorico() sumo el total gastado del cliente
         //y el débito que tiene, para colocarlos en los textField de FichaCliente
         if (fromHistorico)
         {
            while(res.next())
            {              
                if (res.getString("forma_pago").equals("DEBITO") == false && 
                    res.getString("producto").equals("DEBITO") == false)
                {
                      totalGastado = totalGastado.add(BigDecimal.valueOf(Double.parseDouble(res.getString("total"))));
                      FichaCliente.txtTotalGastado.setText(totalGastado.toString());
                }                                  
            }
            fromHistorico = false;
            res.beforeFirst();
         }
 */
/* METODO PARA INSERTAR UN REGISTRO EN LA BASE DE DATOS
 * INPUT:
	table = Nombre de la tabla
	fields = String con los nombres de los campos donde insertar Ej.: campo1,campo2campo_n
	values = String con los datos de los campos a insertar Ej.: valor1, valor2, valor_n
*/

   public boolean insert(String table, String fields, String values)
   {
        boolean res=false;
        //Se arma la consulta
        String q =" INSERT INTO " + table + " ( " + fields + " ) VALUES ( " + values + " ) ";
System.out.println("INSERT: "+ q);
        //se ejecuta la consulta
        try 
        {
            PreparedStatement pstm = conn.prepareStatement(q);
            pstm.execute();
            pstm.close();
            res=true;
        }
        catch(SQLException e)
        {
            System.out.println(e);
            if (e.getErrorCode()==1062)
            {
                JOptionPane.showMessageDialog(null, "Ya existe un registro con el mismo nombre o código",
                                "Inserción", JOptionPane.WARNING_MESSAGE);
            } 
            else if (e.getErrorCode()==1364)
            {
                JOptionPane.showMessageDialog(null, "Debe completar los campos obligatorios",
                                "Inserción", JOptionPane.WARNING_MESSAGE);
            }
            
        }
        return res;
    }

    public void update (String tabla,String valorYcolumna, String condicion){
         String u="UPDATE " + tabla + 
                 " SET " + valorYcolumna + 
                 " WHERE "+ condicion;

        //se ejecuta la consulta 
         System.out.println("sql UPDATE: "+u);
        try {
            PreparedStatement pstm = conn.prepareStatement(u);
            pstm.execute();
            pstm.close();
        }
       
        catch(SQLException e)
        {
            if (e.getErrorCode()==1062)
            {
                JOptionPane.showMessageDialog(null, "El código está duplicado, intente con otro.");
                
            }
                
            
        }
    }

    
    /**
     * 
     */
    public void desconectar(){
      conn = null;
      System.out.println("La conexion a la  base de datos "+bd+" a terminado. PROBLEM?");
   }
    
   public void eliminar(String tabla,String condicion){
       String d=" DELETE FROM "+tabla+ 
                 " where "+ condicion;
       System.out.println("DELETE: "+ d);
        //se ejecuta la consulta
        try {
            PreparedStatement pstm = conn.prepareStatement(d);
            //pstm.executeUpdate();
            pstm.execute();
            pstm.close();
         }catch(SQLException e){
         System.out.println(e);
      }
   }

       public Object[][] getClientes()
    {
        Object[][] res = this.select("clientes","dni_cliente, nombre_cliente, Apellidos_cliente",null);
        if( res.length > 0)
            return res;
        else
            return null;
    }
   /* static class DateRenderer extends DefaultTableCellRenderer {
    DateFormat formatter;
    public DateRenderer() { super(); }
    public void setValue(Object value) {
        if (formatter==null) {
            formatter = DateFormat.getDateInstance();
        }
        setText((value == null) ? "" : formatter.format(value));
    }
    }*/
       
    public DefaultTableModel getProductos(String where)
    {
        Object[][] res = this.select("productos","codigo, producto, cantidad, pvp, proveedor, fecha, identificador, id", where);
   
        String[] columNames = {"codigo", "producto", "cantidad", "pvp", "proveedor", "fecha", "identificador",  "id"};        
           
        if( res.length > 0) 
        {
            // se colocan los datos en la tabla
            modelo = new DefaultTableModel(res,columNames){

                @Override
                public boolean isCellEditable(int fila, int columna) {
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
    
    public DefaultTableModel getClientes(String where)
    {
        Object[][] res = this.select("clientes","dni, nombre, apellidos, telefono_fijo, " +
                                     "telefono_movil, email, id, direccion, codigo_postal, poblacion, provincia, " +
                                     "fecha_nacimiento, fecha_alta, observaciones", where);
   
        String[] columNames = new String[] {
                                            "dni", "nombre", "apellidos", "telefono_fijo", "telefono_movil",
                                            "email", "id", "direccion", "codigo_postal", "poblacion", "provincia",                                              
                                            "fecha_nacimiento", "fecha_alta",  "observaciones"                                            
                                           };    
                 
                                  
        if( res.length > 0) 
        {
            // se colocan los datos en la tabla
            modelo = new DefaultTableModel(res,columNames){
                @Override
                public boolean isCellEditable(int fila, int columna) {
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
    
    public DefaultTableModel getHistorico(String where)
    {
        fromHistorico = true;
        Object[][] res = this.select("historico","id, producto, cantidad, precio, total,"
                                   + "forma_pago, metalico, tarjeta, fecha, observaciones", where);
   
        String[] columNames = new String[] {
                                            "id", "producto", "cantidad", "precio", "total",
                                             "forma_pago", "metalico", "tarjeta", "fecha", "observaciones"
                                           };    
                 
                                  
        if( res.length > 0) 
        {
            // se colocan los datos en la tabla
            modelo = new DefaultTableModel(res,columNames){
                @Override
                public boolean isCellEditable(int fila, int columna) {
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
    
    
    public DefaultTableModel getProductosMini()
    {
        Object[][] res = this.select("productos","id, codigo, cantidad, producto, pvp",null);
   
        String[] columNames = {"Id", "Código", "Cantidad", "Producto", "Precio"};        
           
        // se colocan los datos en la tabla
        modelo = new DefaultTableModel(res,columNames){
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };;                          
        if( res.length > 0) 
        {
            
            return modelo;
        }  
        else {
            return null;
        }
    } 
    
    
    public Object[][] buscarProducto(String id)
    {
        Object[][] res = this.select("productos","id, codigo, producto, proveedor, "
                + "pvp, cantidad, fecha, identificador","id='"+id+"'");
        if( res.length > 0) 
        {
            return res;
        }
        else 
        {
            return null;
        }
    }     

   
}
