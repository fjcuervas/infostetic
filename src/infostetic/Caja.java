package infostetic;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/**
 *
 * @author Javi
 */
public class Caja extends javax.swing.JPanel {

    static DefaultTableModel modelo;
    DefaultTableModel modelo2;
    ResultSet rs;
    ResultSet rs1;
    ResultSet rs2;
    Conexion consulta;
    Object[][] resultado;
    JButton btnServicio[];
    JButton btnRegistro[];
    String proveedor = "";
    Boolean servicioAñadido = false;     
    int row, column;
    static BigDecimal total;    
    String[] columnas;
    Cobro cobro;
    static ArrayList<ArrayList<Integer>> cantProd = new ArrayList<>();  
    static int fila = 0;
    
    public Caja() 
    {
        initComponents();
        consulta = new Conexion();
        btnServicio = new JButton[12];        
        btnRegistro = new JButton[20];
        columnas = new String[] {"Id", "Código", "Cantidad", "Producto", "Precio"};
        rs = null;
        total = new BigDecimal(0);
        SelCliente.seleccionarCliente = false;  
        
        //Columnas tabla histórico
        String[] columNames = {"Id","Cant","Producto","Precio","Total"};
        
        modelo = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                    return false;
            }
        };
        
        modelo2 = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int fila, int columna) {
               return false;
            }
        };       
        
        for (int i=0; i < columNames.length; i++)
        {
            modelo.addColumn(columNames[i]);
        }
        
        tbCompra.setModel(modelo);        
        
        TableColumn[] columna = new TableColumn[columNames.length];
        
        for (int i=0; i < columNames.length; i++)
        {        
            columna[i] = tbCompra.getColumn(columNames[i]); //Obtienes la columna
        }
        
        columna[0].setPreferredWidth(5); //id        
        columna[1].setPreferredWidth(15); //cantidad
        columna[2].setPreferredWidth(290); //producto
        columna[3].setPreferredWidth(20); //precio
        columna[4].setPreferredWidth(10); //total                       
        
        /****************/

        modelo2 = consulta.getProductosMini();
        tbProductos.setModel(modelo2); 
           
        TableColumn colum1 = tbProductos.getColumn("Id");
        TableColumn colum2 = tbProductos.getColumn("Código");
        TableColumn colum3 = tbProductos.getColumn("Cantidad");
        TableColumn colum4 = tbProductos.getColumn("Producto");
        TableColumn colum5 = tbProductos.getColumn("Precio");
        
        colum1.setPreferredWidth(0);
        colum2.setPreferredWidth(10);
        colum3.setPreferredWidth(20);
        colum4.setPreferredWidth(160);
        colum5.setPreferredWidth(20);   
      
        //panelGrupo.setLayout(new GridLayout(2, 1));
        
        panelServicios.setLayout(new GridLayout(4, 4));
        panelRegistros.setLayout(new GridLayout(5, 4));
        panelFila1.setLayout(new GridLayout(1, 4));
        panelFila2.setLayout(new GridLayout(1, 4));
        panelFila3.setLayout(new GridLayout(1, 4));
                                           
        for (int i = 0; i < btnServicio.length; i++) 
        {
            btnServicio[i] = new JButton("");

            btnServicio[i].addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    btnServicioActionPerformed(evt);
                }
            });

            //btnServicio[i].setForeground(Color.red); color de la letra
            
            if (i < 4) 
            {
                panelFila1.add(btnServicio[i]);                
            }
            else if (i < 8) 
            {
                panelFila2.add(btnServicio[i]);
            }
            else 
            {
                panelFila3.add(btnServicio[i]);
            }
        }

        panelServicios.add(panelFila1);
        panelServicios.add(panelFila2);
        panelServicios.add(panelFila3);
        panelServicios.add(panelBuscarProductos);

        for (int i = 0; i < btnRegistro.length; i++) 
        {
            btnRegistro[i] = new JButton("");
            
            btnRegistro[i].addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    btnRegistrosActionPerformed(evt);
                }
            });

            panelRegistros.add(btnRegistro[i]);
        }

        panelGrupo.add(panelServicios);
        panelGrupo.add(panelRegistros);
        panelGrupo.setVisible(true);

        cargarServicios();

        //selCliente = new SelCliente(null, true);
        lbNomCliente.setText(SelCliente.nomCliente);

        comprobarDebitos();                

    }

    private void cargarServicios() {
        /* Comienzo a rellenar los botones con los servicios y registros */
        rs1 = consulta.buscarRegistro("familia_servicios", "id, tipo_servicio", null, "ORDER BY tipo_servicio ASC");
        try 
        {
            borrarTextoServicios();
            borrarTextoRegistros();
            
            for (int i = 0; rs1.next(); i++) 
            {
                //System.out.println("rs.last(): "+rs.last());
                
                if (i == (btnServicio.length - 1) && rs1.last() == true) 
                {
                    btnServicio[i].setText("SIGUIENTE -->");
                    rs1.previous();
                    break;
                }
                btnServicio[i].setText(rs1.getString("tipo_servicio"));
                btnServicio[i].setToolTipText(rs1.getString("tipo_servicio"));
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
    }

    private void cargarProductos(String proveedor) 
    {
        rs2 = consulta.buscarRegistro("productos, familia_servicios", "`productos`.`producto`",
                " where `productos`.`proveedor` = `familia_servicios`.`tipo_servicio` and `db_infostetic`.`productos`.`proveedor` LIKE '"
                + proveedor + "' ", "ORDER BY `productos`.`producto` ASC");

        try 
        {
            borrarTextoRegistros();
            int i;
            for (i = 0; rs2.next(); i++) 
            {
                if (i == (btnRegistro.length - 1)) 
                {
                    btnRegistro[i].setText("SIGUIENTE -->");
                    rs2.previous();
                    break;
                }
                btnRegistro[i].setText(rs2.getString("producto"));
                btnRegistro[i].setToolTipText(rs2.getString("producto"));
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
    }

    private void btnRegistrosActionPerformed(java.awt.event.ActionEvent evt) 
    {        
        String botonPulsado = ((JButton) evt.getSource()).getText();
        //Si pulsamos el botón SIGUIENTE pasamos al resto de registros que no cabían en todos los botones

        if (botonPulsado.equals("SIGUIENTE -->")) {

            try 
            {
                borrarTextoRegistros();
                int i;
                for (i = 0; rs2.next(); i++) 
                {
                    if (i == btnRegistro.length - 1) 
                    {
                        btnRegistro[i].setText("SIGUIENTE -->");
                        rs2.previous();
                        break;
                    }
                    btnRegistro[i].setText(rs2.getString("producto"));
                    btnRegistro[i].setToolTipText(rs2.getString("producto"));
                }
                if (rs2.last()) {
                    btnRegistro[i].setText("<-- VOLVER AL PRINCIPIO");
                    btnRegistro[i].setToolTipText("VOLVER AL PRINCIPIO");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, ex);
            }
        } 
        else if (rs2 != null && botonPulsado.equals("<-- VOLVER AL PRINCIPIO")) 
        {
            cargarProductos(proveedor);
        } 
        else if (botonPulsado.equals("") == false) 
        {
            String[] registro = new String[5];
            String idServicio = "";
            String precioUniServicio = null;
            ResultSet rsInsert;

            rsInsert = consulta.buscarRegistro("productos", "id, producto, pvp", " where producto LIKE '" + botonPulsado + "'", null);
            try {
                for (int i = 0; rsInsert.next(); i++) 
                {
                    precioUniServicio = rsInsert.getString("pvp");
                    idServicio = rsInsert.getString("id");
                    total = total.add(BigDecimal.valueOf(Double.parseDouble(precioUniServicio)));
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, ex);
            }
            registro[0] = idServicio;
            registro[1] = "1";
            registro[2] = botonPulsado;
            registro[3] = precioUniServicio;
            registro[4] = precioUniServicio;

            modelo.addRow(registro);
            tbCompra.setModel(modelo);           
        
            txtTotal.setText(String.valueOf(total));
            
            btnCobrar.setEnabled(true);

        }
    }

    private void btnServicioActionPerformed(java.awt.event.ActionEvent evt) {
        //Si pulsamo el botón SIGUIENTE pasamos al resto de registros que no cabían en todos los botones
        if (rs1 != null && ((JButton) evt.getSource()).getText().equals("SIGUIENTE -->")) 
        {
            
            try 
            {
                System.out.println("ULTIMO" + rs.last());
                borrarTextoServicios();
                int i;
                for (i = 0; rs1.next(); i++) 
                {
                    
                    if (i == (btnServicio.length - 1)) 
                    {
                        btnServicio[i].setText("SIGUIENTE -->");
                        rs1.previous();
                        break;
                    }
                    btnServicio[i].setText(rs1.getString("tipo_servicio"));
                    btnServicio[i].setToolTipText(rs1.getString("tipo_servicio"));
                }
                if (rs1.last()) 
                {
                    btnServicio[i].setText("<-- VOLVER AL PRINCIPIO");
                    btnServicio[i].setToolTipText("VOLVER AL PRINCIPIO");
                }
            } catch (SQLException ex) 
            {
                JOptionPane.showMessageDialog(null, ex);
            }
        } 
        else if (rs1 != null && ((JButton) evt.getSource()).getText().equals("<-- VOLVER AL PRINCIPIO")) 
        {
            cargarServicios();
        }
        else 
        {
            proveedor = ((JButton) evt.getSource()).getText();
            cargarProductos(proveedor);
        }
    }

    public static boolean comprobarCadena(String respuesta)
    {
        boolean correcto = true;
        
        if ( respuesta != null || (int)respuesta.substring(0, 1).charAt(0) != 32)
        {        
            int ultPos = respuesta.length()-1; //última posición del carácter en la cadena
            int puntos=0; //puntos introducidos por teclado         
            
            //Si no se introdujo nada, o al principio o final de la cadena
            //introdujo un . no activo el flag para que no se haga nada
            if (respuesta.equals("")==false && (int)respuesta.substring(ultPos, ultPos+1).charAt(0) != 46 &&
                                               (int)respuesta.substring(0, 1).charAt(0) != 46)
            {          
                for( int i = 0; i < ultPos+1; i++ ) 
                {  
                    //Voy contabilizando los puntos para evitar que se pongan más de uno
                    if((int)respuesta.substring(i, i+1).charAt(0) == 46)
                    {
                        puntos++;
                    }
                    //Si se introduco algún carácter diferente a un número activo el flag
                    else if((int)respuesta.substring(i, i+1).charAt(0) > 57 || (int)respuesta.substring(i, i+1).charAt(0) < 48 || puntos > 1)
                    {                                            
                        correcto = false;
                        break;
                    }                                                   
                }
            }
            else
            {
                correcto = false;
            }  
        }
        else
        {
            correcto = false;
        }
        return correcto;
    }
    
    void borrarTextoRegistros() {
        for (int i = 0; i < btnRegistro.length; i++) {
            btnRegistro[i].setText("");
            btnRegistro[i].setToolTipText("");
        }
    }

    void borrarTextoServicios() {
        for (int i = 0; i < btnServicio.length; i++) {
            btnServicio[i].setText("");
        }
    }
    
    private void comprobarDebitos() 
    {
        //Compruebo si el cliente tiene débitos para añadirlo a la tabla de Compra
        
        rs = consulta.buscarRegistro("debitos", "id, debito", "where id_cliente = '"+SelCliente.idCliente+"'", null);             
        String[] debito = new String[5];
        try 
        {
            while (rs.next()) 
            {
                total = total.add(BigDecimal.valueOf(Double.parseDouble(rs.getString("debito"))));                                
                debito[0] = rs.getString("id");
                debito[1] = "1";
                debito[2] = "DEBITO";
                debito[3] = rs.getString("debito");
                debito[4] = rs.getString("debito");
                
                modelo.addRow(debito);
                tbCompra.setModel(modelo);                                   
            }
            txtTotal.setText(String.valueOf(total));
        } 
        catch (SQLException ex) 
        {
            JOptionPane.showMessageDialog(null, ex);
        }   
    }    

    /*  public boolean isCellEditable(int rowIndex, int columnIndex) 
     {
        
     if (columnIndex==1) {
     System.out.println("true");
     return true;  //La columna 4 es editable.
     }
     else
     {
     System.out.println("false");
     return false;  //El resto de celdas no son editables.
     }
        
     }      */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelGrupo = new javax.swing.JPanel();
        panelServicios = new javax.swing.JPanel();
        panelFila1 = new javax.swing.JPanel();
        panelFila2 = new javax.swing.JPanel();
        panelFila3 = new javax.swing.JPanel();
        panelBuscarProductos = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        txtBuscarProducto = new javax.swing.JTextField();
        lbTecleaCadena = new javax.swing.JLabel();
        btnBuscarProducto = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tbProductos = new javax.swing.JTable();
        panelRegistros = new javax.swing.JPanel();
        panelGrupo2 = new javax.swing.JPanel();
        panelHistorico = new javax.swing.JPanel();
        panelModValores = new javax.swing.JPanel();
        btnCantidad = new javax.swing.JButton();
        btnProducto = new javax.swing.JButton();
        btnPrecio = new javax.swing.JButton();
        btnTotal = new javax.swing.JButton();
        lbValores = new javax.swing.JLabel();
        btnEliminar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbCompra = new javax.swing.JTable();
        lbNomCliente = new javax.swing.JLabel();
        panelCobro = new javax.swing.JPanel();
        panelGrupoTotal = new javax.swing.JPanel();
        panelTotal = new org.edisoncor.gui.panel.PanelRect();
        txtTotal = new javax.swing.JTextField();
        lbTotal = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        btnCobrar = new javax.swing.JButton();
        btnFichaCliente = new javax.swing.JButton();
        btnAbrirCajon = new javax.swing.JButton();
        btnDebitos = new javax.swing.JButton();

        setToolTipText("");
        setPreferredSize(new java.awt.Dimension(1000, 581));
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
        });

        panelServicios.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(153, 153, 153)));
        panelServicios.setToolTipText("");
        panelServicios.setAlignmentX(20.0F);
        panelServicios.setAlignmentY(20.0F);

        panelFila1.setPreferredSize(new java.awt.Dimension(672, 60));

        javax.swing.GroupLayout panelFila1Layout = new javax.swing.GroupLayout(panelFila1);
        panelFila1.setLayout(panelFila1Layout);
        panelFila1Layout.setHorizontalGroup(
            panelFila1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 476, Short.MAX_VALUE)
        );
        panelFila1Layout.setVerticalGroup(
            panelFila1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 58, Short.MAX_VALUE)
        );

        panelFila2.setPreferredSize(new java.awt.Dimension(672, 60));

        javax.swing.GroupLayout panelFila2Layout = new javax.swing.GroupLayout(panelFila2);
        panelFila2.setLayout(panelFila2Layout);
        panelFila2Layout.setHorizontalGroup(
            panelFila2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 475, Short.MAX_VALUE)
        );
        panelFila2Layout.setVerticalGroup(
            panelFila2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 60, Short.MAX_VALUE)
        );

        panelFila3.setPreferredSize(new java.awt.Dimension(672, 60));

        javax.swing.GroupLayout panelFila3Layout = new javax.swing.GroupLayout(panelFila3);
        panelFila3.setLayout(panelFila3Layout);
        panelFila3Layout.setHorizontalGroup(
            panelFila3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panelFila3Layout.setVerticalGroup(
            panelFila3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 60, Short.MAX_VALUE)
        );

        panelBuscarProductos.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(153, 153, 153)));

        txtBuscarProducto.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtBuscarProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBuscarProductoActionPerformed(evt);
            }
        });
        txtBuscarProducto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtBuscarProductoKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBuscarProductoKeyReleased(evt);
            }
        });

        lbTecleaCadena.setText("Buscar producto:");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addComponent(lbTecleaCadena)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtBuscarProducto))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(lbTecleaCadena, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(5, 5, 5))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(txtBuscarProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnBuscarProducto.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnBuscarProducto.setText("Búsqueda avanzada");
        btnBuscarProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarProductoActionPerformed(evt);
            }
        });

        tbProductos.setAutoCreateRowSorter(true);
        tbProductos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Codigo", "Cantidad", "Producto", "Precio"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbProductos.setTableHeader(null);
        tbProductos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbProductosMouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tbProductosMouseReleased(evt);
            }
        });
        tbProductos.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tbProductosKeyReleased(evt);
            }
        });
        jScrollPane3.setViewportView(tbProductos);

        javax.swing.GroupLayout panelBuscarProductosLayout = new javax.swing.GroupLayout(panelBuscarProductos);
        panelBuscarProductos.setLayout(panelBuscarProductosLayout);
        panelBuscarProductosLayout.setHorizontalGroup(
            panelBuscarProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelBuscarProductosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelBuscarProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnBuscarProducto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        panelBuscarProductosLayout.setVerticalGroup(
            panelBuscarProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBuscarProductosLayout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(panelBuscarProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnBuscarProducto)
                    .addGroup(panelBuscarProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 53, Short.MAX_VALUE)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout panelServiciosLayout = new javax.swing.GroupLayout(panelServicios);
        panelServicios.setLayout(panelServiciosLayout);
        panelServiciosLayout.setHorizontalGroup(
            panelServiciosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelServiciosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelServiciosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelServiciosLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(panelFila1, javax.swing.GroupLayout.PREFERRED_SIZE, 476, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1890, 1890, 1890))
                    .addGroup(panelServiciosLayout.createSequentialGroup()
                        .addGroup(panelServiciosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelServiciosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(panelFila2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 475, Short.MAX_VALUE)
                                .addComponent(panelFila3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 475, Short.MAX_VALUE))
                            .addComponent(panelBuscarProductos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        panelServiciosLayout.setVerticalGroup(
            panelServiciosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelServiciosLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(panelFila1, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panelFila2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panelFila3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelBuscarProductos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        panelRegistros.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(153, 153, 153)));
        panelRegistros.setToolTipText("");
        panelRegistros.setPreferredSize(new java.awt.Dimension(500, 500));

        javax.swing.GroupLayout panelRegistrosLayout = new javax.swing.GroupLayout(panelRegistros);
        panelRegistros.setLayout(panelRegistrosLayout);
        panelRegistrosLayout.setHorizontalGroup(
            panelRegistrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 498, Short.MAX_VALUE)
        );
        panelRegistrosLayout.setVerticalGroup(
            panelRegistrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 277, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout panelGrupoLayout = new javax.swing.GroupLayout(panelGrupo);
        panelGrupo.setLayout(panelGrupoLayout);
        panelGrupoLayout.setHorizontalGroup(
            panelGrupoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelRegistros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(panelServicios, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        panelGrupoLayout.setVerticalGroup(
            panelGrupoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelGrupoLayout.createSequentialGroup()
                .addComponent(panelServicios, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelRegistros, javax.swing.GroupLayout.DEFAULT_SIZE, 257, Short.MAX_VALUE))
        );

        panelGrupo2.setPreferredSize(new java.awt.Dimension(476, 400));

        panelHistorico.setPreferredSize(new java.awt.Dimension(694, 200));

        panelModValores.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(153, 153, 153)));
        panelModValores.setPreferredSize(new java.awt.Dimension(156, 80));

        btnCantidad.setText("Cantidad");
        btnCantidad.setEnabled(false);
        btnCantidad.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btnCantidad.setPreferredSize(new java.awt.Dimension(80, 23));
        btnCantidad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCantidadActionPerformed(evt);
            }
        });

        btnProducto.setText("Producto");
        btnProducto.setEnabled(false);
        btnProducto.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btnProducto.setPreferredSize(new java.awt.Dimension(80, 23));
        btnProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProductoActionPerformed(evt);
            }
        });

        btnPrecio.setText("Precio");
        btnPrecio.setEnabled(false);
        btnPrecio.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btnPrecio.setPreferredSize(new java.awt.Dimension(80, 23));
        btnPrecio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrecioActionPerformed(evt);
            }
        });

        btnTotal.setText("Total");
        btnTotal.setEnabled(false);
        btnTotal.setPreferredSize(new java.awt.Dimension(80, 23));
        btnTotal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTotalActionPerformed(evt);
            }
        });

        lbValores.setText("Modificar: ");

        btnEliminar.setText("Eliminar");
        btnEliminar.setEnabled(false);
        btnEliminar.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btnEliminar.setPreferredSize(new java.awt.Dimension(80, 23));
        btnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelModValoresLayout = new javax.swing.GroupLayout(panelModValores);
        panelModValores.setLayout(panelModValoresLayout);
        panelModValoresLayout.setHorizontalGroup(
            panelModValoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelModValoresLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbValores)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnPrecio, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnTotal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelModValoresLayout.setVerticalGroup(
            panelModValoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelModValoresLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelModValoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelModValoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnCantidad, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)
                        .addComponent(lbValores, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnProducto, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnPrecio, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnTotal, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnEliminar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 11, Short.MAX_VALUE))
        );

        tbCompra.setAutoCreateRowSorter(true);
        tbCompra.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Id", "Cantidad", "Producto", "Precio unidad", "Precio total"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbCompra.setOpaque(false);
        tbCompra.setRowHeight(17);
        tbCompra.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tbCompra.setShowHorizontalLines(false);
        tbCompra.setShowVerticalLines(false);
        tbCompra.getTableHeader().setReorderingAllowed(false);
        tbCompra.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbCompraMouseClicked(evt);
            }
        });
        tbCompra.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                tbCompraInputMethodTextChanged(evt);
            }
        });
        tbCompra.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                tbCompraPropertyChange(evt);
            }
        });
        tbCompra.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbCompraKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tbCompraKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                tbCompraKeyTyped(evt);
            }
        });
        tbCompra.addVetoableChangeListener(new java.beans.VetoableChangeListener() {
            public void vetoableChange(java.beans.PropertyChangeEvent evt)throws java.beans.PropertyVetoException {
                tbCompraVetoableChange(evt);
            }
        });
        jScrollPane1.setViewportView(tbCompra);

        lbNomCliente.setBackground(new java.awt.Color(204, 204, 255));
        lbNomCliente.setFont(new java.awt.Font("Cambria", 0, 36)); // NOI18N
        lbNomCliente.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbNomCliente.setText("Cliente");
        lbNomCliente.setToolTipText("");
        lbNomCliente.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout panelHistoricoLayout = new javax.swing.GroupLayout(panelHistorico);
        panelHistorico.setLayout(panelHistoricoLayout);
        panelHistoricoLayout.setHorizontalGroup(
            panelHistoricoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lbNomCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 474, Short.MAX_VALUE)
            .addComponent(panelModValores, javax.swing.GroupLayout.DEFAULT_SIZE, 474, Short.MAX_VALUE)
        );
        panelHistoricoLayout.setVerticalGroup(
            panelHistoricoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelHistoricoLayout.createSequentialGroup()
                .addComponent(lbNomCliente)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelModValores, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        panelCobro.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        panelCobro.setToolTipText("");

        panelGrupoTotal.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(153, 153, 153)));

        panelTotal.setForeground(new java.awt.Color(255, 255, 255));
        panelTotal.setColorSecundario(new java.awt.Color(255, 255, 255));

        txtTotal.setEditable(false);
        txtTotal.setBackground(new java.awt.Color(255, 255, 255));
        txtTotal.setFont(new java.awt.Font("Cambria", 0, 36)); // NOI18N
        txtTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotal.setText("0");

        javax.swing.GroupLayout panelTotalLayout = new javax.swing.GroupLayout(panelTotal);
        panelTotal.setLayout(panelTotalLayout);
        panelTotalLayout.setHorizontalGroup(
            panelTotalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTotalLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(23, Short.MAX_VALUE))
        );
        panelTotalLayout.setVerticalGroup(
            panelTotalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelTotalLayout.createSequentialGroup()
                .addContainerGap(19, Short.MAX_VALUE)
                .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );

        lbTotal.setFont(new java.awt.Font("Cambria", 0, 36)); // NOI18N
        lbTotal.setText("Total:");

        javax.swing.GroupLayout panelGrupoTotalLayout = new javax.swing.GroupLayout(panelGrupoTotal);
        panelGrupoTotal.setLayout(panelGrupoTotalLayout);
        panelGrupoTotalLayout.setHorizontalGroup(
            panelGrupoTotalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelGrupoTotalLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lbTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8))
        );
        panelGrupoTotalLayout.setVerticalGroup(
            panelGrupoTotalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelGrupoTotalLayout.createSequentialGroup()
                .addGroup(panelGrupoTotalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelGrupoTotalLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(panelTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelGrupoTotalLayout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addComponent(lbTotal)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(153, 153, 153)));
        jPanel1.setLayout(new java.awt.GridLayout(1, 3));

        btnCobrar.setFont(new java.awt.Font("Cambria", 0, 14)); // NOI18N
        btnCobrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/Cobrar.gif"))); // NOI18N
        btnCobrar.setText("COBRAR");
        btnCobrar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCobrar.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnCobrar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnCobrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCobrarActionPerformed(evt);
            }
        });
        jPanel1.add(btnCobrar);

        btnFichaCliente.setFont(new java.awt.Font("Cambria", 0, 14)); // NOI18N
        btnFichaCliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/verFichaCaja.gif"))); // NOI18N
        btnFichaCliente.setText("FICHA CLIENTE");
        btnFichaCliente.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnFichaCliente.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnFichaCliente.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnFichaCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFichaClienteActionPerformed(evt);
            }
        });
        jPanel1.add(btnFichaCliente);

        btnAbrirCajon.setFont(new java.awt.Font("Cambria", 0, 14)); // NOI18N
        btnAbrirCajon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/cajon.gif"))); // NOI18N
        btnAbrirCajon.setText("ABRIR CAJÓN");
        btnAbrirCajon.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAbrirCajon.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnAbrirCajon.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAbrirCajon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAbrirCajonActionPerformed(evt);
            }
        });
        jPanel1.add(btnAbrirCajon);

        btnDebitos.setFont(new java.awt.Font("Cambria", 0, 14)); // NOI18N
        btnDebitos.setText("COBROS PENDIENTES");
        btnDebitos.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnDebitos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDebitosActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelCobroLayout = new javax.swing.GroupLayout(panelCobro);
        panelCobro.setLayout(panelCobroLayout);
        panelCobroLayout.setHorizontalGroup(
            panelCobroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelGrupoTotal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnDebitos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        panelCobroLayout.setVerticalGroup(
            panelCobroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCobroLayout.createSequentialGroup()
                .addComponent(panelGrupoTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnDebitos, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout panelGrupo2Layout = new javax.swing.GroupLayout(panelGrupo2);
        panelGrupo2.setLayout(panelGrupo2Layout);
        panelGrupo2Layout.setHorizontalGroup(
            panelGrupo2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelGrupo2Layout.createSequentialGroup()
                .addGroup(panelGrupo2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelHistorico, javax.swing.GroupLayout.DEFAULT_SIZE, 474, Short.MAX_VALUE)
                    .addComponent(panelCobro, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelGrupo2Layout.setVerticalGroup(
            panelGrupo2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelGrupo2Layout.createSequentialGroup()
                .addComponent(panelHistorico, javax.swing.GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelCobro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelGrupo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelGrupo2, javax.swing.GroupLayout.DEFAULT_SIZE, 484, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelGrupo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelGrupo2, javax.swing.GroupLayout.DEFAULT_SIZE, 559, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtBuscarProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarProductoActionPerformed
    }//GEN-LAST:event_txtBuscarProductoActionPerformed

    private void txtBuscarProductoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarProductoKeyPressed
        

    }//GEN-LAST:event_txtBuscarProductoKeyPressed

    private void btnBuscarProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarProductoActionPerformed
        
        Productos productos = new Productos(null, true);
        Productos.fromCaja = true;
        //Productos.btnAñadirAcesta.setEnabled(true);
        productos.setVisible(true);
        
    }//GEN-LAST:event_btnBuscarProductoActionPerformed

    private void btnCobrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCobrarActionPerformed

        cobro = new Cobro(null, true);
        cobro.setVisible(true);

    }//GEN-LAST:event_btnCobrarActionPerformed

    private void tbCompraPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_tbCompraPropertyChange

        if(tbCompra.getRowCount() > 0)
        {
            btnCobrar.setEnabled(true);
        }
        else
        {
            btnCobrar.setEnabled(false);
        }   
        
        
    }//GEN-LAST:event_tbCompraPropertyChange

    private void tbCompraInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_tbCompraInputMethodTextChanged
        //System.out.println("cantidad: "+tbCompra.getValueAt(tbCompra.getSelectedRow(), tbCompra.getSelectedColumn()));
        // TODO add your handling code here:
    }//GEN-LAST:event_tbCompraInputMethodTextChanged

    private void tbCompraMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbCompraMouseClicked

        //habilito todos los botones al seleccionar la tabla
        btnEliminar.setEnabled(true);        
        btnCantidad.setEnabled(true);
        btnProducto.setEnabled(true);
        btnPrecio.setEnabled(true);
        btnTotal.setEnabled(true);
        
    }//GEN-LAST:event_tbCompraMouseClicked

    private void tbCompraKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbCompraKeyTyped
    }//GEN-LAST:event_tbCompraKeyTyped

    private void tbCompraKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbCompraKeyReleased
    }//GEN-LAST:event_tbCompraKeyReleased

    private void tbCompraKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbCompraKeyPressed

     /*   int code = evt.getKeyCode(); //almacena el código numerico de la tecla
        char caracter = evt.getKeyChar(); //almacena el caracter (de momento no lo uso)  

        System.out.println("evt.toString(): " + evt.toString());

        row = tbCompra.getSelectedRow();
        column = tbCompra.getSelectedColumn();

        if (code == 8) {
            tbCompra.setValueAt("00", row, 1);
        }

        if (code == 10) {
            row = tbCompra.getSelectedRow();
            column = tbCompra.getSelectedColumn();


            //  String columnName = modelo.getColumnName(column);
//    Object data = modelo.getValueAt(row, column);      

            //float preciototal=Integer.parseInt(modelo.getValueAt(row, 1).toString())*Float.parseFloat(modelo.getValueAt(row, 3).toString());
            tbCompra.setValueAt(55, row, 4);
        }*/
    }//GEN-LAST:event_tbCompraKeyPressed

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_formKeyPressed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        
        row = tbCompra.getSelectedRow();
        
        //Si existen registros en la tabla, voy eliminando filas
        if (tbCompra.getRowCount() > 0)
        {  
            total = total.add(BigDecimal.valueOf(Double.parseDouble( tbCompra.getValueAt(row, 4).toString() )).negate() );            
            
            txtTotal.setText(String.valueOf(total));
           
            //tbCompra.convertRowIndexToModel(row) me indica la fila seleccionada en el modelo
            //que se corresponde con la fila "row" seleccionada en la tabla. Al ordenar la tabla
            //se produce un desfase entre el modelo y la tabla real, por eso se necesita este método.
            modelo.removeRow(tbCompra.convertRowIndexToModel(row));
            
            //Si está seleccionada la última fila pero no es el último registro en la tabla
            //disminuyo el número de fila para que la selección de la fila vaya subiendo al eliminar
            if (row == tbCompra.getRowCount() && tbCompra.getRowCount() > 0)
            {
                row--;
            }            
            //Si aún quedan registros, selecciono la fila
            if (tbCompra.getRowCount() > 0)
            {
                tbCompra.setRowSelectionInterval(row, row);
            }   
            
        }        
        //Si ya no existen registros en la tabla, deshabilito todos los botones
        if (tbCompra.getRowCount() == 0)
        {
            btnEliminar.setEnabled(false);
            btnCantidad.setEnabled(false);
            btnProducto.setEnabled(false);
            btnPrecio.setEnabled(false);
            btnTotal.setEnabled(false); 
            btnCobrar.setEnabled(false);
        }
        
    }//GEN-LAST:event_btnEliminarActionPerformed

    private void btnCantidadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCantidadActionPerformed
      
        //Almaceno la fila seleccionada
        row = tbCompra.getSelectedRow();
        
        //almaceno el valor de la columna cantidad antes de modificarla
        int valorCantidad = Integer.parseInt(tbCompra.getValueAt(row, 1).toString());
        boolean letras = false;
        
        //Solicito la introducción de una nueva cantidad y coloco por defecto 
        //el valor que marca la columna cantidad de la fila seleccionada
        String respuesta = JOptionPane.showInputDialog(null, "Introduzca cantidad: ", valorCantidad);        
        
        //Si aún así no se introdujo nada o se pulsó Cancelar,  no realizo ningún cambio
        if ( respuesta != null )
        {
            if (respuesta.equals("") == false)
            {
                //Si introdujo una cadena, la analizo en busca de caracteres diferentes a números
                for( int i = 0; i < respuesta.length(); i++ ) 
                {
                    if((int)respuesta.substring(i, i+1).charAt(0) > 57 || (int)respuesta.substring(i, i+1).charAt(0) < 48)
                    {
                        //Activo un flag para indicar que se introdujeron caracteres
                        letras = true;
                        break;
                    }
                }
                //Si lo que se introdujo era un número, continúo con la operación
                if (letras == false)
                {
                    //Hago la multiplicación de la cantidad introducida por el precio por unidad, y coloco el resultado en Total
                    float valorTotal = Integer.parseInt(respuesta) * Float.parseFloat(tbCompra.getValueAt(row, 3).toString());

                    //En vez de "row" asigno tbCompra.convertRowIndexToModel(row) que relaciona la tabla real con el modelo
                    modelo.setValueAt(respuesta, tbCompra.convertRowIndexToModel(row), 1); //columna cantidad
                    modelo.setValueAt(valorTotal, tbCompra.convertRowIndexToModel(row), 4); //columna Total

                    //Recorro la tabla y voy sumando los totales para mostrarlo en el label Total
                    total = new BigDecimal(0);
                    for (int i = 0; tbCompra.getRowCount() > i; i++) 
                    {
                         total = total.add(BigDecimal.valueOf(Double.parseDouble( tbCompra.getValueAt(i, 4).toString() )));                         
                    }
                    txtTotal.setText(String.valueOf(total)); 
                }
            }
        }
        
    }//GEN-LAST:event_btnCantidadActionPerformed

    private void btnProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProductoActionPerformed
       
        //Almaceno la fila seleccionada
        row=tbCompra.getSelectedRow();
        
        //almaceno el valor de la columna producto antes de modificarla
        String valorProducto=String.valueOf(tbCompra.getValueAt(row, 2).toString());        
        
        //Solicito la introducción de una nueva cadena y coloco por defecto 
        //la cadena que marca la columna producto de la fila seleccionada
        String respuesta = JOptionPane.showInputDialog(null, "Introduzca producto: ", valorProducto);
        
        //Si aún así no se introdujo nada o se pulsó Cancelar,  no realizo ningún cambio
        if ( respuesta != null )
        {
            if (respuesta.equals("")==false)
            {       
                modelo.setValueAt(respuesta, tbCompra.convertRowIndexToModel(row), 2); //columna producto
            } 
        }        
    }//GEN-LAST:event_btnProductoActionPerformed

    /*** AL PULSAR EL BOTÓN PRECIO, MODIFICO LOS EL RESULTADO EN EL TOTAL ***/
    private void btnPrecioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrecioActionPerformed

        //Almaceno la fila seleccionada
        row=tbCompra.getSelectedRow();
        
        //almaceno el valor de la columna cantidad 
        int valorCantidad=Integer.parseInt(tbCompra.getValueAt(row, 1).toString());
        
        //almaceno el valor de la columna Precio 
        String valorPrecio=tbCompra.getValueAt(row, 3).toString();
        
        boolean letras=false;
        
        //Solicito la introducción de un nuevo precio y coloco por defecto 
        //el valor que marca la columna precio de la fila seleccionada
        String respuesta = JOptionPane.showInputDialog(null, "Introduzca precio: ", valorPrecio);        

        //Si se pulsó Cancelar,  no realizo ningún cambio
        if ( respuesta != null )
        {        
            int ultPos = respuesta.length()-1; //última posición del carácter en la cadena
            int puntos=0; //puntos introducidos por teclado         
            
            //Si no se introdujo nada, o al principio o final de la cadena
            //introdujo un . no activo el flag para que no se haga nada
            if (respuesta.equals("")==false && (int)respuesta.substring(ultPos, ultPos+1).charAt(0) != 46 &&
                                               (int)respuesta.substring(0, 1).charAt(0) != 46)
            {            
                for( int i = 0; i < ultPos+1; i++ ) 
                {  
                    //Voy contabilizando los puntos para evitar que se pongan más de uno
                    if((int)respuesta.substring(i, i+1).charAt(0) == 46)
                    {
                        puntos++;
                    }
                    //Si se introduco algún carácter diferente a un número activo el flag
                    else if((int)respuesta.substring(i, i+1).charAt(0) > 57 || (int)respuesta.substring(i, i+1).charAt(0) < 48 || puntos > 1)
                    {                                            
                        letras=true;
                        break;
                    }                                                   
                }
            }
            else
            {
                letras=true;
            }

            //System.out.println("CORRECTO: "+(int)respuesta.substring(ultPos, ultPos+1).charAt(0));
            //Si lo que se introdujo era un número, continúo con la operación
            if (letras==false)
            {
               //Hago la multiplicación del precio introducido por la cantidad que había, y coloco el resultado en Total
               float valorTotal= Float.parseFloat(respuesta) * valorCantidad;

               modelo.setValueAt(respuesta, tbCompra.convertRowIndexToModel(row), 3); //columna precio
               modelo.setValueAt(valorTotal, tbCompra.convertRowIndexToModel(row), 4); //columna Total

               //Recorro la tabla y voy sumando los totales para mostrarlo en el label Total
               total = new BigDecimal(0);
               for (int i = 0; tbCompra.getRowCount() > i; i++) 
               {
                   total = total.add(BigDecimal.valueOf(Double.parseDouble( tbCompra.getValueAt(i, 4).toString() )));                 
               }
               txtTotal.setText(String.valueOf(total)); 
           }
        }        
    }//GEN-LAST:event_btnPrecioActionPerformed

    private void btnTotalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTotalActionPerformed

        //Almaceno la fila seleccionada
        row=tbCompra.getSelectedRow();
                
        //almaceno el valor de la columna Total 
        String valorTotal=tbCompra.getValueAt(row, 4).toString();
        
        boolean letras=false;
        
        //Solicito la introducción del total y coloco por defecto 
        //el valor que marca la columna Total de la fila seleccionada
        String respuesta = JOptionPane.showInputDialog(null, "Introduzca precio: ", valorTotal);        

        //Si se pulsó Cancelar,  no realizo ningún cambio
        if ( respuesta != null )
        {        
            int ultPos = respuesta.length()-1; //última posición del carácter en la cadena
            int puntos=0; //puntos introducidos por teclado    
            
            //Si no se introdujo nada, o al principio o final de la cadena
            //introdujo un . no activo el flag para que no se haga nada
            if (respuesta.equals("")==false && (int)respuesta.substring(ultPos, ultPos+1).charAt(0) != 46 &&
                                               (int)respuesta.substring(0, 1).charAt(0) != 46)
            {            
                for( int i = 0; i < ultPos+1; i++ ) 
                {  
                    //Voy contabilizando los puntos para evitar que se pongan más de uno
                    if((int)respuesta.substring(i, i+1).charAt(0) == 46)
                    {
                        puntos++;
                    }
                    //Si se introduco algún carácter diferente a un número activo el flag
                    else if((int)respuesta.substring(i, i+1).charAt(0) > 57 || (int)respuesta.substring(i, i+1).charAt(0) < 48 || puntos > 1)
                    {                                            
                        letras=true;
                        break;
                    }                                                   
                }
            }
            else
            {
                letras=true;
            }
            //Si lo que se introdujo era un número, continúo con la operación
            if (letras==false)
            {
               modelo.setValueAt(respuesta, tbCompra.convertRowIndexToModel(row), 4); //columna Total
               
               //Recorro la tabla y voy sumando los totales para mostrarlo en el label Total
               total = new BigDecimal(0);
               for (int i = 0; tbCompra.getRowCount() > i; i++) 
               {
                   total = total.add(BigDecimal.valueOf(Double.parseDouble( tbCompra.getValueAt(i, 4).toString() ))); 
               }
               
               txtTotal.setText(String.valueOf(total)); 
           }
        }   
    }//GEN-LAST:event_btnTotalActionPerformed

    private void tbProductosKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbProductosKeyReleased

     
        
    }//GEN-LAST:event_tbProductosKeyReleased

    private void tbProductosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbProductosMouseClicked
        
        if(tbCompra.getRowCount() > 0)
        {
            btnCobrar.setEnabled(true);
        }
        else
        {
            btnCobrar.setEnabled(false);
        }    
        
    }//GEN-LAST:event_tbProductosMouseClicked

    private void tbProductosMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbProductosMouseReleased
                        
        row = tbProductos.getSelectedRow();
        
        String[] registro = new String[5];
        
        if (tbProductos.getValueAt(row, 2) != null)
        {            
            
            /* alamaceno el id y la cantidad del producto para luego modificarla en la base de datos
            Al no saber el tamaño del array por ser dinámico, declaro un arrayList bidimensional.
            Cada vez que se elija un producto, instancio un nuevo array dentro del arriba declarado.
            De este modo al ir asignando valores, aumenta automáticamente el índice del arrayList.
            cantProd.get(fila) muestra el contenido de las columnas de una fila. 
            cantProd.get(fila).get(0) muestra la columna 1, y cantProd.get(fila).get(1) la columna 2 */

            cantProd.add(new ArrayList<Integer>(2)); //Añado 2 columnas en cada fila del array

            cantProd.get(fila).add(Integer.parseInt((String) tbProductos.getValueAt(row, 0))); //cantProd[0][0] id producto
            cantProd.get(fila).add(Integer.parseInt((String) tbProductos.getValueAt(row, 2))); //cantProd[0][2] cantidad producto

            fila++;  
        }
        
        registro[0] = tbProductos.getValueAt(row, 0).toString(); //id del producto
        registro[1] = "1"; //cantidad del producto 
        registro[2] = tbProductos.getValueAt(row, 3).toString(); //nombre del producto
        registro[3] = tbProductos.getValueAt(row, 4).toString(); //precio del producto 
        registro[4] = tbProductos.getValueAt(row, 4).toString(); //precio total                                       
                
        modelo.addRow(registro);
        
        tbCompra.setModel(modelo);

        total = total.add(BigDecimal.valueOf(Double.parseDouble( tbProductos.getValueAt(row, 4).toString() )));         

        txtTotal.setText(String.valueOf(total));
              
    }//GEN-LAST:event_tbProductosMouseReleased
