
package infostetic;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;


/**
 *
 * @author Javi
 */
public class Facturacion extends javax.swing.JDialog {

    Conexion consulta = new Conexion();
    DefaultTableModel modelo;
    ResultSet rs = null;
    String[][] productos;
    String[][] clientes;
    String[] columNames;
    String fechaDesde;
    String fechaHasta;
    String fecha;
    String where = null;
    SimpleDateFormat dateformat;
    ButtonGroup grupoBotones;
    
    public Facturacion(java.awt.Frame parent, boolean modal) 
    {
        super(parent, modal);
        initComponents();
        grupoBotones = new ButtonGroup();
        grupoBotones.add(rbEfectivo);
        grupoBotones.add(rbTarjeta);
        grupoBotones.add(rbEfectivoTarjeta);
        grupoBotones.add(rbDebito);
        rbEfectivoTarjeta.setSelected(true);
        
        Calendar ahora = Calendar.getInstance();        
        //Hacer formateador de la fecha, se le pasa el formato en que se quiere obtener la fecha.
        dateformat = new SimpleDateFormat("yyyy-MM-dd"); 
        fecha = dateformat.format(ahora.getTime()); 
        fechaDesde = dateformat.format(calendarioDesde.getDate());    
        fechaHasta = dateformat.format(calendarioHasta.getDate());   

        where = " where (fecha BETWEEN '" + fechaDesde + "%' AND '" + fechaHasta + "' OR fecha LIKE '" + fechaHasta + "%')"
              + " AND producto NOT LIKE 'DEBITO'";
        
        crearTablaGanancias(where); 
        
        cargarComboProductos();
        
        cargarComboClientes();
        
        //Coloco el período de facturación en el label Fecha con el formato día-mes-año
        dateformat = new SimpleDateFormat("dd-MM-yyyy"); 
        fechaDesde = dateformat.format(calendarioDesde.getDate());
        fechaHasta = dateformat.format(calendarioHasta.getDate());
        lbFecha.setText("Facturación del  :   " + fechaDesde + "   al   " + fechaHasta + "    ");
        
        //Por defecto no selecciono nada en el combo
        cbClientes.setSelectedIndex(-1);
        cbProductos.setSelectedIndex(-1);
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
    private void crearTablaGanancias (String where)
    {    

        BigDecimal totalGastado = new BigDecimal (0);
        BigDecimal totalEfectivo = new BigDecimal (0);
        BigDecimal totalTarjeta = new BigDecimal (0);

        String tabla = "historico";
        /*String camposHistorico = "id, producto, cantidad, precio, total, forma_pago, "
                                + "metalico, tarjeta, fecha, observaciones";  */
        
        

        /*where = acotacion + " AND (forma_pago NOT LIKE 'DEBITO' AND producto NOT LIKE 'DEBITO'"
              + " OR forma_pago LIKE 'DEBITO' AND producto LIKE 'DEBITO PAGADO')";*/

        rs = consulta.selectResult("historico", "*", where);        

        try 
        {
            Boolean vacio = true;
            while (rs.next())
            {   
                                
                //Si son DEBITOS los sumo aparte ya que sino me suma todos los totales 
                //cuando solo necesito los totales de cada producto que dejó a deber
                if (rs.getString("producto").equals("DEBITO"))
                {                    
                    totalGastado = totalGastado.add(BigDecimal.valueOf(Double.parseDouble(rs.getString("total"))));
                    txtTotal.setText(totalGastado.toString());  
                    
                    //Al ser un débito, efectivo y tarjeta están a 0
                    txtEfectivo.setText("0");
                    txtTarjeta.setText("0");
                }
                //si se trata de ganancias, voy sumando el total de todo lo vendido
                else if (rs.getString("producto").equals("TOTAL"))
                {
                    //System.out.println("total: "+rs.getString("total"));
                    /*totalGastado = totalGastado.add(BigDecimal.valueOf(Double.parseDouble(rs.getString("total"))));
                    txtTotal.setText(totalGastado.toString());  */
                    totalEfectivo = totalEfectivo.add(BigDecimal.valueOf(Double.parseDouble(rs.getString("metalico"))));
                    txtEfectivo.setText(totalEfectivo.toString());

                    totalTarjeta = totalTarjeta.add(BigDecimal.valueOf(Double.parseDouble(rs.getString("tarjeta"))));
                    txtTarjeta.setText(totalTarjeta.toString());    
                
                    totalGastado = totalGastado.add(BigDecimal.valueOf(Double.parseDouble(rs.getString("total")))); 
                    txtTotal.setText(totalGastado.toString());
                }
                
                //si se trata de un producto en concreto, sumo el total de ese producto
                else if (cbProductos.getSelectedIndex() != -1)
                {
                    //Como no puedo saber lo que se cobró exáctamente en efectivo o tarjeta
                    //de ese producto en concreto, calculo el total de productos vendidos, según
                    //el filtrado de 'Tarjeta' o 'Efectivo'
                    
                    totalGastado = totalGastado.add(BigDecimal.valueOf(Double.parseDouble(rs.getString("total")))); 
                    
                    if (botonSeleccionado(grupoBotones).equals("Efectivo"))
                    {                       
                        txtEfectivo.setText(totalGastado.toString());
                    } 
                    else if (botonSeleccionado(grupoBotones).equals("Tarjeta"))
                    {
                        txtTarjeta.setText(totalGastado.toString());
                    }
                    else
                    {
                        
                    }
                }
                                              
                vacio = false;
            }
            //Si vacio es true, siginifica que no se encontraron registros
            if (vacio)
            {
                txtTotal.setText("0");  
                txtEfectivo.setText("0"); 
                txtTarjeta.setText("0"); 
            }
            
            //Si se seleccionó Efectivo o tarjeta, muestro el total de los pagos que
            //se realizaron solo en efectivo, o solo tarjeta. El total será igual a la forma elgida.
            if (botonSeleccionado(grupoBotones).equals("Efectivo") && cbProductos.getSelectedIndex() == -1)
            {
                txtTarjeta.setText("0"); 
                txtTotal.setText(txtEfectivo.getText());
            }
            else if (botonSeleccionado(grupoBotones).equals("Tarjeta") && cbProductos.getSelectedIndex() == -1)
            {
                txtEfectivo.setText("0"); 
                txtTotal.setText(txtTarjeta.getText());
            }
            
        } 
        catch (SQLException ex) 
        {
            JOptionPane.showMessageDialog(null, ex);
        }  
        
        columNames = new String[] {"id", "id_cliente", "cliente", "producto", "total",
                                   "forma_pago", "fecha"
                                  };         
        
        String camposHistorico = "id, id_cliente, cliente, producto, total, forma_pago, "
                                + "fecha"; 
        
        modelo = consulta.selectModelo(tabla, camposHistorico, where, columNames);
        tbHistorico.setModel(modelo); 
        
        //La columna id no la muestro, pero me hará falta para eliminar el registro
        tbHistorico.getColumnModel().getColumn(0).setMaxWidth(0);
        tbHistorico.getColumnModel().getColumn(0).setMinWidth(0);
        tbHistorico.getColumnModel().getColumn(0).setPreferredWidth(0);     
            
        tbHistorico.getColumnModel().getColumn(1).setPreferredWidth(40); //id_cliente
        tbHistorico.getColumnModel().getColumn(2).setPreferredWidth(240); //cliente        
        tbHistorico.getColumnModel().getColumn(3).setPreferredWidth(240);  //producto
        tbHistorico.getColumnModel().getColumn(4).setPreferredWidth(80); //total
        tbHistorico.getColumnModel().getColumn(5).setPreferredWidth(80); //forma_pago
        tbHistorico.getColumnModel().getColumn(6).setPreferredWidth(150); //fecha        
        
      /*  //La columna id no la muestro, pero me hará falta para eliminar el registro
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
        tbHistorico.getColumnModel().getColumn(8).setPreferredWidth(150); //fecha*/
        
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
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tbHistorico = new javax.swing.JTable();
        panelHistorico = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        lbTarjeta = new javax.swing.JLabel();
        txtTarjeta = new javax.swing.JTextField();
        txtEfectivo = new javax.swing.JTextField();
        txtTotal = new javax.swing.JTextField();
        lbEfectivo = new javax.swing.JLabel();
        lbTotal = new javax.swing.JLabel();
        panelAcotacionDesde = new javax.swing.JPanel();
        lbEfectivo1 = new javax.swing.JLabel();
        panelDesde = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        calendarioDesde = new com.toedter.calendar.JCalendar();
        panelAcotacionHasta = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        calendarioHasta = new com.toedter.calendar.JCalendar();
        jPanel1 = new javax.swing.JPanel();
        lbEfectivo2 = new javax.swing.JLabel();
        lbFecha = new javax.swing.JLabel();
        panelFiltroYbotones = new javax.swing.JPanel();
        cbClientes = new javax.swing.JComboBox();
        btnMostrarDatos = new javax.swing.JButton();
        lbProducto = new javax.swing.JLabel();
        btnImprimir = new javax.swing.JButton();
        btnRestaurar = new javax.swing.JButton();
        lbCliente = new javax.swing.JLabel();
        cbProductos = new javax.swing.JComboBox();
        lbFormaPago = new javax.swing.JLabel();
        panelFormaPago = new javax.swing.JPanel();
        rbTarjeta = new javax.swing.JRadioButton();
        rbEfectivoTarjeta = new javax.swing.JRadioButton();
        rbEfectivo = new javax.swing.JRadioButton();
        rbDebito = new javax.swing.JRadioButton();
        btnBorrarProducto = new javax.swing.JButton();
        btnBorrarCliente = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1000, 581));
        setResizable(false);

