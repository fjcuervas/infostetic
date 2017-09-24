/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package infostetic;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Javi
 */
public class Cobro extends javax.swing.JDialog {

    SelCliente selCliente;
    DefaultTableModel modelo;
    Conexion consulta = new Conexion();
    Caja caja;
    
    public Cobro(java.awt.Frame parent, boolean modal) 
    {
        super(parent, modal);
        initComponents();
        setLocationRelativeTo(null);//centro la ventana en medio de la pantalla
        
        txtPagar.setText(String.valueOf(Caja.total));
        

        //por defecto, deshabilito "Salir" para evitar que me eche de la sesión
        //de compra. Solo lo habilitaré una vez realizado el cobro
        btnSalir.setEnabled(false);
    }

    private void deshabilitarBotonesCobro()
    {
        btnCancelar.setEnabled(false);
        btnDeber.setEnabled(false);
        btnJusto.setEnabled(false);
        btnMetalico.setEnabled(false);
        btnMixto.setEnabled(false);
        btnTarjeta.setEnabled(false);
    }
    
    private void movimientosBD(String formaPago, String metalico, String tarjeta)
    {
        String valores;
        String campos;
        String fecha;            
        Boolean res = true;
        String debito;
        BigDecimal entregado;
        String where;
        
        GregorianCalendar calendario=new GregorianCalendar();                

        //Hacer formateador de la fecha, se le pasa el formato en que se quiere obtener la fecha.
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
        fecha = dateformat.format(calendario.getTime());
        
        selCliente = new SelCliente(null, true);      
        
        //almaceno el modelo de tabla que se está utilizando en la tabla real
        modelo = (DefaultTableModel) Caja.tbCompra.getModel();

              
        //Para evitar que me salgan muchos decimales en el textField "txtDevolver"
        //utilizo esta clase BigDecimal para conseguir que muestre la cantidad exacta con 2 decimales
        BigDecimal metal = new BigDecimal (metalico);
        BigDecimal tarje = new BigDecimal (tarjeta);
        
        entregado = metal.add(tarje);
        BigDecimal pagar = new BigDecimal (txtPagar.getText());
                
        if (formaPago.equals("JUSTO"))
        {
            debito = String.valueOf(Caja.total); 
            txtDevolver.setText("0");
        }
        else
        {                  
            debito = String.valueOf(entregado.add(pagar.negate())); //lo entregado menos lo pagado
            txtDevolver.setText(debito);
        }                                        
        
        /*
         * ******************************** INSERCION en tabla "historico" **********************************
         */
        
        //Recorro la tabla para ir recogiendo los valores de cada columna y fila, y su posterior inserción
        for (int i = 0; Caja.tbCompra.getRowCount() > i; i++) 
        {
            valores = "'" + ((String) Caja.tbCompra.getValueAt(i, 0)) + "'"; //id del producto 
            valores = valores + ", '" + SelCliente.idCliente + "'"; //id del cliente (lo rescato desde SelCliente()
            valores = valores + ", '" + selCliente.nomCliente() + "'"; //nombre del cliente 
            valores = valores + ", '" + String.valueOf(Caja.tbCompra.getValueAt(i, 1)) + "'"; //cantidad
            valores = valores + ", '" + String.valueOf(Caja.tbCompra.getValueAt(i, 2)) + "'"; //producto
            valores = valores + ", '" + String.valueOf(Caja.tbCompra.getValueAt(i, 3)) + "'"; //precio
            valores = valores + ", '" + fecha + "'"; //fecha y hora actual
            valores = valores + ", '" + String.valueOf(Caja.tbCompra.getValueAt(i, 4)) + "'"; //total            
            valores = valores + ", '" + formaPago + "'"; //forma de pago
            
            if (formaPago.equals("JUSTO"))
            {
                 //en metálico añado el precio total de productos porque se pagó JUSTO
                valores = valores + ", '" + String.valueOf(Caja.tbCompra.getValueAt(i, 4)) + "'";
            }
            else
            {
                valores = valores + ", '" + metalico + "'"; //metálico
            }
            valores = valores + ", '" + tarjeta + "'"; //tarjeta

            campos = "id_producto, id_cliente, cliente, cantidad, producto, "
                   + "precio, fecha, total, forma_pago, metalico, tarjeta";
            
            /* Si al cobrar me encuentro con un débito, actualizo el campo "producto" 
             * de la tabla "historico" cuyo contenido sea 'DEBITO' y cuya fecha coincida con 
             * la fecha en la tabla "debitos" y lo sustituyo por 'DEBITO PAGADO'
             */
            if (Caja.tbCompra.getValueAt(i, 2).equals("DEBITO"))
            {
                where = "id_cliente=" + SelCliente.idCliente + " AND fecha = " +
                        "(SELECT debitos.fecha " +
                        "FROM db_infostetic.debitos " +
                        "WHERE debitos.id_cliente = '" + SelCliente.idCliente +
                        "' AND historico.fecha LIKE debitos.fecha " +
                        "AND producto LIKE 'DEBITO' " +
                        "GROUP BY historico.fecha)";
                
                consulta.update("historico", "producto='DEBITO PAGADO', fecha='" + 
                                fecha + "', metalico='" + Caja.tbCompra.getValueAt(i, 4) + 
                                "', tarjeta='" + tarjeta + "', forma_pago='" + formaPago + "'", where);
                
                //realizo la eliminación del débito
                consulta.eliminar("debitos", "id = "+(String) Caja.tbCompra.getValueAt(i, 0));  
            }
            
            //Si no se trata de un débito reaizo una inserción normal en historico
            else
            {
                //realizo la inserción
                res = consulta.insert("historico", campos, valores);
            }

            //Si ocurrió algún problema muestro un mensaje
            if (res == false) 
            {
                JOptionPane.showMessageDialog(null, "Ocurrió un problema en el cobro.",
                        "Añadir historico de compra", JOptionPane.WARNING_MESSAGE);
            } 
            
        }

        /*
         * ******************************** INSERCION en tabla "debitos" **********************************
         */
        
        //Solo añado el débito si la cantidad a devolver salió negativa
        if (Float.parseFloat(txtDevolver.getText()) < 0)
        {

                valores = "'" + SelCliente.idCliente + "'"; //id del cliente (lo rescato desde SelCliente()
                valores = valores + ", '" + selCliente.nomCliente() + "'"; //nombre del cliente 
                valores = valores + ", '" + Caja.total + "'"; //total
                valores = valores + ", '" + entregado + "'"; //cantidad entregada 
                valores = valores + ", '" + Float.parseFloat(txtDevolver.getText()) * -1 +"'"; //debito en positivo
                valores = valores + ", '" + formaPago + "'";  //forma de pago          
                valores = valores + ", '" + fecha + "'"; //fecha y hora actual
                valores = valores + ", '" + metalico + "'"; //metálico
                valores = valores + ", '" + tarjeta + "'"; //tarjeta            

                //Añado un registro en la BD tabla "debitos", con el debito
                campos = "id_cliente, cliente, total, entregado, debito, "
                        + "forma_pago, fecha, metalico, tarjeta";  

                //realizo la inserción en "debitos"
                res = consulta.insert("debitos", campos, valores);

                //Si ocurrió algún problema muestro un mensaje
                if (res == false) 
                {
                    JOptionPane.showMessageDialog(null, "Ocurrió un problema en el cobro.",
                           "Añadir débito", JOptionPane.WARNING_MESSAGE);
                } 
                /*
                * ***************************** INSERCION en tabla "historico" del débito ****************************
                */    

                valores = "'" + SelCliente.idCliente + "'"; //id del cliente (lo rescato desde SelCliente()
                valores = valores + ", '" + selCliente.nomCliente() + "'"; //nombre del cliente 
                valores = valores + ", '0'"; //id_producto
                valores = valores + ", '1'"; //cantidad
                valores = valores + ", 'DEBITO'"; //producto
                valores = valores + ", '" + Float.parseFloat(txtDevolver.getText()) * -1 + "'"; //precio (debito)
                valores = valores + ", '" + Float.parseFloat(txtDevolver.getText()) * -1 +"'"; //total (debito)
                valores = valores + ", '" + formaPago + "'"; //forma de pago                  
                valores = valores + ", '" + fecha + "'"; //fecha y hora actual
                valores = valores + ", '" + metalico + "'"; //metálico
                valores = valores + ", '" + tarjeta + "'"; //tarjeta                   

                //Añado un registro en la BD tabla historico, con el débito
                campos = "id_cliente, cliente, id_producto, cantidad, producto, "
                        + "precio, total, forma_pago, fecha, metalico, tarjeta";  

                //realizo la inserción en "historico"
                
                res = consulta.insert("historico", campos, valores);

               //Si ocurrió algún problema muestro un mensaje
               if (res == false) 
               {
                   JOptionPane.showMessageDialog(null, "Ocurrió un problema en el cobro.",
                          "Añadir histórico", JOptionPane.WARNING_MESSAGE);
               }                                  
        }              
              
        //Si se ha seleccionado algún producto de la tabla tbProductos, actualizo el stock
        if (Caja.cantProd.size() > 0)
        {

            //int j = 0;
            int cantidad = 0;

            //recorro la tabla en busca de productos para actualizar el stock
            for (int i = 0; Caja.tbCompra.getRowCount() > i; i++) 
            {   
                for (int j = 0; Caja.cantProd.size() > j; j++)
                {                
                
                    //Comparo los id de cada producto de las tablas tbCompra y tbProductos
                    //Los id de la tabla tbProductos los almacené en un arrayList desde "Caja"
                    int resp = Caja.cantProd.get(j).get(0).compareTo(
                            Integer.parseInt(String.valueOf(Caja.tbCompra.getValueAt(i, 0))));

                    //Si los id coinciden realizo la actualización
                    if ( resp == 0 )
                    {

                        //Almaceno la nueva cantidad restándole la cantidad de productos de tbCompra
                        //Si el cliente se llevó 2, la columna cantidad marcará 2, que es lo que resto
                        cantidad = Caja.cantProd.get(j).get(1) - 
                                    Integer.parseInt(String.valueOf(Caja.tbCompra.getValueAt(i, 1)));

                        Caja.cantProd.get(j).set(1,cantidad);
                        //tbCompra.convertRowIndexToModel(row)


                        //Si la cantidad es 0, muestro un mensaje 
                        if (cantidad < 1)
                        {
                            JOptionPane.showMessageDialog(null, "No quedan unidades en STOCK del producto: " + Caja.tbCompra.getValueAt(i, 2) + 
                                   ".\nRecuerde realizar un pedido y actualizar el STOCK",
                                  "Stock", JOptionPane.WARNING_MESSAGE);  
                        }
                        //Realizo la actualización en la BD del producto encontrado
                        consulta.update("productos","cantidad="+ cantidad,"id="+Caja.cantProd.get(j).get(0));
                      /*  if (Caja.tbCompra.getRowCount() > (i + 1))
                        {
                            j++; //Aumento el contador para moverme por el arrayList "cantProd"
                        }*/
                        break;
                    }
                } //cierre for
            }
        } //cierre if (Caja.cantProd.size() > 0)
            
        
        //Realizo una inserción en "historico" con el total de la compra        
        campos = "id_producto, id_cliente, cliente, cantidad, producto, "
                   + "precio, fecha, total, forma_pago, metalico, tarjeta";                
                
        valores = "0, " + SelCliente.idCliente + ", '" + SelCliente.nomCliente + "', 1, 'TOTAL', "
                + txtPagar.getText() + ", '" + fecha + "', " + entregado + ", '" + formaPago
                + "', " + metalico + ", " + tarjeta;
        
        res = consulta.insert("historico", campos, valores);
        
        
        deshabilitarBotonesCobro();      
        btnSalir.setEnabled(true);
        
       
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lbNombreCliente = new javax.swing.JLabel();
        panelBotones = new javax.swing.JPanel();
        panelTipoCobro = new javax.swing.JPanel();
        btnMetalico = new javax.swing.JButton();
        btnJusto = new javax.swing.JButton();
        btnTarjeta = new javax.swing.JButton();
        btnMixto = new javax.swing.JButton();
        panelOpciones = new javax.swing.JPanel();
        btnTicket = new javax.swing.JButton();
        btnDeber = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        btnSalir = new javax.swing.JButton();
        panelDatos = new javax.swing.JPanel();
        lbEntregado = new javax.swing.JLabel();
        txtPagar = new javax.swing.JTextField();
        lbDevolver = new javax.swing.JLabel();
        lbPagar = new javax.swing.JLabel();
        txtEntregado = new javax.swing.JTextField();
        txtDevolver = new javax.swing.JTextField();
        lbEntregado1 = new javax.swing.JLabel();
        lbEntregado2 = new javax.swing.JLabel();
        lbEntregado3 = new javax.swing.JLabel();
        lbNombreCliente1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setResizable(false);

        lbNombreCliente.setFont(new java.awt.Font("Cambria", 0, 24)); // NOI18N
        lbNombreCliente.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbNombreCliente.setText("Cliente sin ficha");
        lbNombreCliente.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(153, 153, 153)));

        panelBotones.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(153, 153, 153)));

        panelTipoCobro.setLayout(new java.awt.GridLayout(1, 5, -1, 0));

        btnMetalico.setText("METÁLICO");
        btnMetalico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMetalicoActionPerformed(evt);
            }
        });
        panelTipoCobro.add(btnMetalico);

        btnJusto.setText("JUSTO");
        btnJusto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnJustoActionPerformed(evt);
            }
        });
        panelTipoCobro.add(btnJusto);

        btnTarjeta.setText("TARJETA");
        btnTarjeta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTarjetaActionPerformed(evt);
            }
        });
        panelTipoCobro.add(btnTarjeta);

        btnMixto.setText("MIXTO");
        btnMixto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMixtoActionPerformed(evt);
            }
        });
        panelTipoCobro.add(btnMixto);

        panelOpciones.setLayout(new java.awt.GridLayout(1, 4, -1, 0));

        btnTicket.setText("TICKET");
        btnTicket.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTicketActionPerformed(evt);
            }
        });
        panelOpciones.add(btnTicket);

        btnDeber.setText("A DEBER");
        btnDeber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeberActionPerformed(evt);
            }
        });
        panelOpciones.add(btnDeber);

        btnCancelar.setText("CANCELAR");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });
        panelOpciones.add(btnCancelar);

        btnSalir.setText("SALIR");
        btnSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalirActionPerformed(evt);
            }
        });
        panelOpciones.add(btnSalir);

        javax.swing.GroupLayout panelBotonesLayout = new javax.swing.GroupLayout(panelBotones);
        panelBotones.setLayout(panelBotonesLayout);
        panelBotonesLayout.setHorizontalGroup(
            panelBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBotonesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panelTipoCobro, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelOpciones, javax.swing.GroupLayout.DEFAULT_SIZE, 434, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelBotonesLayout.setVerticalGroup(
            panelBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBotonesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelTipoCobro, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panelOpciones, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelDatos.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

        lbEntregado.setFont(new java.awt.Font("Cambria", 0, 18)); // NOI18N
        lbEntregado.setText("Entregado:");

        txtPagar.setEditable(false);
        txtPagar.setBackground(new java.awt.Color(255, 51, 51));
        txtPagar.setFont(new java.awt.Font("Cambria", 1, 24)); // NOI18N
        txtPagar.setForeground(new java.awt.Color(255, 255, 255));
        txtPagar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPagarActionPerformed(evt);
            }
        });

        lbDevolver.setFont(new java.awt.Font("Cambria", 0, 18)); // NOI18N
        lbDevolver.setText("A devolver:");

        lbPagar.setFont(new java.awt.Font("Cambria", 0, 18)); // NOI18N
        lbPagar.setText("A pagar:");

        txtEntregado.setBackground(new java.awt.Color(51, 153, 255));
        txtEntregado.setFont(new java.awt.Font("Cambria", 1, 24)); // NOI18N
        txtEntregado.setForeground(new java.awt.Color(255, 255, 255));

        txtDevolver.setEditable(false);
        txtDevolver.setBackground(new java.awt.Color(0, 204, 51));
        txtDevolver.setFont(new java.awt.Font("Cambria", 1, 24)); // NOI18N
        txtDevolver.setForeground(new java.awt.Color(255, 255, 255));

        lbEntregado1.setFont(new java.awt.Font("Cambria", 1, 24)); // NOI18N
        lbEntregado1.setText("€");

        lbEntregado2.setFont(new java.awt.Font("Cambria", 1, 24)); // NOI18N
        lbEntregado2.setText("€");

        lbEntregado3.setFont(new java.awt.Font("Cambria", 1, 24)); // NOI18N
        lbEntregado3.setText("€");

        javax.swing.GroupLayout panelDatosLayout = new javax.swing.GroupLayout(panelDatos);
        panelDatos.setLayout(panelDatosLayout);
        panelDatosLayout.setHorizontalGroup(
            panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDatosLayout.createSequentialGroup()
                .addGap(79, 79, 79)
                .addGroup(panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbEntregado)
                    .addComponent(lbPagar, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lbDevolver, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(txtPagar, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtEntregado, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDevolver, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbEntregado1)
                    .addComponent(lbEntregado2)
                    .addComponent(lbEntregado3))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelDatosLayout.setVerticalGroup(
            panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDatosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbEntregado)
                    .addComponent(txtEntregado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbEntregado1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbPagar)
                    .addComponent(txtPagar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbEntregado2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbDevolver)
                    .addComponent(txtDevolver, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbEntregado3))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lbNombreCliente1.setFont(new java.awt.Font("Cambria", 0, 24)); // NOI18N
        lbNombreCliente1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbNombreCliente1.setText("Elija el tipo de cobro");
        lbNombreCliente1.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(153, 153, 153)));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbNombreCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelDatos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbNombreCliente1, javax.swing.GroupLayout.DEFAULT_SIZE, 456, Short.MAX_VALUE)
                    .addComponent(panelBotones, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbNombreCliente)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panelDatos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lbNombreCliente1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panelBotones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnMetalicoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMetalicoActionPerformed

        if ( Caja.comprobarCadena(txtEntregado.getText()) == false || 
                txtEntregado.getText().equals("0") == true) 
        {
            JOptionPane.showMessageDialog(null, "La cantidad entregada no es correcta",
                                          "Cantidad entregada", JOptionPane.INFORMATION_MESSAGE);    
            txtEntregado.requestFocusInWindow();
        }
        else
        {
            movimientosBD("METALICO", txtEntregado.getText(), "0");
        }
        
    }//GEN-LAST:event_btnMetalicoActionPerformed

    private void btnJustoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnJustoActionPerformed

        if (txtEntregado.getText().equals("") == false)
        {
            JOptionPane.showMessageDialog(null, "Si desea cobrar la cantidad JUSTA debe borrar el contenido de 'Cantidad entregada'", 
                                          null, JOptionPane.INFORMATION_MESSAGE);  
            txtEntregado.requestFocusInWindow();
        }
        else
        {        
            txtEntregado.setText(txtPagar.getText());
            movimientosBD("JUSTO", txtEntregado.getText(), "0");
        }
        
    }//GEN-LAST:event_btnJustoActionPerformed

    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed

        Principal.contenedor.setSelectedIndex(0);

        System.out.println("elementos antes: "+Caja.cantProd.size());
        Caja.cantProd.clear();
        Caja.fila = 0;
        System.out.println("elementos despues: "+Caja.cantProd.size());

        dispose();

    }//GEN-LAST:event_btnSalirActionPerformed

    private void btnTicketActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTicketActionPerformed
