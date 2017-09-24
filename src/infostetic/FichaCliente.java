/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package infostetic;

import java.awt.Color;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Javi
 */

public class FichaCliente extends javax.swing.JFrame
{

    Conexion consulta;
    String accion = "";    
    ResultSet rs = null;
    DefaultTableModel modelo;
    String[] columNames;
    
    public FichaCliente ()
    {
        
        initComponents();
        
        setLocationRelativeTo(null);//centro la ventana en medio de la pantalla
        
        lbCliente.setText(SelCliente.nomCliente);
        
        columNames = new String[] {"id", "producto", "cantidad", "precio", "total",
                                   "forma_pago", "metalico", "tarjeta", "fecha", "observaciones"
                                  };    
        
        consulta = new Conexion();
        rs = consulta.buscarRegistro("clientes", "*", " where id=" + SelCliente.idCliente, null);
        
        try 
        {
            while (rs.next())
            {
                txtCodCliente.setText(rs.getString("id"));
                txtNomCliente.setText(rs.getString("nombre"));
                txtApeCliente.setText(rs.getString("apellidos"));
                txtDni.setText(rs.getString("dni"));
                txtDireccion.setText(rs.getString("direccion"));
                txtCodPostal.setText(rs.getString("codigo_postal"));
                txtPoblacion.setText(rs.getString("poblacion")); 
                txtProvincia.setText(rs.getString("provincia"));
                txtTelfnFijo.setText(rs.getString("telefono_fijo"));
                txtTelfnMovil.setText(rs.getString("telefono_movil"));
                txtFecNac.setText(rs.getString("fecha_nacimiento"));
                txtFecAlta.setText(rs.getString("fecha_alta")); 
                txtEmail.setText(rs.getString("email"));
                txtObservaciones.setText(rs.getString("observaciones"));                                 
            }
        } 
        catch (SQLException ex) 
        {
             JOptionPane.showMessageDialog(null, ex);   
        }
        
        crearTabla(" where id_cliente=" + SelCliente.idCliente);
        
        if (SelCliente.nomCliente.equals("Cliente sin ficha"))
        {
            deshabilitarBotonesCliente();     
            deshabilitarTextFieldClientes();
        }
    }
    
    private void crearTabla (String where)
    {  
        BigDecimal totalGastado = new BigDecimal (0);
        BigDecimal totalDebito = new BigDecimal (0);
        String camposHistorico = "id, producto, cantidad, precio, total, forma_pago, "
                                + "metalico, tarjeta, fecha, observaciones";  
        
        
        String campos = "total";

        String where2 = where + " AND producto LIKE 'TOTAL'";
        
        rs = consulta.selectResult("historico", campos, where2);        

        try 
        {
            Boolean vacio = true;
            while (rs.next())
            {                                

                    totalGastado = totalGastado.add(BigDecimal.valueOf(Double.parseDouble(rs.getString("total"))));
                    txtTotalGastado.setText(totalGastado.toString());  
                
                vacio = false;
            }
            if (vacio)
            {
                txtTotalGastado.setText("0");  
            }
        } 
        catch (SQLException ex) 
        {
            JOptionPane.showMessageDialog(null, ex);
        }  
        
        //campos = "total";

        where2 = where + " AND producto LIKE 'DEBITO'";

        rs = consulta.selectResult("historico", campos, where2);        

        try 
        {
            while (rs.next())
            {                     
                totalDebito = totalDebito.add(BigDecimal.valueOf(Double.parseDouble(rs.getString("total"))));
                txtTotalDebito.setText(totalDebito.toString());  
            }
        } 
        catch (SQLException ex) 
        {
            
            JOptionPane.showMessageDialog(null, ex);
        }        
        
        where = where + " ORDER BY fecha DESC";
        
        modelo = consulta.selectModelo("historico", camposHistorico, where, columNames);
        
        tbHistorico.setModel(modelo); 
        
        //La columna id no la muestro, pero me hará falta para eliminar el registro
        tbHistorico.getColumnModel().getColumn(0).setMaxWidth(0);
        tbHistorico.getColumnModel().getColumn(0).setMinWidth(0);
        tbHistorico.getColumnModel().getColumn(0).setPreferredWidth(0); 
        
        //La columna observaciones no la muestro porque está muy justo el espacio en la tabla
        tbHistorico.getColumnModel().getColumn(9).setMaxWidth(0);
        tbHistorico.getColumnModel().getColumn(9).setMinWidth(0);
        tbHistorico.getColumnModel().getColumn(9).setPreferredWidth(0);         
            
        tbHistorico.getColumnModel().getColumn(1).setPreferredWidth(240);  //producto
        tbHistorico.getColumnModel().getColumn(2).setPreferredWidth(40); //cantidad
        tbHistorico.getColumnModel().getColumn(3).setPreferredWidth(60); //precio
        tbHistorico.getColumnModel().getColumn(4).setPreferredWidth(80); //total
        tbHistorico.getColumnModel().getColumn(5).setPreferredWidth(80); //forma_pago
        tbHistorico.getColumnModel().getColumn(6).setPreferredWidth(70); //metalico
        tbHistorico.getColumnModel().getColumn(7).setPreferredWidth(70); //tarjeta
        tbHistorico.getColumnModel().getColumn(8).setPreferredWidth(150); //fecha
        
        /*
         * columna 0 : id
         * columna 1 : producto
         * columna 2 : cantidad
         * columna 3 : precio
         * columna 4 : total
         * columna 5 : forma_pago
         * columna 6 : metalico
         * columna 7 : tarjeta
         * columna 8 : fecha
         * columna 9 : observaciones                          
         */               
    }    

