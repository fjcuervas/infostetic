
package infostetic;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableModel;
    
/**
 *
 * @author Javi
 */
public class Principal extends javax.swing.JFrame 
{
    //Creo el modelo de tabla que hará de intermediario entre la BD y las cajas de texto
    //Creo un objeto "dtAsig" donde se almacenarán los registros obtenidos del SELECT
    DefaultTableModel modelo;
    public Object[][] dtAsig;  
    
    Caja panelCaja;
    Productos productos;    
    Clientes clientes;
    Facturacion factura;
    Configuracion config;
    AcercaDe acerca;
    
    public Principal() throws ClassNotFoundException 
    {
        super("InfoStetic - Gestión de clientes y productos");
        initComponents();       
        setLocationRelativeTo(null);//centro la ventana en medio de la pantalla
        //abrirlo a pantalla completa
        //this.setExtendedState(MAXIMIZED_BOTH);        
        //otra forma de hacer pantalla completa al iniciar el formulario
        //setSize(Toolkit.getDefaultToolkit().getScreenSize());  
        contenedor.setShowTabArea(false);
        Logo logo = new Logo();
        contenedor.add(logo);             

    }
    
    private void añadirPanelCaja() 
    {
        panelCaja = new Caja();
        contenedor.add(panelCaja);
    }
      
    private void añadirPanelProductos() 
    {
        productos = new Productos(this, true);
        contenedor.add(productos.getComponent(0));
    }      
    
    private void añadirPanelCientes() 
    {        
        clientes = new Clientes(this, true);
        contenedor.add(clientes.getComponent(0));            
    }  
    
    private void añadirPanelFacturacion() 
    {
        factura = new Facturacion(this, true);
        contenedor.add(factura.getComponent(0));
    }     
    
    private void añadirPanelConfiguracion() 
    {
        config = new Configuracion(this, true);
        contenedor.add(config.getComponent(0));
    }   
    