/*FileOutputStream os = new FileOutputStream("COM1:"); 

PrintStream ps = new PrintStream(os);

ps.println(imp); //imp es la cadena de caracteres con el texto a impirmir
ps.close();*/
    }//GEN-LAST:event_btnTicketActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
 
        this.dispose();
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void btnTarjetaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTarjetaActionPerformed

        if ( Caja.comprobarCadena(txtEntregado.getText()) )
        {         
            if (Float.parseFloat(txtEntregado.getText()) > Float.parseFloat(txtPagar.getText()))
            {
                JOptionPane.showMessageDialog(null, "La cantidad entregada no puede ser mayor al total.", 
                                              null, JOptionPane.INFORMATION_MESSAGE);       
                txtEntregado.requestFocusInWindow();
            }
            else
            {
                movimientosBD("TARJETA", "0", txtEntregado.getText());
            }
        }
        else
        {
            JOptionPane.showMessageDialog(null, "No se introdujo una cantidad correcta.");
            txtEntregado.requestFocusInWindow();
        }            

    }//GEN-LAST:event_btnTarjetaActionPerformed

    private void btnMixtoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMixtoActionPerformed

        if ( Caja.comprobarCadena(txtEntregado.getText()) )
        {    
            if ( Float.parseFloat(txtEntregado.getText()) < Float.parseFloat(txtPagar.getText()) )
            {
                String respuesta = JOptionPane.showInputDialog(null, "Introduzca la cantidad a pagar con tarjeta: ");
                        /*JOptionPane.showConfirmDialog(null,
                        "Se procederá a cobrar " + txtEntregado.getText() + " euros en metálico,\n" +
                        " y el resto con tarjeta. ¿ Es correcto ?", null, JOptionPane.WARNING_MESSAGE);*/
                if ( Caja.comprobarCadena(txtEntregado.getText()) )
                {
                    BigDecimal totalEntreg = BigDecimal.valueOf(Double.parseDouble(respuesta)).
                            add(BigDecimal.valueOf(Double.parseDouble(txtEntregado.getText()) ));
                    
                    int res = totalEntreg.compareTo(BigDecimal.valueOf(Double.parseDouble(txtPagar.getText())));
                    //Si el total entregado (metalico + tarjeta) es mayor que el total a pagar muestro error 
                    if ( res == 1 )  
                    {
                        BigDecimal maximoTarjeta = BigDecimal.valueOf(Double.parseDouble(txtPagar.getText())).
                                add(BigDecimal.valueOf(Double.parseDouble(txtEntregado.getText())).negate());
                        
                        JOptionPane.showMessageDialog(null, "El máximo a pagar con tarjeta es: " + maximoTarjeta + ".", 
                                null, JOptionPane.INFORMATION_MESSAGE);
                        txtEntregado.requestFocusInWindow();                          
                    }
                    else
                    {
                        movimientosBD("MIXTO", txtEntregado.getText(), respuesta);            
                    }                                       
                }   
            }
            else
            {
                JOptionPane.showMessageDialog(null, "La cantidad en metálico debe ser menor que el total.", 
                        null, JOptionPane.INFORMATION_MESSAGE);
                txtEntregado.requestFocusInWindow();                
            }
        }
        else
        {
            JOptionPane.showMessageDialog(null, "Introduzca una cantidad en metálico");
            txtEntregado.requestFocusInWindow();
        }
    }//GEN-LAST:event_btnMixtoActionPerformed

    private void btnDeberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeberActionPerformed

        if (txtEntregado.getText().equals("") == false)
        {
            JOptionPane.showMessageDialog(null, "Para dejar a DEBER el total, no debe introducir ninguna cantidad.\n"+
                    "        Si lo prefiere puede seleccionar otra forma de pago.", null, JOptionPane.INFORMATION_MESSAGE);   
            txtEntregado.requestFocusInWindow();
        }
        else
        {
            txtEntregado.setText("0");
            movimientosBD("DEBITO", "0", "0");
        }        
    }//GEN-LAST:event_btnDeberActionPerformed

    private void txtPagarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPagarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPagarActionPerformed

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
            java.util.logging.Logger.getLogger(Cobro.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Cobro.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Cobro.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Cobro.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Cobro dialog = new Cobro(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnDeber;
    private javax.swing.JButton btnJusto;
    private javax.swing.JButton btnMetalico;
    private javax.swing.JButton btnMixto;
    private javax.swing.JButton btnSalir;
    private javax.swing.JButton btnTarjeta;
    private javax.swing.JButton btnTicket;
    private javax.swing.JLabel lbDevolver;
    private javax.swing.JLabel lbEntregado;
    private javax.swing.JLabel lbEntregado1;
    private javax.swing.JLabel lbEntregado2;
    private javax.swing.JLabel lbEntregado3;
    private javax.swing.JLabel lbNombreCliente;
    private javax.swing.JLabel lbNombreCliente1;
    private javax.swing.JLabel lbPagar;
    private javax.swing.JPanel panelBotones;
    private javax.swing.JPanel panelDatos;
    private javax.swing.JPanel panelOpciones;
    private javax.swing.JPanel panelTipoCobro;
    private javax.swing.JTextField txtDevolver;
    private javax.swing.JTextField txtEntregado;
    private javax.swing.JTextField txtPagar;
    // End of variables declaration//GEN-END:variables
}