        jScrollPane1.setAutoscrolls(true);

        tbHistorico.setAutoCreateRowSorter(true);
        tbHistorico.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153))));
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

        panelHistorico.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        panelHistorico.setLayout(new java.awt.GridBagLayout());

        lbTarjeta.setFont(new java.awt.Font("Cambria", 0, 18)); // NOI18N
        lbTarjeta.setText("Tarjeta");

        txtTarjeta.setEditable(false);
        txtTarjeta.setBackground(new java.awt.Color(255, 255, 255));
        txtTarjeta.setFont(new java.awt.Font("Cambria", 0, 24)); // NOI18N
        txtTarjeta.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        txtEfectivo.setEditable(false);
        txtEfectivo.setBackground(new java.awt.Color(255, 255, 255));
        txtEfectivo.setFont(new java.awt.Font("Cambria", 0, 24)); // NOI18N
        txtEfectivo.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        txtTotal.setEditable(false);
        txtTotal.setBackground(new java.awt.Color(255, 255, 255));
        txtTotal.setFont(new java.awt.Font("Cambria", 0, 24)); // NOI18N
        txtTotal.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        lbEfectivo.setFont(new java.awt.Font("Cambria", 0, 18)); // NOI18N
        lbEfectivo.setText("Efectivo");

        lbTotal.setFont(new java.awt.Font("Cambria", 0, 18)); // NOI18N
        lbTotal.setText("Total");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtEfectivo, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(txtTarjeta, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(129, 129, 129)
                        .addComponent(lbTotal))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(87, 87, 87)
                        .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addComponent(lbEfectivo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lbTarjeta)
                .addGap(43, 43, 43))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbTarjeta)
                    .addComponent(lbEfectivo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtEfectivo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTarjeta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbTotal)
                .addGap(4, 4, 4)
                .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelHistorico.add(jPanel8, new java.awt.GridBagConstraints());

        panelAcotacionDesde.setPreferredSize(new java.awt.Dimension(400, 30));

        lbEfectivo1.setFont(new java.awt.Font("Cambria", 0, 18)); // NOI18N
        lbEfectivo1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbEfectivo1.setText("Acotaciones de tiempo");
        lbEfectivo1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lbEfectivo1.setPreferredSize(new java.awt.Dimension(400, 24));

        panelDesde.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        panelDesde.setPreferredSize(new java.awt.Dimension(400, 215));

        jLabel1.setFont(new java.awt.Font("Cambria", 0, 18)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Desde: ");
        jLabel1.setPreferredSize(new java.awt.Dimension(400, 22));

        calendarioDesde.setFont(new java.awt.Font("Cambria", 0, 14)); // NOI18N
        calendarioDesde.setMaxSelectableDate(new java.util.Date(253370764877000L));
        calendarioDesde.setPreferredSize(new java.awt.Dimension(400, 174));
        calendarioDesde.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                calendarioDesdeMouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                calendarioDesdeMouseReleased(evt);
            }
        });
        calendarioDesde.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                calendarioDesdePropertyChange(evt);
            }
        });

        javax.swing.GroupLayout panelDesdeLayout = new javax.swing.GroupLayout(panelDesde);
        panelDesde.setLayout(panelDesdeLayout);
        panelDesdeLayout.setHorizontalGroup(
            panelDesdeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(calendarioDesde, javax.swing.GroupLayout.DEFAULT_SIZE, 398, Short.MAX_VALUE)
            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        panelDesdeLayout.setVerticalGroup(
            panelDesdeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDesdeLayout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(calendarioDesde, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout panelAcotacionDesdeLayout = new javax.swing.GroupLayout(panelAcotacionDesde);
        panelAcotacionDesde.setLayout(panelAcotacionDesdeLayout);
        panelAcotacionDesdeLayout.setHorizontalGroup(
            panelAcotacionDesdeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelDesde, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(lbEfectivo1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        panelAcotacionDesdeLayout.setVerticalGroup(
            panelAcotacionDesdeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAcotacionDesdeLayout.createSequentialGroup()
                .addComponent(lbEfectivo1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelDesde, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        panelAcotacionHasta.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        panelAcotacionHasta.setPreferredSize(new java.awt.Dimension(400, 187));

        jLabel2.setFont(new java.awt.Font("Cambria", 0, 18)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Hasta:");
        jLabel2.setPreferredSize(new java.awt.Dimension(400, 22));

        calendarioHasta.setFont(new java.awt.Font("Cambria", 0, 14)); // NOI18N
        calendarioHasta.setPreferredSize(new java.awt.Dimension(400, 174));

        javax.swing.GroupLayout panelAcotacionHastaLayout = new javax.swing.GroupLayout(panelAcotacionHasta);
        panelAcotacionHasta.setLayout(panelAcotacionHastaLayout);
        panelAcotacionHastaLayout.setHorizontalGroup(
            panelAcotacionHastaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(calendarioHasta, javax.swing.GroupLayout.PREFERRED_SIZE, 398, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 398, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        panelAcotacionHastaLayout.setVerticalGroup(
            panelAcotacionHastaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAcotacionHastaLayout.createSequentialGroup()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(calendarioHasta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.setLayout(new java.awt.GridLayout(2, 1, 0, 5));

        lbEfectivo2.setFont(new java.awt.Font("Cambria", 0, 18)); // NOI18N
        lbEfectivo2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbEfectivo2.setText("Histórico");
        lbEfectivo2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(lbEfectivo2);

        lbFecha.setFont(new java.awt.Font("Cambria", 0, 18)); // NOI18N
        lbFecha.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbFecha.setText("Fecha historico");
        lbFecha.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        jPanel1.add(lbFecha);

        panelFiltroYbotones.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        panelFiltroYbotones.setPreferredSize(new java.awt.Dimension(400, 138));

        btnMostrarDatos.setText("Mostrar datos");
        btnMostrarDatos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMostrarDatosActionPerformed(evt);
            }
        });

        lbProducto.setText("      Producto:");

        btnImprimir.setText("Imprimir");
        btnImprimir.setPreferredSize(new java.awt.Dimension(99, 23));
        btnImprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimirActionPerformed(evt);
            }
        });

        btnRestaurar.setText("Restaurar valores");
        btnRestaurar.setPreferredSize(new java.awt.Dimension(99, 23));
        btnRestaurar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRestaurarActionPerformed(evt);
            }
        });

        lbCliente.setText("Cliente:");

        lbFormaPago.setText("Forma pago  : ");

        panelFormaPago.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

        rbTarjeta.setText("Tarjeta");

        rbEfectivoTarjeta.setText("Efectivo y Tarjeta");

        rbEfectivo.setText("Efectivo");

        rbDebito.setText("Debito");

        javax.swing.GroupLayout panelFormaPagoLayout = new javax.swing.GroupLayout(panelFormaPago);
        panelFormaPago.setLayout(panelFormaPagoLayout);
        panelFormaPagoLayout.setHorizontalGroup(
            panelFormaPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFormaPagoLayout.createSequentialGroup()
                .addComponent(rbDebito)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rbEfectivo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rbTarjeta)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(rbEfectivoTarjeta)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelFormaPagoLayout.setVerticalGroup(
            panelFormaPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFormaPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(rbTarjeta)
                .addComponent(rbEfectivoTarjeta)
                .addComponent(rbEfectivo)
                .addComponent(rbDebito))
        );

        btnBorrarProducto.setText("Borrar");
        btnBorrarProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBorrarProductoActionPerformed(evt);
            }
        });

        btnBorrarCliente.setText("Borrar");
        btnBorrarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBorrarClienteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelFiltroYbotonesLayout = new javax.swing.GroupLayout(panelFiltroYbotones);
        panelFiltroYbotones.setLayout(panelFiltroYbotonesLayout);
        panelFiltroYbotonesLayout.setHorizontalGroup(
            panelFiltroYbotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFiltroYbotonesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFiltroYbotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelFiltroYbotonesLayout.createSequentialGroup()
                        .addGroup(panelFiltroYbotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(panelFiltroYbotonesLayout.createSequentialGroup()
                                .addComponent(lbFormaPago, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(panelFormaPago, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                            .addGroup(panelFiltroYbotonesLayout.createSequentialGroup()
                                .addComponent(lbProducto)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbProductos, javax.swing.GroupLayout.PREFERRED_SIZE, 218, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnBorrarProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFiltroYbotonesLayout.createSequentialGroup()
                        .addGroup(panelFiltroYbotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFiltroYbotonesLayout.createSequentialGroup()
                                .addComponent(btnMostrarDatos, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnRestaurar, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnImprimir, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFiltroYbotonesLayout.createSequentialGroup()
                                .addGap(26, 26, 26)
                                .addComponent(lbCliente)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbClientes, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnBorrarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(26, 26, 26))))
        );
        panelFiltroYbotonesLayout.setVerticalGroup(
            panelFiltroYbotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFiltroYbotonesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFiltroYbotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panelFormaPago, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbFormaPago, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFiltroYbotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbProductos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBorrarProducto)
                    .addComponent(lbProducto))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFiltroYbotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbClientes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbCliente)
                    .addComponent(btnBorrarCliente))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFiltroYbotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnImprimir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnMostrarDatos)
                    .addComponent(btnRestaurar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(panelAcotacionHasta, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panelAcotacionDesde, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panelFiltroYbotones, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelHistorico, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 488, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelHistorico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(panelAcotacionDesde, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panelAcotacionHasta, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelFiltroYbotones, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tbHistoricoMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbHistoricoMouseReleased

    }//GEN-LAST:event_tbHistoricoMouseReleased

    private void tbHistoricoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbHistoricoMouseClicked
        
    }//GEN-LAST:event_tbHistoricoMouseClicked

    private void tbHistoricoMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbHistoricoMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_tbHistoricoMouseEntered

    private void tbHistoricoPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_tbHistoricoPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_tbHistoricoPropertyChange

    private void calendarioDesdeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_calendarioDesdeMouseClicked
        

        
    }//GEN-LAST:event_calendarioDesdeMouseClicked

    private void calendarioDesdeMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_calendarioDesdeMouseReleased

    }//GEN-LAST:event_calendarioDesdeMouseReleased

    private void calendarioDesdePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_calendarioDesdePropertyChange
 
    }//GEN-LAST:event_calendarioDesdePropertyChange

    private void btnBorrarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBorrarClienteActionPerformed

        cbClientes.setSelectedIndex(-1);
    }//GEN-LAST:event_btnBorrarClienteActionPerformed

    private void btnBorrarProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBorrarProductoActionPerformed

        cbProductos.setSelectedIndex(-1);
    }//GEN-LAST:event_btnBorrarProductoActionPerformed

    private void btnRestaurarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRestaurarActionPerformed

        cbProductos.setSelectedIndex(-1);
        cbClientes.setSelectedIndex(-1);
        rbEfectivoTarjeta.setSelected(true);
    }//GEN-LAST:event_btnRestaurarActionPerformed

    private void btnImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirActionPerformed

        String [][] parametros = new String [4][2];
        parametros [0][0] = "where";
        parametros [0][1] = where;
        parametros [1][0] = "total";
        parametros [1][1] = txtTotal.getText();
        parametros [2][0] = "efectivo";
        parametros [2][1] = txtEfectivo.getText();
        parametros [3][0] = "tarjeta";
        parametros [3][1] = txtTarjeta.getText();
        consulta.reporte(parametros);
        /*historico.`id_cliente` AS id_cliente,
        clientes.`id` AS clientes_id,
        clientes.`dni` AS dni,
        clientes.`nombre` AS nombre,
        clientes.`apellidos` AS apellidos,
        historico.`fecha` AS fecha,
        historico.`producto` AS producto,
        historico.`cantidad` AS cantidad,
        historico.`total` AS total*/
        //consulta.reporte2();
        //ajFrame reporte = new ajFrame();
        //reporte.cargarReporte("SELECT * FROM historico", "reporte2");
    }//GEN-LAST:event_btnImprimirActionPerformed

    private void btnMostrarDatosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMostrarDatosActionPerformed

        //Coloco el período de facturación en el label Fecha con el formato día-mes-año
        dateformat = new SimpleDateFormat("dd-MM-yyyy");
        fechaDesde = dateformat.format(calendarioDesde.getDate());
        fechaHasta = dateformat.format(calendarioHasta.getDate());
        lbFecha.setText("Facturación del  :   " + fechaDesde + "   al   " + fechaHasta + "    ");

        //Vuelvo a dar formato a la fecha ya que la base de datos utiliza año-mes-día
        dateformat = new SimpleDateFormat("yyyy-MM-dd");
        fechaDesde = dateformat.format(calendarioDesde.getDate());
        fechaHasta = dateformat.format(calendarioHasta.getDate());

        System.out.println("CBCLIENTE: "+cbClientes.getSelectedIndex());
        System.out.println("CBPRODUCTO: "+cbProductos.getSelectedIndex());
        System.out.println("BOTONES: "+botonSeleccionado(grupoBotones).toString());

        //Si se solo se filtró por forma_pago, ejecuto este código
        if (cbClientes.getSelectedIndex() == -1 && cbProductos.getSelectedIndex() == -1
            && botonSeleccionado(grupoBotones).equals("Efectivo y Tarjeta") == false)
        {
            //Si se seleccionó Tarjeta, muestro los pagos con tarjeta
            if (botonSeleccionado(grupoBotones).equals("Tarjeta"))
            {
                where = " where (fecha BETWEEN '" + fechaDesde + "%'"
                + " AND '" + fechaHasta + "' OR fecha LIKE '" + fechaHasta + "%')"
                + " AND (forma_pago LIKE 'TARJETA'"
                + " OR forma_pago LIKE 'MIXTO')"
                + " AND producto NOT LIKE 'DEBITO'";
            }
            //Si se seleccionó Efectivo, muestro los pagos con efectivo
            else if (botonSeleccionado(grupoBotones).equals("Efectivo"))
            {
                where = " where forma_pago NOT LIKE 'DEBITO'"
                + " AND forma_pago NOT LIKE 'TARJETA'"
                + " AND (fecha BETWEEN '" + fechaDesde + "%'"
                + " AND '" + fechaHasta + "' OR fecha LIKE '" + fechaHasta + "%')"
                + " AND producto NOT LIKE 'DEBITO'";
            }
            //Si se seleccionó Debito, muestro los débitos
            else if (botonSeleccionado(grupoBotones).equals("Debito"))
            {
                where = " where (fecha BETWEEN '" + fechaDesde + "%'"
                + " AND '" + fechaHasta + "' OR fecha LIKE '" + fechaHasta + "%')"
                + " AND producto LIKE 'DEBITO'"
                //esta expresion se la pongo porque el report no me cargaba
                //la SQL. Comprobé que poniendo un NOT LIKE lo q sea funcionaba :S
                + " AND producto NOT LIKE ''";
            }
        }

        //Si se filtró por producto y forma_pago, ejecuto este código
        else if (cbClientes.getSelectedIndex() == -1 && cbProductos.getSelectedIndex() != -1
            && botonSeleccionado(grupoBotones).equals("Efectivo y Tarjeta") == false)
        {
            if (botonSeleccionado(grupoBotones).equals("Tarjeta"))
            {
                where = " where (forma_pago LIKE 'TARJETA'"
                + " OR forma_pago LIKE 'MIXTO')"
                + " AND id_producto='" + productos[cbProductos.getSelectedIndex()][0] + "'"
                + " AND (fecha BETWEEN '" + fechaDesde + "%'"
                + " AND '" + fechaHasta + "' OR fecha LIKE '" + fechaHasta + "%')"
                + " AND producto NOT LIKE ''";
            }
            else if (botonSeleccionado(grupoBotones).equals("Efectivo"))
            {
                where = " where forma_pago NOT LIKE 'DEBITO'"
                + " AND forma_pago NOT LIKE 'TARJETA'"
                + " AND id_producto='" + productos[cbProductos.getSelectedIndex()][0] + "'"
                + " AND (fecha BETWEEN '" + fechaDesde + "%'"
                + " AND '" + fechaHasta + "' OR fecha LIKE '" + fechaHasta + "%')"
                + " AND producto NOT LIKE ''";
            }
            //Si se seleccionó Debito, muestro los débitos
            else if (botonSeleccionado(grupoBotones).equals("Debito"))
            {
                where = " where producto LIKE 'DEBITO'"
                + " AND id_producto='" + productos[cbProductos.getSelectedIndex()][0] + "'"
                + " AND (fecha BETWEEN '" + fechaDesde + "%'"
                + " AND '" + fechaHasta + "' OR fecha LIKE '" + fechaHasta + "%')"
                + " AND producto NOT LIKE ''";
            }
        }

        //Si se filtró por cliente y forma_pago, ejecuto este código
        else if (cbClientes.getSelectedIndex() != -1 && cbProductos.getSelectedIndex() == -1
            && botonSeleccionado(grupoBotones).equals("Efectivo y Tarjeta") == false)
        {
            if (botonSeleccionado(grupoBotones).equals("Tarjeta"))
            {
                where = " where (forma_pago LIKE 'TARJETA'"
                + " OR forma_pago LIKE 'MIXTO')"
                + " AND id_cliente='" + clientes[cbClientes.getSelectedIndex()][0] + "'"
                + " AND (fecha BETWEEN '" + fechaDesde + "%'"
                + " AND '" + fechaHasta + "' OR fecha LIKE '" + fechaHasta + "%')"
                + " AND producto NOT LIKE ''";
            }
            else if (botonSeleccionado(grupoBotones).equals("Efectivo"))
            {
                where = " where forma_pago NOT LIKE 'DEBITO'"
                + " AND forma_pago NOT LIKE 'TARJETA'"
                + " AND id_cliente='" + clientes[cbClientes.getSelectedIndex()][0] + "'"
                + " AND (fecha BETWEEN '" + fechaDesde + "%'"
                + " AND '" + fechaHasta + "' OR fecha LIKE '" + fechaHasta + "%')"
                + " AND producto NOT LIKE ''";
            }
            //Si se seleccionó Debito, muestro los débitos
            else if (botonSeleccionado(grupoBotones).equals("Debito"))
            {
                where = " where producto LIKE 'DEBITO'"
                + " AND id_cliente='" + clientes[cbClientes.getSelectedIndex()][0] + "'"
                + " AND (fecha BETWEEN '" + fechaDesde + "%'"
                + " AND '" + fechaHasta + "' OR fecha LIKE '" + fechaHasta + "%')"
                + " AND producto NOT LIKE ''";
            }
        }

        //Si se filtró por cliente, producto y forma_pago, ejecuto este código
        else if (cbClientes.getSelectedIndex() != -1 && cbProductos.getSelectedIndex() != -1
            && botonSeleccionado(grupoBotones).equals("Efectivo y Tarjeta") == false)
        {
            if (botonSeleccionado(grupoBotones).equals("Tarjeta"))
            {
                where = " where (forma_pago LIKE 'TARJETA'"
                + " OR forma_pago LIKE 'MIXTO')"
                + " AND id_cliente='" + clientes[cbClientes.getSelectedIndex()][0] + "'"
                + " AND id_producto='" + productos[cbProductos.getSelectedIndex()][0] + "'"
                + " AND (fecha BETWEEN '" + fechaDesde + "%'"
                + " AND '" + fechaHasta + "' OR fecha LIKE '" + fechaHasta + "%')"
                + " AND producto NOT LIKE ''";
            }
            else if (botonSeleccionado(grupoBotones).equals("Efectivo"))
            {
                where = " where forma_pago NOT LIKE 'DEBITO'"
                + " AND forma_pago NOT LIKE 'TARJETA'"
                + " AND id_cliente='" + clientes[cbClientes.getSelectedIndex()][0] + "'"
                + " AND id_producto='" + productos[cbProductos.getSelectedIndex()][0] + "'"
                + " AND (fecha BETWEEN '" + fechaDesde + "%'"
                + " AND '" + fechaHasta + "' OR fecha LIKE '" + fechaHasta + "%')"
                + " AND producto NOT LIKE ''";
            }
            //Si se seleccionó Debito, muestro los débitos
            else if (botonSeleccionado(grupoBotones).equals("Debito"))
            {
                where = " where producto LIKE 'DEBITO'"
                + " AND id_cliente='" + clientes[cbClientes.getSelectedIndex()][0] + "'"
                + " AND id_producto='" + productos[cbProductos.getSelectedIndex()][0] + "'"
                + " AND (fecha BETWEEN '" + fechaDesde + "%'"
                + " AND '" + fechaHasta + "' OR fecha LIKE '" + fechaHasta + "%')"
                + " AND producto NOT LIKE ''";
            }
        }

        //Si se filtró por producto y cliente, ejecuto este código
        else if (cbClientes.getSelectedIndex() != -1 && cbProductos.getSelectedIndex() != -1
            && botonSeleccionado(grupoBotones).equals("Efectivo y Tarjeta"))
        {
            where = " where id_producto='" + productos[cbProductos.getSelectedIndex()][0] + "'"
            + " AND id_cliente='" + clientes[cbClientes.getSelectedIndex()][0] + "'"
            + " AND producto NOT LIKE 'DEBITO'"
            + " AND (fecha BETWEEN '" + fechaDesde + "%'"
            + " AND '" + fechaHasta + "' OR fecha LIKE '" + fechaHasta + "%')"
            + " AND producto NOT LIKE ''";

        }

        //Si se filtró solo por producto, ejecuto este código
        else if (cbClientes.getSelectedIndex() == -1 && cbProductos.getSelectedIndex() != -1
            && botonSeleccionado(grupoBotones).equals("Efectivo y Tarjeta"))
        {
            where = " where id_producto='" + productos[cbProductos.getSelectedIndex()][0] + "'"
            //+ " AND forma_pago NOT LIKE 'DEBITO'"
            + " AND (fecha BETWEEN '" + fechaDesde + "%'"
            + " AND '" + fechaHasta + "' OR fecha LIKE '" + fechaHasta + "%')"
            + " AND producto NOT LIKE ''";
        }

        //Si se filtró solo por cliente, ejecuto este código
        else if (cbClientes.getSelectedIndex() != -1 && cbProductos.getSelectedIndex() == -1
            && botonSeleccionado(grupoBotones).equals("Efectivo y Tarjeta"))
        {
            where = " where id_cliente='" + clientes[cbClientes.getSelectedIndex()][0] + "'"
            + " AND producto NOT LIKE 'DEBITO'"
            + " AND (fecha BETWEEN '" + fechaDesde + "%'"
            + " AND '" + fechaHasta + "' OR fecha LIKE '" + fechaHasta + "%')"
            + " AND producto NOT LIKE ''";
        }

        //Si no se filtró por ninguna de las opciones, hago una consulta normal con las fechas indicadas
        else if (cbClientes.getSelectedIndex() == -1 && cbProductos.getSelectedIndex() == -1
            && botonSeleccionado(grupoBotones).equals("Efectivo y Tarjeta"))
        {

            where = " where (fecha BETWEEN '" + fechaDesde + "%' AND '" + fechaHasta + "' OR fecha LIKE '" + fechaHasta + "%')"
            + " AND producto NOT LIKE 'DEBITO'";
        }

        txtEfectivo.setText("0");
        txtTarjeta.setText("0");
        txtTotal.setText("0");

        crearTablaGanancias(where);
    }//GEN-LAST:event_btnMostrarDatosActionPerformed

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
            java.util.logging.Logger.getLogger(Facturacion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Facturacion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Facturacion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Facturacion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Facturacion dialog = new Facturacion(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnBorrarCliente;
    private javax.swing.JButton btnBorrarProducto;
    private javax.swing.JButton btnImprimir;
    private javax.swing.JButton btnMostrarDatos;
    private javax.swing.JButton btnRestaurar;
    private com.toedter.calendar.JCalendar calendarioDesde;
    private com.toedter.calendar.JCalendar calendarioHasta;
    private javax.swing.JComboBox cbClientes;
    private javax.swing.JComboBox cbProductos;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbCliente;
    private javax.swing.JLabel lbEfectivo;
    private javax.swing.JLabel lbEfectivo1;
    private javax.swing.JLabel lbEfectivo2;
    private javax.swing.JLabel lbFecha;
    private javax.swing.JLabel lbFormaPago;
    private javax.swing.JLabel lbProducto;
    private javax.swing.JLabel lbTarjeta;
    private javax.swing.JLabel lbTotal;
    private javax.swing.JPanel panelAcotacionDesde;
    private javax.swing.JPanel panelAcotacionHasta;
    private javax.swing.JPanel panelDesde;
    private javax.swing.JPanel panelFiltroYbotones;
    private javax.swing.JPanel panelFormaPago;
    private javax.swing.JPanel panelHistorico;
    private javax.swing.JRadioButton rbDebito;
    private javax.swing.JRadioButton rbEfectivo;
    private javax.swing.JRadioButton rbEfectivoTarjeta;
    private javax.swing.JRadioButton rbTarjeta;
    private javax.swing.JTable tbHistorico;
    private javax.swing.JTextField txtEfectivo;
    private javax.swing.JTextField txtTarjeta;
    private javax.swing.JTextField txtTotal;
    // End of variables declaration//GEN-END:variables

    private void cargarComboProductos() {
                
        rs = consulta.selectResult("productos", "count(*) as total", null);
        
        int dimension = 0;
        try 
        {
            rs.next();
            dimension = Integer.parseInt(rs.getString("total"));
        } catch (SQLException ex) 
        {
            JOptionPane.showMessageDialog(null, ex);
        }
        productos = new String [dimension][2];
        
        rs = consulta.selectResult("productos", "id, producto", " ORDER BY producto ASC");
        
        try
        {
            int i = 0;
            while (rs.next())
            {
                cbProductos.addItem(rs.getString("producto"));
                productos [i][0] = rs.getString("id");
                productos [i][1] = rs.getString("producto");
                
                i++;
                
            }
        }
        
        catch (Exception ex)
        {
            JOptionPane.showMessageDialog(null, ex);
        }
    }

    private void cargarComboClientes() 
    {
        rs = consulta.selectResult("clientes", "count(*) as total", null);
        
        int dimension = 0;
        try 
        {
            rs.next();
            dimension = Integer.parseInt(rs.getString("total"));
        } catch (SQLException ex) 
        {
            JOptionPane.showMessageDialog(null, ex);
        }
        clientes = new String [dimension][2];
        
        rs = consulta.selectResult("clientes", "id, nombre, apellidos", " ORDER BY nombre ASC");
        
        try
        {
            int i = 0;
            while (rs.next())
            {
                cbClientes.addItem(rs.getString("nombre") + " " + rs.getString("apellidos"));
                clientes [i][0] = rs.getString("id");
                clientes [i][1] = rs.getString("nombre");
                
                i++;
                
            }
        }
        
        catch (Exception ex)
        {
            JOptionPane.showMessageDialog(null, ex);
        }
    }
}