    private boolean comprobarTextFieldCliente()
    {               
        if (txtNomCliente.getText().equals("") || txtApeCliente.getText().equals(""))
        {
             JOptionPane.showMessageDialog(null, "Los campos 'Nombre' y 'Apellidos' son obligatorios.",
                          "Acción cliente", JOptionPane.INFORMATION_MESSAGE);            
        
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
                       + "El formato correcto es: AAAA-MM-DD",
                       "Acción cliente", JOptionPane.INFORMATION_MESSAGE);
                
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
    
    private static boolean isFechaValida(String fechax) 
    {
  try 
  {
      SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
      formatoFecha.setLenient(false);
      formatoFecha.parse(fechax);
  } 
  catch (ParseException e) 
  {
      return false;
  }
  return true;
}    
    
    private String[] recogerDatosClientes()
    {
        String valores="";        
        String[][] datoProd=new String[13][2];
        String[] resultado=new String[2];        
        
        datoProd [0][0]="dni";
        datoProd [1][0]="nombre";
        datoProd [2][0]="apellidos";
        datoProd [3][0]="direccion";
        datoProd [4][0]="codigo_postal";
        datoProd [5][0]="poblacion";
        datoProd [6][0]="provincia";
        datoProd [7][0]="telefono_fijo";
        datoProd [8][0]="telefono_movil";
        datoProd [9][0]="fecha_nacimiento";
        datoProd [10][0]="fecha_alta";
        datoProd [11][0]="email";        
        datoProd [12][0]="observaciones";                      
        
        datoProd [0][1]=txtDni.getText();
        datoProd [1][1]=txtNomCliente.getText();
        datoProd [2][1]=txtApeCliente.getText();
        datoProd [3][1]=txtDireccion.getText();
        datoProd [4][1]=txtCodPostal.getText();
        datoProd [5][1]=txtPoblacion.getText();
        datoProd [6][1]=txtProvincia.getText();
        datoProd [7][1]=txtTelfnFijo.getText();
        datoProd [8][1]=txtTelfnMovil.getText();
        datoProd [9][1]=txtFecNac.getText();        
        datoProd [10][1]=txtFecAlta.getText();
        datoProd [11][1]=txtEmail.getText();
        datoProd [12][1]=txtObservaciones.getText();              


        for(int j=0; j < datoProd.length; j++)
        {
            if(datoProd[j][1].equals("") == false)
            {                
                valores = valores + datoProd[j][0] + "='" + datoProd[j][1] + "', ";
            }
        }        
        //elimino la ',' del final, indicando desde y hasta donde copiar la cadena
        valores = valores.substring(0, valores.length() - 2); 

        resultado[0] = "";
        resultado[1] = valores;
                      
        return resultado;
    }
 
    
    private void habilitarTextFieldClientes()
    {
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
    }
    
    //método para deshabilitar los campos de texto de Productos
    private void deshabilitarTextFieldClientes()
    {
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
        txtObservaciones.setBackground(new Color (240,240,240));

    }
    
    //método para habilitar los botones del formulario Productos
    private void habilitarBotonesCliente()
    {        
        btnGuardarCliente.setEnabled(true);
        btnEliminarCliente.setEnabled(true);     
    }
    
     //método para deshabilitar los botones del formulario Productos
    private void deshabilitarBotonesCliente()
    {        
        btnGuardarCliente.setEnabled(false);
        btnEliminarCliente.setEnabled(false); 
        btnFotosCliente.setEnabled(false);
    }     
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lbCliente = new javax.swing.JLabel();
        panelHistorico = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbHistorico = new javax.swing.JTable();
        lbHistorico = new javax.swing.JLabel();
        panelBtnHistorico = new javax.swing.JPanel();
        btnEliminarHistorico = new javax.swing.JButton();
        btnSalir = new javax.swing.JButton();
        btnVerOcultar = new javax.swing.JButton();
        panelGasto = new javax.swing.JPanel();
        panelTotalGastado = new org.edisoncor.gui.panel.PanelRect();
        txtTotalGastado = new javax.swing.JTextField();
        lbTotal = new javax.swing.JLabel();
        lbDebito = new javax.swing.JLabel();
        panelTotalDebito = new org.edisoncor.gui.panel.PanelRect();
        txtTotalDebito = new javax.swing.JTextField();
        lbDebito1 = new javax.swing.JLabel();
        lbTotal1 = new javax.swing.JLabel();
        panelCliente = new javax.swing.JPanel();
        lbDatosCliente = new javax.swing.JLabel();
        panelDatos = new javax.swing.JPanel();
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
        txtProvincia = new javax.swing.JTextField();
        txtTelfnMovil = new javax.swing.JTextField();
        txtEmail = new javax.swing.JTextField();
        txtFecAlta = new javax.swing.JTextField();
        lbDni = new javax.swing.JLabel();
        txtDni = new javax.swing.JTextField();
        lbCodPostal = new javax.swing.JLabel();
        lbProvincia = new javax.swing.JLabel();
        lbTlfnFijo = new javax.swing.JLabel();
        lbFecNac = new javax.swing.JLabel();
        lbEmail = new javax.swing.JLabel();
        lbObservaciones = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtObservaciones = new javax.swing.JTextArea();
        txtTelfnFijo = new javax.swing.JTextField();
        lbTlfnMovil = new javax.swing.JLabel();
        txtFecNac = new javax.swing.JTextField();
        lbFecAlta = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        btnFotosCliente = new javax.swing.JButton();
        btnGuardarCliente = new javax.swing.JButton();
        btnEliminarCliente = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1000, 755));

        lbCliente.setFont(new java.awt.Font("Cambria", 0, 48)); // NOI18N
        lbCliente.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbCliente.setText("Cliente");
        lbCliente.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lbCliente.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        tbHistorico.setAutoCreateRowSorter(true);
        tbHistorico.setFont(new java.awt.Font("Arial Unicode MS", 0, 12)); // NOI18N
        tbHistorico.setModel(new javax.swing.table.DefaultTableModel(
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
        tbHistorico.setRowHeight(18);
        tbHistorico.getTableHeader().setReorderingAllowed(false);
        tbHistorico.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tbHistoricoMouseReleased(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbHistoricoMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                tbHistoricoMouseEntered(evt);
            }
        });
        tbHistorico.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                tbHistoricoPropertyChange(evt);
            }
        });
        jScrollPane1.setViewportView(tbHistorico);

        lbHistorico.setFont(new java.awt.Font("Cambria", 0, 24)); // NOI18N
        lbHistorico.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbHistorico.setText("Histórico");
        lbHistorico.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lbHistorico.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        panelBtnHistorico.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

        btnEliminarHistorico.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/papelera_grande.png"))); // NOI18N
        btnEliminarHistorico.setText("Eliminar histórico");
        btnEliminarHistorico.setEnabled(false);
        btnEliminarHistorico.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEliminarHistorico.setPreferredSize(new java.awt.Dimension(75, 57));
        btnEliminarHistorico.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnEliminarHistorico.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnEliminarHistorico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarHistoricoActionPerformed(evt);
            }
        });

        btnSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/salida_chica.png"))); // NOI18N
        btnSalir.setText("Salir");
        btnSalir.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSalir.setPreferredSize(new java.awt.Dimension(75, 57));
        btnSalir.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnSalir.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalirActionPerformed(evt);
            }
        });

        btnVerOcultar.setText("Ver/Ocultar Histórico ");
        btnVerOcultar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerOcultarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelBtnHistoricoLayout = new javax.swing.GroupLayout(panelBtnHistorico);
        panelBtnHistorico.setLayout(panelBtnHistoricoLayout);
        panelBtnHistoricoLayout.setHorizontalGroup(
            panelBtnHistoricoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBtnHistoricoLayout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(btnVerOcultar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEliminarHistorico, javax.swing.GroupLayout.DEFAULT_SIZE, 111, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnSalir, javax.swing.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE)
                .addGap(43, 43, 43))
        );
        panelBtnHistoricoLayout.setVerticalGroup(
            panelBtnHistoricoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelBtnHistoricoLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelBtnHistoricoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(btnVerOcultar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnEliminarHistorico, javax.swing.GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE)
                    .addComponent(btnSalir, javax.swing.GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE))
                .addContainerGap())
        );

        panelGasto.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

        panelTotalGastado.setForeground(new java.awt.Color(255, 255, 255));
        panelTotalGastado.setColorSecundario(new java.awt.Color(255, 255, 255));

        txtTotalGastado.setEditable(false);
        txtTotalGastado.setBackground(new java.awt.Color(255, 255, 255));
        txtTotalGastado.setFont(new java.awt.Font("Cambria", 0, 24)); // NOI18N
        txtTotalGastado.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotalGastado.setText("0");

        javax.swing.GroupLayout panelTotalGastadoLayout = new javax.swing.GroupLayout(panelTotalGastado);
        panelTotalGastado.setLayout(panelTotalGastadoLayout);
        panelTotalGastadoLayout.setHorizontalGroup(
            panelTotalGastadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTotalGastadoLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(txtTotalGastado)
                .addGap(19, 19, 19))
        );
        panelTotalGastadoLayout.setVerticalGroup(
            panelTotalGastadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTotalGastadoLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(txtTotalGastado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        lbTotal.setFont(new java.awt.Font("Cambria", 0, 24)); // NOI18N
        lbTotal.setText("Total");

        lbDebito.setFont(new java.awt.Font("Cambria", 0, 24)); // NOI18N
        lbDebito.setText("Total");

        panelTotalDebito.setForeground(new java.awt.Color(255, 255, 255));
        panelTotalDebito.setColorSecundario(new java.awt.Color(255, 255, 255));

        txtTotalDebito.setEditable(false);
        txtTotalDebito.setBackground(new java.awt.Color(255, 255, 255));
        txtTotalDebito.setFont(new java.awt.Font("Cambria", 0, 24)); // NOI18N
        txtTotalDebito.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotalDebito.setText("0");

        javax.swing.GroupLayout panelTotalDebitoLayout = new javax.swing.GroupLayout(panelTotalDebito);
        panelTotalDebito.setLayout(panelTotalDebitoLayout);
        panelTotalDebitoLayout.setHorizontalGroup(
            panelTotalDebitoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTotalDebitoLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(txtTotalDebito)
                .addGap(20, 20, 20))
        );
        panelTotalDebitoLayout.setVerticalGroup(
            panelTotalDebitoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTotalDebitoLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(txtTotalDebito, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        lbDebito1.setFont(new java.awt.Font("Cambria", 0, 24)); // NOI18N
        lbDebito1.setText("débito");

        lbTotal1.setFont(new java.awt.Font("Cambria", 0, 24)); // NOI18N
        lbTotal1.setText("gastado");

        javax.swing.GroupLayout panelGastoLayout = new javax.swing.GroupLayout(panelGasto);
        panelGasto.setLayout(panelGastoLayout);
        panelGastoLayout.setHorizontalGroup(
            panelGastoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelGastoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelGastoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbDebito)
                    .addComponent(lbDebito1))
                .addGap(5, 5, 5)
                .addComponent(panelTotalDebito, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(panelGastoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbTotal1)
                    .addComponent(lbTotal))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelTotalGastado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelGastoLayout.setVerticalGroup(
            panelGastoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelGastoLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelGastoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelGastoLayout.createSequentialGroup()
                        .addComponent(lbDebito)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbDebito1)
                        .addGap(5, 5, 5))
                    .addComponent(panelTotalDebito, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panelTotalGastado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelGastoLayout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(lbTotal)
                        .addGap(0, 0, 0)
                        .addComponent(lbTotal1)))
                .addContainerGap())
        );

        javax.swing.GroupLayout panelHistoricoLayout = new javax.swing.GroupLayout(panelHistorico);
        panelHistorico.setLayout(panelHistoricoLayout);
        panelHistoricoLayout.setHorizontalGroup(
            panelHistoricoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lbHistorico, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 510, Short.MAX_VALUE)
            .addComponent(panelGasto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panelBtnHistorico, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        panelHistoricoLayout.setVerticalGroup(
            panelHistoricoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelHistoricoLayout.createSequentialGroup()
                .addComponent(lbHistorico)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelGasto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelBtnHistorico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        lbDatosCliente.setFont(new java.awt.Font("Cambria", 0, 24)); // NOI18N
        lbDatosCliente.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbDatosCliente.setText("Datos del cliente");
        lbDatosCliente.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lbDatosCliente.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        panelDatos.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

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

        txtFecAlta.setEditable(false);

        lbDni.setText("DNI: ");

        txtDni.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDniActionPerformed(evt);
            }
        });

        lbCodPostal.setText("Codigo Postal: ");

        lbProvincia.setText("Provincia: ");

        lbTlfnFijo.setText("Telf. fijo: ");

        lbFecNac.setText("Fecha Nac.: ");

        lbEmail.setText("E-mail: ");

        lbObservaciones.setText("Observaciones: ");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel2.setText("*");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setText("*");

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        txtObservaciones.setColumns(20);
        txtObservaciones.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        txtObservaciones.setLineWrap(true);
        txtObservaciones.setRows(5);
        txtObservaciones.setBorder(null);
        jScrollPane2.setViewportView(txtObservaciones);

        lbTlfnMovil.setText("Telf. Móvil: ");

        lbFecAlta.setText("Fecha Alta: ");

        javax.swing.GroupLayout panelDatosLayout = new javax.swing.GroupLayout(panelDatos);
        panelDatos.setLayout(panelDatosLayout);
        panelDatosLayout.setHorizontalGroup(
            panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelDatosLayout.createSequentialGroup()
                .addContainerGap(25, Short.MAX_VALUE)
                .addGroup(panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
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
                .addGroup(panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelDatosLayout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addGroup(panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtDireccion)
                            .addComponent(txtCodPostal, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(panelDatosLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2))
                    .addGroup(panelDatosLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtApeCliente))
                    .addGroup(panelDatosLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPoblacion))
                    .addGroup(panelDatosLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelDatosLayout.createSequentialGroup()
                                .addComponent(txtFecNac, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lbFecAlta)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtFecAlta, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(panelDatosLayout.createSequentialGroup()
                                .addComponent(txtTelfnFijo, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lbTlfnMovil)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtTelfnMovil, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txtProvincia)
                            .addGroup(panelDatosLayout.createSequentialGroup()
                                .addGroup(panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(txtCodCliente)
                                        .addComponent(txtDni, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(txtNomCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(panelDatosLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(22, 22, 22))
        );
        panelDatosLayout.setVerticalGroup(
            panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDatosLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelDatosLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(lbCodigoCliente))
                    .addComponent(txtCodCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbDni, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDni, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNomCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbNombreCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtApeCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbApellidos, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCodPostal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbCodPostal))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPoblacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbPoblacion, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtProvincia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbProvincia, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTelfnMovil, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbTlfnFijo, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTelfnFijo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbTlfnMovil, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFecAlta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbFecNac)
                    .addComponent(txtFecNac, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbFecAlta))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbEmail))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbObservaciones)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

        btnFotosCliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/fotos.png"))); // NOI18N
        btnFotosCliente.setText("Ver fotos");
        btnFotosCliente.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnFotosCliente.setPreferredSize(new java.awt.Dimension(75, 57));
        btnFotosCliente.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnFotosCliente.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnFotosCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFotosClienteActionPerformed(evt);
            }
        });

        btnGuardarCliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/Guardar_grande.png"))); // NOI18N
        btnGuardarCliente.setText("Guardar cambios");
        btnGuardarCliente.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnGuardarCliente.setPreferredSize(new java.awt.Dimension(75, 57));
        btnGuardarCliente.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnGuardarCliente.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnGuardarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarClienteActionPerformed(evt);
            }
        });

        btnEliminarCliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/papelera_grande.png"))); // NOI18N
        btnEliminarCliente.setText("Eliminar cliente");
        btnEliminarCliente.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEliminarCliente.setPreferredSize(new java.awt.Dimension(75, 57));
        btnEliminarCliente.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnEliminarCliente.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnEliminarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarClienteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnGuardarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnEliminarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnFotosCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnGuardarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(btnEliminarCliente, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnFotosCliente, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout panelClienteLayout = new javax.swing.GroupLayout(panelCliente);
        panelCliente.setLayout(panelClienteLayout);
        panelClienteLayout.setHorizontalGroup(
            panelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lbDatosCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(panelClienteLayout.createSequentialGroup()
                .addGroup(panelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panelDatos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        panelClienteLayout.setVerticalGroup(
            panelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelClienteLayout.createSequentialGroup()
                .addComponent(lbDatosCliente)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelDatos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panelCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelHistorico, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panelHistorico, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tbHistoricoMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbHistoricoMouseReleased

        int[] arr;

        btnEliminarHistorico.setEnabled(true);

        //almaceno la fila seleccionada en la tabla
        arr = tbHistorico.getSelectedRows();

        //si no se ha seleccionado ninguna fila getSelectedRow() devuelve -1
        if(arr.length < 1)
        {
            JOptionPane.showMessageDialog(null, "No se ha seleccionado ninguna fila");
        }
        else
        {
            //almaceno el modelo de tabla que se está utilizando en la tabla real
            modelo = (DefaultTableModel) tbHistorico.getModel();

            if (SelCliente.nomCliente.equals("Cliente sin ficha"))
            {
                deshabilitarBotonesCliente();
            }
            else
            {
                //habilito todos los campos de texto y botones (excepto cancelar)
                habilitarTextFieldClientes();
                habilitarBotonesCliente();
            }
            btnEliminarHistorico.setEnabled(true);

        }
    }//GEN-LAST:event_tbHistoricoMouseReleased

    private void tbHistoricoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbHistoricoMouseClicked
        btnGuardarCliente.requestFocusInWindow();
    }//GEN-LAST:event_tbHistoricoMouseClicked

    private void tbHistoricoMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbHistoricoMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_tbHistoricoMouseEntered

    private void tbHistoricoPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_tbHistoricoPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_tbHistoricoPropertyChange

    private void btnEliminarHistoricoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarHistoricoActionPerformed

        int respuesta;
        float total = 0;
        float precio = 0;
        float metalico = 0;
        float tarjeta = 0;
        String where = null;
        Boolean encontrado = false;
        String borrarHistorico="";
        int filaSelec = tbHistorico.getSelectedRow();
        String fecha = tbHistorico.getValueAt(filaSelec, 8).toString();
        fecha = fecha.substring(0,fecha.length()-2);
        String fecha2 = tbHistorico.getValueAt(filaSelec, 8).toString();

        //Antes de nada compruebo que no se quiera eliminar un registro que fue pagado como MIXTO
        //ya que es imposible saber las cantidades de efectivo y tarjeta que se pagaron por cada producto.
        if (tbHistorico.getValueAt(filaSelec, 5).equals("MIXTO")  &&
            tbHistorico.getValueAt(filaSelec, 1).equals("TOTAL") == false)
        {
            JOptionPane.showMessageDialog(null,
                "No es posible eliminar los registros pagados como MIXTO. Si desea eliminarlo"
                + "\n debe seleccionar el producto 'TOTAL' y se eliminarán todos los registros de esa fecha.",
                "Eliminar registro", JOptionPane.INFORMATION_MESSAGE);
        }

        else if (tbHistorico.getValueAt(filaSelec, 1).equals("DEBITO"))
        {
            JOptionPane.showMessageDialog(null,
                "No es posible eliminar el DÉBITO sin haber sido pagado previamente. Si desea eliminarlo"
                + "\n debe seleccionar el producto 'TOTAL' y se eliminarán todos los registros de esa fecha.",
                "Eliminar registro", JOptionPane.INFORMATION_MESSAGE);
        }

        else if (tbHistorico.getValueAt(filaSelec, 1).equals("TOTAL") == false)
        {

            for ( int i=0; tbHistorico.getRowCount() > i; i++ )
            {
                if (tbHistorico.getValueAt(i, 1).equals("DEBITO") &&
                    tbHistorico.getValueAt(i, 8).equals(fecha2))
                {
                    JOptionPane.showMessageDialog(null,
                        "No es posible eliminar este registro porque existe un DÉBITO en la misma fecha. Para eliminarlo"
                        + "\n debe seleccionar el producto 'TOTAL' y se eliminarán todos los registros de esa fecha.",
                        "Eliminar registro", JOptionPane.INFORMATION_MESSAGE);

                    //encontrado = true;
                }
            }

        }

        else
        {
            borrarHistorico = borrarHistorico + "Producto: " + modelo.getValueAt(filaSelec, 1) +
            " - precio: " + tbHistorico.getValueAt(filaSelec, 4) + "€ \n";

            if (tbHistorico.getValueAt(filaSelec, 1).equals("TOTAL"))
            {
                respuesta = JOptionPane.showConfirmDialog(null,
                    "¡¡ Se borrarán todos los productos y servicios de esta misma cuenta !!",
                    "Eliminar histórico", JOptionPane.WARNING_MESSAGE);
            }
            else
            {
                respuesta = JOptionPane.showConfirmDialog(null,
                    "Se va a borrar el siguiente registro: \n\t" + borrarHistorico,
                    "Eliminar histórico", JOptionPane.WARNING_MESSAGE);
            }

            if (respuesta==0)
            {
                /* if (tbHistorico.getValueAt(filaSelec, 1).equals("DEBITO"))
                {
                    where = "id_cliente=" + SelCliente.idCliente
                    + " and fecha LIKE '" + fecha + "'";

                    consulta.eliminar("debitos", where);

                    rs = consulta.selectResult("historico", "precio, total", " where " + where + " AND producto LIKE 'TOTAL'");

                    precio = 0;
                    total = 0;
                    try
                    {
                        while (rs.next())
                        {
                            //if (rs.getString("total").equals("0") == false)
                            // {
                                total = Float.parseFloat(rs.getString("total")) +
                                Float.parseFloat(tbHistorico.getValueAt(filaSelec, 4).toString());

                                precio = Float.parseFloat(rs.getString("total")) +
                                Float.parseFloat(tbHistorico.getValueAt(filaSelec, 3).toString());
                                //}
                        }
                    }
                    catch (SQLException ex)
                    {
                        JOptionPane.showMessageDialog(null, ex);
                    }
                    consulta.update("historico", "total=" + total, where + " AND producto LIKE 'TOTAL'");
                }*/

                where = "id='" + (String)tbHistorico.getValueAt(filaSelec, 0) +"'";

                consulta.eliminar("historico", where);

                if (tbHistorico.getValueAt(filaSelec, 1).equals("TOTAL"))
                {
                    where = "id_cliente=" + SelCliente.idCliente
                    + " AND fecha LIKE '" + fecha + "'";

                    //Si se borró un registro TOTAL elimino todo lo de esa fecha
                    //y si hay un débito, lo elimino de la tabla débito
                    consulta.eliminar("historico", where);

                    consulta.eliminar("debitos", where);
                }
                else
                {
                    where = " where id_cliente=" + SelCliente.idCliente
                    + " AND producto LIKE 'TOTAL' AND fecha LIKE '" + fecha + "'";

                    rs = consulta.selectResult("historico", "total, precio, metalico, tarjeta", where);
                }

                total = 0;
                precio = 0;
                metalico = 0;
                tarjeta = 0;

                try
                {
                    while (rs.next())
                    {

                        total = Float.parseFloat(rs.getString("total")) -
                        Float.parseFloat(tbHistorico.getValueAt(filaSelec, 4).toString());

                        precio = Float.parseFloat(rs.getString("precio")) -
                        Float.parseFloat(tbHistorico.getValueAt(filaSelec, 4).toString());

                        metalico = Float.parseFloat(rs.getString("metalico")) -
                        Float.parseFloat(tbHistorico.getValueAt(filaSelec, 6).toString());

                        tarjeta = Float.parseFloat(rs.getString("tarjeta")) -
                        Float.parseFloat(tbHistorico.getValueAt(filaSelec, 7).toString());

                    }
                }
                catch (SQLException ex)
                {
                    JOptionPane.showMessageDialog(null, ex);
                }

                if (tbHistorico.getValueAt(filaSelec, 1).equals("DEBITO") == false)
                {
                    where = "id_cliente=" + SelCliente.idCliente
                    + " AND producto LIKE 'TOTAL' AND fecha LIKE '" + fecha + "'";

                    consulta.update("historico", "total=" + total + ", precio=" + precio +
                        "metalico=" + metalico + ", tarjeta=" + tarjeta, where);
                }

                //Antes de recalcular lo gastado y los débitos del cliente,
                //los dejo a 0, por si al buscar en la BD ya no hay registros
                //y evitar que se quede los valores antiguos una vez eliminado de la tabla.
                txtTotalDebito.setText("0");
                txtTotalGastado.setText("0");

                crearTabla(" where id_cliente=" + SelCliente.idCliente);

                btnEliminarHistorico.setEnabled(false);
            }
        }

    }//GEN-LAST:event_btnEliminarHistoricoActionPerformed

    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_btnSalirActionPerformed

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

        String[] camposValores;
        consulta = new Conexion();
        String where;

        if(comprobarTextFieldCliente() == false)
        {
            camposValores = recogerDatosClientes();

            int respuesta = JOptionPane.showConfirmDialog(null,
                "Se va a modificar el cliente: " + txtNomCliente.getText() + " " +
                txtApeCliente.getText() + " ¿Está seguro?",
                "Modificar cliente", JOptionPane.WARNING_MESSAGE);

            if (respuesta==0)
            {

                where = "id='" + txtCodCliente.getText() + "'";

                //llamo al método Update de la clase conexión y le envío tabla, camposYvalores, condición
                consulta.update("clientes", camposValores[1], where);

                JOptionPane.showMessageDialog(null, "Modificado correctamente.",
                    "Modificar cliente", JOptionPane.INFORMATION_MESSAGE);

            }

        }
    }//GEN-LAST:event_btnGuardarClienteActionPerformed

    private void btnEliminarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarClienteActionPerformed

        String where;

        int respuesta = JOptionPane.showConfirmDialog(null,
            "Se va a eliminar el cliente: " + txtNomCliente.getText() + " " +
            txtApeCliente.getText() + " ¿Está seguro?",
            "Eliminar cliente", JOptionPane.WARNING_MESSAGE);

        if (respuesta == 0)
        {
            consulta = new Conexion();

            where = "id='" + txtCodCliente.getText() + "'";
            consulta.eliminar("clientes", where);

            //Vuelvo a cargar los clientes después de borrarlo
            Clientes.actualizarTablaClientes();

            //Vuelvo a la ficha Clientes
            dispose();

        }
    }//GEN-LAST:event_btnEliminarClienteActionPerformed

    private void txtDniActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDniActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDniActionPerformed

    private void btnFotosClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFotosClienteActionPerformed

        FotosCliente fotosCliente = new FotosCliente(null, true);
        fotosCliente.setVisible(true);
    }//GEN-LAST:event_btnFotosClienteActionPerformed

    private void btnVerOcultarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerOcultarActionPerformed

        if (tbHistorico.isVisible())
        {
            tbHistorico.setVisible(false);
            panelGasto.setVisible(false);
        }
        else
        {
            tbHistorico.setVisible(true);
            panelGasto.setVisible(true);
        }

    }//GEN-LAST:event_btnVerOcultarActionPerformed

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
            java.util.logging.Logger.getLogger(FichaCliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FichaCliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FichaCliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FichaCliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FichaCliente().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEliminarCliente;
    private javax.swing.JButton btnEliminarHistorico;
    private javax.swing.JButton btnFotosCliente;
    private javax.swing.JButton btnGuardarCliente;
    private javax.swing.JButton btnSalir;
    private javax.swing.JButton btnVerOcultar;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lbApellidos;
    private javax.swing.JLabel lbCliente;
    private javax.swing.JLabel lbCodPostal;
    private javax.swing.JLabel lbCodigoCliente;
    private javax.swing.JLabel lbDatosCliente;
    private javax.swing.JLabel lbDebito;
    private javax.swing.JLabel lbDebito1;
    private javax.swing.JLabel lbDireccion;
    private javax.swing.JLabel lbDni;
    private javax.swing.JLabel lbEmail;
    private javax.swing.JLabel lbFecAlta;
    private javax.swing.JLabel lbFecNac;
    private javax.swing.JLabel lbHistorico;
    private javax.swing.JLabel lbNombreCliente;
    private javax.swing.JLabel lbObservaciones;
    private javax.swing.JLabel lbPoblacion;
    private javax.swing.JLabel lbProvincia;
    private javax.swing.JLabel lbTlfnFijo;
    private javax.swing.JLabel lbTlfnMovil;
    private javax.swing.JLabel lbTotal;
    private javax.swing.JLabel lbTotal1;
    private javax.swing.JPanel panelBtnHistorico;
    private javax.swing.JPanel panelCliente;
    private javax.swing.JPanel panelDatos;
    private javax.swing.JPanel panelGasto;
    private javax.swing.JPanel panelHistorico;
    private org.edisoncor.gui.panel.PanelRect panelTotalDebito;
    private org.edisoncor.gui.panel.PanelRect panelTotalGastado;
    private javax.swing.JTable tbHistorico;
    private javax.swing.JTextField txtApeCliente;
    private javax.swing.JTextField txtCodCliente;
    private javax.swing.JTextField txtCodPostal;
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
    public static javax.swing.JTextField txtTotalDebito;
    public static javax.swing.JTextField txtTotalGastado;
    // End of variables declaration//GEN-END:variables
}
