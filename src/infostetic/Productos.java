/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package infostetic;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/**
 *
 * @author Javi
 */
public class Productos extends javax.swing.JDialog {

    DefaultTableModel modelo;
    ButtonGroup btnGrupoBusc;
    ButtonGroup btnGrupoFil;
    ButtonGroup btnGrupoOrd;
    String accion = "";
    String ordenar = null;    
    String where = null;
    String fecha;
    Conexion consulta;
    ResultSet rs;
    public static Boolean fromCaja;
    
    public Productos(java.awt.Frame parent, boolean modal) 
    {
        super(parent, modal);
        initComponents();
        
        fromCaja = false;
        
        btnGrupoFil = new ButtonGroup();
        btnGrupoFil.add(rbFiltrarProductos);
        btnGrupoFil.add(rbFiltrarServicios);
        btnGrupoFil.add(rbFiltrarTodos);
        
        setLocationRelativeTo(null);//centro la ventana en medio de la pantalla

        //deshabilito todos los botones de Productos menos "Nuevo"
        deshabilitarBotonesProd();
        btnNuevoProd.setEnabled(true);
        
        //deshabilito todos los campos de texto
        deshabilitarTextFieldProd();        
        
        String[] columNames = new String[] {"codigo", "id", "producto", "cantidad", "pvp", "proveedor", "fecha", "identificador"};        
        //cargo el combo de tipos de columnas para buscar el producto
        for (int i=0; columNames.length > i; i++)
        {
            cbBuscarProductos.addItem(columNames[i]);
        }
        
        consulta = new Conexion();
        
        rs = consulta.selectResult("proveedores", "proveedor", " ORDER BY proveedor ASC");
        
        try 
        {
            while (rs.next()) 
            {
                cbProveedor.addItem(rs.getString("proveedor"));
            }
        } 
        catch (SQLException ex) 
        {
            JOptionPane.showMessageDialog(null, ex);
        }         
        
        rs = consulta.selectResult("familia_servicios", "tipo_servicio", " ORDER BY tipo_servicio ASC");
        
        try 
        {
            while (rs.next()) 
            {
                cbProveedor.addItem(rs.getString("tipo_servicio"));
            }
        } 
        catch (SQLException ex) 
        {
            JOptionPane.showMessageDialog(null, ex);
        } 
        



        modelo = new DefaultTableModel() {

            @Override
            public boolean isCellEditable(int fila, int columna) {
                    return false;
            }

            
               /* public Class getColumnClass(int columna)
                {
                    System.out.println("columna: "+getColumnName(columna));
                    
                   if (columna == 0) return String.class;
                   if (columna == 1) return Integer.class;
                   if (columna == 2) return String.class;
                   if (columna == 3) return Integer.class;
                   if (columna == 4) return Float.class;
                   if (columna == 5) return String.class;
                   if (columna == 6) return java.util.Date.class;
                   if (columna == 7) return String.class;
                   
                   return Object.class;
                }     */         
        }; 
        
        calendarioProductos.addPropertyChangeListener(new java.beans.PropertyChangeListener() 
        {
            public void propertyChange(java.beans.PropertyChangeEvent evt) 
            {
                calendarioProductosPropertyChange(evt);
            }

            private void calendarioProductosPropertyChange(PropertyChangeEvent evt) 
            {
                if (calendarioProductos.getDate() != null)
                {                
                        SimpleDateFormat dateformat;         
                        String fecha = null;

                        dateformat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss"); 
                        calendarioProductos.setDateFormatString("dd-MM-yyyy HH:mm:ss");  
                        
                        txtFechaProd.setText(dateformat.format(calendarioProductos.getDate()));
                        
                        dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
                        fecha = dateformat.format(calendarioProductos.getDate());                                                
                        try 
                        {                            
                            calendarioProductos.setDate(dateformat.parse(fecha));                            
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
        crearTabla(" ORDER BY producto ASC");       
        
        
       // deshabilitarTextFieldProd();
        //deshabilitarBotonesProd();
       // btnNuevoProd.setEnabled(true);
        textFieldProdBlanco();        
    }

    private void crearTabla (String where)
    {
        String[] columNames = new String[] {"codigo", "producto", "cantidad", 
                                            "pvp", "proveedor", "fecha", "identificador","id"};  

    /*    for (int i=0; i < columNames.length; i++)
        {
            modelo.addColumn(columNames[i]);
        }
        
        tbProductos.setModel(modelo);      */          
        
        //consulta = new Conexion();

        modelo = consulta.getProductos(where);
        
        tbProductos.setModel(modelo); 
        
        TableColumn[] columna = new TableColumn[columNames.length];
        
        for (int i=0; i < columNames.length; i++)
        {        
            columna[i] = tbProductos.getColumn(columNames[i]); //Obtienes la columna
        }

        columna[0].setPreferredWidth(45); //Código               
        columna[1].setPreferredWidth(230); //producto
        columna[2].setPreferredWidth(50); //cantidad
        columna[3].setPreferredWidth(40); //precio
        columna[4].setPreferredWidth(105); //proveedor
        columna[5].setPreferredWidth(110); //fecha
        columna[6].setPreferredWidth(40);   //identificador
        //columna[7].setPreferredWidth(0); //id

            tbProductos.getColumnModel().getColumn(7).setMaxWidth(0);
            tbProductos.getColumnModel().getColumn(7).setMinWidth(0);
            tbProductos.getColumnModel().getColumn(7).setPreferredWidth(0);               
              
    }
    //método para detectar el nombre del radiobutton que está seleccionado
    public String botonSeleccionado(ButtonGroup buttonGroup) 
    {
        
        for (Enumeration<AbstractButton> buttons = buttonGroup.getElements(); buttons.hasMoreElements();) 
        {
            AbstractButton button = buttons.nextElement();

            if (button.isSelected()) 
            {
                //System.out.println("entro");
                return button.getText();
            }
        }
        return null;
    }   
    
    private void textFieldProdBlanco()
    {
        txtIdProducto.setText("");
        txtCodigoProducto.setText("");
        txtNombreProducto.setText("");
        cbProveedor.setSelectedIndex(-1);
        txtPrecioProducto.setText("");
        txtCantidadProducto.setText("");
        txtFechaProd.setText("");   
        //txtBuscarProducto.setText("");
        txtCoincidencias.setText("");
    }
    
    public boolean validarFecha(String fecha) 
    {  
  
        if (fecha == null) 
        {
            return false;  
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss"); //año-mes-dia  

        if (fecha.trim().length() != dateFormat.toPattern().length())
        {
            return false;  
        }

        dateFormat.setLenient(false);  

        try 
        {  
            dateFormat.parse(fecha.trim());  
        }  
        catch (ParseException pe) 
        {  
            return false;  
        }  
        
        return true;  
        
    } 
    
    private String[] recogerDatosProductos()
    {
        String valores="", campos="";
        String[][] datoProd=new String[8][2];
        String[] resultado = new String[2];
        Boolean formularioCompleto = false;     
        SimpleDateFormat dateFormat;
        
        if (txtNombreProducto.getText().equals(""))
        {
             JOptionPane.showMessageDialog(null, "El campo Producto es obligatorio.",
                          "Añadir Producto", JOptionPane.INFORMATION_MESSAGE);    
             txtNombreProducto.requestFocusInWindow();
        }
        else if (cbProveedor.getSelectedIndex() == -1)
        {
             JOptionPane.showMessageDialog(null, "Debe seleccionar al menos un Proveedor.",
                          "Añadir Producto", JOptionPane.INFORMATION_MESSAGE);     
             cbProveedor.requestFocusInWindow();
        }
        else if (txtPrecioProducto.getText().equals(""))
        {
             JOptionPane.showMessageDialog(null, "El campo Precio es obligatorio.",
                          "Añadir Producto", JOptionPane.INFORMATION_MESSAGE);    
             txtPrecioProducto.requestFocusInWindow();
        }          
        else if (txtCantidadProducto.getText().equals(""))
        {
             JOptionPane.showMessageDialog(null, "El campo Cantidad es obligatorio.",
                          "Añadir Producto", JOptionPane.INFORMATION_MESSAGE);    
             txtCantidadProducto.requestFocusInWindow();
        }        
         
        //si no se introdujo correctamente la fecha, lo indico en un mensaje
        else if (txtFechaProd.getText().equals("") == false)
        {
             if (validarFecha(txtFechaProd.getText()) == false)
             {
                 txtFechaProd.requestFocusInWindow();
                 JOptionPane.showMessageDialog(null, "Debe indicar una fecha correcta. Utilice el selector "
                        + "o siga el formato siguiente:\n\n               Día-Mes-Año HH:mm:ss (Ej: 15-01-2013 17:30:25)",
                          "Añadir Empleado", JOptionPane.INFORMATION_MESSAGE);   
             }
             else
             {
                 //Configuro el formato que quiero que muestre en el textfield
                 dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

                 try 
                 {
                     //Convierto la fecha que marca el textfield, y le doy el formato deseado
                     Date fechaDate = dateFormat.parse(txtFechaProd.getText()); 
                     //Configuro el calendario con la fecha almacenada
                     calendarioProductos.setDate(fechaDate);          
                     //Cambio el formato por defecto del jdatachooser para poder almacenarlo en MYSQL
                     calendarioProductos.setDateFormatString("yyyy-MM-dd HH:mm:ss"); 
                 } 
                 catch (ParseException ex) 
                 {
                    Logger.getLogger(Configuracion.class.getName()).log(Level.SEVERE, null, ex);
                 }
                 formularioCompleto = true;
             }
        } 
        else
        {
            formularioCompleto = true;
        } 
        
        if (formularioCompleto)
        {

            
            datoProd [0][0]="codigo";
            datoProd [1][0]="producto";
            datoProd [2][0]="proveedor";
            datoProd [3][0]="pvp";
            datoProd [4][0]="cantidad";
            datoProd [5][0]="fecha";
            datoProd [6][0]="identificador";
            datoProd [7][0]="id";

            dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        
            datoProd [0][1]=txtCodigoProducto.getText();
            datoProd [1][1]=txtNombreProducto.getText();
            datoProd [2][1]=cbProveedor.getSelectedItem().toString();
            datoProd [3][1]=txtPrecioProducto.getText();
            datoProd [4][1]=txtCantidadProducto.getText();
            datoProd [7][1]=txtIdProducto.getText();
            
            if (txtFechaProd.getText().equals(""))
            {
                datoProd [5][1] = "";
            }
            else
            {
                datoProd [5][1] = dateFormat.format(calendarioProductos.getDate()); //formateo la fecha que marca
            }   

            datoProd [6][1]=cbTipoProducto.getSelectedItem().toString();

            if (accion.equals("insertar"))
            {
                for(int j=0; j < datoProd.length; j++)
                {
                        if(datoProd[j][1].equals(""))
                        {                
                            datoProd[j][1]=null;
                            valores = valores + "," + datoProd[j][1];
                        }
                        else
                        {
                            valores = valores + ",'" + datoProd[j][1] + "'";
                        }
                        
                        campos = campos + ", " + datoProd[j][0];  
                }
                //elimino la ',' del principio, indicando que empiece desde el carácter 1
                valores = valores.substring(1); 
                campos = campos.substring(1); 

                resultado[0] = campos;
                
                resultado[1] = valores;
                
                
            }
            else if (accion.equals("modificar"))
            {
                for(int j=0; j < datoProd.length; j++)
                {
                        if(datoProd[j][1].equals(""))
                        {                
                            datoProd[j][1]=null;
                            valores = valores + datoProd[j][0] + "=" + datoProd[j][1] + ", ";
                        }
                        else
                        {
                            valores = valores + datoProd[j][0] + "='" + datoProd[j][1] + "', ";
                        }
                }        
                //elimino la ',' del final, indicando desde y hasta donde copiar la cadena

                valores = valores.substring(0,valores.length()-2); 

                resultado[0] = "";
                resultado[1] = valores;
            }     
            return resultado;
        }
        return null;
    }
    
    //compruebo que las cajas de texto no estén todas en blanco. A veces cuando entro en productos
    //y selecciono muy rápido una fila en la tabla, no se cargan los datos del registro en las cajas de texto
    //entonces tengo que controlarlo para no guardar o eliminar un producto incorrectamente
    private boolean comprobarTextFieldProd()
    {       
        if (txtIdProducto.getText().equals("") && txtNombreProducto.getText().equals("") &&
            cbProveedor.getSelectedItem().toString().equals("") && txtPrecioProducto.getText().equals("") &&
            txtCantidadProducto.getText().equals("") && txtFechaProd.getText().equals(""))
        {
            return true;
        }
        else
        {
            return false;
        }                        
    }
    
    //método para actualizar la tabla de productos
    private void actualizarTablaProd()
    {

        String tipo = botonSeleccionado(btnGrupoFil);

        if (txtBuscarProducto.getText().equals(""))
        {
            if (tipo.equals("Todos"))
            {
                where = " ORDER BY producto ASC";
            }          
            else
            {
                where = " WHERE Identificador LIKE '%" + botonSeleccionado(btnGrupoFil) +
                        " ORDER BY producto ASC";
            }                                
        }
        else
        {     
            if (tipo.equals("Todos"))
            {
                where = " WHERE " + cbBuscarProductos.getSelectedItem() + " LIKE '%" + txtBuscarProducto.getText() +
                        "%' ORDER BY producto ASC";
            }
            else
            {
                where = " WHERE " + cbBuscarProductos.getSelectedItem() + " LIKE '%" + txtBuscarProducto.getText() +
                        "%' AND Identificador LIKE '%" + botonSeleccionado(btnGrupoFil) + 
                        "%' ORDER BY producto ASC";
            }
        }
        crearTabla(where); 
        txtCoincidencias.setText(String.valueOf(modelo.getRowCount()));

    }  
    
    private void habilitarTextFieldProd()
    {
        txtIdProducto.setEnabled(true);
        txtCodigoProducto.setEnabled(true);
        txtNombreProducto.setEnabled(true);
        cbProveedor.setEnabled(true);
        txtPrecioProducto.setEnabled(true);
        txtCantidadProducto.setEnabled(true);
        txtFechaProd.setEnabled(true);
        cbTipoProducto.setEnabled(true);
        calendarioProductos.setEnabled(true);
        
    }
    
    //método para deshabilitar los campos de texto de Productos
    private void deshabilitarTextFieldProd()
    {        
        txtIdProducto.setEnabled(false);
        txtCodigoProducto.setEnabled(false);
        txtNombreProducto.setEnabled(false);
        cbProveedor.setEnabled(false);
        txtPrecioProducto.setEnabled(false);
        txtCantidadProducto.setEnabled(false);
        txtFechaProd.setEnabled(false);
        cbTipoProducto.setEnabled(false);
        calendarioProductos.setEnabled(false);        
    }
    
    //método para habilitar los botones del formulario Productos
    private void habilitarBotonesProd()
    {
        btnNuevoProd.setEnabled(true);
        btnGuardarProd.setEnabled(true);
        btnEliminarProd.setEnabled(true);
        btnCancelarProd.setEnabled(true);  
        btnFechaHoy.setEnabled(true);
    }
    
     //método para deshabilitar los botones del formulario Productos
    private void deshabilitarBotonesProd()
    {
        btnNuevoProd.setEnabled(false);
        btnGuardarProd.setEnabled(false);
        btnEliminarProd.setEnabled(false);
        btnCancelarProd.setEnabled(false);    
        btnAñadirAcesta.setEnabled(false);
        btnFechaHoy.setEnabled(false);
    } 
    
    private void navegarPorTabla()
    {
        int[] arr;
        accion = "modificar";
        java.util.Date fecha = null;
        
        //Configuro el formato de fecha que voy a dar luego
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
        
        //Asigno un formato de fecha deseado al calendario 
        calendarioProductos.setDateFormatString("dd-MM-yyyy HH:mm:ss");
        
        //almaceno la fila seleccionada en la tabla
        arr = tbProductos.getSelectedRows();
        txtCoincidencias.setText(Integer.toString(arr.length));

        //Si vengo de Caja (Opciones avanzadas) habilito el botón AñadirAcesta
        if (fromCaja)
        {                       
            btnAñadirAcesta.setEnabled(true);
        }
        
        //si no se ha seleccionado ninguna fila getSelectedRow() devuelve -1
        if(arr.length < 1)
        {
            JOptionPane.showMessageDialog(null, "No se ha seleccionado ninguna fila");
        }
        else
        {
            //almaceno el modelo de tabla que se está utilizando en la tabla real
            modelo = (DefaultTableModel) tbProductos.getModel();

            if (arr.length == 1)
            {
                //habilito todos los campos de texto y botones (excepto cancelar)
                habilitarTextFieldProd();
                habilitarBotonesProd();
                btnCancelarProd.setEnabled(false);


                    txtCodigoProducto.setText((String)tbProductos.getValueAt(arr[0], 0));                                     
                    txtNombreProducto.setText((String)tbProductos.getValueAt(arr[0], 1));
                    txtCantidadProducto.setText((String)tbProductos.getValueAt(arr[0], 2));
                    txtPrecioProducto.setText((String)tbProductos.getValueAt(arr[0], 3));
                    cbProveedor.setSelectedItem((String)tbProductos.getValueAt(arr[0], 4));                    
                    txtIdProducto.setText((String)tbProductos.getValueAt(arr[0], 7));   
                    
                    txtFechaProd.setText("");
                    
                    if (tbProductos.getValueAt(arr[0], 5) != null)
                    {
                        try 
                        {      
                            //Obtengo la fecha de la base de datos, y la formateo con el formato antes definido
                            fecha = dateformat.parse(tbProductos.getValueAt(arr[0], 5).toString());
                        } 
                        catch (ParseException ex) 
                        {
                           JOptionPane.showMessageDialog(null, ex);
                        }
                        calendarioProductos.setDate(fecha);
                        dateformat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss"); 
                        txtFechaProd.setText(dateformat.format(calendarioProductos.getDate()));                         
                    } 
                    else
                    {
                        calendarioProductos.setDate(null);
                    }
                    
                    //txtFechaProd.setText((String)tbProductos.getValueAt(arr[i], 6));
                    cbTipoProducto.setSelectedItem((String)tbProductos.getValueAt(arr[0], 6));
   
            }
            //si se han seleccionado más de 1 registro
            else
            {
                //habilito los botones (excepto el de guardar y Fecha de Hoy) y deshabilito campos texto
                habilitarBotonesProd();
                textFieldProdBlanco();
                deshabilitarTextFieldProd();
                btnGuardarProd.setEnabled(false);
                btnFechaHoy.setEnabled(false);
                txtCoincidencias.setText(String.valueOf(tbProductos.getSelectedRowCount()));
            }
        }        
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelInformacionProducto = new javax.swing.JPanel();
        txtIdProducto = new javax.swing.JTextField();
        lbIdProducto = new javax.swing.JLabel();
        txtNombreProducto = new javax.swing.JTextField();
        lbNombreProducto = new javax.swing.JLabel();
        lbProveedor = new javax.swing.JLabel();
        lbPrecioProducto = new javax.swing.JLabel();
        txtPrecioProducto = new javax.swing.JTextField();
        lbCantidadProducto = new javax.swing.JLabel();
        txtCantidadProducto = new javax.swing.JTextField();
        cbTipoProducto = new javax.swing.JComboBox();
        lbFechaEntrada = new javax.swing.JLabel();
        txtFechaProd = new javax.swing.JTextField();
        lbTipoProducto = new javax.swing.JLabel();
        btnGuardarProd = new javax.swing.JButton();
        btnNuevoProd = new javax.swing.JButton();
        btnCancelarProd = new javax.swing.JButton();
        btnEliminarProd = new javax.swing.JButton();
        lbCodigoProducto = new javax.swing.JLabel();
        txtCodigoProducto = new javax.swing.JTextField();
        cbProveedor = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        btnFechaHoy = new javax.swing.JButton();
        calendarioProductos = new com.toedter.calendar.JDateChooser();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        panelBuscarProductos1 = new javax.swing.JPanel();
        txtBuscarProducto = new javax.swing.JTextField();
        btnBuscarProducto = new javax.swing.JButton();
        lbCoincidencias = new javax.swing.JLabel();
        txtCoincidencias = new javax.swing.JTextField();
        cbBuscarProductos = new javax.swing.JComboBox();
        lbTipoBusq = new javax.swing.JLabel();
        lbTecleaCadena = new javax.swing.JLabel();
        panelFiltrarProd = new javax.swing.JPanel();
        rbFiltrarServicios = new javax.swing.JRadioButton();
        rbFiltrarTodos = new javax.swing.JRadioButton();
        rbFiltrarProductos = new javax.swing.JRadioButton();
        lbSelecFiltro = new javax.swing.JLabel();
        panelCarrito = new javax.swing.JPanel();
        lbAñadirCompra = new javax.swing.JLabel();
        btnAñadirAcesta = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbProductos = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1000, 755));
        setResizable(false);

        panelInformacionProducto.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(153, 153, 153)));
        panelInformacionProducto.setForeground(new java.awt.Color(51, 51, 51));

        txtIdProducto.setEditable(false);
        txtIdProducto.setDoubleBuffered(true);
        txtIdProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIdProductoActionPerformed(evt);
            }
        });