    private void añadirPanelAcercaDe() 
    {
        acerca = new AcercaDe(this, true);
        contenedor.add(acerca.getComponent(0));
    }      
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        barMenu = new javax.swing.JToolBar();
        btnFacturacion = new javax.swing.JButton();
        btnProductos = new javax.swing.JButton();
        btnClientes = new javax.swing.JButton();
        btnCaja = new javax.swing.JButton();
        btnConfiguracion = new javax.swing.JButton();
        btnAcercade = new javax.swing.JButton();
        btnSalir = new javax.swing.JButton();
        contenedor = new com.jidesoft.swing.JideTabbedPane();
        menuBarra = new javax.swing.JMenuBar();
        menuMenu = new javax.swing.JMenu();
        menuFacturacion = new javax.swing.JMenuItem();
        menuProductos = new javax.swing.JMenuItem();
        menuClientes = new javax.swing.JMenuItem();
        menuCaja = new javax.swing.JMenuItem();
        menuConfiguracion = new javax.swing.JMenuItem();
        menuSalir = new javax.swing.JMenuItem();
        menuAyuda = new javax.swing.JMenu();
        menuAcercade = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1000, 755));

        barMenu.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        barMenu.setFloatable(false);
        barMenu.setAlignmentX(0.4F);

        btnFacturacion.setFont(new java.awt.Font("Cambria", 0, 16)); // NOI18N
        btnFacturacion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/Facturacion2.gif"))); // NOI18N
        btnFacturacion.setText("Facturación");
        btnFacturacion.setFocusable(false);
        btnFacturacion.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnFacturacion.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btnFacturacion.setOpaque(false);
        btnFacturacion.setPreferredSize(new java.awt.Dimension(180, 140));
        btnFacturacion.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnFacturacion.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnFacturacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFacturacionActionPerformed(evt);
            }
        });
        barMenu.add(btnFacturacion);

        btnProductos.setFont(new java.awt.Font("Cambria", 0, 16)); // NOI18N
        btnProductos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/Productos.gif"))); // NOI18N
        btnProductos.setText("Productos");
        btnProductos.setToolTipText("");
        btnProductos.setFocusable(false);
        btnProductos.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnProductos.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btnProductos.setOpaque(false);
        btnProductos.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnProductos.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnProductos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnProductosMouseClicked(evt);
            }
        });
        btnProductos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProductosActionPerformed(evt);
            }
        });
        barMenu.add(btnProductos);

        btnClientes.setFont(new java.awt.Font("Cambria", 0, 16)); // NOI18N
        btnClientes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/clientes.gif"))); // NOI18N
        btnClientes.setText("Clientes");
        btnClientes.setFocusable(false);
        btnClientes.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnClientes.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btnClientes.setPreferredSize(new java.awt.Dimension(100, 137));
        btnClientes.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnClientes.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnClientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClientesActionPerformed(evt);
            }
        });
        barMenu.add(btnClientes);

        btnCaja.setFont(new java.awt.Font("Cambria", 0, 16)); // NOI18N
        btnCaja.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/Caja.gif"))); // NOI18N
        btnCaja.setText("Caja");
        btnCaja.setFocusable(false);
        btnCaja.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCaja.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btnCaja.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnCaja.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnCaja.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCajaActionPerformed(evt);
            }
        });
        barMenu.add(btnCaja);

        btnConfiguracion.setFont(new java.awt.Font("Cambria", 0, 16)); // NOI18N
        btnConfiguracion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/Configuracion.gif"))); // NOI18N
        btnConfiguracion.setText("Configuración");
        btnConfiguracion.setFocusable(false);
        btnConfiguracion.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnConfiguracion.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btnConfiguracion.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnConfiguracion.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnConfiguracion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfiguracionActionPerformed(evt);
            }
        });
        barMenu.add(btnConfiguracion);

        btnAcercade.setFont(new java.awt.Font("Cambria", 0, 16)); // NOI18N
        btnAcercade.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/acercade.gif"))); // NOI18N
        btnAcercade.setText("Acerca de");
        btnAcercade.setFocusable(false);
        btnAcercade.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAcercade.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btnAcercade.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnAcercade.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAcercade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAcercadeActionPerformed(evt);
            }
        });
        barMenu.add(btnAcercade);

        btnSalir.setFont(new java.awt.Font("Cambria", 0, 16)); // NOI18N
        btnSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/salir.gif"))); // NOI18N
        btnSalir.setText("Salir");
        btnSalir.setFocusable(false);
        btnSalir.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSalir.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btnSalir.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnSalir.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalirActionPerformed(evt);
            }
        });
        barMenu.add(btnSalir);

        contenedor.setContentBorderInsets(new java.awt.Insets(0, 0, 0, 0));
        contenedor.setPreferredSize(new java.awt.Dimension(1000, 560));

        menuMenu.setText("Menú");

        menuFacturacion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/dolar.gif"))); // NOI18N
        menuFacturacion.setText("Facturación");
        menuFacturacion.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        menuFacturacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuFacturacionActionPerformed(evt);
            }
        });
        menuMenu.add(menuFacturacion);

        menuProductos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/codigobarras.gif"))); // NOI18N
        menuProductos.setText("Productos");
        menuProductos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuProductosActionPerformed(evt);
            }
        });
        menuMenu.add(menuProductos);

        menuClientes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/menuClientes.gif"))); // NOI18N
        menuClientes.setText("Clientes");
        menuClientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuClientesActionPerformed(evt);
            }
        });
        menuMenu.add(menuClientes);

        menuCaja.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/menuCaja.gif"))); // NOI18N
        menuCaja.setText("Caja");
        menuCaja.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuCajaActionPerformed(evt);
            }
        });
        menuMenu.add(menuCaja);

        menuConfiguracion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/menuConfiguracion.gif"))); // NOI18N
        menuConfiguracion.setText("Configuración");
        menuConfiguracion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuConfiguracionActionPerformed(evt);
            }
        });
        menuMenu.add(menuConfiguracion);

        menuSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/menuSalir.gif"))); // NOI18N
        menuSalir.setText("Salir");
        menuSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuSalirActionPerformed(evt);
            }
        });
        menuMenu.add(menuSalir);

        menuBarra.add(menuMenu);

        menuAyuda.setText("Ayuda");

        menuAcercade.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/menuAcercade.gif"))); // NOI18N
        menuAcercade.setText("Acerca de");
        menuAcercade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuAcercadeActionPerformed(evt);
            }
        });
        menuAyuda.add(menuAcercade);

        menuBarra.add(menuAyuda);

        setJMenuBar(menuBarra);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(contenedor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(barMenu, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addComponent(barMenu, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(contenedor, javax.swing.GroupLayout.DEFAULT_SIZE, 620, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnFacturacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFacturacionActionPerformed

        añadirPanelFacturacion();
        contenedor.setSelectedIndex(contenedor.getTabCount()-1);
        
    }//GEN-LAST:event_btnFacturacionActionPerformed

    private void btnProductosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProductosActionPerformed
         
        añadirPanelProductos();
        contenedor.setSelectedIndex(contenedor.getTabCount()-1);

    }//GEN-LAST:event_btnProductosActionPerformed

    private void btnClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClientesActionPerformed

        añadirPanelCientes();
        contenedor.setSelectedIndex(contenedor.getTabCount()-1);
    }//GEN-LAST:event_btnClientesActionPerformed

    private void btnCajaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCajaActionPerformed

        SelCliente cliente = new SelCliente(this, true);
        cliente.setVisible(true);  
        
        //Si al intentar entrar a Caja se selecciona un cliente
        //o se crea una ficha nueva, muestro el panel Caja
      //  if (SelCliente.seleccionarCliente)
      //  {
            añadirPanelCaja();
            contenedor.setSelectedIndex(contenedor.getTabCount()-1);
            
            //SelCliente.seleccionarCliente = false;
     //   }        
        
    }//GEN-LAST:event_btnCajaActionPerformed

    private void btnConfiguracionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfiguracionActionPerformed

        Contraseña contra = new Contraseña(this, true);
        contra.setVisible(true);

    }//GEN-LAST:event_btnConfiguracionActionPerformed

    private void btnAcercadeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAcercadeActionPerformed

        añadirPanelAcercaDe();
        contenedor.setSelectedIndex(contenedor.getTabCount()-1);
    }//GEN-LAST:event_btnAcercadeActionPerformed

    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed

        int resp = JOptionPane.showConfirmDialog(null, "  ¿ Finalizar aplicación ?", "Cerrar aplicación", JOptionPane.YES_NO_OPTION);
        
        if (resp == 0)
        {
            System.exit(0);
        }
        
    }//GEN-LAST:event_btnSalirActionPerformed
     

    private void btnProductosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnProductosMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_btnProductosMouseClicked

    private void menuFacturacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuFacturacionActionPerformed
        añadirPanelFacturacion();
        contenedor.setSelectedIndex(contenedor.getTabCount()-1);
    }//GEN-LAST:event_menuFacturacionActionPerformed

    private void menuProductosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuProductosActionPerformed
         
        añadirPanelProductos();
        contenedor.setSelectedIndex(contenedor.getTabCount()-1);
    }//GEN-LAST:event_menuProductosActionPerformed

    private void menuClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuClientesActionPerformed
       
        añadirPanelCientes();
        contenedor.setSelectedIndex(contenedor.getTabCount()-1);
    }//GEN-LAST:event_menuClientesActionPerformed

    private void menuCajaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuCajaActionPerformed
        
        SelCliente cliente = new SelCliente(this, true);
        cliente.setVisible(true);  
                       
        añadirPanelCaja();
        contenedor.setSelectedIndex(contenedor.getTabCount()-1);

    }//GEN-LAST:event_menuCajaActionPerformed

    private void menuConfiguracionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuConfiguracionActionPerformed

        Contraseña contra = new Contraseña(this, true);
        contra.setVisible(true);
    }//GEN-LAST:event_menuConfiguracionActionPerformed

    private void menuSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuSalirActionPerformed
        
        int resp = JOptionPane.showConfirmDialog(null, "  ¿ Finalizar aplicación ?", "Cerrar aplicación", JOptionPane.YES_NO_OPTION);
        
        if (resp == 0)
        {
            System.exit(0);
        }
    }//GEN-LAST:event_menuSalirActionPerformed

    private void menuAcercadeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuAcercadeActionPerformed

        añadirPanelAcercaDe();
        contenedor.setSelectedIndex(contenedor.getTabCount()-1);
    }//GEN-LAST:event_menuAcercadeActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
 
        //</editor-fold>
 InetAddress ip;
    try {

        ip = InetAddress.getLocalHost();
        System.out.println("Current IP address : " + ip.getHostAddress());

        NetworkInterface network = NetworkInterface.getByInetAddress(ip);

        byte[] mac = network.getHardwareAddress();

        System.out.print("Current MAC address : ");

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mac.length; i++) {
            sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));        
        }
        System.out.println(sb.toString());

    } catch (UnknownHostException e) {

        e.printStackTrace();

    } catch (SocketException e){

        e.printStackTrace();
 
    }        

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            
            @Override
            public void run() 
            {
                //configuro el aspecto que tendrá la aplicación. En este caso será la del 
                //sistema operativo que la esté ejecutando en ese momento
                try
                {
                    JFrame.setDefaultLookAndFeelDecorated(true);
                    JDialog.setDefaultLookAndFeelDecorated(true);
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

                }
                catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e)
                {
                }
                
                try 
                {
                    new Principal().setVisible(true);
                } 
                catch (ClassNotFoundException ex) 
                {
                    Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToolBar barMenu;
    private javax.swing.JButton btnAcercade;
    private javax.swing.JButton btnCaja;
    private javax.swing.JButton btnClientes;
    private javax.swing.JButton btnConfiguracion;
    private javax.swing.JButton btnFacturacion;
    private javax.swing.JButton btnProductos;
    private javax.swing.JButton btnSalir;
    public static com.jidesoft.swing.JideTabbedPane contenedor;
    private javax.swing.JMenuItem menuAcercade;
    private javax.swing.JMenu menuAyuda;
    private javax.swing.JMenuBar menuBarra;
    private javax.swing.JMenuItem menuCaja;
    private javax.swing.JMenuItem menuClientes;
    private javax.swing.JMenuItem menuConfiguracion;
    private javax.swing.JMenuItem menuFacturacion;
    private javax.swing.JMenu menuMenu;
    private javax.swing.JMenuItem menuProductos;
    private javax.swing.JMenuItem menuSalir;
    // End of variables declaration//GEN-END:variables







}
