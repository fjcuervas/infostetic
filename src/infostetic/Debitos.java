/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package infostetic;

import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/**
 *
 * @author Javi
 */
public class Debitos extends javax.swing.JDialog {

    DefaultTableModel modelo;
    Conexion consulta;
    ResultSet rs;
    
    public Debitos(java.awt.Frame parent, boolean modal) 
    {
        super(parent, modal);
        initComponents();
        setLocationRelativeTo(null);//centro la ventana en medio de la pantalla
        String[] columnas = new String [] {"id_cliente", "cliente", "total", "entregado", "debito",
                                           "forma_pago", "fecha", "metalico", "tarjeta"};
        
        modelo = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                    return false;
            }
        };
        
        for (int i=0; i<9; i++)
        {
            modelo.addColumn(columnas[i]);
        }
        
        tbDebitos.setModel(modelo);        
        
        TableColumn[] columna = new TableColumn[9];
        
        for (int i=0; i<9; i++)
        {        
            columna[i] = tbDebitos.getColumn(columnas[i]); //Obtienes la columna
        }
        
        columna[0].setPreferredWidth(20); //id cliente
        columna[1].setPreferredWidth(250); //cliente
        columna[2].setPreferredWidth(60); //total
        columna[3].setPreferredWidth(60); //entregado
        columna[4].setPreferredWidth(60); //debito
        columna[5].setPreferredWidth(100); //forma pago
        columna[6].setPreferredWidth(150); //fecha
        columna[7].setPreferredWidth(60); //metalico
        columna[8].setPreferredWidth(60); //tarjeta
                  
        comprobarDebitos();            
        
    }

    private void comprobarDebitos() 
    {
        //Compruebo si el cliente tiene débitos para añadirlo a la tabla de Compra
        consulta = new Conexion();        

       // modelo = new DefaultTableModel(null, columnas);
        
        rs = consulta.buscarRegistro("debitos", "*", null, null);  
        
        String[] debito = new String[9];
        
        try 
        {
            while (rs.next()) 
            {
                debito[0] = rs.getString("id_cliente");
                debito[1] = rs.getString("cliente");
                debito[2] = rs.getString("total");
                debito[3] = rs.getString("entregado");
                debito[4] = rs.getString("debito");
                debito[5] = rs.getString("forma_pago");
                debito[6] = rs.getString("fecha");
                debito[7] = rs.getString("metalico");
                debito[8] = rs.getString("tarjeta");
                
                modelo.addRow(debito);
                tbDebitos.setModel(modelo);                                   
            }
 
        } 
        catch (SQLException ex) 
        {
            JOptionPane.showMessageDialog(null, ex);
        }   
        
        //Con el siguiente método consigo detectar cuando nos movemos con el cursor por la tabla
        tbDebitos.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        tbDebitos.setModel(modelo);
        
        tbDebitos.getSelectionModel().addListSelectionListener(new ListSelectionListener()
                                            {
                                                public void valueChanged(ListSelectionEvent e) 
                                                {   //si se detecto un cambio de fila, coloco el nombre cliente en el label
                                                    lbNomCliente.setText((String)tbDebitos.getValueAt(tbDebitos.getSelectedRow(),1));
                                                    btnAccesoCaja.setEnabled(true);
                                                }
                                            });           
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lbNomCliente = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbDebitos = new javax.swing.JTable();
        btnAccesoCaja = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Debitos");
        setPreferredSize(new java.awt.Dimension(990, 746));
        setResizable(false);

        lbNomCliente.setBackground(new java.awt.Color(204, 204, 255));
        lbNomCliente.setFont(new java.awt.Font("Cambria", 0, 36)); // NOI18N
        lbNomCliente.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbNomCliente.setText("Cliente");
        lbNomCliente.setToolTipText("");
        lbNomCliente.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        tbDebitos.setAutoCreateRowSorter(true);
        tbDebitos.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tbDebitos.setModel(new javax.swing.table.DefaultTableModel(
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
        tbDebitos.getTableHeader().setReorderingAllowed(false);
        tbDebitos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbDebitosMouseClicked(evt);
            }
        });
        tbDebitos.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                tbDebitosPropertyChange(evt);
            }
        });
        jScrollPane1.setViewportView(tbDebitos);

        btnAccesoCaja.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/Caja_peque.png"))); // NOI18N
        btnAccesoCaja.setEnabled(false);
        btnAccesoCaja.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAccesoCajaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 980, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lbNomCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAccesoCaja, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lbNomCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnAccesoCaja, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 523, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tbDebitosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbDebitosMouseClicked

        
    }//GEN-LAST:event_tbDebitosMouseClicked

    private void tbDebitosPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_tbDebitosPropertyChange


        
    }//GEN-LAST:event_tbDebitosPropertyChange

    private void btnAccesoCajaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAccesoCajaActionPerformed

        SelCliente.idCliente = tbDebitos.getValueAt(tbDebitos.getSelectedRow(),0).toString();
        SelCliente.nomCliente = tbDebitos.getValueAt(tbDebitos.getSelectedRow(),1).toString();

        Caja caja = new Caja();

        Principal.contenedor.add(caja);
        Principal.contenedor.setSelectedIndex(Principal.contenedor.getTabCount()-1);

        dispose();
    }//GEN-LAST:event_btnAccesoCajaActionPerformed

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
            java.util.logging.Logger.getLogger(Debitos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Debitos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Debitos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Debitos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Debitos dialog = new Debitos(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnAccesoCaja;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbNomCliente;
    private javax.swing.JTable tbDebitos;
    // End of variables declaration//GEN-END:variables
}
