/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package infostetic;

import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Javi
 */
public class SelCliente extends javax.swing.JDialog {

    ResultSet rs;
    DefaultTableModel modelo;
    String[] registro;
    String[] columnas;
    static String idCliente = "";
    static String nomCliente = "Cliente sin ficha";    
    static Boolean seleccionarCliente = false;
    static Boolean crearFicha = false;
    Conexion consulta;
    
    public SelCliente(java.awt.Frame parent, boolean modal) 
    {
        super(parent, modal);
        initComponents();
        
        this.setTitle("Seleccionar cliente");
        
        setLocationRelativeTo(null);//centro la ventana en medio de la pantalla
        registro=new String[4];
        columnas=new String []{"id", "dni", "nombre", "apellidos"};
        
        consulta = new Conexion();
        
        modelo = new DefaultTableModel(null,columnas) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                    return false;
            }
        };        
        
        actualizarTabla();        
        
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        txtBuscarCliente = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbClientes = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        btnEntrar = new javax.swing.JButton();
        btnCrearFicha = new javax.swing.JButton();
        txtClienteSelec = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        btnBusquedaAvanzada = new javax.swing.JButton();
        btnClienteSinFicha = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jLabel1.setFont(new java.awt.Font("Cambria", 0, 14)); // NOI18N
        jLabel1.setText("Nombre o apellido: ");

        txtBuscarCliente.setFont(new java.awt.Font("Cambria", 0, 14)); // NOI18N
        txtBuscarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBuscarClienteActionPerformed(evt);
            }
        });
        txtBuscarCliente.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtBuscarClienteKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBuscarClienteKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtBuscarClienteKeyTyped(evt);
            }
        });

        tbClientes.setAutoCreateRowSorter(true);
        tbClientes.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tbClientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Id", "Dni", "Nombre", "Apellido"
            }
        ));
        tbClientes.setRowHeight(18);
        tbClientes.getTableHeader().setReorderingAllowed(false);
        tbClientes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbClientesMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tbClientesMousePressed(evt);
            }
        });
        jScrollPane1.setViewportView(tbClientes);

        jLabel2.setFont(new java.awt.Font("Cambria", 0, 24)); // NOI18N
        jLabel2.setText("Abrir cuenta cliente");

        btnEntrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/aceptar.png"))); // NOI18N
        btnEntrar.setEnabled(false);
        btnEntrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEntrarActionPerformed(evt);
            }
        });

        btnCrearFicha.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/NuevoCliente.png"))); // NOI18N
        btnCrearFicha.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCrearFicha.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCrearFichaActionPerformed(evt);
            }
        });

        txtClienteSelec.setFont(new java.awt.Font("Cambria", 0, 14)); // NOI18N
        txtClienteSelec.setEnabled(false);

        jLabel3.setFont(new java.awt.Font("Cambria", 0, 14)); // NOI18N
        jLabel3.setText("Cliente seleccionado: ");

        btnBusquedaAvanzada.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnBusquedaAvanzada.setText("Búsqueda avanzada");
        btnBusquedaAvanzada.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBusquedaAvanzadaActionPerformed(evt);
            }
        });

        btnClienteSinFicha.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/anonimo.png"))); // NOI18N
        btnClienteSinFicha.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnClienteSinFicha.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClienteSinFichaActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Cambria", 0, 14)); // NOI18N
        jLabel4.setText("Cliente sin ficha");

        jLabel5.setFont(new java.awt.Font("Cambria", 0, 14)); // NOI18N
        jLabel5.setText("Nuevo cliente");

        jLabel6.setFont(new java.awt.Font("Cambria", 0, 14)); // NOI18N
        jLabel6.setText("Aceptar");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(46, 46, 46)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtClienteSelec, javax.swing.GroupLayout.PREFERRED_SIZE, 322, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(jLabel1)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtBuscarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnBusquedaAvanzada))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 513, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(jLabel5)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel4))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnCrearFicha, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnClienteSinFicha, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnEntrar, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(26, 26, 26)
                                .addComponent(jLabel6)))
                        .addGap(105, 105, 105))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnBusquedaAvanzada)
                    .addComponent(jLabel1)
                    .addComponent(txtBuscarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtClienteSelec, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCrearFicha, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnClienteSinFicha, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEntrar, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel4)
                    .addComponent(jLabel6))
                .addGap(15, 15, 15))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tbClientesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbClientesMouseClicked

     
        
    }//GEN-LAST:event_tbClientesMouseClicked

    private void btnEntrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEntrarActionPerformed

        seleccionarCliente = true;
        
        idCliente=((String)tbClientes.getValueAt(tbClientes.getSelectedRow(), 0));
        nomCliente=((String)tbClientes.getValueAt(tbClientes.getSelectedRow(), 2)+" "+
                    (String)tbClientes.getValueAt(tbClientes.getSelectedRow(), 3)); 
 
        dispose();   
    }//GEN-LAST:event_btnEntrarActionPerformed

    private void txtBuscarClienteKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarClienteKeyTyped
        
   
    }//GEN-LAST:event_txtBuscarClienteKeyTyped

    private void txtBuscarClienteKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarClienteKeyReleased
       
        rs=consulta.buscarRegistro("clientes", "id, dni, nombre, apellidos", 
                "WHERE CONCAT(nombre, apellidos) LIKE '%"+txtBuscarCliente.getText()+"%' ", "ORDER BY nombre ASC");
        
        modelo = new DefaultTableModel(null,columnas); 
        
        try 
        {            
            while(rs.next())
            {                    
                
                registro[0]=rs.getString("id");
                registro[1]=rs.getString("dni");
                registro[2]=rs.getString("nombre");
                registro[3]=rs.getString("apellidos");
                
                modelo.addRow(registro);
                tbClientes.setModel(modelo);
            }
            
        }
        catch (SQLException ex) 
        {
            JOptionPane.showMessageDialog(null, ex);
        }  
    }//GEN-LAST:event_txtBuscarClienteKeyReleased

    private void txtBuscarClienteKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarClienteKeyPressed
     
    }//GEN-LAST:event_txtBuscarClienteKeyPressed

    private void txtBuscarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarClienteActionPerformed
        
           // TODO add your handling code here:
    }//GEN-LAST:event_txtBuscarClienteActionPerformed

    private void btnBusquedaAvanzadaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBusquedaAvanzadaActionPerformed
       
        Clientes clientes = new Clientes(null, true);
        clientes.setVisible(true);
        
        dispose();
        
    }//GEN-LAST:event_btnBusquedaAvanzadaActionPerformed

    private void btnCrearFichaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCrearFichaActionPerformed

        //Si vengo de SelCliente / Crear ficha, habilito el flag
        //para que muestre la Caja al cerrar el formulario Clientes.
        SelCliente.seleccionarCliente = true;
        
        dispose();
        
        Clientes cliente = new Clientes(null, true);        
        cliente.setVisible(true);                        
        
    }//GEN-LAST:event_btnCrearFichaActionPerformed

    private void btnClienteSinFichaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClienteSinFichaActionPerformed

        seleccionarCliente = true;
        
        txtClienteSelec.setText("Cliente sin ficha");
        nomCliente = txtClienteSelec.getText();
        idCliente = "0";      
        
        dispose();
    }//GEN-LAST:event_btnClienteSinFichaActionPerformed

    private void tbClientesMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbClientesMousePressed
        txtClienteSelec.setText((String)tbClientes.getValueAt(tbClientes.getSelectedRow(), 2)+" "+
                                (String)tbClientes.getValueAt(tbClientes.getSelectedRow(), 3));

        btnEntrar.setEnabled(true); 
    }//GEN-LAST:event_tbClientesMousePressed
 public void actualizarPantalla(){ 
SwingUtilities.updateComponentTreeUI(this); 
this.validateTree(); 
} 
    public String nomCliente()
    {      
        return nomCliente;
    }
    
    public String idCliente()
    {      
        return idCliente;
    }    
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
            java.util.logging.Logger.getLogger(SelCliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SelCliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SelCliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SelCliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                SelCliente dialog = new SelCliente(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnBusquedaAvanzada;
    private javax.swing.JButton btnClienteSinFicha;
    private javax.swing.JButton btnCrearFicha;
    private javax.swing.JButton btnEntrar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tbClientes;
    private javax.swing.JTextField txtBuscarCliente;
    private javax.swing.JTextField txtClienteSelec;
    // End of variables declaration//GEN-END:variables

    private void actualizarTabla() 
    {
        
        rs=consulta.buscarRegistro("clientes", "id, dni, nombre, apellidos",null, "ORDER BY nombre ASC");
        try 
        {            
            while(rs.next())
            {                                
                registro[0]=rs.getString("id");
                registro[1]=rs.getString("dni");
                registro[2]=rs.getString("nombre");
                registro[3]=rs.getString("apellidos");
                
                modelo.addRow(registro);
            }
            tbClientes.setModel(modelo);
        }
        catch (SQLException ex) 
        {
            JOptionPane.showMessageDialog(null, ex);
        }        
    }


}
