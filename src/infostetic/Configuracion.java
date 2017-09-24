/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package infostetic;

import java.beans.PropertyChangeEvent;
import java.io.File;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Javi
 */
public class Configuracion extends javax.swing.JDialog 
{


    Conexion consulta;
    ResultSet rs;
    DefaultTableModel modelo;
    DefaultTableModel modelo2;
    DefaultTableModel modelo3;
    String accion = "";
    ButtonGroup btnGrupoPagado;
    Process proc;

    public Configuracion(java.awt.Frame parent, boolean modal) 
    {
        super(parent, modal);
        initComponents();
        
        btnGrupoPagado = new ButtonGroup();
        btnGrupoPagado.add(rbSi);
        btnGrupoPagado.add(rbNo);

        cbCargo.addItem("administrador");
        cbCargo.addItem("empleado");
        cbCargo.setSelectedIndex(-1);
        
        //deshabilito todos los botones menos "Nuevo"
        deshabilitarBotonesProveedor();
        deshabilitarBotonesFamilia();
        deshabilitarBotonesEmpleados();
        
        btnNuevaFamilia.setEnabled(true); 
        btnNuevoProveedor.setEnabled(true); 
        btnNuevoEmpleado.setEnabled(true); 
        
        txtProveedor.setEditable(false);
        txtFamilia.setEditable(false);

        consulta = new Conexion();
        
        deshabilitarTextFieldsPago();
        deshabilitarTextFieldsEmpleados();
        
        crearTablaProveedores();    
        crearTablaFamilia();    
        crearTablaPagoProveedores();
        crearTablaDatosEmpleados();

        jDateChooser.addPropertyChangeListener(new java.beans.PropertyChangeListener() 
        {
            public void propertyChange(java.beans.PropertyChangeEvent evt) 
            {
                jDateChooserPropertyChange(evt);
            }

            private void jDateChooserPropertyChange(PropertyChangeEvent evt) 
            {
                if (jDateChooser.getDate() != null)
                {                
                        SimpleDateFormat dateformat;         
                        String fecha = null;

                        dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
                        jDateChooser.setDateFormatString("dd-MM-yyyy HH:mm:ss");  

                        fecha = dateformat.format(jDateChooser.getDate());
                        try 
                        {
                            jDateChooser.setDate(dateformat.parse(fecha));
                        } 
                        catch (ParseException ex) 
                        {
                            Logger.getLogger(Configuracion.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
         });   

        calendarioEmpleados.addPropertyChangeListener(new java.beans.PropertyChangeListener() 
        {
            public void propertyChange(java.beans.PropertyChangeEvent evt) 
            {
                calendarioEmpleadosPropertyChange(evt);
            }

            private void calendarioEmpleadosPropertyChange(PropertyChangeEvent evt) 
            {
                if (calendarioEmpleados.getDate() != null)
                {                
                        SimpleDateFormat dateformat;         
                        String fecha = null;

                        dateformat = new SimpleDateFormat("dd-MM-yyyy"); 
                        calendarioEmpleados.setDateFormatString("dd-MM-yyyy");  
                        
                        txtFechaNac.setText(dateformat.format(calendarioEmpleados.getDate()));
                        
                        dateformat = new SimpleDateFormat("yyyy-MM-dd"); 
                        fecha = dateformat.format(calendarioEmpleados.getDate());                                                
                        try 
                        {                            
                            calendarioEmpleados.setDate(dateformat.parse(fecha));                            
                        } 
                        catch (ParseException ex) 
                        {
                            Logger.getLogger(Configuracion.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
         });

    }
              
    private void crearTablaProveedores()
    {
        String[] campos = {"Id", "Proveedor"};
        
        modelo = consulta.selectModelo("proveedores", "id, proveedor", " ORDER BY proveedor", campos);
        
        tbProveedores.setModel(modelo); 
                  
        tbProveedores.getColumnModel().getColumn(0).setPreferredWidth(20);   
        tbProveedores.getColumnModel().getColumn(1).setPreferredWidth(550);         
    }
    
    private void crearTablaFamilia()
    {
        String[] campos = {"Id", "Tipo servicio"};
        
        modelo2 = consulta.selectModelo("familia_servicios", "id, tipo_servicio", " ORDER BY tipo_servicio", campos);
        
        tbFamilia.setModel(modelo2); 
                  
        tbFamilia.getColumnModel().getColumn(0).setPreferredWidth(20);   
        tbFamilia.getColumnModel().getColumn(1).setPreferredWidth(550);         
    }    

    private void crearTablaPagoProveedores()
    {
        String[] campos = {"Id", "Nº Albaran / Factura", "Proveedor", "Descripción",
                           "Total", "Entregado", "Fecha del pago", "Forma de pago", "Pagado (Si/No)"};
        
        String fields = "id, numero_albaran, proveedor, descripcion, total, "
                      + "cantidad_entregada, fecha_pago, forma_pago, pagado_si_no";
        
        modelo = consulta.selectModelo("pago_proveedores", fields, " ORDER BY fecha_pago", campos);
        
        tbPagoProveedores.setModel(modelo); 
                  
        tbPagoProveedores.getColumnModel().getColumn(0).setPreferredWidth(20);  //id 
        tbPagoProveedores.getColumnModel().getColumn(1).setPreferredWidth(110);  //albaran  
        tbPagoProveedores.getColumnModel().getColumn(2).setPreferredWidth(130); //proveedor  
        tbPagoProveedores.getColumnModel().getColumn(3).setPreferredWidth(200); //descripcion
        tbPagoProveedores.getColumnModel().getColumn(4).setPreferredWidth(50);  //total 
        tbPagoProveedores.getColumnModel().getColumn(5).setPreferredWidth(50);  //Entregado 
        tbPagoProveedores.getColumnModel().getColumn(6).setPreferredWidth(110);  //fecha
        tbPagoProveedores.getColumnModel().getColumn(7).setPreferredWidth(60);  //forma pago 
        tbPagoProveedores.getColumnModel().getColumn(8).setPreferredWidth(30);  //pagado         
    } 
    
    private void crearTablaDatosEmpleados()
    {
        String[] campos = {
                           "Id", "Dni", "Nombre", "Apellidos", "Dirección", "Teléfono fijo",
                           "Teléfono móvil", "E-mail", "Fecha nacimiento", "Contraseña", "Cargo"
                          };
        
        String fields = "id, dni, nombre, apellidos, direccion, telefono_fijo, telefono_movil, "
                      + "email, fecha_nacimiento, password, cargo";
        
        modelo = consulta.selectModelo("personal", fields, " ORDER BY nombre", campos);
        
        tbDatosEmpleados.setModel(modelo); 
                  
        tbDatosEmpleados.getColumnModel().getColumn(0).setPreferredWidth(20);  //id 
        tbDatosEmpleados.getColumnModel().getColumn(1).setPreferredWidth(50);  //dni  
        tbDatosEmpleados.getColumnModel().getColumn(2).setPreferredWidth(100); //nombre  
        tbDatosEmpleados.getColumnModel().getColumn(3).setPreferredWidth(150); //apellidos
        tbDatosEmpleados.getColumnModel().getColumn(4).setPreferredWidth(150);  //direccion 
        tbDatosEmpleados.getColumnModel().getColumn(5).setPreferredWidth(50);  //telefono_fijo 
        tbDatosEmpleados.getColumnModel().getColumn(6).setPreferredWidth(50);  //telefono_movil
        tbDatosEmpleados.getColumnModel().getColumn(7).setPreferredWidth(100);  //email 
        tbDatosEmpleados.getColumnModel().getColumn(8).setPreferredWidth(50);  //fecha_nacimiento         
        tbDatosEmpleados.getColumnModel().getColumn(9).setPreferredWidth(50);  //contraseña
        tbDatosEmpleados.getColumnModel().getColumn(10).setPreferredWidth(50);  //cargo
    }     
    
     //método para deshabilitar los botones del formulario Proveedores
    private void deshabilitarBotonesProveedor()
    {
        btnNuevoProveedor.setEnabled(false);
        btnEliminarProveedor.setEnabled(false);
        btnGuardarProveedor.setEnabled(false);
        btnCancelarProveedor.setEnabled(false);    
    } 
    
     //método para deshabilitar los botones del formulario Familia
    private void deshabilitarBotonesFamilia()
    {
        btnNuevaFamilia.setEnabled(false);
        btnEliminarFamilia.setEnabled(false);
        btnGuardarFamilia.setEnabled(false);
        btnCancelarFamilia.setEnabled(false);    
    }     
    
     //método para deshabilitar los botones del formulario Pago a Proveedores
    private void deshabilitarBotonesPago()
    {
        btnNuevoPago.setEnabled(false);
        btnEliminarPago.setEnabled(false);
        btnGuardarPago.setEnabled(false);
        btnCancelarPago.setEnabled(false);    
    }     
        
     //método para deshabilitar los botones del formulario Pago a Proveedores
    private void deshabilitarBotonesEmpleados()
    {
        btnNuevoEmpleado.setEnabled(false);
        btnEliminarEmpleado.setEnabled(false);
        btnGuardarEmpleado.setEnabled(false);
        btnCancelarEmpleado.setEnabled(false);    
    } 
    
     //método para habilitar los botones del formulario Proveedores
    private void habilitarBotonesProveedor()
    {
        btnNuevoProveedor.setEnabled(true);
        btnEliminarProveedor.setEnabled(true);
        btnGuardarProveedor.setEnabled(true);
        btnCancelarProveedor.setEnabled(true);    
    }   
    
     //método para habilitar los botones del formulario Familia
    private void habilitarBotonesFamilia()
    {
        btnNuevaFamilia.setEnabled(true);
        btnEliminarFamilia.setEnabled(true);
        btnGuardarFamilia.setEnabled(true);
        btnCancelarFamilia.setEnabled(true);    
    }   
    
     //método para habilitar los botones del formulario Pago a proveedores
    private void habilitarBotonesPago()
    {
        btnNuevoPago.setEnabled(true);
        btnEliminarPago.setEnabled(true);
        btnGuardarPago.setEnabled(true);
        btnCancelarPago.setEnabled(true);  
    }     

     //método para habilitar los botones del formulario Datos Empleados
    private void habilitarBotonesEmpleados()
    {
        btnNuevoEmpleado.setEnabled(true);
        btnEliminarEmpleado.setEnabled(true);
        btnGuardarEmpleado.setEnabled(true);
        btnCancelarEmpleado.setEnabled(true);   
    } 
    
    private void habilitarTextFieldsPago()
    {
        txtAlbaran.setEnabled(true);
        txtProveedorPago.setEnabled(true);
        txtDescripcionPago.setEnabled(true);
        txtTotalPago.setEnabled(true);
        txtEntregado.setEnabled(true);
        jDateChooser.setEnabled(true);
        txtFormaPago.setEnabled(true);
        rbNo.setEnabled(true);
        rbSi.setEnabled(true);
    }
    
    private void habilitarTextFieldsEmpleados()
    {
        txtIdEmpleado.setEnabled(true);
        txtDniEmpleado.setEnabled(true);
        txtNombreEmpleado.setEnabled(true);
        txtApellidosEmpleado.setEnabled(true);
        txtDireccionEmpleado.setEnabled(true);
        txtTelefonoFijo.setEnabled(true);
        txtTelefonoMovil.setEnabled(true);
        txtEmailEmpleado.setEnabled(true);
        calendarioEmpleados.setEnabled(true);
        txtFechaNac.setEnabled(true);
        txtContraseñaEmpleado.setEnabled(true);
        //txtCargoEmpleado.setEnabled(true);
        cbCargo.setEnabled(true);
        
    }    
    
    private void deshabilitarTextFieldsPago()
    {
        txtAlbaran.setEnabled(false);
        txtProveedorPago.setEnabled(false);
        txtDescripcionPago.setEnabled(false);
        txtTotalPago.setEnabled(false);
        txtEntregado.setEnabled(false);
        jDateChooser.setEnabled(false);
        txtFormaPago.setEnabled(false);
        rbNo.setEnabled(false);
        rbSi.setEnabled(false);
    }    
    
    private void deshabilitarTextFieldsEmpleados()
    {
        txtIdEmpleado.setEnabled(false);
        txtDniEmpleado.setEnabled(false);
        txtNombreEmpleado.setEnabled(false);
        txtApellidosEmpleado.setEnabled(false);
        txtDireccionEmpleado.setEnabled(false);
        txtTelefonoFijo.setEnabled(false);
        txtTelefonoMovil.setEnabled(false);
        txtEmailEmpleado.setEnabled(false);
        calendarioEmpleados.setEnabled(false);
        txtFechaNac.setEnabled(false);
        txtContraseñaEmpleado.setEnabled(false);
        //txtCargoEmpleado.setEnabled(false);
        cbCargo.setEnabled(false);
    }  
    
    private void textFieldsPagoBlanco()    
    {
        txtIdPago.setText("");
        txtAlbaran.setText("");
        txtProveedorPago.setText("");
        txtDescripcionPago.setText("");
        txtTotalPago.setText("");
        txtEntregado.setText("");
        jDateChooser.setDate(null);
        txtFormaPago.setText("");
        btnGrupoPagado.clearSelection();        
    }
    
    private void textFieldsEmpleadosBlanco()    
    {
        txtIdEmpleado.setText("");
        txtDniEmpleado.setText("");
        txtNombreEmpleado.setText("");
        txtApellidosEmpleado.setText("");
        txtDireccionEmpleado.setText("");
        txtTelefonoFijo.setText("");
        txtTelefonoMovil.setText("");
        txtEmailEmpleado.setText("");
        calendarioEmpleados.setDate(null);
        txtFechaNac.setText("");
        txtContraseñaEmpleado.setText("");  
        cbCargo.setSelectedIndex(-1);
    }
    
/* METODO PARA VALIDAR LA FECHA */
    public boolean validarFecha(String fecha) 
    {  
  
        if (fecha == null) 
        {
            return false;  
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy"); //año-mes-dia  

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

    private String[] recogerDatosPago()
    {
        String valores = "", campos = "";
        String[][] datoPago = new String[9][2];
        String[] resultado = new String[2];
        Boolean formularioCompleto = false;
        Boolean fechaValida = true;
        Boolean formatoTotal = true;
        Boolean formatoEntregado = true;

        //Compruebo todos los campos de texto que son obligatorios
        if (txtAlbaran.getText().equals(""))
        {  
             txtAlbaran.requestFocusInWindow();
             //formularioCompleto = false;
        }
        else if (txtProveedorPago.getText().equals(""))
        { 
             txtProveedorPago.requestFocusInWindow();
             //formularioCompleto = false;
        } 
        else if (txtDescripcionPago.getText().equals(""))
        { 
             txtDescripcionPago.requestFocusInWindow();
             //formularioCompleto = false;
        }  
        else if (txtTotalPago.getText().equals("") ||
                Caja.comprobarCadena(txtTotalPago.getText()) == false)
        {   
             txtTotalPago.requestFocusInWindow();
             formatoTotal = false;
        }     
        else if (txtEntregado.getText().equals("") ||
                Caja.comprobarCadena(txtEntregado.getText()) == false)
        {    
             txtEntregado.requestFocusInWindow();
             formatoEntregado = false;
        }
        else if (jDateChooser.getDate() == null)
        {                             
             //formularioCompleto = false;
             fechaValida = false;
        }  
        else
        {
            formularioCompleto = true;
        }
          
        //Si todo está correcto, continuo con la recogida de datos
        if (formularioCompleto)
        {
            if (Float.parseFloat(txtTotalPago.getText()) < Float.parseFloat(txtEntregado.getText()))
            {
                JOptionPane.showMessageDialog(null, "La cantidad Entregada no puede ser mayor al Total.",
                          "Añadir Pago", JOptionPane.INFORMATION_MESSAGE); 
                txtEntregado.requestFocusInWindow(); 
            }
            else
            {
                //Si las cantidades Total y Entregado son iguales, activo el radiobutton 'Sí' como pagado.
                if (Float.parseFloat(txtTotalPago.getText()) == Float.parseFloat(txtEntregado.getText()))
                {
                    btnGrupoPagado.setSelected(rbSi.getModel(), true);
                }
                else
                {
                    btnGrupoPagado.setSelected(rbNo.getModel(), true);
                }
                    
                //En un array almacenaré los campos de la tabla en una columna,
                //y en la otra columna los valores que quiero asignarles.
                datoPago [0][0] = "id";
                datoPago [1][0] = "numero_albaran";
                datoPago [2][0] = "proveedor";
                datoPago [3][0] = "descripcion";
                datoPago [4][0] = "total";
                datoPago [5][0] = "cantidad_entregada";
                datoPago [6][0] = "fecha_pago";
                datoPago [7][0] = "forma_pago";
                datoPago [8][0] = "pagado_si_no";

                datoPago [0][1] = txtIdPago.getText();
                datoPago [1][1] = txtAlbaran.getText();
                datoPago [2][1] = txtProveedorPago.getText();
                datoPago [3][1] = txtDescripcionPago.getText();
                datoPago [4][1] = txtTotalPago.getText();
                datoPago [5][1] = txtEntregado.getText();

                //Para almacenar la fecha, tengo que obtenerla del jDataChooser
                //dándole el formato requerido para almacenarlo en la base de datos.
                SimpleDateFormat dateformat;  
                dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 

                datoPago [6][1] = dateformat.format(jDateChooser.getDate());                   
                datoPago [7][1] = txtFormaPago.getText();

                //Dependiendo del botón seleccionado, almaceno un Sí o un No
                if (botonSeleccionado(btnGrupoPagado).equals("Sí"))
                {             
                    datoPago [8][1] = "Sí";
                }
                else if (botonSeleccionado(btnGrupoPagado).equals("No"))
                {                
                    datoPago [8][1] = "No";
                }

                if (accion.equals("insertar"))
                {
                    for(int j = 0; j < 9; j++)
                    {
                        if(datoPago[j][1].equals("") == false)
                        {

                            valores = valores + ",'" + datoPago[j][1] + "'";
                            campos = campos + ", " + datoPago[j][0];                        
                        }
                    }
                    //elimino la ',' del principio, indicando que empiece desde el carácter 1
                    valores = valores.substring(1); 
                    campos = campos.substring(1); 

                    resultado[0] = campos;                
                    resultado[1] = valores;

                }
                else if (accion.equals("modificar"))
                {
                    for(int j=0; j < 9; j++)
                    {
                        if(datoPago[j][1].equals("") == false)
                        {                
                            valores = valores + datoPago[j][0] + "='" + datoPago[j][1] + "', ";
                        }
                    }        
                    //elimino la ',' del final, indicando desde y hasta donde copiar la cadena

                    valores = valores.substring(0,valores.length()-2); 

                    resultado[0] = "";
                    resultado[1] = valores;
                }     
                return resultado;        
            }
        }
        //Si falta algún campo por rellenar, muestro un mensaje
        else
        {
            //Si no se introdujo fecha, o no se introdujo correctamente, lo indico en un mensaje
            if (fechaValida == false)
            {
                jDateChooser.requestFocusInWindow();
                
                JOptionPane.showMessageDialog(null, "Debe indicar una fecha correcta. Utilice el selector "
                        + "o siga el formato siguiente:\n\n             Día-Mes-Año  HH:mm:ss  (Ej: 14-01-2013  17:08:50)",
                          "Añadir Pago", JOptionPane.INFORMATION_MESSAGE);        
                
            }
            else if (formatoTotal == false)
            {
                JOptionPane.showMessageDialog(null, "La cantidad Total no es correcta. Utilice solo números y el punto.",
                          "Añadir Pago", JOptionPane.INFORMATION_MESSAGE);        
                txtTotalPago.requestFocusInWindow();                
            }
            else if (formatoEntregado == false)
            {
                JOptionPane.showMessageDialog(null, "La cantidad Entregada no es correcta. Utilice solo números y el punto.",
                          "Añadir Pago", JOptionPane.INFORMATION_MESSAGE);        
                txtEntregado.requestFocusInWindow();                
            }            
            else
            {
                JOptionPane.showMessageDialog(null, "Los campos con * son obligatorios.",
                          "Añadir Pago", JOptionPane.INFORMATION_MESSAGE);                   
            }          
        }
        return null;
    }
    
private String[] recogerDatosEmpleados()
    {
        String valores = "", campos = "";
        String[][] datosEmp = new String[11][2];
        String[] resultado = new String[2];
        Boolean formularioCompleto = false;     
        SimpleDateFormat dateFormat;
        
        //Compruebo todos los campos de texto que son obligatorios
        if (txtNombreEmpleado.getText().equals(""))
        {  
             txtNombreEmpleado.requestFocusInWindow();
             JOptionPane.showMessageDialog(null, "Los campos con * son obligatorios.",
                          "Añadir Empleado", JOptionPane.INFORMATION_MESSAGE);              
        }
        else if (txtApellidosEmpleado.getText().equals(""))
        { 
             txtApellidosEmpleado.requestFocusInWindow();
             JOptionPane.showMessageDialog(null, "Los campos con * son obligatorios.",
                          "Añadir Empleado", JOptionPane.INFORMATION_MESSAGE);               
        }   
        else if (txtContraseñaEmpleado.getText().equals(""))
        { 
             txtContraseñaEmpleado.requestFocusInWindow();
             JOptionPane.showMessageDialog(null, "La contraseña debe tener al menos un carácter.",
                          "Añadir Empleado", JOptionPane.INFORMATION_MESSAGE);               
        }       
        else if (cbCargo.getSelectedIndex() == -1)
        { 
             cbCargo.requestFocusInWindow();
             JOptionPane.showMessageDialog(null, "Debe seleccionar el cargo de la persona.",
                          "Añadir Empleado", JOptionPane.INFORMATION_MESSAGE);               
        }           
        //si no se introdujo correctamente la fecha, lo indico en un mensaje
        else if (txtFechaNac.getText().equals("") == false)
        {
             if (validarFecha(txtFechaNac.getText()) == false)
             {
                 txtFechaNac.requestFocusInWindow();
                 JOptionPane.showMessageDialog(null, "Debe indicar una fecha correcta. Utilice el selector "
                        + "o siga el formato siguiente:\n\n                                  Día-Mes-Año (Ej: 15-01-2013)",
                          "Añadir Empleado", JOptionPane.INFORMATION_MESSAGE);   
             }
             else
             {
                 //Configuro el formato que quiero que muestre en el textfield
                 dateFormat = new SimpleDateFormat("dd-MM-yyyy");

                 try 
                 {
                     //Convierto la fecha que marca el textfield, y le doy el formato deseado
                     Date fechaDate = dateFormat.parse(txtFechaNac.getText()); 
                     //Configuro el calendario con la fecha almacenada
                     calendarioEmpleados.setDate(fechaDate);          
                     //Cambio el formato por defecto del jdatachooser para poder almacenarlo en MYSQL
                     calendarioEmpleados.setDateFormatString("yyyy-MM-dd"); 
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

          
        //Si todo está correcto, continuo con la recogida de datos
        if (formularioCompleto)
        {

                //En un array almacenaré los campos de la tabla en una columna,
                //y en la otra columna los valores que quiero asignarles.
                datosEmp [0][0] = "id";
                datosEmp [1][0] = "dni";
                datosEmp [2][0] = "nombre";
                datosEmp [3][0] = "apellidos";
                datosEmp [4][0] = "direccion";
                datosEmp [5][0] = "telefono_fijo";
                datosEmp [6][0] = "telefono_movil";
                datosEmp [7][0] = "email";
                datosEmp [8][0] = "fecha_nacimiento";
                datosEmp [9][0] = "password";
                datosEmp [10][0] = "cargo";

                //configuro el formato fecha para pasarlo a MYSQL
                dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                
                datosEmp [0][1] = txtIdEmpleado.getText();
                datosEmp [1][1] = txtDniEmpleado.getText();
                datosEmp [2][1] = txtNombreEmpleado.getText();
                datosEmp [3][1] = txtApellidosEmpleado.getText();
                datosEmp [4][1] = txtDireccionEmpleado.getText();
                datosEmp [5][1] = txtTelefonoFijo.getText();
                datosEmp [6][1] = txtTelefonoMovil.getText();
                datosEmp [7][1] = txtEmailEmpleado.getText();
                
                if (txtFechaNac.getText().equals(""))
                {
                    datosEmp [8][1] = "";
                }
                else
                {
                    datosEmp [8][1] = dateFormat.format(calendarioEmpleados.getDate()); //formateo la fecha que marca
                }    
                
                datosEmp [9][1] = txtContraseñaEmpleado.getText();
                datosEmp [10][1] = cbCargo.getSelectedItem().toString();

                if (accion.equals("insertar"))
                {
                    for(int j = 0; j < 11; j++)
                    {
                        if(datosEmp[j][1].equals(""))
                        {                
                            datosEmp[j][1]=null;
                            valores = valores + "," + datosEmp[j][1];
                        }
                        else
                        {
                            valores = valores + ",'" + datosEmp[j][1] + "'";
                        }
                        
                        campos = campos + ", " + datosEmp[j][0];                        
                        
                    }
                    //elimino la ',' del principio, indicando que empiece desde el carácter 1
                    valores = valores.substring(1); 
                    campos = campos.substring(1); 

                    resultado[0] = campos;                
                    resultado[1] = valores;

                }
                else if (accion.equals("modificar"))
                {
                    for(int j=0; j < 11; j++)
                    {
                        if(datosEmp[j][1].equals(""))
                        {                
                            datosEmp[j][1]=null;
                            valores = valores + datosEmp[j][0] + "=" + datosEmp[j][1] + ", ";
                        }
                        else
                        {
                            valores = valores + datosEmp[j][0] + "='" + datosEmp[j][1] + "', ";
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

    //método para detectar el nombre del radiobutton que está seleccionado
    public String botonSeleccionado(ButtonGroup buttonGroup) 
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
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        panelProveedores = new javax.swing.JPanel();
        txtIdProveedor = new javax.swing.JTextField();
        lbIdProveedor = new javax.swing.JLabel();
        txtProveedor = new javax.swing.JTextField();
        lbProveedor = new javax.swing.JLabel();
        btnNuevoProveedor = new javax.swing.JButton();
        btnGuardarProveedor = new javax.swing.JButton();
        btnEliminarProveedor = new javax.swing.JButton();
        btnCancelarProveedor = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tbProveedores = new javax.swing.JTable();
        panelFamilia = new javax.swing.JPanel();
        txtIdFamilia = new javax.swing.JTextField();
        lbIdFamilia = new javax.swing.JLabel();
        txtFamilia = new javax.swing.JTextField();
        lbFamilia = new javax.swing.JLabel();
        btnNuevaFamilia = new javax.swing.JButton();
        btnGuardarFamilia = new javax.swing.JButton();
        btnEliminarFamilia = new javax.swing.JButton();
        btnCancelarFamilia = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        tbFamilia = new javax.swing.JTable();
        lbTablaProveedores = new javax.swing.JLabel();
        lbTablaFamilia = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        lbTablaProveedores1 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        tbPagoProveedores = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();
        panelPagado = new javax.swing.JPanel();
        rbSi = new javax.swing.JRadioButton();
        rbNo = new javax.swing.JRadioButton();
        lbFechaPago = new javax.swing.JLabel();
        txtFormaPago = new javax.swing.JTextField();
        txtProveedorPago = new javax.swing.JTextField();
        lbTotalPago = new javax.swing.JLabel();
        lbIdPago = new javax.swing.JLabel();
        txtAlbaran = new javax.swing.JTextField();
        lbProveedorPago = new javax.swing.JLabel();
        txtIdPago = new javax.swing.JTextField();
        lbFormaPago = new javax.swing.JLabel();
        lbPagado = new javax.swing.JLabel();
        txtDescripcionPago = new javax.swing.JTextField();
        lbDescripcionPago = new javax.swing.JLabel();
        txtEntregado = new javax.swing.JTextField();
        lbEntregado = new javax.swing.JLabel();
        lbAlbaran = new javax.swing.JLabel();
        txtTotalPago = new javax.swing.JTextField();
        btnNuevoPago = new javax.swing.JButton();
        btnGuardarPago = new javax.swing.JButton();
        btnEliminarPago = new javax.swing.JButton();
        btnCancelarPago = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jDateChooser = new com.toedter.calendar.JDateChooser();
        jLabel7 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        lbDatosEmpleados = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        tbDatosEmpleados = new javax.swing.JTable();
        panelDatosEmpleados = new javax.swing.JPanel();
        btnNuevoEmpleado = new javax.swing.JButton();
        btnGuardarEmpleado = new javax.swing.JButton();
        btnEliminarEmpleado = new javax.swing.JButton();
        btnCancelarEmpleado = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        lbDireccionEmpleado = new javax.swing.JLabel();
        txtApellidosEmpleado = new javax.swing.JTextField();
        lbApellidosEmpleado = new javax.swing.JLabel();
        txtFechaNac = new javax.swing.JTextField();
        lbNombreEmpleado = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txtNombreEmpleado = new javax.swing.JTextField();
        lbCargoEmpleado1 = new javax.swing.JLabel();
        calendarioEmpleados = new com.toedter.calendar.JDateChooser();
        jLabel9 = new javax.swing.JLabel();
        cbCargo = new javax.swing.JComboBox();
        lbDniEmpleado = new javax.swing.JLabel();
        txtIdEmpleado = new javax.swing.JTextField();
        txtDniEmpleado = new javax.swing.JTextField();
        txtContraseñaEmpleado = new javax.swing.JTextField();
        lbContraseñaEmpleado = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        lbCargoEmpleado = new javax.swing.JLabel();
        lbIdEmpleado = new javax.swing.JLabel();
        txtDireccionEmpleado = new javax.swing.JTextField();
        lbTelefonoFijo = new javax.swing.JLabel();
        txtTelefonoFijo = new javax.swing.JTextField();
        lbTelefonoMovil = new javax.swing.JLabel();
        txtTelefonoMovil = new javax.swing.JTextField();
        lbEmailEmpleado = new javax.swing.JLabel();
        txtEmailEmpleado = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        lbDatosEmpleados1 = new javax.swing.JLabel();
        lbDatosEmpleados2 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        btnCopiaSeguridad = new javax.swing.JButton();
        selectorExportar = new javax.swing.JFileChooser();
        jPanel9 = new javax.swing.JPanel();
        selectorImportar = new javax.swing.JFileChooser();
        btnRestaurarBD = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1000, 581));
        setResizable(false);

        jTabbedPane1.setPreferredSize(new java.awt.Dimension(1000, 400));
        jTabbedPane1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTabbedPane1MouseClicked(evt);
            }
        });

        jPanel1.setPreferredSize(new java.awt.Dimension(975, 550));

        panelProveedores.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        panelProveedores.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N

        txtIdProveedor.setEditable(false);
        txtIdProveedor.setFont(new java.awt.Font("Cambria", 0, 14)); // NOI18N

        lbIdProveedor.setFont(new java.awt.Font("Cambria", 0, 14)); // NOI18N
        lbIdProveedor.setText("Id:");

        txtProveedor.setFont(new java.awt.Font("Cambria", 0, 14)); // NOI18N
        txtProveedor.setCaretPosition(0);
        txtProveedor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtProveedorMouseClicked(evt);
            }
        });
        txtProveedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtProveedorActionPerformed(evt);
            }
        });

        lbProveedor.setFont(new java.awt.Font("Cambria", 0, 14)); // NOI18N
        lbProveedor.setText("Provee:");

        btnNuevoProveedor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/NuevoRegistro.png"))); // NOI18N
        btnNuevoProveedor.setText("Nuevo");
        btnNuevoProveedor.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnNuevoProveedor.setPreferredSize(new java.awt.Dimension(75, 57));
        btnNuevoProveedor.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnNuevoProveedor.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnNuevoProveedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevoProveedorActionPerformed(evt);
            }
        });

        btnGuardarProveedor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/Guardar.png"))); // NOI18N
        btnGuardarProveedor.setText("Guardar");
        btnGuardarProveedor.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnGuardarProveedor.setPreferredSize(new java.awt.Dimension(75, 57));
        btnGuardarProveedor.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnGuardarProveedor.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnGuardarProveedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarProveedorActionPerformed(evt);
            }
        });

        btnEliminarProveedor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/papelera.png"))); // NOI18N
        btnEliminarProveedor.setText("Eliminar");
        btnEliminarProveedor.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEliminarProveedor.setPreferredSize(new java.awt.Dimension(75, 57));
        btnEliminarProveedor.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnEliminarProveedor.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnEliminarProveedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarProveedorActionPerformed(evt);
            }
        });

        btnCancelarProveedor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/Cancelar.png"))); // NOI18N
        btnCancelarProveedor.setText("Cancelar");
        btnCancelarProveedor.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCancelarProveedor.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnCancelarProveedor.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnCancelarProveedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarProveedorActionPerformed(evt);
            }
        });

        tbProveedores.setAutoCreateRowSorter(true);
        tbProveedores.setFont(new java.awt.Font("Arial Unicode MS", 0, 12)); // NOI18N
        tbProveedores.setModel(new javax.swing.table.DefaultTableModel(
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
        tbProveedores.setRowHeight(18);
        tbProveedores.getTableHeader().setReorderingAllowed(false);
        tbProveedores.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbProveedoresMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                tbProveedoresMouseEntered(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tbProveedoresMouseReleased(evt);
            }
        });
        tbProveedores.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                tbProveedoresPropertyChange(evt);
            }
        });
        jScrollPane3.setViewportView(tbProveedores);

        javax.swing.GroupLayout panelProveedoresLayout = new javax.swing.GroupLayout(panelProveedores);
        panelProveedores.setLayout(panelProveedoresLayout);
        panelProveedoresLayout.setHorizontalGroup(
            panelProveedoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelProveedoresLayout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(panelProveedoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(panelProveedoresLayout.createSequentialGroup()
                        .addGroup(panelProveedoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnNuevoProveedor, javax.swing.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
                            .addComponent(btnEliminarProveedor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelProveedoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnCancelarProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnGuardarProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(panelProveedoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(panelProveedoresLayout.createSequentialGroup()
                            .addComponent(lbProveedor)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtProveedor, javax.swing.GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE))
                        .addGroup(panelProveedoresLayout.createSequentialGroup()
                            .addComponent(lbIdProveedor)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtIdProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 669, Short.MAX_VALUE))
        );
        panelProveedoresLayout.setVerticalGroup(
            panelProveedoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelProveedoresLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelProveedoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(panelProveedoresLayout.createSequentialGroup()
                        .addGroup(panelProveedoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtIdProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbIdProveedor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(6, 6, 6)
                        .addGroup(panelProveedoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbProveedor))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelProveedoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnNuevoProveedor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnGuardarProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelProveedoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnCancelarProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnEliminarProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        panelFamilia.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        panelFamilia.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N

        txtIdFamilia.setEditable(false);
        txtIdFamilia.setFont(new java.awt.Font("Cambria", 0, 14)); // NOI18N

        lbIdFamilia.setFont(new java.awt.Font("Cambria", 0, 14)); // NOI18N
        lbIdFamilia.setText("Id:");

        txtFamilia.setFont(new java.awt.Font("Cambria", 0, 14)); // NOI18N

        lbFamilia.setFont(new java.awt.Font("Cambria", 0, 14)); // NOI18N
        lbFamilia.setText("Familia:");

        btnNuevaFamilia.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/NuevoRegistro.png"))); // NOI18N
        btnNuevaFamilia.setText("Nuevo");
        btnNuevaFamilia.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnNuevaFamilia.setPreferredSize(new java.awt.Dimension(75, 57));
        btnNuevaFamilia.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnNuevaFamilia.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnNuevaFamilia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevaFamiliaActionPerformed(evt);
            }
        });

        btnGuardarFamilia.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/Guardar.png"))); // NOI18N
        btnGuardarFamilia.setText("Guardar");
        btnGuardarFamilia.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnGuardarFamilia.setPreferredSize(new java.awt.Dimension(75, 57));
        btnGuardarFamilia.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnGuardarFamilia.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnGuardarFamilia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarFamiliaActionPerformed(evt);
            }
        });

        btnEliminarFamilia.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/papelera.png"))); // NOI18N
        btnEliminarFamilia.setText("Eliminar");
        btnEliminarFamilia.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEliminarFamilia.setPreferredSize(new java.awt.Dimension(75, 57));
        btnEliminarFamilia.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnEliminarFamilia.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnEliminarFamilia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarFamiliaActionPerformed(evt);
            }
        });

        btnCancelarFamilia.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/Cancelar.png"))); // NOI18N
        btnCancelarFamilia.setText("Cancelar");
        btnCancelarFamilia.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCancelarFamilia.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnCancelarFamilia.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnCancelarFamilia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarFamiliaActionPerformed(evt);
            }
        });

        tbFamilia.setAutoCreateRowSorter(true);
        tbFamilia.setFont(new java.awt.Font("Arial Unicode MS", 0, 12)); // NOI18N
        tbFamilia.setModel(new javax.swing.table.DefaultTableModel(
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
        tbFamilia.setRowHeight(18);
        tbFamilia.getTableHeader().setReorderingAllowed(false);
        tbFamilia.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbFamiliaMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                tbFamiliaMouseEntered(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tbFamiliaMouseReleased(evt);
            }
        });
        tbFamilia.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                tbFamiliaPropertyChange(evt);
            }
        });
        jScrollPane4.setViewportView(tbFamilia);

        javax.swing.GroupLayout panelFamiliaLayout = new javax.swing.GroupLayout(panelFamilia);
        panelFamilia.setLayout(panelFamiliaLayout);
        panelFamiliaLayout.setHorizontalGroup(
            panelFamiliaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFamiliaLayout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(panelFamiliaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelFamiliaLayout.createSequentialGroup()
                        .addGroup(panelFamiliaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lbIdFamilia)
                            .addComponent(lbFamilia, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelFamiliaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtFamilia, javax.swing.GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE)
                            .addComponent(txtIdFamilia)))
                    .addGroup(panelFamiliaLayout.createSequentialGroup()
                        .addGroup(panelFamiliaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnEliminarFamilia, javax.swing.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
                            .addComponent(btnNuevaFamilia, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelFamiliaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnCancelarFamilia, javax.swing.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
                            .addComponent(btnGuardarFamilia, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane4))
        );
        panelFamiliaLayout.setVerticalGroup(
            panelFamiliaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFamiliaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFamiliaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelFamiliaLayout.createSequentialGroup()
                        .addGroup(panelFamiliaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbIdFamilia)
                            .addComponent(txtIdFamilia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelFamiliaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lbFamilia)
                            .addComponent(txtFamilia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelFamiliaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnGuardarFamilia, javax.swing.GroupLayout.DEFAULT_SIZE, 68, Short.MAX_VALUE)
                            .addComponent(btnNuevaFamilia, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelFamiliaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnCancelarFamilia, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnEliminarFamilia, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );

        lbTablaProveedores.setFont(new java.awt.Font("Cambria", 0, 24)); // NOI18N
        lbTablaProveedores.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbTablaProveedores.setText("Tabla de Proveedores");
        lbTablaProveedores.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

        lbTablaFamilia.setFont(new java.awt.Font("Cambria", 0, 24)); // NOI18N
        lbTablaFamilia.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbTablaFamilia.setText("Tabla de tipos de Servicios");
        lbTablaFamilia.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbTablaProveedores, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbTablaFamilia, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelProveedores, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelFamilia, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbTablaProveedores)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelProveedores, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbTablaFamilia)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelFamilia, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Proveedores y Familia de servicios", jPanel1);

        jPanel2.setPreferredSize(new java.awt.Dimension(975, 550));

        lbTablaProveedores1.setFont(new java.awt.Font("Cambria", 0, 24)); // NOI18N
        lbTablaProveedores1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbTablaProveedores1.setText("Pago a proveedores");
        lbTablaProveedores1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

        tbPagoProveedores.setAutoCreateRowSorter(true);
        tbPagoProveedores.setFont(new java.awt.Font("Arial Unicode MS", 0, 12)); // NOI18N
        tbPagoProveedores.setModel(new javax.swing.table.DefaultTableModel(
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
        tbPagoProveedores.setRowHeight(18);
        tbPagoProveedores.getTableHeader().setReorderingAllowed(false);
        tbPagoProveedores.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tbPagoProveedoresMouseReleased(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbPagoProveedoresMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                tbPagoProveedoresMouseEntered(evt);
            }
        });
        tbPagoProveedores.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                tbPagoProveedoresPropertyChange(evt);
            }
        });
        jScrollPane5.setViewportView(tbPagoProveedores);

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)), "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Cambria", 0, 14))); // NOI18N
        jPanel6.setFont(new java.awt.Font("Cambria", 0, 14)); // NOI18N
        jPanel6.setPreferredSize(new java.awt.Dimension(344, 419));

        panelPagado.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        panelPagado.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 35, 5));

        rbSi.setFont(new java.awt.Font("Cambria", 0, 14)); // NOI18N
        rbSi.setText("Sí");
        rbSi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbSiActionPerformed(evt);
            }
        });
        panelPagado.add(rbSi);

        rbNo.setFont(new java.awt.Font("Cambria", 0, 14)); // NOI18N
        rbNo.setText("No");
        panelPagado.add(rbNo);

        lbFechaPago.setFont(new java.awt.Font("Cambria", 0, 14)); // NOI18N
        lbFechaPago.setText("Fecha del pago: ");

        txtFormaPago.setFont(new java.awt.Font("Cambria", 0, 14)); // NOI18N

        txtProveedorPago.setFont(new java.awt.Font("Cambria", 0, 14)); // NOI18N

        lbTotalPago.setFont(new java.awt.Font("Cambria", 0, 14)); // NOI18N
        lbTotalPago.setText("Total: ");

        lbIdPago.setFont(new java.awt.Font("Cambria", 0, 14)); // NOI18N
        lbIdPago.setText("Id:");

        txtAlbaran.setFont(new java.awt.Font("Cambria", 0, 14)); // NOI18N

        lbProveedorPago.setFont(new java.awt.Font("Cambria", 0, 14)); // NOI18N
        lbProveedorPago.setText("Proveedor: ");

        txtIdPago.setEditable(false);
        txtIdPago.setFont(new java.awt.Font("Cambria", 0, 14)); // NOI18N

        lbFormaPago.setFont(new java.awt.Font("Cambria", 0, 14)); // NOI18N
        lbFormaPago.setText("Forma de pago: ");

        lbPagado.setFont(new java.awt.Font("Cambria", 0, 14)); // NOI18N
        lbPagado.setText("Pagado: ");

        txtDescripcionPago.setFont(new java.awt.Font("Cambria", 0, 14)); // NOI18N

        lbDescripcionPago.setFont(new java.awt.Font("Cambria", 0, 14)); // NOI18N
        lbDescripcionPago.setText("Descripción: ");

        txtEntregado.setFont(new java.awt.Font("Cambria", 0, 14)); // NOI18N

        lbEntregado.setFont(new java.awt.Font("Cambria", 0, 14)); // NOI18N
        lbEntregado.setText("Entregado: ");

        lbAlbaran.setFont(new java.awt.Font("Cambria", 0, 14)); // NOI18N
        lbAlbaran.setText("Nº Albarán: ");

        txtTotalPago.setFont(new java.awt.Font("Cambria", 0, 14)); // NOI18N

        btnNuevoPago.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/NuevoRegistro.png"))); // NOI18N
        btnNuevoPago.setText("Nuevo");
        btnNuevoPago.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnNuevoPago.setPreferredSize(new java.awt.Dimension(75, 57));
        btnNuevoPago.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnNuevoPago.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnNuevoPago.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevoPagoActionPerformed(evt);
            }
        });

        btnGuardarPago.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/Guardar.png"))); // NOI18N
        btnGuardarPago.setText("Guardar");
        btnGuardarPago.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnGuardarPago.setPreferredSize(new java.awt.Dimension(75, 57));
        btnGuardarPago.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnGuardarPago.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnGuardarPago.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarPagoActionPerformed(evt);
            }
        });

        btnEliminarPago.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/papelera.png"))); // NOI18N
        btnEliminarPago.setText("Eliminar");
        btnEliminarPago.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEliminarPago.setPreferredSize(new java.awt.Dimension(75, 57));
        btnEliminarPago.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnEliminarPago.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnEliminarPago.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarPagoActionPerformed(evt);
            }
        });

        btnCancelarPago.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/Cancelar.png"))); // NOI18N
        btnCancelarPago.setText("Cancelar");
        btnCancelarPago.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCancelarPago.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnCancelarPago.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnCancelarPago.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarPagoActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel2.setText("*");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setText("*");

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel4.setText("*");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel5.setText("*");

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel6.setText("*");

        jDateChooser.setFont(new java.awt.Font("Cambria", 0, 14)); // NOI18N
        jDateChooser.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jDateChooserMouseClicked(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel7.setText("*");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addGap(23, 23, 23)
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel6Layout.createSequentialGroup()
                                        .addComponent(lbIdPago)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtIdPago, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(110, 110, 110))
                                    .addGroup(jPanel6Layout.createSequentialGroup()
                                        .addComponent(lbAlbaran)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtAlbaran, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(lbEntregado)
                                    .addComponent(lbTotalPago)
                                    .addComponent(lbFormaPago)
                                    .addComponent(lbPagado)
                                    .addComponent(lbFechaPago, javax.swing.GroupLayout.Alignment.LEADING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtTotalPago, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(panelPagado, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtFormaPago, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtEntregado, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(8, 8, 8)))
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)
                            .addComponent(jLabel7)))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addGap(29, 29, 29)
                                .addComponent(lbProveedorPago)
                                .addGap(3, 3, 3))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                                .addComponent(lbDescripcionPago)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtDescripcionPago, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtProveedorPago, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(btnNuevoPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnGuardarPago, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCancelarPago, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnEliminarPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(15, 15, 15))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtIdPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbIdPago))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtAlbaran, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbAlbaran)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtProveedorPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbProveedorPago)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtDescripcionPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbDescripcionPago)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTotalPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbTotalPago)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbEntregado)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtEntregado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbFechaPago)
                    .addComponent(jDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbFormaPago)
                    .addComponent(txtFormaPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelPagado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbPagado, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnNuevoPago, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnEliminarPago, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnGuardarPago, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnCancelarPago, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(53, 53, 53))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lbTablaProveedores1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 616, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbTablaProveedores1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, 489, Short.MAX_VALUE)
                    .addComponent(jScrollPane5))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Pago a proveedores", jPanel2);

        jPanel3.setPreferredSize(new java.awt.Dimension(975, 550));

        lbDatosEmpleados.setFont(new java.awt.Font("Cambria", 0, 24)); // NOI18N
        lbDatosEmpleados.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbDatosEmpleados.setText("Datos de los empleados");
        lbDatosEmpleados.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

        tbDatosEmpleados.setAutoCreateRowSorter(true);
        tbDatosEmpleados.setFont(new java.awt.Font("Arial Unicode MS", 0, 10)); // NOI18N
        tbDatosEmpleados.setModel(new javax.swing.table.DefaultTableModel(
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
        tbDatosEmpleados.setRowHeight(18);
        tbDatosEmpleados.getTableHeader().setReorderingAllowed(false);
        tbDatosEmpleados.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbDatosEmpleadosMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                tbDatosEmpleadosMouseEntered(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tbDatosEmpleadosMouseReleased(evt);
            }
        });
        tbDatosEmpleados.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                tbDatosEmpleadosPropertyChange(evt);
            }
        });
        jScrollPane6.setViewportView(tbDatosEmpleados);

        panelDatosEmpleados.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)), "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Cambria", 0, 14))); // NOI18N
        panelDatosEmpleados.setFont(new java.awt.Font("Cambria", 0, 14)); // NOI18N

        btnNuevoEmpleado.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/NuevoCliente_peque.png"))); // NOI18N
        btnNuevoEmpleado.setText("Nuevo");
        btnNuevoEmpleado.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnNuevoEmpleado.setPreferredSize(new java.awt.Dimension(75, 57));
        btnNuevoEmpleado.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnNuevoEmpleado.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnNuevoEmpleado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevoEmpleadoActionPerformed(evt);
            }
        });

        btnGuardarEmpleado.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/Guardar.png"))); // NOI18N
        btnGuardarEmpleado.setText("Guardar");
        btnGuardarEmpleado.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnGuardarEmpleado.setPreferredSize(new java.awt.Dimension(75, 57));
        btnGuardarEmpleado.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnGuardarEmpleado.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnGuardarEmpleado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarEmpleadoActionPerformed(evt);
            }
        });

        btnEliminarEmpleado.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/papelera.png"))); // NOI18N
        btnEliminarEmpleado.setText("Eliminar");
        btnEliminarEmpleado.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEliminarEmpleado.setPreferredSize(new java.awt.Dimension(75, 57));
        btnEliminarEmpleado.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnEliminarEmpleado.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnEliminarEmpleado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarEmpleadoActionPerformed(evt);
            }
        });

        btnCancelarEmpleado.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/Cancelar.png"))); // NOI18N
        btnCancelarEmpleado.setText("Cancelar");
        btnCancelarEmpleado.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCancelarEmpleado.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnCancelarEmpleado.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnCancelarEmpleado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarEmpleadoActionPerformed(evt);
            }
        });

        lbDireccionEmpleado.setFont(new java.awt.Font("Cambria", 0, 14)); // NOI18N
        lbDireccionEmpleado.setText("Dirección: ");

        txtApellidosEmpleado.setFont(new java.awt.Font("Cambria", 0, 14)); // NOI18N

        lbApellidosEmpleado.setFont(new java.awt.Font("Cambria", 0, 14)); // NOI18N
        lbApellidosEmpleado.setText("Apellidos: ");

        txtFechaNac.setFont(new java.awt.Font("Cambria", 0, 14)); // NOI18N
        txtFechaNac.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFechaNacActionPerformed(evt);
            }
        });

        lbNombreEmpleado.setFont(new java.awt.Font("Cambria", 0, 14)); // NOI18N
        lbNombreEmpleado.setText("Nombre: ");

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel11.setText("*");

        txtNombreEmpleado.setFont(new java.awt.Font("Cambria", 0, 14)); // NOI18N
        txtNombreEmpleado.setToolTipText("");

        lbCargoEmpleado1.setFont(new java.awt.Font("Cambria", 0, 14)); // NOI18N
        lbCargoEmpleado1.setText("Fecha nac.:");

        calendarioEmpleados.setFont(new java.awt.Font("Cambria", 0, 14)); // NOI18N
        calendarioEmpleados.setPreferredSize(new java.awt.Dimension(6, 24));

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel9.setText("*");

        lbDniEmpleado.setFont(new java.awt.Font("Cambria", 0, 14)); // NOI18N
        lbDniEmpleado.setText("Dni: ");

        txtIdEmpleado.setEditable(false);
        txtIdEmpleado.setFont(new java.awt.Font("Cambria", 0, 14)); // NOI18N

        txtDniEmpleado.setFont(new java.awt.Font("Cambria", 0, 14)); // NOI18N
        txtDniEmpleado.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtDniEmpleadoKeyTyped(evt);
            }
        });

        txtContraseñaEmpleado.setFont(new java.awt.Font("Cambria", 0, 14)); // NOI18N

        lbContraseñaEmpleado.setFont(new java.awt.Font("Cambria", 0, 14)); // NOI18N
        lbContraseñaEmpleado.setText("Contraseña: ");

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel10.setText("*");

        lbCargoEmpleado.setFont(new java.awt.Font("Cambria", 0, 14)); // NOI18N
        lbCargoEmpleado.setText("Cargo: ");

        lbIdEmpleado.setFont(new java.awt.Font("Cambria", 0, 14)); // NOI18N
        lbIdEmpleado.setText("Id:");

        txtDireccionEmpleado.setFont(new java.awt.Font("Cambria", 0, 14)); // NOI18N

        lbTelefonoFijo.setFont(new java.awt.Font("Cambria", 0, 14)); // NOI18N
        lbTelefonoFijo.setText("Telf. fijo: ");

        txtTelefonoFijo.setFont(new java.awt.Font("Cambria", 0, 14)); // NOI18N

        lbTelefonoMovil.setFont(new java.awt.Font("Cambria", 0, 14)); // NOI18N
        lbTelefonoMovil.setText("Telf. mvl: ");

        txtTelefonoMovil.setFont(new java.awt.Font("Cambria", 0, 14)); // NOI18N

        lbEmailEmpleado.setFont(new java.awt.Font("Cambria", 0, 14)); // NOI18N
        lbEmailEmpleado.setText("E-mail: ");

        txtEmailEmpleado.setFont(new java.awt.Font("Cambria", 0, 14)); // NOI18N

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel7Layout.createSequentialGroup()
                            .addComponent(lbDireccionEmpleado)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtDireccionEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel7Layout.createSequentialGroup()
                            .addComponent(lbApellidosEmpleado)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtApellidosEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel7Layout.createSequentialGroup()
                            .addGap(9, 9, 9)
                            .addComponent(lbTelefonoFijo)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtTelefonoFijo, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel7Layout.createSequentialGroup()
                            .addGap(5, 5, 5)
                            .addComponent(lbTelefonoMovil)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtTelefonoMovil, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(lbNombreEmpleado)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtNombreEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lbIdEmpleado)
                            .addComponent(lbDniEmpleado))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtDniEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtIdEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lbEmailEmpleado)
                            .addComponent(lbCargoEmpleado1)
                            .addComponent(lbContraseñaEmpleado)
                            .addComponent(lbCargoEmpleado))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtEmailEmpleado)
                            .addComponent(txtContraseñaEmpleado)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                                .addComponent(txtFechaNac, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(calendarioEmpleados, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(cbCargo, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(3, 3, 3))
                    .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtIdEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbIdEmpleado))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtDniEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbDniEmpleado))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNombreEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbNombreEmpleado)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtApellidosEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbApellidosEmpleado)
                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtDireccionEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbDireccionEmpleado))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTelefonoFijo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbTelefonoFijo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTelefonoMovil, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbTelefonoMovil))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtEmailEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbEmailEmpleado))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(calendarioEmpleados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbCargoEmpleado1)
                    .addComponent(txtFechaNac, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbContraseñaEmpleado)
                    .addComponent(txtContraseñaEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbCargoEmpleado)
                    .addComponent(cbCargo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout panelDatosEmpleadosLayout = new javax.swing.GroupLayout(panelDatosEmpleados);
        panelDatosEmpleados.setLayout(panelDatosEmpleadosLayout);
        panelDatosEmpleadosLayout.setHorizontalGroup(
            panelDatosEmpleadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDatosEmpleadosLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(btnNuevoEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnGuardarEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCancelarEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEliminarEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15))
            .addGroup(panelDatosEmpleadosLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelDatosEmpleadosLayout.setVerticalGroup(
            panelDatosEmpleadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelDatosEmpleadosLayout.createSequentialGroup()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(panelDatosEmpleadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnGuardarEmpleado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnNuevoEmpleado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnCancelarEmpleado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnEliminarEmpleado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbDatosEmpleados, javax.swing.GroupLayout.DEFAULT_SIZE, 966, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(panelDatosEmpleados, javax.swing.GroupLayout.PREFERRED_SIZE, 344, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane6)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbDatosEmpleados)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelDatosEmpleados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane6))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Datos de los empleados", jPanel3);

        jPanel4.setPreferredSize(new java.awt.Dimension(975, 550));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 986, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 548, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Estadísticas", jPanel4);

        jPanel5.setPreferredSize(new java.awt.Dimension(975, 550));

        lbDatosEmpleados1.setFont(new java.awt.Font("Cambria", 0, 24)); // NOI18N
        lbDatosEmpleados1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbDatosEmpleados1.setText("Realizar copia de seguridad de la base de datos");
        lbDatosEmpleados1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

        lbDatosEmpleados2.setFont(new java.awt.Font("Cambria", 0, 24)); // NOI18N
        lbDatosEmpleados2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbDatosEmpleados2.setText("Restaurar base de datos");
        lbDatosEmpleados2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

        jPanel8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

        btnCopiaSeguridad.setText("Realizar copia");
        btnCopiaSeguridad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCopiaSeguridadActionPerformed(evt);
            }
        });

        selectorExportar.setVisible(false);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(357, 357, 357)
                .addComponent(btnCopiaSeguridad, javax.swing.GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE)
                .addGap(141, 141, 141)
                .addComponent(selectorExportar, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(41, 41, 41))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(selectorExportar, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(54, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnCopiaSeguridad, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(38, 38, 38))
        );

        jPanel9.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

        selectorImportar.setVisible(false);

        btnRestaurarBD.setText("Restaurar copia");
        btnRestaurarBD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRestaurarBDActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(355, 355, 355)
                .addComponent(btnRestaurarBD, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(71, 71, 71)
                .addComponent(selectorImportar, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(101, 101, 101))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(selectorImportar, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRestaurarBD, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(29, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbDatosEmpleados1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbDatosEmpleados2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbDatosEmpleados1, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(lbDatosEmpleados2, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Copia de seguridad", jPanel5);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 991, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 576, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnNuevoProveedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoProveedorActionPerformed

        accion="insertar";
        txtProveedor.requestFocusInWindow();
        
        deshabilitarBotonesProveedor();
        
        btnGuardarProveedor.setEnabled(true);
        btnCancelarProveedor.setEnabled(true);        
        txtProveedor.setEnabled(true); 
        
        txtProveedor.setEditable(true);
        
        txtProveedor.setText("");
        txtIdProveedor.setText("");         
    }//GEN-LAST:event_btnNuevoProveedorActionPerformed

    private void btnGuardarProveedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarProveedorActionPerformed

        boolean res;
        int filasel;        
        String where;

        if (txtProveedor.getText().equals("") == false)
        {
            switch (accion)
            {
                case "insertar":
                    
                    res = consulta.insert("proveedores", "proveedor", "'" + txtProveedor.getText() + "'");
                    
                    if (res == true)
                    {
                        JOptionPane.showMessageDialog(null, "La inserción se ha realizado correctamente",
                            "Añadir proveedor", JOptionPane.INFORMATION_MESSAGE);

                        crearTablaProveedores();
                        deshabilitarBotonesProveedor();
                        btnNuevoProveedor.setEnabled(true);
                        txtProveedor.setEnabled(false);
                    }

                    break;
                    
                case "modificar":
                    
                    if (tbProveedores.getSelectedRowCount() > 1 || tbProveedores.getSelectedRowCount() < 1)
                    {
                        JOptionPane.showMessageDialog(null, "Debe seleccionar un registro",
                            "Modificar proveedor", JOptionPane.INFORMATION_MESSAGE);
                    }
                    else
                    {
                        //localizo la fila seleccionada en la tabla
                        filasel = tbProveedores.getSelectedRow();
                        int respuesta = JOptionPane.showConfirmDialog(null,
                            "Se va ha modificar el proveedor con 'id " + modelo.getValueAt(filasel, 0) +
                            "' - '" + txtProveedor.getText()+"', ¿Está seguro?",
                            "Modificar proveedor", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                        if (respuesta==0)
                        {
                            if(filasel == -1 || txtProveedor.getText().equals(""))
                            {
                                JOptionPane.showMessageDialog(null, "No se ha seleccionado ningun registro",
                                    "Modificar proveedor", JOptionPane.INFORMATION_MESSAGE);
                            }

                            else
                            {
                                //almaceno el id de la fila seleccionada
                                where = "id='" + (String) modelo.getValueAt(filasel, 0) + "'";
                                
                                //llamo al método Update de la clase conexión y le envío tabla, campo, condición
                                consulta.update("productos", "proveedor", where);

                                JOptionPane.showMessageDialog(null, "El proveedor con id='" + modelo.getValueAt(filasel, 0) +
                                    "' - '" + txtProveedor.getText() + "' ha sido modificado correctamente",
                                    "Modificar proveedor", JOptionPane.INFORMATION_MESSAGE);

                                crearTablaProveedores();
                                deshabilitarBotonesProveedor();
                                btnNuevoProveedor.setEnabled(true);
                                txtProveedor.setEnabled(false);
                            }
                        }
                    }
            }
        }        
    }//GEN-LAST:event_btnGuardarProveedorActionPerformed

    private void btnEliminarProveedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarProveedorActionPerformed

        String where;
        String prodBorrar="";
        int[] arr;

        arr = tbProveedores.getSelectedRows();

        if(arr.length == 0)
        {
            btnEliminarProveedor.setEnabled(false);

            JOptionPane.showMessageDialog(null, "Debe seleccionar un registro",
                "Eliminar proveedor", JOptionPane.INFORMATION_MESSAGE);

        }
        else
        {
            for (int i=0; i<arr.length; i++)
            {
                prodBorrar = prodBorrar + "           'id " + 
                        modelo.getValueAt(arr[i], 0) + "' - '" + tbProveedores.getValueAt(arr[i], 1)+"'\n";
            }

            int respuesta = JOptionPane.showConfirmDialog(null,
                "Se van a borrar los siguientes " + arr.length + " proveedores: \n"
                + prodBorrar, "Eliminar proveedores", JOptionPane.WARNING_MESSAGE);

            if (respuesta == 0)
            {
                //deshabilito todos los campos de texto y botones (excepto el de Nuevo)
                txtProveedor.setEnabled(false);
                deshabilitarBotonesProveedor();
                btnNuevoProveedor.setEnabled(true);
                txtProveedor.setText("");
                txtIdProveedor.setText("");

                int i = 0;

                while (arr.length > i)
                {
                    where = "id='" + (String) tbProveedores.getValueAt(arr[i], 0) + "'";
                    
                    consulta.eliminar("proveedores", where);
                    i++;
                }

                crearTablaProveedores();
            }
        }        
    }//GEN-LAST:event_btnEliminarProveedorActionPerformed

    private void btnCancelarProveedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarProveedorActionPerformed

        deshabilitarBotonesProveedor();
        
        btnNuevoProveedor.setEnabled(true);
        txtProveedor.setEnabled(false);
        
        txtProveedor.setText(""); 
        txtIdProveedor.setText(""); 
        
        crearTablaProveedores();
    }//GEN-LAST:event_btnCancelarProveedorActionPerformed

    private void tbProveedoresMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbProveedoresMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tbProveedoresMouseClicked

    private void tbProveedoresMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbProveedoresMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_tbProveedoresMouseEntered

    private void tbProveedoresMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbProveedoresMouseReleased

        int[] arr;
        accion = "modificar";

        //almaceno la fila seleccionada en la tabla
        arr = tbProveedores.getSelectedRows();
        //txtProveedor.setEditable(true);
        
        //si no se ha seleccionado ninguna fila getSelectedRow() devuelve -1
        if(arr.length < 1)
        {
            JOptionPane.showMessageDialog(null, "No se ha seleccionado ninguna fila");
        }
        else
        {
            //almaceno el modelo de tabla que se está utilizando en la tabla real
            modelo = (DefaultTableModel) tbProveedores.getModel();
            
            if (arr.length == 1)
            {
                //habilito todos los campos de texto y botones (excepto cancelar)
                txtProveedor.setEnabled(true);
                habilitarBotonesProveedor();
                btnCancelarProveedor.setEnabled(false);
                txtProveedor.setEditable(true);
                
                for (int i = 0; i < arr.length; i++)
                {
                    txtIdProveedor.setText((String)tbProveedores.getValueAt(arr[i], 0));
                    txtProveedor.setText((String)tbProveedores.getValueAt(arr[i], 1)); 
                  txtProveedor.setCaretPosition(0);
                   // txtProveedor.selectAll();
                }

            }
            //si se han seleccionado más de 1 registro
            else
            {
                //habilito los botones (excepto el de cancelar y guardar) y deshabilito campos texto
                habilitarBotonesProveedor();
                txtIdProveedor.setText("");
                txtProveedor.setText("");
                txtProveedor.setEnabled(false);
                btnGuardarProveedor.setEnabled(false);
            }
        }        
    }//GEN-LAST:event_tbProveedoresMouseReleased

    private void tbProveedoresPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_tbProveedoresPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_tbProveedoresPropertyChange

    private void btnNuevaFamiliaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevaFamiliaActionPerformed

        accion="insertar";
        txtFamilia.requestFocusInWindow();
        
        deshabilitarBotonesFamilia();
        
        btnGuardarFamilia.setEnabled(true);
        btnCancelarFamilia.setEnabled(true);        
        txtFamilia.setEnabled(true);         
        txtFamilia.setEditable(true);
        
        txtFamilia.setText("");
        txtIdFamilia.setText("");           
    }//GEN-LAST:event_btnNuevaFamiliaActionPerformed

    private void btnGuardarFamiliaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarFamiliaActionPerformed

        boolean res;
        int filasel;        
        String where;

        if (txtFamilia.getText().equals("") == false)
        {
            switch (accion)
            {
                case "insertar":
                    
                    res = consulta.insert("familia_servicios", "tipo_servicio", "'" + txtFamilia.getText() + "'");
                    
                    if (res == true)
                    {
                        JOptionPane.showMessageDialog(null, "La inserción se ha realizado correctamente",
                            "Añadir familia", JOptionPane.INFORMATION_MESSAGE);

                        crearTablaFamilia();
                        deshabilitarBotonesFamilia();
                        btnNuevaFamilia.setEnabled(true);
                        txtFamilia.setEnabled(false);
                    }

                    break;
                    
                case "modificar":
                    
                    if (tbFamilia.getSelectedRowCount() > 1 || tbFamilia.getSelectedRowCount() < 1)
                    {
                        JOptionPane.showMessageDialog(null, "Debe seleccionar un registro",
                            "Modificar familia", JOptionPane.INFORMATION_MESSAGE);
                    }
                    else
                    {
                        //localizo la fila seleccionada en la tabla
                        filasel = tbFamilia.getSelectedRow();
                        
                        int respuesta = JOptionPane.showConfirmDialog(null,
                            "Se va ha modificar el tipo de servicio con 'id " + modelo2.getValueAt(filasel, 0) +
                            "' - '" + txtFamilia.getText() + "', ¿Está seguro?",
                            "Modificar familia", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                        if (respuesta==0)
                        {
                            if(filasel == -1 || txtFamilia.getText().equals(""))
                            {
                                JOptionPane.showMessageDialog(null, "No se ha seleccionado ningun registro",
                                    "Modificar familia", JOptionPane.INFORMATION_MESSAGE);
                            }

                            else
                            {
                                //almaceno el id de la fila seleccionada
                                where = "id='" + (String) modelo2.getValueAt(filasel, 0) + "'";
                                
                                //llamo al método Update de la clase conexión y le envío tabla, campo, condición
                                consulta.update("familia_servicios", "tipo_servicio", where);

                                JOptionPane.showMessageDialog(null, "El tipo de servicio con id='" + modelo2.getValueAt(filasel, 0) +
                                    "' - '" + txtFamilia.getText() + "' ha sido modificado correctamente",
                                    "Modificar familia", JOptionPane.INFORMATION_MESSAGE);

                                crearTablaFamilia();
                                deshabilitarBotonesFamilia();
                                btnNuevaFamilia.setEnabled(true);
                                txtFamilia.setEnabled(false);
                            }
                        }
                    }
            }
        }              
    }//GEN-LAST:event_btnGuardarFamiliaActionPerformed

    private void btnEliminarFamiliaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarFamiliaActionPerformed

        String where;
        String prodBorrar="";
        int[] arr;

        arr = tbFamilia.getSelectedRows();

        if(arr.length == 0)
        {
            btnEliminarFamilia.setEnabled(false);

            JOptionPane.showMessageDialog(null, "Debe seleccionar un registro",
                "Eliminar familia", JOptionPane.INFORMATION_MESSAGE);

        }
        else
        {
            for (int i=0; i<arr.length; i++)
            {
                prodBorrar = prodBorrar + "           'id " + 
                        modelo2.getValueAt(arr[i], 0) + "' - '" + tbFamilia.getValueAt(arr[i], 1)+"'\n";
            }

            int respuesta = JOptionPane.showConfirmDialog(null,
                "Se van a borrar los siguientes " + arr.length + " tipo de servicios: \n"
                + prodBorrar, "Eliminar familia", JOptionPane.WARNING_MESSAGE);

            if (respuesta == 0)
            {
                //deshabilito todos los campos de texto y botones (excepto el de Nuevo)
                txtFamilia.setEnabled(false);
                deshabilitarBotonesFamilia();
                btnNuevaFamilia.setEnabled(true);
                txtFamilia.setText("");
                txtIdFamilia.setText("");

                int i = 0;

                while (arr.length > i)
                {
                    where = "id='" + (String) tbFamilia.getValueAt(arr[i], 0) + "'";
                    
                    consulta.eliminar("familia_servicios", where);
                    i++;
                }

                crearTablaFamilia();
            }
        }               
    }//GEN-LAST:event_btnEliminarFamiliaActionPerformed

    private void btnCancelarFamiliaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarFamiliaActionPerformed

        deshabilitarBotonesFamilia();
        btnNuevaFamilia.setEnabled(true);
        txtFamilia.setEnabled(false);
        txtFamilia.setText(""); 
        txtIdFamilia.setText(""); 
        crearTablaFamilia();        
    }//GEN-LAST:event_btnCancelarFamiliaActionPerformed

    private void tbFamiliaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbFamiliaMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tbFamiliaMouseClicked

    private void tbFamiliaMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbFamiliaMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_tbFamiliaMouseEntered

    private void tbFamiliaMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbFamiliaMouseReleased

        int[] arr;
        accion = "modificar";

        //almaceno la fila seleccionada en la tabla
        arr = tbFamilia.getSelectedRows();
        
        //si no se ha seleccionado ninguna fila getSelectedRow() devuelve -1
        if(arr.length < 1)
        {
            JOptionPane.showMessageDialog(null, "No se ha seleccionado ninguna fila");
        }
        else
        {
            //almaceno el modelo de tabla que se está utilizando en la tabla real
            modelo2 = (DefaultTableModel) tbFamilia.getModel();
            
            if (arr.length == 1)
            {
                //habilito todos los campos de texto y botones (excepto cancelar)
                txtFamilia.setEnabled(true);
                habilitarBotonesFamilia();
                btnCancelarFamilia.setEnabled(false);
                txtFamilia.setEditable(true);
                
                for (int i = 0; i < arr.length; i++)
                {
                    txtIdFamilia.setText((String)tbFamilia.getValueAt(arr[i], 0));
                    txtFamilia.setText((String)tbFamilia.getValueAt(arr[i], 1));   
                    txtFamilia.setCaretPosition(0);
                }

            }
            //si se han seleccionado más de 1 registro
            else
            {
                //habilito los botones (excepto el de cancelar y guardar) y deshabilito campos texto
                habilitarBotonesFamilia();
                txtIdFamilia.setText("");
                txtFamilia.setText("");
                txtFamilia.setEnabled(false);
                btnGuardarFamilia.setEnabled(false);
            }
        }         
    }//GEN-LAST:event_tbFamiliaMouseReleased

    private void tbFamiliaPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_tbFamiliaPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_tbFamiliaPropertyChange

    private void btnNuevoPagoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoPagoActionPerformed

        accion="insertar";
        txtAlbaran.requestFocusInWindow();
        
        deshabilitarBotonesPago();
        
        btnGuardarPago.setEnabled(true);
        btnCancelarPago.setEnabled(true);      
        
        habilitarTextFieldsPago();
        habilitarTextFieldsPago();
        textFieldsPagoBlanco();        
        
        btnGrupoPagado.setSelected(rbNo.getModel(), true);       
        
    }//GEN-LAST:event_btnNuevoPagoActionPerformed

    private void btnGuardarPagoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarPagoActionPerformed

        boolean res;
        int filasel;        
        String where;
        String[] camposValores;
        
        camposValores = recogerDatosPago();
        
        if (camposValores != null)
        {
            switch (accion)
            {
                case "insertar":                    
                    
                    res = consulta.insert("pago_proveedores", camposValores[0], camposValores[1]);
                    
                    if (res == true)
                    {
                        JOptionPane.showMessageDialog(null, "La inserción se ha realizado correctamente",
                            "Añadir pago", JOptionPane.INFORMATION_MESSAGE);

                        crearTablaPagoProveedores();
                        deshabilitarBotonesPago();
                        btnNuevoPago.setEnabled(true);
                        deshabilitarTextFieldsPago();
                    }

                    break;
                    
                case "modificar":
                    
                    if (tbPagoProveedores.getSelectedRowCount() > 1 || tbPagoProveedores.getSelectedRowCount() < 1)
                    {
                        JOptionPane.showMessageDialog(null, "Debe seleccionar un registro",
                            "Modificar pago", JOptionPane.INFORMATION_MESSAGE);
                    }
                    else
                    {
                        //localizo la fila seleccionada en la tabla
                        filasel = tbPagoProveedores.getSelectedRow();
                        
                        int respuesta = JOptionPane.showConfirmDialog(null,
                            "Se va ha modificar el pago cuyo Nº Albarán es: " + modelo.getValueAt(filasel, 1) +
                            ", ¿Está seguro?", "Modificar pago", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                        if (respuesta==0)
                        {
                            if(filasel == -1)
                            {
                                JOptionPane.showMessageDialog(null, "No se ha seleccionado ningun registro",
                                    "Modificar pago", JOptionPane.INFORMATION_MESSAGE);
                            }

                            else
                            {
                    
                                //almaceno el id de la fila seleccionada
                                where = "id='" + (String) modelo.getValueAt(filasel, 0) + "'";
                                
                                //llamo al método Update de la clase conexión y le envío tabla, campo, condición
                                consulta.update("pago_proveedores", camposValores[1], where);

                                JOptionPane.showMessageDialog(null, "Pago modificado correctamente",
                                    "Modificar pago", JOptionPane.INFORMATION_MESSAGE);

                                crearTablaPagoProveedores();
                                deshabilitarBotonesPago();
                                btnNuevoPago.setEnabled(true);
                                deshabilitarTextFieldsPago();
                            }
                        }
                    }
            }
        }     
    }//GEN-LAST:event_btnGuardarPagoActionPerformed

    private void btnEliminarPagoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarPagoActionPerformed

        String where;
        String prodBorrar="";
        int[] arr;

        arr = tbPagoProveedores.getSelectedRows();

        if(arr.length == 0)
        {
            btnEliminarPago.setEnabled(false);

            JOptionPane.showMessageDialog(null, "Debe seleccionar un registro",
                "Eliminar pago", JOptionPane.INFORMATION_MESSAGE);

        }
        else
        {
            for (int i=0; i < arr.length; i++)
            {
                prodBorrar = prodBorrar + "           NºAlbarán: " + modelo.getValueAt(arr[i], 1) + "\n";
            }

            int respuesta = JOptionPane.showConfirmDialog(null,
                "Se van a borrar los siguientes " + arr.length + " pagos: \n"
                + prodBorrar, "Eliminar pago", JOptionPane.WARNING_MESSAGE);

            if (respuesta == 0)
            {
                //deshabilito todos los campos de texto y botones (excepto el de Nuevo)
                deshabilitarTextFieldsPago();
                deshabilitarBotonesPago();
                btnNuevoPago.setEnabled(true);
                textFieldsPagoBlanco();

                int i = 0;

                while (arr.length > i)
                {
                    where = "id='" + (String) tbPagoProveedores.getValueAt(arr[i], 0) + "'";
                    
                    consulta.eliminar("pago_proveedores", where);
                    i++;
                }

                crearTablaPagoProveedores();
            }
        }             
    }//GEN-LAST:event_btnEliminarPagoActionPerformed

    private void tbPagoProveedoresMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbPagoProveedoresMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tbPagoProveedoresMouseClicked

    private void tbPagoProveedoresMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbPagoProveedoresMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_tbPagoProveedoresMouseEntered

    private void tbPagoProveedoresMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbPagoProveedoresMouseReleased
       
        SimpleDateFormat dateformat;         
        java.util.Date fecha = null;

        //Configuro el formato de fecha que voy a dar luego
        dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
        
        //Asigno un formato de fecha deseado al calendario 
        jDateChooser.setDateFormatString("dd-MM-yyyy HH:mm:ss");
        
        int[] arr;
        accion = "modificar";

        //almaceno la fila seleccionada en la tabla
        arr = tbPagoProveedores.getSelectedRows();

        //si no se ha seleccionado ninguna fila getSelectedRow() devuelve -1
        if(arr.length < 1)
        {
            JOptionPane.showMessageDialog(null, "No se ha seleccionado ninguna fila");
        }
        else
        {
            //almaceno el modelo de tabla que se está utilizando en la tabla real
            modelo = (DefaultTableModel) tbPagoProveedores.getModel();
            
            if (arr.length == 1)
            {
                //habilito todos los campos de texto y botones (excepto cancelar)
                habilitarBotonesPago();
                habilitarTextFieldsPago();
                btnCancelarPago.setEnabled(false);                
                
                for (int i = 0; i < arr.length; i++)
                {
                    txtIdPago.setText((String)tbPagoProveedores.getValueAt(arr[i], 0));
                    txtAlbaran.setText((String)tbPagoProveedores.getValueAt(arr[i], 1));     
                    txtProveedorPago.setText((String)tbPagoProveedores.getValueAt(arr[i], 2));
                    txtDescripcionPago.setText((String)tbPagoProveedores.getValueAt(arr[i], 3)); 
                    txtTotalPago.setText((String)tbPagoProveedores.getValueAt(arr[i], 4));
                    txtEntregado.setText((String)tbPagoProveedores.getValueAt(arr[i], 5)); 
                    
                    try 
                    {      
                        //Obtengo la fecha de la base de datos, y la formateo con el formato antes definido
                        fecha = dateformat.parse(tbPagoProveedores.getValueAt(arr[i], 6).toString());
                    } 
                    catch (ParseException ex) 
                    {
                        JOptionPane.showMessageDialog(null, ex);
                    }
                    
                    jDateChooser.setDate(fecha);                    
                    txtFormaPago.setText((String)tbPagoProveedores.getValueAt(arr[i], 7));  
                    
                    //Dependiendo del valor encontrado en la tabla, activo un radiobutton u otro
                    if (tbPagoProveedores.getValueAt(arr[i], 8).toString().equals("No"))
                    {                        
                        btnGrupoPagado.setSelected(rbNo.getModel(), true);
                    }
                    else
                    {
                        btnGrupoPagado.setSelected(rbSi.getModel(), true);
                    }
                    
                }

            }
            //si se han seleccionado más de 1 registro
            else
            {
                //habilito los botones (excepto el de cancelar y guardar) y deshabilito campos texto
                habilitarBotonesPago();
                deshabilitarTextFieldsPago();     
                textFieldsPagoBlanco();
                btnGuardarPago.setEnabled(false);
            }
        }         
    }//GEN-LAST:event_tbPagoProveedoresMouseReleased

    private void tbPagoProveedoresPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_tbPagoProveedoresPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_tbPagoProveedoresPropertyChange

    private void rbSiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbSiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rbSiActionPerformed

    private void jTabbedPane1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTabbedPane1MouseClicked
   
        selectorExportar.setVisible(false);


        
            
    }//GEN-LAST:event_jTabbedPane1MouseClicked

    private void btnCancelarPagoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarPagoActionPerformed

        deshabilitarBotonesPago();
        btnNuevoPago.setEnabled(true);
        deshabilitarTextFieldsPago();
        textFieldsPagoBlanco();

        crearTablaPagoProveedores();            
    }//GEN-LAST:event_btnCancelarPagoActionPerformed


    private void jDateChooserMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jDateChooserMouseClicked
 
    }//GEN-LAST:event_jDateChooserMouseClicked

    private void tbDatosEmpleadosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbDatosEmpleadosMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tbDatosEmpleadosMouseClicked

    private void tbDatosEmpleadosMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbDatosEmpleadosMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_tbDatosEmpleadosMouseEntered

    private void tbDatosEmpleadosMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbDatosEmpleadosMouseReleased
       
        SimpleDateFormat dateformat;         
        java.util.Date fecha = null;

        //Configuro el formato de fecha que voy a dar luego
        dateformat = new SimpleDateFormat("yyyy-MM-dd"); 
        
        //Asigno un formato de fecha deseado al calendario 
        calendarioEmpleados.setDateFormatString("dd-MM-yyyy");
        
        int[] arr;
        accion = "modificar";

        //almaceno la fila seleccionada en la tabla
        arr = tbDatosEmpleados.getSelectedRows();

        //si no se ha seleccionado ninguna fila getSelectedRow() devuelve -1
        if(arr.length < 1)
        {
            JOptionPane.showMessageDialog(null, "No se ha seleccionado ninguna fila");
        }
        else
        {
            //almaceno el modelo de tabla que se está utilizando en la tabla real
            modelo = (DefaultTableModel) tbDatosEmpleados.getModel();
            
            if (arr.length == 1)
            {
                //Si se trata del usuario infostetic con id=1, no permito que se modifique
                if (tbDatosEmpleados.getValueAt(arr[0], 0).equals("1"))
                {
                    //deshabilito los botones (excepto el de nuevo) y deshabilito campos texto
                    deshabilitarBotonesEmpleados();
                    deshabilitarTextFieldsEmpleados();     
                    btnNuevoEmpleado.setEnabled(true);                                        
                }
                else
                {                                    
                    //habilito todos los campos de texto y botones (excepto cancelar)
                    habilitarBotonesEmpleados();
                    habilitarTextFieldsEmpleados();
                    btnCancelarEmpleado.setEnabled(false);                
                }
                    txtIdEmpleado.setText((String)tbDatosEmpleados.getValueAt(arr[0], 0));
                    txtDniEmpleado.setText((String)tbDatosEmpleados.getValueAt(arr[0], 1));     
                    txtNombreEmpleado.setText((String)tbDatosEmpleados.getValueAt(arr[0], 2));
                    txtApellidosEmpleado.setText((String)tbDatosEmpleados.getValueAt(arr[0], 3)); 
                    txtDireccionEmpleado.setText((String)tbDatosEmpleados.getValueAt(arr[0], 4));
                    txtTelefonoFijo.setText((String)tbDatosEmpleados.getValueAt(arr[0], 5)); 
                    txtTelefonoMovil.setText((String)tbDatosEmpleados.getValueAt(arr[0], 6)); 
                    txtEmailEmpleado.setText((String)tbDatosEmpleados.getValueAt(arr[0], 7)); 
                    txtFechaNac.setText("");
                    
                    if (tbDatosEmpleados.getValueAt(arr[0], 8) != null)
                    {
                        try 
                        {      
                            //Obtengo la fecha de la base de datos, y la formateo con el formato antes definido
                            fecha = dateformat.parse(tbDatosEmpleados.getValueAt(arr[0], 8).toString());
                        } 
                        catch (ParseException ex) 
                        {
                           JOptionPane.showMessageDialog(null, ex);
                        }
                       calendarioEmpleados.setDate(fecha); 
                       dateformat = new SimpleDateFormat("dd-MM-yyyy"); 
                       txtFechaNac.setText(dateformat.format(calendarioEmpleados.getDate()));
                    }
                    
                    txtContraseñaEmpleado.setText((String)tbDatosEmpleados.getValueAt(arr[0], 9)); 
                    
                    if (tbDatosEmpleados.getValueAt(arr[0], 10).equals("administrador"))
                    {
                        cbCargo.setSelectedIndex(0);
                    }
                    else
                    {
                        cbCargo.setSelectedIndex(1);
                    }
                    
                    /*txtCargoEmpleado.setText((String)tbDatosEmpleados.getValueAt(arr[0], 10));   
                    
                    //Si el empleado elegido es el administrador, evito que se modifique el cargo.
                    if (txtCargoEmpleado.getText().equals("administrador"))
                    {
                        txtCargoEmpleado.setEditable(false);
                    }
                    else
                    {
                        txtCargoEmpleado.setEditable(true);
                    }*/
                

            }
            //si se han seleccionado más de 1 registro
            else
            {
                //habilito los botones (excepto el de cancelar y guardar) y deshabilito campos texto
                habilitarBotonesEmpleados();
                deshabilitarTextFieldsEmpleados();     
                textFieldsEmpleadosBlanco();
                btnGuardarEmpleado.setEnabled(false);
            }
        }             
    }//GEN-LAST:event_tbDatosEmpleadosMouseReleased

    private void tbDatosEmpleadosPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_tbDatosEmpleadosPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_tbDatosEmpleadosPropertyChange

    private void btnNuevoEmpleadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoEmpleadoActionPerformed

        accion="insertar";
        txtDniEmpleado.requestFocusInWindow();
        
        deshabilitarBotonesEmpleados();
        
        btnGuardarEmpleado.setEnabled(true);
        btnCancelarEmpleado.setEnabled(true);      
        
        habilitarTextFieldsEmpleados();
        habilitarTextFieldsEmpleados();
        textFieldsEmpleadosBlanco();          
        
    }//GEN-LAST:event_btnNuevoEmpleadoActionPerformed

    private void btnGuardarEmpleadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarEmpleadoActionPerformed

        boolean res;
        int filasel;        
        String where;
        String[] camposValores;
        
        camposValores = recogerDatosEmpleados();
        
        if (camposValores != null)
        {
            switch (accion)
            {
                case "insertar":                    
                    
                    res = consulta.insert("personal", camposValores[0], camposValores[1]);
                    
                    if (res == true)
                    {
                        JOptionPane.showMessageDialog(null, "La inserción se ha realizado correctamente",
                            "Añadir Empleado", JOptionPane.INFORMATION_MESSAGE);

                        crearTablaDatosEmpleados();
                        deshabilitarBotonesEmpleados();
                        btnNuevoEmpleado.setEnabled(true);
                        deshabilitarTextFieldsEmpleados();
                    }

                    break;
                    
                case "modificar":
                    
                    if (tbDatosEmpleados.getSelectedRowCount() > 1 || tbDatosEmpleados.getSelectedRowCount() < 1)
                    {
                        JOptionPane.showMessageDialog(null, "Debe seleccionar un registro",
                            "Modificar Empleado", JOptionPane.INFORMATION_MESSAGE);
                    }
                    else
                    {
                        //localizo la fila seleccionada en la tabla
                        filasel = tbDatosEmpleados.getSelectedRow();
                        
                        int respuesta = JOptionPane.showConfirmDialog(null,
                            "Se va ha modificar el empleado: " + modelo.getValueAt(filasel, 2) + " " + modelo.getValueAt(filasel, 3) +
                            ", ¿Está seguro?", "Modificar empleado", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                        if (respuesta==0)
                        {
                            if(filasel == -1)
                            {
                                JOptionPane.showMessageDialog(null, "No se ha seleccionado ningun registro",
                                    "Modificar empleado", JOptionPane.INFORMATION_MESSAGE);
                            }

                            else
                            {
                    
                                //almaceno el id de la fila seleccionada
                                where = "id='" + (String) modelo.getValueAt(filasel, 0) + "'";
                                
                                //llamo al método Update de la clase conexión y le envío tabla, campo, condición
                                consulta.update("personal", camposValores[1], where);

                                JOptionPane.showMessageDialog(null, "Empleado modificado correctamente",
                                    "Modificar empleado", JOptionPane.INFORMATION_MESSAGE);

                                calendarioEmpleados.setDate(null);
                                crearTablaDatosEmpleados();
                                deshabilitarBotonesEmpleados();
                                btnNuevoEmpleado.setEnabled(true);
                                deshabilitarTextFieldsEmpleados();
                                
                                
                            }
                        }
                    }
            }
        }     
        
    }//GEN-LAST:event_btnGuardarEmpleadoActionPerformed

    private void btnEliminarEmpleadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarEmpleadoActionPerformed

        String where;
        String empBorrar="";
        Boolean usuarioSistema;
        usuarioSistema = false;
        int[] arr;

        arr = tbDatosEmpleados.getSelectedRows();

        if(arr.length == 0)
        {
            btnEliminarEmpleado.setEnabled(false);

            JOptionPane.showMessageDialog(null, "Debe seleccionar un empleado",
                "Eliminar empleados", JOptionPane.INFORMATION_MESSAGE);

        }
        else
        {            
            for (int i=0; i<arr.length; i++)
            {
                if (tbDatosEmpleados.getValueAt(arr[i], 0).equals("1"))
                {
                    usuarioSistema = true;
                    JOptionPane.showMessageDialog(null, "El usuario infostetic no puede ser eliminado.",
                    "Eliminar empleados", JOptionPane.INFORMATION_MESSAGE);
                }
                else
                {
                    empBorrar = empBorrar + " id " + 
                        tbDatosEmpleados.getValueAt(arr[i], 0) + " - " + tbDatosEmpleados.getValueAt(arr[i], 2) + "\n";          
                }
                
            }

            int numUsuarios = arr.length;
            int i=0;
            //Si se pretende borrar el usuario infostetic, se lo quito de la selección de usuarios a borrar
            if (usuarioSistema == true)
            {
                numUsuarios = arr.length-1;
                i=1; //El primer registro que es el usuario infostetic no permito borrarlo por eso empiezo desde el 1 en vez de i=0
            }
            if (numUsuarios != 0)
            {
                int respuesta = JOptionPane.showConfirmDialog(null,
                    "Se van a borrar " + numUsuarios + " empleados: \n\n"
                    + empBorrar, "Eliminar empleados", JOptionPane.WARNING_MESSAGE);
            
                if (respuesta==0)                
                {
                    //habilito los botones (excepto el de cancelar y guardar) y deshabilito campos texto
                    habilitarBotonesEmpleados();
                    deshabilitarTextFieldsEmpleados();     
                    textFieldsEmpleadosBlanco();
                    btnGuardarEmpleado.setEnabled(false);                
                    
                    consulta = new Conexion();                

                    while (arr.length>i)
                    {
                        where = "id='"+(String) tbDatosEmpleados.getValueAt(arr[i], 0)+"'";
                        consulta.eliminar("personal", where);
                        i++;
                    }

                    crearTablaDatosEmpleados();
                }
            }
        }
    }//GEN-LAST:event_btnEliminarEmpleadoActionPerformed

    private void btnCancelarEmpleadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarEmpleadoActionPerformed

        deshabilitarBotonesEmpleados();
        btnNuevoEmpleado.setEnabled(true);
        deshabilitarTextFieldsEmpleados();
        textFieldsEmpleadosBlanco();

        crearTablaDatosEmpleados();          
    }//GEN-LAST:event_btnCancelarEmpleadoActionPerformed

    private void txtFechaNacActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFechaNacActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFechaNacActionPerformed

    private void btnCopiaSeguridadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCopiaSeguridadActionPerformed
 
        try 
        {
            String archivo = null;

            selectorExportar.setVisible(true);                        

            int result = selectorExportar.showSaveDialog(null);

            if(result == JFileChooser.APPROVE_OPTION)
            {
                //Guardo el fichero con el nombre que hemos configurado, y me aseguro de sustituir los espacios
                //en blanco por guiones bajos, ya que si ponemos: "copia bd.sql" nos guardará "copia.sql"
                archivo = selectorExportar.getSelectedFile().toString().concat(".sql").replace(' ', '_');

                File file = new File(archivo); 

                if(file.exists())
                {
                    Object[] options = { "Sí", "No" };
                    int opcao = JOptionPane.showOptionDialog(null,"Este archivo ya existe, ¿desea sobreescribirlo?", "Atención!!!",
                                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,options, options[0]);

                    if (opcao == JOptionPane.YES_OPTION) 
                    {                
                        Runtime bck = Runtime.getRuntime();   
                        bck.exec("C://InfoStetic//mysql//bin//mysqldump.exe" + " -v -v -v --host=localhost "
                               + "--user=root --password=toor --port=3306 --protocol=tcp --force --allow-keywords --compress "
                               + "--add-drop-table --default-character-set=latin1 --hex-blob  --result-file="+archivo+" --databases db_infostetic");  

                        JOptionPane.showMessageDialog(null, "La copia se ha realizado correctamente.",
                                    "Realizar copia de seguridad", JOptionPane.INFORMATION_MESSAGE);   
                    }
                    else
                    {
                        btnCopiaSeguridadActionPerformed(evt);
                    }
                }
                else
                {
       /*                 new ProcessBuilder("C:\\Program Files\\MySQL\\MySQL Server 5.0\\bin\\mysqldump.exe").start(); 
Runtime.getRuntime().exec(new String[]{"C:\\Program Files\\MySQL\\MySQL Server 5.0\\bin\\mysqldump.exe"+ " -v -v -v --host=localhost "
                               + "--user=root --password=toor --port=3306 --protocol=tcp --force --allow-keywords --compress "
                               + "--add-drop-table --default-character-set=latin1 --hex-blob  --result-file="+archivo+" --databases db_infostetic"}) ;
Runtime.getRuntime().exec("\"C:\\Program Files\\MySQL\\MySQL Server 5.0\\bin\\mysqldump.exe\""+ " -v -v -v --host=localhost "
                               + "--user=root --password=toor --port=3306 --protocol=tcp --force --allow-keywords --compress "
                               + "--add-drop-table --default-character-set=latin1 --hex-blob  --result-file="+archivo+" --databases db_infostetic") ;*/
                    
                   Runtime bck = Runtime.getRuntime();   
                    bck.exec("C://InfoStetic//mysql//bin//mysqldump.exe" + " -v -v -v --host=localhost "
                           + "--user=root --password=toor --port=3306 --protocol=tcp --force --allow-keywords --compress  "
                           + "--add-drop-table --default-character-set=latin1 --hex-blob  --result-file="+archivo+" --databases db_infostetic");  

                    JOptionPane.showMessageDialog(null, "La copia se ha realizado correctamente.",
                                "Realizar copia de seguridad", JOptionPane.INFORMATION_MESSAGE);   
                }

            }
        }
        catch (Exception e) 
        {
            JOptionPane.showMessageDialog(null, e, "Se ha producido un error", 2);            
        }    
    }//GEN-LAST:event_btnCopiaSeguridadActionPerformed

    private void btnRestaurarBDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRestaurarBDActionPerformed
              
        try 
        {
            selectorImportar.setVisible(true);  
            String bd = "test";
            int result = selectorImportar.showOpenDialog(null); 
 
            if(result == JFileChooser.OPEN_DIALOG)
            {
  
               File bkp;  
               bkp = selectorImportar.getSelectedFile();  
               String archivo = bkp.getPath();  
               System.out.println("bd "+ bd);
               System.out.println("arq "+ archivo); 

               String cad = new String();
               cad = "cmd.exe /C \"C:\\InfoStetic\\mysql\\bin\\mysql.exe\" -u root --password=toor -h localhost "+bd+" < "+archivo;
               Runtime rt = Runtime.getRuntime();
               proc = rt.exec(cad);
               System.out.println("Execing " + cad);


               // Muestro mensaje de error si lo hay
                StreamGobbler errorGobbler = new 
                StreamGobbler(proc.getErrorStream(), "ERROR");            

                StreamGobbler outputGobbler = new 
                StreamGobbler(proc.getInputStream(), "OUTPUT");

                // kick them off
                errorGobbler.run();
                outputGobbler.run();
                                    
                // any error???
                int exitVal = proc.waitFor();
             
                if (exitVal == 0)
                {  
                    JOptionPane.showMessageDialog(null, "La Base de Datos se ha restaurado correctamente.",
                                 "Restaurar Base de Datos", JOptionPane.INFORMATION_MESSAGE);                                        
                }  
                else
                {  
                    JOptionPane.showMessageDialog(null, "Ocurrió un problema al restaurar la Base de Datos. \n "
                                                  + "Compruebe el nombre de la base de datos o póngase en contacto con el administrador.",
                                 "Restaurar Base de Datos", JOptionPane.WARNING_MESSAGE);                                             
                }
            }
             
        } 
        catch (Exception e) 
        {
            JOptionPane.showMessageDialog(null, e, "Se ha producido un error", 2);            
        }  
    }//GEN-LAST:event_btnRestaurarBDActionPerformed

    private void txtDniEmpleadoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDniEmpleadoKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDniEmpleadoKeyTyped

    private void txtProveedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtProveedorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtProveedorActionPerformed

    private void txtProveedorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtProveedorMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_txtProveedorMouseClicked

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
            java.util.logging.Logger.getLogger(Configuracion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Configuracion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Configuracion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Configuracion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Configuracion dialog = new Configuracion(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnCancelarEmpleado;
    private javax.swing.JButton btnCancelarFamilia;
    private javax.swing.JButton btnCancelarPago;
    private javax.swing.JButton btnCancelarProveedor;
    private javax.swing.JButton btnCopiaSeguridad;
    private javax.swing.JButton btnEliminarEmpleado;
    private javax.swing.JButton btnEliminarFamilia;
    private javax.swing.JButton btnEliminarPago;
    private javax.swing.JButton btnEliminarProveedor;
    private javax.swing.JButton btnGuardarEmpleado;
    private javax.swing.JButton btnGuardarFamilia;
    private javax.swing.JButton btnGuardarPago;
    private javax.swing.JButton btnGuardarProveedor;
    private javax.swing.JButton btnNuevaFamilia;
    private javax.swing.JButton btnNuevoEmpleado;
    private javax.swing.JButton btnNuevoPago;
    private javax.swing.JButton btnNuevoProveedor;
    private javax.swing.JButton btnRestaurarBD;
    private com.toedter.calendar.JDateChooser calendarioEmpleados;
    private javax.swing.JComboBox cbCargo;
    private com.toedter.calendar.JDateChooser jDateChooser;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lbAlbaran;
    private javax.swing.JLabel lbApellidosEmpleado;
    private javax.swing.JLabel lbCargoEmpleado;
    private javax.swing.JLabel lbCargoEmpleado1;
    private javax.swing.JLabel lbContraseñaEmpleado;
    private javax.swing.JLabel lbDatosEmpleados;
    private javax.swing.JLabel lbDatosEmpleados1;
    private javax.swing.JLabel lbDatosEmpleados2;
    private javax.swing.JLabel lbDescripcionPago;
    private javax.swing.JLabel lbDireccionEmpleado;
    private javax.swing.JLabel lbDniEmpleado;
    private javax.swing.JLabel lbEmailEmpleado;
    private javax.swing.JLabel lbEntregado;
    private javax.swing.JLabel lbFamilia;
    private javax.swing.JLabel lbFechaPago;
    private javax.swing.JLabel lbFormaPago;
    private javax.swing.JLabel lbIdEmpleado;
    private javax.swing.JLabel lbIdFamilia;
    private javax.swing.JLabel lbIdPago;
    private javax.swing.JLabel lbIdProveedor;
    private javax.swing.JLabel lbNombreEmpleado;
    private javax.swing.JLabel lbPagado;
    private javax.swing.JLabel lbProveedor;
    private javax.swing.JLabel lbProveedorPago;
    private javax.swing.JLabel lbTablaFamilia;
    private javax.swing.JLabel lbTablaProveedores;
    private javax.swing.JLabel lbTablaProveedores1;
    private javax.swing.JLabel lbTelefonoFijo;
    private javax.swing.JLabel lbTelefonoMovil;
    private javax.swing.JLabel lbTotalPago;
    private javax.swing.JPanel panelDatosEmpleados;
    private javax.swing.JPanel panelFamilia;
    private javax.swing.JPanel panelPagado;
    private javax.swing.JPanel panelProveedores;
    private javax.swing.JRadioButton rbNo;
    private javax.swing.JRadioButton rbSi;
    private javax.swing.JFileChooser selectorExportar;
    private javax.swing.JFileChooser selectorImportar;
    private javax.swing.JTable tbDatosEmpleados;
    private javax.swing.JTable tbFamilia;
    private javax.swing.JTable tbPagoProveedores;
    private javax.swing.JTable tbProveedores;
    private javax.swing.JTextField txtAlbaran;
    private javax.swing.JTextField txtApellidosEmpleado;
    private javax.swing.JTextField txtContraseñaEmpleado;
    private javax.swing.JTextField txtDescripcionPago;
    private javax.swing.JTextField txtDireccionEmpleado;
    private javax.swing.JTextField txtDniEmpleado;
    private javax.swing.JTextField txtEmailEmpleado;
    private javax.swing.JTextField txtEntregado;
    private javax.swing.JTextField txtFamilia;
    private javax.swing.JTextField txtFechaNac;
    private javax.swing.JTextField txtFormaPago;
    private javax.swing.JTextField txtIdEmpleado;
    private javax.swing.JTextField txtIdFamilia;
    private javax.swing.JTextField txtIdPago;
    private javax.swing.JTextField txtIdProveedor;
    private javax.swing.JTextField txtNombreEmpleado;
    private javax.swing.JTextField txtProveedor;
    private javax.swing.JTextField txtProveedorPago;
    private javax.swing.JTextField txtTelefonoFijo;
    private javax.swing.JTextField txtTelefonoMovil;
    private javax.swing.JTextField txtTotalPago;
    // End of variables declaration//GEN-END:variables
}