public void abrirCajon(String puerto) {
// Apertura del cajón portamonedas. 
try {
FileWriter imp = new FileWriter(puerto);
imp.write(27);
imp.write(112);
imp.write(0);
imp.write(150);
imp.write(150);
imp.write(0);
imp.close();
} catch (Exception e) {
System.out.println(e.getMessage());
}
}
    private void btnAbrirCajonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAbrirCajonActionPerformed
//Consiste en mandar al puerto de la impresora los "números" que te especifique el fabricante de la impresora (lee su manual).

abrirCajon("/dev/lp0");

//En mi caso cuando llamo a ese método debo pasarle como parametro /dev/lp0
//que es el puerto centronics de la impresora de ticket
    }//GEN-LAST:event_btnAbrirCajonActionPerformed

    private void tbCompraVetoableChange(java.beans.PropertyChangeEvent evt)throws java.beans.PropertyVetoException {//GEN-FIRST:event_tbCompraVetoableChange

    }//GEN-LAST:event_tbCompraVetoableChange

    private void btnFichaClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFichaClienteActionPerformed

        //Creo un nuevo Frame llamado FichaCliente para poder maximizarlo al llamarlo
        FichaCliente ficha = new FichaCliente ();
        ficha.setVisible(true);
        
       /* FichaCliente ficha = new FichaCliente (null, true);
        ficha.setVisible(true);        */ 
    }//GEN-LAST:event_btnFichaClienteActionPerformed

    private void txtBuscarProductoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarProductoKeyReleased

        String[] registro = new String[5];
        String busqueda = txtBuscarProducto.getText();
        
        rs = consulta.buscarRegistro("productos", "id, codigo, cantidad, producto, pvp", 
                "WHERE producto LIKE '" + busqueda + "%' "
                + "OR codigo LIKE '" + busqueda + "%' ", "ORDER BY producto ASC");
        
        modelo2 = new DefaultTableModel(null,columnas); 
        
        try 
        {            
            while(rs.next())
            {                    
                
                registro[0]=rs.getString("id");
                registro[1]=rs.getString("codigo");
                registro[2]=rs.getString("cantidad");
                registro[3]=rs.getString("producto");
                registro[4]=rs.getString("pvp");
                
                modelo2.addRow(registro);
                tbProductos.setModel(modelo2);
                                
            }
        TableColumn colum1 = tbProductos.getColumn("Id");
        TableColumn colum2 = tbProductos.getColumn("Código");
        TableColumn colum3 = tbProductos.getColumn("Cantidad");
        TableColumn colum4 = tbProductos.getColumn("Producto");
        TableColumn colum5 = tbProductos.getColumn("Precio");
        
        colum1.setPreferredWidth(0);
        colum2.setPreferredWidth(10);
        colum3.setPreferredWidth(10);
        colum4.setPreferredWidth(200);
        colum5.setPreferredWidth(10);       
                        
        }
        catch (SQLException ex) 
        {
            JOptionPane.showMessageDialog(null, ex);
        }          
    }//GEN-LAST:event_txtBuscarProductoKeyReleased

    private void btnDebitosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDebitosActionPerformed

        Debitos debitos = new Debitos(null, true);
        debitos.setVisible(true);

    }//GEN-LAST:event_btnDebitosActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAbrirCajon;
    private javax.swing.JButton btnBuscarProducto;
    private javax.swing.JButton btnCantidad;
    public static javax.swing.JButton btnCobrar;
    private javax.swing.JButton btnDebitos;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnFichaCliente;
    private javax.swing.JButton btnPrecio;
    private javax.swing.JButton btnProducto;
    private javax.swing.JButton btnTotal;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lbNomCliente;
    private javax.swing.JLabel lbTecleaCadena;
    private javax.swing.JLabel lbTotal;
    private javax.swing.JLabel lbValores;
    private javax.swing.JPanel panelBuscarProductos;
    private javax.swing.JPanel panelCobro;
    private javax.swing.JPanel panelFila1;
    private javax.swing.JPanel panelFila2;
    private javax.swing.JPanel panelFila3;
    private javax.swing.JPanel panelGrupo;
    private javax.swing.JPanel panelGrupo2;
    private javax.swing.JPanel panelGrupoTotal;
    private javax.swing.JPanel panelHistorico;
    private javax.swing.JPanel panelModValores;
    private javax.swing.JPanel panelRegistros;
    private javax.swing.JPanel panelServicios;
    private org.edisoncor.gui.panel.PanelRect panelTotal;
    public static javax.swing.JTable tbCompra;
    private javax.swing.JTable tbProductos;
    private javax.swing.JTextField txtBuscarProducto;
    public static javax.swing.JTextField txtTotal;
    // End of variables declaration//GEN-END:variables
}
