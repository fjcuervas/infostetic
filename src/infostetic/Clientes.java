/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package infostetic;

import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Javi
 */
public class Clientes extends javax.swing.JDialog {

    static DefaultTableModel modelo;
    static ButtonGroup btnGrupoBusc;
    ButtonGroup btnGrupoOrd;    
    static String ordenar = null;    
    String accion = "";
    static String where = null;
    static Conexion consulta;
    ResultSet rs;
    Boolean insercion = false;
    Boolean accesoCaja = false;
    
    public Clientes(java.awt.Frame parent, boolean modal) 
    {
        super(parent, modal);
        initComponents();   
        
        JScrollBar barra = new JScrollBar();

        setLocationRelativeTo(null);//centro la ventana en medio de la pantalla

        //deshabilito todos los botones de Productos menos "Nuevo"
        deshabilitarBotonesCliente();
        btnNuevoCliente.setEnabled(true);
        
        if (SelCliente.seleccionarCliente)
        {
            accion = "insertar";
            txtDni.requestFocusInWindow();
            btnGuardarCliente.setEnabled(true);
            btnCancelarCliente.setEnabled(true);
            btnNuevoCliente.setEnabled(false);
            habilitarTextFieldClientes();       
            tbClientes.setVisible(false);
            
            //Activo un flag para saber que vengo de SelCliente, 
            //pero hasta que no pulse Guardar, no mostraré la Caja.            
            accesoCaja = true;
            
            //Vuelvo a poner el flag a false para que desde el formulario
            //Principal no siga con las intrucciones de abrir Caja.
            SelCliente.seleccionarCliente = false;            
        }
        else
        {
            //deshabilito todos los campos de texto
            deshabilitarTextFieldClientes();    
            deshabilitarBotonesCliente();
            btnNuevoCliente.setEnabled(true);            
        }
               
        String[] columNames = new String[] {"id", "dni", "nombre", "apellidos", 
                                            "provincia", "fecha_alta", "fecha_nacimiento"};   
        
        //cargo el combo de tipos de columnas para buscar el producto
        for (int i=0; columNames.length > i; i++)
        {            
            cbBuscarCliente.addItem(columNames[i]);
        }
                
        cbBuscarCliente.setSelectedIndex(2);
        
        modelo = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                    return false;
            }
        }; 
        
        calendarioCliente.addPropertyChangeListener(new java.beans.PropertyChangeListener() 
        {
            public void propertyChange(java.beans.PropertyChangeEvent evt) 
            {
                calendarioClientePropertyChange(evt);
            }

            private void calendarioClientePropertyChange(PropertyChangeEvent evt) 
            {
                if (calendarioCliente.getDate() != null)
                {                
                        SimpleDateFormat dateformat;         
                        String fecha = null;

                        dateformat = new SimpleDateFormat("dd-MM-yyyy"); 
                        calendarioCliente.setDateFormatString("dd-MM-yyyy");  
                        
                        txtFecNac.setText(dateformat.format(calendarioCliente.getDate()));
                        
                        dateformat = new SimpleDateFormat("yyyy-MM-dd"); 
                        fecha = dateformat.format(calendarioCliente.getDate());                                                
                        try 
                        {                            
                            calendarioCliente.setDate(dateformat.parse(fecha));                            
                        } 
                        catch (ParseException ex) 
                        {
                            Logger.getLogger(Configuracion.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
         });
        
        //Llamo a este método para crear la tabla con el tamaño
        //de las columnas por defecto que se mostrarán. Cada vez que
        //se modifique la tabla (ordenar, filtrar, actualizar) llamo a crearTabla()
        crearTabla(" ORDER BY nombre ASC");       
        textFieldClienteBlanco(); 
        //deshabilitarTextFieldClientes();

    }


    public static void crearTabla (String where)
    {    

        consulta = new Conexion();
        modelo = consulta.getClientes(where);
        tbClientes.setModel(modelo); 
        
        tbClientes.getColumnModel().getColumn(0).setPreferredWidth(85);  //dni
        tbClientes.getColumnModel().getColumn(1).setPreferredWidth(150); //nombre
        tbClientes.getColumnModel().getColumn(2).setPreferredWidth(200); //apellidos
        tbClientes.getColumnModel().getColumn(3).setPreferredWidth(100); //telefono_fijo
        tbClientes.getColumnModel().getColumn(4).setPreferredWidth(100); //telefono_movil
        tbClientes.getColumnModel().getColumn(5).setPreferredWidth(200); //email
        
        for (int i=6; i < 14; i++)
        {
            tbClientes.getColumnModel().getColumn(i).setMaxWidth(0);
            tbClientes.getColumnModel().getColumn(i).setMinWidth(0);
            tbClientes.getColumnModel().getColumn(i).setPreferredWidth(0);               
        }
        /*
         * columna 0 : dni
         * columna 1 : nombre
         * columna 2 : apellidos
         * columna 3 : telefono_fijo
         * columna 4 : telefono_movil
         * columna 5 : email
         * columna 6 : id
         * columna 7 : direccion
         * columna 8 : codigo_postal
         * columna 9 : poblacion
         * columna 10 : provincia
         * columna 11 : fecha_nacimiento
         * columna 12 : fecha_alta
         * columna 13 : observaciones                 
         */               
    }
    
    //método para detectar el nombre del radiobutton que está seleccionado
    public static String botonSeleccionado(ButtonGroup buttonGroup) 
    {
        for (Enumeration<AbstractButton> buttons = buttonGroup.getElements(); buttons.hasMoreElements();) 
        {
            AbstractButton button = buttons.nextElement();

            if (button.isSelected()) 
            {
                return button.getText();
            }
        }
        return null;
    }   
    
    private void textFieldClienteBlanco()
    {
        txtCodCliente.setText("");
        txtNomCliente.setText("");
        txtApeCliente.setText("");
        txtDni.setText("");
        txtDireccion.setText("");
        txtCodPostal.setText("");
        txtPoblacion.setText("");   
        txtProvincia.setText("");
        txtTelfnFijo.setText("");
        txtTelfnMovil.setText("");
        txtFecNac.setText("");
        txtFecAlta.setText("");   
        txtEmail.setText("");
        txtObservaciones.setText("");  
        calendarioCliente.setDate(null);
        calendarioAlta.setDate(null);
    }
    
    private String[] recogerDatosClientes()
    {
        String valores="", campos="";        
        String[][] datoCli = new String[13][2];
        String[] resultado = new String[2];     
        SimpleDateFormat dateformat;
        Date fecha = null;
        //Calendar ahora = Calendar.getInstance();       
                
        datoCli [0][0]="dni";
        datoCli [1][0]="nombre";
        datoCli [2][0]="apellidos";
        datoCli [3][0]="direccion";
        datoCli [4][0]="codigo_postal";
        datoCli [5][0]="poblacion";
        datoCli [6][0]="provincia";
        datoCli [7][0]="telefono_fijo";
        datoCli [8][0]="telefono_movil";
        datoCli [9][0]="fecha_nacimiento";
        datoCli [10][0]="fecha_alta";
        datoCli [11][0]="email";        
        datoCli [12][0]="observaciones";                      
        
        datoCli [0][1]=txtDni.getText();
        datoCli [1][1]=txtNomCliente.getText();
        datoCli [2][1]=txtApeCliente.getText();
        datoCli [3][1]=txtDireccion.getText();
        datoCli [4][1]=txtCodPostal.getText();
        datoCli [5][1]=txtPoblacion.getText();
        datoCli [6][1]=txtProvincia.getText();
        datoCli [7][1]=txtTelfnFijo.getText();
        datoCli [8][1]=txtTelfnMovil.getText();
        
        if (txtFecNac.getText().equals("") == false)
        {                    
                    try 
                    {
                        dateformat = new SimpleDateFormat("dd-MM-yyyy"); 
                        fecha = dateformat.parse(txtFecNac.getText());
                    } 
                    catch (ParseException ex) 
                    {
                        Logger.getLogger(Clientes.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    calendarioCliente.setDate(fecha);
                    dateformat = new SimpleDateFormat("yyyy-MM-dd");                     
                    datoCli [9][1] = dateformat.format(calendarioCliente.getDate());
                    
          }
          else
          {
              calendarioCliente.setDate(null);
              datoCli [9][1] = txtFecNac.getText();               
          }
                
        if (accion.equals("insertar"))
        {
            //Hacer formateador de la fecha, se le pasa el formato en que se quiere obtener la fecha.
            dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
            datoCli [10][1]= dateformat.format(new Date());        
        }
        else
        {
            if (txtFecAlta.getText().equals("") == false)
            {             
                    try 
                    {
                        dateformat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss"); 
                        fecha = dateformat.parse(txtFecAlta.getText());
                    } 
                    catch (ParseException ex) 
                    {
                        Logger.getLogger(Clientes.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    calendarioAlta.setDate(fecha);
                    dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
                    System.out.println("fecha 1: "+dateformat.format(calendarioAlta.getDate()));
                    datoCli [10][1] = dateformat.format(calendarioAlta.getDate());
            }
           else
           {
              calendarioAlta.setDate(null);
              datoCli [10][1] = txtFecAlta.getText();               
           }            
                    
            //datoCli [10][1] = txtFecAlta.getText();
        }
        datoCli [11][1]=txtEmail.getText();
        datoCli [12][1]=txtObservaciones.getText();              
        
        
        if (accion.equals("insertar"))
        {             
                //datoCli [10][1] = fecha;
                for(int j=0; j < datoCli.length; j++)
                {
                    if(datoCli[j][1].equals(""))
                    {                
                        datoCli[j][1] = null;
                        valores = valores + "," + datoCli[j][1];
                    }
                    else
                    {
                        valores = valores + ",'" + datoCli[j][1] + "'";
                    }                        
                    campos = campos + ", " + datoCli[j][0];  
                }
                //elimino la ',' del principio, indicando que empiece desde el carácter 1
                valores=valores.substring(1); 
                campos=campos.substring(1); 
                resultado[0]=campos;
                resultado[1]=valores;                                
        }
        else if (accion.equals("modificar"))
        {
            for(int j=0; j < datoCli.length; j++)
            {
                //Si hay algún campo vacío, le asigno null para que de este modo, 
                //si se decide borrar algún dato como pj el teléfono, se actualice
                //correctamente en la base de datos, ya que de otra forma no se actualiza.
                if(datoCli[j][1].equals(""))
                {                
                     datoCli[j][1]=null;
                     valores = valores + datoCli[j][0] + "=" + datoCli[j][1] + ", ";
                }
                else
                {
                     valores = valores + datoCli[j][0] + "='" + datoCli[j][1] + "', ";
                }
            }        
            //elimino la ',' del final, indicando desde y hasta donde copiar la cadena
            valores = valores.substring(0, valores.length() - 2); 

            resultado[0] = "";
            resultado[1] = valores;
        }                
        return resultado;
    }
    
/** Valida si el parámetro es una fecha con el formato "dd/MM/yyyy".
  * @return true si cumple el formato, false en caso contrario.
  */
private static boolean isFechaValida(String fechax) 
{
  try 
  {
      SimpleDateFormat formatoFecha = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
      formatoFecha.setLenient(false);
      formatoFecha.parse(fechax);
  } 
  catch (ParseException e) 
  {
      return false;
  }
  return true;
}

    //compruebo que las cajas de texto no estén todas en blanco. A veces cuando entro en clientes
    //y selecciono muy rápido una fila en la tabla, no se cargan los datos del registro en las cajas de texto
    //entonces tengo que controlarlo para no guardar o eliminar un producto incorrectamente
    private boolean comprobarTextFieldCliente()
    {               
        if (txtNomCliente.getText().equals(""))
        {
             JOptionPane.showMessageDialog(null, "El campo 'Nombre' es obligatorio.",
                          "Acción cliente", JOptionPane.INFORMATION_MESSAGE);     
             
             txtNomCliente.requestFocusInWindow();
        
            return true;
        }
        else if (txtApeCliente.getText().equals(""))
        {
             JOptionPane.showMessageDialog(null, "El campo 'Apellido' es obligatorio.",
                          "Acción cliente", JOptionPane.INFORMATION_MESSAGE);     
             
             txtApeCliente.requestFocusInWindow();
        
            return true;
        }
        //Voy a comprobar el formato de la fecha de nacimiento
        //en el caso de haberla introducido. Sino no compruebo.
        else if (txtFecNac.getText().equals("") == false)
        {          
            //si la fecha no es válida, muestro un mensaje y no continúo
            if (isFechaValida(txtFecNac.getText()) == false)
            {
                JOptionPane.showMessageDialog(null, "El formato de fecha no es válido. "
                       + "El formato correcto es: DD-MM-AAAA (Ej: 15-01-1982)",
                       "Acción cliente", JOptionPane.INFORMATION_MESSAGE);
                
                txtFecNac.requestFocusInWindow();
                
                return true;
            }
        }
        //si la fecha es válida, o simplemente se dejó en blanco, continúo
        else
        { 
            return false;
        }  
        
        return false;                
    }
    
    //método para actualizar la tabla de productos
    public static void actualizarTablaClientes()
    {
        ordenar = "ASC";
        
        if (txtBuscarCliente.getText().equals(""))
        {
            //guardo la consulta where y se la mando a crearTabla()
            where = " ORDER BY nombre " + ordenar;            
        }
        else
        {
            where = " WHERE " + cbBuscarCliente.getSelectedItem() + " LIKE '%" + txtBuscarCliente.getText() +
                    "%' ORDER BY nombre " + ordenar;          
        }
      
        crearTabla(where);
        btnFichaCliente.setEnabled(false);
        btnAccesoCaja.setEnabled(false);
    }  
    
    private void habilitarTextFieldClientes()
    {
        txtCodCliente.setEnabled(true);
        txtNomCliente.setEnabled(true);
        txtApeCliente.setEnabled(true);
        txtDni.setEnabled(true);
        txtDireccion.setEnabled(true);
        txtCodPostal.setEnabled(true);
        txtPoblacion.setEnabled(true);
        txtProvincia.setEnabled(true);
        txtTelfnFijo.setEnabled(true);
        txtTelfnMovil.setEnabled(true);
        txtFecNac.setEnabled(true);
        txtFecAlta.setEnabled(true);
        txtEmail.setEnabled(true);
        txtObservaciones.setEnabled(true);
        calendarioCliente.setEnabled(true);
        
    }
    
    //método para deshabilitar los campos de texto de Productos
    private void deshabilitarTextFieldClientes()
    {
        txtCodCliente.setEnabled(false);
        txtNomCliente.setEnabled(false);
        txtApeCliente.setEnabled(false);
        txtDni.setEnabled(false);
        txtDireccion.setEnabled(false);
        txtCodPostal.setEnabled(false);
        txtPoblacion.setEnabled(false);
        txtProvincia.setEnabled(false);
        txtTelfnFijo.setEnabled(false);
        txtTelfnMovil.setEnabled(false);
        txtFecNac.setEnabled(false);
        txtFecAlta.setEnabled(false);
        txtEmail.setEnabled(false);
        txtObservaciones.setEnabled(false);
        calendarioCliente.setEnabled(false);
        calendarioAlta.setEnabled(false);
    }
    
    //método para habilitar los botones del formulario Productos
    private void habilitarBotonesCliente()
    {
        btnNuevoCliente.setEnabled(true);
        btnGuardarCliente.setEnabled(true);
        btnEliminarCliente.setEnabled(true);
        btnCancelarCliente.setEnabled(true);     
    }
    
     //método para deshabilitar los botones del formulario Productos
    private void deshabilitarBotonesCliente()
    {
        btnNuevoCliente.setEnabled(false);
        btnGuardarCliente.setEnabled(false);
        btnEliminarCliente.setEnabled(false);
        btnCancelarCliente.setEnabled(false);    
    } 
    
    private void navegarPorTabla()
    {

        int[] arr;
        accion = "modificar";
        java.util.Date fecha = null;
        SimpleDateFormat dateformat;        
        
        //almaceno la fila seleccionada en la tabla
        arr = tbClientes.getSelectedRows();
        txtCoincidencias.setText(Integer.toString(arr.length));

        //si no se ha seleccionado ninguna fila getSelectedRow() devuelve -1
        if(arr.length < 1)
        {
            JOptionPane.showMessageDialog(null, "No se ha seleccionado ninguna fila");
            btnFichaCliente.setEnabled(false);
            btnAccesoCaja.setEnabled(false);
        }
        else
        {
            //almaceno el modelo de tabla que se está utilizando en la tabla real
            modelo = (DefaultTableModel) tbClientes.getModel();

            if (arr.length == 1)
            {
                //habilito todos los campos de texto y botones (excepto cancelar)
                habilitarTextFieldClientes();
                habilitarBotonesCliente();
                btnCancelarCliente.setEnabled(false);
                btnFichaCliente.setEnabled(true);
                btnAccesoCaja.setEnabled(true);
                

                txtDni.setText((String)tbClientes.getValueAt(arr[0], 0));
                txtNomCliente.setText((String)tbClientes.getValueAt(arr[0], 1));
                txtApeCliente.setText((String)tbClientes.getValueAt(arr[0], 2));
                txtTelfnFijo.setText((String)tbClientes.getValueAt(arr[0], 3));                    
                txtTelfnMovil.setText((String)tbClientes.getValueAt(arr[0], 4));
                txtEmail.setText((String)tbClientes.getValueAt(arr[0], 5));
                txtCodCliente.setText((String)tbClientes.getValueAt(arr[0], 6));                    
                txtDireccion.setText((String)tbClientes.getValueAt(arr[0], 7));
                txtCodPostal.setText((String)tbClientes.getValueAt(arr[0], 8));
                txtPoblacion.setText((String)tbClientes.getValueAt(arr[0], 9));
                txtProvincia.setText((String)tbClientes.getValueAt(arr[0], 10));
                
                dateformat = new SimpleDateFormat("yyyy-MM-dd");                 
                calendarioCliente.setDateFormatString("dd-MM-yyyy");
                
                txtFecNac.setText("");
                
                if (tbClientes.getValueAt(arr[0], 11) != null)
                {                    
                    try 
                    {
                        fecha = dateformat.parse(tbClientes.getValueAt(arr[0], 11).toString());
                    } 
                    catch (ParseException ex) 
                    {
                        Logger.getLogger(Clientes.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    calendarioCliente.setDate(fecha);
                    dateformat = new SimpleDateFormat("dd-MM-yyyy"); 
                    txtFecNac.setText(dateformat.format(calendarioCliente.getDate()));     
                }
                else
                {
                    calendarioCliente.setDate(null);
                    //txtFecNac.setText((String)tbClientes.getValueAt(arr[0], 11));
                }     
                
                
                txtFecAlta.setText("");
                    
                dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");                 
                calendarioAlta.setDateFormatString("dd-MM-yyyy HH:mm:ss");
                
                if (tbClientes.getValueAt(arr[0], 12) != null)
                {                    
                    try 
                    {
                        fecha = dateformat.parse(tbClientes.getValueAt(arr[0], 12).toString());
                    } 
                    catch (ParseException ex) 
                    {
                        Logger.getLogger(Clientes.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    calendarioAlta.setDate(fecha);
                    dateformat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss"); 
                    txtFecAlta.setText(dateformat.format(calendarioAlta.getDate()));     
                }
                else
                {
                    calendarioAlta.setDate(null);
                }
      
                txtObservaciones.setText((String)tbClientes.getValueAt(arr[0], 13));  
                    
                btnFichaCliente.setEnabled(true);
                btnAccesoCaja.setEnabled(true);
            }
            //si se han seleccionado más de 1 registro
            else
            {
                //habilito los botones (excepto el de cancelar y guardar) y deshabilito campos texto
                habilitarBotonesCliente();
                textFieldClienteBlanco();
                deshabilitarTextFieldClientes();
                btnGuardarCliente.setEnabled(false);
                btnFichaCliente.setEnabled(false);
                btnAccesoCaja.setEnabled(false);
                
            }
        }        
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelClientes = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbClientes = new javax.swing.JTable();
        panelDatosCliente = new javax.swing.JPanel();
        panelBuscarCliente = new javax.swing.JPanel();
        txtBuscarCliente = new javax.swing.JTextField();
        btnBuscarCliente = new javax.swing.JButton();
        lbCoincidencias = new javax.swing.JLabel();
        txtCoincidencias = new javax.swing.JTextField();
        cbBuscarCliente = new javax.swing.JComboBox();
        lbTipoBusq = new javax.swing.JLabel();
        lbTecleaCadena = new javax.swing.JLabel();
        panelFichaCliente = new javax.swing.JPanel();
        btnFichaCliente = new javax.swing.JButton();
        lbVerFicha = new javax.swing.JLabel();
        btnVerOcultar = new javax.swing.JButton();
        lbVerOcultar = new javax.swing.JLabel();
        lbVerOcultar1 = new javax.swing.JLabel();
        btnAccesoCaja = new javax.swing.JButton();
        panelInformacionCliente = new javax.swing.JPanel();
        txtCodCliente = new javax.swing.JTextField();
        lbCodigoCliente = new javax.swing.JLabel();
        txtNomCliente = new javax.swing.JTextField();
        lbNombreCliente = new javax.swing.JLabel();
        lbApellidos = new javax.swing.JLabel();
        txtApeCliente = new javax.swing.JTextField();
        lbDireccion = new javax.swing.JLabel();
        txtDireccion = new javax.swing.JTextField();
        txtCodPostal = new javax.swing.JTextField();
        lbPoblacion = new javax.swing.JLabel();
        txtPoblacion = new javax.swing.JTextField();
        btnGuardarCliente = new javax.swing.JButton();
        btnNuevoCliente = new javax.swing.JButton();
        btnCancelarCliente = new javax.swing.JButton();
        btnEliminarCliente = new javax.swing.JButton();
        txtProvincia = new javax.swing.JTextField();
        txtTelfnFijo = new javax.swing.JTextField();
        txtTelfnMovil = new javax.swing.JTextField();
        txtEmail = new javax.swing.JTextField();
        txtFecNac = new javax.swing.JTextField();
        txtFecAlta = new javax.swing.JTextField();
        lbDni = new javax.swing.JLabel();
        txtDni = new javax.swing.JTextField();
        lbCodPostal = new javax.swing.JLabel();
        lbProvincia = new javax.swing.JLabel();
        lbTlfnFijo = new javax.swing.JLabel();
        lbFecNac = new javax.swing.JLabel();
        lbEmail = new javax.swing.JLabel();
        lbObservaciones = new javax.swing.JLabel();
        lbAsterisco1 = new javax.swing.JLabel();
        lbAsterisco2 = new javax.swing.JLabel();
        lbFecAlta = new javax.swing.JLabel();
        lbTlfnMovil = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtObservaciones = new javax.swing.JTextArea();
        calendarioCliente = new com.toedter.calendar.JDateChooser();
        calendarioAlta = new com.toedter.calendar.JDateChooser();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1000, 581));
        setResizable(false);

        panelClientes.setPreferredSize(new java.awt.Dimension(1230, 622));

        tbClientes.setAutoCreateRowSorter(true);
        tbClientes.setFont(new java.awt.Font("Arial Unicode MS", 0, 12)); // NOI18N
        tbClientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tbClientes.setRowHeight(18);
        tbClientes.getTableHeader().setReorderingAllowed(false);
        tbClientes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbClientesMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                tbClientesMouseEntered(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tbClientesMouseReleased(evt);
            }
        });
        tbClientes.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                tbClientesPropertyChange(evt);
            }
        });
        tbClientes.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbClientesKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tbClientesKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(tbClientes);

        panelBuscarCliente.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(153, 153, 153)));
        panelBuscarCliente.setPreferredSize(new java.awt.Dimension(400, 73));

        txtBuscarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBuscarClienteActionPerformed(evt);
            }
        });
        txtBuscarCliente.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtBuscarClienteKeyPressed(evt);
            }
        });

        btnBuscarCliente.setText("Buscar");
        btnBuscarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarClienteActionPerformed(evt);
            }
        });

        lbCoincidencias.setText("Nº coincidencias: ");

        txtCoincidencias.setEnabled(false);

        cbBuscarCliente.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cbBuscarClienteMouseClicked(evt);
            }
        });
        cbBuscarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbBuscarClienteActionPerformed(evt);
            }
        });
        cbBuscarCliente.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cbBuscarClienteFocusGained(evt);
            }
        });

        lbTipoBusq.setText("Buscar por: ");

        lbTecleaCadena.setText("Tec. texto: ");

        javax.swing.GroupLayout panelBuscarClienteLayout = new javax.swing.GroupLayout(panelBuscarCliente);
        panelBuscarCliente.setLayout(panelBuscarClienteLayout);
        panelBuscarClienteLayout.setHorizontalGroup(
            panelBuscarClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBuscarClienteLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelBuscarClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelBuscarClienteLayout.createSequentialGroup()
                        .addComponent(lbTipoBusq)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbBuscarCliente, 0, 114, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbCoincidencias))
                    .addGroup(panelBuscarClienteLayout.createSequentialGroup()
                        .addComponent(lbTecleaCadena)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtBuscarCliente)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelBuscarClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnBuscarCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtCoincidencias))
                .addContainerGap())
        );
        panelBuscarClienteLayout.setVerticalGroup(
            panelBuscarClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBuscarClienteLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelBuscarClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelBuscarClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lbCoincidencias)
                        .addComponent(txtCoincidencias, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelBuscarClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cbBuscarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lbTipoBusq)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelBuscarClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtBuscarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbTecleaCadena)
                    .addComponent(btnBuscarCliente))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelFichaCliente.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(153, 153, 153)));
        panelFichaCliente.setPreferredSize(new java.awt.Dimension(400, 85));

        btnFichaCliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/ficha_pequeño.png"))); // NOI18N
        btnFichaCliente.setEnabled(false);
        btnFichaCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFichaClienteActionPerformed(evt);
            }
        });

        lbVerFicha.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbVerFicha.setText("Ver/Ocultar clientes ");

        btnVerOcultar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/Ver-ocultar.png"))); // NOI18N
        btnVerOcultar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerOcultarActionPerformed(evt);
            }
        });

        lbVerOcultar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbVerOcultar.setText("Ver ficha");

        lbVerOcultar1.setText("Acceso a Caja");

        btnAccesoCaja.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/Caja_peque.png"))); // NOI18N
        btnAccesoCaja.setEnabled(false);
        btnAccesoCaja.setPreferredSize(new java.awt.Dimension(83, 52));
        btnAccesoCaja.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAccesoCajaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelFichaClienteLayout = new javax.swing.GroupLayout(panelFichaCliente);
        panelFichaCliente.setLayout(panelFichaClienteLayout);
        panelFichaClienteLayout.setHorizontalGroup(
            panelFichaClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFichaClienteLayout.createSequentialGroup()
                .addGap(53, 53, 53)
                .addGroup(panelFichaClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelFichaClienteLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(lbVerOcultar1))
                    .addComponent(btnAccesoCaja, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(panelFichaClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelFichaClienteLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnFichaCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelFichaClienteLayout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(lbVerOcultar)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFichaClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbVerFicha, javax.swing.GroupLayout.PREFERRED_SIZE, 96, Short.MAX_VALUE)
                    .addComponent(btnVerOcultar, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(53, 53, 53))
        );
        panelFichaClienteLayout.setVerticalGroup(
            panelFichaClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFichaClienteLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(panelFichaClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnAccesoCaja, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnFichaCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnVerOcultar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(2, 2, 2)
                .addGroup(panelFichaClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbVerOcultar1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFichaClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lbVerFicha)
                        .addComponent(lbVerOcultar)))
                .addGap(11, 11, 11))
        );

        panelInformacionCliente.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(153, 153, 153)));
        panelInformacionCliente.setForeground(new java.awt.Color(51, 51, 51));
        panelInformacionCliente.setPreferredSize(new java.awt.Dimension(400, 420));

        txtCodCliente.setEditable(false);
        txtCodCliente.setDoubleBuffered(true);
        txtCodCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCodClienteActionPerformed(evt);
            }
        });

        lbCodigoCliente.setText("Código cliente: ");

        txtNomCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNomClienteActionPerformed(evt);
            }
        });

        lbNombreCliente.setText("Nombre: ");

        lbApellidos.setText("Apellidos: ");

        txtApeCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtApeClienteActionPerformed(evt);
            }
        });

        lbDireccion.setText("Direccion: ");

        txtDireccion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDireccionActionPerformed(evt);
            }
        });

        txtCodPostal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCodPostalActionPerformed(evt);
            }
        });

        lbPoblacion.setText("Población: ");

        txtPoblacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPoblacionActionPerformed(evt);
            }
        });

        btnGuardarCliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/Guardar.png"))); // NOI18N
        btnGuardarCliente.setText("Guardar");
        btnGuardarCliente.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnGuardarCliente.setPreferredSize(new java.awt.Dimension(75, 57));
        btnGuardarCliente.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnGuardarCliente.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnGuardarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarClienteActionPerformed(evt);
            }
        });

        btnNuevoCliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/NuevoCliente_peque.png"))); // NOI18N
        btnNuevoCliente.setText("Nuevo");
        btnNuevoCliente.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnNuevoCliente.setPreferredSize(new java.awt.Dimension(75, 57));
        btnNuevoCliente.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnNuevoCliente.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnNuevoCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevoClienteActionPerformed(evt);
            }
        });

        btnCancelarCliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/Cancelar.png"))); // NOI18N
        btnCancelarCliente.setText("Cancelar");
        btnCancelarCliente.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCancelarCliente.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnCancelarCliente.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnCancelarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarClienteActionPerformed(evt);
            }
        });

        btnEliminarCliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/papelera.png"))); // NOI18N
        btnEliminarCliente.setText("Eliminar");
        btnEliminarCliente.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEliminarCliente.setPreferredSize(new java.awt.Dimension(75, 57));
        btnEliminarCliente.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnEliminarCliente.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnEliminarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarClienteActionPerformed(evt);
            }
        });

        txtProvincia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtProvinciaActionPerformed(evt);
            }
        });

        txtTelfnFijo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTelfnFijoActionPerformed(evt);
            }
        });

        txtTelfnMovil.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTelfnMovilActionPerformed(evt);
            }
        });

        txtEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEmailActionPerformed(evt);
            }
        });

        txtFecNac.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFecNacActionPerformed(evt);
            }
        });

        txtFecAlta.setEditable(false);
        txtFecAlta.setEnabled(false);
        txtFecAlta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFecAltaActionPerformed(evt);
            }
        });

        lbDni.setText("DNI: ");

        txtDni.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDniActionPerformed(evt);
            }
        });
        txtDni.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtDniKeyPressed(evt);
            }
        });

        lbCodPostal.setText("Codigo Postal: ");

        lbProvincia.setText("Provincia: ");

        lbTlfnFijo.setText("Telf. fijo: ");

        lbFecNac.setText("Fecha Nac.: ");

        lbEmail.setText("E-mail: ");

        lbObservaciones.setText("Observaciones: ");

        lbAsterisco1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lbAsterisco1.setText("*");

        lbAsterisco2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lbAsterisco2.setText("*");

        lbFecAlta.setText("Alta: ");

        lbTlfnMovil.setText("Telf. Mvl: ");

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        txtObservaciones.setColumns(20);
        txtObservaciones.setLineWrap(true);
        txtObservaciones.setRows(5);
        txtObservaciones.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtObservacionesKeyPressed(evt);
            }
        });
        jScrollPane2.setViewportView(txtObservaciones);

        calendarioAlta.setEnabled(false);

        javax.swing.GroupLayout panelInformacionClienteLayout = new javax.swing.GroupLayout(panelInformacionCliente);
        panelInformacionCliente.setLayout(panelInformacionClienteLayout);
        panelInformacionClienteLayout.setHorizontalGroup(
            panelInformacionClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInformacionClienteLayout.createSequentialGroup()
                .addGroup(panelInformacionClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelInformacionClienteLayout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addGroup(panelInformacionClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(panelInformacionClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(lbCodigoCliente)
                                .addComponent(lbDireccion, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(lbApellidos, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(lbNombreCliente, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(lbDni, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(lbCodPostal, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(lbPoblacion, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(lbProvincia, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(lbTlfnFijo, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(lbFecNac, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(lbEmail, javax.swing.GroupLayout.Alignment.TRAILING))
                            .addComponent(lbObservaciones))
                        .addGroup(panelInformacionClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelInformacionClienteLayout.createSequentialGroup()
                                .addGap(4, 4, 4)
                                .addGroup(panelInformacionClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtCodPostal, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(panelInformacionClienteLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panelInformacionClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(panelInformacionClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelInformacionClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(txtNomCliente)
                                            .addComponent(txtApeCliente)
                                            .addComponent(txtPoblacion)
                                            .addComponent(jScrollPane2)
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelInformacionClienteLayout.createSequentialGroup()
                                                .addComponent(txtTelfnFijo, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(lbTlfnMovil)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(txtTelfnMovil, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(panelInformacionClienteLayout.createSequentialGroup()
                                                .addComponent(txtFecNac, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(1, 1, 1)
                                                .addComponent(calendarioCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(lbFecAlta)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(txtFecAlta)
                                                .addGap(1, 1, 1)
                                                .addComponent(calendarioAlta, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(txtProvincia, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(panelInformacionClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(txtCodCliente)
                                        .addComponent(txtDni, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelInformacionClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbAsterisco1, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbAsterisco2, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(panelInformacionClienteLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnNuevoCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnGuardarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEliminarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCancelarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelInformacionClienteLayout.setVerticalGroup(
            panelInformacionClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelInformacionClienteLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelInformacionClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelInformacionClienteLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(lbCodigoCliente))
                    .addComponent(txtCodCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelInformacionClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbDni, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDni, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelInformacionClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNomCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbNombreCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbAsterisco1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelInformacionClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtApeCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbApellidos, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbAsterisco2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelInformacionClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelInformacionClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCodPostal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbCodPostal))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelInformacionClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelInformacionClienteLayout.createSequentialGroup()
                        .addGroup(panelInformacionClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtPoblacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbPoblacion, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelInformacionClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtProvincia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbProvincia, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelInformacionClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtTelfnFijo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbTlfnFijo, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbTlfnMovil, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtTelfnMovil, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(panelInformacionClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(panelInformacionClienteLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panelInformacionClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(panelInformacionClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                                        .addComponent(txtFecNac)
                                        .addComponent(lbFecNac))
                                    .addComponent(calendarioCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(panelInformacionClienteLayout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addGroup(panelInformacionClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtFecAlta)
                                    .addComponent(lbFecAlta)))))
                    .addComponent(calendarioAlta, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelInformacionClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbEmail))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelInformacionClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbObservaciones)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelInformacionClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnGuardarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnNuevoCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCancelarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEliminarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16))
        );

        javax.swing.GroupLayout panelDatosClienteLayout = new javax.swing.GroupLayout(panelDatosCliente);
        panelDatosCliente.setLayout(panelDatosClienteLayout);
        panelDatosClienteLayout.setHorizontalGroup(
            panelDatosClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelInformacionCliente, javax.swing.GroupLayout.DEFAULT_SIZE, 424, Short.MAX_VALUE)
            .addGroup(panelDatosClienteLayout.createSequentialGroup()
                .addGroup(panelDatosClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelFichaCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panelBuscarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        panelDatosClienteLayout.setVerticalGroup(
            panelDatosClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDatosClienteLayout.createSequentialGroup()
                .addComponent(panelInformacionCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 412, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelBuscarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelFichaCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout panelClientesLayout = new javax.swing.GroupLayout(panelClientes);
        panelClientes.setLayout(panelClientesLayout);
        panelClientesLayout.setHorizontalGroup(
            panelClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelClientesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelDatosCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 416, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelClientesLayout.setVerticalGroup(
            panelClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelClientesLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(panelClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelDatosCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelClientes, javax.swing.GroupLayout.DEFAULT_SIZE, 842, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelClientes, javax.swing.GroupLayout.PREFERRED_SIZE, 590, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtCodClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodClienteActionPerformed
        txtCodCliente.transferFocus();//permite que al pulsar Enter pase al campo siguiente
    }//GEN-LAST:event_txtCodClienteActionPerformed

    private void txtNomClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNomClienteActionPerformed
        txtNomCliente.transferFocus();//permite que al pulsar Enter pase al campo siguiente
    }//GEN-LAST:event_txtNomClienteActionPerformed

    private void txtApeClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtApeClienteActionPerformed
        txtApeCliente.transferFocus();//permite que al pulsar Enter pase al campo siguiente
    }//GEN-LAST:event_txtApeClienteActionPerformed

    private void txtDireccionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDireccionActionPerformed
        txtDireccion.transferFocus();//permite que al pulsar Enter pase al campo siguiente
    }//GEN-LAST:event_txtDireccionActionPerformed

    private void txtCodPostalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodPostalActionPerformed
        txtCodPostal.transferFocus();//permite que al pulsar Enter pase al campo siguiente
    }//GEN-LAST:event_txtCodPostalActionPerformed

    private void txtPoblacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPoblacionActionPerformed
        txtPoblacion.transferFocus();//permite que al pulsar Enter pase al campo siguiente
    }//GEN-LAST:event_txtPoblacionActionPerformed

    private void btnGuardarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarClienteActionPerformed

        boolean res;
        int filasel;
        String[] camposValores;
        consulta = new Conexion();
        String where;

        if(comprobarTextFieldCliente() == false)
        {
            camposValores = recogerDatosClientes();

            switch (accion)
            {
                case "insertar":

                    res = consulta.insert("clientes", camposValores[0], camposValores[1]);
                    if (res == true)
                    {
                        JOptionPane.showMessageDialog(null, "La inserción se ha realizado correctamente",
                            "Añadir Cliente", JOptionPane.INFORMATION_MESSAGE);

                        actualizarTablaClientes();
                        
                        //Localizo el id del último registro insertado
                        rs = consulta.buscarRegistro("clientes", "MAX(id) as id", null, null);
                        try 
                        {
                            while (rs.next())                                                                                                            
                            SelCliente.idCliente = rs.getString("id");                      
                        }
                        catch (SQLException ex) 
                        {
                            Logger.getLogger(Clientes.class.getName()).log(Level.SEVERE, null, ex);
                        } 
                        
                        SelCliente.nomCliente = txtNomCliente.getText() + " " + txtApeCliente.getText();
                                                        
                        deshabilitarBotonesCliente();
                        btnNuevoCliente.setEnabled(true);
                        deshabilitarTextFieldClientes();
                        
                        //Al cargar este formulario activé un flag en caso de venir de
                        //SelCliente, para acceder a Caja. Si es así, al crear el nuevo
                        //cliente cierro la ventana Clientes y muestro Caja.
                        if (accesoCaja)
                        {                            
                            dispose();
                        }

                    }

                    break;
                    
                case "modificar":
                    
                    if (tbClientes.getSelectedRowCount() > 1 || tbClientes.getSelectedRowCount() < 1)
                    {
                        JOptionPane.showMessageDialog(null, "Debe seleccionar un cliente",
                            "Modificar cliente", JOptionPane.INFORMATION_MESSAGE);
                    }
                    else
                    {
                        //localizo la fila seleccionada en la tabla
                        filasel = tbClientes.getSelectedRow();

                        int respuesta = JOptionPane.showConfirmDialog(null,
                            "Se va ha modificar el cliente con 'DNI: " + txtDni.getText() + "' y Nombre: '" + 
                                txtNomCliente.getText() + " " + txtApeCliente.getText() + "', ¿Está seguro?",
                            "Modificar cliente", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                        if (respuesta==0)
                        {
                            if(filasel == -1)
                            {
                                JOptionPane.showMessageDialog(null, "No se ha seleccionado ningun cliente",
                                    "Modificar cliente", JOptionPane.INFORMATION_MESSAGE);
                            }

                            else
                            {
                                //almaceno el id de la fila seleccionada
                                where = "id='" + txtCodCliente.getText() + "'";

                                //llamo al método Update de la clase conexión y le envío tabla, camposYvalores, condición
                                consulta.update("clientes", camposValores[1], where);

                                JOptionPane.showMessageDialog(null, "El cliente se ha sido modificado correctamente",
                                    "Modificar cliente", JOptionPane.INFORMATION_MESSAGE);
                                
                                actualizarTablaClientes();
                                deshabilitarBotonesCliente();
                                btnNuevoCliente.setEnabled(true);
                                deshabilitarTextFieldClientes();
                            }
                        }
                    }
                    break;
            }
        }

    }//GEN-LAST:event_btnGuardarClienteActionPerformed

    private void btnNuevoClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoClienteActionPerformed

        accion="insertar";
        txtDni.requestFocusInWindow();
        deshabilitarBotonesCliente();
        btnGuardarCliente.setEnabled(true);
        btnCancelarCliente.setEnabled(true);
        btnFichaCliente.setEnabled(false);
        btnAccesoCaja.setEnabled(false);
        habilitarTextFieldClientes();
        textFieldClienteBlanco();
    }//GEN-LAST:event_btnNuevoClienteActionPerformed

    private void btnCancelarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarClienteActionPerformed

        deshabilitarBotonesCliente();
        btnNuevoCliente.setEnabled(true);
        deshabilitarTextFieldClientes();
        textFieldClienteBlanco();
        
        actualizarTablaClientes();

    }//GEN-LAST:event_btnCancelarClienteActionPerformed

    private void btnEliminarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarClienteActionPerformed

        String where;
        String clienteBorrar="";
        int[] arr;

        arr = tbClientes.getSelectedRows();

        if(arr.length == 0)
        {
            btnEliminarCliente.setEnabled(false);

            JOptionPane.showMessageDialog(null, "Debe seleccionar un cliente",
                "Eliminar clientes", JOptionPane.INFORMATION_MESSAGE);

        }
        else
        {
            txtCoincidencias.setText(Integer.toString(arr.length));
            for (int i=0; i < arr.length; i++)
            {
                clienteBorrar = clienteBorrar+"'id "+modelo.getValueAt(arr[i], 6)+"' - '" + tbClientes.getValueAt(arr[i], 1) + 
                        " " + tbClientes.getValueAt(arr[i], 2) + "'\n";
            }

            int respuesta1 = JOptionPane.showConfirmDialog(null,
                "¿Desea borrar el/los clientes seleccionados y todo su histórico?\n"
              + "Si responde 'No', solo se borrará el cliente, pero no su histórico.",
                "Eliminar clientes", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);            
            System.out.println("respuesta: "+respuesta1);
            if (respuesta1 == 0)
            {                            
                int respuesta2 = JOptionPane.showConfirmDialog(null,
                    "Se van a borrar los siguientes " + arr.length + " clientes: \n" + clienteBorrar,
                    "Eliminar clientes", JOptionPane.WARNING_MESSAGE);

                if (respuesta2 == 0)
                {
                    //deshabilito todos los campos de texto y botones (excepto el de Nuevo)
                    deshabilitarTextFieldClientes();
                    deshabilitarBotonesCliente();
                    btnNuevoCliente.setEnabled(true);
                    textFieldClienteBlanco();

                    int i=0;
                    consulta = new Conexion();

                    while (arr.length > i)
                    {
                        where = "id='"+(String) tbClientes.getValueAt(arr[i], 6)+"'";
                        //Elimino el cliente
                        consulta.eliminar("clientes", where);
                        
                        where = "id_cliente='"+(String) tbClientes.getValueAt(arr[i], 6)+"'";
                        //Borro el historico de cada cliente que se eliminó
                        consulta.eliminar("historico", where);
                        
                        i++;
                    }     
                    actualizarTablaClientes();
                }
            }  
            else if(respuesta1 == 1)
            {                
                //deshabilito todos los campos de texto y botones (excepto el de Nuevo)
                deshabilitarTextFieldClientes();
                deshabilitarBotonesCliente();
                btnNuevoCliente.setEnabled(true);
                textFieldClienteBlanco();

                int i=0;
                consulta = new Conexion();

                while (arr.length > i)
                {
                    where = "id='" + (String) tbClientes.getValueAt(arr[i], 6) + "'";
                    consulta.eliminar("clientes", where);
                    i++;
                }  
                actualizarTablaClientes();
            }
            
        }
    }//GEN-LAST:event_btnEliminarClienteActionPerformed

    private void txtBuscarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarClienteActionPerformed

    }//GEN-LAST:event_txtBuscarClienteActionPerformed

    private void txtBuscarClienteKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarClienteKeyPressed

        int code = evt.getKeyCode(); //almacena el código numerico de la tecla
        char caracter = evt.getKeyChar(); //almacena el caracter (de momento no lo uso)

        //si pulso la tecla ENTER realizo la misma función que el botón "btnBuscar"
        if(code == KeyEvent.VK_ENTER)
        {
            ordenar = "ASC";  

            if (txtBuscarCliente.getText().equals(""))
            {
                //guardo la consulta where y se la mando a crearTabla()
                where = " ORDER BY nombre " + ordenar;            
            }
            else
            {
                where = " WHERE " + cbBuscarCliente.getSelectedItem() + " LIKE '%" + txtBuscarCliente.getText() +
                        "%' ORDER BY nombre " + ordenar;          
            }
            
            crearTabla(where);            
            txtCoincidencias.setText(String.valueOf(modelo.getRowCount()));
            deshabilitarTextFieldClientes();
            deshabilitarBotonesCliente();

            btnNuevoCliente.setEnabled(true);            
        }
    }//GEN-LAST:event_txtBuscarClienteKeyPressed

    private void btnBuscarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarClienteActionPerformed

        ordenar = "ASC";        
        
        if (txtBuscarCliente.getText().equals(""))
        {
            //guardo la consulta where y se la mando a crearTabla()
            where = " ORDER BY nombre " + ordenar;            
        }
        else
        {
            where = " WHERE " + cbBuscarCliente.getSelectedItem() + " LIKE '%" + txtBuscarCliente.getText() +
                    "%' ORDER BY nombre " + ordenar;          
        }        

        crearTabla(where);
            
        txtCoincidencias.setText(String.valueOf(modelo.getRowCount()));
        //textFieldClienteBlanco();
        
        deshabilitarTextFieldClientes();
        deshabilitarBotonesCliente();
        
        btnNuevoCliente.setEnabled(true);
    }//GEN-LAST:event_btnBuscarClienteActionPerformed

    private void cbBuscarClienteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cbBuscarClienteMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_cbBuscarClienteMouseClicked

    private void cbBuscarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbBuscarClienteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbBuscarClienteActionPerformed

    private void cbBuscarClienteFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cbBuscarClienteFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_cbBuscarClienteFocusGained

    private void tbClientesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbClientesMouseClicked
        tbClientes.requestFocusInWindow();
    }//GEN-LAST:event_tbClientesMouseClicked

    private void tbClientesMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbClientesMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_tbClientesMouseEntered

    private void tbClientesMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbClientesMouseReleased

        navegarPorTabla();
        
    }//GEN-LAST:event_tbClientesMouseReleased

    private void tbClientesPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_tbClientesPropertyChange

    }//GEN-LAST:event_tbClientesPropertyChange

    private void txtDniActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDniActionPerformed

        txtDni.transferFocus();
    }//GEN-LAST:event_txtDniActionPerformed

    private void btnVerOcultarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerOcultarActionPerformed
        
        if (tbClientes.isVisible())
        {
            tbClientes.setVisible(false);
        }
        else 
        {
            tbClientes.setVisible(true);
        }        
    }//GEN-LAST:event_btnVerOcultarActionPerformed

    private void btnFichaClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFichaClienteActionPerformed

        
        SelCliente.nomCliente = txtNomCliente.getText() + " " + txtApeCliente.getText();
        SelCliente.idCliente = txtCodCliente.getText();
        
        FichaCliente ficha = new FichaCliente ();
        ficha.setVisible(true);
        /*FichaCliente ficha = new FichaCliente (null, true);
        ficha.setVisible(true);*/
        
    }//GEN-LAST:event_btnFichaClienteActionPerformed

    private void txtDniKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDniKeyPressed

       
    }//GEN-LAST:event_txtDniKeyPressed

    private void txtProvinciaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtProvinciaActionPerformed

        txtProvincia.transferFocus();
    }//GEN-LAST:event_txtProvinciaActionPerformed

    private void txtTelfnFijoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTelfnFijoActionPerformed

        txtTelfnFijo.transferFocus();
    }//GEN-LAST:event_txtTelfnFijoActionPerformed

    private void txtTelfnMovilActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTelfnMovilActionPerformed

        txtTelfnMovil.transferFocus();
    }//GEN-LAST:event_txtTelfnMovilActionPerformed

    private void txtFecNacActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFecNacActionPerformed

        txtFecNac.transferFocus();
        calendarioCliente.transferFocus();
        txtFecAlta.transferFocus();
        calendarioAlta.transferFocus();
    }//GEN-LAST:event_txtFecNacActionPerformed

    private void txtFecAltaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFecAltaActionPerformed

        txtFecAlta.transferFocus();
    }//GEN-LAST:event_txtFecAltaActionPerformed

    private void txtEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEmailActionPerformed

        txtEmail.transferFocus();
    }//GEN-LAST:event_txtEmailActionPerformed

    private void txtObservacionesKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtObservacionesKeyPressed

        txtObservaciones.transferFocus();
    }//GEN-LAST:event_txtObservacionesKeyPressed

    private void btnAccesoCajaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAccesoCajaActionPerformed

        SelCliente.idCliente = txtCodCliente.getText();
        SelCliente.nomCliente = txtNomCliente.getText() + " " + txtApeCliente.getText(); 
         
        Caja caja = new Caja();
        
        Principal.contenedor.add(caja);     
        Principal.contenedor.setSelectedIndex(Principal.contenedor.getTabCount()-1);
                
        dispose();         
    }//GEN-LAST:event_btnAccesoCajaActionPerformed

    private void tbClientesKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbClientesKeyPressed
        
    }//GEN-LAST:event_tbClientesKeyPressed

    private void tbClientesKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbClientesKeyReleased
        navegarPorTabla();
    }//GEN-LAST:event_tbClientesKeyReleased

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Clientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Clientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Clientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Clientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Clientes dialog = new Clientes(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public static javax.swing.JButton btnAccesoCaja;
    private javax.swing.JButton btnBuscarCliente;
    private javax.swing.JButton btnCancelarCliente;
    private javax.swing.JButton btnEliminarCliente;
    public static javax.swing.JButton btnFichaCliente;
    private javax.swing.JButton btnGuardarCliente;
    private javax.swing.JButton btnNuevoCliente;
    private javax.swing.JButton btnVerOcultar;
    private com.toedter.calendar.JDateChooser calendarioAlta;
    private com.toedter.calendar.JDateChooser calendarioCliente;
    public static javax.swing.JComboBox cbBuscarCliente;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lbApellidos;
    private javax.swing.JLabel lbAsterisco1;
    private javax.swing.JLabel lbAsterisco2;
    private javax.swing.JLabel lbCodPostal;
    private javax.swing.JLabel lbCodigoCliente;
    private javax.swing.JLabel lbCoincidencias;
    private javax.swing.JLabel lbDireccion;
    private javax.swing.JLabel lbDni;
    private javax.swing.JLabel lbEmail;
    private javax.swing.JLabel lbFecAlta;
    private javax.swing.JLabel lbFecNac;
    private javax.swing.JLabel lbNombreCliente;
    private javax.swing.JLabel lbObservaciones;
    private javax.swing.JLabel lbPoblacion;
    private javax.swing.JLabel lbProvincia;
    private javax.swing.JLabel lbTecleaCadena;
    private javax.swing.JLabel lbTipoBusq;
    private javax.swing.JLabel lbTlfnFijo;
    private javax.swing.JLabel lbTlfnMovil;
    private javax.swing.JLabel lbVerFicha;
    private javax.swing.JLabel lbVerOcultar;
    private javax.swing.JLabel lbVerOcultar1;
    private javax.swing.JPanel panelBuscarCliente;
    private javax.swing.JPanel panelClientes;
    private javax.swing.JPanel panelDatosCliente;
    private javax.swing.JPanel panelFichaCliente;
    private javax.swing.JPanel panelInformacionCliente;
    public static javax.swing.JTable tbClientes;
    private javax.swing.JTextField txtApeCliente;
    public static javax.swing.JTextField txtBuscarCliente;
    private javax.swing.JTextField txtCodCliente;
    private javax.swing.JTextField txtCodPostal;
    private javax.swing.JTextField txtCoincidencias;
    private javax.swing.JTextField txtDireccion;
    private javax.swing.JTextField txtDni;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtFecAlta;
    private javax.swing.JTextField txtFecNac;
    private javax.swing.JTextField txtNomCliente;
    private javax.swing.JTextArea txtObservaciones;
    private javax.swing.JTextField txtPoblacion;
    private javax.swing.JTextField txtProvincia;
    private javax.swing.JTextField txtTelfnFijo;
    private javax.swing.JTextField txtTelfnMovil;
    // End of variables declaration//GEN-END:variables
}