        lbIdProducto.setText("Id: ");

        txtNombreProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNombreProductoActionPerformed(evt);
            }
        });

        lbNombreProducto.setText("Producto: ");

        lbProveedor.setText("Proveedor: ");

        lbPrecioProducto.setText("Precio: ");

        txtPrecioProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPrecioProductoActionPerformed(evt);
            }
        });

        lbCantidadProducto.setText("Cantidad: ");

        txtCantidadProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCantidadProductoActionPerformed(evt);
            }
        });

        cbTipoProducto.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Servicio", "Producto" }));
        cbTipoProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbTipoProductoActionPerformed(evt);
            }
        });

        lbFechaEntrada.setText("Fecha entrada: ");

        txtFechaProd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFechaProdActionPerformed(evt);
            }
        });

        lbTipoProducto.setText("Tipo Producto: ");

        btnGuardarProd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/Guardar.png"))); // NOI18N
        btnGuardarProd.setText("Guardar");
        btnGuardarProd.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnGuardarProd.setPreferredSize(new java.awt.Dimension(75, 57));
        btnGuardarProd.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnGuardarProd.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnGuardarProd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarProdActionPerformed(evt);
            }
        });

        btnNuevoProd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/NuevoRegistro.png"))); // NOI18N
        btnNuevoProd.setText("Nuevo");
        btnNuevoProd.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnNuevoProd.setPreferredSize(new java.awt.Dimension(75, 57));
        btnNuevoProd.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnNuevoProd.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnNuevoProd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevoProdActionPerformed(evt);
            }
        });

        btnCancelarProd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/Cancelar.png"))); // NOI18N
        btnCancelarProd.setText("Cancelar");
        btnCancelarProd.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCancelarProd.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnCancelarProd.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnCancelarProd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarProdActionPerformed(evt);
            }
        });

        btnEliminarProd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/papelera.png"))); // NOI18N
        btnEliminarProd.setText("Eliminar");
        btnEliminarProd.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEliminarProd.setPreferredSize(new java.awt.Dimension(75, 57));
        btnEliminarProd.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnEliminarProd.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnEliminarProd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarProdActionPerformed(evt);
            }
        });

        lbCodigoProducto.setText("Código: ");

        txtCodigoProducto.setDoubleBuffered(true);
        txtCodigoProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCodigoProductoActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel1.setText("*");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel2.setText("*");

        btnFechaHoy.setText("Fecha de hoy");
        btnFechaHoy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFechaHoyActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel3.setText("*");

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel4.setText("*");

        javax.swing.GroupLayout panelInformacionProductoLayout = new javax.swing.GroupLayout(panelInformacionProducto);
        panelInformacionProducto.setLayout(panelInformacionProductoLayout);
        panelInformacionProductoLayout.setHorizontalGroup(
            panelInformacionProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInformacionProductoLayout.createSequentialGroup()
                .addGroup(panelInformacionProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelInformacionProductoLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnNuevoProd, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnGuardarProd, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEliminarProd, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCancelarProd, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelInformacionProductoLayout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addGroup(panelInformacionProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelInformacionProductoLayout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(lbTipoProducto))
                            .addGroup(panelInformacionProductoLayout.createSequentialGroup()
                                .addGroup(panelInformacionProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(lbNombreProducto)
                                    .addComponent(lbPrecioProducto)
                                    .addComponent(lbCantidadProducto)
                                    .addComponent(lbFechaEntrada)
                                    .addComponent(lbProveedor)
                                    .addComponent(lbCodigoProducto)
                                    .addComponent(lbIdProducto))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panelInformacionProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(panelInformacionProductoLayout.createSequentialGroup()
                                        .addComponent(txtNombreProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel1))
                                    .addGroup(panelInformacionProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(txtIdProducto, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE)
                                        .addComponent(txtCodigoProducto, javax.swing.GroupLayout.Alignment.LEADING))
                                    .addGroup(panelInformacionProductoLayout.createSequentialGroup()
                                        .addGroup(panelInformacionProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(txtCantidadProducto, javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(cbTipoProducto, javax.swing.GroupLayout.Alignment.LEADING, 0, 133, Short.MAX_VALUE)
                                            .addComponent(txtFechaProd, javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtPrecioProducto, javax.swing.GroupLayout.Alignment.LEADING))
                                        .addGroup(panelInformacionProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(panelInformacionProductoLayout.createSequentialGroup()
                                                .addGap(1, 1, 1)
                                                .addComponent(calendarioProductos, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnFechaHoy))
                                            .addGroup(panelInformacionProductoLayout.createSequentialGroup()
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(panelInformacionProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(jLabel3)))))
                                    .addGroup(panelInformacionProductoLayout.createSequentialGroup()
                                        .addComponent(cbProveedor, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel2)))))))
                .addContainerGap())
        );
        panelInformacionProductoLayout.setVerticalGroup(
            panelInformacionProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelInformacionProductoLayout.createSequentialGroup()
                .addContainerGap(18, Short.MAX_VALUE)
                .addGroup(panelInformacionProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtIdProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbIdProducto))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelInformacionProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCodigoProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbCodigoProducto))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelInformacionProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbNombreProducto)
                    .addComponent(txtNombreProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelInformacionProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbProveedor)
                    .addComponent(cbProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelInformacionProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelInformacionProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lbPrecioProducto)
                        .addComponent(txtPrecioProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelInformacionProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panelInformacionProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtCantidadProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lbCantidadProducto)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelInformacionProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(panelInformacionProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtFechaProd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnFechaHoy, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lbFechaEntrada))
                    .addComponent(calendarioProductos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelInformacionProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbTipoProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbTipoProducto))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelInformacionProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnGuardarProd, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnNuevoProd, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCancelarProd, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEliminarProd, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(17, 17, 17))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelInformacionProductoLayout.createSequentialGroup()
                .addGap(76, 76, 76)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelBuscarProductos1.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(153, 153, 153)));
        panelBuscarProductos1.setPreferredSize(new java.awt.Dimension(400, 110));

        txtBuscarProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBuscarProductoActionPerformed(evt);
            }
        });
        txtBuscarProducto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtBuscarProductoKeyPressed(evt);
            }
        });

        btnBuscarProducto.setText("Buscar");
        btnBuscarProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarProductoActionPerformed(evt);
            }
        });

        lbCoincidencias.setText("Total encontrados: ");

        txtCoincidencias.setEnabled(false);

        cbBuscarProductos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cbBuscarProductosMouseClicked(evt);
            }
        });
        cbBuscarProductos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbBuscarProductosActionPerformed(evt);
            }
        });
        cbBuscarProductos.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cbBuscarProductosFocusGained(evt);
            }
        });

        lbTipoBusq.setText("Buscar por: ");

        lbTecleaCadena.setText("Teclear cadena: ");

        javax.swing.GroupLayout panelBuscarProductos1Layout = new javax.swing.GroupLayout(panelBuscarProductos1);
        panelBuscarProductos1.setLayout(panelBuscarProductos1Layout);
        panelBuscarProductos1Layout.setHorizontalGroup(
            panelBuscarProductos1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelBuscarProductos1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelBuscarProductos1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lbCoincidencias)
                    .addComponent(lbTipoBusq)
                    .addComponent(lbTecleaCadena))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelBuscarProductos1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cbBuscarProductos, 0, 179, Short.MAX_VALUE)
                    .addComponent(txtBuscarProducto)
                    .addComponent(txtCoincidencias, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnBuscarProducto)
                .addGap(22, 22, 22))
        );
        panelBuscarProductos1Layout.setVerticalGroup(
            panelBuscarProductos1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBuscarProductos1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(panelBuscarProductos1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbBuscarProductos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbTipoBusq))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelBuscarProductos1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelBuscarProductos1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtBuscarProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lbTecleaCadena))
                    .addComponent(btnBuscarProducto))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelBuscarProductos1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbCoincidencias)
                    .addComponent(txtCoincidencias, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        panelFiltrarProd.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(153, 153, 153)));

        rbFiltrarServicios.setText("Servicio");
        rbFiltrarServicios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbFiltrarServiciosActionPerformed(evt);
            }
        });
        rbFiltrarServicios.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                rbFiltrarServiciosFocusLost(evt);
            }
        });

        rbFiltrarTodos.setSelected(true);
        rbFiltrarTodos.setText("Todos");
        rbFiltrarTodos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbFiltrarTodosActionPerformed(evt);
            }
        });

        rbFiltrarProductos.setText("Producto");
        rbFiltrarProductos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbFiltrarProductosActionPerformed(evt);
            }
        });
        rbFiltrarProductos.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                rbFiltrarProductosFocusLost(evt);
            }
        });

        lbSelecFiltro.setText("Selecciona filtro: ");

        javax.swing.GroupLayout panelFiltrarProdLayout = new javax.swing.GroupLayout(panelFiltrarProd);
        panelFiltrarProd.setLayout(panelFiltrarProdLayout);
        panelFiltrarProdLayout.setHorizontalGroup(
            panelFiltrarProdLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFiltrarProdLayout.createSequentialGroup()
                .addGap(55, 55, 55)
                .addComponent(lbSelecFiltro)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rbFiltrarProductos)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(rbFiltrarServicios)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(rbFiltrarTodos)
                .addContainerGap(70, Short.MAX_VALUE))
        );
        panelFiltrarProdLayout.setVerticalGroup(
            panelFiltrarProdLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFiltrarProdLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(panelFiltrarProdLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbFiltrarProductos)
                    .addComponent(rbFiltrarServicios)
                    .addComponent(rbFiltrarTodos)
                    .addComponent(lbSelecFiltro))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelCarrito.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(153, 153, 153)));

        lbAñadirCompra.setText("Añadir Producto a la cuenta: ");
        lbAñadirCompra.setToolTipText("");

        btnAñadirAcesta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/cesta.png"))); // NOI18N
        btnAñadirAcesta.setEnabled(false);
        btnAñadirAcesta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAñadirAcestaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelCarritoLayout = new javax.swing.GroupLayout(panelCarrito);
        panelCarrito.setLayout(panelCarritoLayout);
        panelCarritoLayout.setHorizontalGroup(
            panelCarritoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCarritoLayout.createSequentialGroup()
                .addGap(85, 85, 85)
                .addComponent(lbAñadirCompra)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAñadirAcesta)
                .addContainerGap(86, Short.MAX_VALUE))
        );
        panelCarritoLayout.setVerticalGroup(
            panelCarritoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCarritoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelCarritoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnAñadirAcesta)
                    .addGroup(panelCarritoLayout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(lbAñadirCompra)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jScrollPane2.setAutoscrolls(true);
        jScrollPane2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jScrollPane2MouseClicked(evt);
            }
        });

        tbProductos.setAutoCreateRowSorter(true);
        tbProductos.setFont(new java.awt.Font("Arial Unicode MS", 0, 12)); // NOI18N
        tbProductos.setModel(new javax.swing.table.DefaultTableModel(
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
        tbProductos.setRowHeight(18);
        tbProductos.getTableHeader().setReorderingAllowed(false);
        tbProductos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tbProductosMouseReleased(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbProductosMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                tbProductosMouseEntered(evt);
            }
        });
        tbProductos.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                tbProductosPropertyChange(evt);
            }
        });
        tbProductos.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tbProductosKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                tbProductosKeyTyped(evt);
            }
        });
        jScrollPane2.setViewportView(tbProductos);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(panelFiltrarProd, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(panelInformacionProducto, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(panelBuscarProductos1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(panelCarrito, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panelInformacionProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelBuscarProductos1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelFiltrarProd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelCarrito, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtIdProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIdProductoActionPerformed
        txtIdProducto.transferFocus();//permite que al pulsar Enter pase al campo siguiente
    }//GEN-LAST:event_txtIdProductoActionPerformed

    private void txtNombreProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNombreProductoActionPerformed
        txtNombreProducto.transferFocus();//permite que al pulsar Enter pase al campo siguiente
    }//GEN-LAST:event_txtNombreProductoActionPerformed

    private void txtPrecioProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPrecioProductoActionPerformed
        txtPrecioProducto.transferFocus();//permite que al pulsar Enter pase al campo siguiente
    }//GEN-LAST:event_txtPrecioProductoActionPerformed

    private void txtCantidadProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCantidadProductoActionPerformed
        txtCantidadProducto.transferFocus();//permite que al pulsar Enter pase al campo siguiente
    }//GEN-LAST:event_txtCantidadProductoActionPerformed

    private void cbTipoProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbTipoProductoActionPerformed
        cbTipoProducto.transferFocus();//permite que al pulsar Enter pase al campo siguiente
    }//GEN-LAST:event_cbTipoProductoActionPerformed

    private void txtFechaProdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFechaProdActionPerformed
        txtFechaProd.transferFocus();//permite que al pulsar Enter pase al campo siguiente
    }//GEN-LAST:event_txtFechaProdActionPerformed

    private void btnGuardarProdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarProdActionPerformed

        boolean res;
        int filasel;
        String[] camposValores;
        consulta = new Conexion();
        String where;

        camposValores = recogerDatosProductos();

        if (camposValores != null)
        {
            switch (accion)
            {
                case "insertar":
                    
                    res = consulta.insert("productos", camposValores[0], camposValores[1]);
                    if (res == true)
                    {
                        JOptionPane.showMessageDialog(null, "La inserción se ha realizado correctamente",
                            "Añadir Producto", JOptionPane.INFORMATION_MESSAGE);

                        actualizarTablaProd();
                        deshabilitarBotonesProd();
                        btnNuevoProd.setEnabled(true);
                        deshabilitarTextFieldProd();
                    }

                    break;
                    
                case "modificar":
                    
                    if (tbProductos.getSelectedRowCount()>1 || tbProductos.getSelectedRowCount()<1)
                    {
                        JOptionPane.showMessageDialog(null, "Debe seleccionar un registro",
                            "Modificar Producto", JOptionPane.INFORMATION_MESSAGE);
                    }
                    else
                    {
                        //localizo la fila seleccionada en la tabla
                        filasel = tbProductos.getSelectedRow();
                        
                        int respuesta = JOptionPane.showConfirmDialog(null,
                            "Se va ha modificar el producto con id " + (String)tbProductos.getValueAt(filasel, 7) + " - " + txtNombreProducto.getText() + 
                                ", ¿Está seguro?", "Modificar producto", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                        if (respuesta == 0)
                        {
                            if(filasel == -1 || comprobarTextFieldProd() == true)
                            {
                                JOptionPane.showMessageDialog(null, "No se ha seleccionado ningun registro",
                                    "Modificar producto", JOptionPane.INFORMATION_MESSAGE);
                            }

                            else
                            {
                                
                                //almaceno el id de la fila seleccionada
                                where = "id='" + (String)tbProductos.getValueAt(filasel, 7) + "'";
    
                                //llamo al método Update de la clase conexión y le envío tabla, camposYvalores, condición
                                consulta.update("productos", camposValores[1], where);

                                JOptionPane.showMessageDialog(null, "Producto modificado correctamente.",
                                    "Modificar producto", JOptionPane.INFORMATION_MESSAGE);

                                actualizarTablaProd();
                                deshabilitarBotonesProd();
                                btnNuevoProd.setEnabled(true);
                                deshabilitarTextFieldProd();
                            }
                        }
                    }
            }
        }
    }//GEN-LAST:event_btnGuardarProdActionPerformed

    private void btnNuevoProdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoProdActionPerformed

        accion="insertar";
        txtCodigoProducto.requestFocusInWindow();
        //Hacer formateador de la fecha, se le pasa el formato en que se quiere obtener la fecha.
        
        deshabilitarBotonesProd();
        btnGuardarProd.setEnabled(true);
        btnCancelarProd.setEnabled(true);
        btnFechaHoy.setEnabled(true);
        
        habilitarTextFieldProd();        
        textFieldProdBlanco();
        
        Date fechaActual = new Date(); 
        SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss"); 
        fecha = dateformat.format(fechaActual);        
        txtFechaProd.setText(fecha);        
    }//GEN-LAST:event_btnNuevoProdActionPerformed

    private void btnCancelarProdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarProdActionPerformed

        deshabilitarBotonesProd();
        btnNuevoProd.setEnabled(true);
        deshabilitarTextFieldProd();
        textFieldProdBlanco();

        actualizarTablaProd();
      /*  consulta = new Conexion();
        modelo=consulta.getProductos(null);
        tbProductos.setModel(modelo);*/
    }//GEN-LAST:event_btnCancelarProdActionPerformed

    private void btnEliminarProdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarProdActionPerformed

        String where;
        String prodBorrar="";
        int[] arr;

        arr = tbProductos.getSelectedRows();

        if(arr.length == 0)
        {
            btnEliminarProd.setEnabled(false);

            JOptionPane.showMessageDialog(null, "Debe seleccionar un registro",
                "Eliminar productos", JOptionPane.INFORMATION_MESSAGE);

        }
        else
        {
            txtCoincidencias.setText(Integer.toString(arr.length));
            for (int i=0; i<arr.length; i++)
            {
                prodBorrar = prodBorrar + " id " + 
                        tbProductos.getValueAt(arr[i], 7) + " - " + tbProductos.getValueAt(arr[i], 1) + "\n";
            }

            int respuesta = JOptionPane.showConfirmDialog(null,
                "Se van a borrar los siguientes " + arr.length + " productos: \n\n"
                + prodBorrar, "Eliminar productos", JOptionPane.WARNING_MESSAGE);

            if (respuesta==0)
            {
                //deshabilito todos los campos de texto y botones (excepto el de Nuevo)
                deshabilitarTextFieldProd();
                deshabilitarBotonesProd();
                btnNuevoProd.setEnabled(true);
                textFieldProdBlanco();

                int i=0;
                consulta = new Conexion();
                //ResultSet rs;
                //rs=consulta.buscarRegistro(tblProductos);

                while (arr.length>i)
                {
                    where = "id='"+(String) tbProductos.getValueAt(arr[i], 7)+"'";
                    consulta.eliminar("productos", where);
                    i++;
                }

                actualizarTablaProd();
            }
        }
    }//GEN-LAST:event_btnEliminarProdActionPerformed

    private void txtBuscarProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarProductoActionPerformed

    }//GEN-LAST:event_txtBuscarProductoActionPerformed

    private void txtBuscarProductoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarProductoKeyPressed

        int code = evt.getKeyCode(); //almacena el código numerico de la tecla
        char caracter = evt.getKeyChar(); //almacena el caracter (de momento no lo uso)

        //si pulso la tecla ENTER realizo la misma función que el botón "btnBuscar"
        if(code == KeyEvent.VK_ENTER)
        {
            String tipo = botonSeleccionado(btnGrupoFil);

            if (txtBuscarProducto.getText().equals(""))
            {
                if (tipo.equals("Todos"))
                {
                    where = " ORDER BY producto ASC";
                }          
                else
                {
                    where = " WHERE Identificador LIKE '%" + botonSeleccionado(btnGrupoFil) +
                            " ORDER BY producto ASC";
                }                                
            }
            else
            {     
                if (tipo.equals("Todos"))
                {
                    where = " WHERE " + cbBuscarProductos.getSelectedItem() + " LIKE '%" + txtBuscarProducto.getText() +
                            "%' ORDER BY producto ASC";
                }
                else
                {
                    where = " WHERE " + cbBuscarProductos.getSelectedItem() + " LIKE '%" + txtBuscarProducto.getText() +
                            "%' AND Identificador LIKE '%" + botonSeleccionado(btnGrupoFil) + 
                            "%' ORDER BY producto ASC";
                }
            }   
            
            crearTabla(where);
            
            txtCoincidencias.setText(String.valueOf(modelo.getRowCount()));
            
            //textFieldProdBlanco();

            deshabilitarTextFieldProd();
            deshabilitarBotonesProd();

            btnNuevoProd.setEnabled(true);            
        }
    }//GEN-LAST:event_txtBuscarProductoKeyPressed

    private void btnBuscarProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarProductoActionPerformed


        String tipo = botonSeleccionado(btnGrupoFil);        
        
        if (txtBuscarProducto.getText().equals(""))
        {
            if (tipo.equals("Todos"))
            {
                where = " ORDER BY producto ASC";
            }          
            else
            {
                where = " WHERE Identificador LIKE '%" + botonSeleccionado(btnGrupoFil) +
                        " ORDER BY producto ASC";
            }                                
        }
        else
        {     
            if (tipo.equals("Todos"))
            {
                where = " WHERE " + cbBuscarProductos.getSelectedItem() + " LIKE '%" + txtBuscarProducto.getText() +
                        "%' ORDER BY producto ASC";
            }
            else
            {
                where = " WHERE " + cbBuscarProductos.getSelectedItem() + " LIKE '%" + txtBuscarProducto.getText() +
                        "%' AND Identificador LIKE '%" + botonSeleccionado(btnGrupoFil) + 
                        "%' ORDER BY producto ASC";
            }
        }
        crearTabla(where);        
        
        txtCoincidencias.setText(String.valueOf(modelo.getRowCount()));

        //textFieldProdBlanco();
        
        deshabilitarTextFieldProd();
        deshabilitarBotonesProd();
        
        btnNuevoProd.setEnabled(true);

    }//GEN-LAST:event_btnBuscarProductoActionPerformed

    private void cbBuscarProductosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cbBuscarProductosMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_cbBuscarProductosMouseClicked

    private void cbBuscarProductosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbBuscarProductosActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbBuscarProductosActionPerformed

    private void cbBuscarProductosFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cbBuscarProductosFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_cbBuscarProductosFocusGained

    private void rbFiltrarServiciosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbFiltrarServiciosActionPerformed
        
        where = " WHERE Identificador LIKE '%" + botonSeleccionado(btnGrupoFil) + "%' AND " +
        cbBuscarProductos.getSelectedItem() + " LIKE '%" + txtBuscarProducto.getText() + "%'"
                + " ORDER BY producto ASC";

        crearTabla(where);
        txtCoincidencias.setText(String.valueOf(modelo.getRowCount()));
    }//GEN-LAST:event_rbFiltrarServiciosActionPerformed

    private void rbFiltrarServiciosFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_rbFiltrarServiciosFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_rbFiltrarServiciosFocusLost

    private void rbFiltrarTodosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbFiltrarTodosActionPerformed
                      
        where = " WHERE " + cbBuscarProductos.getSelectedItem() + " LIKE '%" + txtBuscarProducto.getText() + "%'" +
                " ORDER BY producto ASC";

        crearTabla(where);
        txtCoincidencias.setText(String.valueOf(modelo.getRowCount()));
    }//GEN-LAST:event_rbFiltrarTodosActionPerformed

    private void rbFiltrarProductosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbFiltrarProductosActionPerformed
        
        where = " WHERE Identificador LIKE '%" + botonSeleccionado(btnGrupoFil) + "%' AND " +
        cbBuscarProductos.getSelectedItem() + " LIKE '%" + txtBuscarProducto.getText() + "%'"
                + " ORDER BY producto ASC";

        crearTabla(where);
        txtCoincidencias.setText(String.valueOf(modelo.getRowCount()));
    }//GEN-LAST:event_rbFiltrarProductosActionPerformed

    private void rbFiltrarProductosFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_rbFiltrarProductosFocusLost

    }//GEN-LAST:event_rbFiltrarProductosFocusLost

    private void tbProductosMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbProductosMouseReleased
        navegarPorTabla();
    }//GEN-LAST:event_tbProductosMouseReleased

    private void tbProductosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbProductosMouseClicked
        tbProductos.requestFocusInWindow();
    }//GEN-LAST:event_tbProductosMouseClicked

    private void tbProductosMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbProductosMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_tbProductosMouseEntered

    private void tbProductosPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_tbProductosPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_tbProductosPropertyChange

    private void btnAñadirAcestaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAñadirAcestaActionPerformed

        int[] rows = tbProductos.getSelectedRows();
        int fila = 0;
        String[] registro = new String[5];
        
        for (int i=0; rows.length > i; i++)
        {
            if (tbProductos.getValueAt(rows[i], 2) != null)
            {            

                /* alamaceno el id y la cantidad del producto para luego modificarla en la base de datos
                Al no saber el tamaño del array por ser dinámico, declaro un arrayList bidimensional.
                Cada vez que se elija un producto, instancio un nuevo array dentro del arriba declarado.
                De este modo al ir asignando valores, aumenta automáticamente el índice del arrayList.
                cantProd.get(fila) muestra el contenido de las columnas de una fila. 
                cantProd.get(fila).get(0) muestra la columna 1, y cantProd.get(fila).get(1) la columna 2 */

                Caja.cantProd.add(new ArrayList<Integer>(2)); //Añado 2 columnas en cada fila del array

                Caja.cantProd.get(fila).add(Integer.parseInt((String) tbProductos.getValueAt(rows[i], 7))); //cantProd[0][0] id producto
                Caja.cantProd.get(fila).add(Integer.parseInt((String) tbProductos.getValueAt(rows[i], 2))); //cantProd[0][1] cantidad producto

                fila++;                  
            }

            registro[0] = tbProductos.getValueAt(rows[i], 7).toString(); //id del producto
            registro[1] = "1"; //cantidad del producto 
            registro[2] = tbProductos.getValueAt(rows[i], 1).toString(); //nombre del producto
            registro[3] = tbProductos.getValueAt(rows[i], 3).toString(); //precio del producto 
            registro[4] = tbProductos.getValueAt(rows[i], 3).toString(); //precio total                                       

            Caja.modelo.addRow(registro);

            Caja.tbCompra.setModel(Caja.modelo);

            Caja.total = Caja.total.add(BigDecimal.valueOf(Double.parseDouble( tbProductos.getValueAt(rows[i], 3).toString() )));         

            Caja.txtTotal.setText(String.valueOf(Caja.total));   
            
            JOptionPane.showMessageDialog(null, "Se ha añadido el siguiente producto: \n                    " 
                        + tbProductos.getValueAt(rows[i], 1), "Producto añadido", JOptionPane.INFORMATION_MESSAGE);            
        }
        //Cuando pulso AñadirAcesta habilito el botón de Cobrar
        Caja.btnCobrar.setEnabled(true);
    }//GEN-LAST:event_btnAñadirAcestaActionPerformed

    private void txtCodigoProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodigoProductoActionPerformed

        txtCodigoProducto.transferFocus();
    }//GEN-LAST:event_txtCodigoProductoActionPerformed

    private void btnFechaHoyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFechaHoyActionPerformed
 
        calendarioProductos.setDate(new Date());
    }//GEN-LAST:event_btnFechaHoyActionPerformed

    private void jScrollPane2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jScrollPane2MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jScrollPane2MouseClicked

    private void tbProductosKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbProductosKeyTyped
        
    }//GEN-LAST:event_tbProductosKeyTyped

    private void tbProductosKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbProductosKeyReleased
        navegarPorTabla();
        tbProductos.requestFocusInWindow();
    }//GEN-LAST:event_tbProductosKeyReleased

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
            java.util.logging.Logger.getLogger(Productos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Productos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Productos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Productos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Productos dialog = new Productos(new javax.swing.JFrame(), true);
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
    public static javax.swing.JButton btnAñadirAcesta;
    private javax.swing.JButton btnBuscarProducto;
    private javax.swing.JButton btnCancelarProd;
    private javax.swing.JButton btnEliminarProd;
    private javax.swing.JButton btnFechaHoy;
    private javax.swing.JButton btnGuardarProd;
    private javax.swing.JButton btnNuevoProd;
    private com.toedter.calendar.JDateChooser calendarioProductos;
    private javax.swing.JComboBox cbBuscarProductos;
    private javax.swing.JComboBox cbProveedor;
    private javax.swing.JComboBox cbTipoProducto;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lbAñadirCompra;
    private javax.swing.JLabel lbCantidadProducto;
    private javax.swing.JLabel lbCodigoProducto;
    private javax.swing.JLabel lbCoincidencias;
    private javax.swing.JLabel lbFechaEntrada;
    private javax.swing.JLabel lbIdProducto;
    private javax.swing.JLabel lbNombreProducto;
    private javax.swing.JLabel lbPrecioProducto;
    private javax.swing.JLabel lbProveedor;
    private javax.swing.JLabel lbSelecFiltro;
    private javax.swing.JLabel lbTecleaCadena;
    private javax.swing.JLabel lbTipoBusq;
    private javax.swing.JLabel lbTipoProducto;
    private javax.swing.JPanel panelBuscarProductos1;
    private javax.swing.JPanel panelCarrito;
    private javax.swing.JPanel panelFiltrarProd;
    private javax.swing.JPanel panelInformacionProducto;
    private javax.swing.JRadioButton rbFiltrarProductos;
    private javax.swing.JRadioButton rbFiltrarServicios;
    private javax.swing.JRadioButton rbFiltrarTodos;
    private javax.swing.JTable tbProductos;
    private javax.swing.JTextField txtBuscarProducto;
    private javax.swing.JTextField txtCantidadProducto;
    private javax.swing.JTextField txtCodigoProducto;
    private javax.swing.JTextField txtCoincidencias;
    private javax.swing.JTextField txtFechaProd;
    private javax.swing.JTextField txtIdProducto;
    private javax.swing.JTextField txtNombreProducto;
    private javax.swing.JTextField txtPrecioProducto;
    // End of variables declaration//GEN-END:variables
}
